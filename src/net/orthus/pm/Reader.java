package net.orthus.pm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Reader{

	//----- Variables
	private static BufferedReader reader;
	
	//----- Constructor
	public Reader(){}
	
	//----- Advanced Methods
	public boolean load(File save){
		String first = "";
		try {
			if(save == null)
				save = getLastSaveLocation();
			if(save == null)
				return false;
			
				reader = new BufferedReader(new FileReader(save));
				first = nextLine();

		} catch (FileNotFoundException e) {
			OptionPane.showError(OptionPane.FILE_NOT_FOUND, save.getPath());
			return false;
		} catch (IOException e){
			System.err.println("Problem loading .osf!");
		}
		
		
		while(true){
			//String password = OptionPane.showInput("Please enter password.", 
			//		"Welcome to Orthus Product Manager " + Database.VERSION);
				//TODO temp disable
			String password = "Orthus";
			
			if(password == null || password.equals("")) 
				return false; //Cancel load
			
			Database.setPassword(password);
	
			//if(cheapDecrypt(first, password).equals("Password#is&Correct!!"))
				break;
		}
		
		
		//Category collection
		Category[] categories = null;
		ServiceCategory[] serviceCats = null;
		ProductCategory[] productCats = null;
		String line = getLine();
		while(!line.equals("/arrays")){
			switch(line.charAt(1)){
			case 'k': categories = 
					ArrayManager.addToArray(parseCategory(line.substring(2)), categories); break;
			case 'l': serviceCats = 
					ArrayManager.addToArray(parseServiceCategory(line.substring(2)), serviceCats); break;
			case 'm': productCats = 
					ArrayManager.addToArray(parseProductCategory(line.substring(2)), productCats); break;
			}
			line = getLine();
		}
		Database.setCategories(categories);
		Database.setServiceCategories(serviceCats);
		Database.setProductCategories(productCats);
		
		//Shipping Parts
		Database.setShippingMats(parseAssembly(getLine()));
		
		//Balance collection
		Database.setPayPalBalance(parseCredit(getLine().substring(2)));
		Database.setEbayBalances(parseCreditArray(getLine().substring(2)));
		Database.setUndeliveredIncome(parseCredit(getLine().substring(2)));
		
		//Personal Utility collection
		Database.setLastSerial(Integer.parseInt(getLine()));
		Database.setInvestmentGoal(parseCredit(getLine().substring(2)));
		Database.setReinvestmentRate(Double.parseDouble(getLine()));
		Database.setProfitPercentageGoal(Double.parseDouble(getLine()));
		Database.setFreeListingsAllotted(Integer.parseInt(getLine()));
		Database.setSaleQuantitiyAllotted(Integer.parseInt(getLine()));
		Database.setSaleAmmountAllotted(parseCredit(getLine().substring(2)));
		Database.setPayPalFeeRate(Double.parseDouble(getLine()));
		Database.setTopRatedSeller(Boolean.parseBoolean(getLine()));
		Database.setLastLogin(parseDate(getLine().substring(2)));
		Database.setLastSavePath(save.getPath());
		Database.setReorderLookBack(Integer.parseInt(getLine()));
		Database.setReorderTollerance(Integer.parseInt(getLine()));
		Database.setInvoicePaid(Boolean.parseBoolean(getLine()));
		Database.setSubscription(parseCredit(getLine().substring(2)));
		Database.setSalaryGoal(parseCredit(getLine().substring(2)));
		Database.setTimeStamps(parseTimeStampArray(getLine().substring(2)));
		Database.setProductDescriptionTemplate(getLine());
		Database.setPartDescriptionTemplate(getLine());
		
		return true;
	}
	
	private File getLastSaveLocation() throws IOException{
		File toReturn = null;
		try {
			reader = new BufferedReader(new FileReader(new File("Files/Initialize.oif")));
			toReturn = new File(reader.readLine());
		} catch (FileNotFoundException e) {
			OptionPane.showError(OptionPane.FILE_NOT_FOUND, "Files/Initialize.oif");
		} catch (NullPointerException e){}
		
		return toReturn;
	}
	
	private TimeStamp[] parseTimeStampArray(String input){
		if(input.equals("null")) return null;
		
		String[] split = input.split("#b");
		TimeStamp[] ret = null;
		
		for(int i=0; i<split.length; i++){
			if(!split[i].equals(""))
				ret = ArrayManager.addToArray(parseTimeStamp(split[i]), ret);
		}
		
		return ret;
	}
	
	private TimeStamp parseTimeStamp(String input){
		
		String[] split = input.split("#d");
		return new TimeStamp(parseDate(split[1]), parseDate(split[2]));
	}
	
	private ProductCategory parseProductCategory(String input){
		if(input == null) return null;

		//Reserved for Regex fixing TODO
		
		
		String[] split = input.split("#X|#S|#Y|#T|#O|#U");
		String[] basics = split[0].split("&");
		String[] col = split[4].split("&");
		
		String[] colors = null;
		if(!split[4].equals("null"))
			if(col != null)
				for(int i=0; i<col.length; i++)
					colors = ArrayManager.addToArray(parseString(col[i]), colors);
	
		return new ProductCategory(
				parseString(basics[0]),
				Double.parseDouble(basics[1]),
				Double.parseDouble(basics[2]),
				parseAssemblyArray(split[1]),
				parseAssemblyArray(split[2]),
				parseServiceArray(split[3]),
				colors,
				parseProductArray(split[5]),
				parseShippingOptionArray(split[6]),
				parseUseRecordArray(split[7]));
		
	}//End parseProductCategory()
	
	private ServiceCategory parseServiceCategory(String input){
		if(input == null) return null;
		
		String[] split = input.split("#X|#S|#O|#U");
		String[] basics = split[0].split("&");

		return new ServiceCategory(
				parseString(basics[0]),
				Double.parseDouble(basics[1]),
				parseAssemblyArray(split[1]),
				parseAssemblyArray(split[2]),
				parseServiceArray(split[3]),
				parseShippingOptionArray(split[4]),
				parseUseRecordArray(split[5]));
		
	}//End parseServiceCategory()
	
	private Category parseCategory(String input){
		if(input == null) return null;
		
		String[] split = input.split("#X");
		
		return new Category(
				parseString(split[0]),
				parseAssemblyArray(split[1]),
				parseAssemblyArray(split[2]));
		
	}//End parseCategory()
	
	private UseRecord[] parseUseRecordArray(String input){
	if(input.equals("null")) return null;
		
		String[] use = input.substring(2).split("#u");
		UseRecord[] rec = null;
		
		if(use != null)
			for(int i=0; i<use.length; i++)
				rec = ArrayManager.addToArray(parseUseRecord(use[i]), rec);
		
		return rec;
	}
	
	private UseRecord parseUseRecord(String input){
		if(input == null) return null;
		
		String[] s = input.split("#d");
		String[] s1 = s[0].split("&");
		
		return new UseRecord(
				Integer.parseInt(s1[0]),
				Integer.parseInt(s1[1]),
				parseDate(s[1]));
	}
	
	private GenericItem parseGenericItem(String input){
		if(input == null) return null;
		
		String[] split = input.split("#c|#d");
		String[] basics = split[0].split("&");
		
		return new GenericItem(
				Double.parseDouble(basics[0]),
				Boolean.parseBoolean(basics[1]),
				parseString(basics[2]),
				parseString(basics[3]),
				parseCredit(split[1]),
				parseCredit(split[2]),
				parseCredit(split[3]),
				parseCredit(split[4]),
				parseCredit(split[5]),
				parseCredit(split[6]),
				parseCredit(split[7]),
				parseDate(split[8]),
				parseDate(split[9]));
		
	}//End parseGenericItem()
	
	private Product[] parseProductArray(String input){
		if(input.equals("null")) return null;
		
		String[] pro = input.substring(2).split("#t");
		Product[] products = null;
		
		if(pro != null)
			for(int i=0; i<pro.length; i++)
				products = ArrayManager.addToArray(parseProduct(pro[i]), products);
		
		return products;
	}
	
	private Product parseProduct(String input){
		if(input.equals("null")) return null;
		
		String[] split = input.split("#x"); 	   //Chop off assembly
		String[] split2 = split[0].split("#c|#d"); //Chop Credits & Dates
		String[] basics = split2[0].split("&");    //Isolate basics
	
		int id = 0, dur = 0;
		try{
			id = Integer.parseInt(basics[1]);
			dur = Integer.parseInt(basics[2]);
		}catch(NumberFormatException e){
			System.err.println("Product Parse Error!");
		}catch(ArrayIndexOutOfBoundsException e){
			System.err.println("["+input+ "]" + "\nCaused an out of bounds exception!");
		}
		
		return new Product(
				parseString(basics[0]),
				id,
				dur,
				Boolean.parseBoolean(basics[3]),
				parseString(basics[4]),
				parseString(basics[5]),
				parseString(basics[6]),
				parseString(basics[7]),
				parseString(basics[8]),
				parseCredit(split2[1]),
				parseCredit(split2[2]),
				parseCredit(split2[3]),
				parseCredit(split2[4]),
				parseCredit(split2[5]),
				parseCredit(split2[6]),
				parseCredit(split2[7]),
				parseCredit(split2[8]),
				parseDate(split2[9]),
				parseDate(split2[10]),
				parseDate(split2[11]),
				parseDate(split2[12]),
				parseAssembly(split[1]));
		
	}//End parseProduct()
	
	private ShippingOption[] parseShippingOptionArray(String input){
		if(input.equals("null")) return null;
		
		String[] shi = input.substring(2).split("#o");
		ShippingOption[] options = null;
		
		if(shi != null)
			for(int i=0; i<shi.length; i++)
				options = ArrayManager.addToArray(parseShippingOption(shi[i]), options);
		
		return options;
		
	}//End parseShippingOptionArray()
	
	private ShippingOption parseShippingOption(String input){
		if(input.equals("null")) return null;
		
		String[] split = input.split("#x");
		String[] s2 = split[0].split("#c");
		
		return new ShippingOption(
				parseString(s2[0]),
				parseCredit(s2[1]),
				parseCredit(s2[2]),
				parseAssembly(split[1]));
		
	}//End parseShippingOption()
	
	private Service[] parseServiceArray(String input){
		if(input.equals("null")) return null;
		
		String[] ser = input.split("#s");
		Service[] services = null;
		
		if(ser != null)
			for(int i=0; i<ser.length; i++)
				if(!ser[i].equals(""))
					services = ArrayManager.addToArray(parseService(ser[i]), services);
		
		return services;
		
	}//End parseServiceArray()
	
	private Service parseService(String input){
		if(input.equals("null")) return null;
	
		String[] repairs = input.split("#R");
		String[] assemb = repairs[0].split("#x");
		String[] split = assemb[0].split("#c|#d");
		String[] basics = split[0].split("&");
		
		int dur = 0, quant = 0;
		try{ 
			dur = Integer.parseInt(basics[1]);
			quant = Integer.parseInt(basics[2]);
		}catch(NumberFormatException e){
			System.err.println("Service Parse Error!");
		}
		
		return new Service(
				parseString(basics[0]),
				dur,
				quant,
				Boolean.parseBoolean(basics[3]),
				Boolean.parseBoolean(basics[4]),
				parseString(basics[5]),
				parseString(basics[6]),
				parseCredit(split[1]),
				parseCredit(split[2]),
				parseDate(split[3]),
				parseAssembly(assemb[1]),
				parseRepairArray(repairs[1]));
		
	}//End parseService()
	
	private Repair[] parseRepairArray(String input){
		if(input.equals("null")) return null;
		
		String[] rep = input.split("#r");
		Repair[] repairs = null;
		
		if(rep != null)
			for(int i=0; i<rep.length; i++)
				if(!rep[i].equals(""))
					repairs = ArrayManager.addToArray(parseRepair(rep[i]), repairs);
		
		return repairs;
		
	}//End parseRepairArray()
	
	private Repair parseRepair(String input){
		if(input.equals("null")) return null;
		
		String[] assem = input.split("#x");
		String[] split = assem[0].split("#c|#d");
		String[] basics = split[0].split("&");
		
		int id = 0;
		try{
			id = Integer.parseInt(basics[1]);
		}catch(NumberFormatException e){
			System.err.println("Repair Parse Error!");
		}
		
		String trk;
		try{
			trk = parseString(basics[8]);
		}catch(ArrayIndexOutOfBoundsException e){
			trk = null;
		}
		
		return new Repair(
				parseString(basics[0]),
				id,
				Boolean.parseBoolean(basics[2]),
				parseString(basics[3]),
				parseString(basics[4]),
				parseString(basics[5]), //Email
				parseString(basics[6]),
				parseString(basics[7]),
				trk,
				parseCredit(split[1]),
				parseCredit(split[2]),
				parseCredit(split[3]),
				parseCredit(split[4]),
				parseCredit(split[5]),
				parseDate(split[6]), //Date Sold
				parseDate(split[7]), //Date Repaired
				parseDate(split[8]),
				parseAssembly(assem[1]));
	}
	
	private Assembly[] parseAssemblyArray(String input){
		if(input.equals("null")) return null;
		
		String[] assem = input.split("#x");
		Assembly[] assemblies = null;
		
		if(assem != null)
			for(int i=0; i<assem.length; i++)
				if(!assem[i].equals(""))
					assemblies = ArrayManager.addToArray(parseAssembly(assem[i]), assemblies);
		
		return assemblies;
		
	}//End parseAssemblyArray()
	
	private Assembly parseAssembly(String input){
		if(input.equals("null")) return null;

		//Gather Assemblies array
		int initial = -1, end = -1;
		for(int i=0, count = -1; i<input.length()-1; i++){	//For each char in string
			if(input.charAt(i) == '#' && input.charAt(i+1) == 'A'){ 
											     //If start of Assembly array is found
				if(initial == -1) initial = i+2; //If first #A found, set initial placement
				count++;						 //Increment depth counter
				
			}if(input.charAt(i) == ']' && input.charAt(i+1) == 'A'){
												//If end tag ']A' found
				if(count == 0)	//If depth counter is 0
					end = i;	//Set end placement
				else
					count--;	//Else decrement depth counter
			}
		}
		
		String first = input.substring(2, initial-2); 		   //Basic data
		String second = input.substring(initial, end); 		   //Internal Assembly array
		
		String third = input.substring(end+2, input.length()); //Part array
		
		String[] dateSplit = first.split("#d|#c");			//Contains dates & basic data
		String[] basicSplit = dateSplit[0].split("&");  //Separate basic data

		String[] assemblyStrings = null;				//For internal assemblies to later parse
		String[] partStrings = third.split("#p");       //Contains parts
		
		boolean outShell = true;
		initial = -1;
		for(int i=0, counter = -1; i<second.length()-1; i++){//For each char in String
			if(outShell){ 		                               //If outside internal Assemblies
				if(second.charAt(i) == '#' && second.charAt(i+1) == 'a'){
					 											 //If hit flag for Assembly start
					if(initial == -1) 					           //If start index is unassigned (-1)
						initial = i;						         //Assign index
					else{										   //Else, new assembly reached, package old and repeat
						assemblyStrings = ArrayManager.addToArray(second.substring(initial, i), assemblyStrings);
								//Package up Assembly
						initial = i;	//Assign new index
					}
				}else if(second.charAt(i) == '#' && second.charAt(i+1) == 'A'){
					 											 //If hit flag for Assembly Array start
					outShell = false;							   //Set inside internal Assembly
					counter++;									   //Increment depth counter
				}
			}else{									//Inside internal Assemblies
				if(second.charAt(i) == '#' && second.charAt(i+1) == 'A'){      //If Assembly array start
					counter++;											         //Increment depth counter
				}else if(second.charAt(i) == ']' && second.charAt(i+1) == 'A'){//If Assembly array end
					if(counter == 0) outShell = true;			//If at external Assemblies end met
					counter--;									//Decrement depth counter
				}
			}
		}//End for loop
		
		if(initial != -1)
			assemblyStrings = ArrayManager.addToArray(second.substring(initial), assemblyStrings);
				//Package last Assembly
		
		
		
		//Gather internal assemblies recursively
		Assembly[] assemblies = null;
		if(assemblyStrings != null)
			for(int i=0; i<assemblyStrings.length; i++){
				if(assemblyStrings[i].equals("null"))
					assemblies = ArrayManager.addToArray((Assembly) null , assemblies);
				else
					assemblies = ArrayManager.addToArray(parseAssembly(assemblyStrings[i]), assemblies);
			}
		
		//Gather parts
		Part[] parts = null;
		if(!third.equals("null"))
			if(partStrings != null)
				for(int i=1; i<partStrings.length; i++)
					if(partStrings[i].equals("null"))
						parts = ArrayManager.addToArray((Part) null, parts);
					else
						parts = ArrayManager.addToArray(parsePart(partStrings[i]), parts);
		
		//Return new Assembly
		return new Assembly(
				Integer.parseInt(basicSplit[0]),
				Integer.parseInt(basicSplit[1]),
				Integer.parseInt(basicSplit[2]),
				parseString(basicSplit[3]),
				parseString(basicSplit[4]),
				parseString(basicSplit[5]),
				parseString(basicSplit[6]),
				parseDate(dateSplit[1]),
				parseDate(dateSplit[2]),
				parseDate(dateSplit[3]),
				parseCredit(dateSplit[4]),
				parseCredit(dateSplit[5]),
				parseCredit(dateSplit[6]),
				parseCredit(dateSplit[7]),
				parseCredit(dateSplit[8]),
				parseCredit(dateSplit[9]),
				assemblies,
				parts);
		
	}//End parseAssembly()
	
	private Part parsePart(String input){
		if(input.equals("null")) return null;
		
		String[] s = input.split("#c|#d");
		String[] s1 = s[0].split("&");
		
		return new Part(
				Integer.parseInt(s1[0]),
				Integer.parseInt(s1[1]),
				Integer.parseInt(s1[2]),
				Integer.parseInt(s1[3]),
				parseString(s1[4]),
				parseString(s1[5]),
				parseString(s1[6]),
				parseString(s1[7]),
				parseCredit(s[1]),
				parseCredit(s[2]),
				parseCredit(s[3]),
				parseCredit(s[4]),
				parseCredit(s[5]),
				parseCredit(s[6]),
				parseCredit(s[7]),
				parseDate(s[8]),
				parseDate(s[9]),
				parseDate(s[10]));
		
	}//End parsePart()
	
	private Date parseDate(String input){
		if(input.equals("null")) return null;

		String[] s = input.split("&");
		int sec = 0, min = 0, hr = 0, 
			day = 0, mth = 0, yr = 0, dow = 0;
		try{
			sec = Integer.parseInt(s[0]);
			min = Integer.parseInt(s[1]);
			hr = Integer.parseInt(s[2]);
			day = Integer.parseInt(s[3]);
			mth = Integer.parseInt(s[4]);
			yr = Integer.parseInt(s[5]);
			dow = Integer.parseInt(s[6]);
		}catch(NumberFormatException e){
			e.printStackTrace();
			System.err.println("Date Parse Error!");
		}catch(NullPointerException e){
			System.err.println("Date Format Error!");
		}
		return new Date(sec, min, hr, day, mth, yr, dow);
		
	}//End Date()
	
	private Credit[] parseCreditArray(String input){
		if(input.equals("null")) return null;
		
		String[] cre = input.split("#c");
		Credit[] credits = null;
		
		if(cre != null)
			for(int i=0; i<cre.length; i++)
				if(!cre[i].equals(""))
					credits = ArrayManager.addToArray(parseCredit(cre[i]), credits);
		
		return credits;
	}
	
	private Credit parseCredit(String input){
		if(input.equals("null")) return null;
		
		int val = 0;
		try{ 
			val = Integer.parseInt(input); 
		}catch(NumberFormatException e){
			System.err.println("Credit Parse Error!");
		}
		return new Credit(val);
		
	}//End parseCredit()
	
	private String parseString(String input){
		if(input.equals("") || input.equals("null"))
			return null;
		else
			return input;
		
	}//End parseString()
	
	private String getLine(){
		String line = null;
		try { line = reader.readLine(); } catch (IOException e) {
			System.err.println("Reading Error!");
		}
		
		if(line.startsWith("-|") || line.equals(""))
			line = getLine();
		//else
			//line = cheapDecrypt(line, Database.getPassword());
		
		return line;
		
	} //End getLine()
	
	private String nextLine(){
		String line = null;
		try { line = reader.readLine(); } catch (IOException e) {
			System.err.println("Reading Error!");
		}
		
		if(line.startsWith("-|") || line.equals(""))
			line = nextLine();
		
		return line;
	}

	
	private static String cheapDecrypt(String input, String key){
		
		int[] shift = Writer.getShift(key);
		
		String toReturn = "";
		for(int i=0; i<input.length(); i++){
			int val = input.charAt(i) - shift[i % shift.length];
			
			if(val < 32)
				val = input.charAt(i) + 95 - shift[i % shift.length];
			
			if(input.charAt(i) == 10)
				val = 10;
			
			toReturn = toReturn.concat("" + (char) val);
		}
	
		return toReturn;
		
	}//End cheapDecrypt()
}
	
	
