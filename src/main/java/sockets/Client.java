package sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	private String host;
	private int port;
	private String nickname;

	public static void main(String[] args) throws UnknownHostException, IOException {
		new Client("37.150.107.157", 12345).run();
	}

	public Client(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void run() throws UnknownHostException, IOException {
		// connect client to server
		Socket client = new Socket(host, port);
		PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
		writer.println("Client successfully connected to server!");
		writer.flush();

		// create a new thread for server messages handling
		new Thread(new ReceivedMessagesHandler(client.getInputStream())).start();

		// ask for a nickname
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter a nickname: ");
		nickname = sc.nextLine();

		// read messages from keyboard and send to server
		while(true) {
			System.out.println("Send messages: ");
			String msg = sc.nextLine();
			writer.println(nickname + ": " + msg);
			if(msg.equals("stop")) {
				break;
			}
		}

		sc.close();
		client.close();
	}
}

class ReceivedMessagesHandler implements Runnable {

	private InputStream server;

	public ReceivedMessagesHandler(InputStream server) {
		this.server = server;
	}

	@Override
	public void run() {
		// receive server messages and print out to screen
		Scanner s = new Scanner(server);
		while (s.hasNextLine()) {
			System.out.println(s.nextLine());
		}
		s.close();
	}
}
