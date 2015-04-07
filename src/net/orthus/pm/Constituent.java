package net.orthus.pm;


public class Constituent{
	//----- Variables
	protected int serial, quantity, duration, numPerListing;
	protected String name, status, color;
	protected Date datePurchased, dateListed, dateSold;
	protected Credit listPrice, listFee, finalValueFee, payPalFee, shipCost, shipPaid;
	protected Credit marketValue;
	
	//----- Constants
	public static final String NO_COLOR = "NO COLOR";
	
	//Location constants
	public static final String INCOMING = "Incoming";  //Is ordered and enroute
	public static final String SHIPPING_INCOMING = "Shipping Incoming";
	public static final String SUPPLY = "Supply";	   //Is in supply (inventory)
	public static final String SHIPPING_SUPPLY = "Shipping Supply";
	public static final String HOLD = "Hold"; 		   //Is on hold for repair
	public static final String LISTED_AUCTION = "Listed (Auction)";
	public static final String LISTED_FIXED = "Listed (Fixed)";
	public static final String LISTED_FIXED_AUTO = "Listed (Auto Fixed)";
	public static final String SOLD = "Sold";
	public static final String RETURNED = "Returned";
	
	public static final String PRODUCT = "Product";	   //Part/Assembly is being used in a product
	public static final String REPAIR = "Repair"; 	   //Part/Assembly is being used in an assembly
	public static final String TEMPATE = "Template";   //Used in a template assembly
	
	//----- Constructor
	protected Constituent(int serial,
						  int quantity,
						  int duration,
						  String name, 
						  String status,
						  String color,
						  Date datePurchased,
						  Date dateListed,
						  Date dateSold,
						  Credit listPrice,
						  Credit listFee){
		
		this.serial = serial;
		this.quantity = quantity;
		this.duration = duration;
		this.name = name;
		this.status = status;
		this.datePurchased = datePurchased;
		this.dateSold = dateSold;
		this.color = color;
		this.dateListed = dateListed;
		this.listPrice = listPrice;
		this.listFee = listFee;
	}
	
	
	//Full Constructor
	protected Constituent(int serial,
						  int quantity,
						  int duration,
						  String name, 
						  String status,
						  String color,
						  Date datePurchased,
						  Date dateListed,
						  Date dateSold,
						  Credit listPrice,
						  Credit listFee,
						  Credit finalValueFee,
						  Credit payPalFee,
						  Credit shipCost,
						  Credit shipPaid){
		
		this.serial = serial;
		this.quantity = quantity;
		this.duration = duration;
		this.name = name;
		this.status = status;
		this.datePurchased = datePurchased;
		this.dateSold = dateSold;
		this.color = color;
		this.dateListed = dateListed;
		this.listPrice = listPrice;
		this.listFee = listFee;
		this.finalValueFee = finalValueFee;
		this.payPalFee = payPalFee;
		this.shipCost = shipCost;
		this.shipPaid = shipPaid;
	}
	
	//Copy
	public Constituent(Constituent c){
		try{this.serial = new Integer(c.getSerial());}catch(NullPointerException e){}
		try{this.quantity = new Integer(c.getQuantity());}catch(NullPointerException e){}
		try{this.duration = new Integer(c.getDuration());}catch(NullPointerException e){}
		try{this.name = new String(c.getName());}catch(NullPointerException e){}
		try{this.status = new String(c.getStatus());}catch(NullPointerException e){}
		    this.datePurchased = new Date(c.getDatePurchased());
		    this.dateSold = new Date(c.getDateSold());
		    this.dateListed = new Date(c.getDateListed());
		try{this.color = new String(c.getColor());}catch(NullPointerException e){}
		    this.listPrice = new Credit(c.getListPrice());
		    this.listFee = new Credit(c.getListFee());
		    this.finalValueFee = new Credit(c.getFinalValueFee());
		    this.payPalFee = new Credit(c.getPayPalFee());
		    this.shipCost = new Credit(c.getShippingCost());
		    this.shipPaid = new Credit(c.getShippingPaid());
	}
	
	//----- Standard Methods
	//Getters
	public int getSerial(){ return serial; }
	public int getQuantity(){ return quantity; }
	public int getDuration(){ return duration; }
	public int getNumPerListing(){ 
		
		int ret = (numPerListing == 0)? 1 : numPerListing;
		return ret; 
	}
	
	public String getName(){ return name; }
	public String getStatus(){ return status; }
	public String getColor(){ return color; }
	public Date getDatePurchased(){ return datePurchased; }
	public Date getDateSold(){ return dateSold; }
	public Date getDateListed(){ return dateListed; }
	public Credit getListPrice() { return listPrice; }
	public Credit getListFee(){ return listFee; }
	public Credit getFinalValueFee(){ return finalValueFee; }
	public Credit getPayPalFee(){ return payPalFee; }
	public Credit getShippingCost(){ return shipCost; }
	public Credit getShippingPaid(){ return shipPaid; }
	public Credit getMarketValue(){ return marketValue; }
	
	//Setters
	public void setSerial(int x){ serial = x; } 
	public void setQuantity(int x) { quantity = x; }
	public void setDuration(int x){ duration = x; }
	public void setNumPerListing(int x){ numPerListing = x; }
	public void setName(String x){ name = x; }
	public void setStatus(String x){ status = x; }
	public void setColor(String x){ color = x; }
	public void setDatePurchased(Date x){ datePurchased = x; }
	public void setDateSold(Date x){ dateSold = x; }
	public void setDateListed(Date x){ dateListed = x; }
	public void setListPrice(Credit x){ listPrice = x; }
	public void setListFee(Credit x){ listFee = x; }
	public void setFinalValueFee(Credit x){ finalValueFee = x; }
	public void setPayPalFee(Credit x){ payPalFee = x; }
	public void setShippingCost(Credit x){ shipCost = x; }
	public void setShippingPaid(Credit x){ shipPaid = x; }
	public void setMarketValue(Credit x){ marketValue = x; }
	
	//----- Advanced Methods
	public String record(){
		String d = (datePurchased == null)? "#dnull" : datePurchased.record();
		String d2 = (dateListed == null)? "#dnull" : dateListed.record();
		String d3 = (dateSold == null)? "#dnull" : dateSold.record();
		String c = (listPrice == null)? "#cnull" : listPrice.record();
		String c2 = (listFee == null)? "#cnull" : listFee.record();
		String c3 = (finalValueFee == null)? "#cnull" : finalValueFee.record();
		String c4 = (payPalFee == null)? "#cnull" : payPalFee.record();
		String c5 = (shipCost == null)? "#cnull" : shipCost.record();
		String c6 = (shipPaid == null)? "#cnull" : shipPaid.record();
		
		return String.format("#n%d&%d&%d&%s&%s&%s%s%s%s%s%s%s%s%s%s", 
				serial, quantity, duration, name, status, color, d, d2, d3, c, c2, c3, c4, c5, c6); 
	}
	
	/**
	 * 
	 * @return	Quantity/number per listing
	 */
	public int getEffectiveQuantity(){
		numPerListing = (numPerListing == 0)? 1: numPerListing;
		
		if(quantity == 1)
			return 1;
		
		return quantity / numPerListing; 
	}
	
	public Credit getRecommendedSellAmount(){
		
		Credit toRet = new Credit();
		Credit cost;
		Credit amt;
		
		do{
			toRet.add(1); //Add one cent to revenue
			
			cost = new Credit(getUnitCost());
			
			cost.add(2.5); //Ship cost with 0.30 PPF
			
			if(Database.isTopRatedSeller())
				cost.add(0.072 * toRet.getValueInDollars()); //0.09 rate with TRS discount
			else
				cost.add(0.09 * toRet.getValueInDollars());
			
			cost.add(Database.getPayPalFeeRate() 	//Add PPF
					* toRet.getValueInDollars());
			
			amt = new Credit(toRet.getValueInCents() - cost.getValueInCents());
			
		}while(amt.getValueInCents() < 0);
		
		
		return toRet;
		
	}
	
	public boolean equals(Constituent compareTo){
		if(this.serial == compareTo.getSerial())
			return true;
		return false;
	}
	
	public Credit getProfit(){
		if(!status.equals(Part.SOLD)) return null;
		
		return new Credit(
				listPrice.getValueInDollars()
				- getUltimateCost().getValueInDollars());
	}
	
	//Phantom Method
	public Credit calculateValue(){
		
		System.err.println("Tried to use calculateValue() in Constituent");
		return new Credit();
	}
	
	//Phantom Method
	public Credit getUltimateCost(){
		System.err.println("Tried to use getUltimateCost() in Constituent");
		return new Credit();
	}
	
	public Credit getTotalCost(){
		System.err.println("Tried to use getTotalCost() in Constituent");
		return new Credit();
	}
	
	public Credit getUnitCost(){
		return new Credit(getTotalCost().getValueInDollars() / getQuantity());
	}
	
	public String getData(){

		return name;
	}
	
	private boolean checkColors(Constituent other){
		
		if(color == null && other.getColor() == null)
			return true;
		
		if(color == null || other.getColor() == null)
			return false;
		
		if(color.equals(other.getColor()))
			return true;
		
		return false;
	}
	
	private boolean checkStatus(Constituent other){
		
		if(status == null && other.getStatus() == null)
			return true;
		
		if(status == null || other.getStatus() == null)
			return false;
		
		if(status.equals(other.getStatus()))
			return true;
		
		return false;
	}
	
	/**
	 * Checks serial, color, and status.
	 * 
	 * @param a		Part to compare against.
	 * @return		True if both parts have the same serial, color, and status. Else false.
	 */
	public boolean equivalent(Constituent a){
	
		if(serial == a.getSerial() && checkColors(a) && checkStatus(a))
			return true;
		
		return false;
	}

}
