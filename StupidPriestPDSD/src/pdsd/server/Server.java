package pdsd.server;

import java.io.*;
import java.net.*;
import java.util.HashMap;

import pdsd.beans.Lobby;
import pdsd.beans.User;
import pdsd.dao.LobbyDao;
import pdsd.dao.LobbyDaoImpl;
import pdsd.dao.UserDao;
import pdsd.dao.UserDaoImpl;

public class Server {

	private static UserDao userDao = new UserDaoImpl();

	private static LobbyDao lobbyDao = new LobbyDaoImpl();

	private static HashMap<Integer, User> playersMap = new HashMap<Integer, User>();

	private static HashMap<Integer, Lobby> lobbyMap = new HashMap<Integer, Lobby>();

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
			System.out.println(clientSentence);
			if (clientSentence.startsWith("LOGIN")) {
				String[] tokens = clientSentence.split("_");
				User user = userDao.getUser(tokens[1], tokens[2]);
				if (user != null && user.getUserId() != null) {
					response = "VALID_AUTH";
					playersMap.put(user.getUserId(), user);
				} else {
					response = "INVALID_AUTH";
				}
			} else if (clientSentence.startsWith("REGISTER")) {
				String[] tokens = clientSentence.split("_");
				User user = userDao.registerUser(tokens[1], tokens[2],
						tokens[3]);
				if (user != null && user.getUserId() != null) {
					response = "VALID_REGISTRATION\n";
					response += "USERID:" + user.getUserId() + "_NAME:"
							+ user.getUsername() + "_LOGIN:" + user.getLogin()
							+ "_PASSWORD:" + user.getPassword();
					playersMap.put(user.getUserId(), user);
				} else {
					response = "INVALID_REGISTRATION";
				}
			} else if (clientSentence.startsWith("NEWLOBBY")) {
				String[] tokens = clientSentence.split("_");
				Integer userId = Integer.parseInt(tokens[1]);
				String name = tokens[2];
				Lobby lobby = lobbyDao.createLobby(userId, name);
				if (lobby != null && lobby.getLobbyId() != null) {
					response = "OK_" + "LOBBYID:" + lobby.getLobbyId()
							+ "_LOBBYNAME:" + lobby.getLobbyName();
					lobby.addUser(userId);
					lobbyMap.put(lobby.getLobbyId(), lobby);

				} else {
					response = "ERROR";
				}
			} else if (clientSentence.startsWith("JOINLOBBY")) {
				String[] tokens = clientSentence.split("_");
				Integer userId = Integer.parseInt(tokens[1]);
				Integer lobbyId = Integer.parseInt(tokens[2]);
				boolean ok = lobbyDao.joinLobby(userId, lobbyId);
				if (ok) {
					Lobby currentLobby = lobbyMap.get(lobbyId);
					currentLobby.addUser(userId);
					lobbyMap.put(lobbyId, currentLobby);
					response = "JOIN_OK";
				} else {
					response = "JOIN_NOTOK";
				}
			} else if (clientSentence.startsWith("LEAVELOBBY")) {
				String[] tokens = clientSentence.split("_");
				Integer userId = Integer.parseInt(tokens[1]);
				Integer lobbyId = Integer.parseInt(tokens[2]);
				boolean ok = lobbyDao.leaveLobby(userId, lobbyId);
				if (ok) {
					Lobby currentLobby = lobbyMap.get(lobbyId);
					if (currentLobby != null) {
						currentLobby.removeUser(userId);
					}
					response = "OK";
				} else {
					response = "ERROR";
				}
			}
			pw.println(response);
		}
	}
}