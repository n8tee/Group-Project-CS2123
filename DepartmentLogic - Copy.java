import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;
public class DepartmentLogic {
    public static class Item {
        private final String name;
        private final double price;

        public Item(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }

    public static class Department {
        private String name;
        private double spent = 0;
        private final Deque<Item> itemsDesired = new ArrayDeque<>();
        private final List<Item> itemsRemoved = new ArrayList<>();
        private final List<Item> itemsReceived = new ArrayList<>();

        public Department(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public double getSpent() {
            return spent;
        }

        public List<Item> getItemsRemoved() {
            return itemsRemoved;
        }

        public List<Item> getItemsReceived() {
            return itemsReceived;
        }

        public boolean hasDesiredItems() {
            return !itemsDesired.isEmpty();
        }

        public Item peekNextDesiredItem() {
            return itemsDesired.peekFirst();
        }

        public Item removeNextDesiredItem() {
            return itemsDesired.pollFirst();
        }

        public void addDesiredItem(Item item) {
            itemsDesired.addLast(item);
        }

        public void addRemovedItem(Item item) {
            itemsRemoved.add(item);
        }

        public void addReceivedItem(Item item) {
            itemsReceived.add(item);
            spent += item.getPrice();
        }

        public void addScholarship(double amt) {
            Item s = new Item("Scholarship", amt);
            addReceivedItem(s);

        }


    }

    static void removeUnaffordableItems(Department d, double remaining) {
        while (d.hasDesiredItems()) {
            Item next = d.peekNextDesiredItem();
            if (next.getPrice() > remaining) {
                d.addRemovedItem(d.removeNextDesiredItem());
            } else {
                break;
            }
        }
    }

      static double spendOnce(Department d, double remaining, List<String> purchaseLog) {
        if (!d.hasDesiredItems()) {
            double amt = Math.min(1000.0, remaining);
            d.addScholarship(amt);
            purchaseLog.add(d.getName() + " - Scholarship - $" + String.format("%.2f", amt));
            return amt;
        } else {
            Item it = d.removeNextDesiredItem();
            d.addReceivedItem(it);
            purchaseLog.add(d.getName() + " - " + it.getName() + " - $" + String.format("%.2f", it.getPrice()));
            return it.getPrice();
        }
    }

    public static void main(String[] args) {
        PriorityQueue<Department> departmentQueue = new PriorityQueue<>(Comparator.comparingDouble(Department::getSpent));

        // Example usage
        Department cs = new Department("Computer Science");
        cs.addDesiredItem(new Item("Laptop", 1200.0));
        cs.addDesiredItem(new Item("Textbook", 200.0));

        Department math = new Department("Mathematics");
        math.addDesiredItem(new Item("Graphpaper", 150.0));
        math.addDesiredItem(new Item("Coffee", 300));

        departmentQueue.add(cs);
        departmentQueue.add(math);

        double totalBudget = 600;
        List<String> purchaseLog = new ArrayList<>();

        while (!departmentQueue.isEmpty() && totalBudget > 0) {
            Department dept = departmentQueue.poll();
            removeUnaffordableItems(dept, totalBudget);
            double spent = spendOnce(dept, totalBudget, purchaseLog);
            totalBudget -= spent;
            departmentQueue.add(dept);
        }

        // Print purchase log
        for (String logEntry : purchaseLog) {
            System.out.println(logEntry);
        }
    }
}