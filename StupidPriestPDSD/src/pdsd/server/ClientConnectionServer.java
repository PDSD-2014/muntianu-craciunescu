package pdsd.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import pdsd.beans.Card;
import pdsd.beans.Game;
import pdsd.beans.Lobby;
import pdsd.beans.Player;
import pdsd.beans.User;
import pdsd.dao.LobbyDao;
import pdsd.dao.LobbyDaoImpl;
import pdsd.dao.UserDao;
import pdsd.dao.UserDaoImpl;
import pdsd.service.Util;

public class ClientConnectionServer extends Thread {

	private static UserDao userDao = new UserDaoImpl();

	private static LobbyDao lobbyDao = new LobbyDaoImpl();

	private Socket connectionSocket = null;

	public ClientConnectionServer(Socket socket) {

		super("MiniServer");
		this.connectionSocket = socket;

	}

	public void run() {
		try {
			String response = "";
			BufferedReader readFromClient = new BufferedReader(
					new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream sendToClient = new DataOutputStream(
					connectionSocket.getOutputStream());
			PrintWriter pw = new PrintWriter(sendToClient, true);
			String clientSentence = readFromClient.readLine();
			System.out.println(clientSentence);
			if (clientSentence.startsWith("LOGIN")) {
				String[] tokens = clientSentence.split("_");
				User user = userDao.getUser(tokens[1], tokens[2]);
				if (user != null && user.getUserId() != null) {
					response = "VALID_AUTH: userid=" + user.getUserId();
					Server.playersMap.put(user.getUserId(), user);
					Server.clients.put(user.getUserId(), connectionSocket);
				} else {
					response = "INVALID_AUTH";
				}
			} else if (clientSentence.startsWith("REGISTER")) {
				String[] tokens = clientSentence.split("_");
				User user = userDao.registerUser(tokens[1], tokens[1],
						tokens[2]);
				if (user != null && user.getUserId() != null) {
					response = "VALID_REGISTRATION\n";
					response += "USERID:" + user.getUserId() + "_NAME:"
							+ user.getUsername() + "_LOGIN:" + user.getLogin()
							+ "_PASSWORD:" + user.getPassword();
					Server.playersMap.put(user.getUserId(), user);
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
					response += "_USERS:";
					ArrayList<User> lobbyUsers = userDao.getUsersByLobby(lobby
							.getLobbyId());
					for (User user : lobbyUsers) {
						response += user.getUsername() + "_";
					}
					response = response.substring(0, response.length() - 1);
					Server.lobbyMap.put(lobby.getLobbyId(), lobby);

				} else {
					response = "ERROR";
				}
			} else if (clientSentence.startsWith("JOINLOBBY")) {
				String[] tokens = clientSentence.split("_");
				Integer userId = Integer.parseInt(tokens[1]);
				String lobbyName = tokens[2];
				Integer lobbyId = lobbyDao.joinLobby(userId, lobbyName);
				if (lobbyId != null && lobbyId.intValue() != 0) {
					Lobby currentLobby = Server.lobbyMap.get(lobbyId);
					if (currentLobby == null) {
						currentLobby = lobbyDao.getLobby(lobbyId);
					}
					currentLobby.addUser(userId);
					Server.lobbyMap.put(lobbyId, currentLobby);
					response = "JOIN_OK:";
					ArrayList<User> lobbyUsers = userDao
							.getUsersByLobby(currentLobby.getLobbyId());
					response += "_USERS:";
					for (User user : lobbyUsers) {
						response += user.getUsername() + "_";
					}
					response = response.substring(0, response.length() - 1);
				} else {
					response = "JOIN_NOTOK";
				}
			} else if (clientSentence.startsWith("LEAVELOBBY")) {
				String[] tokens = clientSentence.split("_");
				Integer userId = Integer.parseInt(tokens[1]);
				String lobbyName = tokens[2];
				Integer lobbyId = lobbyDao.getLobbyByName(lobbyName);
				boolean ok = lobbyDao.leaveLobby(userId, lobbyId);
				if (ok) {
					Lobby currentLobby = Server.lobbyMap.get(lobbyId);
					if (currentLobby != null) {
						currentLobby.removeUser(userId);
					}
					response = "OK";
				} else {
					response = "ERROR";
				}
			} else if (clientSentence.startsWith("START")) {
				String[] tokens = clientSentence.split("_");
				if (tokens.length < 3) {
					response = "START_NOLOBBY";
				} else {
					String lobbyName = tokens[1];
					Integer userId = Integer.parseInt(tokens[2]);
					Server.clients.put(userId, connectionSocket);
					boolean started = startGame(lobbyName, userId);
					Integer lobbyId = lobbyDao.getLobbyByName(lobbyName);
					if (started) {
						response = "START_OK:CARDS";
					} else {
						response = "WAITING";
					}
					if (started) {
						Game game = new Game();
						game.setLobbyId(lobbyId);
						game.setStarted(true);
						ArrayList<Integer> keys = Server.lobbyMap.get(lobbyId)
								.getUsers();
						System.out.println(keys);
						ArrayList<ArrayList<Card>> shuffled = Util.shuffle();
						ArrayList<Player> players = new ArrayList<Player>();
						int index = 0;
						for (index = 0; index < 4; index++) {
							Player player = new Player();
							player.setCards(shuffled.get(index));
							if (keys.size() > index) {
								player.setUserId(keys.get(index));
							} else {
								player.setUserId(0);
							}
							players.add(player);
						}
						for (index = 0; index < 4; index++) {
							if (index == 3) {
								players.get(index)
										.setNextPlayer(players.get(0));
							} else {
								players.get(index).setNextPlayer(
										players.get(index + 1));
							}
						}
						game.setPlayers(players);
						Server.games.put(lobbyId, game);
						for (Player player : players) {
							if (player.getUserId() != null
									&& player.getUserId().intValue() == userId
											.intValue()) {
								for (Card card : player.getCards()) {
									response += "_" + card.getNumber() + "_"
											+ card.getColor();
								}
								if (player.getCards().size() == 5) {
									response += "_YES";
								} else {
									response += "_NO";
								}
							}
						}
						for (Player player : players) {
							if (player.getUserId() == null
									|| player.getUserId().intValue() == userId
											.intValue()) {
								continue;
							}
							if (player.getUserId() != null
									&& player.getUserId().intValue() != 0) {
								Socket sock = Server.clients.get(player
										.getUserId());
								System.out.println(player.getUserId());
								DataOutputStream sendToPlayer = new DataOutputStream(
										sock.getOutputStream());
								PrintWriter prw = new PrintWriter(sendToPlayer,
										true);
								String resp = "START_OK:CARDS";
								for (Card card : player.getCards()) {
									resp += "_" + card.getNumber() + "_"
											+ card.getColor();
								}
								if (player.getCards().size() == 5) {
									resp += "_YES";
								} else {
									resp += "_NO";
								}
								prw.println(resp);
							}
						}
						System.out.println(response);
					}
				}
			} else if (clientSentence.startsWith("GETLOBBIES")) {
				ArrayList<Lobby> allLobbies = lobbyDao.getAllLobbies();
				response = "";
				for (Lobby lobby : allLobbies) {
					ArrayList<User> lobbyUsers = userDao.getUsersByLobby(lobby
							.getLobbyId());
					if (lobbyUsers == null || lobbyUsers.size() == 0) {
						continue;
					}
					response += lobby.getLobbyName() + "-";

					for (User user : lobbyUsers) {
						response += user.getUsername() + "_";
					}
					response = response.substring(0, response.length() - 1);
					response += "=";
				}
				if (allLobbies.size() != 0) {
					response = response.substring(0, response.length() - 1);
					System.out.println(response);
				}
			}
			pw.println(response);
			//pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static boolean startGame(String lobbyName, Integer userId) {
		Integer lobbyId = lobbyDao.getLobbyByName(lobbyName);
		Lobby lobby = Server.lobbyMap.get(lobbyId);
		if (lobby == null) {
			lobby = lobbyDao.getLobby(lobbyId);
			Server.lobbyMap.put(lobbyId, lobby);
		}
		ArrayList<Integer> inactiveUsers = new ArrayList<Integer>();
		for (Integer user : lobby.getUsers()) {
			if (Server.clients.get(user) == null) {
				lobbyDao.leaveLobby(user, lobbyId);
				inactiveUsers.add(user);
			}
		}
		for (Integer user : inactiveUsers) {
			lobby.removeUser(user);
		}
		lobby.getUsersStarted().add(userId);
		if (lobby.getUsersStarted().size() == lobby.getUsers().size()) {
			return true;
		}
		return false;
	}

}
