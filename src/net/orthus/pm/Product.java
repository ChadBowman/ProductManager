package net.orthus.pm;

import java.util.ArrayList;

import net.orthus.util.Sorter;

public class Product extends Item 
				     implements Comparable<Product> {

	//----- Variables
	private String parentAddress; //Parent
	private int listDuration;
	private String listingType, overallQuality;
	private Credit purchaseAmount, listAmount, listingFee;
	private Date datePurchased, dateListed;
	
	//----- Constants
	//Product states
	public static final String INCOMING = "Incoming";
	public static final String INVENTORY = "Inventory";
	public static final String STOCK = "Stock";
	public static final String LISTED = "Listed";
	public static final String SOLD = "Sold";
	public static final String RETURNED = "Returned";
	
	//Recommended repair options
	public static final int PART_REPLACEMENT = 0;
	public static final int ASSEMBLY_REPLACEMENT = 1;
	public static final int GENERAL_PART_REPLACEMENT = 2;
	public static final int GENERAL_ASSEMBLY_REPLACEMENT = 3;
	
	
	/////////////////////////////////////////////////////
	
	//----- Constructors
	//FULL Constructor
	public Product(String parentAddress,
				   int id, 
				   int listDuration,
				   boolean discount,
				   String status,  
				   String overallColor, 
				   String note,
				   String listingType,
				   String overallQuality,
				   Credit purchaseAmount,
				   Credit listAmount,
				   Credit listingFee, 
				   Credit finalValueFee, 
				   Credit payPalFee, 
				   Credit shippingCost, 
				   Credit shippingPaid,
				   Credit sellAmount, 
				   Date datePurchased, 
				   Date dateListed, 
				   Date dateSold, 
				   Date dateReturned,
				   Assembly assembly){
		
		super(id, discount, status, overallColor, note, sellAmount, finalValueFee, payPalFee, shippingCost,
				shippingPaid, dateSold, dateReturned, assembly);
		
		if(id == 8 && overallColor.equals("Mario"))
			status = Product.LISTED;
		
		this.parentAddress = parentAddress;
		this.listDuration = listDuration;
		this.purchaseAmount = purchaseAmount;
		this.listAmount = listAmount;
		this.listingFee = listingFee;
		this.listingType = listingType;
		this.overallQuality = overallQuality;
		this.datePurchased = datePurchased;
		this.dateListed = dateListed;
	}
	
	/////////////////////////////////////////////////////
	
	//----- Standard Methods
	//Getters
	public String getParentAddress(){ return parentAddress; }
	public int getListDuration(){ return listDuration; }
	public Credit getListingFee(){ return listingFee; }
	public String getListingType(){ return listingType; }
	public Credit getListAmount(){ return listAmount; }
	public String getOverallQuality(){ return overallQuality; }
	public Date getDatePurchased(){ return datePurchased; }
	public Date getDateListed(){ return dateListed; }
	//Setters
	public void setParentAddress(String x){ parentAddress = x; }
	public void setListDuration(int x){ listDuration = x; }
	public void setListingFee(Credit x){ listingFee = x; }
	public void setListingType(String x){ listingType = x; }
	public void setListAmount(Credit x){ listAmount = x; }
	public void setOverallQuality(String x){ overallQuality = x; }
	public void setDatePurchased(Date x){ datePurchased = x; }
	public void setDateListed(Date x){ dateListed = x; }
	
	
	/////////////////////////////////////////////////////
	
	
	//----- Advanced Methods
	public String getName(){
		return getParent().getName();
	}
	
	public String getFullName(){
		return getParent().getName() + " " + id;
	}
	
	public Credit getShippingCostDifference(){
		if(shippingPaid == null || shippingCost == null) return null;
		
		return new Credit(shippingCost.getValueInDollars() 
				- shippingPaid.getValueInDollars());
	}
	
	public ProductCategory getParent(){
		ProductCategory[] cats = Database.getProductCategories();
		if(cats != null)
			for(int i=0; i<cats.length; i++)
				if(parentAddress.equals(cats[i].getAddress()))
					return cats[i];
		return null;
	}
	
	public Credit getUltimateCost(){
		if(!status.equals(Product.SOLD)) return null;
		
		return new Credit(
				assembly.getTotalCost().getValueInDollars()
				+ finalValueFee.getValueInDollars()
				+ payPalFee.getValueInDollars()
				+ getShippingCostDifference().getValueInDollars());
	}
	
	/////////////////////////////////////////////////////
	
	public void movePartToSupply(Part toMove){
		
		Part pt = assembly.findPartBySerial(toMove);
		
		if(pt == null) return; //No such part on Product to move
		
		Part clone = new Part(pt); //Copy of what exists in Product
		clone.setStatus(Part.SUPPLY);
		
		if(toMove.getQuantity() < pt.getQuantity()){ //Decrement part
			
			pt.setQuantity(pt.getQuantity() - toMove.getQuantity()); //Decrement Product part
			getParent().addPartToSupply(clone);
		
			
		}else if(toMove.getQuantity() == pt.getQuantity()){ //Move entire part

			clone.setQuantity(pt.getQuantity());
			getParent().addPartToSupply(clone); //Add to supply
			assembly.removePart(pt); //Remove from Product
			
		}else{ //Request to move more than available made
			
			System.out.println("Request to move more parts than existed on " 
				    + getFullName() + " occured.");
		}
		
	}//End movePartToSupply()
	
	public void movePartFromSupply(Part toMove){
		if(getParent().getPartSupply() == null) return; //No supply, no part
		
		
		Part pt = getParent().getPartSupply().findEquivalentPartFromSupply(toMove);
		
		if(pt == null) return; //No such part in supply
		
		Part clone = new Part(toMove);
		clone.setStatus(Part.PRODUCT);

		
		if(pt.getQuantity() > toMove.getQuantity()){ //Decrement from supply
			
			pt.setQuantity(pt.getQuantity() - toMove.getQuantity()); //Decrement supply
			assembly.addPart(clone);	//Add to product
			
		}else if(pt.getQuantity() == toMove.getQuantity()){ //Remove from supply
			
			getParent().removePartFromSupply(pt);
			assembly.addPart(clone);
			
		}else{ //Insufficient parts in supply
			
			getParent().removePartFromSupply(pt);
			clone.setQuantity(pt.getQuantity());
			assembly.addPart(clone);
			
		}
	}//End movePartFromSupply()
	
	public void moveAssemblyToSupply(Assembly temp){
		
		Part[] pts = temp.collapseAssembly();
		
		if(pts != null)
			for(int i=0; i<pts.length; i++)
				movePartToSupply(pts[i]);
		
	}//End moveAssemblyToSupply()
	
	public void moveAssemblyFromSupply(Assembly temp){
		if(getParent().getPartSupply() == null) return;
		
	
		getParent().removeSimilarAssemblyFromSupply(temp);
		Assembly a = new Assembly(temp);
		a.setStatus(Assembly.PRODUCT);
		assembly.addAssembly(new Assembly(a));
		System.out.println("Moving " + a.getName() + " " + a.getTotalCost().toString());
		
	}//End moveAssemblyFromSupply()
	
	
	/////////////////////////////////////////////////////
	
	public Credit getProfit(){
		if(!status.equals(Product.SOLD))
			return null;
		Credit toReturn = new Credit(sellAmount);
		toReturn.subtract(assembly.getTotalCost()); //Total Cost
		toReturn.subtract(finalValueFee); //FVFee
		toReturn.subtract(listingFee); //List Fee
		toReturn.subtract(payPalFee); //Paypal
		toReturn.subtract(getShippingCostDifference()); //Shipping Difference
		return toReturn;
	} 
	
	//Counts up all sell amounts of same colored products
	//For sell amounts which have 3 or more instances
	//Find the average profit impulse (average profit/days on market)
	//Return sell amount with best profit impulse
	
	public Credit getRecommendedRetailPrice(){
		
		//Grab all sold products
		Product[] ps = this.getParent().getProductsByStatus(Product.SOLD);
		Product[] focus = null;
		
		//Collect all same-colored sold products
		if(ps != null)
			for(int i=0; i<ps.length; i++)
				if(this.getOverallColor().equals(ps[i].getOverallColor()))
					focus = ArrayManager.addToArray(ps[i], focus);
		
		//If there is no previous sales history or insufficient data
		//Return minimum price to reach profit-percentage goal
		if(focus == null)
			return getRecommendedSellAmount();
		
		//GROUP CLASS FOR DATA
		class Group{
			
			private Credit value;
			private ArrayList<Integer> daysOnMarket;
			
			public Group(Credit value, int dom){
				this.value = value;
				daysOnMarket = new ArrayList<Integer>();
				daysOnMarket.add(dom);
			}
			
			public Credit getValue(){ return value; }
			
			public void add(int val){ daysOnMarket.add(val); }
			
			public Credit getAverageImpulse(){
				int sum = 0, i=0;
				
				if(this.daysOnMarket.size() < 2)
					return new Credit(-1);
				
				for(; i< this.daysOnMarket.size(); i++)
					sum += this.daysOnMarket.get(i);
				
				return new Credit(value.getValueInDollars() / sum);
			}
		}
		//END GROUP CLASS
		
		ArrayList<Group> groups = new ArrayList<Group>();
		
		boolean flag;
		for(int i=0; i<focus.length; i++){		//For each console of the same color
			
			flag = false; //No match found
			for(int j=0; j < groups.size(); j++)//For each group in group array
				if(focus[i].getSellAmount().equals(groups.get(j).getValue())){ //Same sell amt found
					groups.get(j).add(focus[i].getDaysOnMarket()); //Tack on days on market stat
					flag = true; //Flag for no new group
				}
			
			if(!flag) //No match found
				groups.add(new Group(focus[i].getSellAmount(), focus[i].getDaysOnMarket()));
				
		}
					
		//Find price with largest significant impulse
		Group large = new Group(new Credit(), 0);
		for(int i=0; i<groups.size(); i++)
			if(groups.get(i).getAverageImpulse().getValueInCents() > large.getAverageImpulse().getValueInCents())
				large = groups.get(i);

		//No significant data found
		if(large.getAverageImpulse().getValueInCents() < 0)
			return getRecommendedSellAmount();
		
		return large.getValue();
	}
	
	//Finds what's needed to reach profit-percentage goal
	public Credit getRecommendedSellAmount(){
		Credit toRet = new Credit();
		Credit cost;
		Credit amt;
		
		do{
			toRet.add(1); //Add one cent to revenue
			
			cost = new Credit(assembly.getTotalCost());
			
			cost.add(getParent().getAverageShippingDifference());
			
			
			cost.add(getParent().getProductFinalValueFeeRate(Date.getTodaysDate()) //Add FVF
					* toRet.getValueInDollars());
			
			cost.add(Database.getPayPalFeeRate() 	//Add PPF
					* toRet.getValueInDollars());
			
			amt = new Credit(Database.getProfitPercentageGoal()
					* cost.getValueInDollars() + cost.getValueInDollars());
			
			
		}while(toRet.getValueInCents() != amt.getValueInCents());
		
		
		return toRet;
		
	}
	
	/////////////////////////////////////////////////////
	
	//Uses calculateCostToFix()
	public Credit getEstimatedReturn(){
		
		Credit toRet = new Credit();
		
		//Sell value
		//TODO use method which includes quality
		Credit sale = getParent().getAverageSale(overallColor);
		toRet.add(sale);
		
		//Purchase price
		toRet.subtract(assembly.getTotalCost());
		
		//Repair cost
		toRet.subtract(getRepairCost());
			
		//Shipping cost
		toRet.subtract(getParent().getAverageShippingDifference());
		
		//Fee costs
		Date d = (dateSold == null)? Date.getTodaysDate() : dateSold;
		toRet.subtract(getParent().getProductFinalValueFeeRate(d) 
					* sale.getValueInDollars());
		
		toRet.subtract(Database.getPayPalFeeRate() * sale.getValueInDollars());
		
		
		return toRet;
	}
	
	public Credit getRepairCost(){
		
		Credit toRet = new Credit();
		
		Assembly[] t = getParent().getTemplates();
		for(int i=1; i<t.length; i++)	//Skip first element (Default Assembly)
			if(getOptions(t[i]) != null) //If product is actually missing parts
				toRet.add(getOptions(t[i])[0].cost); //Add cheapest option for each template
		
		
		Part[] rest = getPartsNotInATemplate(getMissingParts(getParent().getDefaultAssembly()));
			//Parts which are not in a template and are missing from Default Assembly
		
		Part[] supply = (getParent().getPartSupply() == null)? null :
					getParent().getPartSupply().getParts();
		
		boolean flag;
		
		if(rest != null)
			for(int i=0; i<rest.length; i++){ //for each part
				flag = false;
				
				if(supply != null)
					for(int j=0; j<supply.length; j++)
						if(rest[i].getSerial() == supply[j].getSerial() 	//If serials match
								&& rest[i].getQuantity() <= supply[j].getQuantity()){//And there is enough
							toRet.add(supply[j].getPrice().getValueInDollars() //Add cost to replace
									* rest[i].getQuantity());
							flag = true; //No need to add estimate
						}
				
				if(!flag)
					toRet.add(getParent().getDefaultAssembly().findPartBySerial(rest[i]).getPrice().getValueInDollars()
							* rest[i].getQuantity()); //Estimate with default cost
			}
		
		return toRet;
				
	}//End getRepairCost()
	
	public String[] getRepairOptionResults(Assembly template){
		
		String[] toRet = null;
		RepairOption[] ops = getOptions(template);
		boolean flag;
	
		for(int i=0; i<ops.length; i++){
			
			flag = true;
			
			if(ops[i].cost.getValueInCents() < 1000000)
				switch(ops[i].type){
				case PART_REPLACEMENT: 
					toRet = ArrayManager.addToArray("   Replace with parts from supply: " 
							+ ops[i].cost.toString(), toRet); 
					break;
					
				case ASSEMBLY_REPLACEMENT:
					toRet = ArrayManager.addToArray("   Replace with assembly from supply: " 
							+ ops[i].cost.toString(), toRet); 
					break;
					
				case GENERAL_PART_REPLACEMENT:
					if(toRet != null)
						for(int j=0; j<toRet.length; j++)
							if(toRet[j].contains("parts")) //More accurate provided
								flag = false;
					
					if(flag)
						toRet = ArrayManager.addToArray("   Estimate to replace with parts: " 
								+ ops[i].cost.toString(), toRet);
					break;
					
				case GENERAL_ASSEMBLY_REPLACEMENT:
					if(toRet != null)
						for(int j=0; j<toRet.length; j++)
							if(toRet[j].contains("assembly")) //More accurate provided
								flag = false;
					
					if(flag)
						toRet = ArrayManager.addToArray("   Estimate to replace with assembly: " 
								+ ops[i].cost.toString(), toRet);
				}
		}
			
		
		return toRet;
		
	}//End getRepairOptionResults()
	
	//Returns null if nothing to repair
	private RepairOption[] getOptions(Assembly template){
		if(template == null) return null;
		
		if(getMissingParts(template) == null) //No parts missing from tempate
			return null;
		
		RepairOption[] options = new RepairOption[4];
		options[0] = new RepairOption(getCostOfRawPartReplacement(template), 
				PART_REPLACEMENT);
		options[1] = new RepairOption(getCostOfAssemblyReplacement(template), 
				ASSEMBLY_REPLACEMENT);
		options[2] = new RepairOption(getEstimateOfRawPartReplacement(template), 
				GENERAL_PART_REPLACEMENT);
		options[3] = new RepairOption(getEstimateOfAssemblyReplacement(template), 
				GENERAL_ASSEMBLY_REPLACEMENT);
		
		for(int i=0; i<4; i++)
			if(options[i].cost == null) 
				options[i].cost = new Credit(Integer.MAX_VALUE);

		Sorter<RepairOption> sort = new Sorter<RepairOption>();
		options = sort.bubbleSort(options);
		
		return options;
		
	}//End getCosts()
	
	public Credit getEstimateOfAssemblyReplacement(Assembly template){
		if(template == null) return null;
		
		Credit toRet = new Credit(template.getTotalCost());
		
		Part[] dismantle = getPresentParts(template);
		
		if(dismantle != null)
			for(int i=0; i<dismantle.length; i++)
				toRet.subtract(dismantle[i].getTotalCost());
		
		return toRet;
		
	}//End getGeneralCostOfAssemblyReplacement()
	
	
	//Returns null if there are no usable assemblies in Part Supply
	//Returns cost of cheapest assembly minus worth of all present parts
	public Credit getCostOfAssemblyReplacement(Assembly template){
		if(template == null || getParent().getPartSupply() == null) return null;
		if(getParent().getPartSupply().getAssemblies() == null) return null; //No Assemblies in supply
		
		ItemSorter sort = new ItemSorter(getParent().getPartSupply().getAssemblies());
		Assembly[] as = sort.cost();
		
		if(as == null) return null; //No assemblies available in supply
		if(as.length < 1) return null;
		Credit toRet = new Credit(as[0].getTotalCost()); //Add cost of cheapest assembly in stock
		
		Part[] dismantle = getPresentParts(template); //Parts to dismantle for credit
		
		if(dismantle != null)
			for(int i=0; i<dismantle.length; i++)
				toRet.subtract(dismantle[i].getTotalCost()); //Subtract value of parts which can be
																//dismantled to supply
		return toRet;
		
	}//End getCostOfAssemblyReplacement()
	
	public Credit getEstimateOfRawPartReplacement(Assembly template){
		
		Credit toRet = new Credit();
		
		Assembly def = getParent().getDefaultAssembly();
		Part[] needed = getMissingParts(template); //Parts missing from assembly
		
		if(needed != null)
			for(int i=0; i<needed.length; i++)	//For each missing part
				toRet.add(def.findPartBySerial(needed[i]).getPrice().getValueInDollars()  //Add default price
						* needed[i].getQuantity());								  //Times needed quantity
		
		return toRet;
		
	}//End getEstimateOfRawPartRepalcement
	
	public Credit getCostOfRawPartReplacement(Assembly template){
		if(template == null || getParent().getPartSupply() == null) return null;
		if(getParent().getPartSupply().getParts() == null) return null; //No parts in supply
		
		Credit toRet = new Credit();
		
		Assembly sup = getParent().getPartSupply();
		Part[] needed = getMissingParts(template);
		
		if(needed != null && sup != null)
			for(int i=0; i<needed.length; i++){
				Part p = sup.findCheapestPartBySerialFromSupply(needed[i]);
				if(p == null) return null; //No part available, end method;
				if(p.getQuantity() < needed[i].getQuantity()) return null; //Not enough parts in supply, end method
				
				toRet.add(p.getPrice().getValueInDollars() * needed[i].getQuantity()); //Add cost
			}
		
		return toRet;
		
	}//End getCostOfRawPartReplacement()

	//Returns array of parts which are contained in template, but not in 
	//	current products Assembly
	public Part[] getMissingParts(Assembly template){
		if(template == null || assembly == null) return null;
		
		Part[] t = template.collapseAssembly(); //Make template easier to work with
		Part[] p = assembly.collapseAssembly(); //Same for Products assembly
		
		if(p == null) return null;
		
		Part[] toRet = null;	//Array to build and return
		
		boolean flag; 
		
		for(int i=0; i<t.length; i++){ //For each part in template
			
			flag = false;                 //Assume part not found
			
			for(int j=0; j<p.length; j++) //Cycle through each part in product
				if(t[i].getSerial() == p[j].getSerial()){ //If part found, don't add entire part
					
					flag = true;					        //Notify flag, part is found
					
					if(t[i].getQuantity() != p[j].getQuantity()){ //Some parts are Missing
						Part clone = new Part(t[i]);
						clone.setQuantity(t[i].getQuantity() - p[j].getQuantity()); //Set quantity, the difference
						toRet = ArrayManager.addToArray(clone, toRet);
					}
				}
			
			if(!flag) //If entire part is not found in p array
				toRet = ArrayManager.addToArray(t[i], toRet); //Add to return array
		}
				
		return toRet;
		
	}//End getMissingParts()
	
	
	public Part[] getPresentParts(Assembly template){
		if(template == null || assembly == null) return null;
		
		Part[] t = template.collapseAssembly(); //Make template easier to work with
		Part[] p = assembly.collapseAssembly(); //Same for Products assembly
		
		Part[] toRet = null;	//Array to build and return
		
		for(int i=0; i<t.length; i++)		//For each part in templates
			for(int j=0; j<p.length; j++)   //Cycle though product assembly
				if(t[i].getSerial() == p[j].getSerial()) //If match found
					toRet = ArrayManager.addToArray(p[j], toRet); //Add product part (to conserve part quantity)
		
		return toRet;
		
	}//End getPresentParts()
	
	private Part[] getPartsNotInATemplate(Part[] p){
		if(p == null) return null;
		
		Part[] toRet = null;
		
		Assembly[] temps = getParent().getTemplates();
		boolean found;
		
		for(int i=0; i<p.length; i++){ //For each part in product
			found = false;
			for(int j=1; j<temps.length; j++) //Skip first element (Default Assem)
				if(temps[j].findPartBySerial(p[i]) != null) //Search each template
					found = true;
			
			if(!found)	//If part not found in any template
				toRet = ArrayManager.addToArray(p[i], toRet); //Add to return
		}
		
		return toRet;
		
	}//End getPartsNotInATemplate()

	
	public int getDaysOnMarket(){
		
		return getDateSold().getDateInDays() 
				- getDateListed().getDateInDays();
		
	}//End getPartsNotInATemplate()
	
	public Credit getProfitImpulse(){
		
		return new Credit(this.getProfit().getValueInDollars() / this.getDaysOnMarket());
		
	}
	

	/////////////////////////////////////////////////////
	
	
	//----- Interfaces
	public String record(){ 
		String pa = (purchaseAmount == null)? "#cnull" : purchaseAmount.record();
		String la = (listAmount == null)? "#cnull" : listAmount.record();
		String lf = (listingFee == null)? "#cnull" : listingFee.record();
		String fv = (finalValueFee == null)? "#cnull" : finalValueFee.record();
		String pp = (payPalFee == null)? "#cnull" : payPalFee.record();
		String sc = (shippingCost == null)? "#cnull" : shippingCost.record();
		String sp = (shippingPaid == null)? "#cnull" : shippingPaid.record();
		String sa = (sellAmount == null)? "#cnull" : sellAmount.record();
		String dp = (datePurchased == null)? "#dnull" : datePurchased.record();
		String dl = (dateListed == null)? "#dnull" : dateListed.record();
		String ds = (dateSold == null)? "#dnull" : dateSold.record();
		String dr = (dateReturned == null)? "#dnull" : dateReturned.record();
		String a = (assembly == null)? "#xnull" : "#x" + assembly.record();
		return String.format("#t%s&%d&%d&%b&%s&%s&%s&%s&%s%s%s%s%s%s%s%s%s%s%s%s%s%s",
				parentAddress, id, listDuration, discount, status, overallColor, note, listingType,
				overallQuality, pa, la, lf, fv, pp, sc, sp, sa, dp, dl, ds, dr, a);
	}
	
	public String getData(){
		String data = "";
		data = (note == null)? data : data.concat(note + "\n\n");
		data = (overallColor == null)? "" : data.concat("Color: " + overallColor + "\n");
		data = data.concat("Estimated Return: " + getEstimatedReturn().toString() + "\n");
		data = data.concat("Total Cost: " + assembly.getTotalCost().toString() + "\n\n");
		data = (sellAmount == null)? data : data.concat("Sold For: " + sellAmount.toString() + "\n");
		data = (getProfit() == null)? data : data.concat(String.format("Profit: %s (%.0f%%)%n", getProfit(), 
				getProfit().getValueInDollars() / this.getUltimateCost().getValueInDollars() * 100));
		
		data = (datePurchased == null)? data : data.concat("Date Purchased: " + datePurchased.displaySimpleDate() + "\n");
		data = (dateListed == null)? data : data.concat("Date Listed: " + dateListed.displaySimpleDate() + "\n");
		data = (dateSold == null)? data : data.concat("Date Sold: " + dateSold.displaySimpleDate() + "\n");
		data = (dateReturned == null)? data : data.concat("Date Retunred: " + dateReturned.displaySimpleDate() + "\n");
		data = (listingType == null)? data : data.concat("Listing Type: " + listingType + "\n");
		data = (listingFee == null)? data : data.concat("Listing Fee: " + listingFee.toString() + "\n");
		data = (finalValueFee == null)? data : data.concat("Final Value Fee: " + finalValueFee.toString() + "\n");
		data = (payPalFee == null)? data : data.concat("PayPal Fee: " + payPalFee.toString() + "\n");
		data = (shippingCost == null)? data : data.concat("Shipping Cost: $" + 
				(shippingCost.getValueInDollars() - shippingPaid.getValueInDollars()) + "\n");
		data = (discount)? data.concat("Top Rated Discount: Yes\n") : data.concat("Top Rated Discount: No\n");
		
		return data;
	}
	
	public boolean equals(Product o){
		if(this.record().equals(o.record()))
			return true;
		else
			return false;
	}
	
	public int compareTo(Product o) {
		return Double.compare(o.getEstimatedReturn().getValueInDollars(), 
				this.getEstimatedReturn().getValueInDollars());
	}
	
	private class RepairOption implements Comparable<RepairOption>{
		
		public Credit cost;
		public int type;
		
		public RepairOption(Credit cost, int type){
			this.cost = cost;
			this.type = type;
		}
		@Override
		public int compareTo(RepairOption o) {
			return cost.compareTo(o.cost);
		}
		
	}//End RepairOption
}
