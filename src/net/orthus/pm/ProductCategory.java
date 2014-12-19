package net.orthus.pm;



public class ProductCategory extends ServiceCategory{ 
	//----- Variables
	private double productFinalValueFeeRate;
	private String[] colors;				//Available product colors
	private Product[] products; 	 	    //Array of physical products
	
	//----- Constants
	public static final String DS_LITE = "DS Lite";
	public static final String DSI = "DSi";
	public static final String DSI_XL = "DSi XL";
	public static final String DS3 = "3DS";
	public static final String DS3_XL = "3DS XL";
	
	/////////////////////////////////////////////////////

	//----- Constructors
	//FULL Constructor
	public ProductCategory(String name,
			   double serviceFinalValueFeeRate,
			   double productFinalValueFeeRate,
			   Assembly[] templates,  //Default assembly
			   Assembly[] assemblies, //Parts pool
			   Service[] services,    //Current services
			   String[] colors,  	  //Available Colors
			   Product[] products,
			   ShippingOption[] options,
			   UseRecord[] record) {  //Current Products

		super(name, serviceFinalValueFeeRate, templates, assemblies, services, options, record);
		
		this.productFinalValueFeeRate = productFinalValueFeeRate;
		this.colors = colors;
		this.products = products;
	}
	
	
	public ProductCategory(String name,
						   double serviceFinalValueFeeRate,
						   double productFinalValueFeeRate,
						   Assembly[] templates,  //Default assembly
						   Assembly[] assemblies, //Parts pool
						   Service[] services,    //Current services
						   String[] colors,  	  //Available Colors
						   Product[] products,
						   ShippingOption[] options) {  //Current Products
		
		super(name, serviceFinalValueFeeRate, templates, assemblies, services, options);
		
		this.productFinalValueFeeRate = productFinalValueFeeRate;
		this.colors = colors;
		this.products = products;
	}

	/////////////////////////////////////////////////////
	
	//----- Standard Methods
	public String[] getColors(){ return colors; }
	public Product[] getProducts(){ return products; }
	public String getAddress(){ return "P" + name; }
	//Setters
	public void setProductFinalValueFeeRate(double x){ productFinalValueFeeRate = x; }
	public void setColors(String[] x){ colors = x; }
	
	public void addProduct(Product newProduct){	products = ArrayManager.addToArray(newProduct, products); }
	public void removeProduct(Product toRemove){
		products = ArrayManager.removeFromArray(toRemove, products);
	}
	
	
	/////////////////////////////////////////////////////
	
	//----- Advanced Methods
	//Returns the next, untaken ID in product sequence
	public int getNextID(){
		if(products == null)	//Base case
			return 1;
		
		int counter = 1;
		boolean flag;
		do{
			flag = false;							//Assume no match
			counter++;								//Increment for new value
			for(int i=0; i<products.length; i++)	//For each product
				if(products[i].getID() == counter)  //If there exists match
					flag = true;					//Proceed
		}while(flag);								//If no match, exit loop
		
		return counter;								//Return value
	}
	
	public double getProductFinalValueFeeRate(Date d){ 
		
		if(Database.isTopRatedSeller(d))
			return 0.8 * productFinalValueFeeRate;
		return productFinalValueFeeRate;
	}
	
	public double getProductFinalValueFeeRate(){
		return productFinalValueFeeRate;
	}
	
	/////////////////////////////////////////////////////
	
	public Product[] getProductsByColor(String color){
		Product[] toReturn = null;
		if(products == null) return toReturn;
		
		for(int i=0; i<products.length; i++)
			if(products[i].getOverallColor().equals(color))
				toReturn = ArrayManager.addToArray(products[i], toReturn);
		
		return toReturn;
	}
	
	public Product[] getProductsByStatus(String status){
		Product[] toReturn = null;
		if(products == null) return toReturn;
		
		for(int i=0; i<products.length; i++)
			if(products[i].getStatus().equals(status))
				toReturn = ArrayManager.addToArray(products[i], toReturn);
		
		return toReturn;
	}
	
	//Returns all products which dont match the status
	public Product[] getExcludedProducts(String status){
		
		//TODO
		
		return null;
	}
	
	/////////////////////////////////////////////////////
	
	public Credit getAverageSale(){
		
		Credit toRet = new Credit();
		Product[] sold = getProductsByStatus(Product.SOLD);
		
		if(sold == null){
			
			switch(name){ //Use conservative estimates
			case DS_LITE: toRet.add(57.00); break;
			case DSI: toRet.add(60.00);	 break;
			case DSI_XL: toRet.add(80.00);  break;
			case DS3: toRet.add(130.00);	 break;
			case DS3_XL: toRet.add(190.00); break;
			}
			
		}else{
			
			for(int i=0; i<sold.length; i++)
				toRet.add(sold[i].getSellAmount());
			
			toRet.divide(sold.length);
		}
		
		return toRet;
	}
	
	
	public Credit getAverageSale(String color){
		
		Credit toRet = new Credit();
		Product[] sold = getProductsByStatus(Product.SOLD);
		
		if(sold == null)
			toRet = getAverageSale();
		else{
			int count = 0;
			for(int i=0; i<sold.length; i++)
				if(sold[i].getOverallColor().equals(color)){
					toRet.add(sold[i].getSellAmount());
					count++;
				}
			if(count != 0)
				toRet.divide(count);
			else
				toRet = getAverageSale();
		}
		
		return toRet;
	}
	
	public Credit getAverageSale(String color, String quality){
		return null; //TODO
	}
	
	public Credit getAverageShippingDifference(){
		
		//TODO change to find most used shipping option
		
		Credit toRet = new Credit();
		Product[] sold = getProductsByStatus(Product.SOLD);
		
		if(sold != null){
			for(int i=0; i<sold.length; i++)
				toRet.add(sold[i].getShippingCostDifference());
			
			toRet.divide(sold.length);
		}else
			toRet.add(5.0);
		
		return toRet;
		
	}//End getAverageShippingDifference()
	
	/////////////////////////////////////////////////////
	
	//----- Interfaces
	public String record(){ 
		String temp = "#X", asse = "#X", serv = "#S", colo = "#Y", prod = "#T", opti = "#O", rec = "#U";
		
		if(templates != null){
			if(templates.length > 0)
				for(int i=0; i<templates.length; i++)
					temp = temp.concat("#x" + templates[i].record());
			else
				temp = temp.concat("null");
		}else
			temp = temp.concat("null");
		
		if(assemblies != null){
			if(assemblies.length > 0)
				for(int i=0; i<assemblies.length; i++)
					asse = asse.concat("#x" + assemblies[i].record());
			else
				asse = asse.concat("null");
		}else
			asse = asse.concat("null");
		
		if(services != null){
			if(services.length > 0)
				for(int i=0; i<services.length; i++)
					serv = serv.concat(services[i].record());
			else
				serv = serv.concat("null");
		}else
			serv = serv.concat("null");
		
		if(colors != null){
			if(colors.length > 0)
				for(int i=0; i<colors.length; i++)
					colo = colo.concat(colors[i] + "&");
			else
				colo = colo.concat("null");
		}else
			colo = colo.concat("null");
		
		if(products != null){
			if(products.length > 0)
				for(int i=0; i<products.length; i++)
					prod = prod.concat(products[i].record());
			else
				prod = prod.concat("null");
		}else
			prod = prod.concat("null");
		
		if(shippingOptions != null){
			if(shippingOptions.length > 0)
				for(int i=0; i<shippingOptions.length; i++)
					opti = opti.concat(shippingOptions[i].record());
			else
				opti = opti.concat("null");
		}else
			opti = opti.concat("null");
		
		if(records != null){
			if(records.length > 0)
				for(int i=0; i<records.length; i++)
					rec = rec.concat(records[i].record());
			else
				rec = rec.concat("null");
		}else
			rec = rec.concat("null");
		
		return String.format("#m%s&%f&%f%s%s%s%s%s%s%s",
				name, serviceFinalValueFeeRate, productFinalValueFeeRate, 
				temp, asse, serv, colo, prod, opti, rec);
	}
}
