// File: BankAccountTest.java
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Bank Account Unit Tests")
public class BankAccountTest {

    private BankAccount account;

    @BeforeEach
    void setUp() {
        // Initial balance and interest rate for a new account
        account = new BankAccount("123456789", 1000.0, 0.05);
    }

    // --- Deposit Test Cases ---
    @Test
    @DisplayName("Deposit a positive amount.")
    void testDeposit_PositiveAmount() {
        account.deposit(500.0);
        assertEquals(1500.0, account.getBalance(), "Balance should be 1500 after a 500 deposit.");
    }

    @Test
    @DisplayName("Deposit a large amount.")
    void testDeposit_LargeAmount() {
        account.deposit(100000.0);
        assertEquals(101000.0, account.getBalance(), "Balance should reflect large deposit.");
    }

    @Test
    @DisplayName("Depositing zero amount should throw an exception.")
    void testDeposit_ZeroAmount() {
        assertThrows(IllegalArgumentException.class, () -> account.deposit(0.0), "Zero deposit should fail.");
    }

    @Test
    @DisplayName("Depositing a negative amount should throw an exception.")
    void testDeposit_NegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> account.deposit(-100.0), "Negative deposit should fail.");
    }

    @Test
    @DisplayName("Transaction history should update after a deposit.")
    void testDeposit_TransactionHistory() {
        int initialHistorySize = account.getTransactionHistory().size();
        account.deposit(200.0);
        assertEquals(initialHistorySize + 1, account.getTransactionHistory().size(), "History size should increase by 1.");
    }

    // --- Withdrawal Test Cases ---
    @Test
    @DisplayName("Withdraw a valid amount.")
    void testWithdraw_ValidAmount() {
        boolean success = account.withdraw(200.0);
        assertTrue(success, "Withdrawal should succeed.");
        assertEquals(800.0, account.getBalance(), "Balance should be 800 after 200 withdrawal.");
    }

    @Test
    @DisplayName("Withdraw an amount that causes an overdraft.")
    void testWithdraw_Overdraft() {
        boolean success = account.withdraw(1500.0);
        assertTrue(success, "Withdrawal into overdraft should succeed up to the limit.");
        assertEquals(-500.0, account.getBalance(), "Balance should be exactly at the -500 limit.");
    }

    @Test
    @DisplayName("Withdraw an amount that exceeds the overdraft limit.")
    void testWithdraw_ExceedsOverdraftLimit() {
        boolean success = account.withdraw(1501.0);
        assertFalse(success, "Withdrawal should fail if it exceeds the overdraft limit.");
        assertEquals(1000.0, account.getBalance(), "Balance should remain unchanged.");
    }

    @Test
    @DisplayName("Withdrawing zero amount should throw an exception.")
    void testWithdraw_ZeroAmount() {
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(0.0), "Zero withdrawal should fail.");
    }

    @Test
    @DisplayName("Withdrawing a negative amount should throw an exception.")
    void testWithdraw_NegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(-50.0), "Negative withdrawal should fail.");
    }

    // --- Interest Test Cases ---
    @Test
    @DisplayName("Apply interest to a positive balance.")
    void testInterest_PositiveBalance() {
        account.applyInterest();
        double expectedBalance = 1000.0 + (1000.0 * (0.05 / 12));
        assertEquals(expectedBalance, account.getBalance(), 0.01, "Interest should be applied correctly.");
    }

    @Test
    @DisplayName("No interest applied to a zero balance.")
    void testInterest_ZeroBalance() {
        account.withdraw(1000.0); 
        account.applyInterest();
        assertEquals(0.0, account.getBalance(), "No interest should be applied to a zero balance.");
    }

    @Test
    @DisplayName("No interest applied to a negative balance.")
    void testInterest_NegativeBalance() {
        account.withdraw(1200.0); 
        account.applyInterest();
        assertEquals(-200.0, account.getBalance(), "No interest should be applied to a negative balance.");
    }

    @Test
    @DisplayName("Initial balance below overdraft limit should throw an exception.")
    void testConstructor_InitialBalanceBelowOverdraft() {
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("999", -600.0, 0.01), "Initial balance below limit should throw exception.");
    }

    @Test
    @DisplayName("Transaction history should update with interest calculation.")
    void testInterest_TransactionHistory() {
        int initialHistorySize = account.getTransactionHistory().size();
        account.applyInterest();
        assertEquals(initialHistorySize + 1, account.getTransactionHistory().size(), "History size should increase by 1 after interest.");
    }
}
