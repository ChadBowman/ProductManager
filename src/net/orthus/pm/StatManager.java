package net.orthus.pm;

import net.orthus.util.Sorter;


public class StatManager {
	private Date today;
	private String week, month, year,
					products, pts, csh;
	
	private String[] worked;;
	
	private Credit weekR, monthR, yearR, limit;
	
	private int incP, invP, stkP, lstP, sldP, retP,
	  			incR, penR, comR, retR;
	
	public static final int WEEK = 0;
	public static final int MONTH = 1;
	public static final int YEAR = 2;

	public StatManager() {
		today = Date.getTodaysDate();
		worked = new String[3];
		weekR = new Credit();
		monthR = new Credit();
		yearR = new Credit();
		setStatus();
	}
	
	//Summary Statistics
	public String incProduct(){ return "" + incP; }
	public String invProduct(){ return "" + invP; }
	public String stkProduct(){ return "" + stkP; }
	public String lstProduct(){ return "" + lstP; }
	public String sldProduct(){ return "" + sldP; }
	public String retProduct(){ return "" + retP; }
	public String incRepair(){ return "" + incR; }
	public String penRepair(){ return "" + penR; }
	public String comRepair(){ return "" + comR; }
	public String retRepair(){ return "" + retR; }
	
	private void setStatus(){
		
		ProductCategory[] pro = Database.getProductCategories();
		
		if(pro != null)
			for(int i=0; i<pro.length; i++)
				if(pro[i].getProducts() != null)
					for(int j=0; j<pro[i].getProducts().length; j++)
						switch(pro[i].getProducts()[j].getStatus()){
						case Product.INCOMING:  incP++; break;
						case Product.INVENTORY: invP++; break;
						case Product.STOCK:	    stkP++; break;
						case Product.LISTED:    lstP++; break;
						case Product.SOLD:      sldP++; break;
						case Product.RETURNED:  retP++; break;
						}
		
		ServiceCategory[] serv = Database.getAllServiceCategories();
		
		if(serv != null)
			for(int i=0; i<serv.length; i++)
				if(serv[i].getAllRepairs() != null)
					for(int j=0; j< serv[i].getAllRepairs().length; j++)
						switch(serv[i].getAllRepairs()[j].getStatus()){
						case Repair.INCOMING: incR++; break;
						case Repair.PENDING:  penR++; break;
						case Repair.COMPLETE: comR++; break;
						case Repair.RETURNED: retR++; break;
						}
			
	}
	
	public Credit getCurrentIncome(){
		Credit ret = Database.getUndeliveredIncome();
		
		if(ret == null) ret = new Credit();
		
		return ret;
	}
	
	
	public Credit getProfit(int type){
		Credit ret = new Credit();
		Credit cost = new Credit();
	
		
		ProductCategory[] prod = Database.getProductCategories();
		
		if(prod != null) //Add all Product profits
			for(int i=0; i<prod.length; i++)
				if(prod[i].getProductsByStatus(Product.SOLD) != null){
					Product[] p = prod[i].getProductsByStatus(Product.SOLD);
					
					for(int j=0; j<p.length; j++)
						switch(type){
						case WEEK:
							if(p[j].getDateSold().daysAgo() < 7){
								ret.add(p[j].getProfit());
								cost.add(p[j].getUltimateCost());
								weekR.add(p[j].getSellAmount().getValueInDollars() 
										+ p[j].getShippingPaid().getValueInDollars());
							} break;
						case MONTH:
							if(p[j].getDateSold().getMonth() == today.getMonth()
									&& p[j].getDateSold().getYear() == today.getYear()){
								ret.add(p[j].getProfit());
								cost.add(p[j].getUltimateCost());
								monthR.add(p[j].getSellAmount().getValueInDollars() 
										+ p[j].getShippingPaid().getValueInDollars());
							} break;
						case YEAR:
							if(p[j].getDateSold().getYear() == today.getYear()){
								ret.add(p[j].getProfit());
								cost.add(p[j].getUltimateCost());
								yearR.add(p[j].getSellAmount().getValueInDollars() 
										+ p[j].getShippingPaid().getValueInDollars());
							}
						}
					
					
				}
		
		
		ServiceCategory[] serv = Database.getAllServiceCategories();
		
		if(serv != null)
			for(int i=0; i<serv.length; i++){
				if(serv[i].getAllRepairsByStatus(Repair.COMPLETE) != null){
					Repair[] r = serv[i].getAllRepairsByStatus(Repair.COMPLETE);
					
					for(int j=0; j<r.length; j++) //Add all Service profits
						
						if(r[j].getDateRepaired() != null)
							switch(type){
							case WEEK:
								if(r[j].getDateRepaired().daysAgo() < 7){
									ret.add(r[j].getProfit());
									cost.add(r[j].getUltimateCost());
									weekR.add(r[j].getSellAmount().getValueInDollars() 
											+ r[j].getShippingPaid().getValueInDollars());
								}break;
								
							case MONTH:
								if(r[j].getDateRepaired().getMonth() == today.getMonth()
										&& r[j].getDateRepaired().getYear() == today.getYear()){
									ret.add(r[j].getProfit());
									cost.add(r[j].getUltimateCost());
									monthR.add(r[j].getSellAmount().getValueInDollars() 
											+ r[j].getShippingPaid().getValueInDollars());
								} break;
								
							case YEAR:
								if(r[j].getDateRepaired().getYear() == today.getYear()){
									ret.add(r[j].getProfit());
									cost.add(r[j].getUltimateCost());
									yearR.add(r[j].getSellAmount().getValueInDollars() 
											+ r[j].getShippingPaid().getValueInDollars());
								}
							}
				}
				
				if(serv[i].getSupplyConstituentsByStatus(Constituent.SOLD) != null){
					Constituent[] c = serv[i].getSupplyConstituentsByStatus(Constituent.SOLD);
					
					for(int j=0; j<c.length; j++) //Add all Part profits
						switch(type){
						case WEEK:
							if(c[j].getDateSold().daysAgo() < 7){
								ret.add(c[j].getProfit());
								cost.add(c[j].getUltimateCost());
								weekR.add(c[j].getListPrice().getValueInDollars()
										+ c[j].getShippingPaid().getValueInDollars());
							} break;
						case MONTH:
							if(c[j].getDateSold().getMonth() == today.getMonth()
									&& c[j].getDateSold().getYear() == today.getYear()){
								ret.add(c[j].getProfit());
								cost.add(c[j].getUltimateCost());
								monthR.add(c[j].getListPrice().getValueInDollars()
										+ c[j].getShippingPaid().getValueInDollars());
							} break;
						case YEAR:
							if(c[j].getDateSold().getYear() == today.getYear()){
								ret.add(c[j].getProfit());
								cost.add(c[j].getUltimateCost());
								yearR.add(c[j].getListPrice().getValueInDollars()
										+ c[j].getShippingPaid().getValueInDollars());
							}
						}
				}
			}

		switch(type){
		case WEEK: 
			week = String.format("%.0f", 100 * ret.getValueInDollars() / cost.getValueInDollars()); break;
			
		case MONTH: 
			month = String.format("%.0f", 100 * ret.getValueInDollars() / cost.getValueInDollars()); break;
			
		case YEAR:
			year = String.format("%.0f", 100 * ret.getValueInDollars() / cost.getValueInDollars());
		}
		
		return ret;
	}
	
	public String getWeekPercentage(){
		return week;
	}
	
	public String getMonthPercentage(){
		return month;
	}
	
	public String getYearPercentage(){
		return year;
	}
	
	public Credit getWeekRevenue(){
		return weekR;
	}
	
	public Credit getMonthRevenue(){
		return monthR;
	}
	
	public Credit getYearRevenue(){
		return yearR;
	}
	
	public Credit getTotalInvestment(){
		Credit total = new Credit();
		Credit cash = new Credit();
		Credit complete = new Credit();
		Credit parts = new Credit();
		
		ProductCategory[] pro = Database.getProductCategories();
		
		if(pro != null)
			for(int i=0; i<pro.length; i++){ //For all productCategories
				
						//First Add all complete products
				if(pro[i].getProductsByStatus(Product.STOCK) != null){
					Product[] p = pro[i].getProductsByStatus(Product.STOCK);
					
					for(int j=0; j<p.length; j++){ 
						total.add(p[j].getAssembly().getTotalCost());
						complete.add(p[j].getAssembly().getTotalCost());
					}
				}
				if(pro[i].getProductsByStatus(Product.LISTED) != null){
					Product[] p = pro[i].getProductsByStatus(Product.LISTED);
					
					for(int j=0; j<p.length; j++){ 
						total.add(p[j].getAssembly().getTotalCost());
						complete.add(p[j].getAssembly().getTotalCost());
					}
				}
				if(pro[i].getProductsByStatus(Product.RETURNED) != null){
					Product[] p = pro[i].getProductsByStatus(Product.RETURNED);
					
					for(int j=0; j<p.length; j++){ 
						total.add(p[j].getAssembly().getTotalCost());
						complete.add(p[j].getAssembly().getTotalCost());
					}
				}
				
					//Then add all partial products
				if(pro[i].getProductsByStatus(Product.INCOMING) != null){
					Product[] p = pro[i].getProductsByStatus(Product.INCOMING);
					
					for(int j=0; j<p.length; j++){ 
						total.add(p[j].getAssembly().getTotalCost());
						parts.add(p[j].getAssembly().getTotalCost());
					}
				}
				if(pro[i].getProductsByStatus(Product.INVENTORY) != null){
					Product[] p = pro[i].getProductsByStatus(Product.INVENTORY);
					
					for(int j=0; j<p.length; j++){ 
						total.add(p[j].getAssembly().getTotalCost());
						parts.add(p[j].getAssembly().getTotalCost());
					}
				}
				
			}
		
		ServiceCategory[] serv = Database.getAllServiceCategories();
		
		if(serv != null)
			for(int i=0; i<serv.length; i++) 
				if(serv[i].getPartSupply() != null){ //Add all part supplies
					total.add(serv[i].getPartSupply().getTotalCost());
					parts.add(serv[i].getPartSupply().getTotalCost());
				}
		
		//TODO add items in repairs?
		
		int del = (Database.getUndeliveredIncome() == null)? 0 
				: Database.getUndeliveredIncome().getValueInCents();
		del += (Database.getEbayBalance(Date.getTodaysDate().getMonth()) == null)?
				0 : Database.getEbayBalance(Date.getTodaysDate().getMonth()).getValueInCents();
		
		if(Database.getPayPalBalance() != null){
			Credit net = new Credit(Database.getPayPalBalance().getValueInCents()
					- del);
			
			total.add(net);
			cash.add(net);
		}
		
		
		products = String.format("%.0f%%", 100 * complete.getValueInDollars() / total.getValueInDollars());
		pts = String.format("%.0f%%", 100 * parts.getValueInDollars() / total.getValueInDollars());
		csh = String.format("%.0f%%", 100 * cash.getValueInDollars() / total.getValueInDollars());
		
		Database.setTotalInvestment(total);
		
		return total;
	}
	
	public String getProductProportion(){
		return products;
	}
	
	public String getPartProportion(){
		return pts;
	}
	
	public String getCashProportion(){
		return csh;
	}
	
	public int getNumberOnTheMarket(){
		
		int month = Date.getTodaysDate().getMonth();
		int listed = 0;
		limit = new Credit();
		
		
		ProductCategory[] c = Database.getProductCategories();
		
		if(c != null)
			for(int i=0; i<c.length; i++){
				
				Product[] p1 = c[i].getProductsByStatus(Product.LISTED);
				Product[] p2 = c[i].getProductsByStatus(Product.SOLD);
				
				if(p1 != null)
					for(int j=0; j<p1.length; j++){ //Add all products currently listed
						listed++;
						limit.add(p1[j].getListAmount());
					}
						
							
				
				if(p2 != null)
					for(int j=0; j<p2.length; j++) //Add all products sold this month
						if(p2[j].getDateSold().getMonth() == month){
							listed++;
							limit.add(p2[j].getSellAmount());
						}
			}
		
		ServiceCategory[] s = Database.getAllServiceCategories();
		
		
		if(s != null)
			for(int i=0; i<s.length; i++){
				
				Service[] v = s[i].getServices();
				Repair[] r = s[i].getAllRepairs();
				Constituent[] auct = s[i].getSupplyConstituentsByStatus(Constituent.LISTED_AUCTION);
				Constituent[] fix1 = s[i].getSupplyConstituentsByStatus(Constituent.LISTED_FIXED);
				Constituent[] fix2 = s[i].getSupplyConstituentsByStatus(Constituent.LISTED_FIXED_AUTO);
				Constituent[] sld = s[i].getSupplyConstituentsByStatus(Constituent.SOLD);
				
				
				if(v != null)
					for(int j=0; j<v.length; j++) //Add quantities of all service listings
						if(v[j].getListType().equals(Item.FIXED)){
							listed += v[j].getQuantity();
							limit.add(v[j].getListAmount().getValueInDollars() * v[j].getQuantity());
							
						}
				
				
				
				if(r != null)
					for(int j=0; j<r.length; j++){ //Add repairs sold this month
						if(r[j].getDateSold().getMonth() == month)
							listed ++;
							limit.add(r[j].getSellAmount());
					}
				
				
				
				if(auct != null) //Add all part lots being auctioned
					for(int j=0; j<auct.length; j++){
						listed++;
						limit.add(auct[j].getListPrice());
					}
				
				
						
				if(fix1 != null)
					for(int j=0; j<fix1.length; j++){
						listed += fix1[j].getQuantity();
						limit.add(fix1[j].getListPrice().getValueInDollars() * fix1[j].getQuantity());
					}
				
				
				
				if(fix2 != null)
					for(int j=0; j<fix2.length; j++){
						listed += fix2[j].getQuantity();
						limit.add(fix2[j].getListPrice().getValueInDollars() * fix2[j].getQuantity());
					}
				
				
				if(sld != null)
					for(int j=0; j<sld.length; j++)
						if(sld[j].getDateSold().getMonth() == month){
							listed += sld[j].getQuantity();
							limit.add(sld[j].getListPrice());
						}
			
			}
	
		return listed;
	}
	
	
	public Credit getAmountOnTheMarket(){
		return limit;
	}
	
	public double[] getHourlyWages(){
		double[] ret = new double[3];
		long[] data = new long[4];
		
		TimeStamp[] ts = Database.getTimeStamps();
		if(ts == null)
			return ret;
		
		
		//Add all time worked
		for(int i=0; i<ts.length; i++){
			if(ts[i].getStart().getYear() == today.getYear() 
					&& ts[i].getStart().getMonth() == today.getMonth() 
					&& ts[i].getStart().getDay() == today.getDay())
				data[3] += ts[i].getTimeWorkedSeconds();
			
			if(ts[i].getStart().daysAgo() < 7)
				data[0] += ts[i].getTimeWorkedSeconds();
			
			if(ts[i].getStart().getYear() == today.getYear() 
					&& ts[i].getStart().getMonth() == today.getMonth())
				data[1] += ts[i].getTimeWorkedSeconds();
			
			
			if(ts[i].getStart().getYear() == today.getYear())
				data[2] += ts[i].getTimeWorkedSeconds();
		}
		
		worked[0] = String.format("%.2f", data[3] / 3600.0); //Today
		worked[1] = String.format("%.2f", data[0] / 3600.0); //Week
		worked[2] = String.format("%.2f", data[1] / 3600.0); //Month
		
		ret[0] = getProfit(WEEK).getValueInDollars() / (data[0] / 3600.0);
		ret[1] = getProfit(MONTH).getValueInDollars() / (data[1] / 3600.0);
		ret[2] = getProfit(YEAR).getValueInDollars() / (data[2] / 3600.0);
		
		return ret;
	}
	
	public String[] getTimeWorked(){
		return worked;
	}
	
	
	//Gather average product market lifetimes 
	//Compare each category
	//Compare each quality
	public String absorbtionSummary(ProductCategory cat){
		
		Product[] sold = cat.getProductsByStatus(Product.SOLD);
		
		if(sold == null) return "";
		
		//Total averages
		double totalTime = 0;
		double totalRev = 0;
		
		Attribute[] colors = new Attribute[cat.getColors().length];
		Attribute[] qual = new Attribute[5];
		
		for(int i=0; i<sold.length; i++){
			totalTime += sold[i].getDaysOnMarket();
			totalRev += sold[i].getSellAmount().getValueInDollars();
		}
		
		//Stats by color
		for(int i=0; i<cat.getColors().length; i++){
			
			double time = 0;
			double prof = 0;
			int counter = 0;
			
			for(int j=0; j<sold.length; j++){
				if(sold[j].getOverallColor().equals(cat.getColors()[i])){
					time += sold[j].getDaysOnMarket();
					prof += sold[j].getSellAmount().getValueInDollars();
					counter++;
				}
			}
			
			if(counter == 0)
				colors[i] = new Attribute("NaN", 1.0, 1.0);
			else
				colors[i] = new Attribute(cat.getColors()[i], time / counter, prof / counter);
		}
		
		
		//Stats by quality
		double ad = 0, bd = 0, cd = 0, dd = 0, fd = 0; //DOM totals
		double ap = 0, bp = 0, cp = 0, dp = 0, fp = 0; //Profit totals
		int ac = 0, bc = 0, cc = 0, dc = 0, fc = 0;    //Counters
		
		for(int i=0; i<sold.length; i++){
		
			if(sold[i].getOverallQuality().equals(Part.A_PLUS)
					|| sold[i].getOverallQuality().equals(Part.A)
					|| sold[i].getOverallQuality().equals(Part.A_MINUS)){
				
				ad += sold[i].getDaysOnMarket();
				ap += sold[i].getProfit().getValueInDollars();
				ac++;
			}else if(sold[i].getOverallQuality().equals(Part.B_PLUS)
					|| sold[i].getOverallQuality().equals(Part.B)
					|| sold[i].getOverallQuality().equals(Part.B_MINUS)){
				
				bd += sold[i].getDaysOnMarket();
				bp += sold[i].getProfit().getValueInDollars();
				bc++;
			}else if(sold[i].getOverallQuality().equals(Part.C_PLUS)
					|| sold[i].getOverallQuality().equals(Part.C)
					|| sold[i].getOverallQuality().equals(Part.C_MINUS)){
				
				cd += sold[i].getDaysOnMarket();
				cp += sold[i].getProfit().getValueInDollars();
				cc++;
			}else if(sold[i].getOverallQuality().equals(Part.D_PLUS)
					|| sold[i].getOverallQuality().equals(Part.D)
					|| sold[i].getOverallQuality().equals(Part.D_MINUS)){
				
				dd += sold[i].getDaysOnMarket();
				dp += sold[i].getProfit().getValueInDollars();
				dc++;
			}else{
				fd += sold[i].getDaysOnMarket();
				fp += sold[i].getProfit().getValueInDollars();
				fc++;
			}

		}
		
		
		qual[0] = (ac == 0)? //A
				new Attribute("NaN", 1.0, 1.0)
				: new Attribute("A", ad / ac, ap / ac);
				
		qual[1] = (bc == 0)? //B
				new Attribute("NaN", 1.0, 1.0)
				: new Attribute("B", bd / bc, bp / bc);
						
		qual[2] = (cc == 0)? //C
				new Attribute("NaN", 1.0, 1.0)
				: new Attribute("C", cd / cc, cp / cc);
				
		qual[3] = (dc == 0)? //D
				new Attribute("NaN", 1.0, 1.0)
				: new Attribute("D", dd / dc, dp / dc);
						
		qual[4] = (fc == 0)? //F
				new Attribute("NaN", 1.0, 1.0)
				: new Attribute("F", fd / fc, fp / fc);
		
		colors =  new Sorter<Attribute>().quickSort(colors);
		qual = new Sorter<Attribute>().quickSort(qual);
		
		String clrs = "";
		for(int i=0; i<colors.length; i++)
			if(!colors[i].getVals().contains("NaN"))
				clrs = clrs.concat(colors[i].getVals() + "\n");
		
		String quls = "";
		for(int i=0; i < qual.length; i++)
			if(!qual[i].getVals().contains("NaN"))
				quls = quls.concat(qual[i].getVals() + "\n");
		
		String ret = String.format("%-15s%s%n%-15s$%.2f%n%s%n%-15s%s%n%s",
				"Distinction",
				"Revenue/Day",
				"Average",
				30 / (totalTime / sold.length) * 
				(totalRev / sold.length) / 30, 
				clrs,
				"",
				"Profit/Day",
				quls);
		
	 return ret;
	}
	
	class Attribute implements Comparable<Attribute>{

		public String name;
		public double av;
		
		public Attribute(String name, double dom, double prof){
			this.name = name;
			
			//A month divided by the average Days On Market
			//Multiplied by the average revenue, then divided by a month
			//To get a final value of Revenue/day
			av = 30 / dom * prof / 30;
		}
		
		public String getVals(){
			if(name.length() > 14)
				name = name.substring(0, 14);
			return String.format("%-15s$%.2f", name, av);
		}
		
		@Override
		public int compareTo(Attribute o) {
			if(this.av - o.av < 0)
				return 1;
			else if(this.av - o.av > 0)
				return -1;
			else
				return 0;
		}
		
	}

	//Service Statistics
	
	//Part Statistics
}
