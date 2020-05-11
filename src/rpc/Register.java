package rpc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Register
 */
@WebServlet("/register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Register() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Create the database connection instance
		DBConnection connection = DBConnectionFactory.getConnection();

		// Fetch the JSON object (input) from the body of the HTTP request
		JSONObject input = RpcHelper.readJSONObject(request);
		JSONObject obj = new JSONObject();

		try {

			if (!input.has("user_id")) {
				obj.put("register", "user_id is a required field");
				response.setStatus(400);
			} else if (!input.has("password")) {
				obj.put("register", "password is a required field");
				response.setStatus(400);
			} else {
				// Get the userid, password
				String userId = input.getString("user_id");
				;
				String password = input.getString("password");

				// Get the first name and last name if exist
				String firstname = "";
				String lastname = "";

				if (input.has("first_name")) {
					firstname = input.getString("first_name");
				}
				if (input.has("last_name")) {
					lastname = input.getString("last_name");
				}

				if (connection.registerUser(userId, password, firstname, lastname)) {
					obj.put("register", "SUCCESS").put("user_id", userId).put("name", connection.getFullname(userId));
				} else {
					obj.put("register", "This user already exists");
					response.setStatus(400);
				}
			}

			// write the JSON object to the response
			RpcHelper.writeJSONObject(response, obj);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}

	}

}
