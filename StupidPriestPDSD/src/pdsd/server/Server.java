package pdsd.server;

import java.io.*;
import java.net.*;

public class Server {
	@SuppressWarnings("resource")
	public static void main(String args[]) throws Exception {
		String clientSentence;
		String capitalizedSentence;
		ServerSocket welcomeSocket = new ServerSocket(6789);

		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader readFromClient = new BufferedReader(
					new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream sendToClient = new DataOutputStream(
					connectionSocket.getOutputStream());
			clientSentence = readFromClient.readLine();
			capitalizedSentence = clientSentence.toUpperCase() + '\n';
			sendToClient.writeBytes(capitalizedSentence);
		}
	}
}