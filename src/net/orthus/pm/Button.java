package net.orthus.pm;

import java.awt.event.ActionListener;

import javax.swing.JButton;

public class Button extends JButton {

	//----- Constructor
	public Button(String text, ActionListener listener, int command) {
		super(text);
		this.addActionListener(listener);
		this.setActionCommand("" + command);
	}

}
