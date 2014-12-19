package net.orthus.pm;

import net.orthus.util.Sorter;

public class ItemSorter {
	//----- Variables
	private Product[] products;
	private Service[] services;
	private Repair[] repairs;
	private Constituent[] constituents;
	private Assembly[] assemblies;
	
	//----- Constants
	public static final int PRODUCT_ID = 0;
	public static final int PRODCUT_ESTRETURN = 1;
	public static final int PRODUCT_PURCHASC = 2;
	public static final int PRODUCT_LISTASC = 3;
	public static final int PRODUCT_RETURNASC = 4;
	public static final int PRODUCT_SOLDDES = 5;
	
	public static final int SERVICE_LISTASC = 6;
	
	public static final int REPAIR_SOLDASC = 7;
	public static final int REPAIR_SOLDDES = 8;
	public static final int REPAIR_RETURNASC = 9;
	
	public static final int CONST_PURCHASC = 10;
	public static final int CONST_ALPH = 11;

	//----- Constructors
	public ItemSorter(Product[] products) { this.products = products; }
	public ItemSorter(Service[] services){ this.services = services; }
	public ItemSorter(Repair[] repairs){ this.repairs = repairs; }
	public ItemSorter(Constituent[] parts){ this.constituents = parts; }
	public ItemSorter(Assembly[] assemblies){ this.assemblies = assemblies; }
	
	//----- AdvancedMethods
	//Utility
	private void swap(Item[] array, int i, int j){
		Item temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	private void swap(Service[] array, int i, int j){
		Service temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	private void swap(Constituent[] array, int i, int j){
		Constituent temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	
	//Product methods
	public Product[] productIDSort(){
		if(products == null) return null;

		//Bubble sort
		boolean flag = true;
		while(flag){
			flag = false;
			for(int i=1; i<products.length; i++)
				if(products[i - 1].getID() > products[i].getID()){
					swap(products, i, i-1);
					flag = true;
				}
		}
		return products;
	}
	
	public Product[] productEstReturnSort(){
		if(products == null) return null;
		
		products = new Sorter<Product>().quickSort(products);
		//products =  new Sorter<Product>().quickSort(products);
		
		return products;
	}
	
	public Product[] productDateAscending(int status){
		if(products == null) return null;
		
		//Bubble sort
		boolean flag = true;
		while(flag){
			flag = false;
			for(int i=1; i<products.length; i++){
				try{
					switch(status){
					case PRODUCT_PURCHASC:
						if(products[i - 1].getDatePurchased().compareTo(products[i].getDatePurchased()) > 0){
							swap(products, i, i-1);
							flag = true;
						} break;
						
					case PRODUCT_LISTASC:
						if(products[i - 1].getDateListed().compareTo(products[i].getDateListed()) > 0){
							swap(products, i, i-1);
							flag = true;
						} break;
						
					case PRODUCT_RETURNASC:
						if(products[i - 1].getDateReturned().compareTo(products[i].getDateReturned()) > 0){
							swap(products, i, i-1);
							flag = true;
						} break;
					}
				}catch(NullPointerException e){
					System.err.println("Sort status invalid!");
					e.printStackTrace();
					return null;
				}
			}
		}
		return products;
	}
	
	public Product[] productDateDescending(){
		if(products == null) return null;
		
		Product[] flipped = new Product[products.length];
		for(int i=0, j=flipped.length-1; i<flipped.length; i++, j--)
			flipped[i] = products[j];
		
		//Bubble sort
		boolean flag = true;
		while(flag){
			flag = false;
			for(int i=1; i<flipped.length; i++){
				try{
					if(flipped[i - 1].getDateSold().compareTo(flipped[i].getDateSold()) < 0){
						swap(flipped, i, i-1);
						flag = true;
					}
				}catch(NullPointerException e){
					System.err.println("Sort status invalid!");
					return null;
				} 
			}
		}
		return flipped;
	}

	//Service methods
	public Service[] serviceDateAscending(){
		if(services == null) return null;
		
		//Bubble sort
		boolean flag = true;
		while(flag){
			flag = false;
			
			for(int i=1; i<services.length; i++){
				if(services[i - 1].getDateListed() != null && services[i].getDateListed() != null) //TODO Fix sort
					if(services[i - 1].getDateListed().compareTo(services[i].getDateListed()) > 0){
						swap(services, i, i-1);
						flag = true;
					}
			}
		}
		return services;
	}
	
	//Repair methods
	public Repair[] repairDate(int state){
		if(repairs == null) return null;
		
		//Bubble sort
		boolean flag = true;
		while(flag){
			flag = false;
			for(int i=1; i<repairs.length; i++){
				try{
					switch(state){
					case REPAIR_SOLDASC: 
						if(repairs[i - 1].getDateSold().compareTo(repairs[i].getDateSold()) > 0){
							swap(repairs, i, i-1);
							flag = true;
						} break;
						
					case REPAIR_SOLDDES:
						if(repairs[i - 1].getDateSold().compareTo(repairs[i].getDateSold()) < 0){
							swap(repairs, i, i-1);
							flag = true;
						} break;
					case REPAIR_RETURNASC:
						if(repairs[i - 1].getDateReturned().compareTo(repairs[i].getDateReturned()) > 0){
							swap(repairs, i, i-1);
							flag = true;
						} break;
					}
				}catch(NullPointerException e){
					System.err.println("Sort status invalid!");
					return null;
				}
			}
		}
		return repairs;
	}
	
	//Constituents methods
	public Constituent[] purchaseDate(){
		if(constituents == null) return null;
		
		//Bubble sort
		boolean flag = true;
		while(flag){
			flag = false;
			for(int i=1; i<constituents.length; i++)
				if(constituents[i - 1].getDatePurchased().compareTo(constituents[i].getDatePurchased()) > 0){
					swap(constituents, i, i-1);
					flag = true;
				}
		}
		return constituents;
	}
	
	public Constituent[] alphabetical(){
		if(constituents == null) return null;
		
		//Bubble sort
		boolean flag = true;
		while(flag){
			flag = false;
			for(int i=1; i<constituents.length; i++)
				if(constituents[i - 1].getName().compareTo(constituents[i].getName()) > 0){
					swap(constituents, i, i-1);
					flag = true;
				}
		}
		return constituents;
	}
	
	public Assembly[] cost(){
		if(assemblies == null) return null;
		
		assemblies = new Sorter<Assembly>().quickSort(assemblies);
	
		return assemblies;
	}
	
}
