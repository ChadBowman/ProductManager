package net.orthus.pm;

import java.awt.Color;

public class ColorManager {
	public static Color getOrthusSteel(){ return new Color(105, 105, 105); }
	public static Color getOrthusBronze(){ return new Color(163, 122, 0); }
	public static Color getTransparentBlack(){ return new Color(0f, 0f, 0f, 0.5f); }
	public static Color getTransparent(){ return new Color(0f, 0f, 0f, 0f); }
	
	public static Color getAboveGoal(){ return new Color(181, 240, 0); }
	public static Color getOnGoal(){ return new Color(240, 179, 0); }
	public static Color getLeavingGoal(){ return new Color(164, 122, 0); }
	public static Color getApproachingGoal(){ return new Color(240, 59, 0); }
	public static Color getOffGoal(){ return new Color(164, 40, 0); }
}
