public class Exchange {

    private final String currency;
    private final String name;
    private double rate;
    private double buyRate;
    private double sellRate;

    public Exchange(String currency, String name, double rate, double buyRate, double sellRate) {
        this.currency = currency;
        this.name = name;
        this.rate = rate;
        this.buyRate = buyRate;
        this.sellRate = sellRate;
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

    public String getName() {
        return name;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getBuyRate() {
        return buyRate;
    }

    public void setBuyRate(double buyRate) {
        this.buyRate = buyRate;
    }

    public double getSellRate() {
        return sellRate;
    }

    public void setSellRate(double sellRate) {
        this.sellRate = sellRate;
    }
}