package net.orthus.pm;


public class Credit implements Comparable<Credit>{
	//----- Variables
	int value;
	
	//----- Constructors
	public Credit(){ this.value = 0; }
	public Credit(int value) { this.value = value; }
	public Credit(double value){ this.value = convert(value); }
	public Credit(String value){
		if(value == null)
			this.setValue(0);
		else
			try{
				this.setValue(Double.parseDouble(value));
			}catch(NumberFormatException e){
				OptionPane.showError(OptionPane.CREDIT_FORMAT);
			}
	}
	
	//Clone
	public Credit(Credit value){ 
		try{
			this.value = new Integer(value.getValueInCents());
		}catch(NullPointerException e){}
	}
	
	//----- Standard Methods
	//Getters
	public int getValueInCents(){ return value; }
	public double getValueInDollars(){ return (double) value / 100.0; }
	
	//Setters
	public void setValue(int value){ this.value = value; }
	public void setValue(double value){ this.value = convert(value); }
	
	//----- Advanced Methods
	public String record(){ return "#c" + value; }
	
	//MULTIPLY
	public void multiply(int x){ value *= x; }
	
	public void multiply(double multi){
		try{
			String str = String.format("%.2f", value * multi / 100.0);
			this.value = Integer.parseInt(str.replaceAll("\\.", ""));
		}catch(NumberFormatException e){
			System.err.println("Number format error in Credit string converstion!");
		}
	}
	public void multiply(Credit cred){
		double x = (cred == null)? 0 : cred.getValueInDollars();
		multiply(x); 
	}
	
	//DIVIDE
	public void divide(int x){
		if(x == 0) return;
		value /= x;
	}
	
	public void divide(double divi){ 
		if(divi == 0) return;
		try{
			String str = String.format("%.2f", value / divi / 100.0);
			this.value = Integer.parseInt(str.replaceAll("\\.", ""));
		}catch(NumberFormatException e){
			System.err.println("Number format error in Credit string converstion!");
		}
	}
	public void divide(Credit cred){
		double x = (cred == null)? 0 : cred.getValueInDollars();
		divide(x); 
	}
	
	//ADD
	public void add(double val){ this.value += convert(val); }
	public void add(int val){ this.value += val; }
	public void add(Credit cred){
		int x = (cred == null)? 0 : cred.getValueInCents();
		value += x; 
	}
	
	//SUBTRACT
	public void subtract(double val){ this.value -= convert(val); }
	public void subtract(int val){ this.value -= val; }
	public void subtract(Credit cred){ 
		int x = (cred == null)? 0 : cred.getValueInCents();
		value -= x; 
	}
	
	private int convert(double val){
		try{
			String str = String.format("%.2f", val);
			return Integer.parseInt(str.replaceAll("\\.", ""));
		}catch(NumberFormatException e){
			System.err.println("Number format error in Credit string converstion!");
			return 0;
		}
	}
	
	//----- Interfaces
	public String toString(){ return String.format("$%.2f", getValueInDollars()); }
	public int compareTo(Credit another){ return value - another.getValueInCents(); }
	
}
