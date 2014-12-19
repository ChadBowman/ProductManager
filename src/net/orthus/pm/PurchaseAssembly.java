package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PurchaseAssembly extends ActionFrame 
 							  implements ActionListener,
 							  			 ListSelectionListener {
	
	//----- Variables
	//UI
	private static PurchaseAssembly frame;
	private TextField quantity, cost, date;
	private JLabel focus;
	private JList<String> list;
	private ComboBox<String> color, quality;
	private Button submit;
	private JCheckBox paypal;
	
	//Data
	private Category category;
	private Assembly[] templates;

	//----- Constructor
	public PurchaseAssembly() {
		super("Purchase Assembly", 230, 450);
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		
		category = Database.getFocusedCategory();
		ProductCategory pCat = category.getDefaultAssembly().getProductCategory();
		
		templates = category.getTemplates();
		String[] names = new String[templates.length - 1];
		
		if(templates != null)
			for(int i=1; i<templates.length; i++)
				names[i-1] = templates[i].getName(); //Skip first, the Default Ass
		
		center.add(quantity = new TextField("Quantity"));
		JPanel lPane = new JPanel(new BorderLayout());
		lPane.add(new JScrollPane(list = new JList<String>(names)));
		list.addListSelectionListener(this);
		center.add(lPane);
		center.add(focus  = new JLabel(" ", SwingConstants.CENTER));
		focus.setMinimumSize(new Dimension(0, 30));
		center.add(cost = new TextField("Total Cost"));
		if(pCat != null)
			center.add(color = new ComboBox<String>("", pCat.getColors(), null, -1, "Overall Color"));
		center.add(quality = new ComboBox<String>("", Part.getQualityArray(), null, -1, "Quality"));
		center.add(date = new TextField(Date.getTodaysDate().displaySimpleDate(), "Purchase Date"));
		
		bottom.add(submit = new Button("Purchase", this, 0));
		bottom.add(paypal = new JCheckBox("PayPal"));
		paypal.setSelected(true);
		
		if(names.length < 1){
			focus.setText("No Templates Availible");
			submit.setEnabled(false);
		}
	}
	
	//----- Methods
	public static void initializeFrame(){
		frame = new PurchaseAssembly();
		frame.setVisible(true);
	}
	
	//----- Interfaces
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		@SuppressWarnings("unchecked")
		JList<String> l = (JList<String>) arg0.getSource();
		
		focus.setText(templates[l.getSelectedIndex() + 1].getName());
		
		if(templates[l.getSelectedIndex() + 1].getColor() != null){
			if(templates[l.getSelectedIndex() + 1].getColor().equals(Assembly.NO_COLOR))
				color.setEnabled(false);
			else
				color.setEnabled(true);
		}else
			color.setEnabled(true);
		
		try{
			int q = Integer.parseInt(quantity.getText());
			cost.setText("" + (q * templates[l.getSelectedIndex() + 1].getTotalCost().getValueInDollars()));
		}catch(NumberFormatException e){}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(FormatChecker.quantityCheck(quantity.getText())
				&& FormatChecker.creditCheck(cost.getText())
				&& FormatChecker.dateCheck(date.getText())
				&& !list.isSelectionEmpty()){
			
			
			String clr = null, qul = null;
			if(color != null)
				if(color.getSelectedIndex() != 0)
					clr = (String) color.getSelectedItem();
			
			if(quality.getSelectedIndex() != 0)
				qul = (String) quality.getSelectedItem();
		
			Credit cst = new Credit(cost.getText());
			
			if(paypal.isSelected())
				Database.subtractFromPayPalBalance(cst);
			
			cst.divide(Integer.parseInt(quantity.getText()));
				
			Assembly clone = new Assembly(templates[list.getSelectedIndex() + 1]);
			clone.setQuantity(Integer.parseInt(quantity.getText()));
			clone.setColor(clr);
			clone.setStatus(Assembly.INCOMING);
			clone.setDatePurchased(new Date(date.getText()));
			System.out.println(cst.toString());
			clone.distributeCost(cst);
			clone.distributeQuality(qul);
			category.addAssemblyToSupplyNonGrouping(clone);
			
			GUIEngine.refresh(-1, -1);
			frame.dispose();
		}
	}

}
