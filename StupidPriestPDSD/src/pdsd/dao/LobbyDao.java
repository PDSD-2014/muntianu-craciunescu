package pdsd.dao;

import pdsd.beans.Lobby;

public interface LobbyDao {

	public Lobby createLobby(Integer userId, String name);

	public boolean joinLobby(Integer userId, String name);

	public boolean joinLobby(Integer userId, Integer lobbyId);

	public boolean leaveLobby(Integer userId, Integer lobbyId);

}
