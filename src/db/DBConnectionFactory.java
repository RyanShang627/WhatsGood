package db;

/**
 * This class helps create different database instances
 * 
 * @author Ryan Shang
 * @date 2020-May-07 10:07:02 PM
 *
 */
public class DBConnectionFactory {
	// This should change based on the pipeline.
	private static final String DEFAULT_DB = "mysql";

	/**
	 * This method obtains the database connection instance based on the name of the
	 * database
	 * 
	 * @param db Name of the database
	 * @return dbConnection
	 * @throws IllegalArgumentException
	 */
	public static DBConnection getConnection(String db) throws IllegalArgumentException {
		switch (db) {
		case "mysql":
			// return new MySQLConnection();
			return null;
		case "mongodb":
			// return new MongoDBConnection();
			return null;
		default:
			throw new IllegalArgumentException("Invalid db:" + db);
		}
	}

	/**
	 * Default getConnection method when the name of the database is not provided
	 * 
	 * @return dbConnection
	 */
	public static DBConnection getConnection() {
		return getConnection(DEFAULT_DB);
	}

}
