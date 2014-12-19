package net.orthus.pm;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;

public class Unlock extends ActionFrame 
  					implements ActionListener {

	//----- Variables
	private static Unlock frame;
	private ComboBox<String> ops;
	private TextField num;
	private Label result;
	
	//----- Constructor
	public Unlock() {
		super("Unlock Parental Control", 200, 300, new GridLayout(0,1));
		
		String[] opts = {"DSi", "3DS"};
		
		center.add(ops = new ComboBox<String>(null, opts, null, -1, "Device Type"));
		center.add(new Label(Date.getTodaysDate().displaySimpleDate(), "Set DS's Date To:", SwingConstants.CENTER));
		center.add(num = new TextField("Confirmation Number"));
		center.add(result = new Label("", "Unlock Code", SwingConstants.CENTER));
		bottom.add(new Button("Submit", this, 0));
		
	}
	
	//----- Methods
	public static void initalizeFrame(){
		frame = new Unlock();
		frame.setVisible(true);
	}
	
	//DSI Unlock
	private String getDSiCode(){
		
		String full = Date.getTodaysDate().displaySimpleDate().substring(3, 5)
				+ Date.getTodaysDate().displaySimpleDate().substring(0, 2)
				+ num.getText().substring(4);
		
	
		long[] table = new long[256];
		
		for(int i=0; i<256; i++){
			long crc = i;
			for(int j=0; j<8; j++){
				if((crc & 1) == 1)
					crc = (crc >> 1) ^ 0xEDB88320l;
				else
					crc >>= 1;
			}
			table[i] = crc;
		}
		
		long crc = 0xffffffffl;
		int count = full.length();
		
		for(int i=0; count != 0; i++){
			count -= 1;
			long temp1 = (crc >> 8) & 0xFFFFFF;
			long temp2 = table[(int) ((crc ^ full.charAt(i)) & 0xFF)];
			crc = temp1 ^ temp2;
		}

		
		crc = ((crc ^ 0xaaaa) + 0x14c1) % 100000;
	
		String res = String.format("%05d", crc);
		
		return res;
	}
	
	private String getDS3Code(){
		
		String full = Date.getTodaysDate().displaySimpleDate().substring(3, 5)
				+ Date.getTodaysDate().displaySimpleDate().substring(0, 2)
				+ num.getText().substring(4) + " ";
		
        int[] table = new int[0x100];

        for (int i = 0; i < 0x100; i++) {
            int data = i;

            for (int j = 0; j < 4; j++) {
                if ((data & 1) != 0)
                    data = 0xEDBA6320 ^ (data >> 1);
                else
                    data >>= 1;

                if ((data & 1) != 0)
                    data = 0xEDBA6320 ^ (data >> 1);
                else
                    data = data >> 1;

            }
            table[i] = data;
        }
    


        int y = 0xFFFFFFFF;
        char x = full.charAt(0);

        for (int i = 0; i < 4; i++) {
            x = (char) (x ^ y);
            x = (char)(x & 0xFF);
            y = table[x] ^ (y >> 8);
            x = (char) (full.charAt(1 + i*2) ^ y);
            x = (char) (x & 0xFF);
            y = table[x] ^ (y >> 8);
            x = full.charAt(2 + i*2);
        }

        /*
        y ^= 0xAAAA;
        y += 0x1657;

        long yll = y;
        yll = (yll + 1)*0xA7C5AC47;
        int yhi = (int) (yll >> 48);
        yhi *= 0xFFFFF3CB;
        y += (yhi << 5);
		*/
        
        y = ((y ^ 0xaaaa) + 0x1657) % 100000;
		
		return "" + y;
	}
	
	
	//----- Interfaces
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(FormatChecker.quantityCheck(num.getText())
				&& num.getText().length() == 8){
			
			if(ops.getSelectedIndex() == 0)
				result.setText(getDSiCode());
			else
				result.setText(getDS3Code());
		
		}
	}

}
