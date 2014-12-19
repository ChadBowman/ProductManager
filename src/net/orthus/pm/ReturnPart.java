package net.orthus.pm;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;

public class ReturnPart extends ActionFrame 
						implements ActionListener {

	//----- Variables
	//UI
	private static ReturnPart frame;
	private TextField date;
	
	//Data
	private static Constituent p;
	
	
	//----- Constructor
	public ReturnPart() {
		super("Return Product", 200, 200, new GridLayout(0,1));
		
		center.add(new Label(p.getName(), "Name", SwingConstants.CENTER));
		center.add(date = new TextField(Date.getTodaysDate().displaySimpleDate(), "Date Returned"));
		
		bottom.add(new Button("Return", this, 0));
	}
	
	//----- Methods
	public static void intializeFrame(){
		p = DataSelection.getSelectedConstituent();
		
		if(p == null) return;
		if(!p.getStatus().equals(Product.SOLD)){
			OptionPane.showConfirm(p.getName() + " must be sold before it can be returned.",
					"Product Status Error!");
			return;
		}
		
		frame = new ReturnPart();
		frame.setVisible(true);
	}

	//----- Interfaces
	@Override
	public void actionPerformed(ActionEvent arg0) {
	
		if(!FormatChecker.dateCheck(date.getText())) return;
		
		p.setStatus(Constituent.RETURNED);
		p.setDateSold(new Date(date.getText()));
		
		Database.addToUndeliveredIncome(new Credit(-1 
				* (p.getProfit().getValueInDollars() 
				* (1 - Database.getReinvestmentRate()))));
		Database.addToPayPalBalance(new Credit(-1 * p.getListPrice().getValueInDollars()));
		Database.addToEbayBalance(new Date(date.getText()).getMonth(), 
				new Credit(-1 * p.getFinalValueFee().getValueInDollars()));
	
		GUIEngine.refresh(-1, -1);
		frame.dispose();
			
	}

}
