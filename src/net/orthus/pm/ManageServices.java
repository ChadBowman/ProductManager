package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ManageServices extends ActionFrame 
							implements ActionListener,
									   ListSelectionListener{
	
	//----- Variables
	//UI
	private static ManageServices frame;
	
		//SplitPane
	private JList<String> list;
	private JPanel content = new JPanel(new CardLayout());
	
		//New Service Panel
	private TextField name;
	private Tree tree, tree2;
	private Assembly def;
	
		//List Service Panel
	private TextField[] price;
	private TextField[] quantity;
	private JRadioButton[] radio;
	private TextField[] duration;
	private JCheckBox[] auto;
	private TextField[] fee;
	private TextField[] date;
	private ComboBox<String> color;
	
	//Data
	private ServiceCategory category;
	private int free;
	
	//Utility
	private int currentIndex;
	private JPopupMenu deleteMenu;
	private MenuItem delete;

	public ManageServices(int selection) {
		super("Manage Services", 350, 400, new BorderLayout());
		
		free = new StatManager().getNumberOnTheMarket();
		
		//SplitPane creation
		 category = Database.getFocusedCategory().getDefaultAssembly().getServiceCategory();
		 if(category == null)
			 category = (ServiceCategory) 
			 	Database.getFocusedCategory().getDefaultAssembly().getProductCategory();
		 
		 
		 String[] names = {"New Service"};
		 content.add(newServiceCard(), "-1");
		 if(category.getServices() != null){
			 price = new TextField[category.getServices().length];
			 quantity = new TextField[category.getServices().length];
			 radio = new JRadioButton[category.getServices().length];
			 duration = new TextField[category.getServices().length];
			 auto = new JCheckBox[category.getServices().length];
			 fee = new TextField[category.getServices().length];
			 date = new TextField[category.getServices().length];
			 
			 for(int i=0; i<category.getServices().length; i++){
				 names = ArrayManager.addToArray(category.getServices()[i].getName(), names);
				 if(category.getServices()[i].getListType().equals(Item.NOT_LISTED))
					 content.add(notListedCard(i), "" + i);
				 else
					 content.add(listedCard(i), "" + i);
			 }
			 if(selection == -1) selection = category.getServices().length - 1;
		 }
		 list = new JList<String>(names);
		 list.addListSelectionListener(this);
		 list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		 if(selection == -1) selection = 0;
		 list.setSelectedIndex(selection);
		 
		 deleteMenu = new JPopupMenu();
		 deleteMenu.add(delete = new MenuItem("", this, 7));
		 
		 list.addMouseListener(new MouseAdapter() {
			 public void mousePressed(MouseEvent e){
				 if(SwingUtilities.isRightMouseButton(e)){
					 int row = list.locationToIndex(e.getPoint());
					 
					 if(row != 0){
						 list.setSelectedIndex(row); //Get row number
						 delete.setText("Delete " + list.getSelectedValue());	//Set button text
						 deleteMenu.show(e.getComponent(), e.getX(), e.getY()); //Show at location
						 currentIndex = row - 1;	
					 }
				 }
			 }
		 });
		 
		center.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, list, content));
	}
	
	//----- Methods
	private JPanel newServiceCard(){
		
		def = new Assembly( //Assembly containing copy of parts needed for repair
				Database.getNewSerial(),
				1,
				0,
				category.getAddress(),
				"New Service",
				Assembly.SERVICE_TEMPLATE,
				Assembly.NO_COLOR,
				Date.getTodaysDate(),
				null,
				null,
				null,
				null,
				null);
		
		Assembly treeasm = new Assembly(
				1,
				1,
				0,
				category.getAddress(),
				"Availible",
				Assembly.SERVICE_TEMPLATE,
				Assembly.NO_COLOR,
				Date.getTodaysDate(),
				null,
				null,
				null,
				null,
				null);
		
		treeasm.distributeStatus(Assembly.PART_SUPPLY);
		treeasm.addAssembly(new Assembly(category.getDefaultAssembly()));
		Assembly[] temp = category.getTemplates();
		if(temp != null)
			for(int i=1; i<temp.length; i++)
				treeasm.addAssembly(temp[i]);
		
		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		pan.add(name = new TextField("Name"), BorderLayout.PAGE_START);
		
		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		JPanel treePane = new JPanel(new BorderLayout());
		treePane.setBorder(BorderManager.getTitleBorder("Availible Assembly"));
		
		
		
		treePane.add(new JScrollPane(tree = new Tree(treeasm, null)), BorderLayout.CENTER);
		tree.expandRow(0);
		
		JPanel but1 = new JPanel();
		but1.add(new Button("Use In Service", this, 0));
		treePane.add(but1, BorderLayout.PAGE_END);
		treePane.setMinimumSize(new Dimension(0,140));
		center.add(treePane);
		
		tree2 = new Tree(def, null);
		center.add(new JScrollPane(tree2));
		tree2.setBorder(BorderManager.getTitleBorder("Parts Included"));
		tree2.setMinimumSize(new Dimension(0,100));
		pan.add(center, BorderLayout.CENTER);
		
		JPanel but2 = new JPanel();
		but2.add(new Button("Create Service", this, 1));
		pan.add(but2, BorderLayout.PAGE_END);
		
		return pan;
		
	}//End newServiceCard()
	
	private JPanel notListedCard(int index){
		JPanel pan = new JPanel(new GridLayout(0,1));
		
		pan.add(quantity[index] = new TextField("Quantity Listed"));
		pan.add(price[index] = new TextField("Price Per Service"));
		
		//TODO manage colors
		
		JPanel radios = new JPanel(new GridLayout(0,2));
		radios.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		JRadioButton mock;
		ButtonGroup group = new ButtonGroup();
		radios.add(mock = new JRadioButton("Auction"));
		mock.addActionListener(this);
		mock.setActionCommand("2");
		radios.add(radio[index] = new JRadioButton("Fixed"));
		radio[index].addActionListener(this);
		radio[index].setActionCommand("3");
		radio[index].setSelected(true);
		group.add(radio[index]);
		group.add(mock);
		pan.add(radios);
		
		pan.add(duration[index] = new TextField("30", "Duration"));
		
		String s = (Database.getFreeListingsAllotted() - free > 0)? "0.0" : "0.05";
		pan.add(fee[index] = new TextField(s, "Listing Fee"));
		
		
		pan.add(date[index] = new TextField(Date.getTodaysDate().displaySimpleDate(), "List Date"));
		pan.add(auto[index] = new JCheckBox("Automatic Relist"));
		auto[index].setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
		
		JPanel but = new JPanel();
		but.add(new Button("List Service", this, 4));
		pan.add(but);
		
		return pan;
		
	}//End notListedCard()
	
	private JPanel listedCard(int index){
		JPanel pan = new JPanel(new BorderLayout());
		Service current = category.getServices()[index];
		JPanel content = new JPanel(new GridLayout(0,1));
		content.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
		pan.add(content, BorderLayout.CENTER);
		
		
		content.add(new JLabel("" + current.getQuantity() + " Remaining"));
		content.add(new JLabel(current.getListAmount().toString() + " Per Service"));
		content.add(new JLabel(current.getListType() + " Listing"));
		content.add(new JLabel("" + current.getDaysRemaining() + " Days Remaining"));
		content.add(new JLabel("List Fee: " + current.getListFee().toString()));
		content.add(new JLabel("Listed on " + current.getDateListed().displaySimpleDate()));
		content.add(auto[index] = new JCheckBox("Automatic Relist"));
		if(current.isAutoList()) auto[index].setSelected(true);
		auto[index].addActionListener(this);
		auto[index].setActionCommand("5");
		
		pan.add(new Button("End Listing", this, 6), BorderLayout.PAGE_END);
		
		return pan;
	}

	
	public static void initializeFrame(int selection){
		frame = new ManageServices(selection);
		frame.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		int cmd = Integer.parseInt(arg0.getActionCommand());
		switch(cmd){
		case 0:		//Move part from tree 1 to tree 2
			if(tree.getSelectedNode() == null) break;
			
			if(tree.getSelectedNode().isAssembly())
				tree2.addAssembly(tree.getSelectedNode().getAssembly(), 
						tree2.getRoot());
			else
				tree2.addPart(tree.getSelectedNode().getPart(),
						tree2.getRoot());
			
			tree.removeSelected();
			tree2.expandCurrentPath(); //Show new stuff
			break;
			
		case 1:		//Submit new service
			if(FormatChecker.blankStringCheck(name.getText(), "Name")){
				
				def.setName(name.getText());
				
				category.addService(new Service(
						category.getAddress(),
						0,
						0,
						false,
						false,
						name.getText(), //TODO name must not contain any "."
						Item.NOT_LISTED,
						null,
						null,
						null,
						def,
						null));
				
				frame.dispose();
				GUIEngine.refresh(-1, -1);
				ManageServices.initializeFrame(-1);
			}
			
		case 2: //Auction RadioButton hit
			if(currentIndex != -1) {
				auto[currentIndex].setSelected(false);
				auto[currentIndex].setEnabled(false);
				duration[currentIndex].setText("7");
				if(Database.getFreeListingsAllotted() - free > 0)
					fee[currentIndex].setText("0.0");
				else
					fee[currentIndex].setText("0.25");
			}
			
			break; 
			
		case 3: //Fixed RadioButton hit
			if(currentIndex != -1){
				auto[currentIndex].setEnabled(true);
				duration[currentIndex].setText("30"); 
				if(Database.getFreeListingsAllotted() - free > 0)
					fee[currentIndex].setText("0.0");
				else
					fee[currentIndex].setText("0.05");
			}
			
			break; 
		
		case 4:	//List Service
			
			if(FormatChecker.quantityCheck(quantity[currentIndex].getText()) 
					&& FormatChecker.creditCheck(price[currentIndex].getText()) 
					&& FormatChecker.quantityCheck(duration[currentIndex].getText()) 
					&& FormatChecker.creditCheck(fee[currentIndex].getText()) 
					&& FormatChecker.dateCheck(date[currentIndex].getText())){
				
				
				
				Assembly a = category.getServices()[currentIndex].getDefaultAssembly();
				Assembly s = category.getPartSupply();
				
				//Check for insufficient parts
				if(a != null){
					
					if(a.getAssemblies() != null)
						for(int i=0; i<a.getAssemblies().length; i++){
							
							Assembly clone = new Assembly(a.getAssemblies()[i]);
							clone.setStatus(Assembly.SUPPLY);
							
							Assembly avail = s.findEquivalentAssemblyFromSupply(clone);
								//TODO There may be multiple parts in supply that meet Equiv, this only returns
									//and uses the first one it finds, expand to use more than one
							
							if(a.getAssemblies()[i].getQuantity() > avail.getQuantity()){
								OptionPane.showError(a.getAssemblies()[i].getName() 
										+ " needed for listing: " + a.getAssemblies()[i].getQuantity()
										+ "\n" + a.getAssemblies()[i].getName() + " in supply: "
										+ avail.getQuantity(), "Insufficient Assemblies!");
								return;
							}
									
						}
				

					if(a.getParts() != null)
						for(int i=0; i<a.getParts().length; i++){
							
							Part clone = new Part(a.getParts()[i]);
							clone.setStatus(Part.SUPPLY);
							
							Part avail = s.findEquivalentPartFromSupply(clone);
								//TODO There may be multiple parts in supply that meet Equiv, this only returns
									//and uses the first one it finds, expand to use more than one
							
							if(a.getParts()[i].getQuantity() > avail.getQuantity()){
								OptionPane.showError(a.getParts()[i].getName()
										+ " needed for listing: " + a.getParts()[i].getQuantity()
										+ "\n" + a.getParts()[i].getName() + " in supply: "
										+ avail.getQuantity(), "Insufficient Parts");
								return;
							}
						}
				
					
					//Reserve parts in pool
						if(a.getAssemblies() != null)
							for(int i=0; i<a.getAssemblies().length; i++){
									
									//How many of this assembly need to be found in supply
								int needed = a.getAssemblies()[i].getQuantity() * Integer.parseInt(quantity[currentIndex].getText());
								
									//Make clone with SUPPLY status for Equiv search
								Assembly clone = new Assembly(a.getAssemblies()[i]);
								clone.setStatus(Assembly.SUPPLY);
								
								Assembly orig = s.findEquivalentAssemblyFromSupply(clone);
									//TODO There may be multiple parts in supply that meet Equiv, this only returns
										//and uses the first one it finds, expand to use more than one
								
								if(orig.getQuantity() == needed)
									orig.setStatus(Assembly.HOLD);
								else{
									orig.setQuantity(orig.getQuantity() - needed);
									Assembly clone1 = new Assembly(orig);
									clone1.setQuantity(needed);
									clone1.setStatus(Assembly.HOLD);
									s.addAssembly(clone);
								}
							}
					
					if(a.getParts() != null)
						for(int i=0; i<a.getParts().length; i++){
							int needed = a.getParts()[i].getQuantity() * Integer.parseInt(quantity[currentIndex].getText());
							
							Part temp = new Part(a.getParts()[i]);
							temp.setStatus(Part.SUPPLY);
							
							Part orig = s.findEquivalentPartFromSupply(temp);
								//TODO There may be multiple parts in supply that meet Equiv, this only returns
									//and uses the first one it finds, expand to use more than one
							
							if(orig.getQuantity() == needed)
								orig.setStatus(Part.HOLD);
							else{
								orig.setQuantity(orig.getQuantity() - needed);
								Part clone = new Part(orig);
								clone.setQuantity(needed);
								clone.setStatus(Part.HOLD);
								s.addPart(clone);
							}
						}
				}
					
				
				category.getServices()[currentIndex].setQuantity(
						Integer.parseInt(quantity[currentIndex].getText()));
				
				category.getServices()[currentIndex].setListAmount(
						new Credit(price[currentIndex].getText()));
				
				String type = (radio[currentIndex].isSelected())? Item.FIXED : Item.AUCTION;
				category.getServices()[currentIndex].setListType(type);
				
				category.getServices()[currentIndex].setListDuration(
						Integer.parseInt(duration[currentIndex].getText()));
				
				Date d = new Date(date[currentIndex].getText());
				category.getServices()[currentIndex].setDateListed(d);
				category.getServices()[currentIndex].setDiscounted(false); //TODO make jcheckbox option
				
				category.getServices()[currentIndex].setListFee(new Credit(fee[currentIndex].getText()));
				Database.addToEbayBalance(new Date(date[currentIndex].getText()).getMonth(), 
						new Credit(fee[currentIndex].getText()));
				
				category.getServices()[currentIndex].setAutoList(auto[currentIndex].isSelected());
				
					//Set Assembly null if no parts used (e.g. Diagnosis)
				if(a != null)
					if(a.getAssemblies() == null && a.getParts() == null)
						category.getServices()[currentIndex].setDefaultAssembly(null);
				
				GUIEngine.refresh(-1, -1);
				frame.dispose();
				ManageServices.initializeFrame(currentIndex + 1);
			}
			break;
			
		case 5: category.getServices()[currentIndex].setAutoList(auto[currentIndex].isSelected()); break;
		
		case 6:	//End Listing
			category.getServices()[currentIndex].setListDuration(0);
			category.getServices()[currentIndex].setQuantity(0);
			category.getServices()[currentIndex].setDiscounted(false);
			category.getServices()[currentIndex].setAutoList(false);
			category.getServices()[currentIndex].setListType(Item.NOT_LISTED);
			category.getServices()[currentIndex].setListAmount(null);
			category.getServices()[currentIndex].setListFee(null);
			category.getServices()[currentIndex].setDateListed(null);
			GUIEngine.refresh(-1, -1);
			frame.dispose();
			ManageServices.initializeFrame(currentIndex + 1);
			break;
			
		case 7: //Delete Service
			category.removeService(category.getServices()[currentIndex]);
			GUIEngine.refresh(-1, -1);
			frame.dispose();
			ManageServices.initializeFrame(0);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		CardLayout cards = (CardLayout) (content.getLayout());
		@SuppressWarnings("unchecked")
		JList<String> sel = (JList<String>) e.getSource();
		int index = sel.getSelectedIndex() - 1;
		cards.show(content, "" + index);
		currentIndex = index;	//Memory
	}

}
