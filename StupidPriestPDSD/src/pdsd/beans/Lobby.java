package pdsd.beans;

import java.util.ArrayList;

public class Lobby {

	private ArrayList<Integer> users;

	private String lobbyName;

	private Integer lobbyId;

	public Lobby() {
		super();
	}

	public Lobby(Integer userId1, String lobbyName, Integer lobbyId) {
		super();
		users = new ArrayList<Integer>();
		users.add(userId1);
		this.lobbyName = lobbyName;
		this.lobbyId = lobbyId;
	}

	public void addUser(Integer userId) {
		if (users == null) {
			users = new ArrayList<Integer>();
		}
		if (!users.contains(userId)) {
			users.add(userId);
		}
	}

	public boolean removeUser(Integer userId) {
		if (users != null) {
			return users.remove(userId);
		}
		return true;
	}

	public ArrayList<Integer> getUsers() {
		return users;
	}

	public String getLobbyName() {
		return lobbyName;
	}

	public void setLobbyName(String lobbyName) {
		this.lobbyName = lobbyName;
	}

	public Integer getLobbyId() {
		return lobbyId;
	}

	public void setLobbyId(Integer lobbyId) {
		this.lobbyId = lobbyId;
	}
}
