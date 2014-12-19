package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class SaveAs extends ActionFrame
				    implements ActionListener,
				               TreeSelectionListener{

	//----- Variables
	//UI
	private static SaveAs frame;
	private JLabel path;
	private JTextField name;
	
	//Data
	private JTree tree;
	
	
	//----- Constructors
	public SaveAs() {
		super("Save As", 300, 300, new BorderLayout());
		
		String defaultPath = "Files/Instances";
		
		//Data
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new OFile(defaultPath));
		buildTree(root, new OFile(defaultPath));
		tree = new JTree(root);
		tree.addTreeSelectionListener(this);
		tree.setRootVisible(false);
		
		//UI
		JPanel container = new JPanel(new BorderLayout());
		container.add(path = new JLabel(" "), BorderLayout.PAGE_START);
		container.add(new JScrollPane(tree), BorderLayout.CENTER);
		center.add(container, BorderLayout.CENTER);
		container.add(name = new JTextField(), BorderLayout.PAGE_END);
		name.setPreferredSize(new Dimension(300,25));
		bottom.add(new Button("Save", this, 0));
		
		DefaultMutableTreeNode c = (DefaultMutableTreeNode) root.getFirstChild();
		tree.setSelectionPath(new TreePath(c.getPath()));	//Select first Dir
		tree.expandRow(0);	//Expand root
	}
	
	//----- Advanced Methods
	public static void initializeFrame(){
		frame = new SaveAs();
		frame.setVisible(true);
	}
	
	private void buildTree(DefaultMutableTreeNode node, OFile f){
		if(f.isDirectory()){
			DefaultMutableTreeNode child;
			node.add(child = new DefaultMutableTreeNode(f));
			File[] list = f.listFiles();
			for(int i=0; i<list.length; i++)
				buildTree(child, new OFile(list[i]));
		}else if(FormatChecker.extentionCheck(f)){
			node.add(new DefaultMutableTreeNode(f));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DefaultMutableTreeNode node =
				(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		
		if(node == null){ //If no selection has been made
			name.setText("Please select a location to save.");
			return;
		}
		
		OFile select = (OFile) node.getUserObject(); //Selected file
		OFile toSave = (select.isDirectory())?
				new OFile(select.getPath() + File.separator + name.getText()) : //New file
				new OFile(select.getPath());
		
		if(!toSave.getPath().contains("."))		//If file does not have extension
			toSave = new OFile(toSave.getPath() + ".osf"); //Give it one
		
		if(!FormatChecker.extentionCheck(toSave)){ //If invalid extension
			name.setText("Please use correct extention (.osf)");
			return;
		}
			
		
		if(select.isDirectory()){
			File[] peers = select.listFiles(); //Grab peer files in same dir
			for(int i=0; i<peers.length; i++)
				if(toSave.getName().equals(peers[i].getName())){ //If file of same name already exists
					int sel = OptionPane.showConfirm(
							"Are you sure want to replace " + toSave.getName() + "??", "Warning!");
					if(sel == 0){
						Writer.save(toSave.getPath()); //Save and close
						Database.setLastSavePath(toSave.getPath());
						frame.dispose();
						return; //Exit method
					}else{
						return; //Cancel save
					}
				}
			//No match was found, make new file
			toSave.getParentFile().mkdirs();
			try { toSave.createNewFile(); } catch (IOException e1) { return; } 
			Writer.save(toSave.getPath()); //Save and close
			Database.setLastSavePath(toSave.getPath());
			frame.dispose();
			
		}else{ //Node is file to overwrite
			int sel = OptionPane.showConfirm(
					"Are you sure want to replace " + toSave.getName() + "?", "Warning!");
			if(sel == 0){
				Writer.save(toSave.getPath()); //Save and close
				Database.setLastSavePath(toSave.getPath());
				frame.dispose();
			}
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node =
				(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		
		if(node == null) return; //NPE protection
		
		OFile select = (OFile) node.getUserObject();
		
		path.setText("  " + select.getPath()); //Path banner
		
		if(select.isDirectory()) //TextField 
			name.setText("");
		else
			name.setText(select.getPath());
	}
}
