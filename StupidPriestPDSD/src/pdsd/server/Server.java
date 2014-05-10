package pdsd.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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

	private static HashMap<Integer, Socket> clients = new HashMap<Integer, Socket>();

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
			if (clientSentence.equals("CONNECT")) {
				response = "CONNECTED_OK";
			} else if (clientSentence.startsWith("LOGIN")) {
				String[] tokens = clientSentence.split("_");
				User user = userDao.getUser(tokens[1], tokens[2]);
				if (user != null && user.getUserId() != null) {
					response = "VALID_AUTH: userid=" + user.getUserId();
					playersMap.put(user.getUserId(), user);
					clients.put(user.getUserId(), connectionSocket);
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
					if (currentLobby == null) {
						currentLobby = lobbyDao.getLobby(lobbyId);
					}
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
			} else if (clientSentence.startsWith("START")) {
				String[] tokens = clientSentence.split("_");
				if (tokens.length < 2) {
					response = "START_NOLOBBY";
				} else {
					Integer lobbyId = Integer.parseInt(tokens[1]);
					startGame(lobbyId);
					response = "START_OK";
					Set<Integer> keys = clients.keySet();
					for (Integer key : keys) {
						Socket sock = clients.get(key);
						DataOutputStream sendBcast = new DataOutputStream(
								sock.getOutputStream());
						PrintWriter pwBcast = new PrintWriter(sendBcast, true);
						pwBcast.println(response);
					}
				}
			} else if (clientSentence.startsWith("GETLOBBIES")) {
				ArrayList<Lobby> allLobbies = lobbyDao.getAllLobbies();
				response = "";
				for (Lobby lobby : allLobbies) {
					response += lobby.getLobbyName() + "-";
					ArrayList<User> lobbyUsers = userDao.getUsersByLobby(lobby
							.getLobbyId());
					for (User user : lobbyUsers) {
						response += user.getUsername() + "_";
					}
					response = response.substring(0, response.length() - 1);
					response += "=";
				}
				response = response.substring(0, response.length() - 1);
			}
			pw.println(response);
		}
	}

	private static void startGame(Integer lobbyId) {
		Lobby lobby = lobbyMap.get(lobbyId);
		if (lobby == null) {
			lobby = lobbyDao.getLobby(lobbyId);
		}

	}
}
