package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GenericList extends ActionFrame 
						 implements ActionListener{
	

	//----- Variables
	//UI
	private static GenericList frame;
	private TextField amt, dur, fee, date, quant, title, epid,
		cost1, cost2, cost3, weight, instruction;
	private JRadioButton fixed;
	private JCheckBox auto, offer;
	private ComboBox<String> returns, condid, qual, ship1, ship2, ship3;
	
	private ImageManager im;
	private JTextArea cond, area;
	private String picURL;
	private ActionFrame edit;

	//----- Constructor
	public GenericList() {
		super("List Item", 900, 475, new GridBagLayout());
		
		//Left Side
		JPanel left = new JPanel(new GridLayout(0,1));
		
		//EPID / Category
		left.add(epid = new TextField("EPID / Category"));
		
		JPanel pan0 = new JPanel(new GridLayout());
		//Quantity
		pan0.add(quant = new TextField("1", "Quantity"));
		//Price
		pan0.add(amt = new TextField("", "Start/List Price"));
		left.add(pan0);
		
		//Return Policy
		String[] rets = {"14 Day", "30 Day", "No Returns"};
		left.add(returns = new ComboBox<String>(null, rets, null, -1, "Return Policy"));
		
		JPanel pan = new JPanel(new GridLayout());
		//Condition
		String[] ar = {"New", "New With Defects", "Used", "Good", "Acceptable", "Not Working"};
		pan.add(condid = new ComboBox<String>(null, ar, null, -1, "Condition")); 
		
		//Quality
		pan.add(qual = new ComboBox<String>(null, Part.getQualityArray(), this, 5, "Quality"));
		qual.setSelectedIndex(3);
		left.add(pan);
		
		//Shipping
		String[] ships = {"First Class","FR Envelope", "FR Box", "Express"};
		String[] ships2 = {"None", "First Class","FR Envelope", "FR Box", "Express"};
		
		JPanel ship11 = new JPanel(new GridLayout());
		ship11.add(ship1 = new ComboBox<String>(null, ships, null, -1, "Shipping Op 1"));
		ship11.add(cost1 = new TextField("2.00,0.00", "CX, Add Cost"));
		left.add(ship11);
		
		JPanel ship22 = new JPanel(new GridLayout());
		ship22.add(ship2 = new ComboBox<String>(null, ships2, null, -1, "Shipping Op 2"));
		ship2.setSelectedIndex(2);
		ship22.add(cost2 = new TextField("5.00,0.00", "CX, Add Cost"));
		left.add(ship22);
		
		JPanel ship33 = new JPanel(new GridLayout());
		ship33.add(ship3 = new ComboBox<String>(null, ships2, null, -1, "Shipping Op 3"));
		ship3.setSelectedIndex(4);
		ship33.add(cost3 = new TextField( "20.00,0.00", "CX, Add Cost"));
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
		rightBot.add(title = new TextField("", "Title"), BorderLayout.PAGE_START);
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
		
		//Right Side
		JPanel right = new JPanel(new GridLayout(0,1)); 
		
		//List Format
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
		
		//Date
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
		
		//List Fee
		right.add(fee = new TextField("0.0", "Listing Fee"));
		
		g.gridx = 3;
		g.gridwidth = 1;
		center.add(right, g);
		
		bottom.add(new Button("List", this, 1));
	}

	//----- Methods
	public static void initializeFrame(){
		
		if(!new File("D:/DCIM/100CANON").exists()){
			
			OptionPane.showError("Please insert the correct SD card.", "File System Error");
			return;
		}
		
		frame = new GenericList();
		frame.setVisible(true);
	}
	
	//----- Interfaces
	@Override
	public void actionPerformed(ActionEvent arg0) {
		int cmd = Integer.parseInt(arg0.getActionCommand());
		switch(cmd){
		case 0:
			if(fixed.isSelected()){
				dur.setText("30");
				auto.setEnabled(true);
				auto.setSelected(true);
				offer.setEnabled(true);
			}else{
				dur.setText("7");
				auto.setSelected(false);
				auto.setEnabled(false);
				offer.setEnabled(false);
				offer.setSelected(false);
			}
			break;
			
		case 1:	//List
			
			if(FormatChecker.creditCheck(amt.getText())
					&& FormatChecker.quantityCheck(dur.getText())
					&& FormatChecker.creditCheck(fee.getText())
					&& FormatChecker.dateCheck(date.getText())
					&& FormatChecker.quantityCheck(quant.getText())
					&& FormatChecker.stringCheck(title.getText())
					&& FormatChecker.quantityCheck(epid.getText())
					&& FormatChecker.commaCostCheck(cost1.getText())
					&& FormatChecker.commaCostCheck(cost2.getText())
					&& FormatChecker.commaCostCheck(cost3.getText())
					&& FormatChecker.quantityCheck(weight.getText())
					&& FormatChecker.stringCheck(instruction.getText())){
				
				//Check for uploaded pics
				if(picURL == null)
					if(OptionPane.showConfirmYes("Do you want to upload images?", "Image Upload Warning!") == 0)
						return;
				
				String con = cond.getText().replaceAll("\"", "\"\"");
				       con = con.replace("\n", "");
				String ins = instruction.getText().replaceAll("\"", "\"\"");
				String tit = title.getText().replace("\"", "\"\"");
					
				Database.addToEbayBalance(new Date(date.getText()).getMonth(), 
						new Credit(fee.getText()));
				 
				String label = title.getText().replace(" ", "") + Date.getTodaysDate().hashCode();
				
				
				try {
					new FileExchangeCreator().writeListing(epid.getText(),
														   true,
														   tit,
														   Database.getGenericDescriptionTemplate(),
														   con,
														   (String) qual.getSelectedItem(),
														   (String) condid.getSelectedItem(),
														   quant.getText(),
														   fixed.isSelected(),
														   amt.getText(),
														   offer.isSelected(),
														   auto.isSelected(),
														   dur.getText(),
														   ins,
														   "1",
														   "",
														   (String) returns.getSelectedItem(),
														   weight.getText(),
														   label,
														   (String) ship1.getSelectedItem(),
														   cost1.getText(),
														   (String) ship2.getSelectedItem(),
														   cost2.getText(),
														   (String) ship3.getSelectedItem(),
														   cost3.getText(),
														   picURL);
					
				} catch (IOException e) {
					System.err.println("Writing CSV failed!");
					e.printStackTrace();
				}
						
			
				GUIEngine.refresh(-1, -1);
				frame.dispose();
				
			}break;
			
		case 2: //Upload Images
			
			im.setProgress("Uploading...");
			
			Thread t = new Thread(new Runnable(){

				@Override
				public void run() {
					picURL = im.overlayAndUpload();
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
			
		case 5: //Condition changes
			
			if(qual.getSelectedIndex() == 0 || qual.getSelectedIndex() == 1 || qual.getSelectedIndex() == 2){
				condid.setSelectedIndex(0);
				returns.setSelectedIndex(0);
				
			}else if(qual.getSelectedIndex() == 12){
				condid.setSelectedIndex(2);
				returns.setSelectedIndex(2);
				
			}else{
				condid.setSelectedIndex(2);
				returns.setSelectedIndex(0);
				
			}
			break;
		}
	}

}
