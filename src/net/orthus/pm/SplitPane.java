package net.orthus.pm;


import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SplitPane extends JSplitPane 
					   implements ListSelectionListener,
					              ActionListener{
	//----- Variables
	private ItemSorter sorter;
	private int sort, dividerLoc;
	
	private JList<String> list;
	private JTextArea data;
	private JPopupMenu menu;
	private MenuItem delete, movePart, decrement, editNote, returnP, cancelRep, endPart, imageGallery;
	private JLabel menulabel;
	
	private Product[] pArray;
	private Service[] sArray;
	private Repair[] rArray;
	private Assembly[] aArray;
	private Constituent[] cArray;
	
	private String init;
	
	//----- Constructors
	public SplitPane(Product[] pArray, int sort){
		this.sort = sort;
		sorter = new ItemSorter(pArray);
		init = "No Product Selected.";
		dividerLoc = 45;
		buildPane();
	}
	public SplitPane(Service[] sArray, int sort){
		this.sort = sort;
		sorter = new ItemSorter(sArray);
		init = "No Service Selected.";
		dividerLoc = 80;
		buildPane();
	}
	public SplitPane(Repair[] rArray, int sort){
		this.sort = sort;
		sorter = new ItemSorter(rArray);
		init = "No Repair Selected.";
		dividerLoc = 70;
		buildPane();
	}
	public SplitPane(Assembly[] aArray){
		this.sort = -1;
		this.aArray = aArray;	//Assembly doesn't need sorting
		init = "No Assembly Selected.";
		dividerLoc = 90;
		buildPane();
	}
	public SplitPane(Constituent[] cArray, int sort){
		this.sort = sort;
		sorter = new ItemSorter(cArray);
		init = "No Part/Assembly Selected.";
		dividerLoc = 130;
		buildPane();
	}
	
	//----- Advanced Methods
	
	private String[] getContents(){
		switch(sort){
		case ItemSorter.SERVICE_LISTASC: sArray = sorter.serviceDateAscending(); break;
		
		case ItemSorter.PRODUCT_ID: pArray = sorter.productIDSort(); break;
		case ItemSorter.PRODCUT_ESTRETURN: pArray = sorter.productEstReturnSort(); break;
		case ItemSorter.PRODUCT_PURCHASC: pArray = sorter.productDateAscending(sort); break;
		case ItemSorter.PRODUCT_LISTASC: pArray = sorter.productDateAscending(sort); break;
		case ItemSorter.PRODUCT_RETURNASC: pArray = sorter.productDateAscending(sort); break;
		case ItemSorter.PRODUCT_SOLDDES: pArray = sorter.productDateDescending(); break;
		
		case ItemSorter.REPAIR_SOLDASC: rArray = sorter.repairDate(sort); break;
		case ItemSorter.REPAIR_SOLDDES: rArray = sorter.repairDate(sort); break;
		case ItemSorter.REPAIR_RETURNASC: rArray = sorter.repairDate(sort); break;
		
		case ItemSorter.CONST_PURCHASC: cArray = sorter.purchaseDate(); break;
		case ItemSorter.CONST_ALPH: cArray = sorter.alphabetical(); break;		
		}
		
		String[] listArray = null;
		
		if(pArray != null){	//Products
			listArray = new String[pArray.length];
			for(int i=0; i<pArray.length; i++)
				listArray[i] = "" + pArray[i].getID();
			
		}else if(sArray != null){	//Services
			listArray = new String[sArray.length];
			for(int i=0; i<sArray.length; i++)
				listArray[i] = sArray[i].getName();
			
		}else if(rArray != null){	//Repairs
			listArray = new String[rArray.length];
			for(int i=0; i<rArray.length; i++)
				listArray[i] = rArray[i].getName();
			
		}else if(aArray != null){	//Assemblies
			listArray = new String[aArray.length];
			for(int i=0; i<aArray.length; i++)
				listArray[i] = aArray[i].getName();
			
		}else if(cArray != null){	//Parts
			listArray = new String[cArray.length];
			for(int i=0; i<cArray.length; i++)
				listArray[i] = cArray[i].getName();
			
		}
		
		return listArray;
	}
	
	private void buildPane(){

		String[] s = getContents();
		list = (s == null)? list = new JList<String>() : new JList<String>(s);
		
		this.setLeftComponent(new JScrollPane(list));
		this.setRightComponent(new JScrollPane(data = new JTextArea(init)));
		this.setDividerLocation(dividerLoc);
	
		list.addListSelectionListener(this);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		data.setCaretPosition(0);
		data.setEditable(false);
		
		//Menu 
		menu = new JPopupMenu();
		if(cArray != null){  //Part/Assembly
			menu.add(movePart = new MenuItem("", this, 1));
			menu.add(decrement = new MenuItem("", this, 2));
			menu.add(endPart = new MenuItem("End Listing", this, 6));
			endPart.setVisible(false);
		}
		if(pArray != null){
			menu.add(menulabel = new JLabel());
			menu.add(editNote = new MenuItem("", this, 3));
			menu.add(returnP = new MenuItem("", this, 4));
			menu.add(imageGallery = new MenuItem("Copy Gallery HTML", this, 7));
			returnP.setVisible(false);
		}
		if(rArray != null){
			menu.add(cancelRep = new MenuItem("Cancel", this, 5));
			cancelRep.setVisible(false);
		}
		
		menu.add(delete = new MenuItem("", this, 0));
		
		list.addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e){
				if(SwingUtilities.isRightMouseButton(e)){
					int row = list.locationToIndex(e.getPoint());
					
					//Don't allow Default templates to be deleted
					if(aArray != null)
						if(aArray[row].getStatus().equals(Assembly.DEFAULT_TEMPLATE))
							return;
					
					list.setSelectedIndex(row); //Set DataSelection
					//Cancel Repair
					if(rArray != null)
						if(DataSelection.getSelectedRepair().getStatus().equals(Repair.PENDING))
							cancelRep.setVisible(true);
						
					
					//Return Product
					if(pArray != null)
						if(DataSelection.getSelectedProduct().getStatus().equals(Product.INCOMING)){
							returnP.setVisible(true);
							returnP.setText("Return");
						}
					
					//Delete
					if(pArray != null){
						menulabel.setText("     " + DataSelection.getSelectedProduct().getFullName());
						delete.setText("Delete");
					}else
						delete.setText("Delete " + list.getSelectedValue());
					
					//Move Part
					if(cArray != null){
						Constituent c = DataSelection.getSelectedConstituent();
						if(c.getStatus().contains("Listed"))
							endPart.setVisible(true);
						
						try{
							Part p = (Part) c;
							movePart.setVisible(true);
							movePart.setText("Move " + p.getName());
						}catch(ClassCastException ex){
							movePart.setVisible(false);
						}
					}
					
					//Decrement Part
					if(cArray != null){
						decrement.setVisible(true);
						decrement.setText("Decrement " + DataSelection.getSelectedConstituent().getName());
					}
					
					//Edit Note
					if(pArray != null){
						editNote.setVisible(true);
						editNote.setText("Edit Note");
					}
					
					menu.show(e.getComponent(), e.getX(), e.getY());
					
				}
			}
		});
		
	}
	
	public void setNewData(Constituent[] c){
		
		if(c == null){
			String[] s = {};
			list.setListData(s);
			data.setText("");
		}else{
			sorter = new ItemSorter(c);
			list.setListData(getContents());
		}
	}
	
	//----- Interfaces
	public void valueChanged(ListSelectionEvent e) {
		//TODO Set text to bottom bar
		
		int index = list.getSelectedIndex();
		
		if(pArray != null){
			data.setText(pArray[index].getData());
			DataSelection.setSelection(pArray[index], null, null, null);
		}else if(rArray != null){
			data.setText(rArray[index].getData());
			DataSelection.setSelection(null, null, null, rArray[index]);
		}else if(sArray != null){
			data.setText(""); //TODO after method in Service is created
			DataSelection.setSelection(null, null, sArray[index], null);
		}else if(cArray != null && index != -1){
			data.setText(cArray[index].getData()); 
			DataSelection.setSelection(null, cArray[index], null, null);
		}else if(aArray != null){
			data.setText(aArray[index].getData());
			DataSelection.setSelection(null, aArray[index], null, null);
		}
	    
	    data.setCaretPosition(0);
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		int cmd = Integer.parseInt(arg0.getActionCommand());
		switch(cmd){
		
		case 0: // Delete Constituent
			Product p = DataSelection.getSelectedProduct();
			Service s = DataSelection.getSelectedService();
			Repair r = DataSelection.getSelectedRepair();
			Constituent c = DataSelection.getSelectedConstituent();
			
			
			if(p != null){
				p.getParent().removeProduct(p);
			}else if(s != null){
				s.getServiceCategory().removeService(s);
			}else if(r != null){
				r.getParent().removeRepair(r);
			}else{
				
				try{
					Assembly a = (Assembly) c;
					
					if(a.getStatus().equals(Assembly.PART_TEMPLATE))
						Database.getFocusedCategory().removeAssemblyFromTemplates(a);
					else
						Database.getFocusedCategory().removeAssemblyFromSupply(a);
					
				}catch(ClassCastException e){
					
					Part pt = (Part) c;
					Database.getFocusedCategory().removePartFromSupply(pt);
				}
			}
			GUIEngine.refresh(-1, -1);
			break;
			
		case 1:  //Move Part
			Part prt = (Part) DataSelection.getSelectedConstituent();
			MovePart.initalizeFrame(prt);
			break;
			
		case 2: //Decrement Part
			DecrementPart.initializeFrame(DataSelection.getSelectedConstituent());
			break;
			
		case 3: //Edit Note
			String o = (DataSelection.getSelectedProduct().getNote() != null)? 
					DataSelection.getSelectedProduct().getNote() : "";
			String n = OptionPane.showInput(o, "Edit Note");
			
			if(n != null){
				if(n.equals(""))
					n = null;
				DataSelection.getSelectedProduct().setNote(n);
				GUIEngine.noteDataChange();
			}
			break;
			
		case 4: //Return Product
			Product p1 = DataSelection.getSelectedProduct();
			Database.addToPayPalBalance(p1.getAssembly().getTotalCost());
			p1.getParent().removeProduct(p1);
			GUIEngine.refresh(-1, -1);
			break;			
			
		case 5: //Cancel Repair
			DataSelection.getSelectedRepair().cancelRepair();
			GUIEngine.refresh(-1, -1);
			break;
			
		case 6: //End listing
			c = DataSelection.getSelectedConstituent();
			Category cat = Database.getFocusedCategory();
			try{
				cat.removePartFromSupply((Part) c);
				c.setStatus(Constituent.SUPPLY);
				cat.addPartToSupply((Part) c);
			}catch(ClassCastException e){
				cat.removeAssemblyFromSupply((Assembly) c);
				c.setStatus(Constituent.SUPPLY);
				cat.addAssemblyToSupply((Assembly) c);
			}
			GUIEngine.refresh(-1, -1);
			break;
			
		case 7: //Copy Gallery 
			

			p = DataSelection.getSelectedProduct();
			
			String upPath = "";
			switch(p.getName()){
			case ProductCategory.DS_LITE: 
				upPath = upPath +  "dsl/prd/"; break;
			case ProductCategory.DSI:
				upPath = upPath +  "dsi/prd/"; break;
			case ProductCategory.DSI_XL:
				upPath = upPath +  "dsx/prd/"; break;
			case ProductCategory.DS3:
				upPath = upPath +  "3ds/prd/"; break;
			case ProductCategory.DS3_XL:
				upPath = upPath +  "3dx/prd/"; break;
			}
			upPath = upPath + p.getID();
			
			if(!new File("ProductImageArchive/" + upPath).exists()){
				
				OptionPane.showError("No image archive exists for " + p.getFullName() + ".", "File System Error");
				return;
			}
			
			String fmt = "<a class=\"thumbnail\" href=\"%s\"><img src=\"%s\" width=\"100px\" height=\"75px\"" + 
					"border=\"0\" /><span><img src=\"%s\" width=\"400\" height=\"300\"/></span></a>";
			
			String gallery = "";
			int number = new File("ProductImageArchive/" + upPath).listFiles().length;
			upPath = "http://www.orthus.net/pi/" + upPath;
			
			for(int i=0; i<number; i++)
				gallery = gallery + String.format(fmt, 
						upPath + (i + 1) + ".jpg", 
						upPath + (i + 1) + ".jpg", 
						upPath + (i + 1) + ".jpg");		
			
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(gallery), null);
			break;
		}
		
	
		
	}

}
