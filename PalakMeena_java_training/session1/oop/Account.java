package PalakMeena_java_training.session1.oop;

//Example of Encapsulation and Data Hiding
public class Account {
    // Private variables (Data Hiding)
    private double balance;

    public Account(double initialBalance) {
        if (initialBalance >= 0) {
            this.balance = initialBalance;
        }
    }

    // Public Getter
    public double getBalance() {
        return balance;
    }

    // Public Setter with Validation (Encapsulation logic)
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Successfully deposited: " + amount);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }
}