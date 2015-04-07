package net.orthus.pm;



public class Part extends Constituent 
				  implements Comparable<Part>{
	//----- Variables
	private String quality;
	private Credit price;
	
	//----- Constants
	//Placeholders for permanently null fields
	public static final String NO_QUALITY = "NO QUALITY";
	//Quality constants
	public static final String A_PLUS = "A+", A = "A", A_MINUS = "A-";
	public static final String B_PLUS = "B+", B = "B", B_MINUS = "B-";
	public static final String C_PLUS = "C+", C = "C", C_MINUS = "C-";
	public static final String D_PLUS = "D+", D = "D", D_MINUS = "D-";
	public static final String F ="F";
	
	//----- Constructors
	public Part(){
		super(0, 1, 0, null, null, null, null, null, null, null, null);
		this.price = new Credit();
	}
	
	//FULL Constructor
	public Part(int serial,
			int quantity,
			int duration,
			int numPerListing,
			String name, 
			String status,
			String color,
			String quality,
			Credit price,
			Credit listPrice,
			Credit listFee,
			Credit finalValueFee,
			Credit payPalFee,
			Credit shipCost,
			Credit shipPaid,
			Date datePurchased,
			Date dateListed,
			Date dateSold){
	
	super(serial, quantity, duration, name, status, color, datePurchased, 
			dateListed, dateSold, listPrice, listFee, finalValueFee, payPalFee, shipCost, shipPaid);
	
	this.price = price;
	this.quality = quality;
	this.numPerListing = (numPerListing == 0)? 1 : numPerListing;
}
	
	public Part(int serial,
			int quantity,
			int duration,
			String name, 
			String status,
			String color,
			String quality,
			Credit price,
			Credit listPrice,
			Credit listFee,
			Credit finalValueFee,
			Credit payPalFee,
			Credit shipCost,
			Credit shipPaid,
			Date datePurchased,
			Date dateListed,
			Date dateSold){
	
	super(serial, quantity, duration, name, status, color, datePurchased, 
			dateListed, dateSold, listPrice, listFee, finalValueFee, payPalFee, shipCost, shipPaid);
	
	this.price = price;
	this.quality = quality;
}
	
	public Part(int serial,
				int quantity,
				int duration,
				String name, 
				String status,
				String quality,
				String color,
				Credit price,
				Credit listPrice,
				Credit listFee,
				Date datePurchased,
				Date dateListed,
				Date dateSold){
		
		super(serial, quantity, duration, name, status, color, 
				datePurchased, dateListed, dateSold, listPrice, listFee);
		
		this.price = price;
		this.quality = quality;
	}
	
	//Clone
	public Part(Part copy){
		super(copy.getSerial(), 0, new Integer(copy.getQuantity()), null, null, null, null, null, null, null, null);
		try{this.name = new String(copy.getName());}catch(NullPointerException e){}
		try{this.status = new String(copy.getStatus());}catch(NullPointerException e){}
		try{this.duration = new Integer(copy.getDuration());}catch(NullPointerException e){}
		try{this.numPerListing = new Integer(copy.getNumPerListing());}catch(NullPointerException e){}
			this.datePurchased = new Date(copy.getDatePurchased());
			this.price = new Credit(copy.getPrice());
		try{this.quantity = new Integer(copy.getQuantity());}catch(NullPointerException e){}
		try{this.quality = new String(copy.getQuality());}catch(NullPointerException e){}
		try{this.color = new String(copy.getColor());}catch(NullPointerException e){}
			this.dateListed = new Date(copy.getDateListed());
			this.dateSold = new Date(copy.getDateSold());
			this.listPrice = new Credit(copy.getListPrice());
			this.listFee = new Credit(copy.getListFee());	
	}
	
	//----- Standard Methods
	//Getters
	public Credit getPrice(){ return price; }
	public Credit getTotalCost(){ return new Credit(price.getValueInDollars() * quantity); }
	public String getQuality(){ return quality; }
	
	//Setters
	public void setPrice(Credit x){ price = x; }
	public void setPrice(double x){ price.setValue(x); }
	public void setQuality(String x) { quality = x; }
	
	//----- Advanced Methods
	
	public Credit getUltimateCost(){
		if(!status.equals(Constituent.SOLD)) return null;
		
		return new Credit(
				(getTotalCost().getValueInDollars() * getNumPerListing())
				+ finalValueFee.getValueInDollars()
				+ payPalFee.getValueInDollars()
				+ shipCost.getValueInDollars()
				- shipPaid.getValueInDollars());
	}
	
	public String record(){
		String prc = (price == null)? "#cnull" : price.record();
		String dp = (datePurchased == null)? "#dnull" : datePurchased.record();
		String d2 = (dateListed == null)? "#dnull" : dateListed.record();
		String d3 = (dateSold == null)? "#dnull" : dateSold.record();
		String c = (listPrice == null)? "#cnull" : listPrice.record();
		String c2 = (listFee == null)? "#cnull" : listFee.record();
		String c3 = (finalValueFee == null)? "#cnull" : finalValueFee.record();
		String c4 = (payPalFee == null)? "#cnull" : payPalFee.record();
		String c5 = (shipCost == null)? "#cnull" : shipCost.record();
		String c6 = (shipPaid == null)? "#cnull" : shipPaid.record();
		
		return String.format("#p%d&%d&%d&%d&%s&%s&%s&%s%s%s%s%s%s%s%s%s%s%s", 
				serial, quantity, duration, numPerListing, name, status, color, quality, prc, c, c2, c3, c4, c5, c6, dp, d2, d3);
	}
	
	public String getData(){
		String ret = "";
		
		if(color != null)
			if(!color.equals(Part.NO_COLOR))
				ret = ret.concat("  Color: " + color + "\n");
		
		if(quality != null)
			if(!quality.equals(Part.NO_QUALITY))
				ret = ret.concat("  Quality: " + quality + "\n");
		
		ret = ret.concat("  Unit Cost: " + price.toString() + "\n");
		
		if(quantity > 1)
			ret = ret.concat("  Quantity: " + quantity + "\n");
		
		ret = ret.concat("\n  Status: " + status + "\n");
		
		if(status.equals(Part.INCOMING) 
				|| status.equals(Part.SUPPLY)
				|| status.equals(Part.SHIPPING_INCOMING)
				|| status.equals(Part.SHIPPING_SUPPLY))
			ret = ret.concat("  Date Purchased: " + datePurchased.displaySimpleDate() + "\n");
		
		if(status.contains("Listed")){
			ret = ret.concat("  Date Listed: " + dateListed.displaySimpleDate() + "\n");
			ret = ret.concat("  List Amount: " + listPrice.toString() + "\n");
			ret = ret.concat("  Listing Fee: " + listFee.toString() + "\n");
			int rem = dateListed.getDateInDays() + duration - Date.getTodaysDate().getDateInDays();
			ret = ret.concat("  Days Remaining: " + rem + "\n");
		}
		
		if(status.equals(Part.SOLD)){
			String st = String.format(" (%.0f%%)%n", 100.0 * getProfit().getValueInDollars()
					/ getUltimateCost().getValueInDollars());
			ret = ret.concat("  Profit: " + getProfit().toString() + st);
			ret = ret.concat("  Date Sold: " + dateSold.displaySimpleDate() + "\n");
			ret = ret.concat("  Sell Amount: " + listPrice.toString() + "\n");
			ret = ret.concat("  Final Value Fee: " + finalValueFee.toString() + "\n");
			ret = ret.concat("  PayPal Fee: " + payPalFee.toString() + "\n");
			ret = ret.concat("  Shipping Paid: " + shipPaid.toString() + "\n");
			ret = ret.concat("  Shipping Cost: " + shipCost.toString() + "\n");
		}
		
		ret = ret.concat("\n " + status);
		
		return ret;
	}
	
	
	//Static
	public static Part[] cloneArray(Part[] array){
		if(array == null) return null;
		Part[] toReturn = new Part[array.length];
		for(int i=0; i<array.length; i++)
			toReturn[i] = new Part(array[i]);
		return toReturn;
	}
	
	public static String[] getQualityArray(){
		String[] ret = {A_PLUS, A, A_MINUS,
				B_PLUS, B, B_MINUS,
				C_PLUS, C, C_MINUS,
				D_PLUS, D, D_MINUS,
				F};
		
		return ret;
	}
	
	
	//----- Interfaces
	public boolean equiv(Part o){
	
		if(!this.equivalent(o))
			return false;
		
		if(quality == null && o.getQuality() == null)
			return true;
		
		if(quality == null || o.getQuality() == null)
			return false;
		
		if(quality.equals(o.getQuality()))
			return true;
		
		return false;
	}
	
	public int compareTo(Part anotherPart){ 
		return price.compareTo(anotherPart.getPrice()); 
	}
	
	public String toString(){ 
		if(quantity == 1) return name;
		return name + " (" + quantity + ")"; 
	}

}
