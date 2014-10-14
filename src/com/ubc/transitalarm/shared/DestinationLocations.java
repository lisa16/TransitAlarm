package com.ubc.transitalarm.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DestinationLocations implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<String> names = new ArrayList<String>();
	private List<Double> latitudes = new ArrayList<Double>();
	private List<Double> longitudes = new ArrayList<Double>();
	
	public List<String> getNames()
	{
		return names;
	}
	
	public List<Double> getLatitudes()
	{
		return latitudes;
	}
	
	public List<Double> getLongitudes()
	{
		return longitudes;
	}
	
}
