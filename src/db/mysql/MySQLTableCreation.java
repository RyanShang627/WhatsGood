package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;

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

			// close the connection finally
			connection.close();
			System.out.println("Import done successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
