package net.orthus.pm;


public class DataSelection {
	//----- Variables
	private static Product selectedProduct;
	private static Constituent selectedConstituent;
	private static Service selectedService;
	private static Repair selectedRepair;
	
	//----- StandardMethods
	public static Product getSelectedProduct(){ return selectedProduct; }
	public static Constituent getSelectedConstituent(){ return selectedConstituent; }
	public static Service getSelectedService(){ return selectedService; }
	public static Repair getSelectedRepair(){ return selectedRepair; }
	
	//----- Advanced Methods
	public static void setSelection(Product product, 
								    Constituent part, 
								    Service service, 
								    Repair repair){
		
		selectedProduct = product;
		selectedConstituent = part;
		selectedService = service;
		selectedRepair = repair;
	}
}
