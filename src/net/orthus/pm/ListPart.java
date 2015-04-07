package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class ListPart extends ActionFrame 
					  implements ActionListener {

	//----- Variables
	//UI
	private static ListPart frame;
	private TextField amt, per, dur, fee, date, quant, title, instruction, weight, cost1, cost2, cost3;
	private JRadioButton fixed;
	private JCheckBox auto, acc, offer, file;
	private ComboBox<String> condition, refund, ship1, ship2, ship3;
	
	private ImageManager im;
	private JTextArea cond, area;
	private String picURL;
	private ActionFrame edit;
	
	//Data
	private static Constituent p;
	private Category c;


	//----- Constructor
	public ListPart() {
		super("List Part", 830, 530, new GridBagLayout());
		
		c = Database.getFocusedCategory();
		
		//Left Side
		JPanel left = new JPanel(new GridLayout(0,1));
		left.add(new Label(p.getName(), "Name", SwingConstants.CENTER));
		
		//Quantity
		JPanel qunts = new JPanel(new GridLayout(1, 0));
		qunts.add(quant = new TextField("" + p.getQuantity(), "Quantity"));
		qunts.add(per = new TextField("1", "Per Listing"));
		left.add(qunts);
		
		//Sell Amount
		left.add(amt = new TextField("" + p.getRecommendedSellAmount().getValueInDollars(), "Start/List Price"));
		
		//Return Policy
		String[] refun = {"30 Day", "14 Day", "No Returns"};
		left.add(refund = new ComboBox<String>(null, refun, null, -1, "Return Policy"));
		refund.setSelectedIndex(1);
		
		//Condition
		String[] conds = {"New", "Good", "Acceptable", "Not Working"};
		left.add(condition = new ComboBox<String>(null, conds, this, 5, "Condition"));
		condition.setSelectedIndex(2);

		
		//Shipping
		String[] ships = {"First Class","FR Envelope", "FR Box", "Express"};
		String[] ships2 = {"None", "First Class","FR Envelope", "FR Box", "Express"};
		
		JPanel ship11 = new JPanel(new GridLayout());
		ship11.add(ship1 = new ComboBox<String>(null, ships, null, -1, "Shipping Op 1"));
		ship11.add(cost1 = new TextField("0.00,0.00", "CX, Add Cost"));
		left.add(ship11);
		
		JPanel ship22 = new JPanel(new GridLayout());
		ship22.add(ship2 = new ComboBox<String>(null, ships2, null, -1, "Shipping Op 2"));
		ship2.setSelectedIndex(2);
		ship22.add(cost2 = new TextField("3.50,0.00", "CX, Add Cost"));
		left.add(ship22);
		
		JPanel ship33 = new JPanel(new GridLayout());
		ship33.add(ship3 = new ComboBox<String>(null, ships2, null, -1, "Shipping Op 3"));
		ship3.setSelectedIndex(4);
		ship33.add(cost3 = new TextField( "12.00,0.00", "CX, Add Cost"));
		left.add(ship33);
		
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 0;
		center.add(left, g);
		
		//Center
		JPanel cen = new JPanel(new BorderLayout());
		JPanel rightBot = new JPanel(new BorderLayout());
		
		cen.setPreferredSize(new Dimension(425, 380));
		cen.add(im = new ImageManager(ImageManager.PART), BorderLayout.CENTER);
		JScrollPane sc = new JScrollPane(cond = new JTextArea());
		sc.setPreferredSize(new Dimension(425, 75));
		rightBot.add(title = new TextField("Nintendo " + c.getName() + " " + p.getName(), "Title"), BorderLayout.PAGE_START);
		rightBot.add(sc, BorderLayout.CENTER);
		JPanel buttons = new JPanel(new GridLayout(1,0));
		buttons.add(new Button("Upload Images", this, 2));
		buttons.add(new Button("Edit Listing Template", this, 3));
		rightBot.add(buttons, BorderLayout.PAGE_END);
		cen.add(rightBot, BorderLayout.PAGE_END);
		
		cond.setBorder(BorderManager.getTitleBorder("Description"));
		
		g.gridx = 1;
		g.gridwidth = 2;
		center.add(cen, g);
		
		//Right
		JPanel right = new JPanel(new GridLayout(0, 1));
		
		//CSV Option
		right.add(file = new JCheckBox("Add To CSV"));
		file.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		file.setSelected(true);
		
		//Listing Format
		JPanel radios = new JPanel(new GridLayout(0,2));
		radios.setBorder(BorderManager.getTitleBorder("List Type"));
		ButtonGroup group = new ButtonGroup();
		JRadioButton mock = new JRadioButton("Auction");
		mock.addActionListener(this);
		mock.setActionCommand("0");
		radios.add(mock);
		radios.add(fixed = new JRadioButton("Fixed"));
		fixed.addActionListener(this);
		fixed.setActionCommand("0");
		fixed.setSelected(true);
		group.add(mock);
		group.add(fixed);
		right.add(radios);

		//Duration
		right.add(dur = new TextField("30", "List Duration"));
		
		
		//List Date
		right.add(date = new TextField(Date.getTodaysDate().displaySimpleDate(),
				"List Date"));
		
		//Weight
		right.add(weight = new TextField("3", "Weight (oz)"));
		
		//Shipping Instructions
		right.add(instruction = new TextField("Shipping Instructions"));
		
		//Auto Relist / Best Offer
		JPanel checks = new JPanel(new GridLayout());
		checks.add(auto = new JCheckBox("Auto Relist"));
		auto.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		auto.setSelected(true);
		checks.add(offer = new JCheckBox("Make Offer"));
		offer.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		right.add(checks);
		
		//Accessory
		right.add(acc = new JCheckBox("Accessory"));
		acc.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
				
		//Listing Fee
		right.add(fee = new TextField("0.00", "Listing Fee"));
		
		g.gridx = 3;
		g.gridwidth = 1;
		center.add(right, g);
		
		
		
		bottom.add(new Button("List", this, 1));
	}

	//----- Methods
	public static void initializeFrame(){
		p = DataSelection.getSelectedConstituent();
		
		//If SD card with pictures exists
		if(!new File("D:/DCIM/100CANON").exists()){
			
			OptionPane.showError("Please insert the correct SD card.", "File System Error");
			return;
		}
		
		if(p == null) return;
		if(!p.getStatus().equals(Constituent.SUPPLY)
				&& !p.getStatus().equals(Constituent.SHIPPING_SUPPLY)){
			
			OptionPane.showConfirm(p.getName() + " must be in supply before it can be listed.", 
					"Part Status Error!");
			return;
		}
		
		frame = new ListPart();
		frame.setVisible(true);
	}
	
	//----- Interfaces
	@Override
	public void actionPerformed(ActionEvent arg0) {
		int cmd = Integer.parseInt(arg0.getActionCommand());
		switch(cmd){
		
		case 0: //Listing format change
			if(fixed.isSelected()){
				dur.setText("30");
				auto.setEnabled(true);
				auto.setSelected(true);
			}else{
				dur.setText("7");
				auto.setSelected(false);
				auto.setEnabled(false);
			}
			break;
			
		case 1:	//List
			
			if(FormatChecker.creditCheck(amt.getText())
					&& FormatChecker.quantityCheck(dur.getText())
					&& FormatChecker.quantityCheck(per.getText())
					&& FormatChecker.commaCostCheck(cost1.getText())
					&& FormatChecker.commaCostCheck(cost2.getText())
					&& FormatChecker.commaCostCheck(cost3.getText())
					&& FormatChecker.creditCheck(fee.getText())
					&& FormatChecker.dateCheck(date.getText())
					&& FormatChecker.quantityCheck(weight.getText())
					&& FormatChecker.stringCheck(instruction.getText())
					&& FormatChecker.quantityCheck(quant.getText())
					&& FormatChecker.stringCheck(title.getText())
					&& FormatChecker.stringCheck(cond.getText())){
				
				
				//Check for uploaded pics
				if(picURL == null && file.isSelected())
					if(OptionPane.showConfirmYes("Do you want to upload images?", "Image Upload Warning!") == 0)
						return;
				
				String con = cond.getText().replaceAll("\"", "\"\"");
					   con = con.replace("\n", "");
				String ins = instruction.getText().replaceAll("\"", "\"\"");
				String tit = title.getText().replace("\"", "\"\"");
				
				int listings = Integer.parseInt(quant.getText());
				int perListing = Integer.parseInt(per.getText());
				
				if((listings * perListing) > p.getQuantity()){
					OptionPane.showError("Insufficient parts for listing.",
							"Insufficient parts");
					return;
				}
				
				
				int total = listings * perListing;
				
				if(total > p.getQuantity()){ //If trying to list more than avail
					
					OptionPane.showError(OptionPane.QUANTITY_LIMIT, "Only " + p.getQuantity() + " availible.");
					break;
					
				}else if(total == p.getQuantity()){ //All of part is used
				
					if(fixed.isSelected() && auto.isSelected())
						p.setStatus(Constituent.LISTED_FIXED_AUTO); //Good-Till-Canceled listing
					else if(fixed.isSelected())
						p.setStatus(Constituent.LISTED_FIXED); //Fixed listing
					else
						p.setStatus(Constituent.LISTED_AUCTION); //Auction
					
					p.setDuration(Integer.parseInt(dur.getText()));
					p.setNumPerListing(Integer.parseInt(per.getText()));
					p.setDateListed(new Date(date.getText()));
					p.setListPrice(new Credit(amt.getText()));
					p.setListFee(new Credit(fee.getText()));
					
					
					Database.addToEbayBalance(new Date(date.getText()).getMonth(), 
							new Credit(fee.getText()));
				 
					
					if(file.isSelected())
						new FileExchangeCreator().listPart(
								tit,
								con,
								(String) condition.getSelectedItem(),
								fixed.isSelected(),
								offer.isSelected(),
								auto.isSelected(),
								ins,
								(String) refund.getSelectedItem(),
								weight.getText(),
								(String) ship1.getSelectedItem(),
								cost1.getText(),
								(String) ship2.getSelectedItem(),
								cost2.getText(),
								(String) ship3.getSelectedItem(),
								cost3.getText(),
								picURL,
								acc.isSelected(),
								p);
						
				
				}else{	//Only partial part
					
					
					int toList = total;
		
					p.setQuantity(p.getQuantity() - toList); //Deduct form parts left
					
					Constituent clone = null;
					try{
						clone = new Part((Part) p);
						c.getPartSupply().addPart((Part) clone);
						
					}catch(ClassCastException e){
						System.out.println("Casting into Assembly");
						clone = new Assembly((Assembly) p);
						c.getPartSupply().addAssembly((Assembly) clone);
					}
					
					clone.setQuantity(toList);
					
					if(fixed.isSelected() && auto.isSelected())
						clone.setStatus(Constituent.LISTED_FIXED_AUTO); //Good-Till-Canceled listing
					else if(fixed.isSelected())
						clone.setStatus(Constituent.LISTED_FIXED); //Fixed listing
					else
						clone.setStatus(Constituent.LISTED_AUCTION); //Auction
					
					clone.setNumPerListing(Integer.parseInt(per.getText()));
					clone.setDuration(Integer.parseInt(dur.getText()));
					clone.setDateListed(new Date(date.getText()));
					clone.setListPrice(new Credit(amt.getText()));
					clone.setListFee(new Credit(fee.getText()));
				
					
					Database.addToEbayBalance(new Date(date.getText()).getMonth(), 
							new Credit(fee.getText()));
					
					if(file.isSelected())
						new FileExchangeCreator().listPart(
								tit,
								con,
								(String) condition.getSelectedItem(),
								fixed.isSelected(),
								offer.isSelected(),
								auto.isSelected(),
								ins,
								(String) refund.getSelectedItem(),
								weight.getText(),
								(String) ship1.getSelectedItem(),
								cost1.getText(),
								(String) ship2.getSelectedItem(),
								cost2.getText(),
								(String) ship3.getSelectedItem(),
								cost3.getText(),
								picURL,
								acc.isSelected(),
								clone);
				}
				
				GUIEngine.refresh(-1, -1);
				frame.dispose();
				
			}break;
			
		case 2: //Upload Images
			
			im.setProgress("Uploading...");
			
			Thread t = new Thread(new Runnable(){

				@Override
				public void run() {
					picURL = im.overlayAndUpload(p, c);
				}
				
			});
		
			t.start();
			
			break;
			
		case 3: //Edit Description
			
			edit = new ActionFrame("Part Description Template", 700, 500);
			String des = new String(Database.getPartDescriptionTemplate());
			
			//Make easier to read
			des = des.substring(1, des.length() - 1); //Chop ends off
			des = des.replace("\"\"", "\"");	//Remove CSV formatting
			des = des.replace("<b", "\n<b"); 
			des = des.replace("<h", "\n<h");
			des = des.replace("<p", "\n<p");
			des = des.replace("<d", "\n<d");
			area = new JTextArea(des);
			area.setLineWrap(true);
			edit.getCenter().setLayout(new BorderLayout());
			edit.getCenter().add(new JScrollPane(area));
			edit.getBottom().add(new Button("Submit Changes", this, 4));
			edit.setVisible(true);
			break;
			
		case 4: //Submit Changes
			
			String des2 = area.getText();
			des2 = des2.replace("\n", ""); //Remove line ends
			des2 = des2.replace("\"", "\"\""); //CSV formatting
			des2 = String.format("\"%s\"", des2);
			Database.setPartDescriptionTemplate(des2);
			edit.dispose();
			GUIEngine.noteDataChange();
			break;
			
		case 5: //Condition Change
			
			if(condition.getSelectedIndex() == 3)
				refund.setSelectedIndex(2);	
			else
				refund.setSelectedIndex(1);
			
			
			
			break;
		}
	}
}
