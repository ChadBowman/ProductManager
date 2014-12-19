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

public class ServiceBuilder {
	
	//----- Advanced Methods
	public static JTabbedPane initializeServicePane(){
		JTabbedPane tabbedPane = new JTabbedPane(); //Parent TPane
		
		ProductCategory[] pCategories = Database.getProductCategories();
		ServiceCategory[] sCategories = Database.getServiceCategories();
		
		if(pCategories != null)
			for(int i=0; i<pCategories.length; i++)
				tabbedPane.addTab(pCategories[i].getName(), buildTab(pCategories[i]));
		
		if(sCategories != null)
			for(int i=0; i<sCategories.length; i++)
				tabbedPane.addTab(sCategories[i].getName(), buildTab(sCategories[i]));
		
		return tabbedPane;
	}
	
	public static JPanel buildTab(ServiceCategory category){
		JPanel toReturn = new JPanel(new BorderLayout());
		JPanel container = new JPanel(new GridBagLayout());
		toReturn.add(new ToolBar(GUIEngine.SERVICE), BorderLayout.PAGE_START);
		toReturn.add(new JScrollPane(container), BorderLayout.CENTER);
		GridBagConstraints g = new GridBagConstraints();
		int width = 275, height = 290;
		
		//Repairs
		g.gridx = 0;
		g.gridy = 0;
		g.gridheight = 2;
		g.insets = new Insets(0,0,0,20);
		SplitPane sev = new SplitPane(category.getServices(), ItemSorter.SERVICE_LISTASC);
		sev.setPreferredSize(new Dimension(width, height*2));
		sev.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), "Services"));
		container.add(sev, g);
		
		//Pending
		g.gridx = 1;
		g.gridheight = 1;
		SplitPane pen = new SplitPane(category.getAllRepairsByStatus(Repair.PENDING), ItemSorter.REPAIR_SOLDDES);
		pen.setPreferredSize(new Dimension(width, height));
		pen.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), Repair.PENDING));
		container.add(pen, g);
		//Incoming
		g.gridy = 1;
		SplitPane inc = new SplitPane(category.getAllRepairsByStatus(Repair.INCOMING), ItemSorter.REPAIR_SOLDDES);
		inc.setPreferredSize(new Dimension(width, height));
		inc.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), Repair.INCOMING));
		container.add(inc, g);
		//Returning
		g.gridx = 2;
		g.gridy = 0;
		SplitPane ret = new SplitPane(category.getAllRepairsByStatus(Repair.RETURNED), ItemSorter.REPAIR_RETURNASC);
		ret.setPreferredSize(new Dimension(width, height));
		ret.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), "Returning"));
		container.add(ret, g);
		//Completed
		g.gridy = 1;
		SplitPane com = new SplitPane(category.getAllRepairsByStatus(Repair.COMPLETE), ItemSorter.REPAIR_SOLDDES);
		com.setPreferredSize(new Dimension(width, height));
		com.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), Repair.COMPLETE));
		container.add(com, g);
		
		//Stats
		g.gridx = 3;
		g.gridy = 0;
		g.gridheight = 2;
		g.insets = new Insets(0,0,0,0);
		JPanel stat = new JPanel();
		stat.setPreferredSize(new Dimension(width, height*2));
		stat.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(ColorManager.getOrthusBronze(), 2), "Stats"));
		container.add(stat, g);
		
		
		return toReturn;
	}

}
