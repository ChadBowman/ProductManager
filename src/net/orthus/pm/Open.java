package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class Open extends ActionFrame
					      implements ActionListener,
					      			 TreeSelectionListener{
	
	//----- Variables
	//UI
	private static Open frame;
	private JLabel path;
	
	//Data
	private JTree tree;

	//----- Constructor
	public Open() {
		super("Open", 300, 300, new BorderLayout());
		
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
		bottom.add(new Button("Open", this, 0));
		
		DefaultMutableTreeNode c = (DefaultMutableTreeNode) root.getFirstChild();
		tree.setSelectionPath(new TreePath(c.getPath()));	//Select first Dir
		tree.expandRow(0);	//Expand root
	}

	//----- Advanced Methods
	public static void initializeFrame(){
		frame = new Open();
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
	public void valueChanged(TreeSelectionEvent arg0) {
		DefaultMutableTreeNode node =
				(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		
		if(node == null) return; //NPE protection
		
		OFile select = (OFile) node.getUserObject();
		if(select.isDirectory())
			path.setText(" ");
		else
			path.setText("  " + select.getPath()); //Path banner
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(path.getText().equals(" ")) return;
		Reader read = new Reader();
		frame.dispose();
		if(read.load(new File(path.getText().substring(2)))) 
			Database.prepare();
		
		GUIEngine.refresh(GUIEngine.SUMMARY, -1);
		Writer.save(Database.getLastSavePath());
	}

}
