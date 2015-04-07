package net.orthus.pm;


public class Repair extends Item {
	//----- Variables
	private String parentAddress; //Parent
	private String buyerName, trackingNo, buyerZIP;
	private Date dateRepaired;
	
	//----- Constants
	public static final String INCOMING = "Incoming";
	public static final String PENDING = "Pending";
	public static final String COMPLETE = "Complete";
	public static final String RETURNED = "Returned";
			
	//----- Constructors
	//FULL Constructor
	public Repair(String parentAddress, 
				  int id, 
				  boolean discount,
				  String buyerZip,
				  String status, 
				  String overallColor, 
				  String note, 
				  String buyerName,
				  String trackingNo,
				  Credit sellAmount, 
				  Credit finalValueFee, 
				  Credit payPalFee, 
				  Credit shippingCost, 
				  Credit shippingPaid,
				  Date dateSold,
				  Date dateRepaired,
				  Date dateReturned,
				  Assembly assembly){
		
		super(id, discount, status, overallColor, note, sellAmount, finalValueFee,
				payPalFee, shippingCost, shippingPaid, dateSold, dateReturned, assembly);
		this.parentAddress = parentAddress;
		this.buyerZIP = buyerZip;
		this.buyerName = buyerName;
		this.trackingNo = trackingNo;
		this.dateRepaired = dateRepaired;
	}
	
	//----- Standard Methods
	//Getters
	public String getParentAddress(){ return parentAddress; }
	public String getBuyerZIP(){ return buyerZIP; }
	public String getBuyerName(){ return buyerName; }
	public String getName(){ return getParent().getName() + " " + id; }
	public String getTrackingNo(){ return trackingNo; }
	public Date getDateRepaired(){ return dateRepaired; }
	
	//Setters
	public void setParentAddress(String x){ parentAddress = x; }
	public void setBuyerZIP(String x){ buyerZIP = x; }
	public void setBuyerName(String x){ buyerName = x; }
	public void setTrackingNo(String x){ trackingNo = x; }
	public void setDateRepaired(Date x){ dateRepaired = x; }

	//----- Advanced Methods
	public void cancelRepair(){
		//Return Parts
		if(assembly != null){
			if(assembly.getAssemblies() != null)
				for(int i=0; i<assembly.getAssemblies().length; i++)
					getParent().getProductCategory().addAssemblyToSupply(assembly.getAssemblies()[i]);
			
			if(assembly.getParts() != null)
				for(int i=0; i<assembly.getParts().length; i++)
					getParent().getProductCategory().addPartToSupply(assembly.getParts()[i]);
		}
		//Subtract Paypal
		Database.subtractFromPayPalBalance(new Credit(
				sellAmount.getValueInCents() - payPalFee.getValueInCents()));
		
		//Subtract eBay
		Database.addToCurrentEbayBalance(new Credit(-1 * finalValueFee.getValueInDollars()));
		
		//Delete repair
		getParent().removeRepair(this);
	}
	
	public Credit getProfit(){		
		return new Credit(
				sellAmount.getValueInDollars() 
				- getUltimateCost().getValueInDollars());
	}
	
	public String getFormalName(){
		return new String(buyerName).replaceAll(" \\(.*", "");
	}
	
	public Credit getUltimateCost(){
		Credit cst = (assembly != null)? assembly.getTotalCost() : new Credit();
		
		return new Credit(
				cst.getValueInDollars()
				+ finalValueFee.getValueInDollars()
				+ payPalFee.getValueInDollars()
				+ shippingCost.getValueInDollars()
				- shippingPaid.getValueInDollars());
	}
	
	public Service getParent(){
		char id = parentAddress.charAt(0);
		String[] names = parentAddress.split("\\.");
		
		switch(id){
		case 'S':
			for(int i=0; i<Database.getServiceCategories().length; i++)
				if(Database.getServiceCategories()[i].getName().equals(names[0].substring(1)))
					for(int j=0; j<Database.getServiceCategories()[i].getServices().length; j++)
						if(Database.getServiceCategories()[i].getServices()[j].getName().equals(names[1]))
							return Database.getServiceCategories()[i].getServices()[j];
			break;
			
		case 'P':
			for(int i=0; i<Database.getProductCategories().length; i++)
				if(Database.getProductCategories()[i].getName().equals(names[0].substring(1)))
					for(int j=0; j<Database.getProductCategories()[i].getServices().length; j++)
						if(Database.getProductCategories()[i].getServices()[j].getName().equals(names[1]))
							return Database.getProductCategories()[i].getServices()[j];
			break;
		}
		return null;
	}
	
	public void movePartToSupply(Part toMove){
		
		Part pt = assembly.findPartBySerial(toMove);
		
		if(pt == null) return; //No such part on Product to move
		
		Part clone = new Part(pt); //Copy of what exists in Product
		clone.setStatus(Part.SUPPLY);
		
		if(toMove.getQuantity() < pt.getQuantity()){ //Decrement part
			
			pt.setQuantity(pt.getQuantity() - toMove.getQuantity()); //Decrement Product part
			getParent().getProductCategory().addPartToSupply(clone);
		
			
		}else if(toMove.getQuantity() == pt.getQuantity()){ //Move entire part

			clone.setQuantity(pt.getQuantity());
			getParent().getProductCategory().addPartToSupply(clone); //Add to supply
			assembly.removePart(pt); //Remove from Product
			
		}else{ //Request to move more than available made
			
			System.err.println("Request to move more parts than existed occured.");
		}
		
	}//End movePartToSupply()
	
	public void movePartFromSupply(Part toMove, int quantity){
		if(getParent().getProductCategory().getPartSupply() == null) return; //No supply, no part
		
		
		Part pt = getParent().getProductCategory().getPartSupply().findEquivalentPartFromSupply(toMove);
		
		if(pt == null) return; //No such part in supply
		
		Part clone = new Part(toMove);
		clone.setStatus(Part.HOLD);

		
		if(pt.getQuantity() > quantity){ //Decrement from supply
			
			pt.setQuantity(pt.getQuantity() - quantity); //Decrement supply
			assembly.addPart(clone);	//Add to product
			
		}else if(pt.getQuantity() == quantity){ //Remove from supply
			
			getParent().getProductCategory().removePartFromSupply(pt);
			assembly.addPart(clone);
			
		}else{ //Insufficient parts in supply
			
			getParent().getProductCategory().removePartFromSupply(pt);
			clone.setQuantity(pt.getQuantity());
			assembly.addPart(clone);
			
			OptionPane.showError("Not enough parts in supply!\nRequested " 
					+ quantity + ", delivered" + pt.getQuantity() + ".", "Insufficient Parts!");
			
		}
	}//End movePartFromSupply()
	
	public void moveAssemblyToSupply(Assembly temp){
		
		Part[] pts = temp.collapseAssembly();
		
		if(pts != null)
			for(int i=0; i<pts.length; i++)
				movePartToSupply(pts[i]);
		
	}//End moveAssemblyToSupply()
	
	public void moveAssemblyFromSupply(Assembly temp){
		if(getParent().getProductCategory().getPartSupply() == null) return;
		
	
		getParent().getProductCategory().removeSimilarAssemblyFromSupply(temp);
		Assembly a = new Assembly(temp);
		a.setStatus(Assembly.HOLD);
		assembly.addAssembly(new Assembly(a));
		System.out.println("Moving " + a.getName() + " " + a.getTotalCost().toString());
		
	}//End moveAssemblyFromSupply()
	
	public String record(){
	
		String fv = (finalValueFee == null)? "#cnull" : finalValueFee.record();
		String pp = (payPalFee == null)? "#cnull" : payPalFee.record();
		String sc = (shippingCost == null)? "#cnull" : shippingCost.record();
		String sp = (shippingPaid == null)? "#cnull" : shippingPaid.record();
		String sa = (sellAmount == null)? "#cnull" : sellAmount.record();
		String ds = (dateSold == null)? "#dnull" : dateSold.record();
		String de = (dateRepaired == null)? "#dnull" : dateRepaired.record();
		String dr = (dateReturned == null)? "#dnull" : dateReturned.record();
		String a = (assembly == null)? "#xnull" : "#x" + assembly.record();
		
		return String.format("#r%s&%d&%b&%s&%s&%s&%s&%s&%s%s%s%s%s%s%s%s%s%s",
				parentAddress, id, discount, buyerZIP, status, overallColor, note, buyerName,
				trackingNo, sa, fv, pp, sc, sp, ds, de, dr, a);
	}
	
	public String getData(){
		
		String toRet = "";
		
		if(note != null)
			if(!note.equals(""))
				toRet = toRet.concat("  " + note + "\n\n");
		 
		toRet = toRet.concat("  Buyer: " + buyerName + "\n");
		toRet = toRet.concat("  ZIP: " + buyerZIP + "\n");
		toRet = toRet.concat("  Email: " + overallColor + "\n");
		if(status.equals(Repair.COMPLETE)){
			toRet = toRet.concat("  Tracking: " + trackingNo + "\n");
			toRet = toRet.concat("  Date Repaired: " + dateRepaired.displaySimpleDate() + "\n");
		}
		
		toRet = toRet.concat("  Date Sold: " + dateSold.displaySimpleDate() + "\n\n");
		
		
		if(!status.equals(Repair.PENDING)){
			String per = String.format("%.0f%%", 
					100 * getProfit().getValueInDollars() / getUltimateCost().getValueInDollars());
			toRet = toRet.concat("  Profit: " + getProfit() + " (" + per + ")\n");
		}
		toRet = toRet.concat("  Sold For: " + sellAmount.toString() + "\n");
		if(assembly != null)
			toRet = toRet.concat("  Part Cost: " + assembly.getTotalCost() + "\n");
		toRet = toRet.concat("  Final Value Fee: " + finalValueFee + "\n");
		toRet = toRet.concat("  PayPal Fees: " + payPalFee + "\n");
		toRet = toRet.concat("  Shipping Cost: $" + (shippingCost.getValueInDollars() 
				- shippingPaid.getValueInDollars()) + "\n");
		
		return toRet;
	}
	
	public boolean equals(Repair r){
		if(record().equals(r.record()))
			return true;
		return false;
	}
}
