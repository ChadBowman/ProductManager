package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class CustomShippingOption extends ActionFrame 
								  implements ActionListener {

	private ServiceCategory cat;
	private TextField cost, price;
	private Label matCost;
	private JList<String> parts;
	private JTextArea area;
	private Part[] shipPs;
	private String[] shipStr;
	private JCheckBox express;
	
	private Assembly mats;
	
	public CustomShippingOption(ServiceCategory cat){
		this.cat = cat;
		shipPs = Database.getShippingMats().getParts();
		
		shipStr = null;
		if(shipPs != null)
			for(int i=0; i<shipPs.length; i++)
				shipStr = ArrayManager.addToArray(shipPs[i].getName(), shipStr);
		
		mats = new Assembly(
				Database.getNewSerial(),
				1,
				0,
				null, 
				"", 
				Assembly.SHIPPING_TEMPLATE,
				null,
				null,
				null,
				null,
				null,
				null,
				null);
	}
	
	public ShippingOption getSO(){
		
		JPanel pane = new JPanel(new BorderLayout());
		pane.add(new JLabel(cat.getName(), SwingConstants.CENTER), BorderLayout.PAGE_START);
		pane.setPreferredSize(new Dimension(350, 220));
		
			JPanel pane1 = new JPanel(new GridLayout(0,2)); 
			pane.add(pane1, BorderLayout.CENTER);
		
				JPanel left = new JPanel(new BorderLayout());
				left.add(new Button("Add Part", this, 0), BorderLayout.PAGE_END);
				pane1.add(left);
				
					JPanel left1 = new JPanel(new GridLayout(0,1));
					left1.add(parts = new JList<String>(shipStr));
					parts.setBorder(BorderManager.getTitleBorder("Availible Parts"));
					left1.add(new JScrollPane(area = new JTextArea()));
					area.setBorder(BorderManager.getTitleBorder("Used In Shipping Option"));
					area.setEditable(false);
					left.add(left1);
		
				JPanel right = new JPanel(new BorderLayout());
				pane1.add(right);
				
					JPanel right1 = new JPanel(new GridLayout(0,1));
					right1.add(cost = new TextField("Service Cost"));
					right1.add(matCost = new Label("$0.0", "Material Cost", SwingConstants.CENTER));
					right1.add(price = new TextField("Customer Price"));
					right1.add(express = new JCheckBox("Express Shipping"));
					right.add(right1);
				
				
		JOptionPane.showMessageDialog(
				GUIEngine.getMainFrame(), 
				pane, 
				"Custom Shipping Option", 
				JOptionPane.PLAIN_MESSAGE);
		
		String name = (express.isSelected())? "Express" : "Standard";
		
		if((cost.getText() == null || cost.getText().equals(""))
				&& (price.getText() == null || price.getText().equals("")))
			return new ShippingOption("Cancel", null, null, null);
	
		if(!FormatChecker.creditCheck(cost.getText())
				|| !FormatChecker.creditCheck(price.getText()))
			return null;
	
		
		return new ShippingOption(name, new Credit(cost.getText()), new Credit(price.getText()), mats);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Part clone = new Part(shipPs[parts.getSelectedIndex()]);
		clone.setQuantity(1);
		mats.addPart(clone);
		matCost.setText(mats.getTotalCost().toString());
		area.append("  " + clone.getName() + "\n");
	}



}
