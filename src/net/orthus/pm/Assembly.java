package net.orthus.pm;

import net.orthus.util.Sorter;

public class Assembly extends Constituent
					  implements Comparable<Assembly>{
	
	//----- Variables
	private String parentCategory; //Parent
	private Assembly[] assemblies;
	private Part[] parts;
	
	//----- Constants
	//For Products
	public static final String DEFAULT_TEMPLATE = "Default Template";
	public static final String PART_SUPPLY = "Part Supply";
	
	//For Services
	public static final String SERVICE_TEMPLATE = "Service Template";
	
	//For Part Ordering
	public static final String PART_TEMPLATE = "Part Template";
	
	//For Shipping Options
	public static final String SHIPPING_TEMPLATE = "Shipping Template";

	//----- Constructors
	
	//FULL Constructor
	public Assembly(int serial,
					int quantity,
					int duration,
					String parentCategory, 
					String name, 
					String status,
					String color,
					Date purchaseDate,
					Date dateListed,
					Date dateSold,
					Credit listPrice,
					Credit listFee,
					Credit finalValueFee,
					Credit payPalFee,
					Credit shipCost,
					Credit shipPaid,
					Assembly[] assemblies,
					Part[] parts) {
		
		super(serial, quantity, duration, name, status, color, purchaseDate, dateListed, 
				dateSold, listPrice, listFee, finalValueFee, payPalFee, shipCost, shipPaid);
		
		this.parentCategory = parentCategory;
		this.assemblies = assemblies;
		this.parts = parts;
	}
	
	public Assembly(int serial,
					int quantity,
					int duration,
					String parentCategory, 
					String name, 
					String status,
					String color,
					Date purchaseDate,
					Date dateListed,
					Credit listPrice,
					Credit listFee,
					Assembly[] assemblies,
					Part[] parts) {

		super(serial, quantity, duration, name, status, color, 
				purchaseDate, dateListed, null, listPrice, listFee);

		this.parentCategory = parentCategory;
		this.assemblies = assemblies;
		this.parts = parts;
	}
	
	//Simple utility Constructor
	public Assembly(String name,
					Assembly[] assembly,
					Part[] parts){
		super(0, 1, 0, name, null, null, null, null, null, null, null);
		this.assemblies = assembly;
		this.parts = parts;
	}
	
	//Clone
	public Assembly(Assembly copy){
		super(copy.getSerial(), 0, 0, null, null, null, null, null, null, null, null);
		
		try{this.quantity = new Integer(copy.getQuantity());}catch(NullPointerException e){}
		try{this.name = new String(copy.getName());}catch(NullPointerException e){}
		try{this.status = new String(copy.getStatus());}catch(NullPointerException e){}
		try{this.color = new String(copy.getColor());}catch(NullPointerException e){}
		try{this.duration = new Integer(copy.getDuration());}catch(NullPointerException e){}
			this.datePurchased = new Date(copy.getDatePurchased());
		try{this.parentCategory = new String(copy.getParentAddress());}catch(NullPointerException e){}
		this.assemblies = cloneArray(copy.getAssemblies());
		this.parts = Part.cloneArray(copy.getParts());
		this.dateListed = new Date(copy.getDateListed());
		this.dateSold = new Date(copy.getDateSold());
		this.listPrice = new Credit(copy.getListPrice());
		this.listFee = new Credit(copy.getListFee());
	}
	
	///////////////////////////////////////////////////////////
	//----- Standard Methods
	//Getters
	public String getParentAddress(){ return parentCategory; }
	public Assembly[] getAssemblies() { return assemblies; }
	public Part[] getParts(){ return parts; }
	//Setters
	public void setParentCategoryAddress(String category){ this.parentCategory = category; }
	public void setAssemblies(Assembly[] assemblies){ this.assemblies = assemblies; }
	public void setParts(Part[] parts){ this.parts = parts; }
	
	public void addPart(Part newPart){ 
			parts = ArrayManager.addToArray(newPart, parts); 
	}
	public void addAssembly(Assembly newAss){ 
		assemblies = ArrayManager.addToArray(newAss, assemblies); 
	}
	public void removePart(Part toRemove){ 
		parts = ArrayManager.removeFromArray(toRemove, parts); 
	}
	public void removeAssembly(Assembly toRemove){ 
		assemblies = ArrayManager.removeFromArrayBySerial(toRemove, assemblies); 
	}
	public void removeAssemblyEquiv(Assembly as){
		assemblies = ArrayManager.removeFromArrayByEquiv(as, assemblies);
	}
	//----- Advanced Methods
	///////////////////////////////////////////////////////////	
	public static Assembly[] cloneArray(Assembly[] array){
		if(array == null) return null;
		
		Assembly[] toReturn = new Assembly[array.length];
		for(int i=0; i<array.length; i++)
			toReturn[i] = new Assembly(array[i]);
		
		return toReturn;
	}
	
	//Returns array of all parts in assembly (including sub-assemblies)
	public Part[] collapseAssembly(){
		Part[] arr = null;
		
		if(parts != null)
			for(int i=0; i<parts.length; i++)
				arr = ArrayManager.addToArray(parts[i], arr);
		
		if(assemblies != null)
			for(int i=0; i<assemblies.length; i++)
				arr = ArrayManager.addToArray(assemblies[i].collapseAssembly(), arr);
	
		return arr;
		
	}//End collapseAssembly()
	
	///////////////////////////////////////////////////////////
	
	public Credit getTotalCost(){
		Credit result = new Credit();
		
		if(assemblies != null)
			for(int i=0; i<assemblies.length; i++)
				result.add(assemblies[i].getTotalCost());
		
		if(parts != null) //Check for null list
			for(int i=0; i<parts.length; i++)
				result.add(parts[i].getTotalCost()); //Add all prices
		
		result.multiply(quantity);
		
		return result;
	}
	
	public Credit getUnitPrice(){
		Credit ret = new Credit();
		
		if(quantity == 0) return ret;
		
		ret.add(getTotalCost());
		ret.divide(quantity);
		return ret;
	}
	
	public Credit getUltimateCost(){
		if(!status.equals(Constituent.SOLD)) return null;
		
		return new Credit(
				getTotalCost().getValueInDollars()
				+ finalValueFee.getValueInDollars()
				+ payPalFee.getValueInDollars()
				+ shipCost.getValueInDollars()
				- shipPaid.getValueInDollars());
	}
	
	///////////////////////////////////////////////////////////
	
	public Category getCategory(){ 
		
		if(parentCategory.charAt(0) != 'C')
			return (Category) getServiceCategory();
	
		Category[] arrayToSearch = Database.getCategories();
		if(arrayToSearch != null)
			for(int i=0; i<arrayToSearch.length; i++)
				if(arrayToSearch[i].getAddress().equals(parentCategory))
					return arrayToSearch[i];
		
		return null;
	}
	
	public ServiceCategory getServiceCategory(){ 
		
		if(parentCategory.charAt(0) != 'S')
			return (ServiceCategory) getProductCategory();
	
		ServiceCategory[] arrayToSearch = Database.getServiceCategories();
		if(arrayToSearch != null)
			for(int i=0; i<arrayToSearch.length; i++)
				if(arrayToSearch[i].getAddress().equals(parentCategory))
					return arrayToSearch[i];
		
		return null;
	}
	
	public ProductCategory getProductCategory(){ 
		
		if(parentCategory.charAt(0) != 'P') return null;
	
		ProductCategory[] arrayToSearch = Database.getProductCategories();
		if(arrayToSearch != null)
			for(int i=0; i<arrayToSearch.length; i++)
				if(arrayToSearch[i].getAddress().equals(parentCategory))
					return arrayToSearch[i];
		
		return null;
	}
	
	
	///////////////////////////////////////////////////////////	

	
	public void distributeCost(){ distributeCost(getTotalCost()); }
	
	public void distributeCost(Credit amount){
		Assembly def = getCategory().getDefaultAssembly(); //Grab default distributions
		Credit defT = getDefaultTotal(def); //Total cost of a Default Assembly with the same parts
		
		distributeCost(amount, def, defT); //Recursively distributeCost()
		
		
		getMostExpensiveSinglePart(new Part()).getPrice().add( //Correct to difference
				amount.getValueInDollars() - (getTotalCost().getValueInDollars() / quantity));
		
		//Asses gain/loss
		System.out.println("Distribute Cost resulted in a difference of $" + 
				(amount.getValueInDollars() - (getTotalCost().getValueInDollars() / quantity)));
	}
	
	//Recursive method for distributeCost(Credit amount);
	private void distributeCost(Credit amount, Assembly def, Credit defTotal){
		
		if(parts != null)
			for(int i=0; i<parts.length; i++){ //For each part in current layer
				
				parts[i].setPrice(
						(def.findPartBySerial(parts[i]).getPrice().getValueInDollars()  //Get Default price
									/ defTotal.getValueInDollars())   			//Divide for proportion
										* amount.getValueInDollars());			//Multiply by input amount
			}
		
		
		if(assemblies != null)
			for(int i=0; i<assemblies.length; i++)
				assemblies[i].distributeCost(amount, def, defTotal); //Repeat for sub-assemblies
		
	}//End distributeCost() [Recursive]
	
	//Used in distributeCost()
	private Credit getDefaultTotal(Assembly def){
		
		Credit toRet = new Credit();
		
		if(parts != null)
			for(int i=0; i<parts.length; i++)
				toRet.add(def.findPartBySerial(parts[i]).getPrice().getValueInDollars() //Get Default match
						* parts[i].getQuantity()); //Multiply current quantity
		
		if(assemblies != null)
			for(int i=0; i<assemblies.length; i++)
				toRet.add(assemblies[i].getDefaultTotal(def));	//Repeat & add for sub-assemblies
		
		if(toRet.getValueInCents() == 0){	//Will cause Divide by Zero
			System.err.println("Default Total is $0!");
			return new Credit(0.01);
		}
		
		return toRet;
		
	}//End getDefaultTotal()
	
	//Returns most expensive part with 1 quantity
	//If no such part found, returns initial input
	private Part getMostExpensiveSinglePart(Part toRet){
		
		if(parts != null)
			for(int i=0; i<parts.length; i++)
				if(parts[i].getQuantity() == 1 && toRet.compareTo(parts[i]) < 0)
					toRet = parts[i];
		
		if(assemblies != null)
			for(int i=0; i<assemblies.length; i++)
				toRet = assemblies[i].getMostExpensiveSinglePart(toRet);
		
		return toRet;
		
	}//End getMostExpensiveSinglePart()
	
	///////////////////////////////////////////////////////////
	
	public void distributeColor(String color){
		
		if(parts != null)
			for(int i=0; i<parts.length; i++)
				if(parts[i].getColor() != null){
					if(!parts[i].getColor().equals(Part.NO_COLOR))
						parts[i].setColor(color);
				}else
					parts[i].setColor(color);
		
		
		
		if(assemblies != null)
			for(int i=0; i<assemblies.length; i++)
				assemblies[i].distributeColor(color);
		
	}//End distributeColor()
	
	public void distributeQuality(String quality){
		
		if(parts != null)
			for(int i=0; i<parts.length; i++)
				if(parts[i].getQuality() != null){
					if(!parts[i].getQuality().equals(Part.NO_QUALITY))
						parts[i].setQuality(quality);
				}else
					parts[i].setQuality(quality);
		
		if(assemblies != null)
			for(int i=0; i<assemblies.length; i++)
				assemblies[i].distributeQuality(quality);
		
	}//End distributeQuality()
	
	public void distributeStatus(String status){
		
		if(parts != null)
			for(int i=0; i<parts.length; i++)
				parts[i].setStatus(status);
			
		if(assemblies != null)
			for(int i=0; i<assemblies.length; i++)
				assemblies[i].distributeStatus(status);
		
	}//End distributeStatus()
	
	///////////////////////////////////////////////////////////
	
	//Returns part with same serial & color
	public Part findEquivalentPart(Part toFind){
	
		if(parts != null)	//If parts exist in current layer
			for(int i=0; i<parts.length; i++)
				if(parts[i].equivalent(toFind))
					return parts[i];
		
		//Part not found in current layer
		
		if(assemblies != null) //Search child assemblies
			for(int i=0; i<assemblies.length; i++)
				if(assemblies[i].findEquivalentPart(toFind) != null)
					return assemblies[i].findEquivalentPart(toFind);
			
		return null; //Part not found anywhere
		
	}//End findEquivalentPart()
	
	public Part findExactPartFromSupply(Part toFind){
		
		if(parts != null)
			for(int i=0; i<parts.length; i++)
				if(parts[i].toString().equals(toFind.toString()))
					return parts[i];
		
		return null;
		
	}// End fineExactPartFromSupply;
	
	public Part findEquivalentPartFromSupply(Part toFind){
		
		if(parts != null)
			for(int i=0; i<parts.length; i++)
				if(parts[i].equiv(toFind))
					return parts[i];
		
		return null;
		
	}//End findEquivalentPartFromSupply;
	
	
	public Part findPartBySerial(Part toFind){
		
		if(parts != null)	//If parts exist in current layer
			for(int i=0; i<parts.length; i++)
				if(parts[i].getSerial() == toFind.getSerial())
					return parts[i];
		
		//Part not found in current layer
		
		if(assemblies != null) //Search child assemblies
			for(int i=0; i<assemblies.length; i++)
				if(assemblies[i].findPartBySerial(toFind) != null)
					return assemblies[i].findPartBySerial(toFind);
			
		return null; //Part not found anywhere
		
	}//End findPartBySerial()
	
	public Part findPartBySerialFromSupply(Part toFind){
		
		if(parts != null)	//If parts exist in current layer
			for(int i=0; i<parts.length; i++)
				if(parts[i].getSerial() == toFind.getSerial())
					return parts[i];
			
		return null; //Part not found anywhere
		
	}//End findPartBySerial()
	
	public Part findPartByName(Part toFind){
		
		if(parts != null)
			for(int i=0; i<parts.length; i++)
				if(parts[i].getName().equals(toFind.getName()))
					return parts[i];
		
		return null; //Part not found
		
	}//End findPartByName();

	
	public Part findCheapestPartBySerialFromSupply(Part toFind){
		
		Part[] results = null;
		if(parts != null)
			for(int i=0; i<parts.length; i++)
				if(parts[i].getSerial() == toFind.getSerial())
					results = ArrayManager.addToArray(parts[i], results);
		
		if(results == null) return null;
		
		results = new Sorter<Part>().quickSort(results);
	
		return results[0];
	}
	
	public Part[] findAllPartsBySerialFromSupply(Part toFind){
		
		Part[] toRet = null;
		if(parts != null)
			for(int i=0; i<parts.length; i++)
				if(parts[i].getSerial() == toFind.getSerial())
					toRet = ArrayManager.addToArray(parts[i], toRet);
		
		toRet = new Sorter<Part>().quickSort(toRet);
		
		return toRet;
	}
	
	public Part[] findAllPartsBySerialAndStatusFromSupply(Part fin, String stat){
		
		Part[] pt = findAllPartsBySerialFromSupply(fin);
		
		if(pt  == null) return pt;
		
		Part[] toRet = null;
		for(int i=0; i<pt.length; i++)
			if(pt[i].getStatus().equals(stat))
				toRet = ArrayManager.addToArray(pt[i], toRet);
		
		return toRet;
	}
	
	public Assembly findExactAssemblyFromSupply(Assembly toFind){
		
		if(assemblies != null)
			for(int i=0; i<assemblies.length; i++)
				if(assemblies[i].toString().equals(toFind.toString()))
					return assemblies[i];
		
		return null;
	}
	
	public Assembly findEquivalentAssemblyFromSupply(Assembly toFind){
		
		if(assemblies != null)
			for(int i=0; i<assemblies.length; i++)
				if(assemblies[i].equivalent(toFind))
					return assemblies[i];
		
		return null;
		
	}//End findEquivalentAssemblyFromSupply()
	
	public Assembly[] findAllAssembliesBySerialFromSupply(Assembly toFind){
		
		Assembly[] toRet = null;
		if(assemblies != null)
			for(int i=0; i<assemblies.length; i++)
				if(assemblies[i].getSerial() == toFind.getSerial())
					toRet = ArrayManager.addToArray(assemblies[i], toRet);
		
		toRet = new Sorter<Assembly>().quickSort(toRet);
		
		return toRet;
	}
	
	public Assembly[] finAllAssemsBySerialAndStatusFromSupply(Assembly fin, String stat){
		
		Assembly[] as = findAllAssembliesBySerialFromSupply(fin);
		
		if(as == null) return as;
		
		Assembly[] toRet = null;
		for(int i=0; i<as.length; i++)
			if(as[i].getStatus().equals(stat))
				toRet = ArrayManager.addToArray(as[i], toRet);
		
		return toRet;
	}
	
	
	//----- Interfaces
	///////////////////////////////////////////////////////////
	public String record(){ 
		String aArray="#A", pArray="#P";
		if(assemblies != null){
			for(int i=0; i<assemblies.length; i++)
				aArray = aArray.concat(assemblies[i].record());
			aArray = aArray.concat("]A");
		}else
			aArray = aArray.concat("null]A");
		
		if(parts != null){
			for(int i=0; i<parts.length; i++)
				pArray = pArray.concat(parts[i].record());
		}else
			pArray =  pArray.concat("null");
		
		String d = (datePurchased == null)? "#dnull" : datePurchased.record();
		String d2 = (dateListed == null)? "#dnull" : dateListed.record();
		String d3 = (dateSold == null)? "#dnull" : dateSold.record();
		String c = (listPrice == null)? "#cnull" : listPrice.record();
		String c2 = (listFee == null)? "#cnull" : listFee.record();
		String c3 = (finalValueFee == null)? "#cnull" : finalValueFee.record();
		String c4 = (payPalFee == null)? "#cnull" : payPalFee.record();
		String c5 = (shipCost == null)? "#cnull" : shipCost.record();
		String c6 = (shipPaid == null)? "#cnull" : shipPaid.record();
		
		return String.format("#a%d&%d&%s&%s&%s&%s&%s%s%s%s%s%s%s%s%s%s%s%s", 
				serial, quantity, duration, parentCategory, name, status, color, 
				d, d2, d3, c, c2, c3, c4, c5, c6, aArray, pArray);
	}
	
	public String getData(){
		String ret = "";
		
		if(color != null)
			if(!color.equals(Assembly.NO_COLOR))
				ret = ret.concat("  Color: " + color + "\n");
		
		ret = ret.concat("  Unit Cost: " + getUnitPrice().toString() + "\n");
		
		if(quantity > 1)
			ret = ret.concat("  Quantity: " + quantity + "\n");
		
		if(!status.equals(Assembly.DEFAULT_TEMPLATE) 
				&& !status.equals(Assembly.PART_TEMPLATE)){
			
			ret = ret.concat("\n  Status: " + status + "\n");
			
				
			if(status.equals(Assembly.SUPPLY) 
					|| status.equals(Assembly.INCOMING))
				ret = ret.concat("  Date Purchased: " + datePurchased.displaySimpleDate() + "\n");
				
			if(status.contains("Listed")){
				ret = ret.concat("  Date Lised: " + dateListed.displaySimpleDate() + "\n");
				ret = ret.concat("  List Amount: " + listPrice.toString() + "\n");
				ret = ret.concat("  Listing Fee: " + listFee.toString() + "\n");
				int rem = dateListed.getDateInDays() + duration - Date.getTodaysDate().getDateInDays();
				ret = ret.concat("  Days Remaining: " + rem + "\n");
			}
			if(status.equals(Assembly.SOLD)){
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
			
		}
		
		return ret + "\n  " + serial;
	}
	
	public String toString(){ 
		if(this.getQuantity() != 1)
			return name + " (" + quantity + ")";
		return name; 
	}

	@Override
	public int compareTo(Assembly o) {
		return getUnitPrice().compareTo(o.getUnitPrice());
	}
	
	//TODO Temporary
	public void printContents(){
		System.out.println("   Parts");
		if(parts != null)
			for(int i=0; i<parts.length; i++)
				System.out.println(parts[i].getName() + " " + parts[i].getQuantity());
		
		System.out.println("   Assemblies");
		if(assemblies != null)
			for(int i=0; i<assemblies.length; i++)
				assemblies[i].printContents();
	}
}
