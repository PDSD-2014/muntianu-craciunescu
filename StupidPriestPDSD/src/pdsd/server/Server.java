package pdsd.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import pdsd.beans.Game;
import pdsd.beans.Lobby;
import pdsd.beans.User;

public class Server {

	public static ConcurrentHashMap<Integer, User> playersMap = new ConcurrentHashMap<Integer, User>();

	public static ConcurrentHashMap<Integer, Lobby> lobbyMap = new ConcurrentHashMap<Integer, Lobby>();

	public static ConcurrentHashMap<Integer, Socket> clients = new ConcurrentHashMap<Integer, Socket>();
	
	public static ConcurrentHashMap<Integer, Game> games = new ConcurrentHashMap<Integer, Game>();

	public static void main(String args[]) throws Exception {
		ServerSocket serverSocket = null;
		boolean listeningSocket = true;
		try {
			serverSocket = new ServerSocket(6792);
		} catch (IOException e) {
			System.err.println("Couldn't listen on port: 6792");
		}

		while (listeningSocket) {
			Socket clientSocket = serverSocket.accept();
			ClientConnectionServer miniServer = new ClientConnectionServer(
					clientSocket);
			miniServer.start();
		}
		serverSocket.close();
	}

	public static ConcurrentHashMap<Integer, User> getPlayersMap() {
		return playersMap;
	}

	public static void setPlayersMap(ConcurrentHashMap<Integer, User> playersMap) {
		Server.playersMap = playersMap;
	}

	public static ConcurrentHashMap<Integer, Lobby> getLobbyMap() {
		return lobbyMap;
	}

	public static void setLobbyMap(ConcurrentHashMap<Integer, Lobby> lobbyMap) {
		Server.lobbyMap = lobbyMap;
	}

	public static ConcurrentHashMap<Integer, Socket> getClients() {
		return clients;
	}

	public static void setClients(ConcurrentHashMap<Integer, Socket> clients) {
		Server.clients = clients;
	}
}
