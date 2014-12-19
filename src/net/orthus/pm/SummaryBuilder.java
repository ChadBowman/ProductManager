package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class SummaryBuilder{

	public static JPanel initializeSummary(){
		Date todaysDate = Date.getTodaysDate();
		StatManager stat = new StatManager();
		
		//Main JPanel initialization
		JPanel container = new JPanel(new BorderLayout());
		JPanel summary = new JPanel(new GridLayout(0,2));
		container.add(new JScrollPane(summary), BorderLayout.CENTER);
		summary.setBorder(BorderManager.getLineBorder());
		summary.setBackground(ColorManager.getOrthusSteel());
		
		
		//----- Left JPanel
		JPanel leftJPanel = new JPanel();
		leftJPanel.setLayout(new BoxLayout(leftJPanel, BoxLayout.Y_AXIS));
		leftJPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		
		//Income
		JPanel incomeJPanel = new JPanel(new GridLayout(0,3));
		incomeJPanel.setBorder(BorderManager.getTitleBorder("Income"));
		incomeJPanel.setBackground(ColorManager.getTransparent());
		leftJPanel.add(incomeJPanel);
		//Total
		JLabel total1 = new JLabel("Total", SwingConstants.RIGHT);
		JLabel total2 = new JLabel(stat.getCurrentIncome().toString(), SwingConstants.RIGHT);
		total1.setFont(FontManager.getStatisticFont());
		total2.setFont(FontManager.getStatisticFont());
		incomeJPanel.add(total1);
		incomeJPanel.add(total2);
		incomeJPanel.add(new JLabel());
		
		
		
		//Profit
		JPanel profitJPanel = new JPanel(new GridLayout(0,3));
		profitJPanel.setBorder(BorderManager.getTitleBorder("Profit"));
		profitJPanel.setBackground(ColorManager.getTransparent());
		leftJPanel.add(profitJPanel);
		//Week Profit
		Credit week = stat.getProfit(StatManager.WEEK);
		JLabel weekProfit1 = new JLabel("Week", SwingConstants.RIGHT);
		JLabel weekProfit2 = new JLabel(week.toString(), SwingConstants.RIGHT);
		JLabel weekProfit3 = new JLabel(stat.getWeekPercentage() + "%", SwingConstants.LEFT);
		profitJPanel.add(weekProfit1);
		profitJPanel.add(weekProfit2);
		profitJPanel.add(weekProfit3);
		weekProfit1.setFont(FontManager.getStatisticFont());
		weekProfit2.setFont(FontManager.getStatisticFont());
		weekProfit3.setFont(FontManager.getStatisticFont());
		weekProfit3.setBorder(new EmptyBorder(0,40,0,0));
		
		Double goal = Database.getSalaryGoal().getValueInDollars();
		Double pgoal = Database.getProfitPercentageGoal();
		
		Double wper, mper, yper;
		try{
			wper = Double.parseDouble(stat.getWeekPercentage()) / 100;
		}catch(NumberFormatException e){
			wper = 0.0;
		}
		
		//Color changers
		if(week.getValueInDollars() > goal / 52.17743 * 1.5)
			weekProfit2.setForeground(ColorManager.getAboveGoal());
		else if(week.getValueInDollars() > goal / 52.17743 * 1.1)
			weekProfit2.setForeground(ColorManager.getOnGoal());
		else if(week.getValueInDollars() > goal / 52.17743)
			weekProfit2.setForeground(ColorManager.getLeavingGoal());
		else if(week.getValueInDollars() * 1.1 > goal / 52.17743)
			weekProfit2.setForeground(ColorManager.getApproachingGoal());
		else
			weekProfit2.setForeground(ColorManager.getOffGoal());
		
		if(wper > pgoal * 1.5)
			weekProfit3.setForeground(ColorManager.getAboveGoal());
		else if(wper > pgoal * 1.1)
			weekProfit3.setForeground(ColorManager.getOnGoal());
		else if(wper > pgoal)
			weekProfit3.setForeground(ColorManager.getLeavingGoal());
		else if(wper * 1.1 > pgoal)
			weekProfit3.setForeground(ColorManager.getApproachingGoal());
		else
			weekProfit3.setForeground(ColorManager.getOffGoal());
			
		
		//Month Profit
		Credit month = stat.getProfit(StatManager.MONTH);
		JLabel monthProfit1 = new JLabel(String.format("%s", todaysDate.getMonthString()), SwingConstants.RIGHT);
		JLabel monthProfit2 = new JLabel(month.toString(), SwingConstants.RIGHT);
		JLabel monthProfit3 = new JLabel(stat.getMonthPercentage() + "%", SwingConstants.LEFT);
		profitJPanel.add(monthProfit1);
		profitJPanel.add(monthProfit2);
		profitJPanel.add(monthProfit3);
		monthProfit1.setFont(FontManager.getStatisticFont()); 
		monthProfit2.setFont(FontManager.getStatisticFont());
		monthProfit3.setFont(FontManager.getStatisticFont());
		monthProfit3.setBorder(new EmptyBorder(0,40,0,0));
		
		Credit rev = stat.getMonthRevenue();
		System.out.println(todaysDate.getMonthString() + " Revenue " + rev.toString());
		
		try{
			mper = Double.parseDouble(stat.getMonthPercentage()) / 100;
		}catch(NumberFormatException e){
			mper = 0.0;
		}
	
		if(month.getValueInDollars() > goal / (365.242 / Date.getTodaysDate().getDay()) * 1.5)
			monthProfit2.setForeground(ColorManager.getAboveGoal());
		else if(month.getValueInDollars() > goal / (365.242 / Date.getTodaysDate().getDay()) * 1.1)
			monthProfit2.setForeground(ColorManager.getOnGoal());
		else if(month.getValueInDollars() > goal / (365.242 / Date.getTodaysDate().getDay()))
			monthProfit2.setForeground(ColorManager.getLeavingGoal());
		else if(month.getValueInDollars() * 1.1 > goal / (365.242 / Date.getTodaysDate().getDay()))
			monthProfit2.setForeground(ColorManager.getApproachingGoal());
		else
			monthProfit2.setForeground(ColorManager.getOffGoal());
		
		if(mper > pgoal * 1.5)
			monthProfit3.setForeground(ColorManager.getAboveGoal());
		else if(mper > pgoal * 1.1)
			monthProfit3.setForeground(ColorManager.getOnGoal());
		else if(mper > pgoal)
			monthProfit3.setForeground(ColorManager.getLeavingGoal());
		else if(mper * 1.1 > pgoal)
			monthProfit3.setForeground(ColorManager.getApproachingGoal());
		else
			monthProfit3.setForeground(ColorManager.getOffGoal());
		
		
		//Year Profit
		Credit year = stat.getProfit(StatManager.YEAR);
		JLabel yearProfit1 = new JLabel(String.format("20%d", todaysDate.getYear()), SwingConstants.RIGHT);
		JLabel yearProfit2 = new JLabel(year.toString(), SwingConstants.RIGHT);
		JLabel yearProfit3 = new JLabel(stat.getYearPercentage() + "%", SwingConstants.LEFT);
		profitJPanel.add(yearProfit1);
		profitJPanel.add(yearProfit2);
		profitJPanel.add(yearProfit3);
		yearProfit1.setFont(FontManager.getStatisticFont());
		yearProfit2.setFont(FontManager.getStatisticFont());
		yearProfit3.setFont(FontManager.getStatisticFont());
		yearProfit3.setBorder(new EmptyBorder(0,40,0,0));
		
		try{
			yper = Double.parseDouble(stat.getYearPercentage()) / 100;
		}catch(NumberFormatException e){
			yper = 0.0;
		}
		double prop = 365.0 / Date.getTodaysDate().dayOfYear();
		if(year.getValueInDollars() > goal * 1.5 / prop)
			yearProfit2.setForeground(ColorManager.getAboveGoal());
		else if(year.getValueInDollars() > goal * 1.1 / prop)
			yearProfit2.setForeground(ColorManager.getOnGoal());
		else if(year.getValueInDollars() > goal / prop)
			yearProfit2.setForeground(ColorManager.getLeavingGoal());
		else if(year.getValueInDollars() * 1.1 > goal / prop)
			yearProfit2.setForeground(ColorManager.getApproachingGoal());
		else
			yearProfit2.setForeground(ColorManager.getOffGoal());
		
		if(yper > pgoal * 1.5)
			yearProfit3.setForeground(ColorManager.getAboveGoal());
		else if(yper > pgoal * 1.1)
			yearProfit3.setForeground(ColorManager.getOnGoal());
		else if(yper > pgoal)
			yearProfit3.setForeground(ColorManager.getLeavingGoal());
		else if(yper * 1.1 > pgoal)
			yearProfit3.setForeground(ColorManager.getApproachingGoal());
		else
			yearProfit3.setForeground(ColorManager.getOffGoal());
			
		
		
		//Wages
		JPanel revJPanel = new JPanel(new GridLayout(0,3));
		revJPanel.setBorder(BorderManager.getTitleBorder("Hourly Wage"));
		revJPanel.setBackground(ColorManager.getTransparent());
		leftJPanel.add(revJPanel);
		double[] wages = stat.getHourlyWages();
		
		//Week Revenue
		JLabel weekRev1 = new JLabel("Week", SwingConstants.RIGHT);
		JLabel weekRev2 = new JLabel(String.format("%.2f", wages[0]), SwingConstants.RIGHT);
		revJPanel.add(weekRev1);
		revJPanel.add(weekRev2);
		revJPanel.add(new JLabel());
		weekRev1.setFont(FontManager.getStatisticFont());
		weekRev2.setFont(FontManager.getStatisticFont());
		//Month Revenue
		JLabel monthRev1 = new JLabel(todaysDate.getMonthString(), SwingConstants.RIGHT);
		JLabel monthRev2 = new JLabel(String.format("%.2f", wages[1]), SwingConstants.RIGHT);
		revJPanel.add(monthRev1);
		revJPanel.add(monthRev2);
		revJPanel.add(new JLabel());
		monthRev1.setFont(FontManager.getStatisticFont());
		monthRev2.setFont(FontManager.getStatisticFont());
		
		//Year Revenue
		JLabel yearRev1 = new JLabel(String.format("20%d", todaysDate.getYear()), SwingConstants.RIGHT);
		JLabel yearRev2 = new JLabel(String.format("%.2f", wages[2]), SwingConstants.RIGHT);
		revJPanel.add(yearRev1);
		revJPanel.add(yearRev2);
		revJPanel.add(new JLabel());
		yearRev1.setFont(FontManager.getStatisticFont());
		yearRev2.setFont(FontManager.getStatisticFont());
		
		double wageGoal = Database.getSalaryGoal().getValueInDollars() / 720;
	
			//Achieve salary goal working 15 hrs/week
		if(wages[0] > wageGoal * 1.5)
			weekRev2.setForeground(ColorManager.getAboveGoal());
		else if(wages[0] > wageGoal * 1.1)
			weekRev2.setForeground(ColorManager.getOnGoal());
		else if(wages[0] > wageGoal)
			weekRev2.setForeground(ColorManager.getLeavingGoal());
		else if(wages[0] * 1.1 > wageGoal)
			weekRev2.setForeground(ColorManager.getApproachingGoal());
		else
			weekRev2.setForeground(ColorManager.getOffGoal());
		
		if(wages[1] > wageGoal * 1.5)
			monthRev2.setForeground(ColorManager.getAboveGoal());
		else if(wages[1] > wageGoal * 1.1)
			monthRev2.setForeground(ColorManager.getOnGoal());
		else if(wages[1] > wageGoal)
			monthRev2.setForeground(ColorManager.getLeavingGoal());
		else if(wages[1] * 1.1 > wageGoal)
			monthRev2.setForeground(ColorManager.getApproachingGoal());
		else
			monthRev2.setForeground(ColorManager.getOffGoal());
		
		if(wages[2] > wageGoal * 1.5)
			yearRev2.setForeground(ColorManager.getAboveGoal());
		else if(wages[2] > wageGoal * 1.1)
			yearRev2.setForeground(ColorManager.getOnGoal());
		else if(wages[2] > wageGoal)
			yearRev2.setForeground(ColorManager.getLeavingGoal());
		else if(wages[2] * 1.1 > wageGoal)
			yearRev2.setForeground(ColorManager.getApproachingGoal());
		else
			yearRev2.setForeground(ColorManager.getOffGoal());
		
		//Time worked
		String[] worked = stat.getTimeWorked();
		JPanel workedJPanel = new JPanel(new GridLayout(0,3));
		workedJPanel.setBorder(BorderManager.getTitleBorder("Hours Worked"));
		workedJPanel.setBackground(ColorManager.getTransparent());
		leftJPanel.add(workedJPanel);
		//Today
		JLabel today1 = new JLabel("Today", SwingConstants.RIGHT);
		JLabel today2 = new JLabel(worked[0], SwingConstants.RIGHT);
		today1.setFont(FontManager.getStatisticFont());
		today2.setFont(FontManager.getStatisticFont());
		workedJPanel.add(today1);
		workedJPanel.add(today2);
		workedJPanel.add(new JLabel());
		//Week
		JLabel wek1 = new JLabel("Week", SwingConstants.RIGHT);
		JLabel wek2 = new JLabel(worked[1], SwingConstants.RIGHT);
		wek1.setFont(FontManager.getStatisticFont());
		wek2.setFont(FontManager.getStatisticFont());
		workedJPanel.add(wek1);
		workedJPanel.add(wek2);
		workedJPanel.add(new JLabel());
		//Month
		JLabel mn1 = new JLabel(Date.getTodaysDate().getMonthString(), SwingConstants.RIGHT);
		JLabel mn2 = new JLabel(worked[2], SwingConstants.RIGHT);
		mn1.setFont(FontManager.getStatisticFont());
		mn2.setFont(FontManager.getStatisticFont());
		workedJPanel.add(mn1);
		workedJPanel.add(mn2);
		workedJPanel.add(new JLabel());
		
		//Balance
		JPanel balanceJPanel = new JPanel(new GridLayout(0,3));
		balanceJPanel.setBorder(BorderManager.getTitleBorder("Balances"));
		balanceJPanel.setBackground(ColorManager.getTransparent());
		leftJPanel.add(balanceJPanel);
		
		//PayPal Balance
		JLabel payPal1 = new JLabel("PayPal", SwingConstants.RIGHT);
		JLabel payPal2 = (Database.getPayPalBalance() == null)? 
				new JLabel("$0.00", SwingConstants.RIGHT) : 
				new JLabel(Database.getPayPalBalance().toString(), SwingConstants.RIGHT);
		payPal1.setFont(FontManager.getStatisticFont());
		payPal2.setFont(FontManager.getStatisticFont());
		balanceJPanel.add(payPal1);
		balanceJPanel.add(payPal2);
		balanceJPanel.add(new JLabel());
		
		//eBay Invoice Balance
		JLabel ebayInvoice1 = new JLabel("eBay", SwingConstants.RIGHT);
		
		Credit invbal = new Credit();
		if(!Database.ifInvoicePaid())
			invbal.add(Database.getEbayBalance(Date.getTodaysDate().getMonth() - 1));
		if(Database.getEbayBalance(Date.getTodaysDate().getMonth()) != null)
			invbal.add(Database.getEbayBalance(Date.getTodaysDate().getMonth()));
		
		JLabel ebayInvoice2 = new JLabel(invbal.toString(), SwingConstants.RIGHT);
		ebayInvoice1.setFont(FontManager.getStatisticFont());
		ebayInvoice2.setFont(FontManager.getStatisticFont());
		balanceJPanel.add(ebayInvoice1);
		balanceJPanel.add(ebayInvoice2);
		balanceJPanel.add(new JLabel());
		
		//PayPal - eBay Invoice
		Credit net = new Credit();
		if(Database.getPayPalBalance() != null)
			net.add(Database.getPayPalBalance());
		if(Database.getEbayBalance(Date.getTodaysDate().getMonth()) != null)
			net.subtract(Database.getEbayBalance(Date.getTodaysDate().getMonth()));
		if(Database.getUndeliveredIncome() != null)
			net.subtract(Database.getUndeliveredIncome());
		if(!Database.ifInvoicePaid() && Database.getEbayBalance(Date.getTodaysDate().getMonth() - 1) != null)
			net.subtract(Database.getEbayBalance(Date.getTodaysDate().getMonth() - 1));
		JLabel difference1 = new JLabel("Net Total", SwingConstants.RIGHT);
		JLabel difference2 = new JLabel(net.toString(), SwingConstants.RIGHT);
		difference1.setFont(FontManager.getStatisticFont());
		difference2.setFont(FontManager.getStatisticFont()); 
		if(net.getValueInDollars() < 0)
			difference2.setForeground(ColorManager.getOffGoal());
		else
			difference2.setForeground(ColorManager.getOnGoal());
		balanceJPanel.add(difference1);
		balanceJPanel.add(difference2);
		balanceJPanel.add(new JLabel());
		
		
		//----- Right Panel
		JPanel rightJPanel = new JPanel();
		rightJPanel.setLayout(new BoxLayout(rightJPanel, BoxLayout.Y_AXIS));
		rightJPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		//Investment
		JPanel investmentJPanel = new JPanel(new GridLayout(0,2));
		investmentJPanel.setBorder(BorderManager.getTitleBorder("Investment"));
		investmentJPanel.setBackground(ColorManager.getTransparent());
		rightJPanel.add(investmentJPanel);
		//Current Investment
		JLabel totalInvestment1 = new JLabel("Current Investment", SwingConstants.RIGHT);
		JLabel totalInvestment2 = new JLabel(stat.getTotalInvestment().toString(), SwingConstants.RIGHT);
		totalInvestment1.setFont(FontManager.getStatisticFont());
		totalInvestment2.setFont(FontManager.getStatisticFont());
		totalInvestment2.setBorder(new EmptyBorder(0,0,0,70));
		investmentJPanel.add(totalInvestment1);
		investmentJPanel.add(totalInvestment2);
		
		Double inv = stat.getTotalInvestment().getValueInDollars();
		Double igoal = Database.getInvestmentGoal().getValueInDollars();
		
		if(inv > igoal * 1.5)
			totalInvestment2.setForeground(ColorManager.getOnGoal());
		else if(inv > igoal * 1.1)
			totalInvestment2.setForeground(ColorManager.getOnGoal());
		else if(inv > igoal)
			totalInvestment2.setForeground(ColorManager.getLeavingGoal());
		else if(inv * 1.1 > igoal)
			totalInvestment2.setForeground(ColorManager.getApproachingGoal());
		else
			totalInvestment2.setForeground(ColorManager.getOffGoal());
		
		
		
		JLabel cash1 = new JLabel("Cash", SwingConstants.RIGHT);
		JLabel cash2 = new JLabel(stat.getCashProportion(), SwingConstants.RIGHT);
		cash1.setFont(FontManager.getStatisticFont());
		cash2.setFont(FontManager.getStatisticFont());
		cash2.setBorder(new EmptyBorder(0,0,0,70));
		investmentJPanel.add(cash1);
		investmentJPanel.add(cash2);
		JLabel products1 = new JLabel("Complete Products", SwingConstants.RIGHT);
		JLabel products2 = new JLabel(stat.getProductProportion(), SwingConstants.RIGHT);
		products1.setFont(FontManager.getStatisticFont());
		products2.setFont(FontManager.getStatisticFont()); 
		products2.setBorder(new EmptyBorder(0,0,0,70));
		investmentJPanel.add(products1);
		investmentJPanel.add(products2);
		JLabel parts1 = new JLabel("Parts", SwingConstants.RIGHT);
		JLabel parts2 = new JLabel(stat.getPartProportion(), SwingConstants.RIGHT);
		parts1.setFont(FontManager.getStatisticFont());
		parts2.setFont(FontManager.getStatisticFont()); 
		parts2.setBorder(new EmptyBorder(0,0,0,70));
		investmentJPanel.add(parts1);
		investmentJPanel.add(parts2);
		
		
		//Products
		JPanel products = new JPanel(new GridLayout(0,4));
		products.setBorder(BorderManager.getTitleBorder("Products"));
		products.setBackground(ColorManager.getTransparent());
		rightJPanel.add(products);	
		//Incoming
		JLabel incoming1 = new JLabel("Incoming", SwingConstants.RIGHT);
		JLabel incoming2 = new JLabel(stat.incProduct(), SwingConstants.RIGHT);
		incoming1.setFont(FontManager.getStatisticFont());
		incoming2.setFont(FontManager.getStatisticFont());
		incoming2.setBorder(new EmptyBorder(0,0,0,70));
		products.add(incoming1);
		products.add(incoming2);
		//Inventory
		JLabel inventory1 = new JLabel("Inventory", SwingConstants.RIGHT);
		JLabel inventory2 = new JLabel(stat.invProduct(), SwingConstants.RIGHT);
		inventory1.setFont(FontManager.getStatisticFont());
		inventory2.setFont(FontManager.getStatisticFont());
		inventory2.setBorder(new EmptyBorder(0,0,0,70));
		products.add(inventory1);
		products.add(inventory2);
		//Stock
		JLabel stock1 = new JLabel("Stock", SwingConstants.RIGHT);
		JLabel stock2 = new JLabel(stat.stkProduct(), SwingConstants.RIGHT);
		stock1.setFont(FontManager.getStatisticFont());
		stock2.setFont(FontManager.getStatisticFont());
		stock2.setBorder(new EmptyBorder(0,0,0,70));
		products.add(stock1);
		products.add(stock2);
		//Listed
		JLabel listed1 = new JLabel("Listed", SwingConstants.RIGHT);
		JLabel listed2 = new JLabel(stat.lstProduct(), SwingConstants.RIGHT);
		listed1.setFont(FontManager.getStatisticFont());
		listed2.setFont(FontManager.getStatisticFont());
		listed2.setBorder(new EmptyBorder(0,0,0,70));
		products.add(listed1);
		products.add(listed2);
		//Sold
		JLabel sold1 = new JLabel("Sold", SwingConstants.RIGHT);
		JLabel sold2 = new JLabel(stat.sldProduct(), SwingConstants.RIGHT);
		sold1.setFont(FontManager.getStatisticFont());
		sold2.setFont(FontManager.getStatisticFont());
		sold2.setBorder(new EmptyBorder(0,0,0,70));
		products.add(sold1);
		products.add(sold2);
		//Returning
		JLabel returning1 = new JLabel("Returned", SwingConstants.RIGHT);
		JLabel returning2 = new JLabel(stat.retProduct(), SwingConstants.RIGHT);
		returning1.setFont(FontManager.getStatisticFont());
		returning2.setFont(FontManager.getStatisticFont());
		returning2.setBorder(new EmptyBorder(0,0,0,70));
		products.add(returning1);
		products.add(returning2);
		
		
		//Services
		JPanel services = new JPanel(new GridLayout(0,4));
		services.setBorder(BorderManager.getTitleBorder("Services"));
		services.setBackground(ColorManager.getTransparent());
		rightJPanel.add(services);
		//Incoming
		JLabel incoming3 = new JLabel("Incoming", SwingConstants.RIGHT);
		JLabel incoming4 = new JLabel(stat.incRepair(), SwingConstants.RIGHT);
		incoming3.setFont(FontManager.getStatisticFont());
		incoming4.setFont(FontManager.getStatisticFont());
		incoming4.setBorder(new EmptyBorder(0,0,0,70));
		services.add(incoming3);
		services.add(incoming4);
		//Pending
		JLabel pending1 = new JLabel("Pending", SwingConstants.RIGHT);
		JLabel pending2 = new JLabel(stat.penRepair(), SwingConstants.RIGHT);
		pending1.setFont(FontManager.getStatisticFont());
		pending2.setFont(FontManager.getStatisticFont());
		pending2.setBorder(new EmptyBorder(0,0,0,70));
		services.add(pending1);
		services.add(pending2);
		//Sold
		JLabel complete1 = new JLabel("Complete", SwingConstants.RIGHT);
		JLabel complete2 = new JLabel(stat.comRepair(), SwingConstants.RIGHT);
		complete1.setFont(FontManager.getStatisticFont());
		complete2.setFont(FontManager.getStatisticFont());
		complete2.setBorder(new EmptyBorder(0,0,0,70));
		services.add(complete1);
		services.add(complete2);
		//Returning
		JLabel ret1 = new JLabel("Returned", SwingConstants.RIGHT);
		JLabel ret2 = new JLabel(stat.retRepair(), SwingConstants.RIGHT);
		ret1.setFont(FontManager.getStatisticFont());
		ret2.setFont(FontManager.getStatisticFont());
		ret2.setBorder(new EmptyBorder(0,0,0,70));
		services.add(ret1);
		services.add(ret2);
		
		
		//Selling Limits
		/*JPanel limitsJPanel = new JPanel(new GridLayout(0,4));
		limitsJPanel.setBorder(BorderManager.getTitleBorder("Selling Limits"));
		limitsJPanel.setBackground(ColorManager.getTransparent());
		rightJPanel.add(limitsJPanel);
		//Items
		JLabel items1 = new JLabel("Items", SwingConstants.CENTER);
		int num = stat.getNumberOnTheMarket();
		
		JLabel items2 = new JLabel("" + (Database.getSaleQuantityAllotted() - num)
				+ " (" + (Database.getFreeListingsAllotted() - num) + ")", SwingConstants.CENTER);
		
		items1.setFont(FontManager.getStatisticFont());
		items2.setFont(FontManager.getStatisticFont());
		limitsJPanel.add(items1);
		limitsJPanel.add(items2);
		
		if(Database.getSaleQuantityAllotted() < 0.1 * Database.getSaleQuantityAllotted())
			items2.setForeground(Color.red);
		else if(Database.getSaleQuantityAllotted() < 0.25 * Database.getSaleQuantityAllotted())
			items2.setForeground(ColorManager.getOrthusBronze());
		
		//Amount
		String saa = ((Database.getSaleAmmountAllotted() != null)? 
				new Credit(Database.getSaleAmmountAllotted().getValueInCents()
						- stat.getAmountOnTheMarket().getValueInCents()).toString().replaceAll("\\...", "")
				: new Credit(0)).toString();
		
		JLabel amount1 = new JLabel("Amount", SwingConstants.CENTER);
		JLabel amount2 = new JLabel(saa, SwingConstants.CENTER);
		amount1.setFont(FontManager.getStatisticFont());
		amount2.setFont(FontManager.getStatisticFont());
		limitsJPanel.add(amount1);
		limitsJPanel.add(amount2);
		
		if(Database.getSaleAmmountAllotted() != null)
			if(Database.getSaleAmmountAllotted().getValueInDollars() 
					< 0.1 * Database.getSaleAmmountAllotted().getValueInDollars())
				amount2.setForeground(Color.red);
			else if(Database.getSaleAmmountAllotted().getValueInDollars()
					< 0.25 * Database.getSaleAmmountAllotted().getValueInDollars())
				amount2.setForeground(ColorManager.getOrthusBronze());
		*/
		
		//Alerts
		JPanel alertJPanel = new JPanel(new BorderLayout());
		alertJPanel.setBackground(ColorManager.getTransparent());
		rightJPanel.add(alertJPanel);
		JTextArea alerts = new JTextArea();
		alerts.setEditable(false);
		alerts.setBackground(ColorManager.getTransparent());
		alerts.setForeground(Color.black);
		alerts.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), "Alerts"), 
				new EmptyBorder(10,10,10,10)));
		alertJPanel.add(alerts);
		
		Credit inc = stat.getProfit(StatManager.MONTH);
		inc.multiply(1 - Database.getReinvestmentRate());
		if(inc.getValueInCents() > 37500)
			alerts.append("Rent Covered!\n");
		if(inc.getValueInCents() > 48000)
			alerts.append("Utilities Covered!\n");
		if(inc.getValueInCents() > 80000)
			alerts.append("Food/Play Covered!\n");
		if(inc.getValueInCents() > 10000)
			alerts.append("Savings Covered!\n");

		alerts.append("\n");
		String[] sug = new AlertManager().bidSuggestions();
		for(int i=0; i<sug.length; i++)
			alerts.append(sug[i]);
		
		alerts.append("\nRecommend Lowering Prices:\n");
		ProductCategory[] cats = Database.getProductCategories();
		for(int i=0; i<cats.length; i++){
			String[] sug1 = new ProductStats(cats[i]).getPriceChangeRecomendations();
			if(sug1 != null)
				for(int j=0; j<sug1.length; j++)
					alerts.append(sug1[j]);
		}
		
		summary.add(leftJPanel);
		summary.add(rightJPanel);
		
		return container;
	}
}
