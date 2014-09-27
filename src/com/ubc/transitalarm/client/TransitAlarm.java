package com.ubc.transitalarm.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	final HTML alarmPageHTML = new HTML();
	final HTML destinationPageHTML = new HTML();
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() 
	{
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
}
