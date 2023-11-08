package pl.darsonn.socketserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private Socket socket;
    private ServerSocket serverSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Błąd podczas wysyłania wiadomości do clienta.");
        }
    }

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            socket = serverSocket.accept();
            System.out.println("Client accepted");

            socket = serverSocket.accept();
            System.out.println("Client accepted");
            System.out.println("Client ip address: " + socket.getRemoteSocketAddress().toString());

            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String line = "";

            while (!line.equals("Over")) {
                try {
                    line = reader.readLine();
                    outputStream.writeUTF(line);
                    outputStream.flush();
                } catch (IOException i) {
                    System.out.println(i.getMessage());
                }
            }

            System.out.println("Closing connection");

            socket.close();
            inputStream.close();
            outputStream.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
