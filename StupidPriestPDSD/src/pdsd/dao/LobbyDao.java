package pdsd.dao;

import java.util.ArrayList;

import pdsd.beans.Lobby;

public interface LobbyDao {

	public Lobby createLobby(Integer userId, String name);

	public Integer joinLobby(Integer userId, String name);

	public boolean joinLobby(Integer userId, Integer lobbyId);

	public boolean leaveLobby(Integer userId, Integer lobbyId);

	public Lobby getLobby(Integer lobbyId);

	public Integer getLobbyByName(String name);

	public ArrayList<Lobby> getAllLobbies();

}
