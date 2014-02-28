package bananabank.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MTServer {
	
	private static final int PORT = 4444;

	public static void main(String[] args) throws IOException {
		
		BananaBank bank = new BananaBank("accounts.txt");
		System.out.println("MAIN: BananaBank created");
		
		ServerSocket ss = new ServerSocket(PORT);
		System.out.println("MAIN: ServerSocket created");
		
		ArrayList<WorkerThread> threads = new ArrayList<WorkerThread>();
		
		try {
			for(;;) { // exits for loop when WorkerThread closes ServerSocket
				System.out.println("MAIN: Waiting for client connection on port " + PORT);				
				Socket cs = ss.accept();
				System.out.println("MAIN: Client connected");
				WorkerThread t = new WorkerThread(cs, ss, bank);
				t.start(); // calls run() on the thread
				System.out.println("MAIN: thread started");
				threads.add(t);
				System.out.println("MAIN: thread added");
			}
		} catch(IOException e) {}
		
		// stop all workers
		for (WorkerThread workerThread : threads) {
			try {
				System.out.println("MAIN: waiting for threads...");
				workerThread.join();
				System.out.println("MAIN: threads joined");
			} catch (InterruptedException e) {}
		}	
		
		// save each thread
		bank.save("accounts.txt");
		
		// calculate the total account value
		System.out.println("MAIN: calculating account value");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream("accounts.txt")));
		String line;
		int accountTotal = 0;
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);
			st.nextToken(); // this token value doesn't matter here
			accountTotal = accountTotal + Integer.parseInt(st.nextToken());
		}
		WorkerThread.shutdownPS.println(accountTotal);
		WorkerThread.shutdownPS.close();
		br.close();
		// print out the accountTotal
		System.out.println("Server: " + accountTotal);
	}
}