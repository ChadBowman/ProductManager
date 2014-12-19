package net.orthus.pm;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ReturnProduct extends ActionFrame 
						   implements ActionListener {

	//----- Variables
	//UI
	private static ReturnProduct frame;
	private TextField ad, date, note;
	
	//Data
	private static Product p;
	
	
	//----- Constructor
	public ReturnProduct() {
		super("Return Product", 200, 230, new GridLayout(0,1));
		
		center.add(new JLabel(p.getFullName(), SwingConstants.CENTER));
		center.add(ad = new TextField("0.0", "Aditional Cost"));
		center.add(date = new TextField(Date.getTodaysDate().displaySimpleDate(), "Date Returned"));
		center.add(note  = new TextField(p.getNote(), "Note"));
		
		bottom.add(new Button("Return", this, 0));
	}
	
	//----- Methods
	public static void intializeFrame(){
		p = DataSelection.getSelectedProduct();
		
		if(p == null) return;
		if(!p.getStatus().equals(Product.SOLD)){
			OptionPane.showConfirm(p.getFullName() + " must be sold before it can be returned.",
					"Product Status Error!");
			return;
		}
		
		frame = new ReturnProduct();
		frame.setVisible(true);
	}

	//----- Interfaces
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(FormatChecker.dateCheck(date.getText())
				&& FormatChecker.stringCheck(note.getText())
				&& FormatChecker.creditCheck(ad.getText())){
		
			
			p.setDateReturned(new Date(date.getText()));
			p.setNote(note.getText());
			
			//FFV reversal (proactive)
			Database.addToEbayBalance(new Date(date.getText()).getMonth(), 
					new Credit(-1 * p.getFinalValueFee().getValueInDollars()));
			
			//PPF reversal
			Database.addToPayPalBalance(p.getPayPalFee());
			
			//Reversal in paycheck
			Database.addToUndeliveredIncome(new Credit(-1 * ((1 - Database.getReinvestmentRate()) 
					* p.getProfit().getValueInDollars())));
			
			//Reverse payment
			//The 0.3 is the 30 cents PayPal keeps
			Database.subtractFromPayPalBalance(new Credit(p.getSellAmount().getValueInDollars()
					+ p.getShippingPaid().getValueInDollars() + 0.3));
			
			//Subtract additional costs
			Database.subtractFromPayPalBalance(new Credit(ad.getText()));
			
			p.setStatus(Product.RETURNED);
			
			GUIEngine.refresh(-1, -1);
			frame.dispose();
			
			OptionPane.showConfirm("Don't forget to cancel the sale on eBay.", "Reminder");
			
		}
	}

}
