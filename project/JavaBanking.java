import java.io.*;
import java.util.*;

class InsufficientFunds extends Exception {}

class Account implements Serializable {
    private long accountNumber;
    private String firstName;
    private String lastName;
    private float balance;
    private static long NextAccountNumber = 0;

    public Account() {}

    public Account(String fname, String lname, float balance) {
        NextAccountNumber++;
        this.accountNumber = NextAccountNumber;
        this.firstName = fname;
        this.lastName = lname;
        this.balance = balance;
    }

    public long getAccNo() {
        return accountNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public float getBalance() {
        return balance;
    }

    public void Deposit(float amount) {
        balance += amount;
    }

    public void Withdraw(float amount) throws InsufficientFunds {
        if (balance - amount < 500) { // Minimum balance of 500
            throw new InsufficientFunds();
        }
        balance -= amount;
    }

    public static void setLastAccountNumber(long accountNumber) {
        NextAccountNumber = accountNumber;
    }

    public static long getLastAccountNumber() {
        return NextAccountNumber;
    }

    @Override
    public String toString() {
        return "First Name: " + firstName + "\n" +
               "Last Name: " + lastName + "\n" +
               "Account Number: " + accountNumber + "\n" +
               "Balance: " + balance + "\n";
    }
}

class Bank {
    private Map<Long, Account> accounts = new HashMap<>();

    public Bank() {
        try (ObjectInputStream infile = new ObjectInputStream(new FileInputStream("Bank.data"))) {
            while (true) {
                Account account = (Account) infile.readObject();
                accounts.put(account.getAccNo(), account);
                Account.setLastAccountNumber(account.getAccNo());
            }
        } catch (EOFException e) {
            // End of file reached, no more accounts to read
        } catch (FileNotFoundException e) {
            // File not found, first time running
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Account OpenAccount(String fname, String lname, float balance) {
        Account account = new Account(fname, lname, balance);
        accounts.put(account.getAccNo(), account);
        saveAccounts();
        return account;
    }

    public Account BalanceEnquiry(long accountNumber) {
        return accounts.get(accountNumber);
    }

    public Account Deposit(long accountNumber, float amount) {
        Account account = accounts.get(accountNumber);
        account.Deposit(amount);
        saveAccounts();
        return account;
    }

    public Account Withdraw(long accountNumber, float amount) throws InsufficientFunds {
        Account account = accounts.get(accountNumber);
        account.Withdraw(amount);
        saveAccounts();
        return account;
    }

    public void CloseAccount(long accountNumber) {
        accounts.remove(accountNumber);
        System.out.println("Account Deleted");
        saveAccounts();
    }

    public void ShowAllAccounts() {
        for (Account account : accounts.values()) {
            System.out.println(account);
        }
    }

    private void saveAccounts() {
        try (ObjectOutputStream outfile = new ObjectOutputStream(new FileOutputStream("Bank.data"))) {
            for (Account account : accounts.values()) {
                outfile.writeObject(account);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        saveAccounts();
    }
}

public class Main {
    public static void main(String[] args) {
        Bank b = new Bank();
        Scanner sc = new Scanner(System.in);
        int choice;
        String fname, lname;
        long accountNumber;
        float balance, amount;

        System.out.println("***Banking System***");
        do {
            System.out.println("\n\tSelect one option below ");
            System.out.println("\t1 Open an Account");
            System.out.println("\t2 Balance Enquiry");
            System.out.println("\t3 Deposit");
            System.out.println("\t4 Withdrawal");
            System.out.println("\t5 Close an Account");
            System.out.println("\t6 Show All Accounts");
            System.out.println("\t7 Quit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter First Name: ");
                    fname = sc.next();
                    System.out.print("Enter Last Name: ");
                    lname = sc.next();
                    System.out.print("Enter Initial Balance: ");
                    balance = sc.nextFloat();
                    Account acc = b.OpenAccount(fname, lname, balance);
                    System.out.println("\nCongratulations, Account Created!");
                    System.out.println(acc);
                    break;

                case 2:
                    System.out.print("Enter Account Number: ");
                    accountNumber = sc.nextLong();
                    acc = b.BalanceEnquiry(accountNumber);
                    System.out.println("\nYour Account Details");
                    System.out.println(acc);
                    break;

                case 3:
                    System.out.print("Enter Account Number: ");
                    accountNumber = sc.nextLong();
                    System.out.print("Enter Deposit Amount: ");
                    amount = sc.nextFloat();
                    acc = b.Deposit(accountNumber, amount);
                    System.out.println("\nAmount Deposited");
                    System.out.println(acc);
                    break;

                case 4:
                    System.out.print("Enter Account Number: ");
                    accountNumber = sc.nextLong();
                    System.out.print("Enter Withdrawal Amount: ");
                    amount = sc.nextFloat();
                    try {
                        acc = b.Withdraw(accountNumber, amount);
                        System.out.println("\nAmount Withdrawn");
                        System.out.println(acc);
                    } catch (InsufficientFunds e) {
                        System.out.println("Insufficient funds! Minimum balance should be 500.");
                    }
                    break;

                case 5:
                    System.out.print("Enter Account Number: ");
                    accountNumber = sc.nextLong();
                    b.CloseAccount(accountNumber);
                    break;

                case 6:
                    b.ShowAllAccounts();
                    break;

                case 7:
                    b.close();
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Enter correct choice");
            }
        } while (choice != 7);

        sc.close();
    }
}
