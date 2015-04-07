package net.orthus.pm;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToolBar;

public class ToolBar extends JToolBar 
					 implements ActionListener {
	
	//----- Constructors
	public ToolBar(int type) {
		this.setFloatable(false);
		this.setLayout(new GridLayout());
		
		switch(type){
		case GUIEngine.PRODUCT: setProducts(); break;
		case GUIEngine.SERVICE: setServices(); break;
		case GUIEngine.PART: setParts(); break;
		}
	}
	
	//----- Advanced Methods
	
	private void setProducts(){
		this.add(new Button("Purchase", this, 10));
		this.add(new Button("Diagnose", this, 11));
		this.add(new Button("Repair", this, 12));
		this.add(new Button("List", this, 13));
		this.add(new Button("Sell", this, 14));
		this.add(new Button("Return", this, 15));
		this.add(new Button("Shipping Options", this, 16));
	}
	
	private void setServices(){
		this.add(new Button("Manage Services", this, 20));
		this.add(new Button("Sell", this, 21));
		this.add(new Button("Aquire", this, 22));
		this.add(new Button("Repair", this, 23));
		this.add(new Button("Return", this, 24));
		this.add(new Button("Shipping Options", this, 25));
	}
	
	private void setParts(){
		this.add(new Button("Build Assembly", this, 30));
		this.add(new Button("Purchase Part", this, 31));
		this.add(new Button("Purchase Assembly", this, 32));
		this.add(new Button("Aquire", this, 33));
		this.add(new Button("List", this, 34));
		this.add(new Button("Sell", this, 35));
		this.add(new Button("Return", this, 36));
		this.add(new Button("Shipping Options", this, 37));
		this.add(new Button("Inventory", this, 38));
	}

	//----- Interfaces
	public void actionPerformed(ActionEvent arg0) {
		int cmd = Integer.parseInt(arg0.getActionCommand());
		switch(cmd){
		//SUMMARY
		case 0:  SummaryActions.sendPaycheck(); break;
		case 1: SummaryActions.reinvestmentRate(); break;
		case 2: break;
		//PRODUCT
		case 10: PurchaseProduct.initializeFrame(); break;
		case 11: DiagnoseProduct.initializeFrame(); break;
		case 12: RepairProduct.initializeFrame(null); break;
		case 13: ListProduct.initializeFrame(); break;
		case 14: SellProduct.initializeFrame(); break;
		case 15: ReturnProduct.intializeFrame(); break;
		case 16: ShippingOptions.initializeFrame(); break;
		//SERVICE
		case 20: ManageServices.initializeFrame(0); break;
		case 21: SellRepair.initializeFrame(); break;
		case 22: AquireRepair.initalizeFrame(); break;
		case 23: RepairRepair.initializeFrame(); break;
		case 24: ReturnRepair.initializeFrame(); break;
		case 25: ShippingOptions.initializeFrame(); break;
		//PART
		case 30: BuildAssembly.initializeFrame(); break;
		case 31: PurchasePart.initializeFrame(); break;
		case 32: PurchaseAssembly.initializeFrame(); break;
		case 33: AquirePart.initializeFrame(); break;
		case 34: ListPart.initializeFrame(); break;
		case 35: SellPart.initalizeFrame(); break;
		case 36: ReturnPart.intializeFrame(); break;
		case 37: ShippingOptions.initializeFrame(); break;
		case 38: InventoryMode.initializeFrame();
		}
		
	}


}
