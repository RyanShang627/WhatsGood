package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * This class automatically resets the tables in database
 * 
 * @author Ryan Shang
 * @date 2020-May-08 11:10:30 AM
 *
 */
public class MySQLTableCreation {
	// Run this as Java application to reset db schema.
	public static void main(String[] args) {
		try {
			// Step 1 Connect to MySQL.
			System.out.println("Connecting to " + MySQLDBUtil.URL);
			// Allow the JVM to recognize and load the MySQL jdbc driver
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			// Build MySQL connection
			Connection connection = DriverManager.getConnection(MySQLDBUtil.URL);
			if (connection == null) {
				return;
			}

			// Step 2 Drop tables in case they exist
			Statement statement = connection.createStatement(); // Create SQL statement instance
			String sql = "DROP TABLE IF EXISTS categories";
			statement.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS history";
			statement.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS items";
			statement.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS users";
			statement.executeUpdate(sql);

			// close the connection finally
			connection.close();
			System.out.println("Import done successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
