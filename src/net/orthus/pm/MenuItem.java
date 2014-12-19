package net.orthus.pm;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MenuItem extends JMenuItem {
	
	//----- Constructors
	public MenuItem(String text, ActionListener listener, int command) {
		super(text);
		this.addActionListener(listener);
		this.setActionCommand("" + command);
	}
	
	public MenuItem(String text, ActionListener 
			listener, int command, KeyStroke acc){
		super(text);
		this.addActionListener(listener);
		this.setActionCommand("" + command);
		this.setAccelerator(acc);
	}
}
