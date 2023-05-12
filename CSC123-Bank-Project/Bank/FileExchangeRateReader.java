import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileExchangeRateReader extends ExchangeRateReader {
    private String filename;

    public FileExchangeRateReader(String filename) {
        this.filename = filename;
    }

    @Override
    public InputStream getStream() throws IOException {
        return new FileInputStream(filename);
    }
}