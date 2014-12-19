package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class DiagnoseProduct extends ActionFrame 
							 implements ActionListener,
							 			TreeSelectionListener {

	//----- Variables
	//UI
	public static DiagnoseProduct frame;
	
	//Data
	private static Product product;
	private Assembly copy;
	private Tree tree;
	private ComboBox<String> overall, color, quality;
	private JLabel focus;
	private TextField note;
	

	
	//----- Constructor
	public DiagnoseProduct() {
		super("Diagnose Product", 430, 320, new GridLayout(0,2));
		
		JPanel leftOut = new JPanel(new BorderLayout()), 
			   leftIn = new JPanel(new BorderLayout()), 
			   rightOut = new JPanel(new BorderLayout()), 
			   rightIn = new JPanel(new BorderLayout());
		
		
		//LEFT SIDE
		leftIn.setBorder(BorderManager.getTitleBorder(product.getFullName()));
		copy = new Assembly(product.getAssembly());
		leftIn.add(new JScrollPane(tree = new Tree(copy, this)), BorderLayout.CENTER);
		tree.expandRow(0);
		
		JPanel p = new JPanel(new GridLayout(1,0));
		p.add(new Button("Remove", this, 0));
		p.add(new Button("Decrement", this, 4));
		leftIn.add(p, BorderLayout.PAGE_END);
		leftOut.add(leftIn, BorderLayout.CENTER);
		
		leftOut.add(new JScrollPane(
				overall = new ComboBox<String>("", 
				product.getParent().getColors(), null, -1, "Overall Color")), 
				BorderLayout.PAGE_END);
	
		for(int i=0; i<product.getParent().getColors().length; i++)
			if(product.getParent().getColors()[i].equals(product.getOverallColor()))
				overall.setSelectedIndex(i+1);
		center.add(leftOut);
		
		//RIGHT SIDE
		rightIn.setBorder(BorderManager.getTitleBorder("Attributes"));
		JPanel attr = new JPanel(new GridLayout(0,1));
		attr.add(focus = new JLabel("", SwingConstants.CENTER));
		attr.add(color = new ComboBox<String>("", product.getParent().getColors(), this, 1, "Color"));
		attr.add(quality =  new ComboBox<String>("", Part.getQualityArray(), this, 2, "Quality"));
		rightIn.add(attr, BorderLayout.CENTER);
		
		rightOut.add(rightIn, BorderLayout.CENTER);
		
		rightOut.add(note = new TextField(product.getNote(), "Note"), BorderLayout.PAGE_END);
		center.add(rightOut);
		
		bottom.add(new Button("Diagnose", this, 3));
	}

	//----- Methods
	public static void initializeFrame(){
		product = DataSelection.getSelectedProduct();
		if(product == null) return;
		if(!product.getStatus().equals(Product.INCOMING)
				&& !product.getStatus().equals(Product.INVENTORY)
				&& !product.getStatus().equals(Product.STOCK)
				&& !product.getStatus().equals(Product.LISTED)
				&& !product.getStatus().equals(Product.RETURNED)){
			OptionPane.showConfirm(product.getFullName() + " must be in inventory to diagnose.", 
					"Product Status Error!");
			return;
		}
		frame = new DiagnoseProduct();
		frame.setVisible(true);
	}
	
	
	private void applyQualityChanges(Assembly a){
		
		if(a.getParts() != null){
			for(int i=0; i<a.getParts().length; i++)
				if(a.getParts()[i].getQuality() != null){
					if(!a.getParts()[i].getQuality().equals(Part.NO_QUALITY))
						if(quality.getSelectedIndex() == 0)
							a.getParts()[i].setQuality(null);
						else
							a.getParts()[i].setQuality((String) quality.getSelectedItem());
					
				}else if(quality.getSelectedIndex() == 0)
					a.getParts()[i].setQuality(null);
				else
					a.getParts()[i].setQuality((String) quality.getSelectedItem());
			
			if(a.getAssemblies() != null)
				for(int i=0; i<a.getAssemblies().length; i++)
					applyQualityChanges(a.getAssemblies()[i]);
		}
		
	}//End applyColorChanges()
	
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		
		if(tree.getSelectedNode() != null)
			if(tree.getSelectedNode().isAssembly()){	//Tree selection is Assembly
				
				Assembly a = tree.getSelectedNode().getAssembly();
				focus.setText(tree.getSelectedNode().getAssembly().getName());
				quality.setEnabled(true);
				color.setSelectedIndex(0);
				quality.setSelectedIndex(0);
				
				if(a.getColor() == null){
					color.setEnabled(true);
					color.setSelectedIndex(0);
				}else if(a.getColor().equals(Part.NO_COLOR)){
					color.setEnabled(false);
					color.setSelectedIndex(0);
				}else{
					color.setEnabled(true);
					for(int i=0; i<color.getItemCount(); i++)
						if(color.getItemAt(i).equals(a.getColor()))
							color.setSelectedIndex(i);
				}
				
			}else{						//Tree selection is Part
				Part current = tree.getSelectedNode().getPart();
				
				focus.setText(current.getName());
				
				//Enable/Disable CBox if part doesn't use color
				//If Part has a color, reflect it on the box
				if(current.getColor() == null){
					color.setEnabled(true);
					color.setSelectedIndex(0);
				}else if(current.getColor().equals(Part.NO_COLOR)){
					color.setEnabled(false);
					color.setSelectedIndex(0);
				}else{
					color.setEnabled(true);
					for(int i=0; i<color.getItemCount(); i++)
						if(color.getItemAt(i).equals(current.getColor()))
							color.setSelectedIndex(i);
				}
				
				//Enable/Disable CBox if part doesn't use quality
				//If Part has a quality, reflect it on the box
				if(current.getQuality() == null){
					quality.setEnabled(true);
					quality.setSelectedIndex(0);
				}else if(current.getQuality().equals(Part.NO_QUALITY)){
					quality.setEnabled(false);
					quality.setSelectedIndex(0);
				}else{
					quality.setEnabled(true);
					for(int i=0; i<quality.getItemCount(); i++)
						if(quality.getItemAt(i).equals(current.getQuality()))
							quality.setSelectedIndex(i);
				}
			}
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		int cmd = Integer.parseInt(arg0.getActionCommand());
		switch(cmd){
		case 0: tree.removeSelected(); break;
		
		
		case 1:	//Set Color to Part/Assembly
			if(tree.getSelectedNode() == null) break;
			
			if(tree.getSelectedNode().isPart())								//If Selection is Part
				if(tree.getSelectedNode().getPart().getColor() != null){				//If Color exists
					if(!tree.getSelectedNode().getPart().getColor().equals(Part.NO_COLOR))//If part uses color
						if(color.getSelectedIndex() == 0)					
							tree.getSelectedNode().getPart().setColor(null);			//Set null
						else
							tree.getSelectedNode().getPart().setColor((String) color.getSelectedItem()); 
																			//Set selected color
				}else if(color.getSelectedIndex() != 0)
					tree.getSelectedNode().getPart().setColor((String) color.getSelectedItem());
		
			
			if(tree.getSelectedNode().isAssembly())
				if(tree.getSelectedNode().getAssembly().getColor() != null){
					if(!tree.getSelectedNode().getAssembly().getColor().equals(Part.NO_COLOR))
						if(color.getSelectedIndex() == 0)
							tree.getSelectedNode().getAssembly().setColor(null);
						else
							tree.getSelectedNode().getAssembly().setColor((String) color.getSelectedItem());
					
				}else if(color.getSelectedIndex() != 0)
					tree.getSelectedNode().getAssembly().setColor((String) color.getSelectedItem());
			break;
			
			
		case 2: //Set Quality to Part/Assembly
			if(tree.getSelectedNode() == null) break;
			
			if(tree.getSelectedNode().isPart())
				if(tree.getSelectedNode().getPart().getQuality() != null){
					if(!tree.getSelectedNode().getPart().getQuality().equals(Part.NO_QUALITY))
						if(color.getSelectedIndex() == 0)
							tree.getSelectedNode().getPart().setQuality(null);
						else
							tree.getSelectedNode().getPart().setQuality((String) quality.getSelectedItem());
					
				}else if(quality.getSelectedIndex() == 0)
					tree.getSelectedNode().getPart().setQuality(null);
				else
					tree.getSelectedNode().getPart().setQuality((String) quality.getSelectedItem());
				
			if(tree.getSelectedNode().isAssembly())
				applyQualityChanges(tree.getSelectedNode().getAssembly());
			break;
			
			
		case 3:
			if(FormatChecker.stringCheck(note.getText())){
				
				if(overall.getSelectedIndex() == 0)	//Set overall Color
					product.setOverallColor(null);
				else
					product.setOverallColor((String) overall.getSelectedItem());
				
				product.setNote(note.getText());	//Set note
				
				copy.distributeCost(product.getAssembly().getTotalCost());
				product.setAssembly(copy);
				product.setStatus(Product.INVENTORY);
				
				GUIEngine.refresh(-1, -1);
				frame.dispose();
			} break;
			
			
		case 4: //Decrement part quantity
			if(tree.getSelectedNode() != null)
				if(tree.getSelectedNode().isPart())
					if(tree.getSelectedNode().getPart().getQuantity() > 1){
						
						tree.getSelectedNode().getPart().setQuantity(	//Decrement the part
								tree.getSelectedNode().getPart().getQuantity() - 1);
																		
																		//Update name
						tree.getSelectedNode().setUserObject(tree.getSelectedNode().getPart());
						
						tree.setSelectionRow(0); //Update tree 		//TODO could be better
					}
		}

	}

}
