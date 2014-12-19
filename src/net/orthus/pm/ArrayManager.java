package net.orthus.pm;


public class ArrayManager {
	
	//Part Actions
	public static Part[] addToArray(Part toAdd, Part[] array){
		if(array == null){
			array = new Part[1];
			array[0] = toAdd;
			return array;
		}else{
			Part[] temp = new Part[array.length + 1];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}
	
	public static Part[] addToStack(Part toAdd, Part[] array){
		if(array == null){
			array = new Part[1];
			array[0] = toAdd;
			return array;
		}else{
			Part[] temp = new Part[array.length + 1];
			for(int i=1; i<array.length + 1; i++)
				temp[i] = array[i-1];
			temp[0] = toAdd;
			return temp;
		}
	}
	
	public static Part[] addToArray(Part[] toAdd, Part[] array){
		if(array == null) return toAdd;
		if(toAdd == null) return array;
		
		Part[] temp = new Part[array.length + toAdd.length];
		int i;
		for(i=0; i<array.length; i++)
			temp[i] = array[i];
		for(int j=0; j<toAdd.length; i++, j++)
			temp[i] = toAdd[j];
		
		return temp;
	}
	
	public static Part[] removeFromArray(Part toRemove, Part[] array){
		if(array == null)
			return array;
		
		int index = -1;
		for(int i=0; i<array.length; i++)		//Find index of Part to remove
			if(array[i].equiv(toRemove))
				index = i;
		
		if(index == -1){
			System.err.println("Part to remove from array was not found!");
			return array;
		}
		
		Part[] temp = new Part[array.length - 1];
		for(int i=0; i<temp.length; i++)			//Create new array with omitted Part
			if(i < index)
				temp[i] = array[i];
			else
				temp[i] = array[i + 1];
		
		return temp;
	}
	
	//Product Actions
	public static Product[] addToArray(Product toAdd, Product[] array){
		if(array == null){
			array = new Product[1];
			array[0] = toAdd;
			return array;
		}else{
			Product[] temp = new Product[array.length + 1];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}
	//TODO Change to an index-remove instead of search
	public static Product[] removeFromArray(Product toRemove, Product[] array){
		if(array == null)
			return array;
		
		int index = -1;
		for(int i=0; i<array.length; i++)		//Find index of Product to remove
			if(array[i].equals(toRemove))
				index = i;
		
		if(index == -1){
			System.err.println("Part to remove from array was not found!");
			return array;
		}
		
		Product[] temp = new Product[array.length - 1];
		for(int i=0; i<temp.length; i++)			//Create new array with omitted Product
			if(i < index)
				temp[i] = array[i];
			else
				temp[i] = array[i + 1];
		
		return temp;
	}
	
	//Repair Actions
	public static Repair[] addToArray(Repair toAdd, Repair[] array){
		if(array == null){
			array = new Repair[1];
			array[0] = toAdd;
			return array;
		}else{
			Repair[] temp = new Repair[array.length + 1];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}
	//TODO Change to an index-remove instead of search
	public static Repair[] removeFromArray(Repair toRemove, Repair[] array){
		if(array == null)
			return array;
		
		int index = -1;
		for(int i=0; i<array.length; i++)		//Find index of Repair to remove
			if(array[i].equals(toRemove))
				index = i;
		
		if(index == -1){
			System.err.println("Repair to remove from array was not found!");
			return array;
		}
		
		Repair[] temp = new Repair[array.length - 1];
		for(int i=0; i<temp.length; i++)			//Create new array with omitted Repair
			if(i < index)
				temp[i] = array[i];
			else
				temp[i] = array[i + 1];
		
		return temp;
	}
	
	//Assembly Actions
	public static Assembly[] addToArray(Assembly toAdd, Assembly[] array){
		if(array == null){
			array = new Assembly[1];
			array[0] = toAdd;
			return array;
		}else{
			Assembly[] temp = new Assembly[array.length + 1];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}
	public static Assembly[] removeFromArrayBySerial(Assembly toRemove, Assembly[] array){
		if(array == null)
			return array;
		
		int index = -1;
		for(int i=0; i<array.length; i++)		//Find index of Assembly to remove
			if(array[i].equals(toRemove))
				index = i;
		
		if(index == -1){
			System.err.println("Assembly to remove from array was not found!");
			return array;
		}
		
		Assembly[] temp = new Assembly[array.length - 1];
		for(int i=0; i<temp.length; i++)			//Create new array with omitted Assembly
			if(i < index)
				temp[i] = array[i];
			else
				temp[i] = array[i + 1];
		
		return temp;
	}
	
	public static Assembly[] removeFromArrayByEquiv(Assembly toRemove, Assembly[] array){
		if(array == null)
			return array;
		
		int index = -1;
		for(int i=0; i<array.length; i++)		//Find index of Assembly to remove
			if(array[i].equivalent(toRemove))
				index = i;
		
		if(index == -1){
			System.err.println("Assembly to remove from array was not found!");
			return array;
		}
		
		Assembly[] temp = new Assembly[array.length - 1];
		for(int i=0; i<temp.length; i++)			//Create new array with omitted Assembly
			if(i < index)
				temp[i] = array[i];
			else
				temp[i] = array[i + 1];
		
		return temp;
	}
	
	//Constituent Actions
	public static Constituent[] addToArray(Constituent toAdd, Constituent[] array){
		if(array == null){
			array = new Constituent[1];
			array[0] = toAdd;
			return array;
		}else{
			Constituent[] temp = new Constituent[array.length + 1];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}
	public static Constituent[] addToArray(Constituent[] toAdd, Constituent[] array){
		if(array == null) return toAdd;
		if(toAdd == null) return array;
		
		Constituent[] temp = new Constituent[array.length + toAdd.length];
		int i;
		for(i=0; i<array.length; i++)
			temp[i] = array[i];
		for(int j=0; j<toAdd.length; i++, j++)
			temp[i] = toAdd[j];
		
		return temp;
	}
	//TODO Change to an index-remove instead of search
	public static Constituent[] removeFromArray(Constituent toRemove, Constituent[] array){
		if(array == null)
			return array;
		
		int index = -1;
		for(int i=0; i<array.length; i++)		//Find index of Constituent to remove
			if(array[i].equals(toRemove))
				index = i;
		
		if(index == -1){
			System.err.println("Constituent to remove from array was not found!");
			return array;
		}
		
		Constituent[] temp = new Constituent[array.length - 1];
		for(int i=0; i<temp.length; i++)			//Create new array with omitted Constituent
			if(i < index)
				temp[i] = array[i];
			else
				temp[i] = array[i + 1];
		
		return temp;
	}
	
	//String Actions
	public static String[] addToArray(String toAdd, String[] array){
		if(array == null){
			array = new String[1];
			array[0] = toAdd;
			return array;
		}else{
			String[] temp = new String[array.length + 1];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}

	public static String[] addToArray(String[] toAdd, String[] array){
		if(array == null) return toAdd;
		
		String[] temp = new String[array.length + toAdd.length];
		int i;
		for(i=0; i<array.length; i++)
			temp[i] = array[i];
		for(int j=0; j<toAdd.length; i++, j++)
			temp[i] = toAdd[j];
		
		return temp;
	}
	
	//ProductCategory Actions
	public static ProductCategory[] addToArray(ProductCategory toAdd, ProductCategory[] array){
		if(array == null){
			array = new ProductCategory[1];
			array[0] = toAdd;
			return array;
		}else{
			ProductCategory[] temp = new ProductCategory[array.length + 1];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}
	
	//ServiceCategory Actions
	public static ServiceCategory[] addToArray(ServiceCategory toAdd, ServiceCategory[] array){
		if(array == null){
			array = new ServiceCategory[1];
			array[0] = toAdd;
			return array;
		}else{
			ServiceCategory[] temp = new ServiceCategory[array.length + 1];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}
	
	//Service Actions
	public static Service[] addToArray(Service toAdd, Service[] array){
		if(array == null){
			array = new Service[1];
			array[0] = toAdd;
			return array;
		}else{
			Service[] temp = new Service[array.length + 1];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}
	
	public static Service[] removeFromArray(Service toRemove, Service[] array){
		if(array == null) return null;
		if(array.length == 1) return null;
		
		
		int index = -1;
		for(int i=0; i<array.length; i++)		//Find index of Product to remove
			if(array[i].equals(toRemove))
				index = i;
		
		if(index == -1){
			System.err.println("Part to remove from array was not found!");
			return array;
		}
		
		Service[] temp = new Service[array.length -1];
		for(int i=0; i<temp.length; i++)			//Create new array with omitted Service
			if(i < index)
				temp[i] = array[i];
			else
				temp[i] = array[i + 1];
		
		return temp;
		
	}
	
	//ShippingOption Actions
	public static ShippingOption[] addToArray(ShippingOption toAdd, ShippingOption[] array){
		if(array == null){
			array = new ShippingOption[1];
			array[0] = toAdd;
			return array;
		}else{
			ShippingOption[] temp = new ShippingOption[array.length + 1];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}
	
	public static ShippingOption[] removeFromArray(ShippingOption toRemove, ShippingOption[] array){
		if(array == null)
			return array;
		
		int index = -1;
		for(int i=0; i<array.length; i++)		//Find index of ShippingOption to remove
			if(array[i].record().equals(toRemove.record()))
				index = i;
		
		if(index == -1){
			System.err.println("ShippingOption to remove from array was not found!");
			return array;
		}
		
		if(array.length == 1) return null;
		
		ShippingOption[] temp = new ShippingOption[array.length - 1];
		for(int i=0; i<temp.length; i++)			//Create new array with omitted ShippingOption
			if(i < index)
				temp[i] = array[i];
			else
				temp[i] = array[i + 1];
	
		return temp;
	}
	
	//Category Actions
	public static Category[] addToArray(Category toAdd, Category[] array){
		if(array == null){
			array = new Category[1];
			array[0] = toAdd;
			return array;
		}else{
			Category[] temp = new Category[array.length + 1];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}
	
	//UseRecord Actions
	public static UseRecord[] addToArray(UseRecord toAdd, UseRecord[] array){
		if(array == null){
			array = new UseRecord[1];
			array[0] = toAdd;
			return array;
		}else{
			UseRecord[] temp = new UseRecord[array.length + 1];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}
	
	//int Actions
	public static int[] addToArray(int toAdd, int[] array){
		if(array == null){
			array = new int[1];
			array[0] = toAdd;
			return array;
		}else{
			int[] temp = new int[array.length + 1];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}
	
	//Credit Actions
	public static Credit[] addToArray(Credit toAdd, Credit[] array){
		if(array == null){
			array = new Credit[1];
			array[0] = toAdd;
			return array;
		}else{
			Credit[] temp = new Credit[array.length + 1];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}
	
	public static Credit[][] addToArray(Credit[] toAdd, Credit[][] array){
		if(array == null){
			array = new Credit[1][];
			array[0] = toAdd;
			return array;
		}else{
			Credit[][] temp = new Credit[array.length + 1][];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}
	
	//Timestamp Actions
	public static TimeStamp[] addToArray(TimeStamp toAdd, TimeStamp[] array){
		if(array == null){
			array = new TimeStamp[1];
			array[0] = toAdd;
			return array;
		}else{
			TimeStamp[] temp = new TimeStamp[array.length + 1];
			for(int i=0; i<array.length; i++)
				temp[i] = array[i];
			temp[array.length] = toAdd;
			return temp;
		}
	}
	
	//Utility
	public static void printContents(Part[] array){
		if(array == null)
			System.out.println("Array is null.");
		else
			for(int i=0; i<array.length; i++)
				System.out.println(array[i].getName()); //TODO change using record()
	}
}
