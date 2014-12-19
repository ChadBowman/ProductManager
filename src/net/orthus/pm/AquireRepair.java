package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class AquireRepair extends ActionFrame 
						  implements ActionListener,
						  			 ListSelectionListener,
						  			 ChangeListener{

	//----- Variables
	//UI
	private JList<String> parts, splitList;
	private JSplitPane partsUsed;
	private Label cost;
	private JPanel data;
	
	private JCheckBox email;
	private static AquireRepair frame;
	private TextField note;

	//Data
	private static Repair r;
	private ServiceCategory cat;
	private Constituent[] cons;
	private Constituent[] supply;
	private Spinner[] quant;
	private int[] quants;
	
	//Constants
	private static final int INVOICE = 0;
	private static final int FIXED_REPAIR = 1;
	

	//----- Constructor
	public AquireRepair(int width, int height, LayoutManager layout, int mode) {
		super("Aquire Repair", width, height, layout);
		
		cat = r.getParent().getServiceCategory();
		int cen = SwingConstants.CENTER;
		
		switch(mode){
		case INVOICE:
			
			JPanel left = new JPanel(new BorderLayout());
			JPanel cent = new JPanel(new BorderLayout());
			JPanel right = new JPanel(new GridLayout(0,1));
			
				//Left Panel
				supply = cat.getSupplyConstituentsByStatus(Constituent.SUPPLY);
				String[] ptNames = null;
				if(supply != null)
					for(int i=0; i<supply.length; i++){
						String abr = " ";
						if(supply[i].getColor() != null)
							abr = abr.concat(supply[i].getColor().replaceAll("[a-z]|NO COLOR| ", ""));
						ptNames = ArrayManager.addToArray(supply[i].getName() + abr, ptNames);
					}
				
				if(ptNames != null){
					left.add(new JScrollPane(parts = new JList<String>(ptNames)), BorderLayout.CENTER);
					left.add(new Button("Use Part", this, 2), BorderLayout.PAGE_END);
				}
				else
					left.add(new JLabel("No Parts Availible", SwingConstants.CENTER));
				
				//Center Panel
				splitList = new JList<String>();
				data = new JPanel(new CardLayout());
				cent.add(partsUsed = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
						new JScrollPane(splitList), new JScrollPane(data)));
				partsUsed.setDividerLocation(90);
				splitList.addListSelectionListener(this);
				cent.add(cost = new Label("$0.0", "Parts Cost"), BorderLayout.PAGE_END);
				
				//Right Panel
				right.add(new Label(r.getName(), "Name"));
				right.add(new Label(r.getDateSold().displaySimpleDate(), "Date Sold"));
				right.add(new Label(r.getBuyerName(), "Customer Name"));
				right.add(new Label("" + r.getBuyerZIP(), "Customer ZIP"));
				right.add(new Label(r.getOverallColor(), "Buyer Email"));
				right.add(email = new JCheckBox("Send Email"));
				email.setSelected(true);
			
				right.add(note = new TextField(r.getNote(), "Note"));
				
			center.add(left);
			center.add(cent);
			center.add(right);
			
			bottom.add(new Button("Cancel", this, 3));
			bottom.add(new Button("Aquire", this, 1));
			
			break;
			
		case FIXED_REPAIR:
			center.add(new JLabel(r.getName(), cen));
			center.add(new Label(r.getDateSold().displaySimpleDate(), "Date Sold", cen));
			center.add(new Label(r.getSellAmount().toString(), "Sold For", cen));
			center.add(new Label(r.getBuyerName(), "Buyer", cen));
			center.add(new Label("" + r.getBuyerZIP(), "ZIP", cen));
			if(r.getOverallColor() != null)
				center.add(new Label(r.getOverallColor(), "Color", cen));
			center.add(note = new TextField(r.getNote(), "Note"));
			bottom.add(new Button("Aquire", this, 0));
			break;
		}
	}

	//----- Methods
	private String[] updateAvailible(){
		String[] ptNames = null;
		if(supply != null)
			for(int i=0; i<supply.length; i++){
				String abr = " ";
				if(supply[i].getColor() != null)
					abr = abr.concat(supply[i].getColor().replaceAll("[a-z]|NO COLOR| ", ""));
				ptNames = ArrayManager.addToArray(supply[i].getName() + abr, ptNames);
			}
		return ptNames;
	}
	
	private String[] listContent(){
		data.removeAll();
		String[] toRet = null;
		quant = new Spinner[cons.length];
		Credit total = new Credit();
		
		if(cons != null)
			for(int i=0; i<cons.length; i++){
				toRet = ArrayManager.addToArray(cons[i].getName(), toRet);
				quant[i] = new Spinner(1, quants[i], this, null);
				quant[i].setValue(cons[i].getQuantity());
				data.add(getCard(cons[i], i),"" + i);
				total.add(cons[i].getTotalCost());
			}
		
		cost.setText(total.toString());
	
		return toRet;
	}
	
	private JPanel getCard(Constituent c, int index){
		JPanel toRet = new JPanel(new GridLayout(0,2));
		
		toRet.add(new JLabel("Color:"));
		toRet.add(new JLabel(c.getColor()));
		try{
			String q = ((Part)c).getQuality();
			toRet.add(new JLabel("Quality:"));
			toRet.add(new JLabel(q));
		}catch(ClassCastException e){}
		toRet.add(new JLabel("Quantity:"));
		toRet.add(quant[index]);
		
		return toRet;
	}
	
	private void updateCost(){
		Credit c = new Credit();
		if(cons != null)
			for(int i=0; i<cons.length; i++)
				c.add(cons[i].getTotalCost());
		cost.setText(c.toString());
	}
	
	public static void initalizeFrame(){
		r = DataSelection.getSelectedRepair();
		
		if(r == null) return;
		if(!r.getStatus().equals(Repair.INCOMING)){
			OptionPane.showConfirm(r.getName() + " must be incomming to aquire.",
					"Repair Status Error!");
			return;
		}
		
		if(r.getAssembly() == null)
			frame = new AquireRepair(450, 420, new GridLayout(0,3), INVOICE);
		else
			frame = new AquireRepair(200, 375, new GridLayout(0,1), FIXED_REPAIR);
			
		frame.setVisible(true);
	}
	
	
	//----- Interfaces
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		int val = Integer.parseInt(arg0.getActionCommand());
		switch(val){
		
		case 0: //Fixed Submit
			if(FormatChecker.stringCheck(note.getText())){
				
				r.setStatus(Repair.PENDING);
				
				GUIEngine.refresh(-1, -1);
				frame.dispose();
				
			}break;
			
		case 1: //Invoice Submit
			

			
			Assembly rep = new Assembly(
					Database.getNewSerial(),
					1,
					0,
					r.getParent().getAddress(),
					r.getName(),
					Assembly.REPAIR,
					Assembly.NO_COLOR,
					null,
					null,
					null,
					null,
					null,
					null);
			
			if(cons != null){
				for(int i=0; i<cons.length; i++){
					try{
						Assembly a = (Assembly) cons[i];
						cat.removeAssemblyFromSupply(a); //Remove
						rep.addAssembly(a); //Add
					}catch(ClassCastException e){
						Part p = (Part) cons[i];
						cat.removePartFromSupply(p); //Remove
						rep.addPart(p); //Add
					}
				}
			}
			
			r.setAssembly(rep);
			r.setStatus(Repair.PENDING);
			
			Thread t = new Thread(new Runnable(){

				@Override
				public void run() {
					new EmailManager(EmailManager.ORTHS).aquisitionNotice(r.getOverallColor(), r.getFormalName());					
				}
			
			});
			
			
			GUIEngine.refresh(-1, -1);
			frame.dispose();
			
			if(email.isSelected())
				t.run();
			
			break;
			
		case 2: //Use Part
			int sel = parts.getSelectedIndex();
			parts.removeAll();
			Constituent clone;
			try{
				clone = new Assembly((Assembly) supply[sel]);
			}catch(ClassCastException e){
				clone = new Part((Part) supply[sel]);
			}
			quants = ArrayManager.addToArray(clone.getQuantity(), quants);
			clone.setQuantity(1);
			cons = ArrayManager.addToArray(clone, cons);
			splitList.setListData(listContent());
			splitList.setSelectedIndex(0);
			supply = ArrayManager.removeFromArray(supply[sel], supply);
			parts.setListData(updateAvailible());
			
			break;
			
		case 3: //Cancel Repair
			//Reverse Paypal
			Database.subtractFromPayPalBalance(
					new Credit(r.getSellAmount().getValueInDollars()- r.getPayPalFee().getValueInDollars()));
			
			//Reverse ebay
			Database.addToEbayBalance(Date.getTodaysDate().getMonth(), 
					new Credit(-1 * r.getFinalValueFee().getValueInDollars()));
			
			r.getParent().removeRepair(r);
			
			GUIEngine.refresh(-1, -1);
			frame.dispose();
			break;
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		CardLayout lay = (CardLayout) data.getLayout();
		lay.show(data, "" + splitList.getSelectedIndex());
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		int val = (int) ((SpinnerNumberModel) arg0.getSource()).getValue();
		cons[splitList.getSelectedIndex()].setQuantity(val);
		updateCost();
	}

}
