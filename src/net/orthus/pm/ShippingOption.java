package net.orthus.pm;


public class ShippingOption {
	
	//----- Variables
	private String name;
	private Credit cost, paid;
	private Assembly materials;
	
	//----- Constructor
	//FULL Constructor
	public ShippingOption(String name, Credit cost, Credit paid, Assembly materials) {
		this.name = name;
		this.cost = cost;
		this.paid = paid;
		this.materials = materials;
	}
	
	//----- Standard Methods
	//Getters
	public String getName(){ return name; }
	public Credit getCost(){ return cost; }
	public Credit getPaid(){ return paid; }
	public Assembly getMaterials(){ return materials; }
	
	//Setters
	public void setName(String name){ this.name = name; }
	public void setCost(Credit cost){ this.cost = cost; }
	public void setPaid(Credit paid){ this.paid = paid; }
	public void setMaterials(Assembly materials){ this.materials = materials; }
	
	
	//----- Advanced Methods
	public Credit useOption(Category cat, Date d){
		
		Assembly def = Database.getShippingMats();
		
		//Warning: def.findPart could find part with wrong status, causing problems
		Credit cst = new Credit();
		if(materials.getParts() != null)
			for(int i=0; i<materials.getParts().length; i++){
				
				Part p = def.findPartBySerial(materials.getParts()[i]); //Find part
				
				cst.add(p.getPrice().getValueInDollars()
						* materials.getParts()[i].getQuantity());
		
				p.setQuantity(p.getQuantity() - materials.getParts()[i].getQuantity());
				
				((ProductCategory) cat).addUseRecord(new UseRecord(materials.getParts()[i].getSerial(),
						materials.getParts()[i].getQuantity(), d));
			}
		
		cst.add(cost);
		return cst;
	}
	
	public String record(){
		String c = (cost == null)? "#cnull" : cost.record();
		String p = (paid == null)? "#cnull" : paid.record();
		String m = (materials == null)? "#xnull" : "#x" + materials.record();
		return String.format("#o%s%s%s%s", name, c, p, m); 
	}
	
	public boolean enoughParts(Assembly def){
		if(def == null) return false;
		if(def.getParts() == null) return false;
		
		Part[] ps = materials.getParts();
	
		boolean flag = false;
		if(ps != null)
			for(int i=0; i<ps.length; i++){
				
				flag = false;
				for(int j=0; j<def.getParts().length; j++)
					if(ps[i].getSerial() == def.getParts()[j].getSerial())
						flag = true;
				
				if(!flag) return false; //Part not found or not in supply
				
			}
		
		
		return true;
	}

}
