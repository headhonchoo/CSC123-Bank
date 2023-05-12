import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Account implements Serializable {

	private static final long serialVersionUID = 1L;
	private String accountName;
	private Customer accountHolder;
	private String currency = "USD";
	private ArrayList<Transaction> transactions;

	private boolean open = true;
	private int accountNumber;

	public Account(String name, String currency, Customer customer) {
		accountName = name;
		accountHolder = customer;
		this.currency = currency;
		transactions = new ArrayList<Transaction>();
		accountNumber = UniqueCounter.nextValue();
	}

	public String getAccountName() {
		return accountName;
	}

	public Customer getAccountHolder() {
		return accountHolder;
	}

	public String getAccountCurrency() {
		return currency;
	}

	public double getBalance() {
		double workingBalance = 0;

		for (Transaction t : transactions) {
			if (t.getType() == Transaction.CREDIT)
				workingBalance += t.getAmount();
			else
				workingBalance -= t.getAmount();
		}

		return workingBalance;
	}

	public void deposit(double amount) throws AccountClosedException {
		double balance = getBalance();
		if (!isOpen() && balance >= 0) {
			throw new AccountClosedException("\nAccount is closed with positive balance, deposit not allowed!\n\n");
		}
		transactions.add(new Transaction(Transaction.CREDIT, amount));
	}

	public void withdraw(double amount) throws InsufficientBalanceException {

		double balance = getBalance();

		if (!isOpen() && balance <= 0) {
			throw new InsufficientBalanceException("\nThe account is closed and balance is: " + balance + "\n\n");
		}

		transactions.add(new Transaction(Transaction.DEBIT, amount));
	}

	public void close() {
		open = false;
	}

	public boolean isOpen() {
		return open;
	}

	public int getAccountNumber() {
		return accountNumber;
	}

	public String toString() {
		String aName = accountNumber + "(" + accountName + ")" + " : " + accountHolder.toString() + " : "
				+ getAccountCurrency() + " : " + getBalance() + " : "
				+ String.format("%.2f", MainBank.exchangeRates.get(getAccountCurrency()).currentToUSD(getBalance()))
				+ " : " + (open ? "Account Open" : "Account Closed");
		return aName;
	}

	public void printTransactions(OutputStream out) throws IOException {

		out.write("\n\n".getBytes());
		out.write("------------------\n".getBytes());

		for (Transaction t : transactions) {
			out.write(t.toString().getBytes());
			out.write((byte) 10);
		}
		out.write("------------------\n".getBytes());
		out.write(("Balance: " + getBalance() + "\n\n\n").getBytes());
		out.flush();

	}

	public void printInformation(OutputStream out) throws IOException {
		out.write(("Account Number: " + String.valueOf(getAccountNumber()) + "\n").getBytes());
		out.write(("Name: " + getAccountHolder().getFirstName() + " " + getAccountHolder().getLastName() + "\n")
				.getBytes());
		out.write(("SSN: " + getAccountHolder().getSSN() + "\n").getBytes());
		out.write(("Currency: " + getAccountCurrency() + "\n").getBytes());
		out.write(("Currency Balance: " + getAccountCurrency() + " " + String.format("%.2f", getBalance()) + "\n")
				.getBytes());
		Exchange exchange = MainBank.exchangeRates.get(getAccountCurrency());
		if (exchange != null) {
			double usdAmount = exchange.currentToUSD(getBalance());
			out.write(("USD Balance: " + String.format("%.2f", usdAmount) + "\n").getBytes());
		} else {
			out.write("No exchange rate available.\n".getBytes());
		}

		out.write(("Account Status: " + (isOpen() ? "Open" : "Closed") + "\n").getBytes());
		out.flush();
	}
}
					