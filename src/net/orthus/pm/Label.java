package net.orthus.pm;

import javax.swing.Icon;
import javax.swing.JLabel;

public class Label extends JLabel {

	public Label(String text, String borderTitle) {
		super(text);
		this.setBorder(BorderManager.getTitleBorder(borderTitle));
	}
	
	public Label(String text, String borderTitle, int alignment) {
		super(text, alignment);
		this.setBorder(BorderManager.getTitleBorder(borderTitle));
	}

}
