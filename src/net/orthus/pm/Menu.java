package net.orthus.pm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;


public class Menu extends JMenuBar implements ActionListener{
	//----- Variables
	private JMenu data,
		  		  add,
		  		  modify,
		  		  purchase,
		  		  list,
		  		  sell,
		  		  utility;
	
	private JMenu product,
				  productCategory,
				  serviceCategory;
	
	private JCheckBox trs;
	
	//----- Constructors
	public Menu(){
		//DATA
		this.add(data = new JMenu("Data"));
			data.add(new MenuItem("New", this, 0, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK)));
			data.add(new MenuItem("Open", this, 1));
			data.add(new MenuItem("Save", this, 2, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK)));
			data.add(new MenuItem("Save As", this, 3));
			data.add(new MenuItem("Print", this, 4));
		
		//ADD
		this.add(add = new JMenu("Add"));
			add.add(new MenuItem("Product Category", this, 10, KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK)));
			add.add(new MenuItem("Service Category", this, 11, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK)));
		
		//MODIFY
		this.add(modify = new JMenu("Modify"));
			modify.add(trs = new JCheckBox("Top Rated Seller"));
			trs.addActionListener(this);
			trs.setActionCommand("" + 20);
			if(Database.isTopRatedSeller()) trs.setSelected(true);
			modify.add(new MenuItem("Profit Percentage Goal", this, 21));
			modify.add(new MenuItem("Reinvestment Rate", this, 22));
			modify.add(new MenuItem("Investment Goal", this, 23));
			modify.add(new MenuItem("PayPal Balance", this, 24));
			modify.add(new MenuItem("PayPal Fee Rate", this, 25));
			modify.add(new MenuItem("Ebay Invoice", this, 26));
			modify.add(new MenuItem("Free Listing Allotment", this, 27));
			modify.add(new MenuItem("Sale Limits", this, 28));
			modify.add(new MenuItem("Reorder Tollerance", this, 29));
			modify.add(new MenuItem("Subscription Fee", this, 200));
			modify.add(new MenuItem("Salary Goal", this, 201));
			modify.addSeparator();
			modify.add(productCategory = new JMenu("Product Category"));
			if(Database.getProductCategories() != null){
				MenuItem[] prods2 = new MenuItem[Database.getProductCategories().length];
				
				for(int i=0; i<prods2.length; i++)
					productCategory.add(prods2[i] = new MenuItem(Database.getProductCategories()[i].getName(), this, (i+1)*-1));
			}
			modify.add(serviceCategory = new JMenu("Service Category"));
			
		//PURCHASE
		this.add(purchase = new JMenu("Purchase"));
			purchase.add(product = new JMenu("Product"));
			if(Database.getProductCategories() != null){
				MenuItem[] prods1 = new MenuItem[Database.getProductCategories().length];
				for(int i=0; i<prods1.length; i++)
					product.add(prods1[i] = new MenuItem(Database.getProductCategories()[i].getName(), this, i*100));
			}
			
			purchase.add(new MenuItem("Part", this, 31));
			
		//LIST
		this.add(list = new JMenu("List"));
			list.add(new MenuItem("Generic Item", this, 40));
			list.add(new MenuItem("Product", this, 41));
			list.add(new MenuItem("Service", this, 42));
			list.add(new MenuItem("Part", this, 43));
			
		//SELL
		this.add(sell = new JMenu("Sell"));
			sell.add(new MenuItem("Generic Item", this, 50));
			sell.add(new MenuItem("Product", this, 51));
			sell.add(new MenuItem("Service", this, 52));
			sell.add(new MenuItem("Part", this, 53));
		
		//UTILITY
		this.add(utility = new JMenu("Utility"));
			utility.add(new MenuItem("Send Paycheck", this, 50,
					KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK)));
			utility.add(new MenuItem("Pay Invoice", this, 51, 
					KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK)));
			utility.add(new MenuItem("Unlock", this, 52));
			utility.add(new MenuItem("Time Keeper", this, 53));
		
	}
	
	
	public void actionPerformed(ActionEvent e) {
		int cmd = Integer.parseInt(e.getActionCommand());
		switch(cmd){
		case -5:
		case -4:
		case -3:
		case -2:
		case -1: ModifyCategory.initializeFrame(Database.getProductCategories()[(cmd*-1)-1]); break;
		case 0: New.NewInstance(); break;
		case 1: Open.initializeFrame(); break;
		case 2:
			if(Database.getLastSavePath() == null) 
				SaveAs.initializeFrame();
			else if(new File(Database.getLastSavePath()).exists())
				Writer.save(Database.getLastSavePath());
			else
				SaveAs.initializeFrame(); break;
				
		case 3: SaveAs.initializeFrame(); break;
		
		case 10: CreateProductCategory.initializeFrame(); break;
		case 11: CreateServiceCategory.initializeFrame(); break;
		
		case 20: 
			Database.setTopRatedSeller(trs.isSelected());
			GUIEngine.noteDataChange(); break;
		case 21: SummaryActions.profitPercentage(); break;
		case 22: SummaryActions.reinvestmentRate(); break;
		case 23: SummaryActions.investmentGoal(); break;
		case 24: SummaryActions.payPalBalance(); break;
		case 25: SummaryActions.paypalFee(); break;
		case 26: SummaryActions.ebayInvoice(); break;
		case 27: SummaryActions.freeListings(); break;
		case 28: SummaryActions.saleLimits(); break;
		case 29: SummaryActions.reorderTollerance(); break;
		case 200: SummaryActions.subscriptionFee(); break;
		case 201: SummaryActions.salaryGoal(); break;
			
		case 40: GenericList.initializeFrame(); break;
		
		case 50: SummaryActions.sendPaycheck(); break;
		case 51: SummaryActions.payInvoice(); break;
		case 52: Unlock.initalizeFrame(); break;
		}
	}
}
