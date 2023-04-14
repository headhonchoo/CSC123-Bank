
import java.io.Serializable;

public class Transaction implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final char CREDIT = 'C';
	public static final char DEBIT = 'D';

	int txnID;
	double amount;
	char type; // D|C

	public Transaction(char type, double amount) {
		super();
		this.amount = amount;
		this.type = type;
		this.txnID = UniqueCounter.nextValue();
	}

	public double getAmount() {
		return amount;
	}

	public char getType() {
		return type;
	}

	public String toString() {
		return (this.txnID + ":" + (type == this.CREDIT ? "Deposit" : "Withdrawal") + "\t: " + amount);

	}

}
