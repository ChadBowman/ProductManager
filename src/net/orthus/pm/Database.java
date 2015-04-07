package net.orthus.pm;



public class Database {
	//----- Application Version
	public static final String VERSION = "2.3";
		
	//----- Variables
	//Product Categories
	private static Category[] categories;
	private static ServiceCategory[] serviceCategories;
	private static ProductCategory[] productCategories;
	private static Assembly shippingMaterials;
	//Balances
	private static Credit payPalBalance = new Credit();
	private static Credit[] ebayBalance = new Credit[12]; //Individual Credit representing each month's invoice
	private static boolean invoicePaid; //If last months invoice has been paid
	private static Credit undeliveredIncome = new Credit();
	private static Credit subscription = new Credit();
	private static TimeStamp[] stamps;
	//Personal Utility
	private static Credit investmentGoal = new Credit();
	private static double reinvestmentRate;
	private static double profitPercentageGoal;
	private static Credit salaryGoal = new Credit();
	//Utility
	private static int lastSerial = Integer.MAX_VALUE;
	private static int freeListingsAllotted;
	private static int saleQuantityAllotted;
	private static Credit saleAmmountAllotted = new Credit();
	private static double payPalFeeRate;
	private static boolean topRatedSeller;
	private static int reorderLookBack;
	private static int reorderTollerance;
	private static Credit totalInvestment;
	private static String productDescriptionTemplate;
	private static String partDescriptionTemplate;
	private static String genDescriptionTemplate;
	public static boolean onTheClock = false;
	
	//Class utility
	private static Date lastLogin; //Not intended for use outside of prepare()
	private static String lastSavePath;
	private static String sessionPassword = "Orthus";
	
	//----- Standard Methods
	//Getters
	public static Credit getPayPalBalance(){ return payPalBalance; }
	public static Credit[] getEbayBalances(){ return ebayBalance; }
	public static Credit getUndeliveredIncome(){ return undeliveredIncome; }
	public static Credit getInvestmentGoal(){ return investmentGoal; }
	public static double getReinvestmentRate(){ return reinvestmentRate; }
	public static double getProfitPercentageGoal(){ return profitPercentageGoal; }
	public static int getLastSerial(){ return lastSerial; }
	public static int getFreeListingsAllotted(){ return freeListingsAllotted; }
	public static int getSaleQuantityAllotted(){ return saleQuantityAllotted; }
	public static Credit getSaleAmmountAllotted(){ return saleAmmountAllotted; }
	public static double getPayPalFeeRate(){ return payPalFeeRate; }
	public static boolean isTopRatedSeller(){ return topRatedSeller; }
	public static Category[] getCategories(){ return categories; }
	public static ServiceCategory[] getServiceCategories(){ return serviceCategories; }
	public static ProductCategory[] getProductCategories(){ return productCategories; }
	public static Date getLastLogin(){ return lastLogin; }
	public static String getLastSavePath(){ return lastSavePath; }
	public static String getPassword(){ return sessionPassword; }
	public static int getReorderTollerance() { return reorderTollerance; }
	public static boolean ifInvoicePaid(){ return invoicePaid; }
	public static int getReorderLookBack() { return reorderLookBack; }
	public static Credit getSubscription(){ return subscription; } 
	public static Credit getSalaryGoal(){ return salaryGoal; } 
	public static Credit getTotalInvestment(){ return totalInvestment; }
	public static TimeStamp[] getTimeStamps(){ return stamps; }
	public static String getProductDescriptionTemplate(){ return productDescriptionTemplate; }
	public static String getPartDescriptionTemplate(){ return partDescriptionTemplate; }
	public static String getGenericDescriptionTemplate(){ return genDescriptionTemplate; }
	public static Assembly getShippingMats(){ return shippingMaterials; }
	
	//Setters
	public static void setPayPalBalance(Credit x){ payPalBalance = x; }
	public static void setEbayBalances(Credit[] x){ ebayBalance = x; }
	public static void setUndeliveredIncome(Credit x){ undeliveredIncome = x; }
	public static void setInvoicePaid(Boolean x){ invoicePaid = x; }
	public static void setInvestmentGoal(Credit x){ investmentGoal = x; }
	public static void setReinvestmentRate(double x){ reinvestmentRate = x; }
	public static void setProfitPercentageGoal(double x){ profitPercentageGoal = x; }
	public static void setLastSerial(int x){ lastSerial = x; }
	public static void setFreeListingsAllotted(int x){ freeListingsAllotted = x; }
	public static void setSaleQuantitiyAllotted(int x){ saleQuantityAllotted = x; }
	public static void setSaleAmmountAllotted(Credit x){ saleAmmountAllotted = x; }
	public static void setPayPalFeeRate(double x){ payPalFeeRate = x; }
	public static void setTopRatedSeller(boolean x){ topRatedSeller = x; }
	public static void setLastLogin(Date x){ lastLogin = x; }
	public static void setCategories(Category[] x){ categories = x; }
	public static void setServiceCategories(ServiceCategory[] x){ serviceCategories = x; }
	public static void setProductCategories(ProductCategory[] x){ productCategories = x; }
	public static void setLastSavePath(String x){ lastSavePath = x; }
	public static void setPassword(String x){ sessionPassword = x; }
	public static void setReorderTollerance(int x) { reorderTollerance = x; }
	public static void setReorderLookBack(int x) { reorderLookBack = x; }
	public static void setSubscription(Credit x){ subscription = x; }
	public static void setSalaryGoal(Credit x){ salaryGoal = x; }
	public static void setTotalInvestment(Credit x){ totalInvestment = x; }
	public static void setTimeStamps(TimeStamp[] s){ stamps = s; }
	public static void setProductDescriptionTemplate(String s){ productDescriptionTemplate = s; }
	public static void setPartDescriptionTemplate(String s){ partDescriptionTemplate = s; }
	public static void setGenDescriptionTemplate(String s){ genDescriptionTemplate = s; }
	public static void setShippingMats(Assembly x){ shippingMaterials = x; }
	
	//----- Advanced Methods
	//MUST be ran at the start of program AFTER data acquisition
	public static void prepare(){
		if(lastLogin != null)
			if(Date.getTodaysDate().getMonth() > lastLogin.getMonth()){ //If start of a new month
				ebayBalance[Date.getTodaysDate().getMonth() - 1] = subscription;
				invoicePaid = false;
			}
		lastLogin = Date.getTodaysDate();
	}
	
	public static int getNewSerial(){
		return --lastSerial;
	}
	
	/////////
	
	public static void addToUndeliveredIncome(Credit x){ 
		if(undeliveredIncome == null)
			undeliveredIncome = x;
		else
			undeliveredIncome.add(x); 
	}
	
	public static void addProductCategory(ProductCategory newCat){
		productCategories = ArrayManager.addToArray(newCat, productCategories);
	}
	
	public static void addServiceCategory(ServiceCategory newCat){
		serviceCategories = ArrayManager.addToArray(newCat, serviceCategories);
	}
	
	public static void addTimeStamp(TimeStamp add){
		stamps = ArrayManager.addToArray(add, stamps);
	}
	
	public static Category getFocusedCategory(){

		String s = GUIEngine.getParentTPane().getTitleAt(GUIEngine.getParentTPane().getSelectedIndex());
		
		if(categories != null)
			for(int i=0; i<categories.length; i++)
				if(categories[i].getName().equals(s))
					return categories[i];
		if(serviceCategories != null)
			for(int i=0; i<serviceCategories.length; i++)
				if(serviceCategories[i].getName().equals(s))
					return serviceCategories[i];
		if(productCategories != null)
			for(int i=0; i<productCategories.length; i++)
				if(productCategories[i].getName().equals(s))
					return productCategories[i];
		
		return null;		
	}
	
	
	//----- Ebay Operations
	//Returns Null if no data available or input out of bounds.
	public static Credit getEbayBalance(int month){
		
		if(month > 0 && month < 13)
			return ebayBalance[month - 1];
		else if(month == 0)
			return ebayBalance[11];
		else
			return null;
		
	}
	
	public static void addToEbayBalance(int month, Credit amt){
		if(month > 0 && month < 13)
			if(ebayBalance[month - 1] == null)
				ebayBalance[month - 1] = amt;
			else
				ebayBalance[month - 1].add(amt);
	}
	
	public static void setEbayBalance(int month, Credit amt){
		if(month > 0 && month < 13)
			ebayBalance[month - 1] = amt;
	}
	
	public static void addToCurrentEbayBalance(Credit toAdd){
		int m = Date.getTodaysDate().getMonth() - 1;
		if(ebayBalance[m] == null)
			ebayBalance[m] = toAdd;
		else
			ebayBalance[m].add(toAdd); 
	}
	
	//----- PayPal Operations
	public static void subtractFromPayPalBalance(Credit x){
		payPalBalance.subtract(x);
	}
	
	public static void addToPayPalBalance(Credit x){
		payPalBalance.add(x); 
	}
	

	/*
	 * This is a note
	 */
	public static boolean isTopRatedSeller(Date d){
		int month = d.getMonth();
		
		if(Date.getTodaysDate().getMonth() == month)
			return topRatedSeller;
		
		if(productCategories != null)
			for(int i=0; i<productCategories.length; i++){
				Product[] p = productCategories[i].getProductsByStatus(Product.SOLD);
				Repair[] r = productCategories[i].getAllRepairs();
				
				if(p != null)
					for(int j=0; j<p.length; j++)
						if(p[j].getDateSold().getMonth() == month)
							return p[j].isDiscounted();
				
				if(r != null)
					for(int j=0; j<r.length; j++)
						if(r[j].getDateSold().getMonth() == month)
							return r[j].isDiscounted();
			}
		
		int sel = OptionPane.showConfirmYes("Were you are Top Rated Seller in " 
				+ Date.getTodaysDate().getMonthString() + "?", 
				"Top Rated Status");
		
		if(sel == 0) return true;
		
		return false;
	}
	
	
	public static ServiceCategory[] getAllServiceCategories(){
		if(serviceCategories == null && productCategories == null) return null;
		
	
		int length = 0;
		if(serviceCategories != null)
			length += serviceCategories.length;
		if(productCategories != null)
			length += productCategories.length;
		
		ServiceCategory[] ret = new ServiceCategory[length];
		
		int i = 0;
		if(serviceCategories != null)
			for(; i<serviceCategories.length; i++)
				ret[i] = serviceCategories[i];
		
		if(productCategories != null)
			for(int j=0; j<productCategories.length; j++, i++)
				ret[i] = productCategories[j];
		
		return ret;
	}

	
}//End Database
