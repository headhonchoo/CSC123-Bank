public class Exchange {

	private String currency;
	private String name;
	private double rate;

	public Exchange(String currency, String name, double rate) {
		this.currency = currency;
		this.name = name;
		this.rate = rate;
	}

	public double currentToUSD(double amount) {
		return amount * rate;
	}

	public double usdToCurrent(double amount) {
		return amount / rate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

}