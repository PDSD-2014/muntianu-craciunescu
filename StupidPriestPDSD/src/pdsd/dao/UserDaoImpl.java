package pdsd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import pdsd.beans.User;
import pdsd.service.ConnectionManager;

public class UserDaoImpl implements UserDao {

	@Override
	public User getUser(String login, String password) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		StringBuffer sb = new StringBuffer();
		User result = null;
		try {
			sb.append("SELECT * FROM Users WHERE Login = ? AND Password = ?");
			conn = ConnectionManager.getSqlConnection();
			ps = conn.prepareStatement(sb.toString());
			ps.setString(1, login);
			ps.setString(2, password);
			rs = ps.executeQuery();
			if (rs.next()) {
				Integer id = rs.getInt("UserId");
				String name = rs.getString("Name");
				result = new User(id, name, password, login);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, ps, rs);
		}
		return result;
	}

	@Override
	public User registerUser(String name, String login, String password) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		StringBuffer sb = new StringBuffer();
		User result = null;
		try {
			sb.append("INSERT INTO Users (Login, Password, Name, CreateDate) VALUES (?, ?, ?, getdate())");
			conn = ConnectionManager.getSqlConnection();
			ps = conn.prepareStatement(sb.toString(),
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, login);
			ps.setString(2, password);
			ps.setString(3, name);
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				Integer id = rs.getInt(1);
				result = new User(id, name, password, login);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, ps, rs);
		}
		return result;
	}

	@Override
	public boolean isUserRegistered(String login) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		StringBuffer sb = new StringBuffer();
		boolean result = false;
		try {
			sb.append("SELECT * FROM Users WHERE Login = ? ");
			conn = ConnectionManager.getSqlConnection();
			ps = conn.prepareStatement(sb.toString());
			ps.setString(1, login);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, ps, rs);
		}
		return result;
	}

}
