package bananabank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class WorkerThread extends Thread {

	public static PrintStream shutdownPS;

	Socket clientSocket;
	ServerSocket serverSocket;
	BananaBank bank;

	public WorkerThread(Socket cs, ServerSocket ss, BananaBank b) {
		this.clientSocket = cs;
		this.serverSocket = ss;
		this.bank = b;
	}

	@Override
	public void run() {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			PrintStream ps = new PrintStream(clientSocket.getOutputStream());
			String line;
			while ((line = r.readLine()) != null) { // read line(s) in from client

				System.out.println("Client: " + line);

				if (line.startsWith("S")) { // SHUTDOWN
					serverSocket.close();
					shutdownPS = ps;
					return;
				}

				// parse string into individual integers
				StringTokenizer st = new StringTokenizer(line);
				int transferAmt = Integer.parseInt(st.nextToken());
				int srcAccountNumber = Integer.parseInt(st.nextToken());
				int dstAccountNumber = Integer.parseInt(st.nextToken());
				System.out.println("String was parsed");

				// do error checking for srcAccountNumber
				if (bank.getAccount(srcAccountNumber) == null)  {
					System.out.println("Invalid source account");
					ps.println("Invalid source account");
					continue;
				}

				// do error checking for dstAccountNumber
				if (bank.getAccount(dstAccountNumber) == null) {
					System.out.println("Invalid source account");
					ps.println("Invalid source account");
					continue;
				}

				// set account numbers to their corresponding accounts
				Account srcA = bank.getAccount(srcAccountNumber);
				Account dstA = bank.getAccount(dstAccountNumber);

				// transfer money to destination account - do locking here
				if (srcAccountNumber < dstAccountNumber) {
					synchronized(srcA) { // if you have the lock, you are the only one who reserved srcA
						synchronized(dstA) { // now you are the only one who reserved dstA
							srcA.transferTo(transferAmt, dstA);
							System.out.println("Sever: " + transferAmt + " transferred from account "
									+ srcAccountNumber + " to account " + dstAccountNumber);
							ps.println("Transfer is successful");
						}
					}
				} else {
					synchronized(dstA) {
						synchronized(srcA) {
							srcA.transferTo(transferAmt, dstA);
							System.out.println("Server: " + transferAmt + " transferred from account "
									+ srcAccountNumber + " to account " + dstAccountNumber);
							ps.println("Transfer is successful");
						}	
					}
				}
			}
			ps.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}