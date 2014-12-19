package net.orthus.pm;



public class Main {
	
	public static void main(String[] args) {
		
		if(new Reader().load(null)) Database.prepare();
		GUIEngine.initializeUI();
	
	}
}
