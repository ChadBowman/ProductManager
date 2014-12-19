package net.orthus.pm;

import net.orthus.util.Sorter;

public class ProductStats {

	private Date today;
	private ProductCategory cat;
	
	public ProductStats(ProductCategory cat){
		
		today = Date.getTodaysDate();
		this.cat = cat;
		
	}
	
	public String[] getPriceChangeRecomendations(){
		
		Product[] listed = cat.getProductsByStatus(Product.LISTED);
		Product[] stock = cat.getProductsByStatus(Product.STOCK);
		
		String[] toRet = null;
		
		if(listed != null)
			for(int i=0; i<listed.length; i++){
				boolean found = false;
				if(listed[i].getDateListed().daysAgo() > 7) //If product has been listed longer than a week
					if(stock != null)
						for(int j=0; j<stock.length; j++) //Check stock for console with same color
							if(listed[i].getOverallColor().equals(stock[j].getOverallColor()))
								found = true;
					
			
				if(found){ //Product on deck, suggest moving listed item sooner
					
					int amt = listed[i].getDateListed().daysAgo() / 7 * 10;
					
					toRet = ArrayManager.addToArray(
							String.format("%s %s [%d] %d%%%n", listed[i].getOverallColor(), cat.getName(), listed[i].getID(), amt), 
							toRet);
				}
					
			}
			
		return toRet;
		
	}
	
	public Credit getCategoryTotal(){
		
		Credit catTotal = new Credit();
		
		//First Add all complete products
		if(cat.getProductsByStatus(Product.STOCK) != null){
			Product[] p = cat.getProductsByStatus(Product.STOCK);
		
			for(int j=0; j<p.length; j++)
				catTotal.add(p[j].getAssembly().getTotalCost());
		}
		if(cat.getProductsByStatus(Product.LISTED) != null){
			Product[] p = cat.getProductsByStatus(Product.LISTED);
			
			for(int j=0; j<p.length; j++)
				catTotal.add(p[j].getAssembly().getTotalCost());
		}
		if(cat.getProductsByStatus(Product.RETURNED) != null){
			Product[] p = cat.getProductsByStatus(Product.RETURNED);
			
			for(int j=0; j<p.length; j++)
				catTotal.add(p[j].getAssembly().getTotalCost());
		}
		
			//Then add all partial products
		if(cat.getProductsByStatus(Product.INCOMING) != null){
			Product[] p = cat.getProductsByStatus(Product.INCOMING);
			
			for(int j=0; j<p.length; j++)
				catTotal.add(p[j].getAssembly().getTotalCost());
		}
		if(cat.getProductsByStatus(Product.INVENTORY) != null){
			Product[] p = cat.getProductsByStatus(Product.INVENTORY);
			
			for(int j=0; j<p.length; j++)
				catTotal.add(p[j].getAssembly().getTotalCost());
		}
		
			//Add all parts
		if(cat.getSupplyConstituents() != null)
			for(int i=0; i<cat.getSupplyConstituents().length; i++)
				catTotal.add(cat.getSupplyConstituents()[i].getTotalCost());
				
		
		return catTotal;
	}
	
	public Reorder[] neededConstituents(){
		
		int lookBack = Database.getReorderLookBack(); //How many days to look back in the useRecords
		UseRecord[] recs = cat.getRecords(); //Records of all parts and assemblies used in repairs
		Assembly[] ases = cat.getTemplates(); //All purchasable assemblies
		Reorder[] toOrder = null; //Array of needed parts sorted by urgency
		
			//Assemblies
		if(ases != null && recs != null)
			for(int i=0; i<ases.length; i++){ //For all templates
				
				int history = 0; //All template Assemblies which were used in repairs lookBack days ago
				for(int j=0; j<recs.length; j++) //For all use records
					if(ases[i].getSerial() == recs[j].getSerial()) //Matching serials
						if(recs[j].getDate().getDateInDays() > Date.getTodaysDate().getDateInDays() - lookBack)
							history += recs[j].getQuanity();
				
				double rate = (double) history / lookBack; //Assemblies used per day
				int need = (int) (rate * Database.getReorderTollerance() + 0.5);
				
				Assembly[] supply = cat.getPartSupply().findAllAssembliesBySerialFromSupply(ases[i]);
				int count = 0; //How many of needed assembly are available in supply
				if(supply != null)
					for(int j=0; j<supply.length; j++)
						if(supply[j].getStatus().equals(Constituent.SUPPLY))
							count += supply[j].getQuantity();
				
				if(count < need) //Not enough available to sustain
					toOrder = addToArray(new Reorder((int)(count / rate), need - count, history, ases[i]), toOrder);
				
			}//End template for
				
			
		Part[] parts = cat.getDefaultAssembly().collapseAssembly(); //All parts
		
		if(parts != null && recs != null)
			for(int i=0; i<parts.length; i++){
				
				int history = 0;
				for(int j=0; j<recs.length; j++)
					if(parts[i].getSerial() == recs[j].getSerial())
						if(recs[j].getDate().getDateInDays() > Date.getTodaysDate().getDateInDays() - lookBack)
							history += recs[j].getQuanity();
				
				double rate = (double) history / lookBack; //Parts used per day
				int need = (int) (rate * Database.getReorderTollerance() + 0.5);
				
				
				Part[] supply = cat.getPartSupply().findAllPartsBySerialFromSupply(parts[i]);
				int count = 0; //How many of needed assembly are available in supply
				if(supply != null)
					for(int j=0; j<supply.length; j++)
						if(supply[j].getStatus().equals(Constituent.SUPPLY))
							count += supply[j].getQuantity();
				
				toOrder = addToArray(new Reorder((int)(count / rate), need - count, history, parts[i]), toOrder);
			}
		
			toOrder = new Sorter<Reorder>().quickSort(toOrder); //Sort
			return toOrder;
	}
	
	
	public Reorder[] toLiquidate(){
		
		int lookBack = Database.getReorderLookBack(); //How many days to look back in the useRecords
		UseRecord[] recs = cat.getRecords(); //Records of all parts and assemblies used in repairs
		Assembly[] ases = cat.getTemplates(); //All purchasable assemblies
		Reorder[] toOrder = null; //Array of needed parts sorted by urgency
		
		
		Part[] parts = cat.getDefaultAssembly().collapseAssembly(); //All parts
		
		if(parts != null && recs != null)
			for(int i=0; i<parts.length; i++){ // For every part in the default assembly
				
				int history = 0;
				for(int j=0; j<recs.length; j++)
					if(parts[i].getSerial() == recs[j].getSerial())
						if(recs[j].getDate().getDateInDays() > Date.getTodaysDate().getDateInDays() - lookBack)
							history += recs[j].getQuanity();
				
				double rate = (double) history / lookBack; //Parts used per day
				int need = (int) (2 * rate * Database.getReorderTollerance() + 0.5);
						//Number of parts needed for two cyles
				
				
				Part[] supply = cat.getPartSupply().findAllPartsBySerialFromSupply(parts[i]);
				int count = 0; //How many of needed assembly are available in supply
				if(supply != null)
					for(int j=0; j<supply.length; j++)
						if(supply[j].getStatus().equals(Constituent.SUPPLY))
							count += supply[j].getQuantity();
				
				if(count > need) //More parts than needed for two cycles
					toOrder = addToArray(new Reorder((int) (-count / rate), count - need, history, parts[i]), toOrder);
			}
		
		toOrder = new Sorter<Reorder>().quickSort(toOrder); //Sort
		return toOrder;
	}
	
	public class Reorder implements Comparable<Reorder>{
		
		private int sustain, need, consumption;
		private Constituent part;
		
		public Reorder(int sustain, int need, int consumption, Constituent part){
			this.sustain = sustain;
			this.part = part;
			this.need = need;
			this.consumption = consumption;
		}

		public int getSustain(){ return sustain; }
		public Constituent getPart(){ return part; }
		public int getNeedCount(){ return need; }
		public int getConsumption(){ return consumption; }
		
		@Override
		public int compareTo(Reorder o) {
			return o.getNeedCount() - this.need;
		}
		
	}
	
	private Reorder[] addToArray(Reorder toAdd, Reorder[] array){
		if(array == null){
			array = new Reorder[1];
			array[0] = toAdd;
			return array;
		}else{
			Reorder[] temp = new Reorder[array.length + 1];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}
}
