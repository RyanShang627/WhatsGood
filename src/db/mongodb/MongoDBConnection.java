package db.mongodb;

// "import static" allows to direclty use "eq" as a method.
// Otherwise, it has to be called like: "Filters.eq()" when:
// import com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.eq;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterClient;

public class MongoDBConnection implements DBConnection {

	private MongoClient mongoClient;
	private MongoDatabase db;

	public MongoDBConnection() {
		// Connects to local mongodb server.
		mongoClient = MongoClients.create();
		db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
	}

	@Override
	public void close() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {

		if (db == null) {
			return;
		}

		// Use updateOne syntax to set favorite items:
		// Example: db.users.updateOne({"user_id": "1111"}, {$push: {"favorite": {$each:
		// ["abcd", "efgh"]}}})
		// The eq("user_id", userId) is equal to {"user_id": userId}
		db.getCollection("users").updateOne(eq("user_id", userId),
				new Document("$push", new Document("favorite", new Document("$each", itemIds))));
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if (db == null) {
			return;
		}

		// Use updateOne syntax to unset favorite items:
		// Example: db.users.updateOne({"user_id": "1111"}, {$pullAll: {"favorite":
		// ["abcd", "efgh"]}})
		db.getCollection("users").updateOne(eq("user_id", userId),
				new Document("$pullAll", new Document("favorite", itemIds)));

	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		if (db == null) {
			return new HashSet<>();
		}
		Set<String> favoriteItems = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection("users").find(eq("user_id", userId));

		if (iterable.first() != null && iterable.first().containsKey("favorite")) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) iterable.first().get("favorite");
			favoriteItems.addAll(list);
		}

		return favoriteItems;

	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		if (db == null) {
			return new HashSet<>();
		}

		Set<Item> favoriteItems = new HashSet<>();

		// Obtain the item ids based on the user id
		Set<String> itemIds = getFavoriteItemIds(userId);

		// For every item id, do the search
		for (String itemId : itemIds) {
			FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id", itemId));
			if (iterable.first() != null) {

				// Define the first element of the iterable
				Document doc = iterable.first();

				// Build the item object
				ItemBuilder builder = new ItemBuilder();
				builder.setItemId(doc.getString("item_id"));
				builder.setName(doc.getString("name"));
				builder.setAddress(doc.getString("address"));
				builder.setUrl(doc.getString("url"));
				builder.setImageUrl(doc.getString("image_url"));
				builder.setRating(doc.getDouble("rating"));
				builder.setDistance(doc.getDouble("distance"));
				builder.setCategories(getCategories(itemId));

				// Add the built item object to favorite items
				favoriteItems.add(builder.build());
			}
		}

		return favoriteItems;

	}

	@Override
	public Set<String> getCategories(String itemId) {
		if (db == null) {
			return new HashSet<>();
		}
		Set<String> categories = new HashSet<>();

		FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id", itemId));

		if (iterable.first() != null && iterable.first().containsKey("categories")) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) iterable.first().get("categories");
			categories.addAll(list);
		}

		return categories;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		// Create a TicketMasterClient instance
		TicketMasterClient ticketMasterClient = new TicketMasterClient();
		// Obtain the event items
		List<Item> items = ticketMasterClient.search(lat, lon, term);
		// Save all the items to the MongoDB database
		for (Item item : items) {
			saveItem(item);
		}

		return items;
	}

	@Override
	public void saveItem(Item item) {
		if (db == null) {
			return;
		}

		// Obtain the iterable result. Equivalent expression:
		// FindIterable<Document> iterable = db.getCollection("items").find(new
		// Document("item_id",item.getItemId()));
		FindIterable<Document> iterable = db.getCollection("items").find(new Document("item_id", item.getItemId()));

		// Save item to db when this item does not exist
		if (iterable.first() == null) {
			db.getCollection("items")
					.insertOne(new Document().append("item_id", item.getItemId()).append("distance", item.getDistance())
							.append("name", item.getName()).append("address", item.getAddress())
							.append("url", item.getUrl()).append("image_url", item.getImageUrl())
							.append("rating", item.getRating()).append("categories", item.getCategories()));
		}

	}

	@Override
	public String getFullname(String userId) {
		FindIterable<Document> iterable = db.getCollection("users").find(eq("user_id", userId));
		Document document = iterable.first();
		String firstName = document.getString("first_name");
		String lastName = document.getString("last_name");

		return firstName + " " + lastName;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		FindIterable<Document> iterable = db.getCollection("users").find(eq("user_id", userId));
		Document document = iterable.first();

		// Check whether the input password is equal to the saved password
		return document.getString("password").equals(password);
	}

	@Override
	public boolean registerUser(String userId, String password, String firstname, String lastname) {
		// Try to fetch the legal result
		FindIterable<Document> iterable = db.getCollection("users").find(eq("user_id", userId));

		// Insert the new user to MongoDB only if it does not exist
		if (iterable.first() == null) {
			db.getCollection("users").insertOne(new Document().append("first_name", firstname)
					.append("last_name", lastname).append("password", password).append("user_id", userId));
			return true;
		}
		return false;
	}

}
