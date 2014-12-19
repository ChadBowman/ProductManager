package net.orthus.pm;

import java.awt.event.ActionListener;

import javax.swing.JComboBox;

public class ComboBox<E> extends JComboBox<E> {
	
	public ComboBox(){}
	
	public ComboBox(E[] array){
		super(array);
	}
	
	public ComboBox(E first, E[] array, 
			ActionListener listener, int command){
		
		this.addItem(first);
		if(array != null)
			for(int i=0; i<array.length; i++)
				this.addItem(array[i]);
		
		this.addActionListener(listener);
		this.setActionCommand("" + command);
	}
	
	public ComboBox(E first, E[] array, 
			ActionListener listener, int command, String borderTitle){
		
		if(first != null)
			this.addItem(first);
		
		if(array != null)
			for(int i=0; i<array.length; i++)
				this.addItem(array[i]);
		
		this.addActionListener(listener);
		this.setActionCommand("" + command);
		this.setBorder(BorderManager.getTitleBorder(borderTitle));
	}

}
