package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterClient;

/**
 * This class implements DBConnection interface and provides a set of behaviors
 * to talk to MySQL database
 * 
 * @author Ryan Shang
 * @date 2020-May-08 12:03:40 PM
 *
 */
public class MySQLConnection implements DBConnection {

	private Connection conn;

	/**
	 * This class constructor helps connect to MySQL database
	 */
	public MySQLConnection() {
		try {
			// Allow the JVM to recognize and load the MySQL jdbc driver
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			// Build MySQL connection
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		if (conn == null) {
			System.err.println("MySQL connection failed");
			return;
		}

		try {
			// Insert user's id and item's id into history table
			String sql = "INSERT IGNORE INTO history(user_id, item_id) VALUES (?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, userId);

			for (String itemId : itemIds) {
				ps.setString(2, itemId);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if (conn == null) {
			System.err.println("MySQL connection failed");
			return;
		}

		try {
			// Delete the row from history table based on userid and itemid
			String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			for (String itemId : itemIds) {
				ps.setString(2, itemId);
				ps.execute();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		if (conn == null) {
			return new HashSet<>();
		}

		// prepare the set of favorite items
		Set<Item> favoriteItems = new HashSet<>();
		Set<String> itemIds = getFavoriteItemIds(userId);

		try {
			// Fetch the rows from items table based on the item id
			String sql = "SELECT * FROM items WHERE item_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (String itemId : itemIds) {
				stmt.setString(1, itemId);

				ResultSet rs = stmt.executeQuery();

				ItemBuilder builder = new ItemBuilder();

				while (rs.next()) {
					// Build the favorite item
					builder.setItemId(rs.getString("item_id"));
					builder.setName(rs.getString("name"));
					builder.setAddress(rs.getString("address"));
					builder.setImageUrl(rs.getString("image_url"));
					builder.setUrl(rs.getString("url"));
					builder.setCategories(getCategories(itemId));
					builder.setDistance(rs.getDouble("distance"));
					builder.setRating(rs.getDouble("rating"));

					// Add the favorite item to the result set
					favoriteItems.add(builder.build());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return favoriteItems;

	}

	@Override
	public Set<String> getCategories(String itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		// Create a TicketMasterClient instance
		TicketMasterClient ticketMasterClient = new TicketMasterClient();
		// Obtain the event items
		List<Item> items = ticketMasterClient.search(lat, lon, term);
		// Save all the items to the MySQL db
		for (Item item : items) {
			saveItem(item);
		}

		return items;

	}

	@Override
	public void saveItem(Item item) {
		if (conn == null) {
			System.err.println("MySQL database connection failed");
			return;
		}

		try {
			// Define a SQL template that insert values into items table
			String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)";

			// Prepare the SQL statement
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, item.getItemId());
			ps.setString(2, item.getName());
			ps.setDouble(3, item.getRating());
			ps.setString(4, item.getAddress());
			ps.setString(5, item.getImageUrl());
			ps.setString(6, item.getUrl());
			ps.setDouble(7, item.getDistance());

			// Execute the SQL statement
			ps.execute();

			// Redefine the SQL template to insert values into categories table
			sql = "INSERT IGNORE INTO categories VALUES(?, ?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, item.getItemId());

			// iterate the categories of item and set the statement, then execute
			for (String category : item.getCategories()) {
				ps.setString(2, category);
				ps.execute();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getFullname(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		return false;
	}

}
