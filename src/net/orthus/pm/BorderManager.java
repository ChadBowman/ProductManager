package net.orthus.pm;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class BorderManager {

	public static Border getTitleBorder(String title){
		return BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), title);
	}
	
	public static Border getLineBorder(){
		return BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2);
	}

}
