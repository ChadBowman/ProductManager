package net.orthus.pm;


public class AlertManager {

	public AlertManager(){
		
	}
	
	public String[] bidSuggestions(){
		
		//Want X Consoles on supply
		int dsl = 8;
		int ds3 = 8;
		int dsx = 6;
		
		
		ProductCategory[] pr = Database.getProductCategories();
		int[] tot = new int[pr.length];
		String[] toRet = new String[pr.length];
		
		for(int i=0; i<pr.length; i++){
			
			//Total in supply
			if(pr[i].getProductsByStatus(Product.INCOMING) != null)
				tot[i] = pr[i].getProductsByStatus(Product.INCOMING).length;
			if(pr[i].getProductsByStatus(Product.INVENTORY) != null)
				tot[i] += pr[i].getProductsByStatus(Product.INVENTORY).length;
		
			switch(i){
			case 0: 
				tot[i] = dsl - tot[i];
				toRet[i] = (tot[i] > 0)? "Order " + tot[i] + " DS Lite\n" : ""; 
				break;
				
			case 1: toRet[i] = ""; break;
			case 2: toRet[i] = ""; break;
			case 3: 
				tot[i] = ds3 - tot[i];
				toRet[i] = (tot[i] > 0)? "Order " + tot[i] + " 3DS\n" : ""; 
				break;
				
			case 4: 
				tot[i] = dsx - tot[i];
				toRet[i] = (tot[i] > 0)? "Order " + tot[i] + " 3DS XL\n" : ""; 
				break;
			}
			
		}
		
		return toRet;
		
	}
	
}
