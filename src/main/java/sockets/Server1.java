package sockets;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Server for Server-Client chat
public class Server1 {

    private int port;
    private ServerSocket server;

    public static void main(String[] args) throws IOException {
        new Server(12345).run();
    }

    public Server1(int port) {
        this.port = port;
    }

    public void run() throws IOException {
        this.server = new ServerSocket(port);
        System.out.println("Port 12345 is now open.");

        Socket client = server.accept();
        System.out.println("Connection established with client: " + client.getInetAddress().getHostAddress());


        PrintWriter writer = new PrintWriter(client.getOutputStream(),true);

        // create a new thread for client handling
        new Thread(new ClientHandler1(this, client)).start();

        Scanner sc = new Scanner(System.in);
        System.out.println("Type nickname:");
        String nickname = sc.nextLine();

        while(true) {
            System.out.println("Send messages: ");
            String msg = sc.nextLine();
            writer.println(nickname + ": " + msg);
            if(msg.equals("stop")) {
                break;
            }
        }

    }

    class ClientHandler1 implements Runnable {

        private Server1 server;
        private Socket client;

        public ClientHandler1(Server1 server, Socket client) {
            this.server = server;
            this.client = client;
        }

        @Override
        public void run() {
            String message;

            // when there is a new message, broadcast to all
            Scanner sc = null;
            try {
                sc = new Scanner(this.client.getInputStream());
                while (sc.hasNextLine()) {
                    message = sc.nextLine();
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            sc.close();
        }
    }
}


