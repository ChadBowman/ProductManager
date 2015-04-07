package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class GUIEngine{
	//----- Variables
	private static JTabbedPane tPane;
	private static JFrame gui;	
	private static UtilityBar util;

	//----- Constants
	public static final int SUMMARY = 0;
	public static final int PRODUCT = 1;
	public static final int SERVICE = 2;
	public static final int PART = 3;

	//----- Standard Methods
	//Getters
	public static JFrame getMainFrame(){ return gui; }
	public static JTabbedPane getParentTPane(){ return tPane; }
	
	//----- Advanced Methods
	private static void buildUI(){	
		tPane.addTab("Summary", SummaryBuilder.initializeSummary());	
		
		ProductCategory[] pc = Database.getProductCategories();
		if(pc != null)
			for(int i=0; i<pc.length; i++)
				tPane.addTab(pc[i].getName(), compileFrames(pc[i]));
	}
	
	private static JTabbedPane compileFrames(ProductCategory c){
		
		JTabbedPane toRet = new JTabbedPane();
		toRet.addTab("Products", ProductBuilder.buildTab(c));
		toRet.addTab("Services", ServiceBuilder.buildTab(c));
		toRet.addTab("Parts", new PartsBuilder().buildTab(c));
		return toRet;
	}
	
	public static void refresh(int first, int second){
		
		if(first == -1 && second == -1){
			
			first = tPane.getSelectedIndex();

			if(first != 0)
				second = ((JTabbedPane) tPane.getSelectedComponent()).getSelectedIndex();
			
		}

		tPane.removeAll();
		buildUI();
		noteDataChange();
		
		tPane.setSelectedIndex(first);
		if(second != -1)
			((JTabbedPane) tPane.getSelectedComponent()).setSelectedIndex(second);
	}
	
	public static void noteDataChange(){
		if(!gui.getTitle().contains("*"))
			gui.setTitle(gui.getTitle().concat("*"));
	}
	
	public static void setUtilityText(String txt){ util.setText(txt); }
	
	public static void initializeUI() {
		String file = (Database.getLastSavePath() == null)? "" : 
			" [ " + Database.getLastSavePath().replaceAll(".*\\\\", "") + " ]";
		
		gui = new JFrame("Orthus Product Manager " + Database.VERSION + file);
		
		try{ gui.setIconImage(ImageIO.read(new File("Images/Orthus-Icon.jpg")));
		}catch (IOException e){ e.printStackTrace(); }
		
		gui.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		gui.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(Database.onTheClock){
					int sel = JOptionPane.showConfirmDialog(gui, 
							"Are you sure you want to exit?\nYou are clocked in!", 
							"Warning", JOptionPane.YES_OPTION, JOptionPane.WARNING_MESSAGE);
					if(sel == JOptionPane.YES_OPTION)
						System.exit(0);
				}else if(GUIEngine.getMainFrame().getTitle().contains("*")){		
					int sel = JOptionPane.showConfirmDialog(gui, 
							"Are you sure you want to exit?\nAll unsaved changes will be lost.", 
							"Warning", JOptionPane.YES_OPTION, JOptionPane.WARNING_MESSAGE);
					if(sel == JOptionPane.YES_OPTION)
						System.exit(0);
				}else
					System.exit(0);
			}
		});
		
		gui.setVisible(true);
		gui.setExtendedState(JFrame.MAXIMIZED_BOTH);
		gui.setLayout(new BorderLayout());
		gui.add(new Menu(), BorderLayout.PAGE_START);
		gui.add(util = new UtilityBar(), BorderLayout.PAGE_END);
		gui.add(tPane = new JTabbedPane(JTabbedPane.LEFT), BorderLayout.CENTER);
		buildUI();
		
		gui.setMinimumSize(new Dimension(700,400));
	} 
}
