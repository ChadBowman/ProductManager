package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ShippingOptions extends ActionFrame 
							 implements ActionListener,
							 			ListSelectionListener{

	//----- Variables
	//UI
	private static ShippingOptions frame;
	private JList<String> list, currentOps;
	private JTextArea area;
	private TextField name, cost, fee;
	private Label lab;
	
	//Data
	private Part[] p;
	private Part[] option = null;
	private Credit cst;
	private ShippingOption[] current;
	private ServiceCategory cat;
	
	//----- Constructor
	public ShippingOptions() {
		super("Manage Shipping Options", 460, 300, new GridLayout(0, 3));
		
		cat = Database.getFocusedCategory().getDefaultAssembly().getServiceCategory();
		p = (Database.getShippingMats() == null)? null : Database.getShippingMats().collapseAssembly();
		cst = new Credit();

		top.add(new JLabel(Database.getFocusedCategory().getName()));
		
		
		
		//Left
		JPanel currentPane = new JPanel(new BorderLayout());
		currentPane.add(currentOps = new JList<String>(getListData()), BorderLayout.CENTER);
		currentOps.addListSelectionListener(this);
		if(getListData()[0].equals("No Parts Availible."))
			currentOps.setEnabled(false);
		
		currentOps.setBorder(BorderManager.getTitleBorder("Current Options"));
		JPanel but1 = new JPanel();
		but1.add(new Button("Delete", this, 2));
		currentPane.add(but1, BorderLayout.PAGE_END);
		center.add(currentPane);
		
		//Middle 
		JPanel left = new JPanel(new BorderLayout());
		JPanel listP = new JPanel(new GridLayout(0, 1));
		
		String[] s = {"No Parts Availible."};
		if(p == null){
			listP.add(list = new JList<String>(s));
			list.setEnabled(false);
			
		}else{
			s = new String[p.length];
			for(int i=0; i<p.length; i++)
				s[i] = p[i].getName();
			
			listP.add(new JScrollPane(list = new JList<String>(s)));
		}
		list.setBorder(BorderManager.getTitleBorder("Availible Parts"));
		
		listP.add(new JScrollPane(area = new JTextArea()));
		area.setEditable(false);
		area.setBorder(BorderManager.getTitleBorder("Parts In New Option"));
	
		left.add(listP, BorderLayout.CENTER);
		
		JPanel but = new JPanel();
		left.add(but, BorderLayout.PAGE_END);
		but.add(new Button("Add Part", this, 0));
		
		center.add(left);
		
		JPanel right = new JPanel(new GridLayout(0,1));
		
		right.add(name = new TextField("Name"));
		right.add(cost = new TextField("Service Cost"));
		right.add(lab = new Label("$0.0", "Current Materials Cost", SwingConstants.CENTER));
		right.add(fee = new TextField("Customer's Price"));
		JPanel b2 = new JPanel();
		right.add(b2);
		b2.add(new Button("Add New Option", this, 1));
		
		center.add(right);
		
	}

	//----- Methods
	public static void initializeFrame(){
		frame = new ShippingOptions();
		frame.setVisible(true);
	}
	
	private String[] getListData(){
		current = cat.getShippingOptions();
		
		String[] o = {"No Shipping Options."};
		if(current == null){
			if(currentOps != null)
				currentOps.setEnabled(false);
			
		}else if(current.length < 1){
			currentOps.setEnabled(false);
			
			
		}else{
			o = new String[current.length];
			for(int i=0; i<o.length; i++)
				o[i] = current[i].getName();
		}
		
		return o;
	}
	
	
	//----- Interfaces
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(!list.isEnabled()) return;
		
		int cmd = Integer.parseInt(arg0.getActionCommand());
		switch(cmd){
		
		case 0: //Add part to option
			
			Part clone = new Part(p[list.getSelectedIndex()]);
			clone.setQuantity(1);
			cst.add(clone.getPrice());
			lab.setText(cst.toString());
			area.append("  " + clone.getName() + "\n");
			option = ArrayManager.addToArray(clone, option);
			
			break;
			
			
		case 1: //Submit
			
			if(FormatChecker.blankStringCheck(name.getText(), "Name Field")
					&& FormatChecker.creditCheck(cost.getText())
					&& FormatChecker.creditCheck(fee.getText())){
				
				ProductCategory p = Database.getFocusedCategory().getDefaultAssembly().getProductCategory();
				ServiceCategory s = null;
				
				String address;
				if(p == null){
					s = Database.getFocusedCategory().getDefaultAssembly().getServiceCategory();
					address = s.getAddress();
				}else
					address = p.getAddress();
				
				Assembly a = new Assembly(
						Database.getNewSerial(),
						1,
						0,
						address,
						name.getText(),
						Assembly.SHIPPING_TEMPLATE,
						Assembly.NO_COLOR,
						Date.getTodaysDate(),
						null,
						null,
						null,
						null,
						option);
				
				ShippingOption so = new ShippingOption(
						name.getText(),
						new Credit(cost.getText()),
						new Credit(fee.getText()),
						a);
				
				if(p == null)
					s.addShippingOption(so);
				else
					p.addShippingOption(so);
				
				currentOps.setListData(getListData());
				GUIEngine.noteDataChange();
				
			} break;
			
			
		case 2: //Delete ShippingOption
			
			if(currentOps.getSelectedIndex() < 0) break; //No selection
			
		
			ProductCategory p = Database.getFocusedCategory().getDefaultAssembly().getProductCategory();
			ServiceCategory s = null;
			
			if(p == null){
				s = Database.getFocusedCategory().getDefaultAssembly().getServiceCategory();
				s.removeShippingOption(current[currentOps.getSelectedIndex()]);
			}else
				p.removeShippingOption(current[currentOps.getSelectedIndex()]);
			
			currentOps.setListData(getListData());
			GUIEngine.noteDataChange();
			
			break;
			
		}

	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		
		int index = currentOps.getSelectedIndex();
		
		if(index != -1){
			ShippingOption so = current[index];
			name.setText(so.getName());
			cost.setText("" + so.getCost().getValueInDollars());
			fee.setText("" + so.getPaid().getValueInDollars());
			lab.setText(so.getMaterials().getTotalCost().toString());
		}
	}

}
