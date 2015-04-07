package net.orthus.pm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ImageManager extends JPanel 
						  implements ListSelectionListener{
	
	private JLabel picture, progress;
	private JList<String> list;
	private JSplitPane splitPane;
	private File[] pics;
	
	public static final int PRODUCT = 0;
	public static final int PART = 1;
	
	public ImageManager(int state){
		super(new BorderLayout());
		
		int[] sel = {0, 1, 2, 3};
		list = new JList<String>(gatherNames());
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		if(state == PRODUCT)
			list.setSelectedIndices(sel);
		else
			list.setSelectedIndex(0);
		list.addListSelectionListener(this);
		
		picture = new JLabel();
		picture.setFont(picture.getFont().deriveFont(Font.ITALIC));
		picture.setHorizontalAlignment(JLabel.CENTER);
		
		JScrollPane data = new JScrollPane(picture);
		data.setPreferredSize(new Dimension(209, 154));
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
								   new JScrollPane(list),
								   data);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(75);
		
		if(state == PRODUCT && gatherNames().length > 3)
			updateLabel(pics[list.getSelectedIndices()[3]]);
		else if(gatherNames().length > 0)
			updateLabel(pics[list.getSelectedIndices()[0]]);
		
		this.add(progress = new JLabel("", SwingConstants.CENTER), BorderLayout.PAGE_START);
		this.add(splitPane, BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(300, 200));
		
	}
	
	
	//Private Methods
	
	private String[] gatherNames(){
		pics = new File("D:/DCIM/100CANON").listFiles();
		
		String[] names = (pics == null)? new String[0]: new String[pics.length];
		for(int i=0; i<names.length; i++)
			names[i] = pics[i].getName();
		
		return names;
	}
	
	private ImageIcon createImageIcon(File file){
		
		
		ImageIcon first = new ImageIcon(file.getPath());
		Image newim = first.getImage().getScaledInstance(288, 216, Image.SCALE_FAST);
		return new ImageIcon(newim);
	
	}
	
	private void updateLabel(File pic){
		ImageIcon icon = createImageIcon(pic);
		picture.setIcon(icon);
		if(icon != null)
			picture.setText(null);
		else
			picture.setText("Image Not Found");
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		if(list.getSelectedIndex() > -1)
			updateLabel(pics[list.getSelectedIndex()]);
	}
	
	private void setOverlay(File image, File target){
		
		try {
			BufferedImage img = ImageIO.read(image);
		
			BufferedImage overlay = ImageIO.read(new File("Images/ProductPictureOverlay.png"));
			BufferedImage combined = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
			
			Graphics g = combined.getGraphics();
			g.drawImage(img, 0, 0, null);
			g.drawImage(overlay, 0, img.getHeight() - overlay.getHeight(), null);
			
			ImageIO.write(combined, "JPG", target);
			
		} catch (IOException e) {
			System.err.println("Image Read Fail");
			e.printStackTrace();
		}
	}
	


	
	public String overlayAndUpload(Product p) {	
		
		String upPath = "";
		switch(p.getName()){
		case ProductCategory.DS_LITE: 
			upPath = "dsl/prd/"; break;
		case ProductCategory.DSI:
			upPath = "dsi/prd/"; break;
		case ProductCategory.DSI_XL:
			upPath = "dsx/prd/"; break;
		case ProductCategory.DS3:
			upPath = "3ds/prd/"; break;
		case ProductCategory.DS3_XL:
			upPath = "3dx/prd/"; break;
		}
		
		String tag = "" + p.getID();
		
		//Make Dir
		File dir = new File("ProductImageArchive/" + upPath + tag);
		dir.mkdir();
		
		String ret = "";
		//Overlay and place in new Dir
		for(int i=0; i<list.getSelectedIndices().length; i++){
			
			ret = ret.concat(String.format(",http://orthus.net/pi/%s%s%d.jpg", 
					upPath, tag, i+1));
			
			setOverlay(pics[list.getSelectedIndices()[i]], 
					new File(String.format("ProductImageArchive/%s%s/%s%d.jpg", 
							upPath, tag, tag, i+1)));
			
			pics[list.getSelectedIndices()[i]].delete();
		}
			
		//UploadFiles
		if(new FTPManager().upLoad("pi/" + upPath, dir))
			progress.setText("Upload Complete!");
		else
			progress.setText("Upload Failed!");
		
		//Reset UI
		list.setSelectedIndex(0);
		list.setListData(gatherNames());
		
		return ret.substring(1);
	}
	
	public String overlayAndUpload(Constituent c, Category cat) {	
		
		String upPath = "";
		switch(cat.getName()){
		case ProductCategory.DS_LITE: 
			upPath = "dsl/prt/"; break;
		case ProductCategory.DSI:
			upPath = "dsi/prt/"; break;
		case ProductCategory.DSI_XL:
			upPath = "dsx/prt/"; break;
		case ProductCategory.DS3:
			upPath = "3ds/prt/"; break;
		case ProductCategory.DS3_XL:
			upPath = "3dx/prt/"; break;
		}
				
		
		
		String tag = c.getName().replaceAll(" ", "") + c.getSerial();
		
		//Make Dir
		File dir = new File("ProductImageArchive/" + upPath + tag);
		dir.mkdir();
		
		String ret = "";
		//Overlay and place in new Dir
		for(int i=0; i<list.getSelectedIndices().length; i++){
			
			ret = ret.concat(String.format(",http://orthus.net/pi/%s%s%d.jpg", 
					upPath, tag, i+1));
			
			setOverlay(pics[list.getSelectedIndices()[i]], 
					new File(String.format("ProductImageArchive/%s%s/%s%d.jpg", 
							upPath, tag, tag, i+1)));
			
			pics[list.getSelectedIndices()[i]].delete();
		}
			
		//UploadFiles
		if(new FTPManager().upLoad("pi/" + upPath, dir))
			progress.setText("Upload Complete!");
		else
			progress.setText("Upload Failed!");
		
		//Reset UI
		list.setSelectedIndex(0);
		list.setListData(gatherNames());
		
		return ret.substring(1);
	}
	
public String overlayAndUpload() {	
		
		String upPath = "gen/";
		
		String tag = Date.getTodaysDate().getMonthString().substring(0, 2) + Date.getTodaysDate().getDateInSeconds();
		
		//Make Dir
		File dir = new File("ProductImageArchive/" + upPath + tag);
		dir.mkdir();
		
		String ret = "";
		//Overlay and place in new Dir
		for(int i=0; i<list.getSelectedIndices().length; i++){
			
			ret = ret.concat(String.format(",http://orthus.net/pi/%s%s%d.jpg", 
					upPath, tag, i+1));
			
			setOverlay(pics[list.getSelectedIndices()[i]], 
					new File(String.format("ProductImageArchive/%s%s/%s%d.jpg", 
							upPath, tag, tag, i+1)));
			
			pics[list.getSelectedIndices()[i]].delete();
		}
			
		//UploadFiles
		if(new FTPManager().upLoad("pi/" + upPath, dir))
			progress.setText("Upload Complete!");
		else
			progress.setText("Upload Failed!");
		
		//Reset UI
		list.setSelectedIndex(0);
		list.setListData(gatherNames());
		
		return ret.substring(1);
	}
	
	public void setProgress(String prog){
		progress.setText(prog);
	}
}
