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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class ModifyCategory extends ActionFrame 
					        implements ActionListener,
							 		   TreeSelectionListener{
	
	private static final long serialVersionUID = -8532014786394549220L;
	
	//----- Variables
	//UI
	private static ModifyCategory frame;
	private JTextArea colorArea;
	private JTextField addColorField,
					   addAssemblyField,
					   partName,
					   partPrice,
					   partQuantity,
					   feeRateField,
					   feeRateField2,
					   name;
	
	private JPanel treePanel,
				   addPartPanel,
				   addAssemblyPanel;
	
	//Data for ProductCategory
	private ProductCategory cat;
	private Assembly[] templates;
	private String[] colors;
	
	
	//Utility & Data Structure
	private Tree tree;
	private JCheckBox colored, quality;
	private Button remove;

	//----- Constructor
	public ModifyCategory(ProductCategory cat) {
		super("Modify Category", 480, 455, new GridLayout(0,2));
	
		this.cat = cat;
		
		//Separate two halves
		JPanel left, right;
		center.add(left = new JPanel(new BorderLayout()));
		center.add(right = new JPanel());
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		
		//Data initialization
		templates = new Assembly[cat.getTemplates().length];
		
		for(int i=0; i<cat.getTemplates().length; i++)
			templates[i] = cat.getTemplates()[i];
		
		
		
		//LEFTSIDE
		//Tree Module
		JPanel cont = new JPanel(new GridLayout(0,1));
		left.add(cont, BorderLayout.CENTER);
		treePanel = new JPanel(new BorderLayout());
		treePanel.setBorder(BorderManager.getTitleBorder("Default Assembly Heiarchy"));
		treePanel.add(new JScrollPane(tree = new Tree(templates[0], this)), BorderLayout.CENTER);
		tree.expandRow(0);
		treePanel.add(remove = new Button("Remove", this, 4), BorderLayout.PAGE_END);
		cont.add(treePanel);
	
		//Color Module
		JPanel colors;
		cont.add(colors = new JPanel(new BorderLayout()));
		colors.setBorder(BorderManager.getTitleBorder("Availible Colors"));
		colors.add(new JScrollPane(colorArea = new JTextArea()), BorderLayout.CENTER);
		if(cat.getColors() != null)
			for(int i=0; i<cat.getColors().length; i++)
				colorArea.append(cat.getColors()[i] + '\n');
		colorArea.setEditable(true);
		getColors();
				
		
		//RIGHT SIDE
		//Category Name
		JPanel setName = new JPanel(new GridLayout());
		setName.setBorder(BorderManager.getTitleBorder("Category Name"));
		setName.add(name = new JTextField(cat.getName()));
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
		JPanel buttons = new JPanel(new GridLayout(0,2));
		Button updatePart = new Button("Update Part", this, 5);
		Button addPart = new Button("Add Part", this, 2);
		buttons.add(updatePart);
		buttons.add(addPart);
		addPartPanel.add(buttons);
		
		//Final Value Fee
		JPanel fee = new JPanel(new GridLayout());
		fee.setBorder(BorderManager.getTitleBorder("Product Final Value Fee Rate (%)"));
		fee.add(feeRateField = new JTextField("" + cat.getProductFinalValueFeeRate() * 100));
		left.add(fee, BorderLayout.PAGE_END);
		
		JPanel fee2 = new JPanel(new GridLayout());
		fee2.setBorder(BorderManager.getTitleBorder("Service Final Value Fee Rate (%)"));
		fee2.add(feeRateField2 = new JTextField("" + cat.getServiceFinalValueFeeRate() * 100));
		right.add(fee2);
		
		//Submit
		Button submit = new Button("Update Category", this, 3);
		bottom.add(submit);
		
	}//End Constructor
	
	//----- Private Methods
	//NOTICE: toADD must be initialized with a name first
	private void setAddresses(Assembly root){
		root.setParentCategoryAddress(cat.getAddress());
		
		if(root.getAssemblies() != null)
			for(int i=0; i<root.getAssemblies().length; i++)
				setAddresses(root.getAssemblies()[i]);
	}
	
	//Populates color textArea
	private void getColors(){
		if(colors == null) return;
		colorArea.setText("");
		for(int i=0; i<colors.length; i++)
			colorArea.append("  " + colors[i] + "\n");
	}

	//----- Public Methods
	public static void initializeFrame(ProductCategory cat) {
		frame = new ModifyCategory(cat);
		frame.setVisible(true);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		
		if(tree.getSelectedNode() == null) return;
		
		if(tree.getSelectedNode().isAssembly()){
			Assembly a = tree.getSelectedNode().getAssembly();
			addAssemblyPanel.setBorder(
					BorderManager.getTitleBorder("Add Assembly to " + a.getName()));
			addPartPanel.setBorder(
					BorderManager.getTitleBorder("Add Part to " + a.getName()));
			remove.setText("Remove Assembly");
		}else{
			addAssemblyPanel.setBorder(
					BorderManager.getTitleBorder("Add Assembly"));
			addPartPanel.setBorder(
					BorderManager.getTitleBorder("Add Part"));
			remove.setText("Remove Part");
			
			Part p = tree.getSelectedNode().getPart();

			partName.setText(p.getName());
			partPrice.setText("" + p.getPrice().getValueInDollars());
			partQuantity.setText("" + p.getQuantity());
			
			if(p.getQuality() != null)
				quality.setSelected(false);
			else
				quality.setSelected(true);
				
			
			if(p.getQuality() != null)
				colored.setSelected(false);
			else
				colored.setSelected(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		int val = Integer.parseInt(arg0.getActionCommand());
		switch(val){
		case 0: 		//Add Color
			if(!FormatChecker.blankStringCheck(addColorField.getText(), "Color Name")) break;
			colors = ArrayManager.addToArray(addColorField.getText(), colors);
			getColors(); 
			addColorField.setText(""); break;
		
		case 1:			//Add Assembly
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
						null, null, null,
						null, null);
				
				
				System.out.println(newAss.getQuantity());
				tree.addAssembly(newAss, tree.getSelectedNode());
				tree.expandCurrentPath(); //Show new stuff
				
				addAssemblyField.setText("");
			} break;
			
		case 2:		//Add Part
			if(tree.getSelectedNode() != null)
				if(!tree.getSelectedNode().isAssembly())
					partName.setText("No Assembly Selected");
				else if(FormatChecker.blankStringCheck(partName.getText(), "Part Name") 
						&& FormatChecker.creditCheck(partPrice.getText()) 
						&& FormatChecker.quantityCheck(partQuantity.getText())){
					
					String c = (colored.isSelected())? null : Part.NO_COLOR;
					String q = (quality.isSelected())? null : Part.NO_QUALITY;
					
					Part newP = new Part(
							Database.getNewSerial(),
							Integer.parseInt(partQuantity.getText()),
							0,
							partName.getText(),
							Part.TEMPATE,
							q,
							c,
							new Credit(partPrice.getText()),
							null, null,
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
					&& FormatChecker.percentageCheck(feeRateField2.getText())
					&& FormatChecker.blankStringCheck(name.getText(), "Category Name")){
				//Proceed if there are valid values in name & percentage fields
				
				colors = colorArea.getText().split("\n");

				
				//cat.setName(name.getText()); //TODO Not working
				cat.setProductFinalValueFeeRate(Double.parseDouble(feeRateField.getText()) / 100);
				cat.setServiceFinalValueFeeRate(Double.parseDouble(feeRateField2.getText()) / 100);
				cat.setTemplates(templates);
				cat.setColors(colors);
				
				//templates[0].setName(name.getText());
				//setAddresses(templates[0]);
				
				frame.dispose();
				GUIEngine.refresh(-1, -1);
			}
			break;
			
		case 4: //Remove part/ass
			tree.removeSelected(); 
			break;
			
		case 5: //Update Part
			Part p = tree.getSelectedNode().getPart();
			
			if(FormatChecker.quantityCheck(partQuantity.getText())
					&& FormatChecker.creditCheck(partPrice.getText())
					&& FormatChecker.stringCheck(partName.getText())){
				
				p.setName(partName.getText());
				p.setQuantity(Integer.parseInt(partQuantity.getText()));
				p.setPrice(new Credit(partPrice.getText()));
				
				if(quality.isSelected())
					p.setQuality("");
				else
					p.setQuality(Part.NO_QUALITY);
				
				if(colored.isSelected())
					p.setColor("");
				else
					p.setColor(Part.NO_COLOR);
				
				break;
			
			}
		}
	}
}
