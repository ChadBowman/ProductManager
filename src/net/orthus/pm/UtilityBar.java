package net.orthus.pm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JMenuBar;

public class UtilityBar	extends JMenuBar
				        implements ActionListener{

	private JLabel time;
	private Button stamp;
	private boolean working;
	private Date start;
	
	private JLabel label;
	
	public UtilityBar() {
		this.add(label = new JLabel("   "));
		this.add(Box.createHorizontalGlue());
		this.add(stamp = new Button("Clock In", this, 0));
		this.add(time = new JLabel("  - : - : -  "));
		this.add(new JLabel("     "));
		working = false;
	}
	
	public void setText(String text){ label.setText("     " + text); }

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(working){
			stamp.setText("Clock In");
			working = false;
			Database.onTheClock = false;
			
		}else{
			
			Thread clock = new Thread(){
				Date now;
				public void run(){
					while(working){
						now = Date.getTodaysDate();
						String timer = start.displayTimeDifference(now);
						time.setText("  " + timer + "  ");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					//Clocked Off
					time.setText("  - : - : -  ");
					Database.addTimeStamp(new TimeStamp(start, now));
					GUIEngine.refresh(0, -1);
				}
			};
			
			stamp.setText("Clock Out");
			working = true;
			Database.onTheClock = true;
			start = Date.getTodaysDate();
			clock.start();
		}
		
	}

}
