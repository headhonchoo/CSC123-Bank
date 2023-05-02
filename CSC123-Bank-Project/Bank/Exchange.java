public class Exchange {

	private String currency;
	private String name;
	private double rate;
	private double buyRate;
	private double sellRate;

	public Exchange(String currency, String name, double rate) {
		this.currency = currency;
		this.name = name;
		this.rate = rate;
		this.buyRate = buyRate;
		this.sellRate = sellRate;
	}

	public Exchange(double buyRate2, double sellRate2) {

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