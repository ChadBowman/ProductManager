package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class InventoryMode extends ActionFrame implements ActionListener {

	private static final long serialVersionUID = -6405758578168854772L;
	// Variables
	private static InventoryMode frame;
	
	private ProductCategory cat;
	private ArrayList<JTextField> inputs;
	private ArrayList<Constituent> parts;
	
	// Constructor
	private InventoryMode(ProductCategory cat){
		super("" + cat.getName() + " Inventory", 600, 500, new BorderLayout());
		
		this.cat = cat;
		ArrayList<Part> template = cat.getDefaultAssembly().collapse();
		
		JPanel scroll = new JPanel(new GridLayout(0,1));
		center.add(new JScrollPane(scroll), BorderLayout.CENTER);
		JPanel p = new JPanel();
		p.add(new Button("Submit", this, 0));
		center.add(p, BorderLayout.PAGE_END);
		
		inputs = new ArrayList<JTextField>();
		parts = new ArrayList<Constituent>();
		
		String[] status = {Part.SUPPLY, 
						   Part.HOLD, 
						   Part.LISTED_AUCTION, 
						   Part.LISTED_FIXED, 
						   Part.LISTED_FIXED_AUTO };
		
		ArrayList<Constituent> supply = cat.getSupplyConstituentsByStatus(status);
		
		
		for(int i=0; i<template.size(); i++){ // Cycle through each avail part
			
			int quant = 0; // Total quantity of part
			
			for(int j=0; j<supply.size(); j++)
				if(supply.get(j).getSerial() == template.get(i).getSerial()){ // Same part
				
					Constituent c = supply.get(j);
					JPanel row = new JPanel(new GridLayout(1,0));
					row.add(new JLabel(c.getName(), JLabel.CENTER));	// Name
					row.add(new JLabel(c.getStatus(), JLabel.CENTER));  // Status
					
					String color = "";
					
					if(c.getColor() != null)
						color = (c.getColor().equals(Part.NO_COLOR))? "" : c.getColor();
					
					row.add(new JLabel(color, JLabel.CENTER));
					
					String qual = "";
					
					try{
						if(((Part) c) != null)
							if(((Part) c).getQuality() != null)
								qual = (((Part) c).getQuality().equals(Part.NO_QUALITY))? 
										"" : ((Part) c).getQuality();
						
					}catch(ClassCastException e){}
					
					row.add(new JLabel(qual, JLabel.CENTER));
				
					JTextField qt = new JTextField("" + c.getQuantity() * c.getNumPerListing());
					inputs.add(qt);
					parts.add(c);
					row.add(qt);
					
					row.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
					
					scroll.add(row);
				}
			
			}
		
	
		
		
	}
	
	
	public static void initializeFrame(){
		
		frame = new InventoryMode((ProductCategory) Database.getFocusedCategory());
		frame.setVisible(true);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		for(int i=0; i<inputs.size(); i++){ // For each row
			
			if(FormatChecker.quantityCheck(inputs.get(i).getText())){
				
				int quantity = Integer.parseInt(inputs.get(i).getText());
				
				if(quantity == 0)
					try{
						
						cat.removePartFromSupply((Part) parts.get(i));
						
					}catch(ClassCastException e){
						
						cat.removeAssemblyFromSupply((Assembly) parts.get(i));
					}
				
				if(quantity % parts.get(i).getNumPerListing() != 0){
					
					OptionPane.showError("Quantity for " + parts.get(i).getName() + " " + parts.get(i).getStatus() +
							"\nMust be in multiples of " + parts.get(i).getNumPerListing() + ".", "Error!");
					
					return;
				}
				
				parts.get(i).setQuantity(quantity / parts.get(i).getNumPerListing());
				
			}
			
		}
		
		GUIEngine.refresh(-1, -1);
		frame.dispose();
		
	}

}
