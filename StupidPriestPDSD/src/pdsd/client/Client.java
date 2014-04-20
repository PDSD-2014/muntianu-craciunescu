package pdsd.client;

import java.io.*;
import java.net.*;

public class Client {
	public static void main(String argv[]) throws Exception {
		String command;
		String response;
		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));

		Socket clientSocket = new Socket("localhost", 6789);
		DataOutputStream serverOutputStream = new DataOutputStream(
				clientSocket.getOutputStream());
		BufferedReader serverInputStream = new BufferedReader(
				new InputStreamReader(clientSocket.getInputStream()));
		do {
			command = input.readLine();
			serverOutputStream.writeBytes(command + '\n');
			response = serverInputStream.readLine();
			System.out.println(response);
			clientSocket.close();
		} while (!command.equalsIgnoreCase("end"));
	}
}
