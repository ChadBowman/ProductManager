package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ActionFrame extends JFrame {
	//----- Variables
	protected JPanel top, center, bottom;
	
	//----- Constructors
	public ActionFrame(){ prepareFrame(100,100); }
	
	public ActionFrame(String title) throws HeadlessException {
		super(title);
		prepareFrame(100,100);
	}
	
	public ActionFrame(String title, int width, int height){
		super(title);
		prepareFrame(width, height);
	}
	
	public ActionFrame(String title, LayoutManager cent){
		super(title);
		prepareFrame(100,100);
		center.setLayout(cent);
	}
	
	public ActionFrame(String title, int width, int height, LayoutManager cent){
		super(title);
		prepareFrame(width, height);
		center.setLayout(cent);
	}
	
	//FULL Constructor
	public ActionFrame(String title, int width, int height,
			LayoutManager tp, LayoutManager cen, LayoutManager btt){
		super(title);
		prepareFrame(width, height);
		top.setLayout(tp);
		center.setLayout(cen);
		bottom.setLayout(btt);
		
	}
	
	public JPanel getCenter(){
		return center;
	}
	
	public JPanel getBottom(){
		return bottom;
	}
	
	private void prepareFrame(int width, int height){
		
		try{ this.setIconImage(ImageIO.read(new File("Images/Orthus-Icon.jpg")));
		}catch (IOException e){ e.printStackTrace(); }
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(width,height));
		this.add(top = new JPanel(), BorderLayout.PAGE_START);
		this.add(center = new JPanel(), BorderLayout.CENTER);
		this.add(bottom = new JPanel(), BorderLayout.PAGE_END);
		pack();
		Toolkit kit = this.getToolkit();
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] dev = env.getScreenDevices();
		Insets ins = kit.getScreenInsets(dev[0].getDefaultConfiguration());
		Dimension dim = kit.getScreenSize();
		int maxWidth = (dim.width - ins.left - ins.right);
		int maxHeight = (dim.height - ins.top - ins.bottom);
		this.setLocation((int) (maxWidth - this.getWidth()) / 2, 
				         (int) (maxHeight - this.getHeight()) / 2);
	}
}
