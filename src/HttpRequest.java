// SYSC 4502 - lab 1 - W15
// Lamis Al-Dib (100868769)

import java.io.* ;
import java.net.* ;
import java.util.* ;


final class HttpRequest implements Runnable {
	final static String CRLF = "\r\n";   ///to terminate each line of the server's response message with a carriage return (CR) and a line feed (LF)
	Socket socket;   ///connection socket

	
	public HttpRequest(Socket socket) throws Exception {
		this.socket = socket;
	}

	
	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	
	private void processRequest() throws Exception {
		//Get a reference to the socket's input and output streams
		InputStream is = socket.getInputStream();     ///creating input stream-attached to socket
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());   ///creating output stream
		//Set up input stream filters
		BufferedReader br = new BufferedReader(new InputStreamReader(is));   ///reading from input stream
		
		//Get the request line of the HTTP request message
		String requestLine = br.readLine();    ///reading request line from socket 
		//Extract the filename from the request line
		StringTokenizer tokens = new StringTokenizer(requestLine);    ///tokenized request line 
		
		///parsing request line 
		tokens.nextToken();     ///skip over the method field (assume: GET)
		String fileName = tokens.nextToken();   ///store 'URL' field as filename
		
		fileName = "." + fileName ;  ///Because the browser precedes the filename with a slash, prefix a dot so resulting pathname is within current directory
		
		FileInputStream fis = null ;
		boolean fileExists = true ;
		
		///try opening the file 
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false ;
		}
		System.out.println("Incoming!!!");
		System.out.println(requestLine);
		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
		}
		//Construct the response message
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		
		
		///HTTP response status codes
		if (fileExists) {
			statusLine = "HTTP/1.0 200 OK"   ///request succeeded  
							+CRLF;
			contentTypeLine = "Content -Type: "
			                + contentType(fileName)
			                + CRLF;
		} else {   ///ERROR MESSAGE
			
			statusLine = "HTTP/1.0 404 Not Found"    ///if file doesn't exist  
							+ CRLF;
							
			// statusLine = "Sorry...! Document requested not found." + CRLF;
			contentTypeLine =  "Content -Type: "
								+ "text/html"
								+ CRLF; 
			entityBody = "<HTML>"
			           + "<HEAD><TITLE>Not Found<P></TITLE></HEAD>"
			           +"<BODY>File Not Found on the server</BODY></HTML>";
		}
		os.writeBytes(statusLine);  
		//Send the content type line
		os.writeBytes(contentTypeLine);
		os.writeBytes(CRLF);
		
		
		/*
			If the requested file exists, we call a
			separate method to send the file. If the requested file does not exist, we
			send the HTML-encoded error message that we have prepared.
		*/
		
		if (fileExists) {
			sendBytes(fis, os);
			fis.close();
		} else {
			//byte[] err = entityBody.getBytes();
			os.writeBytes(entityBody);
		}
		os.close();
		br.close();
		socket.close();
	}

	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
	byte[] buffer = new byte[1024];
	int bytes = 0;
		
		while ((bytes = fis.read(buffer)) != -1) {
     	System.out.println("buffer= " + buffer);
			os.write(buffer, 0, bytes);
		}
	}

	private static String contentType(String fileName) {
		if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		} else if(fileName.endsWith(".gif")) {
			return "image/gif";
		} else if(fileName.endsWith(".jpeg")) {
			return "image/jpeg";
		
		} else {
			return "application/octet-stream" ;
		}
	}

}
