import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ExchangeRateHook {
    private final HttpClient httpClient;
    private final String apiUrl;
    private final String apiKey;
    
    public ExchangeRateHook(String apiUrl, String apiKey) {
        this.httpClient = HttpClient.newHttpClient();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    public JSONObject getExchangeRates() throws IOException, InterruptedException, ParseException {
        String requestUrl = apiUrl + "?access_key=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.body());
        
        return json;
    }
}