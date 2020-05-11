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
	// private fields of an event item
	private String itemId;
	private String name;
	private double rating;
	private String address;
	private Set<String> categories;
	private String imageUrl;
	private String url;
	private double distance;

	/**
	 * This private constructor uses builder pattern (ItemBuilder) to instantiate
	 * 
	 * @param builder An instance of ItemBuilder class
	 * @return Item An instance of Item class
	 */
	private Item(ItemBuilder builder) {
		// Assign the fields from builder to item
		// Note: "builder.xxx" is the member of the static class of Item class
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.distance = builder.distance;
	}

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

	/**
	 * This method converts the JAVA Item object to a JSON Object
	 * 
	 * @return JSONObject The event item in JSON Object format
	 */
	public JSONObject toJSONObject() {
		// Create an empty JSON object
		JSONObject obj = new JSONObject();

		try {
			// assign the fields to the JSON object
			obj.put("item_id", itemId);
			obj.put("name", name);
			obj.put("rating", rating);
			obj.put("address", address);
			obj.put("categories", new JSONArray(categories)); // ["", ""]
			obj.put("image_url", imageUrl);
			obj.put("url", url);
			obj.put("distance", distance);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * This class is the internal builder of the Item class. It separates the
	 * construction of the Item object from its representation so that the same
	 * construction process can create different representations of the event item.
	 * 
	 * Example to instantiate Item class:
	 * 
	 * Item item = new ItemBuilder().setItemId().setName().set...().build();
	 * 
	 * @author Ryan Shang
	 * @date 2020-May-07 12:13:05 PM
	 *
	 */
	public static class ItemBuilder {

		// The same fields of Item class
		private String itemId;
		private String name;
		private double rating;
		private String address;
		private Set<String> categories;
		private String imageUrl;
		private String url;
		private double distance;

		/**
		 * This method sets up the id of the item
		 * 
		 * @param itemId The id of the item
		 * @return ItemBuilder
		 */
		public ItemBuilder setItemId(String itemId) {
			this.itemId = itemId;
			return this;
		}

		/**
		 * This method sets up the name of the item
		 * 
		 * @param name The name of the item
		 * @return ItemBuilder
		 */
		public ItemBuilder setName(String name) {
			this.name = name;
			return this;
		}

		/**
		 * This method sets up the rating of the item
		 * 
		 * @param rating The rating of the item
		 * @return ItemBuilder
		 */
		public ItemBuilder setRating(double rating) {
			this.rating = rating;
			return this;
		}

		/**
		 * This method sets up the address of the item
		 * 
		 * @param address The address of the item
		 * @return ItemBuilder
		 */
		public ItemBuilder setAddress(String address) {
			this.address = address;
			return this;
		}

		/**
		 * This method sets up the categories of the item
		 * 
		 * @param categories The categories of the item
		 * @return ItemBuilder
		 */
		public ItemBuilder setCategories(Set<String> categories) {
			this.categories = categories;
			return this;
		}

		/**
		 * This method sets up the url of the image of the item
		 * 
		 * @param imageUrl The url of the image of the item
		 * @return ItemBuilder
		 */
		public ItemBuilder setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}

		/**
		 * This method sets up the url of the item
		 * 
		 * @param url The url of the item
		 * @return ItemBuilder
		 */
		public ItemBuilder setUrl(String url) {
			this.url = url;
			return this;
		}

		/**
		 * This method sets up the distance of the item
		 * 
		 * @param distance The distance of the item
		 * @return ItemBuilder
		 */
		public ItemBuilder setDistance(double distance) {
			this.distance = distance;
			return this;
		}

		/**
		 * This method builds an instance of the Item class
		 * 
		 * @return Item The Item class
		 */
		public Item build() {
			return new Item(this);
		}
	}

}
