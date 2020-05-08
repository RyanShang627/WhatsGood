package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/history")
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ItemHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// Fetch user id from request parameters
		String userId = request.getParameter("user_id");
		JSONArray array = new JSONArray();

		DBConnection conn = DBConnectionFactory.getConnection();
		try {
			Set<Item> items = conn.getFavoriteItems(userId);
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				obj.append("favorite", true);
				array.put(obj);
			}

			RpcHelper.writeJsonArray(response, array);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}

	}

	/**
	 * The body of HTTP POST request looks like: { 'user_id': '1111', 'favorite': [
	 * 'item_id1', 'item_id2' ] }
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Create the db connection instance
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			JSONObject input = RpcHelper.readJSONObject(request);

			// Fetch parameters from the JSON object
			String userId = input.getString("user_id");
			JSONArray array = input.getJSONArray("favorite");

			// Prepare the list of item ids
			List<String> itemIds = new ArrayList<>();
			for (int i = 0; i < array.length(); ++i) {
				itemIds.add(array.getString(i));
			}

			// Set favorite items
			connection.setFavoriteItems(userId, itemIds);

			// write the result back to response
			RpcHelper.writeJSONObject(response, new JSONObject().put("result", "SUCCESS"));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// close the db connection
			connection.close();
		}

	}

	/**
	 * The body of HTTP DELETE request looks like: { 'user_id': '1111', 'favorite':
	 * [ 'item_id1', 'item_id2' ] }
	 * 
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Create the db connection instance
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			JSONObject input = RpcHelper.readJSONObject(request);

			// Fetch parameters from the JSON object
			String userId = input.getString("user_id");
			JSONArray array = input.getJSONArray("favorite");

			// Prepare the list of item ids
			List<String> itemIds = new ArrayList<>();
			for (int i = 0; i < array.length(); ++i) {
				itemIds.add(array.getString(i));
			}

			// Set favorite items
			connection.unsetFavoriteItems(userId, itemIds);

			// write the result back to response
			RpcHelper.writeJSONObject(response, new JSONObject().put("result", "SUCCESS"));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// close the db connection
			connection.close();
		}

	}

}
