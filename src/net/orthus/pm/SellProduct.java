package net.orthus.pm;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class SellProduct extends ActionFrame 
						 implements ActionListener {
	
	//----- Variables
	//UI
	private static SellProduct frame;
	private TextField amt, date, note;
	private ComboBox<String> ship;
	private ProductCategory cat;
	
	//Data
	private static Product p;
	
	//----- Constructor
	public SellProduct() {
		super("Sell Product", 200, 300, new GridLayout(0,1));
		
		cat = p.getParent();
		
		center.add(new JLabel(p.getFullName(), SwingConstants.CENTER));
		center.add(amt = new TextField("" + p.getListAmount().getValueInDollars(), "Sell Amount"));
		center.add(ship = new ComboBox<String>(null, cat.getShippinOptionNames(), null, -1, "Shipping Option"));
		center.add(date = new TextField(Date.getTodaysDate().displaySimpleDate(), "Date Sold"));
		center.add(note = new TextField(p.getNote(), "Note"));
		bottom.add(new Button("Sell", this, 0));
		
	}
	
	//----- Methods
	public static void initializeFrame(){
		p = DataSelection.getSelectedProduct();
		
		if(p == null) return;
		if(!p.getStatus().equals(Product.LISTED)){
			OptionPane.showConfirm(p.getFullName() + " must be listed before it can be sold.", 
					"Product Status Error!");
			return;
		}
	
		frame = new SellProduct();
		frame.setVisible(true);
	}
	
	//----- Interfaces
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(FormatChecker.creditCheck(amt.getText())
				&& FormatChecker.dateCheck(date.getText())
				&& FormatChecker.stringCheck(note.getText())){
			
			p.setStatus(Product.SOLD);
			p.setSellAmount(new Credit(amt.getText()));
			p.setDateSold(new Date(date.getText()));
			p.setNote(note.getText());
			p.setDiscounted(Database.isTopRatedSeller(new Date(date.getText())));
			
			
			ShippingOption so = null;
			if(ship.getSelectedItem().equals("Custom"))
				while(so == null){
					so = new CustomShippingOption(cat).getSO();
					if(so.getName().equals("Cancel"))
						return;
				}
			else
				so = cat.getShippingOptions()[ship.getSelectedIndex()];
			
			if(!FormatChecker.shippingOptionCheck(so))
				return;
			
			
			p.setShippingCost(so.useOption(cat, new Date(date.getText())));
			p.setShippingPaid(so.getPaid());
			
			Credit fvfship = (so.getName().contains("Express"))? new Credit() :
				new Credit(cat.getProductFinalValueFeeRate() * so.getPaid().getValueInDollars());
			
			Credit fvf = new Credit(
					(cat.getProductFinalValueFeeRate(new Date(date.getText()))
					* new Credit(amt.getText()).getValueInDollars()) 
					+ fvfship.getValueInDollars());
			p.setFinalValueFee(fvf);
			
			Credit ppf = new Credit(Database.getPayPalFeeRate()
					* (new Credit(amt.getText()).getValueInDollars() + so.getPaid().getValueInDollars())
					+ 0.3);
			p.setPayPalFee(ppf);
			
			Database.addToPayPalBalance(new Credit(
					new Credit(amt.getText()).getValueInDollars()	//Sell amt
					+ so.getPaid().getValueInDollars()				//Plus ship paid
					- so.getCost().getValueInDollars()				//Minus ship cost
					- ppf.getValueInDollars()));					//Minus paypal fee
			
			Database.addToEbayBalance(new Date(date.getText()).getMonth(), fvf);
			
			Database.addToUndeliveredIncome(new Credit(
					(1 - Database.getReinvestmentRate()) * p.getProfit().getValueInDollars()));
			
			GUIEngine.refresh(-1, -1);
			frame.dispose();
		}

	}

}
