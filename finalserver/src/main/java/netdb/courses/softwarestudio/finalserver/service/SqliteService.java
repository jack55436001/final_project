package netdb.courses.softwarestudio.finalserver.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteService {

	static final String driver = "org.sqlite.JDBC";
	static final String database = "C://Users/Amy/Desktop/search";
	static int requestCount = 0;

	public static Connection connect() {

		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection("jdbc:sqlite:" + database);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return conn;
	}

	public static void disconnect(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static Statement createStatement(Connection conn) {

		Statement stmt = null;
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return stmt;
	}

	public static PreparedStatement createPreparedStatementReturnKey(
			Connection conn, String sql) {

		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return stmt;
	}

	public static PreparedStatement createPreparedStatement(Connection conn,
			String sql) {

		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return stmt;
	}

	public static ResultSet executeQuery(String query, Statement stmt) {
		ResultSet resultSet = null;

		try {
			if (stmt.execute(query)) {
				resultSet = stmt.getResultSet();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return resultSet;
	}

	public static ResultSet executeQuery(PreparedStatement stmt) {
		ResultSet resultSet = null;
		try {
			resultSet = stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return resultSet;
	}

	public static int executeUpdate(String sql, Statement stmt)
			throws SQLException {
		return stmt.executeUpdate(sql);
	}

	public static long executeUpdateReturnKey(String sql, Statement stmt) {

		long key = -1;

		try {
			stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

			ResultSet rs = stmt.getGeneratedKeys();
			rs.first();
			key = rs.getLong(1);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return key;
	}

	public static int executeUpdate(PreparedStatement stmt) {
		try {
			return stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static long executeUpdateReturnKey(PreparedStatement stmt) {

		long key = -1;
		try {
			stmt.executeUpdate();

			ResultSet rs = stmt.getGeneratedKeys();
			rs.first();
			key = rs.getLong(1);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return key;
	}

	public static int executeUpdateReturnRowCount(String sql, Statement stmt) {
		int updatedRowCount = 0;

		try {
			updatedRowCount = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return updatedRowCount;
	}

	public static void executeBatch(PreparedStatement stmt) {
		try {
			stmt.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
