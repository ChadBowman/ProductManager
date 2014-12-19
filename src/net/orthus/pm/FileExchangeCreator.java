package net.orthus.pm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.IllegalFormatConversionException;

public class FileExchangeCreator {
	
	
	private BufferedWriter w;
	
	public FileExchangeCreator(){}
	
	
	//For use with Product Listings
	public void listProduct(boolean productInfo,
							String description,
							String conditionID,
							boolean format,
							String instructions,
							String picURLs,
							Product p){
		
		String ePID = "", 
			   storeCategory = null,
			   color = null;
			
		switch(p.getName()){
		case "DS Lite": 
			color = "Nintendo DS Lite ";
			switch(p.getOverallColor()){
			case "Cobalt": 
				ePID = "101862028"; 
				color = color + "Cobalt and Black Blue"; break;
			case "Red":
				ePID = "100252766";
				color = color + "Full Red RARE!"; break;
			case "Dragon":
				ePID = "100252766";
				color = color + "Crimson and Black Dragon Red RARE!"; break;
			case "Crimson": 
				ePID = "100252766"; 
				color = color + "Crimson and Black Red"; break;
			case "White": 
				ePID = "111823144"; 
				color = color + "White"; break;
			case "Gloss Black": 
				ePID = "101803118"; 
				color = color + "Onyx Black"; break;
			case "Silver": 
				ePID = "100234359"; 
				color = color + "Silver Gray Grey"; break;
			case "Pink": 
				ePID = "100289917"; 
				color = color + "Coral Pink"; break;
			case "Rose": 
				ePID = "110634996"; 
				color = color + "Metallic Rose Pink"; break;
			case "Pikachu": 
				ePID = "110577804"; 
				color = color + "Pikachu Pokemon Yellow RARE!"; break;
			case "Dialga and Palkia": 
				ePID = "111915093"; 
				color = color + "Dialga & Palkia Pokemon Black RARE!"; break;
			case "Zelda": 
				ePID = "100273740"; 
				color = color + "Legend of Zelda Gold RARE!"; break;
			case "Mario": 
				ePID = "108266780"; 
				color = color + "Mario Red RARE!"; break;
			default: System.err.println("No category number for this color: " + p.getOverallColor());
			}
	
			storeCategory = "4845392016"; //DSL Products
			break;
		
		case "DSi":
			color = "Nintendo DSi ";
			switch(p.getOverallColor()){
			case "White": 
				color = color + "White";
				ePID = "100234299"; break;
			case "Black": 
				color = color + "Black";
				ePID = "100289687"; break;
			case "Blue": 
				color = color + "Light Blue";
				ePID = "108220849"; break;
			case "Pink": 
				color = color + "Pink";
				ePID = "100299168"; break;
			default: System.err.println("No category number for this color: " + p.getOverallColor());
			}
			
			storeCategory = "4845390016"; //DSi Products
			break;
		
		case "DSi XL":
			color = "Nintendo DSi XL ";
			switch(p.getOverallColor()){
			case "Burgundy": 
				color = color + "Burgundy Crimson Red";
				ePID = "100190014"; break;
			case "Blue": 
				color = color + "Midnight Blue";
				ePID = "102669315"; break;
			case "Black": 
				color = color + "Bronze Black";
				ePID = "101758104"; break;
			case "Mario": 
				color = color + "Mario Red RARE!";
				ePID = "102661081"; break;
			case "Yellow": 
				color = color + "Yellow & Black VERY RARE!";
				ePID = "101863418"; break;
			default: System.err.println("No category number for this color: " + p.getOverallColor());
			}
			
			storeCategory = "4845388016"; //DSi XL Products
			break;
			
		case "3DS":
			color = "Nintendo 3DS ";
			switch(p.getOverallColor()){
			case "Red": 
				color = color + "Flame Red";
				ePID = "110615602"; break;
			case "Blue": 
				color = color + "Aqua Blue";
				ePID = "101798790"; break;
			case "Black": 
				color = color + "Cosmo Black";
				ePID = "101755559"; break;
			case "Pink": 
				color = color + "Pearl Pink";
				ePID = "112393391"; break;
			case "White": 
				color = color + "Ice White RARE! US/NA Motherboard!";
				ePID = "112454530"; break;
				
			case "Purple":
				color = color + "Midnight Purple";
				ePID = "114443988"; break;
				
			default: System.err.println("No category number for this color: " + p.getOverallColor());
			}
			
			storeCategory = "4845384016"; //3DS Products
			break;
			
		case "3DS XL":
			color = "Nintendo 3DS XL ";
			switch(p.getOverallColor()){
			case "Blue": 
				color = color + "Blue & Black";
				ePID = "141153316"; break;
			case "Red": 
				color = color + "Red & Black";
				ePID = "116002572"; break;
			case "Pink": 
				color = color + "Pink & White";
				ePID = "131662626"; break;
			case "Mario":
				color = color + "Mario Red and Black RARE! US Motherboard!";
				ePID = "116002572"; break;
			case "Luigi":
				color = color + "Mario & Luigi Dream Time Silver RARE!";
				ePID="176271438"; break;
			default: System.err.println("No category number for this color: " + p.getOverallColor());
			}
			
			storeCategory = "4845380016"; //3DS XL Products
			break;
		}
		
		color = color.concat("Trade-ins Welcome! ");
		if(p.getNote() != null)
			if(p.getNote().contains("4.") 
					|| p.getNote().contains("3.")
					|| p.getNote().contains("2.")
					|| p.getNote().contains("1."))
				color = color.concat(p.getNote() + " Firmware!");
			else
				color = color.concat("FAST Shipping!");
		
		try {
			writeListing(ePID,
						false,
					 	color,
					 	description,
					 	p.getOverallQuality(),
					 	conditionID,
					 	"1",
					 	format,
					 	"" + p.getListAmount().getValueInDollars(),
					 	false,
					 	true,
					 	"" + p.getListDuration(),
					 	instructions,
					 	storeCategory,
					 	"",
					 	"14 Days",
					 	"10",
					 	p.getFullName(),
					 	"FR Envelope",
					 	"0",
					 	"FR Box",
					 	"1.00",
					 	"Express",
					 	"10.00",
					 	picURLs);
			
		} catch (IOException e) {
			System.err.println("Writing CSV failed!");
			e.printStackTrace();
		}
		
	}
	
	public void listPart(String title,
						 String description,
						 String conditionID,
						 boolean format,
						 boolean bestOffer,
						 boolean autoRelist,
						 String instructions,
						 String refunds,
						 String weight,
						 String ship1Option,
						 String ship1Cost,
						 String ship2Option,
						 String ship2Cost,
						 String ship3Option,
						 String ship3Cost,
						 String picURLs,
			             boolean accessory,
			             Constituent c){
		
		String category, storeCat;
		
		if(accessory){
			category = "54968"; //Accessories
			
			switch(Database.getFocusedCategory().getName()){
			case ProductCategory.DS_LITE:
				storeCat = "4845466016"; break;//DSL Accessories
				
			case ProductCategory.DSI:
				storeCat = "4845465016"; break; //DSi Accessories
				
			case ProductCategory.DSI_XL:
				storeCat = "4845459016"; break; //DSi XL Accessories
				
			case ProductCategory.DS3:
				storeCat = "4845458016"; break; //3DS Accessories
				
			case ProductCategory.DS3_XL:
				storeCat = "4845457016"; break; //3DS XL Accessories
				
			default:
				storeCat = "1"; //Other
			}
		}else{
			category = "171833"; //ReplacementParts & tools
			switch(Database.getFocusedCategory().getName()){
			case ProductCategory.DS_LITE:
				storeCat = "4845393016"; break;//DSL Parts
				
			case ProductCategory.DSI:
				storeCat = "4845391016"; break; //DSi Parts
				
			case ProductCategory.DSI_XL:
				storeCat = "4845389016"; break; //DSi XL Parts
				
			case ProductCategory.DS3:
				storeCat = "4845385016"; break; //3DS Parts
				
			case ProductCategory.DS3_XL:
				storeCat = "4845381016"; break; //3DS XL Parts
				
			default:
				storeCat = "1"; //Other
			}
		}
		
		String q = null;
		try{
			q = ((Part) c).getQuality();
		}catch(ClassCastException e){
			q = "A+";
		}
		
		
		try {
			writeListing(category,
						 false,
						 title,
						 description,
						 q,
						 conditionID,
						 "" + c.getQuantity(),
						 format,
						 "" + c.getListPrice().getValueInDollars(),
						 bestOffer,
						 autoRelist,
						 "" + c.getDuration(),
						 instructions,
						 storeCat,
						 "",
						 refunds,
						 weight,
						 c.getName() + " " + c.getSerial(),
						 ship1Option,
						 ship1Cost,
						 ship2Option,
						 ship2Cost,
						 ship3Option,
						 ship3Cost,
						 picURLs);
			
		} catch (IOException e) {
			System.err.println("Writing CSV failed!");
			e.printStackTrace();
		}
		
	}
	
	public void writeListing(String epid,
							  boolean productInfo,
						   	  String title,
							  String description,
							  String conditionAlph,
							  String conditionID,
							  String quantity,
							  boolean format,
							  String price,
							  boolean bestOffer,
							  boolean autoRelist,
							  String duration,
							  String instructions,
							  String storeCat1,
							  String storeCat2,
							  String refunds,
							  String weight,
							  String label,
							  String ship1Option,
							  String ship1Cost,
							  String ship2Option,
							  String ship2Cost,
							  String ship3Option,
							  String ship3Cost,
							  String picURLs) throws IOException{
		
		
		String datestamp = String.format("%2d%2d%2d.csv", 
				Date.getTodaysDate().getDay(), 
				Date.getTodaysDate().getMonth(), 
				Date.getTodaysDate().getYear());
		
		datestamp = datestamp.replace(" ", "0");
		
		File file = new File("C:/Users/Jack Harper/Documents/Orthus/List/" + datestamp);
		
		if(!file.exists()){
			w = new BufferedWriter(new FileWriter(file));
			
			// HEADER //
			w.write("*Action(SiteID=US|Country=US|Currency=USD|Version=745),");
			w.append("Product:EPID,");
			w.append("*Category,");
			w.append("Product:IncludePreFilledItemInformation,");
			w.append("Title,");
			w.append("Description,");
			w.append("*ConditionID,");
			w.append("ConditionDescription,");
			w.append("PicURL,");
			w.append("*Quantity,");
			w.append("*Format,");
			w.append("*StartPrice,");
			w.append("BestOfferEnabled,");
			w.append("*Duration,");
			w.append("*Location=59715,");
			w.append("PayPalAccepted=1,");
			w.append("PayPalEmailAddress=sales@orthus.net,");
			w.append("ImmediatePayRequired=1,");
			w.append("PaymentInstructions,");
			w.append("StoreCategory,");
			w.append("StoreCategory2,");
			w.append("ShippingType=Flat,");
			w.append("ShippingService-1:Option,");
			w.append("ShippingService-1:Cost,");
			w.append("ShippingService-1:AdditionalCost,");
			w.append("ShippingService-1:Priority=1,");
			w.append("ShippingService-2:Option,");
			w.append("ShippingService-2:Cost,");
			w.append("ShippingService-2:AdditionalCost,");
			w.append("ShippingService-2:Priority=2,");
			w.append("ShippingService-3:Option,");
			w.append("ShippingService-3:Cost,");
			w.append("ShippingService-3:AdditionalCost,");
			w.append("ShippingService-3:Priority=3,");
			w.append("DispatchTimeMax=1,");
			w.append("ReturnsAcceptedOption,");
			w.append("RefundOption,");
			w.append("ReturnsWithinOption,");
			w.append("ShippingCostPaidByOption,");
			w.append("GlobalShipping=1,");
			w.append("WeightMinor,");
			w.append("CustomLabel\n");
		}else{
			w = new BufferedWriter(new FileWriter(file, true));
			w.append("\n");
		}
		
		
		//// BEGIN APPENDING CSV FILE ////
		
		//Action
		w.append("Add,");
		
		//Catalog or Category
		if(epid.length() == 9)
			w.append(epid + ",,");  //Product Catalog ID
		else
			w.append("," + epid + ",");

		
		//Pre-filled Product Info
		if(productInfo)
			w.append("1,");
		else
			w.append("0,");
		
		
		
		//Title
		if(title.length() > 80)
			title = title.substring(0, 79);
		
		w.append("\"" + title + "\",");
		
		//Description
		String fmt = "<a class=\"\"thumbnail\"\" href=\"\"%s\"\"><img src=\"\"%s\"\" width=\"\"100px\"\" height=\"\"75px\"\"" + 
				"border=\"\"0\"\" /><span><img src=\"\"%s\"\" width=\"\"400\"\" height=\"\"300\"\"/></span></a>";
		
		String gallery = ""; //HTML for image gallery
		String[] urls = null;
		if(picURLs != null){
			urls = picURLs.split(",");
			for(int i=0; i<urls.length; i++){
				if(i % 3 == 0 && i != 0) //Every three photos
					gallery = gallery.concat("<br>"); //Add in a break
				gallery = gallery.concat(String.format(fmt, urls[i], urls[i], urls[i]));
			}
		}
		
		
		//Shipping section
		String shiphtml = "USPS " + ship1Option.replace("FR", "Priority Flat Rate") + ": ";
		
		if(new Credit(ship1Cost).getValueInCents() == 0)
			shiphtml = shiphtml.concat("FREE!<br>");
		else
			shiphtml = shiphtml.concat(new Credit(ship1Cost).toString() + "<br>");
		
		if(!ship2Option.equals("None"))
			if(new Credit(ship2Cost).getValueInCents() == 0)
				shiphtml = shiphtml.concat("USPS " + ship2Option.replace("FR", "Priority Flat Rate") + ": FREE!<br>");
			else
				shiphtml = shiphtml.concat("USPS " + ship2Option.replace("FR", "Priority Flat Rate") + ": " + new Credit(ship2Cost).toString() + "<br>");
		
		if(!ship3Option.equals("None"))
			if(new Credit(ship3Cost).getValueInCents() == 0)
				shiphtml = shiphtml.concat("USPS " + ship3Option.replace("FR", "Priority Flat Rate") + ": FREE!<br>");
			else
				shiphtml = shiphtml.concat("USPS " + ship3Option.replace("FR", "Priority Flat Rate") + ": " + new Credit(ship3Cost).toString() + "<br>");
		
		String des = "";
		try{
			
			des = String.format(Database.getProductDescriptionTemplate(),
					gallery, conditionAlph, description, shiphtml, label);
			
		}catch(IllegalFormatConversionException e){ //If user-modified format creates problem
			e.printStackTrace();
			OptionPane.showError("Make sure description tempalte has five %s for data insertion!", "Format Error!");
			
		}
					
		w.append(des + ",");
		
		//ConditionID
		switch(conditionID){
		case "New": w.append("1000,"); break;
		case "New With Defects": w.append("1750,"); break;
		case "Seller Refirbished": w.append("2500,"); break;
		case "Good": w.append("5000,"); break;
		case "Used": w.append("3000,"); break;
		case "Acceptable": w.append("6000,"); break;
		case "Not Working": w.append("7000,"); break;
		}
			
		
		//Condition Description
		w.append("\"" + description + "\",");
		
		//PicURLS
		if(picURLs != null)
			w.append(urls[0] + ",");
		else
			w.append(",");
		
		//Quantity
		w.append("" + quantity + ",");
		 
		//Format
		if(format)
			w.append("FixedPrice,");
		else
			w.append("Auction,");
		
		//Start Price
		w.append(price + ",");
	
		
		//Best Offer Enabled
		if(bestOffer)
			w.append("1,");
		else
			w.append("0,");
		
		//Duration
		if(autoRelist)
			w.append("GTC,");
		else
			w.append(duration + ",");
		
		//Location and Paypal info
		w.append(",,,,"); //Locked in
		
		//Payment Instructions
		w.append("\"" + instructions + "\",");
		
		//Store Category
		w.append(storeCat1 + ",");
		
		//Store Category2
		w.append(storeCat2 + ",");
		
		//Shipping Type
		w.append(","); //Locked in
		
		//Shipping Option 1
		switch(ship1Option){
		case "First Class": w.append("USPSFirstClass," + ship1Cost + ",,,"); break;
		case "FR Envelope": w.append("USPSPriorityFlatRateEnvelope," + ship1Cost + ",,,"); break;
		case "FR Box": w.append("USPSPriorityMailSmallFlatRateBox," + ship1Cost + ",,,"); break;
		case "Express": w.append("USPSExpressFlatRateEnvelope," + ship1Cost + ",,,"); break;
		case "None": w.append(",,,,");
		}
		
		//Shipping Option 2
		switch(ship2Option){
		case "First Class": w.append("USPSFirstClass," + ship2Cost + ",,,"); break;
		case "FR Envelope": w.append("USPSPriorityFlatRateEnvelope," + ship2Cost + ",,,"); break;
		case "FR Box": w.append("USPSPriorityMailSmallFlatRateBox," + ship2Cost + ",,,"); break;
		case "Express": w.append("USPSExpressFlatRateEnvelope," + ship2Cost + ",,,"); break;
		case "None": w.append(",,,,");
		}
		
		
		//Shipping Option 3
		switch(ship3Option){
		case "First Class": w.append("USPSFirstClass," + ship3Cost + ",,,"); break;
		case "FR Envelope": w.append("USPSPriorityFlatRateEnvelope," + ship3Cost + ",,,"); break;
		case "FR Box": w.append("USPSPriorityMailSmallFlatRateBox," + ship3Cost + ",,,"); break;
		case "Express": w.append("USPSExpressFlatRateEnvelope," + ship3Cost + ",,,"); break;
		case "None": w.append(",,,,");
		}
		
		
		//Dispatch Time max
		w.append(","); //Locked in
		
		//Return Policy and Global Shipping
		switch(refunds){
		case "No Returns": w.append("ReturnsNotAccepted,,,,,"); break;
		case "14 Days": w.append("ReturnsAccepted,MoneyBack,Days_14,Buyer,,"); break;
		case "30 Days": w.append("ReturnsAccepted,MoneyBack,Days_30,Buyer,,"); break;
		}
		
		//Weight Minor 
		w.append(weight + ",");
		
		//Custom Label
		w.append(label);
	
		
		w.flush();
	}
	
	

}
