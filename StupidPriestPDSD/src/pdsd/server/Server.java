package pdsd.server;

import java.io.*;
import java.net.*;

import pdsd.beans.User;
import pdsd.dao.UserDao;
import pdsd.dao.UserDaoImpl;

public class Server {

	private static UserDao userDao = new UserDaoImpl();

	@SuppressWarnings("resource")
	public static void main(String args[]) throws Exception {
		String clientSentence;
		ServerSocket welcomeSocket = new ServerSocket(6792);
		String response = "";

		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader readFromClient = new BufferedReader(
					new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream sendToClient = new DataOutputStream(
					connectionSocket.getOutputStream());
			PrintWriter pw = new PrintWriter(sendToClient, true);
			clientSentence = readFromClient.readLine();
			if (clientSentence.startsWith("LOGIN")) {
				String[] tokens = clientSentence.split("_");
				User user = userDao.getUser(tokens[1], tokens[2]);
				if (user != null && user.getUserId() != null) {
					response = "VALID_AUTH";
				} else {
					response = "INVALID_AUTH";
				}
			}
			pw.println(response);
		}
	}
}