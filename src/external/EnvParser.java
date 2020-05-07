package external;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Helper class to parse the environment variables
 */
public class EnvParser {
	/**
	 * This method obtain the API key of TicketMaster from environment variable
	 * @return String The API key
	 */
	public static String getTicketMasterKey() {
		String TICKETMASTER_API_KEY = Dotenv.load().get("TICKETMASTER_API_KEY");
		return TICKETMASTER_API_KEY;
	}

}
