package net.orthus.pm;


public class ServiceCategory extends Category {
	//----- Variables
	protected double serviceFinalValueFeeRate;       //eBay final value fee proportion
	protected Service[] services;
	protected ShippingOption[] shippingOptions;
	protected UseRecord[] records;

	//----- Constructors
	//FULL Constructor
	public ServiceCategory(String name, 
						   double serviceFinalValueFeeRate,
						   Assembly[] templates,
						   Assembly[] assemblies, 
						   Service[] services,
						   ShippingOption[] shippingOptions,
						   UseRecord[] records) {
		
		super(name, templates, assemblies);
		this.serviceFinalValueFeeRate = serviceFinalValueFeeRate;
		this.services = services;
		this.shippingOptions = shippingOptions;
		this.records = records;
	}
	
	public ServiceCategory(String name, 
			   double serviceFinalValueFeeRate,
			   Assembly[] templates,
			   Assembly[] assemblies, 
			   Service[] services,
			   ShippingOption[] shippingOptions) {

		super(name, templates, assemblies);
		this.serviceFinalValueFeeRate = serviceFinalValueFeeRate;
		this.services = services;
		this.shippingOptions = shippingOptions;
	}
	
	//----- Standard Methods
	//Getters
	public Service[] getServices(){ return services; }
	public ShippingOption[] getShippingOptions(){ return shippingOptions; }
	public String getAddress(){ return "S" + name; }
	public UseRecord[] getRecords(){ return records; }
	public double getServiceFinalValueFeeRate(){ return serviceFinalValueFeeRate; }
	//Setters
	public void setServiceFinalValueFeeRate(double x){ serviceFinalValueFeeRate = x; }
	public void setServices(Service[] x){ services = x; }
	public void setShippingOptions(ShippingOption[] x){ shippingOptions = x; }
	public void setRecords(UseRecord[] x){ records = x; }

	
	//----- Advanced Methods
	
	//Shipping Options
	public void addShippingOption(ShippingOption opt){
		shippingOptions = ArrayManager.addToArray(opt, shippingOptions);
	}
	
	public void removeShippingOption(ShippingOption opt){
		shippingOptions = ArrayManager.removeFromArray(opt, shippingOptions);
	}
	
	public void addUseRecord(UseRecord rec){
		records = ArrayManager.addToArray(rec, records);
	}
	
	public String[] getShippinOptionNames(){
		if(shippingOptions == null) return null;
		
		String[] str = new String[shippingOptions.length + 1];
		for(int i=0; i<shippingOptions.length; i++)
			str[i] = shippingOptions[i].getName();
		
		 str[str.length - 1] = "Custom";
		
		return str;
	}

	public Part[] getShippingPartTemplates(){
		
		Part[] toRet = null;
		
		if(shippingOptions == null) return toRet;
		
		for(int i=0; i<shippingOptions.length; i++)
			for(int j=0; j<shippingOptions[i].getMaterials().getParts().length; j++)
				if(checkSerialMatch(shippingOptions[i].getMaterials().getParts()[j], toRet))
					toRet = ArrayManager.addToArray(
							shippingOptions[i].getMaterials().getParts()[j],
							toRet);
		
		return toRet;
	}
	
	private boolean checkSerialMatch(Part p, Part[] ps){
		
		if(ps == null) return true;
		
		for(int i=0; i<ps.length; i++)
			if(ps[i].getSerial() == p.getSerial())
				return false;
		
		return true;
	}
	
	
	public Repair[] getAllRepairs(){
		if(services == null) return null;
		Repair[] toReturn = null;
		for(int i=0; i<services.length; i++)
			if(services[i].getRepairs() != null)
				for(int j=0; j<services[i].getRepairs().length; j++)
					toReturn = ArrayManager.addToArray(services[i].getRepairs()[j], toReturn);
		return toReturn;
	}
	
	public Repair[] getAllRepairsByStatus(String status){
		if(services == null) return null;
		Repair[] toReturn = null;
		for(int i=0; i<services.length; i++)
			if(services[i].getRepairs() != null)
				for(int j=0; j<services[i].getRepairs().length; j++)
					if(services[i].getRepairs()[j].getStatus().equals(status))
						toReturn = ArrayManager.addToArray(services[i].getRepairs()[j], toReturn);
		return toReturn;
	}
	
	public void addService(Service newService){
		services = ArrayManager.addToArray(newService, services);
	}
	
	public void removeService(Service toRemove){
		services = ArrayManager.removeFromArray(toRemove, services);
	}
	
	
	//----- Interfaces
	
	public String record(){ 
		String temp = "#X", asse = "#X", serv = "#S", opti = "#O", rec = "#U";
		if(templates != null){
			for(int i=0; i<templates.length; i++)
				temp = temp.concat("#x" + templates[i].record());
		}else
			temp = temp.concat("null");
		
		if(assemblies != null){
			for(int i=0; i<assemblies.length; i++)
				asse = asse.concat("#x" + assemblies[i].record());
		}else
			asse = asse.concat("null");
		
		if(services != null){
			for(int i=0; i<services.length; i++)
				serv = serv.concat(services[i].record());
		}else
			serv = serv.concat("null");
		
		if(shippingOptions != null){
			for(int i=0; i<shippingOptions.length; i++)
				opti = opti.concat(shippingOptions[i].record());
		}else
			opti = opti.concat("null");
		
		if(records != null){
			for(int i=0; i<records.length; i++)
				rec = rec.concat(records[i].record());
		}else
			rec = rec.concat("null");
		
		return String.format("#l%s&%f%s%s%s%s%s", 
				name, serviceFinalValueFeeRate, temp, asse, serv, opti, rec);
	}
	
}	
