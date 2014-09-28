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

	private double latitude;
	private double longitude;
	private int queryInterval = 10000; 
	private int refreshInterval = 10000;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// example distance calculations by Alborz
		/*
		 System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, 'M') + " Miles\n");
	      System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, 'K') + " Kilometers\n");
	      System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, 'N') + " Nautical Miles\n");
		System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, 'Meter') + " Meters\n");
*/
		
	
		Geolocation geoposition = Geolocation.getIfSupported();
		if (geoposition == null) {
			Window.alert("Sorry, your browser doesn't support the Geolocation feature!");
		}

		Geolocation.PositionOptions options = new PositionOptions();
		options.setMaximumAge(queryInterval);

		geoposition.getCurrentPosition(new Callback<Position, PositionError>() {
			@Override
			public void onSuccess(Position result) {
				Coordinates coordinates = result.getCoordinates();
				latitude = coordinates.getLatitude();
				longitude = coordinates.getLongitude();
			}

			@Override
			public void onFailure(PositionError reason) {
				Window.alert("Sorry, your location cannot be determined!");
			}
		});	

		Timer refreshTimer = new Timer() {
			public void run() {
				Window.Location.reload();
			}
		};
		refreshTimer.scheduleRepeating(refreshInterval); // Auto refresh every 10 secs
		
		Button refreshButton = new Button ("Refresh");
		refreshButton.getElement().setClassName("btn btn-info");
		refreshButton.addClickHandler(new refreshClickHandler());
	}
	
	class refreshClickHandler implements ClickHandler{
		@Override
		public void onClick(ClickEvent event) {
			Window.Location.reload();				
		}			
	}
	
    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
          dist = dist * 1.609344;}
        else if (unit == 'Q'){
        	  dist = (( dist * 1.609344 ) / 1000);
          }
        else if (unit == 'N') {
          dist = dist * 0.8684;
          }
        return (dist);
      }

      /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
      /*::  This function converts decimal degrees to radians             :*/
      /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
      private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
      }

      /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
      /*::  This function converts radians to decimal degrees             :*/
      /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
      private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
      }}

     
	
		
	

