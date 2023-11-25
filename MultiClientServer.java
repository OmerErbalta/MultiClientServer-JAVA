import java.io.*;
import java.net.*;
import java.util.*;

public class MultiClientServer {
    private static final HashMap<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(6000);
            System.out.println("Sunucu başlatıldı. İstemci bekleniyor...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("İstemci bağlandı: " + clientSocket);

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // İstemci adını belirleme
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String clientName = in.readLine();
                clients.put(clientName, out);

                ClientHandler clientThread = new ClientHandler(clientSocket, clientName);
                Thread t = new Thread(clientThread);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessageToClient(String targetClient, String message) {
        PrintWriter writer = clients.get(targetClient);
        if (writer != null) {
            writer.println(message);
        } else {
            System.out.println("Belirtilen istemci bulunamadı.");
        }
    }

    public static void broadcastMessage(String sender, String message) {
        for (String client : clients.keySet()) {
            sendMessageToClient(client, "[" + sender + "]: " + message);
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private String clientName;

    public ClientHandler(Socket socket, String clientName) {
        try {
            this.clientSocket = socket;
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.clientName = clientName;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            String messageFromClient;
            while ((messageFromClient = in.readLine()) != null) {
                System.out.println("[" + clientName + "]: " + messageFromClient);

                // Özel mesaj işleme
                if (messageFromClient.startsWith("#")) {
                    int index = messageFromClient.indexOf(' ');
                    if (index != -1) {
                        String targetClient = messageFromClient.substring(1, index);
                        String messageContent = messageFromClient.substring(index + 1);
                        MultiClientServer.sendMessageToClient(targetClient, "[" + clientName + "]: " + messageContent);
                    } else {
                        System.out.println("Geçersiz komut formatı.");
                    }
                } else {
                    // Genel mesajları diğer istemcilere yayınla
                    MultiClientServer.broadcastMessage(clientName, messageFromClient);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
