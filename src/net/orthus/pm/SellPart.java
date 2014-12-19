package net.orthus.pm;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SellPart extends ActionFrame 
				      implements ActionListener,
				                 ChangeListener{

	//----- Variables
	//UI
	private static SellPart frame;
	private TextField amt, date;
	private Spinner qunt;
	private ComboBox<String> ship;
	
	//Data
	private static Constituent c;
	private Category cat;
	private ShippingOption[] ops;
	
	
	//----- Constructor
	public SellPart() {
		super("Sell Part", 200, 300, new GridLayout(0,1));
		
		cat = Database.getFocusedCategory();
		ops = ((ServiceCategory) cat).getShippingOptions();
		
		String[] opStr = ((ServiceCategory) cat).getShippinOptionNames();
	
		
		center.add(new Label(c.getName(), "Name", SwingConstants.CENTER));
		
		String amount = "";
		if(c.getStatus().equals(Constituent.LISTED_AUCTION))
			center.add(new Label("" + c.getQuantity(), "Quantity", SwingConstants.CENTER));
		else{
			int end = (c.getEffectiveQuantity() < c.getNumPerListing())? c.getNumPerListing() : c.getEffectiveQuantity();
			
			center.add(qunt = new Spinner(c.getNumPerListing(), end, c.getNumPerListing(), this, "Quantity"));
			amount = "" + c.getListPrice().getValueInDollars();
		}
			

		center.add(amt = new TextField(amount, "Sell Amount"));
		center.add(ship = new ComboBox<String>(null, opStr, null, -1, "Shipping Option"));
		center.add(date = new TextField(Date.getTodaysDate().displaySimpleDate(), "Date Sold"));
		
		
		bottom.add(new Button("Sell", this, 0));
	}
	
	//----- Methods
	public static void initalizeFrame(){
		c = DataSelection.getSelectedConstituent();
		
		if(c == null) return;
		if(!(c.getStatus().equals(Constituent.LISTED_AUCTION) 
				|| c.getStatus().equals(Constituent.LISTED_FIXED)
				|| c.getStatus().equals(Constituent.LISTED_FIXED_AUTO))){
			
			OptionPane.showConfirm(c.getName() + " must be listed before it can be sold.", 
					"Part Status Error!");
			return;
		}
		
		if(c.getNumPerListing() == 0)
			c.setNumPerListing(1);
		
		frame = new SellPart();
		frame.setVisible(true);
		
		
	}
	
	
	
	//----- Interfaces
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(FormatChecker.creditCheck(amt.getText())
				&& FormatChecker.dateCheck(date.getText())){
			
			
			ProductCategory pc = (ProductCategory) cat;
			
			ShippingOption so = null;
			if(ship.getSelectedItem().equals("Custom"))
				while(so == null){
					so = new CustomShippingOption(pc).getSO();
					if(so.getName().equals("Cancel"))
						return;
				}
			else
				so = pc.getShippingOptions()[ship.getSelectedIndex()];
			
			if(!FormatChecker.shippingOptionCheck(so))
				return;
			
			Credit ppf = new Credit(so.getPaid());
			ppf.add(new Credit(amt.getText()));
			ppf.multiply(Database.getPayPalFeeRate());
			ppf.add(0.3);
			
			Credit fvf = new Credit();
			fvf.add((new Credit(amt.getText()).getValueInDollars() 
					+ so.getPaid().getValueInDollars())
					* cat.getDefaultAssembly().getProductCategory()
						.getServiceFinalValueFeeRate());
			
			
			
			if(c.getStatus().equals(Constituent.LISTED_AUCTION)){ //Whole listing is sold
			
				c.setStatus(Constituent.SOLD);
				c.setDateSold(new Date(date.getText()));
				c.setListPrice(new Credit(amt.getText()));
				c.setFinalValueFee(fvf);
				c.setPayPalFee(ppf);
				c.setShippingPaid(so.getPaid());
				c.setShippingCost(so.useOption(cat, new Date(date.getText())));
				Database.addToUndeliveredIncome(
						new Credit(c.getProfit().getValueInDollars()
								* (1 - Database.getReinvestmentRate())));
			
				pc.addUseRecord(new UseRecord(c.getSerial(), c.getQuantity(), new Date(date.getText())));
				
			}else{			//Fixed listing
				
					
				int q = (int) qunt.getValue(); //Number sold
				
				
				if(q < c.getQuantity()){ // partial sold
					
					c.setQuantity(c.getQuantity() - q); //Deduct from listing
					
					Constituent clone;
					try{
						clone = new Assembly((Assembly) c);
						cat.getPartSupply().addAssembly((Assembly) clone);
					}catch(ClassCastException e){
						clone = new Part((Part) c);
						cat.getPartSupply().addPart((Part) clone);
					}
					
					clone.setStatus(Constituent.SOLD);
					clone.setQuantity(q);
					clone.setDateSold(new Date(date.getText()));
					clone.setListPrice(new Credit(amt.getText()));
	
					clone.setFinalValueFee(fvf);
					clone.setPayPalFee(ppf);
					clone.setShippingPaid(so.getPaid());
					clone.setShippingCost(so.useOption(cat, new Date(date.getText())));
					Database.addToUndeliveredIncome(
							new Credit(clone.getProfit().getValueInDollars()
									* (1 - Database.getReinvestmentRate())));
				
					pc.addUseRecord(new UseRecord(clone.getSerial(), 
							clone.getQuantity(), new Date(date.getText())));
						
				}else{ //Sell all parts at once
					
					c.setStatus(Constituent.SOLD);
					c.setDateSold(new Date(date.getText()));
					c.setListPrice(new Credit(amt.getText()));
					c.setFinalValueFee(fvf);
					c.setPayPalFee(ppf);
					c.setShippingPaid(so.getPaid());
					c.setShippingCost(so.useOption(cat, new Date(date.getText())));
					
					Database.addToUndeliveredIncome(
							new Credit(c.getProfit().getValueInDollars()
									* (1 - Database.getReinvestmentRate())));
					
					pc.addUseRecord(new UseRecord(c.getSerial(), 
							c.getQuantity() * c.getNumPerListing(), new Date(date.getText())));
				}
				
				Database.addToEbayBalance(new Date(date.getText()).getMonth(), fvf);
				
				Credit total = new Credit(amt.getText());
				total.add(so.getPaid());
				total.subtract(ppf); 	//Paypal Fee
				total.subtract(so.getCost()); //USPS cost
				
				Database.addToPayPalBalance(total);
				
			}
			
			GUIEngine.refresh(-1, -1);
			frame.dispose();
		}
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		amt.setText("" + (c.getListPrice().getValueInDollars() * (int) qunt.getValue() / c.getNumPerListing()));		
	}

}
