package rpc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchItem() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

// 		***************** HTML *************
//		response.setContentType("text/html");
//		PrintWriter writer = response.getWriter();
//		if (request.getParameter("username") != null) {
//			String username = request.getParameter("username"); 
//			
//			writer.println("<html><body>");
//			writer.println("<h1>Hello " + username + "</h1>");
//			writer.println("</body></html>");
//			writer.close();
//		}

//		***************  JSON   *************
//		response.setContentType("application/json");
//		PrintWriter writer = response.getWriter();
//		if (request.getParameter("username") != null) {
//			String username = request.getParameter("username");
//			JSONObject obj = new JSONObject();
//			try {
//				obj.put("username", username);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			writer.print(obj);
//		}
//		writer.close();

//		***************  JSON ARRAY   *************
//		response.setContentType("application/json");
//		PrintWriter writer = response.getWriter();

//		JSONArray array = new JSONArray();
//		try {
//			array.put(new JSONObject().put("username", "abcde"));
//		} catch (JSONException e1) {
//			e1.printStackTrace();
//		}
//		try {
//			array.put(new JSONObject().put("username", "1234"));
//		} catch (JSONException e1) {
//			e1.printStackTrace();
//		}

//		writer.print(array);
//		writer.close();
//	}

//		************   using RpcHelper  ***************
//		JSONArray array = new JSONArray();
//		try {
//			array.put(new JSONObject().put("username", "abcd").put("address", "San Francisco").put("time", "01/01/2017"));
//			array.put(new JSONObject().put("username", "1234").put("address", "San Jose").put("time", "01/02/2017"));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		RpcHelper.writeJsonArray(response, array);
//		
//	}

		// Allow access only if the session exists
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403); // 403 means "Authorization error" <=> No access to the service
			return;
		}

		String userId = session.getAttribute("user_id").toString(); // Get userId from the http session

		// Get parameters from the url of the HTTP request
		double latitude = Double.parseDouble(request.getParameter("lat"));
		double longitude = Double.parseDouble(request.getParameter("lon"));
		String term = request.getParameter("term"); // Term can be empty or null.

		// Instantiate Database connection
		DBConnection connection = DBConnectionFactory.getConnection();

		// Get the event items searching results from database connection and write them
		// to HTTP response
		try {
			List<Item> items = connection.searchItems(latitude, longitude, term);
			Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);

			JSONArray array = new JSONArray();
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				obj.put("favorite", favoritedItemIds.contains(item.getItemId()));
				array.put(obj);

			}
			RpcHelper.writeJsonArray(response, array);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close the db connection
			connection.close();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
