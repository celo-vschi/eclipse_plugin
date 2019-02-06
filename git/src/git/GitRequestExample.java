package git;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GitRequestExample {

	private static final String URL_ALL_COMMITS = "https://api.github.com/repos/celo-vschi/eclipse_plugin/commits";
	private static final String URL_COMMIT = "https://api.github.com/repos/celo-vschi/eclipse_plugin/commits/";

	private static final String USERNAME = "celovschiandrei@gmail.com";
	private static final String PASSWORD = "sugipulamuielacacat";
	

	public static void main(String[] args) throws IOException {
		git();
	}

	private static void git() {
		try {
			String commits = sendGET(URL_ALL_COMMITS);
			parseAllCommits(commits);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

	}

	private static void parseAllCommits(String commits) throws ParseException {
		JSONParser jsonParser = new JSONParser();
		JSONArray jsonArray = (JSONArray) (jsonParser.parse(commits));

		for (int i=0; i<jsonArray.size(); i++) {
			JSONObject commit = (JSONObject) jsonArray.get(i);
			String sha = (String) commit.get("sha");

			parseCommit(sha);
		}

	}

	private static void parseCommit(String sha) {
		try {
			String commit = sendGET(URL_COMMIT + sha);

			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(commit);
			JSONArray array = (JSONArray) object.get("files");

			for (int i = 0; i < array.size(); i++) {
				object = (JSONObject) array.get(i);
				String filename = (String) object.get("filename");
				System.out.println("\t" + filename);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	private static String sendGET(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");

		String encoded = Base64.getEncoder().encodeToString((USERNAME + ":" + PASSWORD).getBytes(StandardCharsets.UTF_8));
		con.setRequestProperty("Authorization", "Basic " + encoded);
		con.setRequestProperty("Accept", "application/vnd.github.VERSION.raw");

		int responseCode = con.getResponseCode();
		System.out.println("GET | " + con.getURL() + " :: " + responseCode);

		BufferedReader in = null;
		if (responseCode == HttpURLConnection.HTTP_OK) {
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} else {
			in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		}

		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return response.toString();
	}

}