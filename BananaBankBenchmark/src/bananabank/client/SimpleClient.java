package bananabank.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
//import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SimpleClient {
	
	private static final int PORT = 4444;

	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket s = new Socket("localhost", PORT);
		System.out.println("Client is connected to the server");
		PrintStream ps = new PrintStream(s.getOutputStream());
		BufferedReader r = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		
		// Round 1 - Valid
		ps.println("10 11111 44444");
		String line = r.readLine(); // read in from server
		System.out.println(line);
		
		// Round 2 - Invalid
		ps.println("10 11111 121212");
		line = r.readLine();
		System.out.println(line);
		
		// Round 3 - Shutdown
		ps.println("SHUTDOWN");
		line = r.readLine();
		System.out.println(line);
		
		ps.close();
	}
}