import java.io.*;
import java.net.*;
public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("server_ip", 6000);
            System.out.println("Sunucuya bağlanıldı.");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // İstemci adını sunucuya gönderme
            System.out.print("İstemci adını girin: ");
            String clientName = consoleReader.readLine();
            out.println(clientName);
             System.out.print("Hedef İstemci adını girin: ");
             String targetClient = consoleReader.readLine(); 

            Thread receiveThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        // Sunucudan gelen mesajı istemci adıyla birlikte yazdır
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();
        
            // İstemci me sajları gönderme
            while (true) {
                String message = consoleReader.readLine();            
                // Hedef istemci adını değiştirmek için kontrol et
                if (message.startsWith("#degistir ")) {
                    targetClient = message.substring(10); // Anahtar kelimenin sonrasındaki kısmı hedef istemci adı olarak al
                    System.out.println("Hedef istemci adı değiştirildi: " + targetClient);
                } else {
                    out.println(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
