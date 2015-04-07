package net.orthus.pm;

import java.util.ArrayList;


public class Category {
	//----- Variables
	protected String name;		//Ex: "DS Lite" or "Shipping" or "Battery Repair"
	protected Assembly[] assemblies;  //Inventory of availably, single parts
	protected Assembly[] templates;	  //Default template as well as Part-ordering templates (i.e. shells)

	//----- Constants
	
	//----- Constructors
	//FULL Constructor
	public Category(String name,
					Assembly[] templates,
					Assembly[] assemblies) {
		
		this.name = name;
		this.templates = templates;
		this.assemblies = assemblies;
	}
	
	//----- Standard Methods
	//Getters
	public String getName(){ return name; }
	public Assembly[] getTemplates(){ return templates; }
	public Assembly[] getAssemblies(){ return assemblies; }
	public String getAddress(){ return "C" + name; }
	//Setters
	public void setName(String name){ this.name = name; }
	public void setTemplates(Assembly[] x){ this.templates = x; }
	
	//----- Advanced Methods
	public String record(){ 
		String temp = "#X", asse = "#X";
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
		
		return String.format("#k%s%s%s", name, temp, asse);
	}
	
	//Returns Default Template 
	public Assembly getDefaultAssembly(){
		if(templates == null) return null;
		for(int i=0; i<templates.length; i++)
			if(templates[i].getStatus().equals(Assembly.DEFAULT_TEMPLATE))
				return templates[i];
		return null; //If none found
	}
	
	///////////////////////////////////////////////
	
	//Returns all Assemblies used as part supply (Inventory Parts)
	public Assembly getPartSupply(){
		if(assemblies == null) return null;
		for(int i=0; i<assemblies.length; i++)
			if(assemblies[i].getStatus().equals(Assembly.PART_SUPPLY))
				return assemblies[i];
		
		return null;	//If none found
	}
	
	public Part[] getShippingParts(){
		if(getPartSupply() == null) return null;
		
		Assembly a = getPartSupply();
		if(a.getParts() == null) return null;
		
		Part[] ret = null;
		
		for(int i=0; i<a.getParts().length; i++)
			if(a.getParts()[i].getStatus().equals(Constituent.SHIPPING_INCOMING)
					|| a.getParts()[i].getStatus().equals(Constituent.SHIPPING_SUPPLY))
				ret = ArrayManager.addToArray(a.getParts()[i], ret);
		
		return ret;
	}
	
	public void addPartToSupply(Part toAdd){
		
		if(getPartSupply() == null){	//If Part_Supply doesn't exist
			
			Part[] arr = {toAdd};
			addAssembly(new Assembly(	//Make it exist
					Database.getNewSerial(),
					1,
					0,
					this.getAddress(),
					name + " " + Assembly.PART_SUPPLY,
					Assembly.PART_SUPPLY,
					Assembly.NO_COLOR,
					Date.getTodaysDate(),
					null,
					null,
					null,
					null,
					arr));
		}else
			equivAdd(toAdd);
		
	}
	
	public void addPartToSupplyNonGrouping(Part toAdd){
		
		if(getPartSupply() == null){	//If Part_Supply doesn't exist
			
			Part[] arr = {toAdd};
			addAssembly(new Assembly(	//Make it exist
					Database.getNewSerial(),
					1,
					0,
					this.getAddress(),
					name + " " + Assembly.PART_SUPPLY,
					Assembly.PART_SUPPLY,
					Assembly.NO_COLOR,
					Date.getTodaysDate(),
					null,
					null,
					null,
					null,
					arr));
		}else
			getPartSupply().addPart(toAdd);
		
	}
	
	private void equivAdd(Part add){
		Part eq = getPartSupply().findEquivalentPartFromSupply(add);
		
		if(eq == null) //No equivalent parts available
			getPartSupply().addPart(add);
		else{
			Credit total = new Credit(eq.getTotalCost().getValueInDollars()
					+ add.getTotalCost().getValueInDollars()); //Current parts + added
			
			int quan = eq.getQuantity() + add.getQuantity();
			
			Credit price = new Credit(total.getValueInDollars() / quan);
			
			
			eq.setPrice(price); //Set new unit price
			eq.setQuantity(eq.getQuantity() + add.getQuantity()); //Add to existing part
			
			
			Credit net = new Credit(eq.getTotalCost().getValueInDollars() - total.getValueInDollars());
			if(total.getValueInCents() != (quan * price.getValueInCents()))
				System.out.println("A gain/loss of " + net.toString()
						+ " was caused from a Part merge.");
		}
		
	}//End equivAdd()
	
	public void removePartFromSupply(Part rem){
		if(getPartSupply() == null) return;
		
		Part pt = getPartSupply().findExactPartFromSupply(rem);
	
		if(pt == null)
			pt = getPartSupply().findPartByName(rem);
		
		if(pt == null){
			System.err.println("Part not found for removal");
			return;
		}
		
		if(pt.getQuantity() > rem.getQuantity()) //Decrement supply
			pt.setQuantity(pt.getQuantity() - rem.getQuantity());
		else if(pt.getQuantity() == rem.getQuantity()) //Remove part completely
			getPartSupply().removePart(rem);
		else
			System.err.println("Requested to remove more Parts than available from supply.");
		
	}
	
	public void addAssemblyToSupply(Assembly toAdd){
		
		if(getPartSupply() == null){
			
			Assembly[] arr = {toAdd};
			addAssembly(new Assembly(
					Database.getNewSerial(),
					1,
					0,
					this.getAddress(),
					name + " " + Assembly.PART_SUPPLY,
					Assembly.PART_SUPPLY,
					Assembly.NO_COLOR,
					Date.getTodaysDate(),
					null,
					null,
					null,
					arr,
					null));
		
		}else
			equivAdd(toAdd);
	}
	
	public void addAssemblyToSupplyNonGrouping(Assembly toAdd){
		
		if(getPartSupply() == null){
			
			Assembly[] arr = {toAdd};
			addAssembly(new Assembly(
					Database.getNewSerial(),
					1,
					0,
					this.getAddress(),
					name + " " + Assembly.PART_SUPPLY,
					Assembly.PART_SUPPLY,
					Assembly.NO_COLOR,
					Date.getTodaysDate(),
					null,
					null,
					null,
					arr,
					null));
		
		}else
			getPartSupply().addAssembly(toAdd);
	}
	
	private void equivAdd(Assembly add){
		Assembly eq = getPartSupply().findEquivalentAssemblyFromSupply(add);
		
		if(eq == null) //No equivalent parts available
			getPartSupply().addAssembly(add);
		else{
			Credit price = new Credit(
					((eq.getTotalCost().getValueInDollars() * eq.getQuantity())
						+ (add.getTotalCost().getValueInDollars() * add.getQuantity()))
						/ (eq.getQuantity() + add.getQuantity()));
			
			eq.distributeCost(price);
			eq.setQuantity(eq.getQuantity() + add.getQuantity()); //Add to existing part
			
		}
		
	}
	
	public void removeAssemblyFromSupply(Assembly rem){
		if(getPartSupply() == null) return;
		
		Assembly a = getPartSupply().findExactAssemblyFromSupply(rem);
		
		
		if(a == null) return; //Assembly not found
			
		if(a.getQuantity() > rem.getQuantity()) //Decrement supply
			a.setQuantity(a.getQuantity() - rem.getQuantity());
		else if(a.getQuantity() == rem.getQuantity()) //Remove from supply
			getPartSupply().removeAssemblyEquiv(rem);
		else
			System.err.println("Requested to remove more Assemblies than available from supply.");
		
	}
	
	public void removeSimilarAssemblyFromSupply(Assembly rem){
		if(getPartSupply() == null) return;
		
		Assembly a = getPartSupply().findEquivalentAssemblyFromSupply(rem);
		
		System.out.println("Supply: " + a.getQuantity() + "\nSel: " + rem.getQuantity());
		
		if(a == null) return; //Assembly not found
			
		if(a.getQuantity() > rem.getQuantity()) //Decrement supply
			a.setQuantity(a.getQuantity() - rem.getQuantity());
		else if(a.getQuantity() == rem.getQuantity()) //Remove from supply
			getPartSupply().removeAssemblyEquiv(rem);
		else
			System.err.println("Requested to remove more Assemblies than available from supply.");
		
	}
	
	public void removeAssemblyFromTemplates(Assembly rem){
		if(templates == null) return;
		
		int index = -1;
		for(int i=0; i<templates.length; i++)
			if(templates[i].equivalent(rem))
				index = i;
		
		if(index == -1) return; //Not found
		
		Assembly[] temp = new Assembly[templates.length - 1];
		for(int i=0; i<temp.length; i++)
			if(i < index)
				temp[i] = templates[i];
			else
				temp[i] = templates[i + 1];
		
		templates = temp;
	}
	
	///////////////////////////////////////////////
	
	//Returns all parts and assemblies in part supply
	//Note: PART_SUPPLY Assembly is only one layer deep
	public Constituent[] getSupplyConstituents(){
		Assembly a = getPartSupply();
		Constituent[] toRet = null;
		
		if(a == null) return toRet;
		
		if(a.getParts() != null)
			for(int i=0; i<a.getParts().length; i++)
				toRet = ArrayManager.addToArray(a.getParts()[i], toRet);
		
		if(a.getAssemblies() != null)
			for(int i=0; i<a.getAssemblies().length; i++)
				toRet = ArrayManager.addToArray(a.getAssemblies()[i], toRet);
		
		return toRet;
	}
	
	public Constituent[] getSupplyConstituentsByFilter(String name, 
													   String color, 
													   String quality, 
													   String status){
		
		Constituent[] c = getSupplyConstituents();
		
		if(c == null) return null; //There are no supply constituents
		
		Constituent[] ret = null; //Array to return
		
		
		for(int i=0; i<c.length; i++)
			//Filter status
			if(status.equals("Status") || c[i].getStatus().contains(status)){
				//Filter name string
				if(name.equals("") || c[i].getName().contains(name)){
					
					//Filter color
					if(!color.equals("Color")){ //If color matters
						try{
							if(c[i].getColor().equals(color))
								//Filter Quality
								if(!quality.equals("Quality")){
									try{
										Part p = (Part) c[i]; //Cast to part
										if(quality.equals(p.getQuality()))
											ret = ArrayManager.addToArray(c[i], ret);
										
									}catch(ClassCastException e){
										//Constituent is an Assembly, reject
									}catch(NullPointerException e){
										//Part has no quality, reject
									}
								}else //Skip quality
									ret = ArrayManager.addToArray(c[i], ret);
							
						}catch(NullPointerException e){
							//Constituent has no color, reject
						}
						
					}else{ //Skip color
						if(!quality.equals("Quality")){
							try{
								Part p = (Part) c[i];
								if(quality.equals(p.getQuality()))
									ret = ArrayManager.addToArray(c[i], ret);
								
							}catch(ClassCastException e){
								//Reject
							}catch(NullPointerException e){
								//Reject
							}
						}else //Skip quality
							ret = ArrayManager.addToArray(c[i], ret);
					}
				}
			}
		
		return ret;
	}
	
	/**
	 * Gathers all constituents (Parts and Assemblies) in the Supply Assembly of this Category.
	 * 
	 * @param status	Array of status to consider
	 * @return			List of all Parts and Assemblies with any of the status' in the status array.
	 */
	public ArrayList<Constituent> getSupplyConstituentsByStatus(String[] status){
		
		// Grab supply assembly
		Assembly a = getPartSupply();
		
		// Check for empty
		if(a == null || status == null) return null;
		
		// Initialize List
		ArrayList<Constituent> toRet = new ArrayList<Constituent>();
		
		for(int i=0; i<status.length; i++){ // For each status to consider
			
			if(a.getParts() != null)
				for(int j=0; j<a.getParts().length; j++)
					if(status[i].equals(a.getParts()[j].getStatus()))
						toRet.add(a.getParts()[j]);
			
			if(a.getAssemblies() != null)
				for(int j=0; j<a.getAssemblies().length; j++)
					if(status[i].equals(a.getAssemblies()[j].getStatus()))
						toRet.add(a.getAssemblies()[j]);
		
		}
		
		return toRet;
		
	} // END getSupplyConstituentsByStatus()
	
	/**
	 * @param status
	 * @return
	 * @deprecated		Use ArrayList version.
	 */
	public Constituent[] getSupplyConstituentsByStatus(String status){
		Assembly a = getPartSupply();
		Constituent[] toRet = null;
		
		if(a == null) return toRet;
		
		if(a.getParts() != null)
			for(int i=0; i<a.getParts().length; i++)
				try{
				if(a.getParts()[i].getStatus().equals(status))
					toRet = ArrayManager.addToArray(a.getParts()[i], toRet);
				}catch(NullPointerException e){
					System.out.println(a.getParts()[i].getName());
				}
		
		
		if(a.getAssemblies() != null)
			for(int i=0; i<a.getAssemblies().length; i++)
				if(a.getAssemblies()[i].getStatus().equals(status))
					toRet = ArrayManager.addToArray(a.getAssemblies()[i], toRet);
	
		
		return toRet;
		
	}//End getSupplyConstituentsByStatus()
	
	public void addTemplate(Assembly newTemp){
		templates = ArrayManager.addToArray(newTemp, templates);
	}
	
	public void addAssembly(Assembly newAss){
		assemblies = ArrayManager.addToArray(newAss, assemblies);
	}
	
}//End Category
