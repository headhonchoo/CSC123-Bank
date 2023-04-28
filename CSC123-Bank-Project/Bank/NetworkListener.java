import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class NetworkListener {

	public static void main(String[] args) throws IOException {
		ServerSocket server = new ServerSocket(80);
		Map<String, String> config = readConfig();

		while (true) {
			Socket sock = server.accept();
			banner(sock.getOutputStream());
			new MainBank(sock.getInputStream(), sock.getOutputStream(), config).run();
		}
	}

	private static void banner(OutputStream out) throws IOException {
		StringBuffer buff = new StringBuffer();
		out.write("\n\nWelcome to the Bank\n".getBytes());
		out.flush();
	}

	private static Map<String, String> readConfig() {
		Map<String, String> config = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(MainBank.CONFIG_FILE))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split("=");
				config.put(tokens[0], tokens[1]);
			}
		} catch (IOException e) {
			System.err.println("Error reading config file: " + e.getMessage());
		}
		return config;
	}
}