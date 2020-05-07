package entity;

import java.util.Set;

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

	public String getItemId() {
		return itemId;
	}

	public String getName() {
		return name;
	}

	public double getRating() {
		return rating;
	}

	public String getAddress() {
		return address;
	}

	public Set<String> getCategories() {
		return categories;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getUrl() {
		return url;
	}

	public double getDistance() {
		return distance;
	}

}
