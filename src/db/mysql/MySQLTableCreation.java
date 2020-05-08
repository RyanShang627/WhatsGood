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

			// Step 2 Drop four tables in case they exist
			Statement statement = connection.createStatement(); // Create SQL statement instance
			String sql = "DROP TABLE IF EXISTS categories";
			statement.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS history";
			statement.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS items";
			statement.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS users";
			statement.executeUpdate(sql);

			// Step 3 Create four new tables: items, users, categories, history
			sql = "CREATE TABLE items (" + "item_id VARCHAR(255) NOT NULL," + "name VARCHAR(255)," + "rating FLOAT,"
					+ "address VARCHAR(255)," + "image_url VARCHAR(255)," + "url VARCHAR(255)," + "distance FLOAT,"
					+ "PRIMARY KEY (item_id)" + ")";
			statement.executeUpdate(sql);

			sql = "CREATE TABLE users (" + "user_id VARCHAR(255) NOT NULL," + "password VARCHAR(255) NOT NULL,"
					+ "first_name VARCHAR(255)," + "last_name VARCHAR(255)," + "PRIMARY KEY (user_id)" + ")";
			statement.executeUpdate(sql);

			sql = "CREATE TABLE categories (" + "item_id VARCHAR(255) NOT NULL," + "category VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (item_id, category)," + "FOREIGN KEY (item_id) REFERENCES items(item_id)" + ")";
			statement.executeUpdate(sql);

			sql = "CREATE TABLE history (" + "user_id VARCHAR(255) NOT NULL," + "item_id VARCHAR(255) NOT NULL,"
					+ "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "PRIMARY KEY (user_id, item_id)," + "FOREIGN KEY (user_id) REFERENCES users(user_id),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id)" + ")";
			statement.executeUpdate(sql);

			// Step 4: insert fake user 1111/3229c1097c00d497a0fd282d586be050
			sql = "INSERT INTO users VALUES('1111', '3229c1097c00d497a0fd282d586be050', 'John', 'Smith')";
			statement.executeUpdate(sql);

			// close the connection in the end
			connection.close();
			System.out.println("Import done successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
