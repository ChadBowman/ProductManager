package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RepairProduct extends ActionFrame 
						   implements ActionListener, 
						   			  ListSelectionListener {

	//----- Variables
	//UI
	private static RepairProduct frame;
	private Tree tree;
	private JTextArea area;
	private JList<String> list;
	private JPanel content = new JPanel(new CardLayout());
	private ComboBox<String>[] color;
	
	//Data
	private static Product p;
	private ProductCategory pc;
	private Part[][] pts;
	private Assembly[][] assems;
	
	
	//----- Constructor
	public RepairProduct() {
		super("Repair Prodcut", 515, 400, new GridLayout(0,1));

		pc = p.getParent(); //Grab product category
		
		//Top Half
		JPanel top = new JPanel(new GridLayout(1,0));
		JPanel treePane = new JPanel(new BorderLayout());
		treePane.add(new JScrollPane(tree = new Tree(p.getAssembly(), null)), BorderLayout.CENTER);
		tree.expandRow(0);
		JPanel btp = new JPanel();
		btp.add(new Button("Dismantle", this, 0));
		treePane.add(btp, BorderLayout.PAGE_END);
		treePane.setBorder(BorderManager.getTitleBorder("Current Assembly"));
		top.add(treePane);
		top.add(new JScrollPane(area = new JTextArea()));
		area.setBorder(BorderManager.getTitleBorder(p.getFullName()));
		populateSummary();
		area.setEditable(false);
		center.add(top);
		
		//Bottom Half
		initializeList();
		JSplitPane split = 
				new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(list), content);
		split.setDividerLocation(125);
		split.setBorder(BorderManager.getTitleBorder("Needed Parts"));
		center.add(split);
		
		bottom.add(new Button("Finalize Repair", this, 3));
	}
	
	//----- Methods
	public static void initializeFrame(Product prod){
		if(prod == null)
			p = DataSelection.getSelectedProduct();
		else
			p = prod;
		
		if(p == null) return;
		if(!p.getStatus().equals(Product.INVENTORY)){
			OptionPane.showConfirm(p.getFullName() + " must in inventory to repair.", 
					"Product Status Error!");
			return;
		}
		frame = new RepairProduct();
		frame.setVisible(true);
	}
	
	private void populateSummary(){
		
		area.append("\n\n");
		area.append("       Estimated Return: " + p.getEstimatedReturn().toString() + "\n");
		area.append("       Estimated Repair Cost: " 
					+ p.getRepairCost().toString() + "\n");
		
		
		String rec = (p.getEstimatedReturn().getValueInDollars() < 7 )?
				"Full Dismantle" : "Repair";

		area.append("\n       Recommend " + rec);
		
	}
	
	@SuppressWarnings("unchecked")
	private void initializeList(){
		
		String[] arr = null;	//String for JList
		
		
		//////// Add Elements to JList Array
		//Grab each template
		Assembly[] templates = pc.getTemplates();
		for(int i=1; i<templates.length; i++) 	//Skip first element (default assem)
			if(p.getMissingParts(templates[i]) != null) //If there are parts missing
				arr = ArrayManager.addToArray(templates[i].getName(), arr);
		
		if(arr != null)
			assems = new Assembly[templates.length - 1][];
		
		//Grab each missing part
		Part[] miss = p.getMissingParts(pc.getDefaultAssembly());
		if(miss != null)
			for(int i=0; i<miss.length; i++)
				arr = ArrayManager.addToArray(miss[i].getName(), arr);
		
		if(arr == null) return; //Nothing to replace
		
		color = new ComboBox[arr.length];//Card components initialized
		if(miss != null) pts = new Part[miss.length][];
		
		
		////////Add Cards to layout
		int count = 0;
		
		for(int i=1; i<templates.length; i++) 	//Skip first element (default assem)
			if(p.getMissingParts(templates[i]) != null) //If there are parts missing
				content.add(getAssemblyCard(templates[i], count), "" + count++);
		
		if(miss != null)
			for(int i=0; i<miss.length; i++)
				content.add(getPartCard(miss[i], count), "" + count++);
			
		
		list = new JList<String>(arr); //Initialize
		list.setSelectedIndex(0);
		list.addListSelectionListener(this);
		
	}//End initalizeList()
	
	private JPanel getAssemblyCard(Assembly template, int index){
		
		JPanel toRet = new JPanel(new BorderLayout());
		JTextArea a = new JTextArea("\n");
		toRet.add(a, BorderLayout.CENTER);
		
		String[] ops = p.getRepairOptionResults(template);
		for(int i=0; i<ops.length; i++)
			a.append(ops[i] + "\n");
		
		JPanel btns = new JPanel();
		
		Assembly[] ass = (pc.getPartSupply() == null || 
				pc.getPartSupply().getAssemblies() == null)? null :
				pc.getPartSupply().finAllAssemsBySerialAndStatusFromSupply(template, Constituent.SUPPLY);
		
		Assembly[] clones = null; //Set of assemblies with quantity of 1
		
		if(ass != null){
			clones = new Assembly[ass.length];
			for(int i=0; i<ass.length; i++){
				clones[i] = new Assembly(ass[i]);
				clones[i].setQuantity(1);
				clones[i].setStatus(Constituent.SUPPLY);
			}
		}
		
		assems[index] = clones;
		
		String[] disp = (ass == null)? null : new String[ass.length];
		
		if(disp != null)
			for(int i=0; i<disp.length; i++)
				disp[i] = ass[i].getColor() + " " + ass[i].getUnitPrice().toString();
		
		color[index] = (disp == null)? new ComboBox<String>() : new ComboBox<String>(disp);
		if(disp == null) color[index].setEnabled(false);
		
		Button b;
		btns.add(color[index]);
		btns.add(b = new Button("Replace Assembly", this, 1));
		if(disp == null) b.setEnabled(false); //No Assemblies available, can't replace
		toRet.add(btns, BorderLayout.PAGE_END);
		
		return toRet;
		
	}//End getAssemblyCard()
	
	private JPanel getPartCard(Part part, int index){
		
		JPanel toRet = new JPanel(new BorderLayout());
		JTextArea a = new JTextArea("\n");
		toRet.add(a, BorderLayout.CENTER);
		
		Part[] parts = (pc.getPartSupply() == null)? null :
			pc.getPartSupply().findAllPartsBySerialAndStatusFromSupply(part, Part.SUPPLY);
		
		
		
		if(parts == null){
			
			Credit cst = pc.getDefaultAssembly().findPartBySerial(part).getPrice();
			String q = (part.getQuantity() == 1)? "          " + cst.toString():
					"          " + cst.toString() + " (" + part.getQuantity() + ") = " 
					+ new Credit(cst.getValueInDollars() * part.getQuantity()).toString();
			
			a.append(q + "\n");
			a.append("   There are no " + part.getName() + "s in supply.");
			color[index] = new ComboBox<String>();
			color[index].setEnabled(false);
			
		}else{
			
			String[] ops = new String[parts.length];
			Part[] record = new Part[parts.length];
			
			int num = 0;
			for(int i=0; i<parts.length; i++){
				num += parts[i].getQuantity();
				
				if(parts[i].getColor() == null)
					ops[i] = parts[i].getQuantity() + " " + parts[i].getPrice().toString();
				else
					ops[i] = (parts[i].getColor().equals(Part.NO_COLOR))? 
							parts[i].getQuantity() + " " + parts[i].getPrice().toString() : 
							parts[i].getQuantity() + " " 
								+ parts[i].getColor() + " " + parts[i].getPrice();
							
				Part rec = new Part(parts[i]);
				if(rec.getQuantity() > part.getQuantity())
					rec.setQuantity(part.getQuantity());
				
				record[i] = rec;
				
			}//End for
			
			if(assems != null)
				pts[index - assems.length] = record;
			else
				pts[index] = record;
			
			Credit cst = parts[0].getPrice();
			String q = (part.getQuantity() == 1)? "          " + cst.toString():
				"          " + cst.toString() + " (" + part.getQuantity() + ") = " 
				+ new Credit(cst.getValueInDollars() * part.getQuantity()).toString();
			
			a.append(q + "\n");
			a.append("   There are " + num + " " + part.getName() + "s in supply.");
			
			color[index] = new ComboBox<String>(ops);
		}
		
		JPanel bts = new JPanel();
		bts.add(color[index]);
		Button b;
		bts.add(b = new Button("Replace Part", this, 2));
		if(parts == null) b.setEnabled(false); //No parts available, can't replace
		toRet.add(bts, BorderLayout.PAGE_END);
		
		return toRet;
		
	}//End getPartCard()

	//----- Interfaces
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		CardLayout cards = (CardLayout) (content.getLayout());
		@SuppressWarnings("unchecked")
		JList<String> sel = (JList<String>) arg0.getSource();
		int index = sel.getSelectedIndex();
		cards.show(content, "" + index);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		int cmd = Integer.parseInt(arg0.getActionCommand());
		int idx, sub;
		
		switch(cmd){
		case 0:  //Dismantle part
		
			if(tree.getSelectedNode() != null){
				
				if(tree.getSelectedNode().isAssembly()){
					
					Part[] pts = tree.getSelectedNode().getAssembly().collapseAssembly();
					
					for(int i=0; i<pts.length; i++){
						pts[i].setStatus(Part.SUPPLY);
						
						if(pts[i].getQuality() == null)
							pts[i].setQuality(Part.B);
						else if(!pts[i].getQuality().equals(Part.NO_QUALITY))
							pts[i].setQuality(Part.B);
						
						if(pts[i].getColor() == null)
							pts[i].setColor(p.getOverallColor());
						else if(!pts[i].getColor().equals(Part.NO_COLOR))
							pts[i].setColor(p.getOverallColor());
							
							
						
						pc.addPartToSupply(pts[i]);
					}
					
					tree.removeSelected();
				
				}else{
					Part clone = new Part(tree.getSelectedNode().getPart());
					clone.setQuantity(1);
					clone.setStatus(Part.SUPPLY);
					
					if(!clone.getQuality().equals(Part.NO_QUALITY))
						clone.setColor(p.getOverallColor());
					clone.setQuality(Part.B);
					pc.addPartToSupply(clone);
					
					int q = tree.getSelectedNode().getPart().getQuantity();
					if(q > 1)
						tree.getSelectedNode().getPart().setQuantity(q - 1);
					else
						tree.removeSelected();
				}
				
				GUIEngine.refresh(-1, -1);
				frame.dispose();
				RepairProduct.initializeFrame(p);
			}
			break;
		
		case 1: //Move Assembly
			
			idx = list.getSelectedIndex();
			sub = color[idx].getSelectedIndex();
			
			Part[] ps = p.getPresentParts(assems[idx][sub]);
			p.moveAssemblyToSupply(assems[idx][sub]); //Dismantle any parts used in assembly
			p.moveAssemblyFromSupply(assems[idx][sub]); //Grab fresh assembly
			p.getParent().addUseRecord(new UseRecord(
					assems[idx][sub].getSerial(),
					assems[idx][sub].getQuantity(),
					Date.getTodaysDate()));
			
			if(assems[idx][sub].getName().equals("Housing"))
				p.setOverallColor(assems[idx][sub].getColor());
			
			if(ps != null){
				JTextArea a = new JTextArea();
				
				for(int i=0; i<ps.length; i++)
					a.append(ps[i].getQuantity() + " " + ps[i].getName() + "\n");
				
				JOptionPane.showMessageDialog(
						GUIEngine.getMainFrame(), 
						new JScrollPane(a), 
						"Place Following Parts In Inventory!", 
						JOptionPane.WARNING_MESSAGE);
			}
			
			GUIEngine.refresh(-1, -1);
			frame.dispose();
			RepairProduct.initializeFrame(p);
			
			break;
		
		case 2: //Move Part
			
			idx = list.getSelectedIndex();
			sub = color[idx].getSelectedIndex();
			
			if(assems != null){
				p.movePartFromSupply(pts[idx - assems.length][sub]);
				p.getParent().addUseRecord(new UseRecord(
						pts[idx - assems.length][sub].getSerial(),
						pts[idx - assems.length][sub].getQuantity(),
						Date.getTodaysDate()));
			}else{
				p.movePartFromSupply(pts[idx][sub]);
				p.getParent().addUseRecord(new UseRecord(
						pts[idx][sub].getSerial(),
						pts[idx][sub].getQuantity(),
						Date.getTodaysDate()));
			}
			
			GUIEngine.refresh(-1, -1);
			frame.dispose();
			RepairProduct.initializeFrame(p);
			
			break;
			
		case 3: //Finalize
			
			p.setStatus(Product.STOCK);
			GUIEngine.refresh(-1, -1);
			frame.dispose();
		}
	}
}
