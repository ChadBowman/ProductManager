package net.orthus.pm;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DecrementPart extends ActionFrame 
						   implements ActionListener,
						   			  ChangeListener{
	
	private static DecrementPart frame;
	private static Constituent con;
	
	private Spinner toDec;
	private Label left;
	private JCheckBox conserve;
	
	public DecrementPart(){
		super("Decrement Part", 175, 225, new GridLayout(0,1));
		
		center.add(new JLabel(con.getName(), SwingConstants.CENTER));
		center.add(toDec = new Spinner(1, con.getQuantity(), this, "Quantity To Remove"));
		center.add(left = new Label("" + (con.getQuantity() - 1), 
				"Remaining In Stock", SwingConstants.CENTER));
		center.add(conserve = new JCheckBox("Conserve Investment"));
		
		bottom.add(new Button("Decrement", this, 0));
	}
	
	public static void initializeFrame(Constituent c){
		con = c;
		frame = new DecrementPart();
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if((int) toDec.getValue() == con.getQuantity()){
			OptionPane.showError(OptionPane.RECOMEND_DELETE);
			frame.dispose();
			return;
		}
			
		
		Credit total = con.getTotalCost();
		con.setQuantity(con.getQuantity() - (int) toDec.getValue());
		
		if(conserve.isSelected()){ //Prevent loss of assets by distributing cost
			try{
				Part p = (Part) con;
				p.setPrice(total.getValueInDollars() / p.getQuantity());
			}catch (ClassCastException e){
				Assembly a = (Assembly) con;
				a.distributeCost(new Credit(total.getValueInDollars() / a.getQuantity()));
			}
			
		}
		
		frame.dispose();
		GUIEngine.refresh(-1,  -1);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		
		int dec = (int) toDec.getValue();
		left.setText("" + (con.getQuantity() - dec));
		
	}

}
