package net.orthus.pm;


public class Service {
	//----- Variables
	private String parentAddress;
	private int listDuration, quantity;
	private String name, listType;
	private Credit listAmount, listFee;
	private Date dateListed;
	private Assembly defaultAssembly;
	private boolean discount, autoList;
	private Repair[] repairs;
	
	//----- Constructors
	//FULL Constructor
	public Service(
			String parentAddress, 
			int listDuration,
			int quantity,
			boolean discount,
			boolean autoList,
			String name, 
			String listType,
			Credit listAmount, 
			Credit listFee, 
			Date dateListed, 
			Assembly defaultAssembly,
			Repair[] repairs) {
		
		this.parentAddress = parentAddress;
		this.listDuration = listDuration;
		this.name = name;
		this.listType = listType;
		this.listAmount = listAmount;
		this.quantity = quantity;
		this.listFee = listFee;
		this.dateListed = dateListed;
		this.defaultAssembly = defaultAssembly;
		this.discount = discount;
		this.autoList = autoList;
		this.repairs = repairs;
	}
	
	//----- Standard Methods
	//Getters
	public String getParentAddress(){ return parentAddress; }
	public int getListDuration(){ return listDuration; }
	public int getQuantity(){ return quantity; } 
	public String getName(){ return name; }
	public String getListType(){ return listType; }
	public Credit getListAmount(){ return listAmount; }
	public Credit getListFee(){ return listFee; }
	public Date getDateListed(){ return dateListed; }
	public Assembly getDefaultAssembly(){ return defaultAssembly; }
	public boolean isDiscounted(){ return discount; }
	public boolean isAutoList(){ return autoList; }
	public Repair[] getRepairs(){ return repairs; }
	public String getAddress(){ return parentAddress + "." + name; }
	//Setters
	public void setParentAddress(String x){ parentAddress = x; }
	public void setListDuration(int x){ listDuration = x; }
	public void setQuantity(int x){ quantity = x; }
	public void setName(String x){ name = x; }
	public void setListType(String x){ listType = x; }
	public void setListFee(Credit x){ listFee = x; }
	public void setListAmount(Credit x){ listAmount = x; }
	public void setDateListed(Date x){ dateListed = x; }
	public void setDefaultAssembly(Assembly x){ defaultAssembly = x; }
	public void setDiscounted(boolean x){ discount = x; }
	public void setAutoList(boolean x){ autoList = x; }
	//NOTE: Repairs is not intended to be replaced
	
	//----- Advanced Methods
	public String record(){
		String rep = "#R";
		if(repairs != null){
			for(int i=0; i<repairs.length; i++)
				rep = rep.concat(repairs[i].record());
		}else
			rep = rep.concat("null");
		
		String la = (listAmount == null)? "#cnull" : listAmount.record();
		String lf = (listFee == null)? "#cnull" : listFee.record();
		String dl = (dateListed == null)? "#dnull" : dateListed.record();
		String a = (defaultAssembly == null)? "#xnull" : "#x" + defaultAssembly.record();
		
		return String.format("#s%s&%d&%d&%b&%b&%s&%s%s%s%s%s%s",
				parentAddress, listDuration, quantity, discount, autoList, name, listType, la, lf, dl, a, rep);
		
	}//End record()
	
	public void addRepair(Repair toAdd){
		repairs = ArrayManager.addToArray(toAdd, repairs);
	}
	
	public void removeRepair(Repair toRemove){
		repairs = ArrayManager.removeFromArray(toRemove, repairs);
	}
	
	public ServiceCategory getServiceCategory(){ 
		if(parentAddress.charAt(0) != 'S') return (ServiceCategory) getProductCategory();
	
		ServiceCategory[] arrayToSearch = Database.getServiceCategories();
		if(arrayToSearch != null)
			for(int i=0; i<arrayToSearch.length; i++)
				if(arrayToSearch[i].getAddress().equals(parentAddress))
					return arrayToSearch[i];
		return null;
	}
	
	public ProductCategory getProductCategory(){ 
		if(parentAddress.charAt(0) != 'P') return null;
	
		ProductCategory[] arrayToSearch = Database.getProductCategories();
		if(arrayToSearch != null)
			for(int i=0; i<arrayToSearch.length; i++)
				if(arrayToSearch[i].getAddress().equals(parentAddress))
					return arrayToSearch[i];
		return null;
	}
	
	public int getDaysRemaining(){
		if(dateListed == null) return 0;
		return listDuration - (Date.getTodaysDate().getDateInDays() - dateListed.getDateInDays());
	}
	
	public Repair[] getRepairsByStatus(String status){
		if(repairs == null) return null;
		Repair[] toReturn = null;
		for(int i=0; i<repairs.length; i++)
			if(repairs[i].getStatus().equals(status))
				toReturn = ArrayManager.addToArray(repairs[i], toReturn);
		return toReturn;
	}
	
	public int getNextID(){
		if(repairs == null)	//Base case
			return 1;
		
		int counter = 1;
		boolean flag;
		do{
			flag = false;							//Assume no match
			counter++;								//Increment for new value
			for(int i=0; i<repairs.length; i++)	//For each product
				if(repairs[i].getID() == counter)  //If there exists match
					flag = true;					//Proceed
		}while(flag);								//If no match, exit loop
		
		return counter;								//Return value
	}
	
	public boolean equals(Service s){
		if(record().equals(s.record()))
			return true;
		return false;
	}

}
