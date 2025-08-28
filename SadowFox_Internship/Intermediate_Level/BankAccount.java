import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class BankAccount {

    private String accountNumber;
    private double balance;
    private double interestRate; 
    private List<Transaction> transactionHistory;
    private final double OVERDRAFT_LIMIT = -500.0;

    static class Transaction {
        enum Type { DEPOSIT, WITHDRAWAL, INTEREST }
        Type type;
        double amount;
        double newBalance;
        LocalDateTime timestamp;

        public Transaction(Type type, double amount, double newBalance) {
            this.type = type;
            this.amount = amount;
            this.newBalance = newBalance;
            this.timestamp = LocalDateTime.now();
        }

        @Override
        public String toString() {
            return String.format("%s: %.2f (New Balance: %.2f) at %s",
                    type, amount, newBalance, timestamp);
        }
    }
 
    //constructor
    public BankAccount(String accountNumber, double initialBalance, double interestRate) {
        if (initialBalance < OVERDRAFT_LIMIT) {
            throw new IllegalArgumentException("Initial balance cannot be below overdraft limit.");
        }
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.interestRate = interestRate;
        this.transactionHistory = new ArrayList<>();
        if (initialBalance > 0) {
            transactionHistory.add(new Transaction(Transaction.Type.DEPOSIT, initialBalance, initialBalance));
        }
    }
    //deposit method
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        balance += amount;
        transactionHistory.add(new Transaction(Transaction.Type.DEPOSIT, amount, balance));
    }

    //withdraw method
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (balance - amount >= OVERDRAFT_LIMIT) {
            balance -= amount;
            transactionHistory.add(new Transaction(Transaction.Type.WITHDRAWAL, amount, balance));
            return true;
        }
        return false;
    }

    //apply interest method
    public void applyInterest() {
        if (balance > 0) {
            double monthlyInterest = balance * (interestRate / 12);
            balance += monthlyInterest;
            transactionHistory.add(new Transaction(Transaction.Type.INTEREST, monthlyInterest, balance));
        }
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory); 
    }
}