import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ExchangeRateFetcher {
	private String webserviceUrl;

	public ExchangeRateFetcher(String webserviceUrl) {
		this.webserviceUrl = webserviceUrl;
	}

	private static final String EXCHANGE_RATE_URL = "http://www.usman.cloud/banking/exchange-rate.csv";

	public String fetchExchangeRates() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(webserviceUrl)).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() == 200) {
			return response.body();
		} else {
			throw new IOException("Error fetching exchange rates. Status code: " + response.statusCode());
		}
	}
}