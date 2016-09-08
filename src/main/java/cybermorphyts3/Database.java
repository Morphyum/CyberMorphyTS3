package cybermorphyts3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	public static void initialize() {
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:morphspeak.db");
			System.out.println("Opened database successfully");

			Statement stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS MORPHIS " + "(USER           TEXT PRIMARY KEY    NOT NULL, " + " POINTS         INT     NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();
			stmt = c.createStatement();
			sql = "CREATE TABLE IF NOT EXISTS BEER " + "(USER           TEXT PRIMARY KEY   NOT NULL, " + " COUNT          INT     NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int raiseBeerCount(String invokerUniqueId) {
		Connection c = null;
		int count = 1;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:morphspeak.db");
			System.out.println("Opened database successfully");
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM BEER WHERE USER LIKE '" + invokerUniqueId + "';");
			if (!rs.next()) {
				stmt.executeUpdate("INSERT INTO BEER (USER,COUNT) " + "VALUES ('" + invokerUniqueId + "', 1 );");
			} else {
				count = rs.getInt("count");
				count++;
				stmt.executeUpdate("UPDATE BEER set COUNT = " + count + " WHERE USER LIKE '" + invokerUniqueId + "';");
			}
			rs.close();
			stmt.close();
			c.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;

	}

	public static int lowerBeerCount(String invokerUniqueId) {
		Connection c = null;
		int count = 1;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:morphspeak.db");
			System.out.println("Opened database successfully");
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM BEER WHERE USER LIKE '" + invokerUniqueId + "';");
			if (!rs.next()) {
				stmt.executeUpdate("INSERT INTO BEER (USER,COUNT) " + "VALUES ('" + invokerUniqueId + "', 0 );");
			} else {
				count = rs.getInt("count");
				if (count != 0) {
					count--;
				}
				stmt.executeUpdate("UPDATE BEER set COUNT = " + count + " WHERE USER LIKE '" + invokerUniqueId + "';");
			}
			rs.close();
			stmt.close();
			c.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	public static void resetBeerCount(String invokerUniqueId) {
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:morphspeak.db");
			System.out.println("Opened database successfully");
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM BEER WHERE USER LIKE '" + invokerUniqueId + "';");
			if (!rs.next()) {
				stmt.executeUpdate("INSERT INTO BEER (USER,COUNT) " + "VALUES ('" + invokerUniqueId + "', 0 );");
			} else {
				stmt.executeUpdate("UPDATE BEER set COUNT = 0 WHERE USER LIKE '" + invokerUniqueId + "';");
			}
			rs.close();
			stmt.close();
			c.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int userBeerCount(String invokerUniqueId) {
		Connection c = null;
		int count = 0;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:morphspeak.db");
			System.out.println("Opened database successfully");
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM BEER WHERE USER LIKE '" + invokerUniqueId + "';");
			if (!rs.next()) {
				stmt.executeUpdate("INSERT INTO BEER (USER,COUNT) " + "VALUES ('" + invokerUniqueId + "', 0 );");
			} else {
				count = rs.getInt("count");
			}
			rs.close();
			stmt.close();
			c.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
}
