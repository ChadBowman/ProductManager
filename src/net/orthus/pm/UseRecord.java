package net.orthus.pm;


public class UseRecord {

	private int id, quant;
	private Date date;
	
	public UseRecord(int id, int quant, Date date) {
		this.id = id;
		this.quant = quant;
		this.date = date;
	}
	
	public String record(){
		return String.format("#u%d&%d%s", id, quant, date.record());
	}
	
	public int getSerial(){
		return id;
	}
	
	public int getQuanity(){
		return quant;
	}
	
	public Date getDate(){
		return date;
	}
}
