package com.ubc.transitalarm.client;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Geolocation.PositionOptions;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.Position.Coordinates;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

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
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	final HTML alarmPageHTML = new HTML();
	final HTML destinationPageHTML = new HTML();
	
	private int queryInterval = 10000; 
	private int refreshInterval = 10000;
	Geolocation geoposition; 
	Geolocation.PositionOptions options;
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() 
	{
		refreshPosition();

		destinationPageHTML.setHTML("		<div class=\"row\">\r\n" + 
				"			<h3>Source:</h3>\r\n" + 
				"			<input class=\"form-control\" placeHolder=\"Enter Starting Location\"\r\n" + 
				"				type=\"text\"></input> <input type=\"checkbox\"> Current\r\n" + 
				"			Location\r\n" + 
				"		</div>\r\n" + 
				"\r\n" + 
				"		<div class=\"row\">\r\n" + 
				"			<h3>Dest:</h3>\r\n" + 
				"			<input class=\"form-control\" placeHolder=\"Enter Destination Location\"\r\n" + 
				"				type=\"text\"></input>\r\n" + 
				"		</div>\r\n" + 
				"\r\n" + 
				"		<button type=\"button\" class=\"btn btn-lg btn-primary\">Search</button>");
		
		alarmPageHTML.setHTML("		<div class=\"row\">\r\n" + 
				"			<button type=\"button\" class=\"btn btn-lg btn-primary\">Stop Alarm</button>\r\n" + 
				"		</div>\r\n" + 
				"\r\n" + 
				"		<div class=\"row\">\r\n" + 
				"			<h3>Transfer stop name 1:</h3>\r\n" + 
				"			<h4>Distance remaining <font color=\"red\">500m</font></h4>\r\n" + 
				"		</div>\r\n" + 
				"\r\n" + 
				"		<div class=\"row\">\r\n" + 
				"			<h3>Transfer stop name 2:</h3>\r\n" + 
				"			<h4>Distance remaining 10km</h4>\r\n" + 
				"		</div>\r\n" + 
				"\r\n" + 
				"		<div class=\"row\">\r\n" + 
				"			<h3>Transfer stop name 3:</h3>\r\n" + 
				"			<h4>Distance remaining 15km</h4>\r\n" + 
				"		</div>\r\n" + 
				"\r\n" + 
				"		<div class=\"row\">\r\n" + 
				"			<button type=\"button\" class=\"btn btn-lg btn-primary\">Refresh Now</button>\r\n" + 
				"			<h4>refreshing in 40 seconds...</h4>\r\n" + 
				"		</div>");
		
		
		loadDestinationPage();
//		loadAlarmPage();

		Timer refreshTimer = new Timer() {
			public void run() {
				refreshPosition();
			}
		};
		refreshTimer.scheduleRepeating(refreshInterval); // Auto refresh every 10 secs

		Button refreshButton = new Button ("Refresh");
		refreshButton.getElement().setClassName("btn btn-info");
		refreshButton.addClickHandler(new refreshClickHandler());
		RootPanel.get().add(refreshButton);
		
	}
	
	public void loadDestinationPage()
	{
		Button searchButton = new Button("Search");
		searchButton.getElement().setClassName("btn btn-lg btn-primary");
		searchButton.addClickHandler(new SearchButtonClickHandler());
		
		RootPanel.get("searchDestinationField").add(destinationPageHTML);
		RootPanel.get("searchDestinationField").add(searchButton);
	}
	
	public void loadAlarmPage()
	{
		System.out.println("Loading alarm page");
		RootPanel.get("alarmPageField").add(alarmPageHTML);
	}
	
class SearchButtonClickHandler implements ClickHandler {

		public void onClick(ClickEvent event) 
		{

			RootPanel.get("searchDestinationField").clear();
			
			loadAlarmPage();
		}
	}


	private void refreshPosition() {
		geoposition = Geolocation.getIfSupported();
		if (geoposition == null) {
			Window.alert("Sorry, your browser doesn't support the Geolocation feature!");
		}

		options = new PositionOptions();
		options.setMaximumAge(queryInterval);

		geoposition.getCurrentPosition(new Callback<Position, PositionError>() {
			@Override
			public void onSuccess(Position result) {
				Coordinates coordinates = result.getCoordinates();
				latitude = coordinates.getLatitude();
				longitude = coordinates.getLongitude();
				System.out.println(latitude);
				System.out.println(longitude);
			}

			@Override
			public void onFailure(PositionError reason) {
				Window.alert("Sorry, your location cannot be determined!");
			}
		}, options);

	}

	class refreshClickHandler implements ClickHandler{
		@Override
		public void onClick(ClickEvent event) {
			refreshPosition();
		}			
	}
}
