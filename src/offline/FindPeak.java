package offline;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoDatabase;

import db.mongodb.MongoDBUtil;

/**
 * This class uses the MapReduce feature provided by MongoDB to find the peak
 * use time period of the WhatsGood Application.
 * 
 * @author Ryan Shang
 *
 */
public class FindPeak {
	private static List<LocalTime> buckets = initBuckets();

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		// Initialization
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);

		// Construct mapper function in Javascript function format

		// Note: This is defined by Mongo DB. If you want to use the map reduce feature
		// in MongoDB, you have to pass JS method. Example:

		// function() {
		// if (this.url.startswith("/Titan")) {
		// emit(this.time.substring(0, 5), 1);
		// }
		// }

		// In this case, we need to write the above JS function as string.
		StringBuilder sb = new StringBuilder();
		sb.append("function() {");
		sb.append(" if (this.url.startsWith(\"/Titan\")) {");
		sb.append(" emit(this.time.substring(0, 5), 1); }");
		sb.append("}");
		String map = sb.toString();

		// Construct a reducer function
		// key is time, and value is the number of request
		String reduce = "function(key, values) {return Array.sum(values)} ";

		// Do the MapReduce operation
		MapReduceIterable<Document> results = db.getCollection("logs").mapReduce(map, reduce);

		// Reduce result 10:10 => 2, 10:11 => 5
		// 10:10 => [10:00, 10:15]
		// 10:11 => [10:00, 10:15]
		// [10:00, 10:15] => 2 + 4 = 6

		// Iterate the result of the MapReduce
		// Save total count to each bucket
		Map<String, Double> timeMap = new HashMap<>();
		results.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String time = findBucket(document.getString("_id")); // 10:10 => [10:00, 10:15]
				Double count = document.getDouble("value");
				if (timeMap.containsKey(time)) {
					timeMap.put(time, timeMap.get(time) + count);
				} else {
					// save to map
					timeMap.put(time, count);
				}
			}
		});

		// Need a sorting here
		// Sort by count of requests within the time interval
		List<Map.Entry<String, Double>> timeList = new ArrayList<Map.Entry<String, Double>>(timeMap.entrySet());
		Collections.sort(timeList, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				return Double.compare(o2.getValue(), o1.getValue());
			}
		});

		printList(timeList);
		mongoClient.close();

	}

	private static void printList(List<Map.Entry<String, Double>> timeList) {
		for (Map.Entry<String, Double> entry : timeList) {
			System.out.println("time: " + entry.getKey() + " count: " + entry.getValue());
		}
	}

	/**
	 * This method sets the time interval to 15 minutes, which means for every
	 * single day, it has 24 * 4 = 96 intervals. In this case, we can accumulate the
	 * number of requests within 15 minutes
	 * 
	 * @return The time period/interval
	 */
	private static List<LocalTime> initBuckets() {
		List<LocalTime> buckets = new ArrayList<>();
		LocalTime time = LocalTime.parse("00:00");
		for (int i = 0; i < 96; ++i) {
			buckets.add(time);
			time = time.plusMinutes(15);
		}
		return buckets;
	}

	/**
	 * This method helps determine which time period does the current time belong
	 * to. It uses LocalTime.isAfter/isBefore to compare to objects
	 * 
	 * @param currentTime
	 * @return The time interval that the current time belongs to
	 */
	private static String findBucket(String currentTime) {
		LocalTime curr = LocalTime.parse(currentTime);
		int left = 0, right = buckets.size() - 1;
		while (left < right - 1) {
			int mid = (left + right) / 2;
			if (buckets.get(mid).isAfter(curr)) {
				right = mid - 1;
			} else {
				left = mid;
			}
		}
		if (buckets.get(right).isAfter(curr)) {
			return buckets.get(left).toString();
		}
		return buckets.get(right).toString();
	}
}
