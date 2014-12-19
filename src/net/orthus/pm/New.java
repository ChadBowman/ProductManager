package net.orthus.pm;

import javax.swing.JOptionPane;

public class New {

	public static void NewInstance(){
		if(!check()) return;
		
		Database.setCategories(null);
		Database.setServiceCategories(null);
		Database.setProductCategories(null);
		
		Database.setPayPalBalance(null);
		Database.setEbayBalances(new Credit[12]);
		Database.setUndeliveredIncome(null);
		
		Database.setInvestmentGoal(null);
		Database.setReinvestmentRate(0);
		
		Database.setFreeListingsAllotted(0);
		Database.setSaleQuantitiyAllotted(0);
		Database.setSaleAmmountAllotted(null);
		Database.setPayPalFeeRate(0);
		Database.setTopRatedSeller(false);
		
		Database.setLastLogin(null);
		Database.setLastSavePath(null);
		Database.setPassword("Orthus");
		Database.setLastSerial(Integer.MAX_VALUE);
		
		GUIEngine.getMainFrame().setTitle("Orthus Product Manager " + Database.VERSION);
		GUIEngine.refresh(GUIEngine.SUMMARY, 0);
	}
	
	private static boolean check(){
		if(GUIEngine.getMainFrame().getTitle().contains("*")){		
			int sel = JOptionPane.showConfirmDialog(GUIEngine.getMainFrame(), 
					"Are you sure you want to exit?\nAll unsaved changes will be lost.", 
					"Warning", JOptionPane.YES_OPTION, JOptionPane.WARNING_MESSAGE);
			if(sel == JOptionPane.YES_OPTION)
				return true;
		}else
			return true;
		
		return false;
		
	}//End check()
}
