package com.ubc.transitalarm.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.ubc.transitalarm.shared.DestinationLocations;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	DestinationLocations greetServer(String name) throws IllegalArgumentException;
}
