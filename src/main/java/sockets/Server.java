package sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Server for Multi-client chat
public class Server {

	private int port;
	private List<PrintStream> clients;
	private ServerSocket server;

	public static void main(String[] args) throws IOException {
		new Server(12345).run();
	}

	public Server(int port) {
		this.port = port;
		this.clients = new ArrayList<PrintStream>();
	}

	public void run() throws IOException {
		this.server = new ServerSocket(port);
		System.out.println("Port 12345 is now open.");

		while (true) {
			// accepts a new client
			Socket client = server.accept();
			System.out.println("Connection established with client: " + client.getInetAddress().getHostAddress());
			
			// add client message to list
			this.clients.add(new PrintStream(client.getOutputStream()));
			
			// create a new thread for client handling
			new Thread(new ClientHandler(this, client)).start();
		}
	}

	void broadcastMessages(String msg) {
		for (PrintStream client : this.clients) {
			client.println(msg);

		}
	}
}

class ClientHandler implements Runnable {

	private Server server;
	private Socket client;

	public ClientHandler(Server server, Socket client) {
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
                server.broadcastMessages(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		sc.close();
	}
}
