package db.mongodb;

import java.text.ParseException;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

/**
 * This class automatically resets and creates the tables in MongoDB
 * 
 * @author Ryan Shang
 * @date 2020-May-09 1:27:17 PM
 *
 */
public class MongoDBTableCreation {

	/**
	 * Run as Java application to create MongoDB collections with index.
	 * 
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {

		// Step 1, Connect to MongoDB
		MongoClient mongoClient = MongoClients.create(); // Create client
		MongoDatabase db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);

		// Step 2, Remove old collections from the database
		db.getCollection("users").drop();
		db.getCollection("items").drop();

		// Step 3, Create new collections
		IndexOptions indexOptions = new IndexOptions().unique(true);
		db.getCollection("users").createIndex(new Document("user_id", 1), indexOptions); // "1" means ascending order
		db.getCollection("items").createIndex(new Document("item_id", 1), indexOptions);

		// Step 4, Insert fake user data and create index.
		db.getCollection("users").insertOne(
				new Document().append("user_id", "1111").append("password", "3229c1097c00d497a0fd282d586be050")
						.append("first_name", "John").append("last_name", "Smith"));

		// Close the MongoDB connection
		mongoClient.close();
		
		System.out.println("Import is done successfully.");

	}

}
