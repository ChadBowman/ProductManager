package net.orthus.pm;

import javax.swing.JTextField;
import javax.swing.text.Document;

public class TextField extends JTextField {
	
	public TextField(String borderTitle){
		this.setBorder(BorderManager.getTitleBorder(borderTitle));
	}

	public TextField(String stuff, String borderTitle) {
		super(stuff);
		this.setBorder(BorderManager.getTitleBorder(borderTitle));
	}

}
