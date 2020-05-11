package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class provides several rpc helper methods
 * 
 * @author Ryan Shang
 * @date 2020-May-07 11:32:29 AM
 */
public class RpcHelper {

	/**
	 * Writes a JSON Array to HTTP response
	 * 
	 * @param response This is the HTTP response
	 * @param array    This is the JSON array to be added to HTTP response
	 * @return Nothing
	 * @throws IOException
	 */
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();
		out.print(array);
		out.close();
	}

	/**
	 * Writes a JSON Object to HTTP response
	 * 
	 * @param response This is the HTTP response
	 * @param obj      This is the JSON array to be added to HTTP response
	 * @return Nothing
	 * @throws IOException
	 */
	public static void writeJSONObject(HttpServletResponse response, JSONObject obj) throws IOException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();
		out.print(obj);
		out.close();
	}

	/**
	 * This method parses a JSON Object from HTTP request's body
	 * 
	 * @param request The HTTP request
	 * @return JSONObject
	 */
	public static JSONObject readJSONObject(HttpServletRequest request) {
		StringBuilder sBuilder = new StringBuilder();
		try (BufferedReader reader = request.getReader()) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				sBuilder.append(line);
			}
			return new JSONObject(sBuilder.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new JSONObject();
	}

}
