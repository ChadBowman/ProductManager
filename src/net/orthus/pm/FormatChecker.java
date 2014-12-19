package net.orthus.pm;

import java.io.File;

public class FormatChecker {

	//----- Advanced Methods
	public static boolean creditCheck(String input){
		try{
			Double.parseDouble(input);
		}catch(NumberFormatException e){
			OptionPane.showError(OptionPane.CREDIT_FORMAT);
			return false;
		}
		return true;
	}
	
	public static boolean percentageCheck(String input){
		double test;
		try{
			test = Double.parseDouble(input);
		}catch(NumberFormatException e){
			OptionPane.showError(OptionPane.PERCENTAGE_FORMAT);
			return false;
		}
		if(test < 0 || test > 100){
			OptionPane.showError(OptionPane.PERCENTAGE_FORMAT);
			return false;
		}
		return true;
	}
	
	public static boolean quantityCheck(String input){
		int test;
		try{
			test = Integer.parseInt(input);
		}catch(NumberFormatException e){
			OptionPane.showError(OptionPane.QUANTITY_FORMAT);
			return false;
		}
		if(test < 0){
			OptionPane.showError(OptionPane.QUANTITY_FORMAT);
			return false;
		}
		return true;
	}
	
	public static boolean blankStringCheck(String input, String aditional){
		if(!stringCheck(input)) return false;
		
		if(input == null){
			OptionPane.showError(OptionPane.BLANKSTRING_FORMAT, aditional);
			return false;
		}else if(input.equals("")){
			OptionPane.showError(OptionPane.BLANKSTRING_FORMAT, aditional);
			return false;
		}
		return true;
	}
	
	public static boolean stringCheck(String input){
		if(input.contains("&") || input.contains("#")){
			OptionPane.showError(OptionPane.STRING_FORMAT);
			return false;
		}
		return true;
	}
	
	public static boolean extentionCheck(File check){
		if(check.getName().endsWith(".osf"))
			return true;
		return false;
	}
	
	public static boolean dateCheck(String input){
		if(input == null){
			OptionPane.showError(OptionPane.DATE_FORMAT);
			return false;
		}
		String[] split = input.split("\\.");

		try{
			Integer.parseInt(split[0]);
			Integer.parseInt(split[1]);
			Integer.parseInt(split[2]);
		}catch(ArrayIndexOutOfBoundsException e){
			OptionPane.showError(OptionPane.DATE_FORMAT);
			return false;
		}catch(NumberFormatException e){
			OptionPane.showError(OptionPane.DATE_FORMAT);
			return false;
		}
		return true;
	}
	
	public static boolean shippingOptionCheck(ShippingOption s){
		
		if(Database.getShippingMats() == null)
			return false;
		
		if(!s.enoughParts(Database.getShippingMats())){
			OptionPane.showError(OptionPane.SHIPPING_OPTION, s.getName());
			return false;
		}else
			return true;
		
	
	}

}
