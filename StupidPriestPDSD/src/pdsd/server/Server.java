package pdsd.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import pdsd.beans.Lobby;
import pdsd.beans.User;

public class Server {

	public static HashMap<Integer, User> playersMap = new HashMap<Integer, User>();

	public static HashMap<Integer, Lobby> lobbyMap = new HashMap<Integer, Lobby>();

	public static HashMap<Integer, Socket> clients = new HashMap<Integer, Socket>();

	public static void main(String args[]) throws Exception {
		ServerSocket serverSocket = null;
		boolean listeningSocket = true;
		try {
			serverSocket = new ServerSocket(6792);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 6792");
		}

		while (listeningSocket) {
			Socket clientSocket = serverSocket.accept();
			ClientConnectionServer mini = new ClientConnectionServer(clientSocket);
			mini.start();
		}
		serverSocket.close();
	}

	public static HashMap<Integer, User> getPlayersMap() {
		return playersMap;
	}

	public static void setPlayersMap(HashMap<Integer, User> playersMap) {
		Server.playersMap = playersMap;
	}

	public static HashMap<Integer, Lobby> getLobbyMap() {
		return lobbyMap;
	}

	public static void setLobbyMap(HashMap<Integer, Lobby> lobbyMap) {
		Server.lobbyMap = lobbyMap;
	}

	public static HashMap<Integer, Socket> getClients() {
		return clients;
	}

	public static void setClients(HashMap<Integer, Socket> clients) {
		Server.clients = clients;
	}
}
