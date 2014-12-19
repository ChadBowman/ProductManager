package net.orthus.pm;

import javax.swing.tree.DefaultMutableTreeNode;

public class Node extends DefaultMutableTreeNode {

	//----- Variables
	private Node parent;
	private Assembly assembly;
	private Part part;
	
	//----- Constructors
	public Node(Assembly assembly, Node parent) {
		super(assembly);
		this.assembly = assembly;
		this.parent = parent;
	}

	public Node(Part part, Node parent) {
		super(part);
		this.part = part;
		this.parent = parent;
	}
	
	//----- Methods
	public Assembly getAssembly(){ return assembly; }
	public Part getPart(){ return part; }
	public Node getParentNode(){ return parent; }
	
	public boolean isAssembly(){
		if(assembly == null) return false;
		return true;
	}
	
	public boolean isPart(){
		if(assembly == null) return true;
		return false;
	}

}
