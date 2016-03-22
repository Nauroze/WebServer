// SYSC 4502 - W15
// Nauroze Hoath (100788740)

import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class WebServer {
	public static void main(String argv[]) throws Exception {
		
		System.out.println("Waiting for port number...\n");
		int listeningPort = 8000;
		ServerSocket listenSocket = new ServerSocket(listeningPort);
		System.out.println("Listening to port " + listeningPort + "\n");
		
	
		while (true) {
			
			
			Socket connectionSocket = listenSocket.accept(); // Listen for a TCP connection
			
			
			HttpRequest request = new HttpRequest(connectionSocket); // Process Http requests.
			Thread thread = new Thread(request);                    // Each HTTP request is handled on a different thread.
			thread.start();
		}
	}
}