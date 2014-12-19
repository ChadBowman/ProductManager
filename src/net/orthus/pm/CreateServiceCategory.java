package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class CreateServiceCategory extends ActionFrame
								   implements ActionListener,
								              TreeSelectionListener{
	

	//----- Variables
	//UI
	private static CreateServiceCategory frame;
	private JTextField addAssemblyField,
					   partName,
					   partPrice,
					   partQuantity,
					   feeRateField,
					   name;
	
	private JPanel treePanel,
				   addPartPanel,
				   addAssemblyPanel;
	
	//Data for ServiceCategory creation
	private ServiceCategory toAdd;
	private Assembly[] templates;
	
	//Utility & Data Structure
	private Tree tree;
	private JCheckBox colored, quality;
	private Button remove;

	//----- Constructor
	public CreateServiceCategory() {
		super("Build Service Category", 480, 456, new GridLayout(0,2));
	
		//Separate two halves
		JPanel left, right;
		center.add(left = new JPanel(new GridLayout(0,1)));
		center.add(right = new JPanel());
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		
		//Data initialization
		templates = new Assembly[1];
		templates[0] = new Assembly(Database.getNewSerial(),
									1,
									0,
									null, 
									Assembly.DEFAULT_TEMPLATE,
									Assembly.DEFAULT_TEMPLATE,
									Assembly.NO_COLOR,
									Date.getTodaysDate(),
									null,
									null,
									null,
									null,
									null);
		
		//LEFTSIDE
		//Tree Module
		treePanel = new JPanel(new BorderLayout());
		treePanel.setBorder(BorderManager.getTitleBorder("Default Assembly Heiarchy"));
		treePanel.add(new JScrollPane(tree =  new Tree(templates[0], this)), BorderLayout.CENTER);
		treePanel.add(remove = new Button("Remove", this, 4), BorderLayout.PAGE_END);
		left.add(treePanel);
				
		
		//RIGHT SIDE
		//Category Name
		JPanel setName = new JPanel(new GridLayout());
		setName.setBorder(BorderManager.getTitleBorder("Category Name"));
		setName.add(name = new JTextField());
		right.add(setName);
		
		//Add Assembly
		addAssemblyPanel = new JPanel();
		addAssemblyPanel.setLayout(new BoxLayout(addAssemblyPanel, BoxLayout.X_AXIS));
		addAssemblyPanel.setBorder(BorderManager.getTitleBorder("Add Assembly"));
		right.add(addAssemblyPanel);
		addAssemblyPanel.add(addAssemblyField = new JTextField());
		Button assButton = new Button("Add", this, 1);
		addAssemblyPanel.add(assButton);
	
		//Add Part
		addPartPanel = new JPanel(new GridLayout(0,1));
		addPartPanel.setBorder(BorderManager.getTitleBorder("Add Part"));
		right.add(addPartPanel);
		addPartPanel.add(new JLabel("Name"));
		addPartPanel.add(partName = new JTextField());
		addPartPanel.add(new JLabel("Unit Price"));
		addPartPanel.add(partPrice = new JTextField());
		addPartPanel.add(new JLabel("Quantity"));
		addPartPanel.add(partQuantity = new JTextField());
		JPanel options = new JPanel(new GridLayout(0,2));
		options.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		options.add(colored = new JCheckBox("Color"));
		options.add(quality = new JCheckBox("Quality"));
		addPartPanel.add(options);
		Button addPart = new Button("Add Part", this, 2);
		addPartPanel.add(addPart);
		
		//Final Value Fee
		JPanel fee = new JPanel(new GridLayout());
		fee.setBorder(BorderManager.getTitleBorder("Final Value Fee Rate (%)"));
		fee.add(feeRateField = new JTextField());
		right.add(fee);
		
		//Submit
		Button submit = new Button("Build Category", this, 3);
		bottom.add(submit);
		
	}//End Constructor
	
	//----- Private Methods
	//NOTICE: toADD must be initialized with a name first
	private void setAddresses(Assembly root){
		root.setParentCategoryAddress(toAdd.getAddress());
		
		if(root.getAssemblies() != null)
			for(int i=0; i<root.getAssemblies().length; i++)
				setAddresses(root.getAssemblies()[i]);
	}

	//----- Public Methods
	public static void initializeFrame() {
		frame = new CreateServiceCategory();
		frame.setVisible(true);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
	
		if(tree.getSelectedNode() == null) return;
		
		if(tree.getSelectedNode().isAssembly()){ //Selection is Assembly
			
			addAssemblyPanel.setBorder(BorderManager.getTitleBorder(
					"Add Assembly to " + tree.getSelectedNode().getAssembly().getName()));
			
			addPartPanel.setBorder(BorderManager.getTitleBorder(
					"Add Part to " + tree.getSelectedNode().getAssembly().getName()));
			
			remove.setText("Remove Assembly");
			
		}else{	//Selection is Part

			addAssemblyPanel.setBorder(BorderManager.getTitleBorder("Add Assembly"));
			addPartPanel.setBorder(BorderManager.getTitleBorder("Add Part"));
			remove.setText("Remove Part");
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		int val = Integer.parseInt(arg0.getActionCommand());
		switch(val){
		
		case 1:			//Add Assembly
			if(tree.getSelectedNode() == null) break;
			
			if(!tree.getSelectedNode().isAssembly())
				addAssemblyField.setText("No Assembly Selected");
			else if(FormatChecker.blankStringCheck(addAssemblyField.getText(), "Assembly Name")){
				Assembly newAss = new Assembly(
						Database.getNewSerial(),
						1,
						0,
						null,
						addAssemblyField.getText(),
						Assembly.DEFAULT_TEMPLATE,
						Assembly.NO_COLOR,
						Date.getTodaysDate(),
						null,
						null,
						null,
						null, 
						null);
				
				tree.addAssembly(newAss, tree.getSelectedNode());
				tree.expandCurrentPath();
				addAssemblyField.setText("");
				
			} break;
			
		case 2:		//Add Part
			if(tree.getSelectedNode() == null) break;
			
			if(!tree.getSelectedNode().isAssembly())
				partName.setText("No Assembly Selected");
			else if(partName != null && 
					FormatChecker.blankStringCheck(partName.getText(), "Part Name") &&
					FormatChecker.creditCheck(partPrice.getText()) &&
					FormatChecker.quantityCheck(partQuantity.getText())){
				
				String c = (colored.isSelected())? null : Part.NO_COLOR;
				String q = (quality.isSelected())? null : Part.NO_QUALITY;
				
				
				Part newP = new Part(
						Database.getNewSerial(),
						Integer.parseInt(partQuantity.getText()),
						0,
						partName.getText(),
						null,
						c,
						q,
						new Credit(partPrice.getText()),
						null,
						null,
						Date.getTodaysDate(),
						null,
						null);
				
				tree.addPart(newP, tree.getSelectedNode());
				tree.expandCurrentPath();
				partName.setText("");
				partQuantity.setText("");
				partPrice.setText("");
				
			} break;
			
		case 3:		//Submit
			if(FormatChecker.percentageCheck(feeRateField.getText())
					&& FormatChecker.blankStringCheck(name.getText(), "Category Name")){
				//Proceed if there are valid values in name & percentage fields
				
				toAdd = new ServiceCategory( //Build new Product Category
						name.getText(),
						Double.parseDouble(feeRateField.getText()) / 100,
						templates,
						null,
						null,
						null);
				
				templates[0].setName(name.getText());
				setAddresses(templates[0]);
				
				Database.addServiceCategory(toAdd);	//Add category to database
				frame.dispose();
				GUIEngine.refresh(-1, -1);
			}
			break;
			
		case 4: tree.removeSelected();
				
		}
	}

}
