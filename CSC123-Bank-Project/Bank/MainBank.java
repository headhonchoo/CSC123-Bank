import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainBank {

	// All messages are declared as constants to make it easier to change. Also, to
	// ensure future proofing in case the application need to be made available
	// in more than one languages
	public static final String MSG_ACCOUNT_OPENED = "%nAccount opened, account number is: %s%n%n";
	public static final String MSG_ACCOUNT_CLOSED = "%nAccount number %s has been closed, balance is %s%n%n";
	public static final String MSG_ACCOUNT_NOT_FOUND = "%nAccount number %s not found! %n%n";
	public static final String MSG_FIRST_NAME = "Enter first name:  ";
	public static final String MSG_LAST_NAME = "Enter last name:  ";
	public static final String MSG_CURRENCY = "Account Currency: ";
	public static final String MSG_SSN = "Enter Social Security Number:  ";
	public static final String MSG_ACCOUNT_NAME = "Enter account name:  ";
	public static final String MSG_ACCOUNT_OD_LIMIT = "Enter overdraft limit:  ";
	public static final String MSG_ACCOUNT_CREDIT_LIMIT = "Enter credit limit:  ";
	public static final String MSG_AMOUNT = "Enter amount: ";
	public static final String MSG_ACCOUNT_NUMBER = "Enter account number: ";
	public static final String MSG_ACCOUNT_ACTION = "%n%s was %s, account balance is: %s%n%n";

	public static final String EXCHANGE_RATE_FILE = "exchange-rate.csv";
	private static final String MSG_CURRENCY_SELLING = "The currency you are selling: ";
	private static final String MSG_AMOUNT_SELLING = "The amount you are selling: ";
	private static final String MSG_CURRENCY_BUYING = "The currency you are buying: ";
	private static final String MSG_EXCHANGE_ACTION = "The exchange rate is %s and you will get %s %s%n";
	private static final String MSG_EXCHANGE_INVALID = "One of the currency should be USD";
	private static final String ACCOUNTS_FILE = "accounts.ser";

	public static Map<String, Exchange> exchangeRates;
	public static boolean exchangeFileLoaded = false;

	// Declare main menu and prompt to accept user input
	public static final String[] menuOptions = { "Open Checking Account%n", "Open Saving Account%n", "List Accounts%n",
			"View Statement%n", "Show Account Information%n", "Deposit Funds%n", "Withdraw Funds%n",
			"Foreign Exchange%n", "Close an Account%n", "Exit%n" };
	public static final String MSG_PROMPT = "%nEnter choice: ";

	// Declare streams to accept user input / provide output
	InputStream in;
	OutputStream out;

	// Constructor
	public MainBank(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
		this.exchangeRates = new HashMap<>();
	}

	// Main method.
	public static void main(String[] args) {

		new MainBank(System.in, System.out).run();

	}

	private void loadExchangeRate() throws IOException {
		try {
			BufferedReader inputStream = new BufferedReader(new FileReader(EXCHANGE_RATE_FILE));
			String line = null;
			do {
				line = inputStream.readLine();
				if (line != null) {
					String[] tokens = line.split(",");
					exchangeRates.put(tokens[0], new Exchange(tokens[0], tokens[1], Double.parseDouble(tokens[2])));
				}
			} while (line != null);
			inputStream.close();
			exchangeFileLoaded = true;

		} catch (FileNotFoundException ex) {
			exchangeFileLoaded = false;
			out.write(("** Currency file could not be loaded, " + "Currency Conversion Service and "
					+ "Foreign Currency accounts are not available **\n").toString().getBytes());
		}
	}

	// The core of the program responsible for providing user experience.
	public void run() {
		try {
			loadExchangeRate();
		} catch (IOException e1) {
		}

		Account acc;
		int option = 0;

		UIManager ui = new UIManager(this.in, this.out, menuOptions, MSG_PROMPT);

		try {
			loadAccounts(ui);

			do {
				option = ui.getMainOption(); // Render main menu

				switch (option) {
				case 1:

					// Compact statement to accept user input, open account, and print the result
					// including the account number
					ui.print(MSG_ACCOUNT_OPENED,
							new Object[] { Bank.openCheckingAccount(ui.readToken(MSG_FIRST_NAME),
									ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN),
									exchangeFileLoaded ? ui.readToken(MSG_CURRENCY) : "USD",
									ui.readDouble(MSG_ACCOUNT_OD_LIMIT)).getAccountNumber() });
					break;
				case 2:
					// Compact statement to accept user input, open account, and print the result
					// including the account number
					ui.print(MSG_ACCOUNT_OPENED, new Object[] { Bank
							.openSavingAccount(ui.readToken(MSG_FIRST_NAME), ui.readToken(MSG_LAST_NAME),
									ui.readToken(MSG_SSN), exchangeFileLoaded ? ui.readToken(MSG_CURRENCY) : "USD")
							.getAccountNumber() });
					break;

				case 3:

					// Get bank to print list of accounts to the output stream provided as method
					// arguemnt
					Bank.listAccounts(this.out);
					break;

				case 4:

					// find account and get the account to print transactions to the output stream
					// provided in method arguments
					try {
						Bank.printAccountTransactions(ui.readInt(MSG_ACCOUNT_NUMBER), this.out);
					} catch (NoSuchAccountException e1) {
						this.handleException(ui, e1);

					}

					break;

				case 5:
					try {
						Bank.printAccountInformation(ui.readInt(MSG_ACCOUNT_NUMBER), this.out);
					} catch (NoSuchAccountException e1) {
						this.handleException(ui, e1);

					}

					break;

				case 6:
					// find account, deposit money and print result

					try {
						int accountNumber = ui.readInt(MSG_ACCOUNT_NUMBER);
						Bank.makeDeposit(accountNumber, ui.readDouble(MSG_AMOUNT));
						ui.print(MSG_ACCOUNT_ACTION,
								new Object[] { "Deposit", "successful", Bank.getBalance(accountNumber) });
					} catch (NoSuchAccountException | AccountClosedException e) {
						this.handleException(ui, e);

					}
					break;

				case 7:
					// find account, withdraw money and print result
					try {
						int accountNumber = ui.readInt(MSG_ACCOUNT_NUMBER);
						Bank.makeWithdrawal(accountNumber, ui.readDouble(MSG_AMOUNT));
						ui.print(MSG_ACCOUNT_ACTION,
								new Object[] { "Withdrawal", "successful", Bank.getBalance(accountNumber) });

					} catch (NoSuchAccountException | InsufficientBalanceException e) {
						this.handleException(ui, e);

					}
					break;

				case 8:
					String from = ui.readToken(MSG_CURRENCY_SELLING);
					double amount = ui.readDouble(MSG_AMOUNT_SELLING);
					String to = ui.readToken(MSG_CURRENCY_BUYING);
					if ("USD".equalsIgnoreCase(from) || "USD".equalsIgnoreCase(to)) {
						Exchange exchange = exchangeRates.get(from.toUpperCase());
						if (exchange != null) {
							double changed = 0.0;
							if ("USD".equalsIgnoreCase(from)) {
								changed = exchange.usdToCurrent(amount);
							} else {
								changed = exchange.currentToUSD(amount);
							}
							ui.print(MSG_EXCHANGE_ACTION, new Object[] { String.format("%.2f", exchange.getRate()), to,
									String.format("%.2f", changed) });
						} else {
							ui.print(MSG_EXCHANGE_INVALID, new Object[] {});
						}
					} else {
						ui.print(MSG_EXCHANGE_INVALID, new Object[] {});
					}

					break;

				case 9:
					// find account and close it

					try {
						int accountNumber = ui.readInt(MSG_ACCOUNT_NUMBER);
						Bank.closeAccount(accountNumber);
						ui.print(MSG_ACCOUNT_CLOSED, new Object[] { accountNumber, Bank.getBalance(accountNumber) });

					} catch (NoSuchAccountException e) {
						this.handleException(ui, e);

					}
					break;
				case 10:
					saveAccounts(ui);
					break;
				}

			} while (option != menuOptions.length);

		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	private void handleException(UIManager ui, Exception e) throws IOException {
		ui.print(e.getMessage(), new Object[] {});
	}

	private void loadAccounts(UIManager ui) throws IOException {
		try {
			FileInputStream fIn = new FileInputStream(ACCOUNTS_FILE);
			ObjectInputStream in = new ObjectInputStream(fIn);
			Object object = null;
			while ((object = in.readObject()) != null) {
				Account account = (Account) object;
				if (account != null) {
					Bank.getAllAccounts().put(account.getAccountNumber(), account);
				}
			}
			int maxNumber = Integer.MIN_VALUE;
			Set<Integer> integers = Bank.getAllAccounts().keySet();
			for (Integer integer : integers) {
				if (integer > maxNumber) {
					maxNumber = integer;
				}
			}
			UniqueCounter.setCounterState(maxNumber + 1);
			in.close();
			fIn.close();
		} catch (FileNotFoundException e) {
			// ui.print(e.getMessage(), new Object[] { });
		} catch (IOException ex) {
			// ui.print(ex.getMessage(), new Object[] { });
		} catch (ClassNotFoundException e) {
			// ui.print(e.getMessage(), new Object[] { });
		}
	}

	private void saveAccounts(UIManager ui) throws IOException {
		try {
			FileOutputStream fOut = new FileOutputStream(ACCOUNTS_FILE);
			ObjectOutputStream out = new ObjectOutputStream(fOut);
			for (Account account : Bank.getAllAccounts().values()) {
				out.writeObject(account);
			}
			out.close();
			fOut.close();
		} catch (FileNotFoundException e) {
			// ui.print(e.getMessage(), new Object[] { });
		} catch (IOException ex) {

		}
	}

}