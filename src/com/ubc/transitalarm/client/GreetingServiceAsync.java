package com.ubc.transitalarm.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.ubc.transitalarm.shared.DestinationLocations;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<DestinationLocations> callback)
			throws IllegalArgumentException;
}
