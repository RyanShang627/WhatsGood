package offline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;

import db.mongodb.MongoDBUtil;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * <p>
 * This class helps purify the original log file of the server and save it into
 * mongodb database.
 * 
 * <p>
 * The format of log is: 131.147.144.197 - - [06/Sep/2017:00:39:57 +0000]
 * "GET/Titan/history?user_id=1111 HTTP/ 1.1" 200 11410.
 * 
 * <p>
 * This class filters the log and extracts the request url and time. By
 * rebuilding the log line, this class saves all the lines of log file into the
 * database.
 * 
 * <p>
 * The new log line looks like: { "_id" : ObjectId("5ebca97892d17c3a838777bb"),
 * "ip" : "131.147.144.197", "date" : "06/Sep/2017", "time" : "00:39:57",
 * "method" : "GET", "url" : "/Titan/history?user_id=1111", "status" : "200" }
 * 
 * @author Ryan Shang
 *
 */
public class Purify {
	public static void main(String[] args) {
		// Connect to mongoDB
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);

		// Note: the log file can be found and downloaded from '/opt/tomcat/logs' folder
		// in the Virtual machine on the AWS EC2.
		// Switch to your own path of the tomcat log file
		String filePath = new File("").getAbsolutePath();
		// The test log file is stored in the root\\log
		String fileName = filePath.concat("\\logs\\tomcat_log.txt");
		
		

		try {
			// Drop the log collection first
			db.getCollection("logs").drop();

			// Read the log file line by line
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				// Sample input:
				// 73.223.210.212 - - [19/Aug/2017:22:00:24 +0000] "GET
				// /Titan/history?user_id=1111 HTTP/1.1" 200 11410
				List<String> values = Arrays.asList(line.split(" ")); // split by space

				// Extract different parts from the string
				String ip = values.size() > 0 ? values.get(0) : null;
				String timestamp = values.size() > 3 ? values.get(3) : null;
				String method = values.size() > 5 ? values.get(5) : null;
				String url = values.size() > 6 ? values.get(6) : null;
				String status = values.size() > 8 ? values.get(8) : null;

				// Note: In our project, date is not important, as we only want to know what
				// time slot is the peak time. So we are only interested in time rather than
				// date. Additionally, we are interested in the url

				// Explanation of regular expression:
				// ' \\[ ' the first '\' means the second '\' is a normal '\', while the '\['
				// means a normal square bracket
				// ' . ' means any character
				// ' + ' means the character before it can appear more than once
				// ' ? ' means match the string as short as possible
				// ' \\[(.+?): ' means stop at the first ':' after the '['
				// Two sets of parenthesis, (.+?) and (.+) means two groups
				// They will be saved as two groups once successfully matched
				Pattern pattern = Pattern.compile("\\[(.+?):(.+)"); // regular expression
				Matcher matcher = pattern.matcher(timestamp);
				matcher.find();

				// insert the line to logs collection
				db.getCollection("logs")
						.insertOne(new Document().append("ip", ip).append("date", matcher.group(1))
								.append("time", matcher.group(2)).append("method", method.substring(1))
								.append("url", url).append("status", status));
			}
			System.out.println("Logs have been saved to database successfully");

			// Close the reader and mongo client
			// Note: Type "db.logs.find()" command in the command line to check the logs
			// which are newly saved to mongodb
			bufferedReader.close();
			mongoClient.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
