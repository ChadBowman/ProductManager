package net.orthus.pm;


public class GenericItem {
	//----- Variables
	//For use with subclasses
	protected boolean discount;
	protected String note;
	protected Credit sellAmount, finalValueFee, payPalFee, shippingCost, shippingPaid;
	protected Date dateSold, dateReturned;
	//For only this class
	private double rate;
	private Credit purchaseAmount, listAmount;
	private String name;

	//----- Constructors
	protected GenericItem(
		  boolean discount,
		  String note,
		  Credit sellAmount,
		  Credit finalValueFee,
		  Credit payPalFee,
		  Credit shippingCost,
		  Credit shippingPaid,
		  Date dateSold,
		  Date dateReturned) {
		
		this.discount = discount;
		this.note = note;
		this.sellAmount = sellAmount;
		this.finalValueFee = finalValueFee;
		this.payPalFee = payPalFee;
		this.shippingCost = shippingCost;
		this.shippingPaid = shippingPaid;
		this.dateSold = dateSold;
		this.dateReturned = dateReturned;
	}
	//FULL Constructor
	public GenericItem(double rate,
					   boolean discount,
					   String name,
					   String note,
					   Credit purchaseAmount,
					   Credit listAmount,
					   Credit sellAmount,
					   Credit finalValueFee,
					   Credit payPalFee,
					   Credit shippingCost,
					   Credit shippingPaid,
					   Date dateSold,
					   Date dateReturned){
		
		this.rate = rate;
		this.discount = discount;
		this.name = name;
		this.note = note;
		this.purchaseAmount = purchaseAmount;
		this.listAmount = listAmount;
		this.sellAmount = sellAmount;
		this.finalValueFee = finalValueFee;
		this.payPalFee = payPalFee;
		this.shippingCost = shippingCost;
		this.shippingPaid = shippingPaid;
		this.dateSold = dateSold;
		this.dateReturned = dateReturned;
	}
	
	//----- Standard Methods
	//Getters
	public boolean isDiscounted(){ return discount; }
	public double getFeeRate(){ return rate; }
	public String getNote(){ return note; }
	public String getName(){ return name; }
	public Credit getPurchaseAmount(){ return purchaseAmount; }
	public Credit getListAmount(){ return listAmount; }
	public Credit getSellAmount(){ return sellAmount; }
	public Credit getFinalValueFee(){ return finalValueFee; }
	public Credit getPayPalFee(){ return payPalFee; }
	public Credit getShippingCost(){ return shippingCost; }
	public Credit getShippingPaid(){ return shippingPaid; }
	public Date getDateSold(){ return dateSold; }
	public Date getDateReturned(){ return dateReturned; }
	//Setters
	public void setDiscounted(boolean x){ discount = x; }
	public void setFeeRate(double x){ rate = x; }
	public void setNote(String x){ note = x; }
	public void setName(String x){ name = x; }
	public void setPurchaseAmount(Credit x){ purchaseAmount = x; }
	public void setListAmount(Credit x){ listAmount = x; }
	public void setSellAmount(Credit x){ sellAmount = x; }
	public void setFinalValueFee(Credit x){ finalValueFee = x; }
	public void setPayPalFee(Credit x){ payPalFee = x; }
	public void setShippingCost(Credit x){ shippingCost = x; }
	public void setShippingPaid(Credit x){ shippingPaid = x; }
	public void setDateSold(Date x){ dateSold = x; }
	public void setDateReturned(Date x){ dateReturned = x; }

	//----- Advanced Methods
	public String record(){
		String pa = (purchaseAmount == null)? "#cnull" : purchaseAmount.record();
		String la = (listAmount == null)? "#cnull" : listAmount.record();
		String fv = (finalValueFee == null)? "#cnull" : finalValueFee.record();
		String pp = (payPalFee == null)? "#cnull" : payPalFee.record();
		String sc = (shippingCost == null)? "#cnull" : shippingCost.record();
		String sp = (shippingPaid == null)? "#cnull" : shippingPaid.record();
		String sa = (sellAmount == null)? "#cnull" : sellAmount.record();
		String ds = (dateSold == null)? "#dnull" : dateSold.record();
		String dr = (dateReturned == null)? "#dnull" : dateReturned.record();
		
		return String.format("#g%f&%b&%s&%s%s%s%s%s%s%s%s%s%s",
				rate, discount, name, note, pa, la, sa, fv, pp, sc, sp, ds, dr);
	}
}
