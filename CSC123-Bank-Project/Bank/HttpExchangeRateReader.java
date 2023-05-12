import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class HttpExchangeRateReader extends ExchangeRateReader {
    private String url;

    public HttpExchangeRateReader(String url) {
        this.url = url;
    }

    @Override
    public InputStream getStream() throws IOException {
        URL url = new URL(this.url);
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }
}