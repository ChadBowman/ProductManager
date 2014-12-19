package net.orthus.pm;


public class AquirePart {

	public static void initializeFrame(){
		Constituent con = DataSelection.getSelectedConstituent();
		
		if(con == null) return;
		if(!con.getStatus().equals(Constituent.INCOMING)
				&& !con.getStatus().equals(Constituent.SHIPPING_INCOMING)){
			
			OptionPane.showConfirm(con.getName() + " must be incomming to aquire.", 
					"Part Status Error!");
			return;
		}
		
		int sel = OptionPane.showConfirmYes("Has " + con.toString() + " been aquired?", "Aquire Part");
		
		if(sel == 0){
			
			try{
				Part p = (Part) con;
				Database.getFocusedCategory().removePartFromSupply(p);
				
				if(con.getStatus().equals(Constituent.INCOMING)){
					p.setStatus(Part.SUPPLY);
					Database.getFocusedCategory().addPartToSupply(p);
				}else{
					p.setStatus(Part.SHIPPING_SUPPLY);
					if(Database.getShippingMats() == null){
						Assembly a = new Assembly(
								"ShippingParts", null, null);
						a.addPart(p);
						Database.setShippingMats(a);
					}else
						Database.getShippingMats().addPart(p);
				}
				
				
			}catch(ClassCastException e){
				con.setStatus(Part.SUPPLY);
				//TODO set merge for assemblies
			}
			
			
			GUIEngine.refresh(-1, -1);
		}
	}

}
