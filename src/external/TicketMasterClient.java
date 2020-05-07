package external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

/**
 * This class connects to the TicketMaster API, sends query, and fetches event
 * data.
 * 
 * @author Ryan Shang
 * @date 2020-May-07 11:27:42 AM
 */
public class TicketMasterClient {
	private static final String HOST = "https://app.ticketmaster.com";
	private static final String ENDPOINT = "/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = "event";
	private static final String API_KEY = "IMn4J2NotwtAU62YUidAUuQQH1l2QsCi";

	/**
	 * This method search the events based on the given latitude, longitude, and
	 * keyword. Finally it returns a JSON array of events.
	 * 
	 * @param lat     Latitude of the event
	 * 
	 * @param lon     Longitude of the event
	 * 
	 * @param keyword Keyword of the event
	 * 
	 * @return JSONArray The search results
	 */
	public List<Item> search(double lat, double lon, String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}

		// Encode the keyword input
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8"); // "Ryan Shang" => "Ryan%20Shang"
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// Build the query for TicketMaster API
		String query = String.format("apikey=%s&latlong=%s,%s&keyword=%s&radius=%s", API_KEY, lat, lon, keyword, 50);
		// Build the complete URL
		String url = HOST + ENDPOINT + "?" + query;

		StringBuilder responseBody = new StringBuilder();

		try {
			// Create a URLConnection instance that represents a connection to the remote
			// object referred to by the URL. The HttpUrlConnection class allows us to
			// perform basic HTTP requests without the use of any additional libraries.
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");

			// Get the status code from an HTTP response message.
			int responseCode = connection.getResponseCode();
			System.out.println("Sending requests to url: " + url);
			System.out.println("Response code: " + responseCode);

			// return empty JSON array when the request fails
			if (responseCode != 200) {
				return new ArrayList<>();
			}

			// Create a BufferedReader to help read text from a character-input stream.
			// Provide for the efficient reading of characters, arrays, and lines.
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			// Append the input stream to the response data
			String line;
			while ((line = reader.readLine()) != null) {
				responseBody.append(line);
			}

			reader.close(); // close the reader after the reading operation

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			// Extract events array only
			JSONObject obj = new JSONObject(responseBody.toString());
			if (!obj.isNull("_embedded")) {
				JSONObject embedded = obj.getJSONObject("_embedded");
				return getItemList(embedded.getJSONArray("events"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return new ArrayList<>();

	}

	// Convert JSONArray to a list of item objects.
	/**
	 * This method converts a list of events from a JSON array to a list of Item
	 * instances
	 * 
	 * @param events The list of event
	 * @return List<Item> The list of Item objects
	 * @throws JSONException
	 */
	private List<Item> getItemList(JSONArray events) throws JSONException {
		// Define an empty list of Item objects
		List<Item> itemList = new ArrayList<>();

		for (int i = 0; i < events.length(); ++i) {
			// Get the JSON object
			JSONObject event = events.getJSONObject(i);

			// Instantiate ItemBuilder class
			ItemBuilder builder = new ItemBuilder();

			// Set the fields of the ItemBuilder object
			if (!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			if (!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			if (!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			if (!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
			}
			builder.setAddress(getAddress(event));
			builder.setCategories(getCategories(event));
			builder.setImageUrl(getImageUrl(event));

			// Add the successfully built item to the item list
			itemList.add(builder.build());
		}

		return itemList;
	}

	/**
	 * This helper method obtains the address of an event
	 * 
	 * @param event Event in JSON Object format
	 * @return String The address of the event
	 * @throws JSONException
	 */
	private String getAddress(JSONObject event) throws JSONException {
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			if (!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				for (int i = 0; i < venues.length(); ++i) {
					JSONObject venue = venues.getJSONObject(i);
					StringBuilder builder = new StringBuilder();
					if (!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						if (!address.isNull("line1")) {
							builder.append(address.getString("line1"));
						}

						if (!address.isNull("line2")) {
							builder.append(",");
							builder.append(address.getString("line2"));
						}

						if (!address.isNull("line3")) {
							builder.append(",");
							builder.append(address.getString("line3"));
						}
					}

					if (!venue.isNull("city")) {
						JSONObject city = venue.getJSONObject("city");
						builder.append(",");
						builder.append(city.getString("name"));
					}

					String result = builder.toString();
					if (!result.isEmpty()) {
						return result;
					}
				}
			}
		}
		return "";
	}

	/**
	 * This helper method obtains the URL of the image of an event
	 * 
	 * @param event Event in JSON Object format
	 * @return String The URL of the image of the event
	 * @throws JSONException
	 */
	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull("images")) {
			JSONArray array = event.getJSONArray("images");
			for (int i = 0; i < array.length(); i++) {
				JSONObject image = array.getJSONObject(i);
				if (!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		return "";
	}

	/**
	 * This helper method obtains the categories of an event
	 * 
	 * @param event Event in JSON Object format
	 * @return Set<String> The categories of the event
	 * @throws JSONException
	 */
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		if (!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			for (int i = 0; i < classifications.length(); ++i) {
				JSONObject classification = classifications.getJSONObject(i);
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					if (!segment.isNull("name")) {
						categories.add(segment.getString("name"));
					}
				}
			}
		}
		return categories;
	}

	/**
	 * Main entry to test TicketMasterClient.
	 */
	public static void main(String[] args) {
		TicketMasterClient client = new TicketMasterClient();
		List<Item> events = client.search(37.38, -122.08, null);

		for (Item event : events) {
			System.out.println(event.toJSONObject());
		}

	}

}
