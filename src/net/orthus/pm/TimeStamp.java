package net.orthus.pm;


public class TimeStamp {

	//Instance Variables
	private Date timeOn;
	private Date timeOff;
	
	public TimeStamp(Date timeOn, Date timeOff){
		this.timeOn = timeOn;
		this.timeOff = timeOff;
	}
	
	public String record(){
		return String.format("#b%s%s", timeOn.record(), timeOff.record());
	}
	
	public long getTimeWorkedSeconds(){
		
		return timeOff.getDateInSeconds() - timeOn.getDateInSeconds();
	}
	
	public double getTimeWorkedHours(){
		
		long sec = getTimeWorkedSeconds();
		
		return (double) (sec / 3600.0);
		
	}
	
	public Date getStart(){
		return timeOn;
	}
	
	public Date getEnd(){
		return timeOff;
	}
	
}
