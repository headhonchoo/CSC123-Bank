import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.TreeMap;

public class Bank {

    private static final String CONFIG_FILE = "config.txt";
    private static Map<Integer, Account> accounts = new TreeMap<>();
    private static Map<String, Exchange> exchangeRates = new HashMap<>();

	public static Account openCheckingAccount(String firstName, String lastName, String ssn, String currency,
			double overdraftLimit) {
		Customer c = new Customer(firstName, lastName, ssn);
		Account a = new CheckingAccount(c, currency, overdraftLimit);
		accounts.put(a.getAccountNumber(), a);
		return a;

	}

	public static Account openSavingAccount(String firstName, String lastName, String ssn, String currency) {
		Customer c = new Customer(firstName, lastName, ssn);
		Account a = new SavingAccount(c, currency);
		accounts.put(a.getAccountNumber(), a);
		return a;

	}

	public static Account lookup(int accountNumber) throws NoSuchAccountException {
		if (!accounts.containsKey(accountNumber)) {
			throw new NoSuchAccountException("\nAccount number: " + accountNumber + " nout found!\n\n");
		}

		return accounts.get(accountNumber);
	}

	public static void makeDeposit(int accountNumber, double amount)
			throws AccountClosedException, NoSuchAccountException {

		lookup(accountNumber).deposit(amount);

	}

	public static void makeWithdrawal(int accountNumber, double amount)
			throws InsufficientBalanceException, NoSuchAccountException {
		lookup(accountNumber).withdraw(amount);
	}

	public static void closeAccount(int accountNumber) throws NoSuchAccountException {
		lookup(accountNumber).close();
	}

	public static double getBalance(int accountNumber) throws NoSuchAccountException {
		return lookup(accountNumber).getBalance();
	}

	public static void listAccounts(OutputStream out) throws IOException {

		out.write((byte) 10);
		Collection<Account> col = accounts.values();

		for (Account a : col) {
			out.write(a.toString().getBytes());
			out.write((byte) 10);
		}

		out.write((byte) 10);
		out.flush();
	}

	public static Map<Integer, Account> getAllAccounts() {
		return accounts;
	}

	public static void printAccountInformation(int accountNumber, OutputStream out)
			throws IOException, NoSuchAccountException {
		lookup(accountNumber).printTransactions(out);
	}
	
	public static void printAccountTransactions(int accountNumber, OutputStream out)
	        throws IOException, NoSuchAccountException {
	    lookup(accountNumber).printTransactions(out);
	}

	private static void loadCurrencyData() {
	    Properties config = new Properties();
	    try {
	        config.load(new FileInputStream(CONFIG_FILE));

	        String supportCurrencies = config.getProperty("support.currencies");
	        if ("true".equalsIgnoreCase(supportCurrencies)) {
	            String source = config.getProperty("currencies.source");
	            ExchangeRateReader reader;

	            if ("file".equalsIgnoreCase(source)) {
	                String filename = config.getProperty("currency.file");
	                reader = new FileExchangeRateReader(filename);
	            } else { 
	                String url = config.getProperty("webservice.url");
	                reader = new HttpExchangeRateReader(url);
	            }

	            try {
	                exchangeRates = reader.getExchangeRates();
	            } catch (IOException e) {
	                System.err.println("Error reading exchange rates: " + e.getMessage());
	            }
	        }
	    } catch (IOException e) {
	        System.err.println("Error loading configuration: " + e.getMessage());
	    }
	}	
	static {
	    loadCurrencyData();
	}
}