package com.ubc.transitalarm.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TransitAlarm implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	public AsyncCallback<String> alarmService;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		
		callGoogleDirectionAPI();
	}
	
	public void callGoogleDirectionAPI()
	{
		alarmService = new AsyncCallback<String>(){
			@Override
			public void onFailure(Throwable caught) {
				System.out.println(caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				
				System.out.println(result);
			}
		};
		String dddd= "dd";
		String cccc= "cc";
		String queryUri = "maps.googleapis.com/maps/api/directions/json?origin="+
				dddd +
				"&destination="+
				cccc+
				"&key=AIzaSyDdbIImonbUzFmDPgfy37d0zBEsrXEo3FI&departure_time=1343641500&mode=transit";
		
		greetingService.greetServer(queryUri, alarmService);
	}
}
