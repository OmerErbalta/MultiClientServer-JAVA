import java.io.*;
import java.net.*;
public class Client {
    public static void main(String[] args) {
        try {
            //Server's IP address and port are entered for connection.
            Socket socket = new Socket("server_ip", 6000);
            System.out.println("Connected Server");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // Send client name to server
            System.out.print("Enter client name: ");
            String clientName = consoleReader.readLine();
            out.println(clientName);
             System.out.print("Enter the Target Client name:");
             String targetClient = consoleReader.readLine(); 

             // This thread listens for incoming messages from the server continuously.
            Thread receiveThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
            // When a message is received, it gets printed onto the console.
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();
        
            // Sending client messages
            while (true) {
                String message = consoleReader.readLine();            
                //Check to change target client name
                if (message.startsWith("#change ")) {
                    // Take the part after the keyword as the target client name
                    targetClient = message.substring(10); 
                    System.out.println("Target client name changed:" + targetClient);
                } else {
                    out.println(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
