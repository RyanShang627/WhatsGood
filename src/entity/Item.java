package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class defines the structure of an event item holding data fields that
 * are needed in the project
 * 
 * @author Ryan Shang
 * @date 2020-May-07 11:37:09 AM
 *
 */
public class Item {
	// fields of an event item
	private String itemId;
	private String name;
	private double rating;
	private String address;
	private Set<String> categories;
	private String imageUrl;
	private String url;
	private double distance;

	/**
	 * This method obtains the id of the event item
	 * 
	 * @return String The id of the event item
	 */
	public String getItemId() {
		return itemId;
	}

	/**
	 * This method obtains the name of the event item
	 * 
	 * @return String The name of the event item
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method obtains the rating of the event item
	 * 
	 * @return double The rating of the event item
	 */
	public double getRating() {
		return rating;
	}

	/**
	 * This method obtains the address of the event item
	 * 
	 * @return String The address of the event item
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * This method obtains the categories of the event item. Since the category may
	 * not be only one, the method returns a set of category
	 * 
	 * @return Set<String> The categories of the event item
	 */
	public Set<String> getCategories() {
		return categories;
	}

	/**
	 * This method obtains the URL of the image of the event item
	 * 
	 * @return String The URL of the image of the event item
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * This method obtains the URL of the event item
	 * 
	 * @return String The URL of the event item
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * This method obtains the distance of the event item
	 * 
	 * @return double The distance of the event item
	 */
	public double getDistance() {
		return distance;
	}

	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("item_id", itemId);
			obj.put("name", name);
			obj.put("rating", rating);
			obj.put("address", address);
			obj.put("categories", new JSONArray(categories));
			obj.put("image_url", imageUrl);
			obj.put("url", url);
			obj.put("distance", distance);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

}
