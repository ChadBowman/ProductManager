package net.orthus.pm;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class MovePart extends ActionFrame 
					  implements ActionListener {

	private static MovePart frame;
	private static Part toMove;
	
	private ComboBox<String> catss;
	private TextField quant;
	private Button send;
	
	private MovePart(){
		super("Move Part", 200, 220, new GridLayout(0,1));
		
		
		//Get cats with same part name
		String[] catnames = null;
		ServiceCategory[] cats = Database.getAllServiceCategories();
		String current = Database.getFocusedCategory().getName();
		
		
		
		for(int i=0; i<cats.length; i++)
			if(!cats[i].getName().equals(current)){
				Part[] pts = cats[i].getDefaultAssembly().collapseAssembly();
				
				if(pts != null)
					for(int j=0; j<pts.length; j++)
						if(toMove.getName().equals(pts[j].getName())){
							catnames = ArrayManager.addToArray(cats[i].getName(), catnames);
							break;
						}
			}
		
		
		bottom.add(send = new Button("Send", this, 0));
		
		if(catnames == null){
			catnames = ArrayManager.addToArray("No Categories Availible", catnames);
			send.setEnabled(false);
		}
		
		center.add(new JLabel(toMove.getName(), SwingConstants.CENTER));
		center.add(catss = new ComboBox<String>(null, catnames, null, -1, "Category To Send Part To"));
		center.add(quant = new TextField("" + toMove.getQuantity(), "Quantity"));
		
	}

	public static void initalizeFrame(Part p){
		toMove = p;
		frame = new MovePart();
		frame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(!FormatChecker.quantityCheck(quant.getText())) return;
		
		int q = Integer.parseInt(quant.getText());
		
		ServiceCategory category = null;
		int serial = 0;
		
		for(int i=0; i<Database.getAllServiceCategories().length; i++)
			if(Database.getAllServiceCategories()[i].getName().equals((String) catss.getSelectedItem()))
				category = Database.getAllServiceCategories()[i];
		
		if(category != null){
			Part[] ps = category.getDefaultAssembly().collapseAssembly();
			for(int i=0; i<ps.length; i++)
				if(ps[i].getName().equals(toMove.getName()))
					serial = ps[i].getSerial();
		}
		
		if(q > toMove.getQuantity()){
			OptionPane.showError(OptionPane.QUANTITY_LIMIT, "Max: " + toMove.getQuantity());
			quant.setText("" + toMove.getQuantity());
			return;
			
		}else if(q < toMove.getQuantity()){
			toMove.setQuantity(toMove.getQuantity() - q);
			
			Part clone = new Part(toMove);
			clone.setSerial(serial);
			clone.setQuantity(q);
			category.addPartToSupply(clone);
			
		}else{
			
			toMove.setSerial(serial);
			category.addPartToSupply(toMove);
			Database.getFocusedCategory().removePartFromSupply(toMove);
		}

		GUIEngine.refresh(-1,  -1);
		frame.dispose();
		
	}

}
