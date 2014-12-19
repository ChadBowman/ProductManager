package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.orthus.pm.ProductStats.Reorder;

public class PartsBuilder implements ActionListener{
		
	private JTextField bar;
	private ComboBox<String> color, quality, status;
	private SplitPane sea;
	private Constituent[] search;
	private Category cat;
	
	public PartsBuilder(){}
	
	public JPanel buildTab(Category category){
		
		cat = category;
		JPanel toReturn = new JPanel(new BorderLayout());
		JPanel container = new JPanel(new GridBagLayout());
		toReturn.add(new ToolBar(GUIEngine.PART), BorderLayout.PAGE_START);
		toReturn.add(new JScrollPane(container), BorderLayout.CENTER);
		GridBagConstraints g = new GridBagConstraints();
		int width = 275, height = 290;
		
		//Assemblies
		g.gridx = 0;
		g.gridy = 0;
		g.gridheight = 3;
		g.insets = new Insets(0,0,0,20);
		SplitPane ass = new SplitPane(category.getTemplates());
		ass.setPreferredSize(new Dimension(width*2-150, height*2));
		ass.setBorder(BorderManager.getTitleBorder("Template Assemblies"));
		container.add(ass, g);
		
		
		//Incoming
		g.gridx = 1;
		g.gridheight = 1;
		Constituent[] cons = category.getSupplyConstituentsByStatus(Constituent.INCOMING);
		cons = ArrayManager.addToArray(
				category.getSupplyConstituentsByStatus(Constituent.SHIPPING_INCOMING), cons);
		
		SplitPane inc = new SplitPane(cons, ItemSorter.CONST_PURCHASC);
		inc.setPreferredSize(new Dimension(width, 100));
		inc.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), Assembly.INCOMING));
		container.add(inc, g);
		
		//Stats
		g.gridy = 1;
		g.gridheight = 2;
		JPanel stats = new JPanel();
		stats.setPreferredSize(new Dimension(width, height*2-100));
		stats.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), "Stats"));
		container.add(stats, g);
		
		JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setBackground(ColorManager.getTransparent());
		stats.add(area);
		
		ProductStats stat = new ProductStats((ProductCategory) category);
		Credit total = stat.getCategoryTotal();
		area.append(String.format("%s Value: %s (%.0f%%)%n%n", 
				category.getName(), 
				total.toString(),
				100 * total.getValueInDollars() / Database.getTotalInvestment().getValueInDollars()));
		
		Reorder[] toReOrder = stat.neededConstituents();
		Reorder[] toLiquidate = stat.toLiquidate();
		
		if(toReOrder != null){
			area.append("Recomend Ordering:\n");
			for(int i=0; i<toReOrder.length; i++)
				area.append(toReOrder[i].getNeedCount() + " " + 
						toReOrder[i].getPart().getName() + " [" + toReOrder[i].getConsumption() + "]\n");
		}
		
		/*
		if(toLiquidate != null){
			area.append("\nRecommend Liquidating:\n");
			for(int i=0; i<toLiquidate.length; i++)
				area.append(toLiquidate[i].getNeedCount() + " " +
						toLiquidate[i].getPart().getName() + "\n");
		}
		*/
		
		//Search Mech
		g.gridx = 2;
		g.gridy = 0;
		g.gridheight = 1;
		g.gridwidth = 2;
		g.insets = new Insets(0,0,0,0);
		JPanel mech = new JPanel(new GridLayout(0,1));
		mech.setPreferredSize(new Dimension(width*2-150, 100));
		mech.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), "Search Mech"));
		container.add(mech, g);
		mech.add(bar = new JTextField());
		
		JPanel btns = new JPanel(new GridLayout(1,0));
		btns.add(color = new ComboBox<String>("Color", ((ProductCategory) category).getColors(), null, -1));
		btns.add(quality = new ComboBox<String>("Quality", Part.getQualityArray(), null, -1));
		String[] stts = {Constituent.INCOMING, Constituent.SUPPLY, "Listed", Constituent.SOLD, 
				Constituent.RETURNED, Constituent.HOLD,Constituent.PRODUCT, Constituent.TEMPATE};
		btns.add(status = new ComboBox<String>("Status", stts, null, -1));
		status.setSelectedIndex(2);
		btns.add(new Button("Search", this, 0));
		mech.add(btns);
		
		//Search
		g.gridy = 1;
		g.gridheight = 2;
		search = cat.getSupplyConstituentsByFilter(
				bar.getText(), 
				(String) color.getSelectedItem(), 
				(String) quality.getSelectedItem(), 
				"Supply");
		sea = new SplitPane(search, ItemSorter.CONST_ALPH);
		sea.setPreferredSize(new Dimension(width*2-150, height*2-100));
		//sea.setBorder(BorderFactory.createTitledBorder(
		//		BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), "Search"));
		container.add(sea, g);
		
		
		return toReturn;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		search = cat.getSupplyConstituentsByFilter(
					bar.getText(), 
					(String) color.getSelectedItem(), 
					(String) quality.getSelectedItem(), 
					(String) status.getSelectedItem());
	
		sea.setNewData(search);
	
	}
}
