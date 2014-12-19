package net.orthus.pm;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

public class Spinner extends JSpinner {


	public Spinner(int start, int end, ChangeListener listener, String border) {
		super(new SpinnerNumberModel(start, start, end, 1));
		
		this.getModel().addChangeListener(listener);
		
		JTextField f = ((JSpinner.DefaultEditor) this.getEditor()).getTextField();
		f.setHorizontalAlignment(JTextField.CENTER);
		f.setEditable(false);
		
		if(border != null)
			setBorder(BorderManager.getTitleBorder(border));
	}
	
	public Spinner(int start, int end, int step, ChangeListener listener, String border){
		super(new SpinnerNumberModel(start, start, end, step));
		
		this.getModel().addChangeListener(listener);
		
		JTextField f = ((JSpinner.DefaultEditor) this.getEditor()).getTextField();
		f.setHorizontalAlignment(JTextField.CENTER);
		f.setEditable(false);
		
		if(border != null)
			setBorder(BorderManager.getTitleBorder(border));
	}

}
