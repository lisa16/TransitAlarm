package com.ubc.transitalarm.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.ubc.transitalarm.client.GreetingService;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
GreetingService {

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid. 

		StringBuilder uriBuilder= new StringBuilder(input);
		String result = makeJSONQuery(uriBuilder);

		try{
			JSONObject arr= new JSONObject(result);
			JSONArray routesArray = arr.getJSONArray("routes");
			JSONObject firstObject = routesArray.getJSONObject(0);
			JSONArray legsArray = firstObject.getJSONArray("legs");
			JSONObject transition=legsArray.getJSONObject(0);
			JSONArray secondObject = transition.getJSONArray("steps");
			for(int i1=0; i1<secondObject.length(); i1++){
				JSONObject stepsObject = secondObject.getJSONObject(i1);
				String travelMode = stepsObject.getString("travel_mode");
				if(travelMode.equals("TRANSIT"))
				{
					JSONObject transitDetail=stepsObject.getJSONObject("transit_details");
					JSONObject stopArrival=transitDetail.getJSONObject("arrival_stop");
					String stopName=stopArrival.getString("name");
					JSONObject stopLocation=stopArrival.getJSONObject("location");
					double locationLatitude=stopLocation.getDouble("lat");
					double locationLongtitude=stopLocation.getDouble("lng");
					System.out.println(locationLatitude);
					System.out.println(locationLongtitude);
					System.out.println(stopName);
				}
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return result;

		// Escape data from the client to avoid cross-site script vulnerabilities.
		//		input = escapeHtml(input);
		//		userAgent = escapeHtml(userAgent);
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
	private String makeJSONQuery(StringBuilder urlBuilder) {
		HttpURLConnection client = null;
		try {
			URL url = new URL("https://" + urlBuilder.toString());
			client = (HttpURLConnection) url.openConnection();
			client.setRequestProperty("accept", "application/json");
			client.setConnectTimeout(30000);
			client.setReadTimeout(30000);
			client.connect();
			BufferedReader br;
			InputStream err = client.getErrorStream();
			if( err != null )
				br = new BufferedReader(new InputStreamReader(err));
			else {
				InputStream in = client.getInputStream();
				br = new BufferedReader(new InputStreamReader(in));
			}
			String returnString = "";
			String line;
			while((line=br.readLine())!=null)
			{
				returnString += line;
			}
			return returnString;
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			return "ERROR";
		} finally {
			if(client != null)
				client.disconnect();
		}
	}
}

