package pdsd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import pdsd.beans.Lobby;
import pdsd.service.ConnectionManager;

public class LobbyDaoImpl implements LobbyDao {

	@Override
	public Lobby createLobby(Integer userId, String name) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		StringBuffer sb = new StringBuffer();
		Lobby result = null;
		try {
			sb.append("INSERT INTO Lobby (LobbyName, User1, Active, GameEnded, CreateDate) VALUES (?, ?, ?, ?, getdate())");
			conn = ConnectionManager.getSqlConnection();
			ps = conn.prepareStatement(sb.toString(),
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, name);
			ps.setInt(2, userId);
			ps.setBoolean(3, false);
			ps.setBoolean(4, false);
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				Integer id = rs.getInt(1);
				result = new Lobby(userId, name, id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, ps, rs);
		}
		return result;
	}

	@Override
	public Integer joinLobby(Integer userId, String name) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		StringBuffer sb = new StringBuffer();
		Integer result = 0;
		try {
			sb.append("SELECT * FROM Lobby WHERE LobbyName = ?");
			conn = ConnectionManager.getSqlConnection();
			ps = conn.prepareStatement(sb.toString());
			ps.setString(1, name);
			rs = ps.executeQuery();
			String updateField = "";
			if (rs.next()) {
				Integer id1 = rs.getInt("User1");
				Integer id2 = rs.getInt("User2");
				Integer id3 = rs.getInt("User3");
				Integer id4 = rs.getInt("User4");
				result = rs.getInt("LobbyId");

				if (id1 != null && id1.intValue() != 0 && id2 != null
						&& id2.intValue() != 0 && id3 != null
						&& id3.intValue() != 0 && id4 != null
						&& id4.intValue() != 0) {
					result = 0;
				} else {
					if (id1 == null || id1.intValue() == 0) {
						updateField = "User1";
					} else if (id2 == null || id2.intValue() == 0) {
						updateField = "User2";
					} else if (id3 == null || id3.intValue() == 0) {
						updateField = "User3";
					} else {
						updateField = "User4";
					}
					updateLobby(updateField, name, userId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, ps, rs);
		}
		return result;
	}

	private void updateLobby(String updateField, String name, Integer userId) {
		PreparedStatement ps = null;
		Connection conn = null;
		StringBuffer sb = new StringBuffer();
		try {
			conn = ConnectionManager.getSqlConnection();
			sb.append("UPDATE Lobby SET " + updateField
					+ " = ? WHERE LobbyName = ?");
			ps = conn.prepareStatement(sb.toString());
			ps.setInt(1, userId);
			ps.setString(2, name);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, ps, null);
		}
	}

	private void updateLobby(String updateField, Integer lobbyId, Integer userId) {
		PreparedStatement ps = null;
		Connection conn = null;
		StringBuffer sb = new StringBuffer();
		try {
			conn = ConnectionManager.getSqlConnection();
			sb.append("UPDATE Lobby SET " + updateField
					+ " = ? WHERE LobbyId = ?");
			ps = conn.prepareStatement(sb.toString());
			ps.setInt(1, userId);
			ps.setInt(2, lobbyId);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, ps, null);
		}
	}

	@Override
	public boolean joinLobby(Integer userId, Integer lobbyId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		StringBuffer sb = new StringBuffer();
		boolean result = true;
		try {
			sb.append("SELECT * FROM Lobby WHERE LobbyId = ?");
			conn = ConnectionManager.getSqlConnection();
			ps = conn.prepareStatement(sb.toString());
			ps.setInt(1, lobbyId);
			rs = ps.executeQuery();
			String updateField = "";
			if (rs.next()) {
				Integer id1 = rs.getInt("User1");
				Integer id2 = rs.getInt("User2");
				Integer id3 = rs.getInt("User3");
				Integer id4 = rs.getInt("User4");
				boolean active = rs.getBoolean("Active");
				boolean gameEnded = rs.getBoolean("GameEnded");
				if (gameEnded || !active) {
					result = false;
				}
				if (id1 != null && id1.intValue() != 0 && id2 != null
						&& id2.intValue() != 0 && id3 != null
						&& id3.intValue() != 0 && id4 != null
						&& id4.intValue() != 0) {
					result = false;
				} else {
					if (id1 == null || id1.intValue() == 0) {
						updateField = "User1";
					} else if (id2 == null || id2.intValue() == 0) {
						updateField = "User2";
					} else if (id3 == null || id3.intValue() == 0) {
						updateField = "User3";
					} else {
						updateField = "User4";
					}
					if (id1.intValue() != userId.intValue()
							&& id2.intValue() != userId.intValue()
							&& id3.intValue() != userId.intValue()
							&& id4.intValue() != userId.intValue()) {
						updateLobby(updateField, lobbyId, userId);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, ps, rs);
		}
		return result;
	}

	@Override
	public boolean leaveLobby(Integer userId, Integer lobbyId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		StringBuffer sb = new StringBuffer();
		boolean result = true;
		String updateField = "";
		try {
			conn = ConnectionManager.getSqlConnection();
			sb.append("SELECT * FROM Lobby WHERE LobbyId = ?");
			ps = conn.prepareStatement(sb.toString());
			ps.setInt(1, lobbyId);
			rs = ps.executeQuery();
			if (rs.next()) {
				Integer user1 = rs.getInt("User1");
				Integer user2 = rs.getInt("User2");
				Integer user3 = rs.getInt("User3");
				if (user1 != null && user1.intValue() == userId.intValue()) {
					updateField = "User1";
				} else if (user2 != null
						&& user2.intValue() == userId.intValue()) {
					updateField = "User2";
				} else if (user3 != null
						&& user3.intValue() == userId.intValue()) {
					updateField = "User3";
				} else {
					updateField = "User4";
				}
				updateLobby(updateField, lobbyId, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, ps, rs);
		}
		return result;
	}

	@Override
	public Lobby getLobby(Integer lobbyId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		StringBuffer sb = new StringBuffer();
		Lobby result = new Lobby();
		try {
			conn = ConnectionManager.getSqlConnection();
			sb.append("SELECT * FROM Lobby WHERE LobbyId = ?");
			ps = conn.prepareStatement(sb.toString());
			ps.setInt(1, lobbyId);
			rs = ps.executeQuery();
			if (rs.next()) {
				Integer user1 = rs.getInt("User1");
				Integer user2 = rs.getInt("User2");
				Integer user3 = rs.getInt("User3");
				Integer user4 = rs.getInt("User4");
				String name = rs.getString("LobbyName");
				result.setLobbyId(lobbyId);
				result.setLobbyName(name);
				result.addUser(user1);
				result.addUser(user2);
				result.addUser(user3);
				result.addUser(user4);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, ps, rs);
		}
		return result;
	}

	@Override
	public Integer getLobbyByName(String lobbyName) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		StringBuffer sb = new StringBuffer();
		Integer result = 0;
		try {
			conn = ConnectionManager.getSqlConnection();
			sb.append("SELECT * FROM Lobby WHERE LobbyName = ?");
			ps = conn.prepareStatement(sb.toString());
			ps.setString(1, lobbyName);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getInt("LobbyId");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, ps, rs);
		}
		return result;
	}

	@Override
	public ArrayList<Lobby> getAllLobbies() {
		ArrayList<Lobby> allLobbies = new ArrayList<Lobby>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = ConnectionManager.getSqlConnection();
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM Lobby WHERE Active = 0 AND GameEnded = 0");
			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				Lobby lobby = new Lobby();
				Integer lobbyId = rs.getInt("LobbyId");
				Integer user1 = rs.getInt("User1");
				Integer user2 = rs.getInt("User2");
				Integer user3 = rs.getInt("User3");
				Integer user4 = rs.getInt("User4");
				String name = rs.getString("LobbyName");
				Boolean ended = rs.getBoolean("GameEnded");
				lobby.setLobbyId(lobbyId);
				lobby.setLobbyName(name);
				lobby.addUser(user1);
				lobby.addUser(user2);
				lobby.addUser(user3);
				lobby.addUser(user4);
				if (!ended) {
					allLobbies.add(lobby);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, ps, rs);
		}
		return allLobbies;
	}

}
