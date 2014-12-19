package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class PurchasePart extends ActionFrame 
						  implements TreeSelectionListener,
						  		     ActionListener {
	
	//----- Variables
	//UI
	private static PurchasePart frame;
	private TextField quantity, cost, date;
	private TextField focus;
	private ComboBox<String> color, quality;
	private Tree tree;
	private JCheckBox ship, paypal;
	
	//Data
	private Category category;
	private int serial;
	private String stat0;
	

	//----- Constructor
	public PurchasePart() {
		super("Purchase Part", 230, 450);
		top.setLayout(new GridLayout(1, 0));
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		
		category = Database.getFocusedCategory();
		ProductCategory pcat = category.getDefaultAssembly().getProductCategory();
		
		
		top.add(quantity = new TextField("Quantity"));
		top.add(ship = new JCheckBox("New Shipping"));
		ship.addActionListener(this);
		ship.setActionCommand("1");
		
		Part[] shpt = pcat.getShippingPartTemplates();
		Assembly a1 = new Assembly("Shipping", null, shpt);
		Assembly[] a = { category.getDefaultAssembly(), a1 };
		Assembly a0 = new Assembly("", a, null);
		JPanel tpane = new JPanel(new BorderLayout());
		tpane.add(new JScrollPane(tree = new Tree(a0, this)));
		center.add(tpane);
		tree.expandRow(0);
		center.add(focus = new TextField("Name"));
		focus.setEditable(false);
		focus.setMinimumSize(new Dimension(0, 40));
		center.add(cost = new TextField("Total Cost"));
		if(pcat != null)
			center.add(color = new ComboBox<String>("", pcat.getColors(), null, -1, "Color"));
		center.add(quality = new ComboBox<String>("", Part.getQualityArray(), null, -1, "Quality"));
		quality.setSelectedIndex(1);
		center.add(date = new TextField(Date.getTodaysDate().displaySimpleDate(), "Purchase Date"));
		
		bottom.add(new Button("Purchase", this, 0));
		bottom.add(paypal = new JCheckBox("PayPal"));
		paypal.setSelected(true);
	}
	
	//----- Methods
	public static void initializeFrame(){
		frame = new PurchasePart();
		frame.setVisible(true);
	}
	
	//----- Interfaces
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		int cmd = Integer.parseInt(arg0.getActionCommand());
		switch(cmd){
		
		case 0: //Submit (purchase part)
			if(FormatChecker.quantityCheck(quantity.getText())
					&& FormatChecker.creditCheck(cost.getText())
					&& FormatChecker.dateCheck(date.getText())
					&& FormatChecker.blankStringCheck(focus.getText(), "Name field")){
			
				String stat = Constituent.INCOMING;
				
				if(ship.isSelected()){
					stat = Constituent.SHIPPING_INCOMING;
					serial = Database.getNewSerial();
				}else if(stat0.equals(Part.SHIPPING_SUPPLY))
					stat = Constituent.SHIPPING_INCOMING;
			
				String col = null;
				if(color != null)
					if(color.isEnabled()){
						if(color.getSelectedIndex() != 0)
							col = (String) color.getSelectedItem();
					}else
						col = Part.NO_COLOR;
				
				String qua = null;
				if(quality.isEnabled()){
					if(quality.getSelectedIndex() != 0)
						qua = (String) quality.getSelectedItem();
				}else
					qua = Part.NO_QUALITY;
				
				int quan = Integer.parseInt(quantity.getText());
				double cst = Double.parseDouble(cost.getText());
				
				if(paypal.isSelected())
					Database.subtractFromPayPalBalance(new Credit(cst));
						
				category.addPartToSupplyNonGrouping(new Part(
						serial,
						quan,
						0,
						focus.getText(),
						stat,
						qua,
						col,
						new Credit(cst / quan),
						null,
						null,
						new Date(date.getText()),
						null,
						null));
				
				GUIEngine.refresh(-1, -1);
				frame.dispose();
			}
			break;
			
		case 1:
			if(ship.isSelected()){
				tree.setEnabled(false);
				focus.setEditable(true);
				color.setEnabled(false);
				quality.setEnabled(false);
			}else{
				tree.setEnabled(true);
				focus.setEditable(false);
			}
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		if(tree.getSelectedNode() != null)
			if(tree.getSelectedNode().isPart()){
				Part p = tree.getSelectedNode().getPart();

				serial = p.getSerial();
				focus.setText(p.getName());
				stat0 = p.getStatus();
				
				try{
					cost.setText("" + (Integer.parseInt(quantity.getText()) 
									* p.getPrice().getValueInDollars()));
				}catch(NumberFormatException e){
					cost.setText("");
				}
			
				
				color.setSelectedIndex(0);
				if(p.getColor() == null)
					color.setEnabled(true);
				else if(p.getColor().equals(Part.NO_COLOR))
					color.setEnabled(false);
				else
					color.setEnabled(true);
				
				
				if(p.getQuality() == null){
					quality.setSelectedIndex(1);
					quality.setEnabled(true);
				}else if(p.getQuality().equals(Part.NO_QUALITY)){
					quality.setSelectedIndex(0);
					quality.setEnabled(false);
				}else{
					quality.setSelectedIndex(1);
					quality.setEnabled(true);
				}
			}
	}
}
