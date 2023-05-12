import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

public class MainBank {

	public static final String CONFIG_FILE = "/Users/headhoncho/Library/Mobile Documents/com~apple~TextEdit/Documents/config.txt";
	public static final String SUPPORT_CURRENCIES_KEY = "support.currencies";
	public static final String CURRENCIES_SOURCE_KEY = "currencies.source";
	public static final String WEBSERVICE_URL_KEY = "webservice.url";
	public static final String CURRENCY_FILE_KEY = "currency.file";

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

	public static final String EXCHANGE_RATE_FILE = "/Users/headhoncho/git/CSC123-Bank/CSC123-Bank-Project/Bank/exchange-rate.csv";
	private static final String MSG_CURRENCY_SELLING = "The currency you are selling: ";
	private static final String MSG_AMOUNT_SELLING = "The amount you are selling: ";
	private static final String MSG_CURRENCY_BUYING = "The currency you are buying: ";
	private static final String MSG_EXCHANGE_ACTION = "The exchange rate is %s and you will get %s %s%n";
	private static final String MSG_EXCHANGE_INVALID = "One of the currency should be USD%n%n";
	private static final String ACCOUNTS_FILE = "accounts.ser";

	public static Map<String, Exchange> exchangeRates;
	public static boolean exchangeFileLoaded = false;

	// Declare main menu and prompt to accept user input
	public static final String[] menuOptions = { "Open Checking Account%n", "Open Saving Account%n", "List Accounts%n",
			"View Statement%n", "Show Account Information%n", "Deposit Funds%n", "Withdraw Funds%n",
			"Foreign Exchange%n", "Close an Account%n", "Exit%n" };
	public static final String MSG_PROMPT = "%nEnter choice: ";

	// Declare streams to accept user input/provide output
	InputStream in;
	OutputStream out;

	// Constructor
	private Map<String, String> config;

	public MainBank(InputStream in, OutputStream out, Map<String, String> config) {
		this.in = in;
		this.out = out;
		this.exchangeRates = new HashMap<>();

		this.config = config;
	}

	// Main method.
	public static void main(String[] args) {
		
		String apiUrl = "https://exchangeratesapi.io/"; 
        String apiKey = "NZ987T5uR1YWSdXLKi10RS6IaB3h7gGv"; //Api key
        ExchangeRateHook exchangeRateHook = new ExchangeRateHook(apiUrl, apiKey);

        try {
            JSONObject exchangeRates = exchangeRateHook.getExchangeRates();
            System.out.println("Exchange rates: " + exchangeRates);
        } catch (Exception e) {
            System.err.println("Error fetching exchange rates: " + e.getMessage());
        }
	 
		try {
			Map<String, String> config = readConfig();
			new MainBank(System.in, System.out, config).run();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static Map<String, String> readConfig() throws IOException {
		Map<String, String> config = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(CONFIG_FILE))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split("=");
				if (tokens.length == 2) {
					config.put(tokens[0].trim(), tokens[1].trim());
				}
			}
		}
		return config;
	}

	private void loadExchangeRate() throws IOException {
	    if (!Boolean.parseBoolean(config.get(SUPPORT_CURRENCIES_KEY))) {
	        return;
	    }

	    ExchangeRateReader reader;

	    if (config.get(CURRENCIES_SOURCE_KEY).equals("webservice")) {
	        reader = new HttpExchangeRateReader(config.get(WEBSERVICE_URL_KEY));
	    } else {
	        reader = new FileExchangeRateReader(config.get(CURRENCY_FILE_KEY));
	    }

	    try {
	        Map<String, Exchange> content = reader.getExchangeRates();
	       
	        exchangeFileLoaded = true;  // set to true if successfully loaded
	    } catch (IOException ex) {
	        exchangeFileLoaded = false;
	        out.write(("** Currency file could not be loaded, " + "Currency Conversion Service and "
	                + "Foreign Currency accounts are not available **\n").getBytes());
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
					} catch (NoSuchAccountException e) {
						this.handleException(ui, e);

					}

					break;

				case 5:
					try {
						Bank.printAccountInformation(ui.readInt(MSG_ACCOUNT_NUMBER), this.out);
					} catch (NoSuchAccountException e2) {
						this.handleException(ui, e2);

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