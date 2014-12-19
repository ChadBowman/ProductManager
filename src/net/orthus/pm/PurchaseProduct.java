package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class PurchaseProduct extends ActionFrame 
							 implements ActionListener {
	
	//----- Variables
	//UI
	private static PurchaseProduct frame;
	
	//Data
	private Tree tree;
	
	private int id;
	private String color;
	private TextField amt, date, note;
	private ComboBox<String> colors;
	private ProductCategory pc;
	private Assembly ass;
	private JCheckBox pay;

	//----- Constructor
	public PurchaseProduct() {
		super("Purchase Product", 450, 350, new GridLayout(0,2));
		
		//Data initialization
		ass = new Assembly(Database.getFocusedCategory().getDefaultAssembly());
		pc = Database.getFocusedCategory().getDefaultAssembly().getProductCategory();
		id = pc.getNextID();
		
		
		//Separate two halves
		JPanel left, right;
		center.add(left = new JPanel(new BorderLayout()));
		center.add(right = new JPanel(new GridLayout(0,1)));
		
		//Left half
		left.setBorder(BorderManager.getTitleBorder("Usable Parts"));
		left.add(new JScrollPane(tree = new Tree(ass, null)));
		tree.expandRow(0);
		JPanel btn = new JPanel(new GridLayout());
		btn.add(new Button("Remove", this, 0));
		btn.add(new Button("Decrement", this, 3));
		left.add(btn, BorderLayout.PAGE_END);
		
		//Right half
		right.add(new Label(pc.getName() + " " + id, "ID", SwingConstants.CENTER));
		right.add(amt = new TextField("Purchase Amount"));
		right.add(colors = new ComboBox<String>("", pc.getColors(), this, 1, "Overall Color"));
		right.add(date = new TextField(Date.getTodaysDate().displaySimpleDate(), "Date Purchased"));
		right.add(note = new TextField("Note"));
		
		//Bottom
		bottom.add(new Button("Purchase", this, 2));
		bottom.add(pay = new JCheckBox("PayPal"));
		pay.setSelected(true);
	
	}
	
	//----- Methods
	public static void initializeFrame(){
		frame = new PurchaseProduct();
		frame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		int cmd = Integer.parseInt(arg0.getActionCommand());
		switch(cmd){
		
		case 0:  //Remove part/assembly
			if(tree.getSelectedNode() != tree.getRoot())
				tree.removeSelected(); break;
				
		case 1: //Update color var
			color = (colors.getSelectedIndex() == 0)? null :
				(String) colors.getSelectedItem(); break;
				
		case 2: //Submit (purchase product)
			if(FormatChecker.creditCheck(amt.getText())
					&& FormatChecker.dateCheck(date.getText())
					&& FormatChecker.stringCheck(note.getText())){
				
				ass.distributeCost(new Credit(amt.getText()));
				ass.distributeStatus(Part.PRODUCT);
				
				pc.addProduct(new Product(
						pc.getAddress(),
						id,
						0,
						false,
						Product.INCOMING,
						color,
						note.getText(),
						null,
						null,
						new Credit(amt.getText()),
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						new Date(date.getText()),
						null,
						null,
						null,
						ass));
				
				if(pay.isSelected())
					Database.getPayPalBalance().subtract(new Credit(amt.getText()));
				
				frame.dispose();
				GUIEngine.refresh(-1, -1);
				
			}
			break;
		
		case 3: //Decrement part quantity
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
