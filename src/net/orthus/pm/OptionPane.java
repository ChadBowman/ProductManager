package net.orthus.pm;

import javax.swing.JOptionPane;

public class OptionPane{
	
	//----- Constants
	public static final int CREDIT_FORMAT = 0;
	public static final int PERCENTAGE_FORMAT = 1;
	public static final int QUANTITY_FORMAT = 2;
	public static final int BLANKSTRING_FORMAT = 3;
	public static final int STRING_FORMAT = 4;
	public static final int FILE_NOT_FOUND = 5;
	public static final int INVALID_PASSWORD = 6;
	public static final int DATE_FORMAT = 7;
	public static final int QUANTITY_LIMIT = 8;
	public static final int SHIPPING_OPTION = 9;
	public static final int RECOMEND_DELETE = 10;
	
	//----- Advanced Methods
	public static int showConfirmYes(String message, String title){
		return JOptionPane.showConfirmDialog(
				GUIEngine.getMainFrame(),
				message,
				title,
				JOptionPane.YES_OPTION);
	}
	
	public static int showConfirm(String message, String title){
		return JOptionPane.showConfirmDialog(
				GUIEngine.getMainFrame(), 
				message, 
				title, 
				JOptionPane.OK_CANCEL_OPTION);
	}
	
	public static String showInput(String message, String title){
		return JOptionPane.showInputDialog(
				GUIEngine.getMainFrame(), 
				message, 
				title, 
				JOptionPane.QUESTION_MESSAGE);
	}
	
	public static void showError(int type){
		String message, title;
		
		switch(type){
		case CREDIT_FORMAT: 
			message = "Input must be an integer or decimal."; 
			title = "Credit Format Error!"; break;
			
		case PERCENTAGE_FORMAT:
			message = "A percentage must be between 0 and 100.";
			title = "Percentage Format Error!"; break;
			
		case QUANTITY_FORMAT:
			message = "Please use a positive integer.";
			title = "Quantity Format Error!"; break;
			
		case BLANKSTRING_FORMAT:
			message = "Please input a valid string.";
			title = "Blank String Error!"; break;
			
		case STRING_FORMAT:
			message = "'&' and '#' cannot be used.";
			title = "String Format Error!"; break;
			
		case INVALID_PASSWORD:
			message = "Password does not match.";
			title = "Invalid Password!"; break;
			
		case DATE_FORMAT:
			message = "Invald date format.\nUse: DD.MM.YY";
			title = "Date Format Error!"; break;
			
		case SHIPPING_OPTION:
			message = "Insufficient parts in supply for shipping option";
			title = "Insufficient Parts!"; break;
			
		case RECOMEND_DELETE:
			message = "Use Delete function to remove entire part/assembly";
			title = "Decrement Value Exceeded!"; break;
			
		default: message = "There was an error!"; title = "Error!";
		}
		JOptionPane.showMessageDialog(
				GUIEngine.getMainFrame(), 
				message, 
				title, 
				JOptionPane.ERROR_MESSAGE);
	}
	
	public static void showError(int type, String aditional){
		String message, title;
		
		switch(type){
		case CREDIT_FORMAT: 
			message = aditional + " must be an integer or decimal."; 
			title = "Credit Format Error!"; break;
			
		case PERCENTAGE_FORMAT:
			message = aditional + " must be between 0 and 100.";
			title = "Percentage Format Error!"; break;
			
		case QUANTITY_FORMAT:
			message = "Please use a positive integer for " + aditional + ".";
			title = "Quantity Format Error!"; break;
			
		case BLANKSTRING_FORMAT:
			message = "Please input a valid string for " + aditional + ".";
			title = "Blank String Error!"; break;
			
		case FILE_NOT_FOUND:
			message = "File not found in:\n" + aditional;
			title = "File Not Found!"; break;
			
		case QUANTITY_LIMIT: 
			message = "Quantity limit exceeded!\n" + aditional;
			title = "Limit Exceeded!"; break;
			
		case SHIPPING_OPTION:
			message = "Insufficient parts in supply for " + aditional + " shipping option.";
			title = "Insufficient Parts!"; break;
			
		default: message = "There was an error in " + aditional + "!"; title = "Error!";
		}
		
		JOptionPane.showMessageDialog(
				GUIEngine.getMainFrame(), 
				message, 
				title, 
				JOptionPane.ERROR_MESSAGE);
	}
		
	public static void showError(String message, String title){
		JOptionPane.showMessageDialog(
				GUIEngine.getMainFrame(),
				message,
				title,
				JOptionPane.ERROR_MESSAGE);
	}

}
