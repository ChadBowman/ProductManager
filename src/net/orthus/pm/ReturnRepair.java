package net.orthus.pm;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ReturnRepair extends ActionFrame implements ActionListener {

	//----- Variables
	//UI
	private static ReturnRepair frame;
	private TextField note;
	
	//Data
	private static Repair r;

	//----- Constructor
	public ReturnRepair() {
		super("Return Product", 200, 300, new GridLayout(0,1));

		int cen = SwingConstants.CENTER;
		center.add(new JLabel(r.getName(), cen));
		center.add(new Label(r.getBuyerName(), "Buyer", cen));
		center.add(new Label("" + r.getBuyerZIP(), "ZIP", cen));
		if(r.getOverallColor() != null)
			center.add(new Label(r.getOverallColor(), "Color", cen));
		center.add(note = new TextField(r.getNote(), "Note"));
		
		bottom.add(new Button("Return", this, 0));
	}

	//----- Methods
	public static void initializeFrame(){
		r = DataSelection.getSelectedRepair();
		
		if(r == null) return;
		if(!r.getStatus().equals(Repair.COMPLETE)){
			OptionPane.showConfirm(r.getName() + " must be sold before it can be returned.",
					"Repair Status Error!");
			return;
		}
		
		frame = new ReturnRepair();
		frame.setVisible(true);
	}
	
	
	//----- Interfaces
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(FormatChecker.stringCheck(note.getText())){
			
			r.setStatus(Repair.RETURNED);
			//TODO deduct from PayPal, credit ebay?
			r.setNote(note.getText());
			
			GUIEngine.refresh(-1, -1);
			frame.dispose();
			
		}

	}

}
