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

public class ListProduct extends ActionFrame 
						 implements ActionListener {

	//----- Variables
	//UI
	private static ListProduct frame;
	private TextField amt, dur, fee, date, note, instruction;
	private JRadioButton fixed;
	private ComboBox<String> qual, condid;
	private JTextArea cond;
	private ImageManager im;
	private String picURLs;
	private JCheckBox auto, info, file;
	
	private JTextArea area;
	private ActionFrame edit;
	
	//Data
	private static Product p;
	private boolean returned;

	//----- Constructor
	public ListProduct() {
		super("List Product", 750, 475, new GridBagLayout());
		
		returned = (p.getStatus().equals(Product.RETURNED))? true : false;
		
		JPanel left = new JPanel(new GridLayout(0,1));
		
		//Name
		left.add(new Label(p.getFullName(), "", SwingConstants.CENTER));
		
		//Price
		left.add(amt = new TextField(
				"" + p.getRecommendedRetailPrice().getValueInDollars(), 
				"List Price"));
		
		//Condition
		String[] ar = {"Seller Refirbished", "Used", "Not Working"};
		left.add(condid = new ComboBox<String>(null, ar, null, -1, "Condition")); 
		condid.setSelectedIndex(1);
		
		//Quality
		left.add(qual = new ComboBox<String>(null, Part.getQualityArray(), 
				this, 5, "Overall Quality"));
		qual.setSelectedIndex(3);
		

		
		//List Format
		JPanel radios = new JPanel(new GridLayout(0,2));
		radios.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
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
		left.add(radios);

		//List Duration
		left.add(dur = new TextField("30", "List Duration"));
		
		//Upload images
		left.add(new Button("Upload Images", this, 2));

		
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 0;
		
		center.add(left, g);
		
		//Center
		JPanel cen = new JPanel(new BorderLayout());
		
		JPanel rightBot = new JPanel(new BorderLayout());
		
		cen.setPreferredSize(new Dimension(425, 380));
		cen.add(im = new ImageManager(ImageManager.PRODUCT), BorderLayout.CENTER);
		
		JScrollPane sc = new JScrollPane(cond = new JTextArea());
		sc.setPreferredSize(new Dimension(425, 75));
		rightBot.add(sc, BorderLayout.CENTER);
		cen.add(rightBot, BorderLayout.PAGE_END);
		
		cond.setBorder(BorderManager.getTitleBorder("Description"));
		
		g.gridx = 1;
		g.gridwidth = 2;
		center.add(cen, g);
		
		//Right
		JPanel right = new JPanel(new GridLayout(0,1));
		
		//CSV Option
		right.add(file = new JCheckBox("Add To CSV"));
		right.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		file.setSelected(true);
		
		//Date
		right.add(date = new TextField(Date.getTodaysDate().displaySimpleDate(),
				"List Date"));
		
		//List Fee
		right.add(fee = new TextField("0.0", "Listing Fee"));
		
		//Auto Relist
		right.add(auto = new JCheckBox("Automatic Relist"));
		auto.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		auto.setSelected(true);
		
		//Product Info
		right.add(info = new JCheckBox("Product Information"));
		info.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		info.setSelected(false);
		
		//Shipping Instructions
		right.add(instruction = new TextField("Shipping Instructions"));
		
		//Note
		right.add(note = new TextField(p.getNote(), "Note"));
		
		//Edit Template
		right.add(new Button("Edit Listing Template", this, 3));
		
		g.gridx = 3;
		g.gridwidth = 1;
		center.add(right, g);
	
		
		bottom.add(new Button("List", this, 1));
	}

	//----- Methods
	public static void initializeFrame(){
		p = DataSelection.getSelectedProduct();
		
		if(!new File("D:/DCIM/100CANON").exists()){
			
			OptionPane.showError("Please insert the correct SD card.", "File System Error");
			return;
		}
		
		if(p == null) return;
		if(!p.getStatus().equals(Product.STOCK)
				&& !p.getStatus().equals(Product.RETURNED)
				&& !p.getStatus().equals(Product.LISTED)){
			OptionPane.showConfirm(p.getFullName() + " must be in stock before it can be listed.", 
					"Product Status Error!");
			return;
		}
		
		frame = new ListProduct();
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
				fee.setText("0.0");
				auto.setEnabled(true);
			}else{
				dur.setText("7");
				fee.setText("0.0");
				auto.setSelected(false);
				auto.setEnabled(false);
			}
			break;
			
		case 1:	//List
			
			if(FormatChecker.creditCheck(amt.getText())
					&& FormatChecker.quantityCheck(dur.getText())
					&& FormatChecker.creditCheck(fee.getText())
					&& FormatChecker.dateCheck(date.getText())
					&& FormatChecker.stringCheck(note.getText())
					&& FormatChecker.stringCheck(cond.getText())
					&& FormatChecker.stringCheck(instruction.getText())){
				
				//Check for uploaded pics
				if(picURLs == null && file.isSelected())
					if(OptionPane.showConfirmYes("Do you want to upload images?", "Image Upload Warning!") == 0)
						return;
		
				
				p.setStatus(Product.LISTED);
				p.setListAmount(new Credit(amt.getText()));
				p.setListDuration(Integer.parseInt(dur.getText()));
				p.setListingFee(new Credit(fee.getText()));
				Database.addToEbayBalance(new Date(date.getText()).getMonth(), new Credit(fee.getText()));
				p.setOverallQuality((String) qual.getSelectedItem());
				p.setDateListed(new Date(date.getText()));
				p.setNote(note.getText());
				
				String con = cond.getText().replaceAll("\"", "\"\"");
					   con = con.replace("\n", "");
				String ins = instruction.getText().replaceAll("\"", "\"\"");
				
				if(!returned && file.isSelected())
					new FileExchangeCreator().listProduct(info.isSelected(), 
														  con, 
														  (String) condid.getSelectedItem(), 
														  fixed.isSelected(), 
														  ins, 
														  picURLs,
														  p);
				
				GUIEngine.refresh(-1, -1);
				frame.dispose();
				
			}break;
			
		case 2: //Upload images
			
			im.setProgress("Uploading...");
			
			Thread t = new Thread(new Runnable(){

				@Override
				public void run() {
					picURLs = im.overlayAndUpload(p);
				}
				
			});
		
			t.start();
			
			break;
			
		case 3: //Edit description HTML
			
			edit = new ActionFrame("Product Description Template", 700, 500);
			String des = new String(Database.getProductDescriptionTemplate());
			
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
			
		case 4: //Submit correction
			
			String des2 = area.getText();
			des2 = des2.replace("\n", ""); //Remove line ends
			des2 = des2.replace("\"", "\"\""); //CSV formatting
			des2 = String.format("\"%s\"", des2);
			Database.setProductDescriptionTemplate(des2);
			edit.dispose();
			GUIEngine.noteDataChange();
			break;
			
		case 5: //Condition changes
			
			if(qual.getSelectedIndex() == 0 || qual.getSelectedIndex() == 1 || qual.getSelectedIndex() == 2)
				condid.setSelectedIndex(0);
			else if(qual.getSelectedIndex() == 12)
				condid.setSelectedIndex(2);
			else
				condid.setSelectedIndex(1);
			break;
		}
	}
}
