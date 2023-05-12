import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public abstract class ExchangeRateReader {
	

    public abstract InputStream getStream() throws IOException;

    public Map<String, Exchange> getExchangeRates() throws IOException {
        InputStream stream = getStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        Map<String, Exchange> exchangeRates = new HashMap<>();

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            String currency = parts[0];
            String name = parts[1];
            double rate = Double.parseDouble(parts[2]);
            double buyRate = Double.parseDouble(parts[3]);
            double sellRate = Double.parseDouble(parts[4]);
            exchangeRates.put(currency, new Exchange(currency, name, rate, buyRate, sellRate));
        }

        return exchangeRates;
    }
}