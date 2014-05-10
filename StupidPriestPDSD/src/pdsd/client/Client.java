package pdsd.client;

import java.io.*;
import java.net.*;

public class Client {
	public static void main(String argv[]) throws Exception {
		String command = "";
		String response  = "";
		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));
		do {
			Socket clientSocket = new Socket("localhost", 6792);
			DataOutputStream serverOutputStream = new DataOutputStream(
					clientSocket.getOutputStream());
			PrintWriter pw = new PrintWriter(serverOutputStream, true);
			BufferedReader serverInputStream = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			if( response != null && !response.equals("WAITING")){
				command = input.readLine();
			}
			pw.println(command);
			pw.flush();
			response = serverInputStream.readLine();
			System.out.println(response);
			clientSocket.close();
		} while (!command.equals("end"));
	}
}
