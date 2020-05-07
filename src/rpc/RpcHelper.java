package rpc;

import java.io.IOException;
import java.io.PrintWriter;

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
	 * 
	 * @param array    This is the JSON array to be added to HTTP response
	 * 
	 * @return Nothing
	 * 
	 * @exception IOException On input error
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
	 * 
	 * @param obj      This is the JSON array to be added to HTTP response
	 * 
	 * @return Nothing
	 * 
	 * @exception IOException On input error
	 */
	public static void writeJSONObject(HttpServletResponse response, JSONObject obj) throws IOException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();
		out.print(obj);
		out.close();
	}

}
