package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class ProductBuilder{
	
	//----- Advanced Methods
	public static JTabbedPane initializeProductPane(){
		JTabbedPane tabbedPane = new JTabbedPane(); //Parent TPane
		
		ProductCategory[] categories = Database.getProductCategories();
		
		if(categories != null)
			for(int i=0; i<categories.length; i++)
				tabbedPane.addTab(categories[i].getName(), buildTab(categories[i]));
		
		return tabbedPane;
	}
	
	public static JPanel buildTab(ProductCategory category){
		JPanel toReturn = new JPanel(new BorderLayout());
		JPanel container = new JPanel(new GridBagLayout());
		toReturn.add(new ToolBar(GUIEngine.PRODUCT), BorderLayout.PAGE_START);
		toReturn.add(new JScrollPane(container), BorderLayout.CENTER);
		GridBagConstraints g = new GridBagConstraints();
		int width = 275, height = 290;
		
		//Inventory SplitPane
		g.gridx = 0;	
		g.gridy = 0;
		g.insets = new Insets(0,0,0,20);
		SplitPane inv = new SplitPane(category.getProductsByStatus(Product.INVENTORY), ItemSorter.PRODCUT_ESTRETURN);
		inv.setPreferredSize(new Dimension(width, height));
		inv.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), Product.INVENTORY));
		container.add(inv, g);
		//Listed SplitPane
		g.gridx = 1;
		SplitPane list = new SplitPane(category.getProductsByStatus(Product.LISTED), ItemSorter.PRODUCT_LISTASC);
		list.setPreferredSize(new Dimension(width, height));
		list.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), Product.LISTED));
		container.add(list, g);
		//Returning SplitPane
		g.gridx = 2;
		SplitPane ret = new SplitPane(category.getProductsByStatus(Product.RETURNED), ItemSorter.PRODUCT_RETURNASC);
		ret.setPreferredSize(new Dimension(width, height));
		ret.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), "Returning"));
		container.add(ret, g);
		//Incoming SplitPane
		g.gridx = 0;
		g.gridy = 1;
		SplitPane inc = new SplitPane(category.getProductsByStatus(Product.INCOMING), ItemSorter.PRODUCT_PURCHASC);
		inc.setPreferredSize(new Dimension(width, height));
		inc.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), Product.INCOMING));
		container.add(inc, g);
		//Stock SplitPane
		g.gridx = 1;
		SplitPane stc = new SplitPane(category.getProductsByStatus(Product.STOCK), ItemSorter.PRODCUT_ESTRETURN);
		stc.setPreferredSize(new Dimension(width, height));
		stc.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), Product.STOCK));
		container.add(stc, g);
		//Sold SplitPane
		g.gridx = 2;
		SplitPane sld = new SplitPane(category.getProductsByStatus(Product.SOLD), ItemSorter.PRODUCT_SOLDDES);
		sld.setPreferredSize(new Dimension(width, height));
		sld.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), Product.SOLD));
		container.add(sld, g);
		
		
		//Stats pane
		JPanel stats = new JPanel();
		JTextArea pane = new JTextArea();
		pane.setEditable(false);
		pane.setBackground(ColorManager.getTransparent());
		pane.setFont(FontManager.getMonospacedFont());
		stats.setBorder(BorderManager.getTitleBorder("Statistics"));
		stats.setPreferredSize(new Dimension(width, height * 2));
		pane.setText(new StatManager().absorbtionSummary(category));
		stats.add(new JScrollPane(pane));
		g.gridx = 3;
		g.gridy = 0;
		g.gridheight = 2;
		g.insets = new Insets(0,0,0,0);
		container.add(stats, g);
	
		
		return toReturn;
	}
}
