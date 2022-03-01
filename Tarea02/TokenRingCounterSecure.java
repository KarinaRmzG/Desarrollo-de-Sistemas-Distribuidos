import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class TokenRingCounterSecure {
	
	private static int token = 0; //token to be sent
	private static int nodeNumber = 6; //number of nodes, should be 6

	public static void main(String[] args) {
		
		//Setting the certificate credentials
		System.setProperty("javax.net.ssl.trustStore","client_keystore.jks");
		System.setProperty("javax.net.ssl.keyStore", "server_keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "abc123");
		System.setProperty("javax.net.ssl.trustStorePassword","abc456");
		
		try (//Choosing node number
		Scanner input = new Scanner(System.in)) {
			System.out.println("Choose the node number: ");
			int node = Integer.parseInt(input.nextLine());
			
			ExecutorService service = null;
			Socket serverSocket = null;
			Socket clientSocket = null;
			try {
				service = Executors.newFixedThreadPool(2); //Creates a pool of 2 threads
				Future<Socket> clientTask = service.submit(initServer(node));
				Future<Socket> serverTask = service.submit(initClient(node));
				while(!serverTask.isDone()) {//If server thread result is done, then create the socket
					Thread.sleep(100);
				}
				serverSocket = serverTask.get();
				while(!clientTask.isDone()) {//If client thread result is done, then create the socket
					Thread.sleep(100);
				}
				clientSocket = clientTask.get();
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
			}
			DataInputStream serverInput = null; //Servers will only recive data
			DataOutputStream clientOutput = null; //Clients will only send data
			try {
				serverInput = new DataInputStream(serverSocket.getInputStream());
				clientOutput = new DataOutputStream(clientSocket.getOutputStream());
				if (node == 0)
					sendInfo(clientOutput); //If node is 0 then start sending info
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				while (true) {
					token = recieveInfo(serverInput) + 1; //Receive & increase token by 1
					System.out.println("Counter: " + token); //Print current token
					if (token >= 500 && node == 0)
						break;
					sendInfo(clientOutput); //Send token to next server
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			service.shutdown();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Callable<Socket> initServer(int node) { //Inits node server
		return () -> {
			SSLServerSocketFactory secureServerSocket = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			ServerSocket server = null;
			Socket connection = null;
			try {
				server = secureServerSocket.createServerSocket(5000 + node);
				connection = server.accept();
				System.out.println("The server of node: " + node 
						+ " has been set up succesfully");
			} catch(IOException e) {
				e.printStackTrace();
			}
			return connection;
		};
	}
	
	public static Callable<Socket> initClient(int node) { //Inits node client
		return () -> {
			SSLSocketFactory secureClientSocket = (SSLSocketFactory) SSLSocketFactory.getDefault();
			Socket connection = null;
			while(true) { //Waits until the connection with the server is established
				try {
					connection = secureClientSocket.createSocket("localhost", 5000 + ((node + 1) % nodeNumber));
					System.out.println("Client connection to the node " + ((node + 1) % nodeNumber)
							+ " accepted");
					break;
				} catch (IOException e) {
						Thread.sleep(100);
				}
			}
			return connection;
		};
	}
	
	public static void sendInfo(DataOutputStream output) throws IOException {
		output.writeInt(token);
	}
	
	public static int recieveInfo(DataInputStream input) throws IOException {
		return input.readInt();
	}

}
