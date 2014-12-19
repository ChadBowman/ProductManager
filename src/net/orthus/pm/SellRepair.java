package net.orthus.pm;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class SellRepair extends ActionFrame 
						implements ActionListener {
	//----- Variables
	//UI
	private static SellRepair frame;
	private TextField name, zip, date, note, price, email;
	private JCheckBox check;
	
	//Data
	private static Service parent;
	private int id;
	private String rname;
	

	//----- Constructor
	public SellRepair() {
		super("Sell Repair", 210, 400, null, new GridLayout(0,1), new GridLayout());
		
		id = parent.getNextID();
		rname = parent.getName();
		
		center.add(new JLabel(rname + " " + id, SwingConstants.CENTER));
		center.add(price = new TextField("" + parent.getListAmount().getValueInDollars(), 
				"Sell Ammount"));
		center.add(name = new TextField("Buyer Name"));
		center.add(zip = new TextField("Buyer ZIP"));
		center.add(email = new TextField("Buyer Email"));
		center.add(date = new TextField(Date.getTodaysDate().displaySimpleDate(), "Sell Date"));
		center.add(check = new JCheckBox("Send Email"));
		check.setSelected(true);
		center.add(note = new TextField("Note"));
		
		bottom.add(new Button("Sell", this, 0));
	}
	
	//----- Methods
	public static void initializeFrame(){
		parent = DataSelection.getSelectedService();
		if(parent == null) return;
		
		// TODO Check to make sure there are enough in the listing
		
		if(parent.getListType().equals(Item.NOT_LISTED)){
			OptionPane.showConfirm(parent.getName() + " must be listed before repairs can be sold.", 
					"Service Status Error!");
			return;
		}
		frame = new SellRepair();
		frame.setVisible(true);
	}
	
	//----- Interfaces
	@Override
	public void actionPerformed(ActionEvent arg0) {
		int cmd = Integer.parseInt(arg0.getActionCommand());
		switch(cmd){
		case 0:	//Submit
			if(FormatChecker.stringCheck(name.getText())
					&& FormatChecker.creditCheck(price.getText())
					&& FormatChecker.quantityCheck(zip.getText()) //TODO make checker for ZIP
					&& FormatChecker.dateCheck(date.getText())
					&& FormatChecker.stringCheck(note.getText())
					&& FormatChecker.stringCheck(email.getText())){
				
				
				parent.setQuantity(parent.getQuantity() - 1);
				
				//Move parts from supply to repair
				Assembly supply = parent.getProductCategory().getPartSupply();
				Assembly usedInRepair = null;
				
				if(parent.getDefaultAssembly() != null){
					
					usedInRepair = new Assembly(
							Database.getNewSerial(),
							1,
							0,
							parent.getAddress(),
							parent.getName(),
							Assembly.REPAIR,
							Assembly.NO_COLOR,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null);
					
					Assembly needed = parent.getDefaultAssembly();
					
					if(needed.getAssemblies() != null)
						for(int i=0; i<needed.getAssemblies().length; i++){
							Assembly clone = new Assembly(needed.getAssemblies()[i]);
							clone.setStatus(Assembly.HOLD);
							
							supply.removeAssemblyEquiv(clone);
							
							clone.setStatus(Assembly.REPAIR);
							usedInRepair.addAssembly(clone);
						}
					
					if(needed.getParts() != null)
						for(int i=0; i<needed.getParts().length; i++){
							Part clone = new Part(needed.getParts()[i]);
							clone.setStatus(Part.HOLD);
							
							supply.removePart(clone);
							
							clone.setStatus(Part.REPAIR);
							usedInRepair.addPart(clone);
						}
				}
						
					
				Credit fvf = new Credit(parent.getListAmount().getValueInDollars()
							* parent.getServiceCategory().getServiceFinalValueFeeRate());
				
				//Ebay Fees
				Database.addToEbayBalance(new Date(date.getText()).getMonth(), fvf);
				
				Credit rev = new Credit(parent.getListAmount());
				Credit ppf = new Credit(rev.getValueInDollars() * Database.getPayPalFeeRate() + 0.3);
				rev.subtract(ppf);
				//PayPal Balance
				Database.addToPayPalBalance(rev);
				
				
				final Repair rep = new Repair(
						parent.getAddress(),
						id,
						false,  //TODO Is there ever a time a repair qualifies for TRS discount?
						zip.getText(),
						Repair.INCOMING,
						email.getText(),
						note.getText(),
						name.getText(),
						null,
						new Credit(price.getText()),
						fvf,
						ppf,
						new Credit(),
						new Credit(),
						new Date(date.getText()), //Date sold
						null,
						null,
						usedInRepair);
			
				parent.addRepair(rep);
				
				
				Thread t = new Thread(new Runnable(){

					@Override
					public void run() {
						new EmailManager(EmailManager.ORTHS).purchaseNotice(email.getText(), rep.getFormalName());
					}
					
				});
				
				GUIEngine.refresh(-1, -1);
				frame.dispose();
				
				if(check.isSelected())
					t.run();
			}
		}

	}

}
