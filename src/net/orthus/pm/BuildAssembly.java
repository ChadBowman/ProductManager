package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class BuildAssembly extends ActionFrame 
						   implements ActionListener {
	//----- Variables
	//UI
	private static BuildAssembly frame;
	
	private TextField name, cost;
	private Tree tree1, tree2;
	private JCheckBox color;
	
	//Data
	private Category category;
	private Assembly toBuild;

	//----- Constructor
	public BuildAssembly() {
		super("Build Assembly", 275, 490, new GridLayout(), null, new FlowLayout());
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		
		//Get Data
		category = Database.getFocusedCategory();
		
		
		top.add(name = new TextField("Name"));
		center.add(cost = new TextField("Cost Per Assembly"));
		
		
		JPanel treePane1 = new JPanel(new BorderLayout());
		treePane1.setBorder(BorderManager.getTitleBorder("Availible Parts"));
		treePane1.add(new JScrollPane(
				tree1 = new Tree(new Assembly(category.getDefaultAssembly()), null)), BorderLayout.CENTER);
		tree1.expandRow(0);
		treePane1.setMinimumSize(new Dimension(0,140));
		treePane1.add(new Button("Use In Assembly", this, 0), BorderLayout.PAGE_END);
		center.add(treePane1);
		
		toBuild = new Assembly(
				Database.getNewSerial(),
				1,
				0,
				category.getAddress(),
				"New Assembly",
				Assembly.PART_TEMPLATE,
				null,
				Date.getTodaysDate(),
				null,
				null,
				null,
				null,
				null);
		
		JPanel treePane2 = new JPanel(new BorderLayout());
		treePane2.setBorder(BorderManager.getTitleBorder("New Assembly"));
		treePane2.setMinimumSize(new Dimension(0,140));
		treePane2.add(new JScrollPane(tree2 = new Tree(toBuild, null)), BorderLayout.CENTER);
		treePane2.add(new Button("Remove", this, 2), BorderLayout.PAGE_END);
		center.add(treePane2);
		
		center.add(color = new JCheckBox("Uses Color"));
		
		bottom.add(new Button("Build Assembly", this, 1));
	
	}
	
	//----- Methods
	public static void initializeFrame(){
		frame = new BuildAssembly();
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		int cmd = Integer.parseInt(arg0.getActionCommand());
		switch(cmd){
		case 0:		//Move Assembly/part
			if(tree1.getSelectedNode() != null)
				if(tree1.getSelectedNode().isAssembly())
					tree2.addAssembly(tree1.getSelectedNode().getAssembly(), tree2.getRoot());
				else
					tree2.addPart(tree1.getSelectedNode().getPart(), tree2.getRoot());
			
			tree1.removeSelected();
			tree2.expandRow(0);
			break;
			
		case 1:		//Submit new Assembly as template
			if(FormatChecker.blankStringCheck(name.getText(), "Name")
					&& FormatChecker.creditCheck(cost.getText())){
				
				toBuild.setName(name.getText());
				toBuild.distributeCost(new Credit(cost.getText()));
				if(!color.isSelected())
					toBuild.setColor(Assembly.NO_COLOR);
				
				category.addTemplate(toBuild);
				GUIEngine.refresh(-1, -1);
				frame.dispose();
			}
			break;
			
		case 2: tree2.removeSelected(); break;
		}
	}

}
