package PalakMeena_java_training.session1.advanced_topics;

//Example of Interface representing a payment method in a payment system

// The Contract
interface PaymentMethod {
    void processPayment(double amount);

    void refund(double amount);
}

// Implementation 1: Credit Card
class CreditCard implements PaymentMethod {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing Credit Card payment of ₹" + amount + " (2% tax applied)");
    }

    @Override
    public void refund(double amount) {
        System.out.println("Refunding ₹" + amount + " to Credit Card statement.");
    }
}

// Implementation 2: UPI (Scan and Pay)
class UpiPayment implements PaymentMethod {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing UPI payment of ₹" + amount + " (Zero tax)");
    }

    @Override
    public void refund(double amount) {
        System.out.println("Refund of ₹" + amount + " initiated to linked Bank Account.");
    }
}

public class InterfaceExample_PaymentSystem {
    public static void main(String[] args) {
        PaymentMethod payment;

        // Using Credit Card
        payment = new CreditCard();
        payment.processPayment(5000);

        // Using UPI
        payment = new UpiPayment();
        payment.processPayment(1200);
    }
}