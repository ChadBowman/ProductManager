package net.orthus.pm;


public class Item extends GenericItem{
	//----- Variables
	protected int id;
	protected String status, overallColor;
	protected Assembly assembly;
	
	//----- Constants
	public static final String NOT_LISTED = "Not Listed";
	public static final String AUCTION = "Auction";
	public static final String FIXED = "Fixed";
	
	//----- Constructors
	//FULL Constructor
	protected Item(int id, 
				   boolean discount,
				   String status, 
				   String overallColor, 
				   String note,
				   Credit sellAmount, 
				   Credit finalValueFee, 
				   Credit payPalFee, 
				   Credit shippingCost, 
				   Credit shippingPaid,
				   Date dateSold, 
				   Date dateReturned,
				   Assembly assembly) {
		
		super(discount, note, sellAmount, finalValueFee, payPalFee, shippingCost,
				shippingPaid, dateSold, dateReturned);
		this.id = id;
		this.status = status;
		this.overallColor = overallColor;
		this.assembly = assembly;
	}
	
	//----- Standard Methods
	//Getters
	public int getID(){ return id; }
	public String getStatus(){ return status; }
	public String getOverallColor(){ return overallColor; }
	public Assembly getAssembly(){ return assembly; }
	//Setters
	public void setStatus(String x){ status = x; }
	public void setOverallColor(String x){ overallColor = x; }
	public void setAssembly(Assembly x){ assembly = x; }
	
	//----- Advanced Methods
	public String record(){
		String fv = (finalValueFee == null)? "#cnull" : finalValueFee.record();
		String pp = (payPalFee == null)? "#cnull" : payPalFee.record();
		String sc = (shippingCost == null)? "#cnull" : shippingCost.record();
		String sp = (shippingPaid == null)? "#cnull" : shippingPaid.record();
		String sa = (sellAmount == null)? "#cnull" : sellAmount.record();
		String ds = (dateSold == null)? "#dnull" : dateSold.record();
		String dr = (dateReturned == null)? "#dnull" : dateReturned.record();
		String a = (assembly == null)? "#xnull" : "#x" + assembly.record();
		
		return String.format("#i%d&%b&%s&%s&%s%s%s%s%s%s%s%s%s",
				id, discount, status, overallColor, note, sa, fv, pp, sc, sp, ds, dr, a);
	}
}
