package pq;

import java.io.*;
import java.util.*;

public class PriorityQ {

    // Item
    public static class Item {
        String name;
        double price;

        public Item(String name, double price) {
            this.name = name;
            this.price = price;
        }
    }

    // Department
    public static class Department implements Comparable<Department> {
        String name;
        double spent = 0;
        Queue<Item> itemsDesired = new LinkedList<>();
        Queue<Item> itemsReceived = new LinkedList<>();
        Queue<Item> itemsRemoved = new LinkedList<>();

        public Department(String fileName) {
            File file = new File(fileName);
            try (Scanner input = new Scanner(file)) {
                if (input.hasNextLine()) this.name = input.nextLine().trim();

                while (input.hasNextLine()) {
                    String itemName = input.nextLine().trim();
                    if (itemName.isEmpty()) continue;
                    if (input.hasNextLine()) {
                        String priceLine = input.nextLine().trim();
                        try {
                            double price = Double.parseDouble(priceLine);
                            itemsDesired.offer(new Item(itemName, price));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid price in " + fileName + ": " + itemName);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error reading file: " + fileName);
            }
        }

        @Override
        public int compareTo(Department o) {
            return Double.compare(this.spent, o.spent);
        }

        public boolean hasDesired() { return !itemsDesired.isEmpty(); }
        public Item nextDesired() { return itemsDesired.peek(); }
        public Item pollDesired() { return itemsDesired.poll(); }
        public void addRemoved(Item i) { itemsRemoved.offer(i); }
        public void addReceived(Item i) { itemsReceived.offer(i); spent += i.price; }
        public void addScholarship(double amt) {
            addReceived(new Item("Scholarship", amt));
        }
    }

    // PQ Budget Algorithm
    static void removeUnaffordable(Department d, double remaining) {
        while (d.hasDesired()) {
            Item next = d.nextDesired();
            if (next.price > remaining)
                d.addRemoved(d.pollDesired());
            else break;
        }
    }

    static double spendOnce(Department d, double remaining, List<String> log) {
        if (!d.hasDesired()) {
            double amt = Math.min(1000.0, remaining);
            d.addScholarship(amt);
            log.add("Department of " + String.format("%-30s", d.name) + " - Scholarship\n- $" + String.format("%.2f", amt));
            return amt;
        } else {
            Item it = d.pollDesired();
            d.addReceived(it);
            log.add("Department of " + String.format("%-30s", d.name) + String.format( " - %-8s",it.name) + "\n- $" + String.format("%.2f", it.price));
            return it.price;
        }
    }

    // Main
    public static void main(String[] args) {
        // example files
        List<String> files = List.of("Department-Chemistry.txt", "Department-ComputerScience.txt", "Department-Mathematics.txt", "Department-PhysicsAndAstronomy.txt");
        List<Department> departments = new ArrayList<>();
        for (String f : files) departments.add(new Department(f));

        PriorityQueue<Department> pq = new PriorityQueue<>();
        pq.addAll(departments);

        double totalBudget = 1000;
        System.out.println("This solution was completed by: \n<Nikolas Romero>\n<Nathaniel Hughes>");
        System.out.println("\nTESTING WITH TOTAL BUDGET: " + totalBudget);
        double initialBudget = totalBudget;
        List<String> purchaseLog = new ArrayList<>();

        while (!pq.isEmpty() && totalBudget > 0) {
            Department d = pq.poll();
            removeUnaffordable(d, totalBudget);
            double spent = spendOnce(d, totalBudget, purchaseLog);
            totalBudget -= spent;
            pq.offer(d);
        }

        printReport(purchaseLog, departments, initialBudget, totalBudget);
    }

    // Report
    static void printReport(List<String> log, List<Department> depts,
                            double initialBudget, double remaining) {
        System.out.println("\nITEMS PURCHASED");
        System.out.println("---------------------------------"
        		+ "---------------------------------------");
        for (String s : log) System.out.println(s);

        System.out.println("---------------------------------"
        		+ "---------------------------------------");
        System.out.printf("Total Budget: $%.2f\nRemaining Budget: $%.2f\n\n",
                initialBudget, remaining);

        for (Department d : depts) {
            System.out.println("Department of " + d.name);
            System.out.printf("  Total Spent: $%.2f\n", d.spent);
            System.out.printf("  Percent of Budget: %.2f%%\n\n",
                    (d.spent / initialBudget) * 100);

            System.out.println("  ITEMS RECEIVED:");
            if (d.itemsReceived.isEmpty()) System.out.println("    None");
            else
                for (Item i : d.itemsReceived)
                    System.out.printf("\t - %-30s \t$%8.2f\n", i.name, i.price);

            System.out.println("  ITEMS NOT RECEIVED:");
            if (d.itemsRemoved.isEmpty()) System.out.println("\t - None");
            else
                for (Item i : d.itemsRemoved)
                    System.out.printf("\t - %-30s \t$%8.2f\n", i.name, i.price);

            System.out.println("---------------------------------"
            		+ "---------------------------------------");
        }
    }
}

