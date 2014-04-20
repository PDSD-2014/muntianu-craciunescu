package pdsd.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionManager {

	private static final String sqlDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static final String sqlUrl = "jdbc:sqlserver://127.0.0.1\\SQLEXPRESS:1433;databaseName=pdsd";
	private static final String sqlUser = "sa";
	private static final String sqlPass = "root";

	private ConnectionManager() {
	}

	private static class InstanceHolder {
		private static final ConnectionManager instance = new ConnectionManager();
	}

	public static ConnectionManager getInstance() {
		return InstanceHolder.instance;
	}

	public static void close(Connection conn, Statement ps, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if (ps != null) {
				ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Connection getSqlConnection() {
		Connection connection = null;
		try {
			Class.forName(sqlDriver);
			connection = DriverManager.getConnection(sqlUrl, sqlUser, sqlPass);
			connection
					.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

}
