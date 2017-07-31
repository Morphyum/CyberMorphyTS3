package cybermorphyts3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public final class Util {
	public static String getYoutube(String link) {
		String id;
		if (link.substring(4, 5).toLowerCase().equals("s")) {
			id = link.substring(32);
		} else {
			id = link.substring(31);
		}
		String video = getHTML("https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + id + "&key=AIzaSyDOqZtT01vX5FyWTPzalHIfq-wbOXIju2w");
		JSONObject jsonobject = new JSONObject(video);
		JSONArray items = jsonobject.getJSONArray("items");
		jsonobject = items.getJSONObject(0).getJSONObject("snippet");
		String response = "Oh thanks! A link to the video: " + jsonobject.getString("title") + " uploaded by " + jsonobject.getString("channelTitle");
		return response;

	}

	public static String getHTML(String urlToRead) {
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";

		try {

			url = new URL(urlToRead);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.addRequestProperty("User-Agent", "Mozilla/4.76"); 
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static int diceRoll(int number, int dice) {
		int result=0;
		for(int i = 0; i < number; i++){
			Random ran = new Random();
			result += ran.nextInt(dice)+1;
		}
		return result;
	}
}
