package net.orthus.pm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class Writer extends BufferedWriter {
	
	private static Writer writer;
	
	
	
	public Writer(String filePath) throws IOException{
		super(new FileWriter(new File(filePath)));
	}
	
	private void encrln(String input) throws IOException{
		//writer.write(cheapEncrypt(input, Database.getPassword()) + "\n");
		writer.write(input + "\n");
	}
	
	public static String cheapEncrypt(String input, String key){
		int[] shift = getShift(key);
	
		String toReturn = "";
		for(int i=0; i<input.length(); i++){
			char nch = (char)((input.charAt(i) - 32 + shift[i % shift.length]) % 95 + 32);
			toReturn = toReturn.concat("" + nch);
		}
		
		return toReturn;
	}
	
	public static int[] getShift(String key){
		int[] shift = new int[key.length()];
		
		for(int i=0; i<key.length(); i++)
			shift[i] = Math.abs((key.charAt(i) * key.hashCode())) % 95;
		
		return shift;
	}
	
	private static void writeInitializer(String lastSave){
		try{
			writer = new Writer("Files/Initialize.oif");
			writer.write(lastSave);
			writer.flush();
		}catch(FileNotFoundException e){
			OptionPane.showError(OptionPane.FILE_NOT_FOUND, "Files/Initialize.oif");
		}catch(IOException e){
			System.err.println("Problem writing .oif file!");
		}
	}
	
	public static void save(String filePath){
		writeInitializer(filePath);
		try{
			writer = new Writer(filePath);
			
			//Header
			writer.write("-| ORTHUS PRODUCT MANAGER " + Database.VERSION + "\n");
			writer.write("-| SAVE FILE LAST UPDATED: " + Date.getTodaysDate().displayDate() + "\n\n");
			
			writer.encrln("Password#is&Correct!!"); //Password check
			
			//Arrays
			if(Database.getCategories() != null)
				for(int i=0; i<Database.getCategories().length; i++)
					writer.encrln(Database.getCategories()[i].record());
			if(Database.getServiceCategories() != null)
				for(int i=0; i<Database.getServiceCategories().length; i++)
					writer.encrln(Database.getServiceCategories()[i].record());
			if(Database.getProductCategories() != null)
				for(int i=0; i<Database.getProductCategories().length; i++)
					writer.encrln(Database.getProductCategories()[i].record());
			writer.encrln("/arrays");
			
			
			//Shipping Parts
			writer.encrln(Database.getShippingMats().record());
			
			//Balances
			String ppb = (Database.getPayPalBalance() == null)? "#cnull" : Database.getPayPalBalance().record();
			writer.encrln(ppb);
			String ebb = "";
			for(int i=0; i<Database.getEbayBalances().length; i++)
				if(Database.getEbayBalances()[i] == null)
					ebb = ebb.concat("#cnull");
				else
					ebb = ebb.concat(Database.getEbayBalances()[i].record());
			writer.encrln(ebb);
			String udi = (Database.getUndeliveredIncome() == null)? "#cnull" : Database.getUndeliveredIncome().record();
			writer.encrln(udi);
			
			//Personal Utility
			writer.encrln("" + Database.getLastSerial());
			String inv = (Database.getInvestmentGoal() == null)? "#cnull" : Database.getInvestmentGoal().record();
			writer.encrln(inv);
			writer.encrln("" + Database.getReinvestmentRate());
			writer.encrln("" + Database.getProfitPercentageGoal());
			writer.encrln("" + Database.getFreeListingsAllotted());
			writer.encrln("" + Database.getSaleQuantityAllotted());
			String saa = (Database.getSaleAmmountAllotted() == null)? "#cnull" : Database.getSaleAmmountAllotted().record();
			writer.encrln(saa);
			writer.encrln("" + Database.getPayPalFeeRate());
			writer.encrln("" + Database.isTopRatedSeller());
			
			//Class Utility
			String lli = (Database.getLastLogin() == null)? "#dnull" : Database.getLastLogin().record();
			writer.encrln(lli);
			writer.encrln("" + Database.getReorderLookBack());
			writer.encrln("" + Database.getReorderTollerance());
			writer.encrln("" + Database.ifInvoicePaid());
			writer.encrln(Database.getSubscription().record());
			writer.encrln(Database.getSalaryGoal().record());
			
			if(Database.getTimeStamps() == null)
				writer.encrln("#bnull");
			else{
				String ts = "";
				for(int i=0; i<Database.getTimeStamps().length; i++)
					ts = ts.concat(Database.getTimeStamps()[i].record());
				
				writer.encrln(ts);
			}
		
			writer.encrln(Database.getProductDescriptionTemplate());
			writer.encrln(Database.getPartDescriptionTemplate());
			writer.encrln(Database.getGenericDescriptionTemplate());
			
			writer.flush();
			
		}catch(FileNotFoundException e){
			OptionPane.showError(OptionPane.FILE_NOT_FOUND, filePath);
		}catch(IOException e){
			System.err.println("IOExeption in save()");
		}
		
		String file = " [ " + filePath.replaceAll(".*\\\\", "") + " ]";
		GUIEngine.getMainFrame().setTitle("Orthus Product Manager " + Database.VERSION + file);
		
		//uploadSave(filePath);
	}
	
	private static void uploadSave(String filePath){
		
		String ftpUrl = "ftp://%s:%s@%s/%s;type=i";
		String host = "ftp.cerberus1.net";
		String user = "cerber7";
		String pass = "M40A1RFL";
		String uploadPath = "/orthus/saves/save.orth";
		
		ftpUrl = String.format(ftpUrl, user, pass, host, uploadPath);
		
		try{
			URL url = new URL(ftpUrl);
			URLConnection conn = url.openConnection();
			OutputStream os = conn.getOutputStream();
			FileInputStream is = new FileInputStream(filePath);
			
			byte[] buffer = new byte[256];
			int bytesRead = -1;
			while((bytesRead = is.read(buffer)) != -1)
				os.write(buffer, 0, bytesRead);
			
			is.close();
			os.close();
			
			System.out.print("Save Uploaded");
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
