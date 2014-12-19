package net.orthus.pm;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class SummaryActions {
	
	public static void salaryGoal(){
		
		String inp = OptionPane.showInput(
				String.format("The current salary goal is is %s%n" +
						"What would you like to change it to?", 
						Database.getSalaryGoal().toString()),
						"Salary Goal");
		
		if(inp == null) return;
		if(FormatChecker.creditCheck(inp)){ //If in correct format
			Database.setSalaryGoal(new Credit(inp));
			GUIEngine.refresh(GUIEngine.SUMMARY, -1);
		}else{
			salaryGoal();
			return;
		}
	}
	
	public static void subscriptionFee(){
		
		String inp = OptionPane.showInput(
				String.format("The current monthly subscription fee is %s%n" +
						"What would you like to change it to?", 
						Database.getSubscription().toString()),
						"Subscription Fee");
		
		if(inp == null) return;
		if(FormatChecker.creditCheck(inp)){ //If in correct format
			Database.setSubscription(new Credit(inp));
			GUIEngine.refresh(GUIEngine.SUMMARY, -1);
		}else{
			subscriptionFee();
			return;
		}
	}
	
	public static void payInvoice(){
		
		int lastMonth = (Date.getTodaysDate().getMonth() == 1)? 12 : Date.getTodaysDate().getMonth() - 1;
		
		int op = OptionPane.showConfirmYes("Have you paid last month's eBay Invoice of " + 
		Database.getEbayBalance(lastMonth).toString() + "?", "Pay Invoice");
		System.out.print(op);
		if(op == 0){
			Database.setInvoicePaid(true);
			Database.subtractFromPayPalBalance(Database.getEbayBalance(lastMonth));
		}
		GUIEngine.refresh(GUIEngine.SUMMARY, -1);
	}

	public static void profitPercentage(){
		
		String inp = OptionPane.showInput(
				String.format("The current Profit Percentage Goal is %.0f%%%n" +
						"What would you like to change it to?", 
						Database.getProfitPercentageGoal() * 100.0), 
						"Profit Percentage Goal");
		
		if(inp == null) return;
		if(FormatChecker.percentageCheck(inp)){ //If in correct format
			Database.setProfitPercentageGoal(Double.parseDouble(inp) / 100.0);
			GUIEngine.refresh(GUIEngine.SUMMARY, -1);
		}else{
			profitPercentage();
			return;
		}
	}
	
	public static void reinvestmentRate(){
		
		String inp = OptionPane.showInput(
				String.format("The current Reinvestment Rate is %.0f%%%n" +
						"What would you like to change it to?",
						Database.getReinvestmentRate() * 100.0), 
						"Reinvestment Rate");
		
		if(inp == null) return;
		if(FormatChecker.percentageCheck(inp)){
			Database.setReinvestmentRate(Double.parseDouble(inp) / 100);
			GUIEngine.refresh(GUIEngine.SUMMARY, -1);
		}else{
			reinvestmentRate();
			return;
		}
	}
	
	public static void paypalFee(){
		
		String inp = OptionPane.showInput(
				String.format("The current PayPal fee Rate is %.02f%%%n" +
						"What would you like to change it to?",
						Database.getPayPalFeeRate() * 100.0), 
						"PayPal Fee Rate");
		
		if(inp == null) return;
		if(FormatChecker.percentageCheck(inp)){
			Database.setPayPalFeeRate(Double.parseDouble(inp) / 100);
			GUIEngine.refresh(GUIEngine.SUMMARY, -1);
		}else{
			paypalFee();
			return;
		}
	}
	
	public static void investmentGoal(){
		
		String data = (Database.getInvestmentGoal() == null)? "$0.0" :
			Database.getInvestmentGoal().toString();
		
		String inp = OptionPane.showInput(
				String.format("The current Investment Goal is %s%n" +
						"What would you like to change it to?", data), 
						"Investment Goal");
		
		if(inp == null) return;
		if(FormatChecker.creditCheck(inp)){
			Database.setInvestmentGoal(new Credit(inp));
			GUIEngine.refresh(GUIEngine.SUMMARY, -1);
		}else{
			investmentGoal();
			return;
		}
	}
	
	public static void payPalBalance(){
		
		String data = (Database.getPayPalBalance() == null)? "$0.0" :
			Database.getPayPalBalance().toString();
		
		String inp = OptionPane.showInput(
				String.format("The current PayPal Balance is %s%n" +
						"What would you like to change it to?", data),
						"PayPal Balance");
		
		if(inp == null) return;
		switch(inp.charAt(0)){
		case '+':
			if(FormatChecker.creditCheck(inp.substring(1)))
				if(Database.getPayPalBalance() == null)
					Database.setPayPalBalance(new Credit(inp.substring(1)));
				else
					Database.getPayPalBalance().add(new Credit(inp.substring(1)));
			else{
				payPalBalance();
				return;
			}
			break;
			
		case '-':
			if(FormatChecker.creditCheck(inp.substring(1)))
				if(Database.getPayPalBalance() == null)
					Database.setPayPalBalance(new Credit(-1 
							* new Credit(inp.substring(1)).getValueInDollars()));
				else
					Database.getPayPalBalance().subtract(new Credit(inp.substring(1)));
			else{
				payPalBalance();
				return;
			}
			break;
			
		default:
			if(FormatChecker.creditCheck(inp))
				Database.setPayPalBalance(new Credit(inp));
			else{
				payPalBalance();
				return;
			}
		}
		GUIEngine.refresh(GUIEngine.SUMMARY, -1);
	}
	
	public static void ebayInvoice(){
		
		String inp = OptionPane.showInput(
				"Change invoice to (int)month, amount", 
				"eBay Invoice");
		
		if(inp == null) return;
		String[] inps = inp.split(", ");
		
		if(inps.length < 2) return;
		if(FormatChecker.quantityCheck(inps[0]) 
				&& FormatChecker.creditCheck(inps[1]))
			Database.setEbayBalance(Integer.parseInt(inps[0]), 
					new Credit(inps[1]));
				
	
		GUIEngine.refresh(GUIEngine.SUMMARY, -1);
	}
	
	public static void freeListings(){
		
		String inp = OptionPane.showInput(
				String.format("Currently there are %d free listings per month%n" +
						"What would you like to change it to?", Database.getFreeListingsAllotted()), 
						"Free Listings");
		
		if(inp == null) return;
		if(FormatChecker.quantityCheck(inp))
			Database.setFreeListingsAllotted(Integer.parseInt(inp));
		else{
			freeListings();
			return;
		}
		
		GUIEngine.refresh(GUIEngine.SUMMARY, -1);
	}
	
	public static void saleLimits(){
		
		String s = (Database.getSaleAmmountAllotted() == null)? "$0.0" 
				: Database.getSaleAmmountAllotted().toString();
		
		String msg = String.format(
				"The current selling limits are %n%d sales or %s%n", 
				Database.getSaleQuantityAllotted(), s);
		
		JPanel pane = new JPanel(new GridLayout(0,1));
		JTextArea a;
		pane.add(a = new JTextArea(msg));
		a.setEditable(false);
		
		TextField quan, amt;
		pane.add(quan = new TextField("Quantity"));
		pane.add(amt = new TextField("Amount"));
		
		JOptionPane.showMessageDialog(
				GUIEngine.getMainFrame(), 
				pane, 
				"Selling Limits", 
				JOptionPane.QUESTION_MESSAGE);
		
		if(quan.getText().equals("") && amt.getText().equals("")) return; //No input
		
		if(FormatChecker.quantityCheck(quan.getText()) &&
				FormatChecker.creditCheck(amt.getText())){
			Database.setSaleQuantitiyAllotted(Integer.parseInt(quan.getText()));
			Database.setSaleAmmountAllotted(new Credit(amt.getText()));
			GUIEngine.refresh(GUIEngine.SUMMARY, -1);
		}else{
			saleLimits();
			return;
		}
		
	}
	
	public static void reorderTollerance(){
		
		String msg = String.format(
				"When calculating when to reorder a part,%n" +
				"the usage rate is gathered from data up to%n" +
				"%d days ago and if that rate cannot be continued%n" +
				"for %d days, a reorder recommendation is made.", 
				Database.getReorderLookBack(),
				Database.getReorderTollerance());
		
		JPanel pane = new JPanel(new GridLayout(0,1));
		JTextArea a;
		pane.add(a = new JTextArea(msg));
		a.setEditable(false);
		
		TextField quan, amt;
		pane.add(quan = new TextField("Days Prior To Gather Data"));
		pane.add(amt = new TextField("Days To Sustain Rate"));
		
		JOptionPane.showMessageDialog(
				GUIEngine.getMainFrame(), 
				pane, 
				"Reorder Tollerence", 
				JOptionPane.QUESTION_MESSAGE);
		
		if(quan.getText().equals("") && amt.getText().equals("")) return; //No input
		
		if(FormatChecker.quantityCheck(quan.getText()) &&
				FormatChecker.quantityCheck(amt.getText())){
			Database.setReorderLookBack(Integer.parseInt(quan.getText()));
			Database.setReorderTollerance(Integer.parseInt(amt.getText()));
			GUIEngine.refresh(GUIEngine.SUMMARY, -1);
		}else{
			saleLimits();
			return;
		}
		
	}
		
	public static void sendPaycheck(){
		
		GUIEngine.getParentTPane().setSelectedIndex(0);
		String inp = OptionPane.showInput("Send how much?", "Send Paycheck");
		
		if(inp == null) return;
		if(FormatChecker.creditCheck(inp)){
			Database.getPayPalBalance().subtract(new Credit(inp));
			Database.getUndeliveredIncome().subtract(new Credit(inp));
			GUIEngine.refresh(GUIEngine.SUMMARY, -1);
		}else{
			sendPaycheck();
			return;
		}
	}
	


}
