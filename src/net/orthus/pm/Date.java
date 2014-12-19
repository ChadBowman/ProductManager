package net.orthus.pm;

import java.util.GregorianCalendar;

public class Date implements Comparable<Date>{
	//----- Variables
	private int second, minute, hour, day, month, year, dayOfWeek;
	private GregorianCalendar calendar = new GregorianCalendar();
	
	//----- Constants
	//Months
	public static final int JANUARY = 1;
	public static final int FEBUARY = 2;
	public static final int MARCH = 3;
	public static final int APRIL = 4;
	public static final int MAY = 5;
	public static final int JUNE = 6;
	public static final int JULY = 7;
	public static final int AUGUST = 8;
	public static final int SEPTEMBER = 9;
	public static final int OCTOBER = 10;
	public static final int NOVEMBER = 11;
	public static final int DECEMBER = 12;
	
	//Days
	public static final String SUNDAY = "Sunday";
	public static final String MONDAY = "Monday";
	public static final String TUESDAY ="Tuesday";
	public static final String WEDNESDAY = "Wednesday";
	public static final String THURSDAY = "Thursday";
	public static final String FRIDAY = "Friday";
	public static final String SATURDAY = "Saturday";
	
	//----- Constructors
	//Grabs most recent Date
	public Date(){
		second = calendar.get(GregorianCalendar.SECOND);
		minute = calendar.get(GregorianCalendar.MINUTE);
		hour = calendar.get(GregorianCalendar.HOUR_OF_DAY);
		day = calendar.get(GregorianCalendar.DAY_OF_MONTH);
		month = calendar.get(GregorianCalendar.MONTH) + 1;
		year = calendar.get(GregorianCalendar.YEAR) - 2000;
		dayOfWeek = calendar.get(GregorianCalendar.DAY_OF_WEEK);
	}
	
	//FULL Constructor
	public Date(int second, int minute, int hour, int day, int month, int year, int dayOfWeek){
			this.second = second;
			this.minute = minute;
			this.hour = hour;
			this.day = day;
			this.month = month;
			this.year = year;
			this.dayOfWeek = dayOfWeek;
	}
	
	//Needs to be protected
	public Date(String input){
		String[] split = input.split("\\.");
		this.day = Integer.parseInt(split[0]);
		this.month = Integer.parseInt(split[1]);
		this.year = Integer.parseInt(split[2]);
	}
	
	//Clone
	public Date(Date copy){
		try{
			this.second = new Integer(copy.getSecond());
			this.minute = new Integer(copy.getMinute());
			this.hour = new Integer(copy.getHour());
			this.day = new Integer(copy.getDay());
			this.month = new Integer(copy.getMonth());
			this.year = new Integer(copy.getYear());
			this.dayOfWeek = new Integer(copy.getDayOfWeek());
		}catch(NullPointerException e){}
	}
	
	//----- Standard Methods
	//Getters
	public int getSecond(){ return second; }
	public int getMinute(){ return minute; }
	public int getHour(){ return hour; }
	public int getDay(){ return day; }
	public int getDayOfWeek(){ return dayOfWeek; }
	public int getMonth(){ return month; }
	public int getYear(){ return year; }
	
	//----- Advanced Methods
	public boolean isLeapYear(){
		if(year % 4 == 0) return true;
		return false;
		
		//TODO returns true for 2100
	}
	
	public String getMonthString(){
		switch(month){
		case 1: return "January";
		case 2: return "Febuary";
		case 3: return "March";
		case 4: return "April";
		case 5: return "May";
		case 6: return "June";
		case 7: return "July";
		case 8: return "August";
		case 9: return "September";
		case 10: return "October";
		case 11: return "November";
		case 12: return "December";
		default: return "Invalid";
		}
	}
	
	public String getDayOfTheWeek(){
		switch(dayOfWeek){
		case 1: return SUNDAY;
		case 2: return MONDAY;
		case 3: return TUESDAY;
		case 4: return WEDNESDAY;
		case 5: return THURSDAY;
		case 6: return FRIDAY;
		case 7: return SATURDAY;
		default: return "Invalid";
		}
	}
	
	public int daysAgo(){
		return getTodaysDate().getDateInDays() - this.getDateInDays();
	}
	
	public Date addDays(int days){
		
		int dy = new Integer(day);
		int mn = new Integer(month);
		int yr = new Integer(year);
		int dw = new Integer(dayOfWeek);
		
		for(int i=0; i<days; i++){
			if(mn == 1 || mn == 3 || mn == 5 || mn == 7 
					|| mn == 8 || mn == 10){
				if(dy == 31){ //If last day of the month
					mn++;
					dy = 0;
				}
			}else if(mn == 4 || mn == 6 || mn == 9 || mn == 11){
				if(dy == 30){
					mn++;
					dy = 0;
				}
			}else if(mn == 2){
				if(isLeapYear() && dy == 29){
					mn++;
					dy = 0;
				}else if(!isLeapYear() && dy == 28){
					mn++;
					dy = 0;
				}
			}else if(dy == 31){ //Month is Dec.
				yr++;
				mn = 1;
				dy = 0;
			}
			
			dw = (dw == 7)? 1 : dw + 1;
			
			dy++; //Add day
		}
		
		return new Date(second, minute, hour, dy, mn, yr, dw);
	}
	
	public int getDateInDays(){
		
		int newDay = new Integer(day);
		if(year > 13)
			newDay += 365 * (year - 13);
		//TODO add past leap years
		
		if(month > 2 && isLeapYear()) 
			newDay++;
		
		switch(month){
		case 1: return newDay;
		case 2: return 31 + newDay;
		case 3: return 59 + newDay;
		case 4: return 89 + newDay;
		case 5: return 119 + newDay;
		case 6: return 150 + newDay;
		case 7: return 180 + newDay;
		case 8: return 211 + newDay;
		case 9: return 242 + newDay;
		case 10: return 272 + newDay;
		case 11: return 303 + newDay;
		case 12: return 333 + newDay;
		default: return newDay;
		}
	}
	
	public int dayOfYear(){
		int ret = getDateInDays();
		if(ret > 365)
			ret -= 365;
		
		return ret;
	}
	
	public boolean isUSPSHoliday(){
		
		switch(month){
		case 1: if(day == 20) return true; break;
		case 2: if(day == 17) return true; break;
		case 4: if(day == 26) return true; break;
		case 7: if(day == 4) return true; break;
		case 9: if(day == 1) return true; break;
		case 10: if(day == 13) return true; break;
		case 11: if(day == 11) return true; 
				 if(day == 27) return true; break;
		case 12: if(day == 25) return true; break;
		}
		
		return false;
	}

	public String displaySimpleDate(){
		if(day < 10 && month < 10)
			return String.format("0%d.0%d.%d", day, month, year);
		if(day < 10)
			return String.format("0%d.%d.%d", day, month, year);
		if(month < 10)
			return String.format("%d.0%d.%d", day, month, year);
		else
			return String.format("%d.%d.%d", day, month, year);
	}
	
	public String displayDate(){
		return String.format("%s at %d:%d",  displaySimpleDate(), hour, minute);
	}
	
	public String displayTime(){
		return String.format("%d:%d:%d", hour, minute, second);
	}
	
	public String displayTimeDifference(Date d){
		
		//TODO Handle changes in day
		long diff = d.getDateInSeconds() - this.getDateInSeconds(); 
		
		int day = (int) ((diff / 86400));
		int hor = (int) ((diff / 3600) % 24);
		int min = (int) ((diff / 60) % 60);
		int sec = (int) (diff % 60);
		
		String s = (day != 0)? "" + day + " " : "";
		
		return String.format("%s%d:%d:%d", 
				s, hor, min, sec);
	}
	
	public long getDateInSeconds(){

		long seconds = this.getDateInDays() * 24 * 3600;
		seconds += hour * 3600;
		seconds += minute * 60;
		seconds += second;
		
		return seconds;
	}
	
	public String record(){
		return String.format("#d%d&%d&%d&%d&%d&%d&%d", second, minute, hour, day, month, year, dayOfWeek);
	} 
	
	//Static
	public static Date getTodaysDate(){ return new Date(); }
	
	//----- Interfaces
	public int compareTo(Date o) {
		if(year != o.year)
			return year - o.year;
		else if(month != o.month)
			return month - o.month;
		else if(day != o.day)
			return(day - o.day);
		else if(hour != o.hour)
			return(hour - o.hour);
		else if(minute != o.minute)
			return minute - o.minute;
		else 
			return second - o.second;
	}

	
	
}
