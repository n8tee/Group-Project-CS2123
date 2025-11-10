package pq;
import java.util.*;
import java.io.*;

class Department implements Comparable<Department> {
	
	String name;
	Double priority;
	Queue<Item> itemsDesired;
	Queue<Item> itemsReceived;
	Queue<Item> itemsRemoved;
	
	public Department(String fileName) {
		
		this.name = "";
		this.priority = 0.0;
		this.itemsDesired = new LinkedList<>();
		this.itemsReceived = new LinkedList<>();
		this.itemsRemoved = new LinkedList<>();
		
		File file = new File(fileName);
		Scanner input;
		
		try {
			input = new Scanner(file);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("The file " + fileName + " was not found.");
			return;
		}
		
		if (input.hasNextLine()) {
			this.name = input.nextLine().trim();
		}
		
		while (input.hasNextLine()) {
			String itemName = input.nextLine().trim();
			
			if (itemName.isEmpty()) {
				continue;
			}
			if (input.hasNextLine()) {
				String priceLine = input.nextLine().trim();
				
				try {
					Double price = Double.parseDouble(priceLine);
					Item item = new Item(itemName, price);
					itemsDesired.offer(item);
					
				} catch (NumberFormatException e) {
					System.err.println("invalid price format in file " + fileName + " for item: " + itemName);
				}
			}
		}
		input.close();
	}
	
	public int compareTo(Department dept) {
		return this.priority.compareTo(dept.priority); 
	}
	public boolean equals( Department dept ){
	    return this.name.compareTo( dept.name ) == 0;
	  }

	  @Override 
	  public boolean equals(Object aThat) {
	    if (this == aThat)
	      return true;
	    if (!(aThat instanceof Department))
	      return false;
	    Department that = (Department)aThat;
	    return this.equals( that ); 
	  }
	  
	  @Override
	  public int hashCode() {
	    return name.hashCode(); 
	  }
	
	  @Override
	  public String toString() {
	    return "NAME: " + name + "\nPRIORITY: " + priority + "\nDESIRED: " + itemsDesired + "\nRECEIVED " + itemsReceived + "\nREMOVED " + itemsRemoved + "\n";
	  }
}

class Item {
	
	String name;
	Double price;
	
	public Item(String name, Double price) {
		this.name = name;
		this.price = price;
	}
}

public class PriorityQ {
	
	private PriorityQueue<Department> departmentPQ;
	private Double remainingBudget;
	private Double budget;
	private List<Department> allDepartments;
	
	public void printSummary() {
		
		System.out.println("\nSummary:");
		System.out.println("Remaining Budget: " + String.format("$%.ef", remainingBudget));
		System.out.println("Total Budget: " + String.format("$%.2lf", budget));
		System.out.println();
		
		for (Department dept : allDepartments) {
			System.out.println("Department of " + dept.name + ":");
			System.out.println("  Received:");
			
			if (dept.itemsReceived.isEmpty()) {
				
				System.out.println("\tNone");
			}
			else {
				
				for (Item item : dept.itemsReceived) {
					String price = String.format("$%.2lf", item.price);
					System.out.printf("\t%-30s - %30s\n", item.name, price);
				}
			}
			
			if (dept.itemsRemoved.isEmpty()) {
				
				System.out.println("\tNone");
			}
			else {
				
				for (Item item : dept.itemsRemoved) {
					String price = String.format("$%.2lf", item.price);
					System.out.printf("\t%-30s - %30s\n", item.name, price);
				}
			}
			
			System.out.println("  Remaining Desired:");
			if (dept.itemsDesired.isEmpty()) {
				
				System.out.println("\tNone");
			}
			else {
				
				for (Item item : dept.itemsDesired) {
					String price = String.format("$%.2lf", item.price);
					System.out.printf("\t%-30s - %30s\n", item.name, price);
				}
			}
			System.out.println();
		}
		
	}
}






