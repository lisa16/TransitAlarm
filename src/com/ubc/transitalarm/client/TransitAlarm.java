package com.ubc.transitalarm.client;

import java.util.HashSet;
import java.util.List;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.Position.Coordinates;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.http.client.URL;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.ubc.transitalarm.shared.DestinationLocations;
import com.ubc.transitalarm.shared.FieldVerifier;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TransitAlarm implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	public AsyncCallback<DestinationLocations> alarmService;

	//===============Final Strings==============
	private final String SEARCH_PAGE_DIV_ID = "searchDestinationField";
	private final String ALARM_PAGE_DIV_ID = "alarmPageField";
	private final int REFRESH_INTERVAL = 10999;
	private final String LOADING_GIF_DIV_ID = "loadingGif";
	private final int ALARM_RING_DISTANCE_METERS = 600;
	private final String ALARM_BUTTON_DIV_ID = "alarmBtn";
	private final boolean IS_TEST_MODE = false;
	//==========================================
	
	//================Widgets===================
	private final HTML _alarmPageHTML = new HTML();
	private final Button _alarmBtn = Button.wrap(Document.get().getElementById("alarmBtn"));
	private final Button _refreshBtn = Button.wrap(Document.get().getElementById("refreshBtn"));;
	private final TextBox _startingInput = TextBox.wrap(DOM.getElementById("startingInput"));
	private final SimpleCheckBox _currentLocationCheckBox = SimpleCheckBox.wrap(DOM.getElementById("currentLocationCheckBox"));
	private final TextBox _destinationInput = TextBox.wrap(DOM.getElementById("destinationInput"));
	private final Button _searchButton = Button.wrap(DOM.getElementById("searchBtn"));
	//==========================================

	private Geolocation _geoposition; 
	private DestinationLocations _destinationLocations;

	private double _latitude;
	private double _longitude;
	private double _accuracy;
	
	private HashSet<Integer> _alarmActivationList = new HashSet<Integer>();

	private long _currentTime = System.currentTimeMillis();
	private long _endTime = _currentTime + REFRESH_INTERVAL;
	
	private Audio _alarmAudio;
	{
		_alarmAudio = Audio.createIfSupported();
		_alarmAudio.setSrc("audio/minion_fire_alarm.mp3");
		_alarmAudio.setAutoplay(false);
		_alarmAudio.setLoop(true);
		_alarmAudio.load();
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() 
	{
		RefreshCurrentGPSLocation();
		LoadSearchPage();
	}
	
	public void LoadSearchPage()
	{
		System.out.println("Loading search page");

		_searchButton.addClickHandler(new SearchButtonClickHandler());
		_currentLocationCheckBox.addClickHandler(new CurrentLocationCheckBoxClickHandler());
		
		DOM.getElementById(SEARCH_PAGE_DIV_ID).getStyle().setDisplay(Display.INLINE);
		DOM.getElementById(ALARM_PAGE_DIV_ID).getStyle().setDisplay(Display.NONE);
	}
	public void LoadAlarmPage()
	{
		System.out.println("Loading alarm page");
		
		DOM.getElementById(SEARCH_PAGE_DIV_ID).getStyle().setDisplay(Display.NONE);
		DOM.getElementById(ALARM_PAGE_DIV_ID).getStyle().setDisplay(Display.INLINE);
		
		RootPanel.get(ALARM_PAGE_DIV_ID).add(_alarmPageHTML);
		_refreshBtn.addClickHandler(new RefreshClickHandler());
		_alarmBtn.addClickHandler(new AlarmButtonClickHandler());
	}
	
	private void StartLocationRefreshTimer()
	{
		Timer countdown = new Timer() {
			@Override
			public void run() {
				_currentTime = System.currentTimeMillis();
				if (_endTime - _currentTime <= 0)
				{
					RefreshCurrentGPSLocation();
					_endTime = _currentTime + REFRESH_INTERVAL;
				}
				if(_refreshBtn!=null){
					_refreshBtn.setText("Refreshing in: " + (_endTime - _currentTime)/1000 + " seconds");
				}
			}
		};
		countdown.scheduleRepeating(200);
	}
	
	private void SetAlarmStatus(boolean alarmOn)
	{
		if(alarmOn)
		{
			DOM.getElementById(ALARM_BUTTON_DIV_ID).getStyle().setDisplay(Display.INLINE);
			_alarmAudio.play();
		}
		else
		{
			DOM.getElementById(ALARM_BUTTON_DIV_ID).getStyle().setDisplay(Display.NONE);
			_alarmAudio.load();
		}
	}
	
	class AlarmButtonClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			SetAlarmStatus(false);
		}
	}

	class SearchButtonClickHandler implements ClickHandler {

		public void onClick(ClickEvent event) 
		{
			String start=_startingInput.getText();
			String end=_destinationInput.getText();

			callGoogleDirectionAPI(start,end);

			LoadAlarmPage();
		}
	}

	private void RefreshCurrentGPSLocation() {
		SetLoadingGifVisibility(true);
		_geoposition = Geolocation.getIfSupported();
		if (_geoposition == null) {
			Window.alert("Sorry, your browser doesn't support the Geolocation feature!");
		}

		_geoposition.getCurrentPosition(new CurrentPositionCallBack());
	}
	
	private class CurrentPositionCallBack implements Callback<Position, PositionError>
	{
		@Override
		public void onSuccess(Position result) {
			SetLoadingGifVisibility(false);
			Coordinates coordinates = result.getCoordinates();
			_latitude = coordinates.getLatitude();
			_longitude = coordinates.getLongitude();
			_accuracy = coordinates.getAccuracy();
			
			System.out.println("Current GPS location (" + _latitude + ", " + _longitude +")");
		
			if(_destinationLocations != null)
			{
				changeDestinations(_destinationLocations);
			}
			_currentLocationCheckBox.setEnabled(true);
		}

		@Override
		public void onFailure(PositionError reason) {
			SetLoadingGifVisibility(false);
			Window.alert("Sorry, your location cannot be determined!: " + reason.getMessage());
		}
	}
	
	private class CurrentLocationCheckBoxClickHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) {
			if(_currentLocationCheckBox.getValue())
			{
				_startingInput.setText(_latitude + "," + _longitude);
				_startingInput.setEnabled(false);
			}
			else
			{
				_startingInput.setEnabled(true);
			}
		}
	}

	private class RefreshClickHandler implements ClickHandler{
		@Override
		public void onClick(ClickEvent event) {
			RefreshCurrentGPSLocation();
		}			
	}

	private void changeDestinations(DestinationLocations result)
	{
		_destinationLocations = result;
		
		String content = "";
		
		List<String> names = _destinationLocations.getNames();
		List<Double> latitudes = _destinationLocations.getLatitudes();
		List<Double> longitudes = _destinationLocations.getLongitudes();
		
		for(int i=0; i<names.size(); i++)
		{
			content += "<h3>" + names.get(i) + "</h3>";
			
			double distance = FieldVerifier.distance(latitudes.get(i), longitudes.get(i), _latitude, _longitude, 'K');
			
			int kiloMeters = (int)distance;
			int meters = (int)((distance - kiloMeters)*1000);
			
			if(IS_TEST_MODE)
			{
				if(Math.random()<0.1f)
				{
					distance = 0.5f;
				}
			}
			
			content += "<h4> Distance remaining: " + kiloMeters + "km "+ meters +"m</h4>";
			
			if(!_alarmActivationList.contains(i) && distance < ALARM_RING_DISTANCE_METERS / 1000f)
			{
				//Set off alarm
				_alarmActivationList.add(i);
				SetAlarmStatus(true);
				System.out.println("Setting alarm On: " + i);
			}
		}
		
		_alarmPageHTML.setHTML(content);
	}
	
	public void callGoogleDirectionAPI(String origin, String destination)
	{
		SetLoadingGifVisibility(true);
		alarmService = new AsyncCallback<DestinationLocations>(){
			@Override
			public void onFailure(Throwable caught) {
				SetLoadingGifVisibility(false);
				System.out.println(caught.getMessage());
			}

			@Override
			public void onSuccess(DestinationLocations result) {
				SetLoadingGifVisibility(false);
				changeDestinations(result);
				StartLocationRefreshTimer();
			}
		};
		long curTimeSeconds = System.currentTimeMillis()/1000;
		
		String formattedOrigin = URL.encode(origin);
		String formattedDestination = URL.encode(destination);
		
		String queryUri = "maps.googleapis.com/maps/api/directions/json?origin="+
				formattedOrigin +
				"&destination="+
				formattedDestination+
				"&key=GOOGLE_TRANSIT_API_KEY&departure_time="+
				curTimeSeconds +
				"&mode=transit";
		
//		System.out.println(queryUri);

		greetingService.greetServer(queryUri, alarmService);
	}
	
	private void SetLoadingGifVisibility(boolean isVisible)
	{
		try {
			if(isVisible)
				DOM.getElementById(LOADING_GIF_DIV_ID).getStyle().setDisplay(Display.INLINE);
			else
				DOM.getElementById(LOADING_GIF_DIV_ID).getStyle().setDisplay(Display.NONE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}






