package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class RepairRepair extends ActionFrame 
						  implements ActionListener {

	//----- Variables
	//UI
	public static RepairRepair frame;
	private TextField note, track, labor;
	private JCheckBox email;
	private ComboBox<String> ship;
	
	//Data
	private static Repair r;
	
	//----- Constructor
	public RepairRepair() {
		super("Repair", 350, 500, new GridLayout(1,0));
		
		//Left Side
		JPanel left = new JPanel(new BorderLayout());
		left.setBorder(BorderManager.getTitleBorder("Parts Needed"));
		JTextArea area = new JTextArea();
		area.setEditable(false);
		
		Assembly[] a = r.getAssembly().getAssemblies();
		Part[] p = r.getAssembly().getParts();
		
		int c = 1;
		if(a != null)
			for(int i=0; i<a.length; i++){
				area.append("" + c++ + ". ");
				if(a[i].getColor() != null)
					if(!a[i].getColor().equals(Part.NO_COLOR))
						area.append(a[i].getColor() + " ");
				area.append(a[i].getName());
				if(a[i].getQuantity() > 1)
					area.append(" (" + a[i].getQuantity() + ")");
				area.append("\n");
			}
		
		if(p != null)
			for(int i=0; i<p.length; i++){
				area.append("" + c++ + ". ");
				if(p[i].getColor() != null)
					if(!p[i].getColor().equals(Part.NO_COLOR))
						area.append(p[i].getColor() + " ");
				area.append(p[i].getName());
				if(p[i].getQuantity() > 1)
					area.append(" (" + p[i].getQuantity() + ")");
				if(p[i].getQuality() != null)
					if(!p[i].getQuality().equals(Part.NO_QUALITY))
						area.append(" " + p[i].getQuality());
				area.append("\n");
			}
		
		left.add(new JScrollPane(area), BorderLayout.CENTER);
		left.add(new Label(r.getAssembly().getTotalCost().toString(), "Total Part Cost", 
				SwingConstants.CENTER), BorderLayout.PAGE_END);
		center.add(left);
		
		//Right Side
		JPanel right = new JPanel(new GridLayout(0,1));
		int cen = SwingConstants.CENTER;
		
		right.add(new JLabel(r.getName(), cen));
		right.add(new Label(r.getDateSold().displaySimpleDate(), "Date Sold", cen));
		right.add(new Label(r.getBuyerName(), "Buyer", cen));
		right.add(new Label("" + r.getBuyerZIP(), "ZIP", cen));
		right.add(new Label(r.getOverallColor(), "Email", cen));
		right.add(email = new JCheckBox("Send Email"));
		email.setSelected(true);
		right.add(labor = new TextField("", "Sold For"));
		right.add(ship = new ComboBox<String>(null, 
				r.getParent().getProductCategory().getShippinOptionNames(), null, -1, "Shipping Options"));
		if(r.getShippingCost().getValueInCents() != 0)
			ship.setEnabled(false);
		right.add(track = new TextField(r.getTrackingNo(), "Tracking Number"));
		right.add(note = new TextField(r.getNote(), "Note"));
		
		center.add(right);
		
		//bottom.add(new Button("Cancel", this, 1));
		bottom.add(new Button("Repair", this, 0));
	}
	
	
	//----- Methods
	public static void initializeFrame(){
		r = DataSelection.getSelectedRepair();
		
		if(r == null) return;
		if(!r.getStatus().equals(Repair.PENDING)){
			OptionPane.showConfirm(r.getName() + " must be pending before it can be repaired.", 
					"Repair Status Error!");
			return;
		}
		
		frame = new RepairRepair();
		frame.setVisible(true);
	}
	
	
	//----- Interfaces
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		int sel = Integer.parseInt(arg0.getActionCommand());
		
		switch(sel){
		
		case 0: //Repair
			if(FormatChecker.stringCheck(note.getText())
					&& FormatChecker.stringCheck(track.getText())
					&& FormatChecker.creditCheck(labor.getText())){
				
				
				Credit soldFor = new Credit(labor.getText());
				Credit ppf = new Credit(soldFor.getValueInDollars() * Database.getPayPalFeeRate() + 0.3);
				
				r.setDateRepaired(new Date());
				r.getSellAmount().add(soldFor);
				r.setTrackingNo(track.getText());
				
				if(r.getShippingCost().getValueInCents() == 0){
					ShippingOption[] ops = r.getParent().getProductCategory().getShippingOptions();
					
						ShippingOption so = null;
						if(ship.getSelectedItem().equals("Custom"))
							while(so == null){
								so = new CustomShippingOption(r.getParent().getServiceCategory()).getSO();
								if(so.getName().equals("Cancel"))
									return;
							}
						else
							so = ops[ship.getSelectedIndex()];
						
						if(!FormatChecker.shippingOptionCheck(so))
							return;
						
						r.setShippingCost(so.useOption(r.getParent().getServiceCategory(), Date.getTodaysDate()));
						r.setShippingPaid(so.getPaid());
					
					ppf.add(new Credit(so.getPaid().getValueInDollars() * Database.getPayPalFeeRate()));
						//No need to subtract the 0.30$
					Credit toAdd = new Credit(so.getPaid().getValueInDollars() 
							- ppf.getValueInDollars() - so.getCost().getValueInDollars());
					Database.addToPayPalBalance(toAdd); //Add the additional ship
					
					r.getPayPalFee().add(ppf); //Add additional PPF
					
				}
				
				soldFor.subtract(ppf);
				Database.addToPayPalBalance(soldFor); //Rev to acct
				
				Database.addToUndeliveredIncome(new Credit(
						r.getProfit().getValueInDollars() * (1 - Database.getReinvestmentRate())));
				
				
				r.setStatus(Repair.COMPLETE);
				
				
				Thread t = new Thread(new Runnable(){
					@Override
					public void run() {
						new EmailManager(EmailManager.ORTHS).completionNotice(r.getOverallColor(), r.getFormalName(), r.getTrackingNo());	
					}
				});
				
				GUIEngine.refresh(-1, -1);
				frame.dispose();
				if(email.isSelected())
					t.run();
				
			}break;
			
		}

	}

}
