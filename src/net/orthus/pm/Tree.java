package net.orthus.pm;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class Tree extends JTree {
	
	//----- Variables
	//UI
	private Node root;
	private DefaultTreeModel model;

	//----- Constructor
	public Tree(Assembly root, TreeSelectionListener listener) {
		this.root = new Node(root, null);
		model = new DefaultTreeModel(this.root);
		this.setModel(model);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.addTreeSelectionListener(listener);
		buildTree(this.root, root);
	}
	
	//----- Methods
	//Basic
	public Node getRoot(){ return root; }
	
	//For constructor
	private void buildTree(Node top, Assembly root){
		Node node;
		if(root.getAssemblies() != null)
			for(int i=0; i<root.getAssemblies().length; i++){
				top.add(node = new Node(root.getAssemblies()[i], top));
				buildTree(node, root.getAssemblies()[i]); //Recursion
			}
		
		if(root.getParts() != null)
			for(int i=0; i<root.getParts().length; i++)
				top.add(node = new Node(root.getParts()[i], top));
	}
	
	//Public
	public Node getSelectedNode(){
		return (Node) this.getLastSelectedPathComponent();
	}
	
	//Updates GUI as well as underlying data structure
	public void addAssembly(Assembly child, Node parent){
		
		if(!parent.isAssembly()) return; //Parent cannot be Part
		
		//Update UI
		addNode(new Node(child, parent), parent); //Add Part or Assembly

		//Update Data
		parent.getAssembly().addAssembly(child);
	}
	//Updates GUI as well as underlying data structure
	public void addPart(Part child, Node parent){
		
		if(!parent.isAssembly()) return; //Parent cannot be a part
		
		//Update UI
		addNode(new Node(child, parent), parent);
		
		//Update Data
		parent.getAssembly().addPart(child);
	}
	
	//Used in addAssembly()
	//Adds only portion visible in tree, does NOT modify data structure
	private void addNode(Node child, Node parent){
		
		if(!parent.isAssembly()) return; //parent cannot be Part
		
		if(child.isAssembly()){
			model.insertNodeInto(child, parent, parent.getChildCount()); //Add current assembly
			
			if(child.getAssembly().getParts() != null)	//Add all parts
				for(int i=0; i<child.getAssembly().getParts().length; i++)
					addNode(new Node(child.getAssembly().getParts()[i], child), child);
			
			if(child.getAssembly().getAssemblies() != null) //Add all assemblies
				for(int i=0; i<child.getAssembly().getAssemblies().length; i++)
					addNode(new Node(child.getAssembly().getAssemblies()[i], child), child);
		}else
			model.insertNodeInto(child, parent, parent.getChildCount());
	}
	
	public void removeSelected(){
		
		if(getSelectedNode().isAssembly()){
			
			Assembly toRemove = getSelectedNode().getAssembly();
			getSelectedNode().getParentNode().getAssembly().removeAssembly(toRemove);
				//TODO Remove needs to be equiv??
			
		}else{	//Node is a Part
			
			//Update Data
			Part toRemove = getSelectedNode().getPart();
			getSelectedNode().getParentNode().getAssembly().removePart(toRemove);
		}
		
		model.removeNodeFromParent(getSelectedNode());
	}
	
	public void expandCurrentPath(){
		if(getSelectedNode() != null)
			this.expandPath(new TreePath(getSelectedNode().getPath()));
	}
	
}//End Tree
