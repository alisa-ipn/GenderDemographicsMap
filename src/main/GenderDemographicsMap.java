package main;

import parsing.ParseFeed;
import processing.core.PApplet;
import processing.core.PGraphics;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.providers.*;
import de.fhpotsdam.unfolding.providers.Google.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;

import java.util.HashMap;

import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
/**
 * Visualizes gender demographics in different countries. 
 * 
 * It loads the country shapes from a GeoJSON file via a data reader, and loads the gender population values from
 * another CSV file (provided by the World Bank). The data value is encoded to transparency via a simplistic linear
 * mapping.
 */


public class GenderDemographicsMap extends PApplet{

	UnfoldingMap map;
	HashMap<String, long[]> genderMap;
	List<Feature> countries;	
	List<Marker> originalCountryMarkers;
	List<Marker> countryMarkers; 
	
	
	private Marker lastSelected;
	//private CountryMarker lastClicked;
	private Map<Marker, float[]> lastClicked = new HashMap<Marker, float[]>();
	
	public void setup() {
		size(900, 700, OPENGL);
		map = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleMapProvider());
		MapUtils.createDefaultEventDispatcher(this, map);

		// Load lifeExpectancy data
		genderMap = ParseFeed.loadGenderDemographicsFromCSV(this,"GenderDemographics_Data.csv");
		
		// Load country polygons and adds them as markers
		countries = GeoJSONReader.loadData(this, "countries.geo.json");
		
		//countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//alternative solution 
		originalCountryMarkers = MapUtils.createSimpleMarkers(countries);					
		initializeCountryMarkers(originalCountryMarkers);
				
		map.addMarkers(countryMarkers);
		//debug
		//System.out.println(countryMarkers.get(0).getId());
		
		// Country markers are shaded according to the gender demographics (only once)
		shadeCountries();
	}

	public void draw() {
		// Draw map tiles and country markers		
		background(0);
		map.draw();
		addLegend();
		
		
		if (lastSelected != null) 
			showTitle(lastSelected, mouseX, mouseY);
		
		for (Marker clickedCountry : lastClicked.keySet()){
			showTitle(clickedCountry, lastClicked.get(clickedCountry)[0],
					lastClicked.get(clickedCountry)[1]);
		}
		
	}

	// helper method to initialize array of CountryMarkers
	private void initializeCountryMarkers(List<Marker> initMarkerList){
		countryMarkers = new ArrayList<Marker>(); 
		
		for (Marker m: initMarkerList) {
			if (m.getClass().getName().endsWith("SimplePolygonMarker")) {
				//System.out.println(m.getClass().getName());
				SimplePolygonMarker marker = (SimplePolygonMarker)m;  
				countryMarkers.add(new CountryMarker(marker));
			}
			else {
				MultiMarker multiM = (MultiMarker) m;					
				countryMarkers.add(new CountryMultiMarker(multiM));
			}
		}  
	}//initializeCountryMarkers
	
	
	//Helper method to color each country based on life expectancy
	//Red-orange indicates low (near 40)
	//Blue indicates high (near 100)
	private void shadeCountries() {
		boolean printProperties = false; 
		for (Marker marker : countryMarkers) {
			//debug			
			if (!printProperties) {
				System.out.print("Printing one property set: ");
				for (String key : marker.getProperties().keySet())
					System.out.println(key+": " + marker.getProperties().get(key));
				printProperties = true; 
			}
			// Find data for country of the current marker
			String countryId = marker.getId();
			System.out.println(countryId +": "+ genderMap.containsKey(countryId));
			if (genderMap.containsKey(countryId)) {
				long fPop = genderMap.get(countryId)[0];
				long mPop = genderMap.get(countryId)[1];
				float delta = calcDelta(fPop, mPop);
				
				marker.getProperties().put("fPop", fPop);
				marker.getProperties().put("mPop", mPop);
				marker.getProperties().put("delta", deltaToPercent(fPop, mPop));
				
				int deltaInt = (int)Math.round(delta*1000);			
				System.out.println(countryId + ": "+ deltaInt);
				//if (Math.abs(deltaInt) > 150) deltaInt = deltaInt*150/Math.abs(deltaInt);  
				// Encode value as brightness				
				//marker.setColor(color(150+deltaInt, 50, 150-deltaInt, 230));
				if (delta > 0) {
					if (Math.abs(deltaInt*2) > 105) deltaInt = 105/2;
					marker.setColor(color(150+deltaInt*2, 90, 70, 210));					
				} 
				else {
					if (Math.abs(deltaInt) > 150) 
						marker.setColor(color(20, 90, 255, 210));
					else{
						if (Math.abs(deltaInt*3) > 150) deltaInt = -105/5;
						marker.setColor(color(70, 90, 150 - deltaInt*3, 210));
					}
				}
			}
			else {
				marker.setColor(color(250,250,250, 50));
			}
		}
	}//shadeCountries
	
	private float calcDelta(long a, long b){
	    // ideally, we should check for null division, etc. 	
		return (a-b)/(float)(a+b); 
	}
	
	private double deltaToPercent(long a, long b){
		float c = (a-b)/(float)(a+b);
		System.out.println(c);
		double result = Math.abs(Math.round(c*10000))/(double)100;
		
		System.out.println(result);
		return result;
		
	}
	
	// helper method to draw key in GUI
	private void addLegend() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		rect(xbase, ybase, 150, 400);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		String title = "Gender Demographics Map Legend";
				
		text(title, xbase+5, ybase+5, 140, 50);		
		
		// red rectangle
		fill(215, 90, 100, 230);
		rect(xbase+5, ybase+75, 15, 15);
		
		//blue rectangle
		fill(100, 90, 215, 230);
		rect(xbase+5, ybase+135, 15, 15);
		
		// white rectangle
		fill(255, 255, 255, 230);
		rect(xbase+5, ybase+185, 15, 15);
		
		textSize(10);
		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("Pink Shades: countries with larger female population", xbase+25, ybase+70, 120, 55);
		
		text("Blue Shades: countries with larger male population", xbase+25, ybase+125, 120, 55);
		text("White: no gender demographics data", xbase+25, ybase+185, 120, 30);
				
		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		String msg = "For detailed demographics information, hover over a country";
		text(msg, xbase+5, ybase+230, 140, 55);				
		
		String msg3 = "Click on countries to show the information statically";
		text(msg3, xbase+5, ybase+285, 140, 30);				
		
		String msg2 = "Press any key to clear";
		text(msg2, xbase+5, ybase+315, 140, 55);
	}

	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(countryMarkers);
		
		//loop();
	}
	
	// If there is a marker selected 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
		
		for (Marker m : markers) 
		{
			//System.out.println(m.getClass().getName());
			if (m.getClass().getName().endsWith("CountryMarker")) {
				CountryMarker marker = (CountryMarker)m;
				if (marker.isInside(map,  mouseX, mouseY)) {
					lastSelected = marker;
					marker.setSelected(true);
					return;
			    }
			}
			else{
				CountryMultiMarker marker = (CountryMultiMarker)m;
				if (marker.isInside(map,  mouseX, mouseY)) {
					lastSelected = marker;
					marker.setSelected(true);
					return;
			    }
				
			}
			
		}
	}
	
	/** The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{

		for (Marker m : countryMarkers) {			
			if (m.isInside(map, mouseX, mouseY)) {					
				System.out.println(m.getStringProperty("name"));
				float[] pos = new float[2];
				pos[0] = mouseX; 
				pos[1] = mouseY;
				lastClicked.put(m, pos);			
				break;
			}
		}
	}
	
	// Helper method that will check if a country was clicked upon
	// and respond appropriately
	/*	
	private void checkCountryForClick()
		{
			// Loop over the country markers to see if one of them is selected
			for (Marker m : countryMarkers) {
				CountryMarker marker = (CountryMarker)m;
				if (marker.isInside(map, mouseX, mouseY)) {
					lastClicked = marker;
					// Hide all the other earthquakes and hide
					for (Marker mhide : quakeMarkers) {
						if (mhide != lastClicked) {
							mhide.setHidden(true);
						}
					}
					for (Marker mhide : cityMarkers) {
						if (mhide.getDistanceTo(marker.getLocation()) 
								> marker.threatCircle()) {
							mhide.setHidden(true);
						}
					}
					return;
				}
			}
		}
        */
	
	public void keyPressed(){
		
		lastClicked = new HashMap<Marker, float[]>();
		
	} 
	
	
	public void showTitle(Marker country, float x, float y){
	                
				String name = country.getStringProperty("name");
					
				pushStyle();				
				fill(0, 0, 0);
				textAlign(LEFT, CENTER);
				textSize(12);				
				text(name, x+3, y+3);
						
				if (country.getProperties().keySet().contains("fPop")) {
						String fPop = "Female Population: " + country.getProperty("fPop"); 
						String mPop = "Male Population: " + country.getProperty("mPop");
						Double delta = (Double)country.getProperty("delta");
					
						String deltaString = "Delta,%: " + delta;
						
						text(fPop, x+3, y+17);
						text(mPop, x+3, y+32);
						text(deltaString, x+3, y+45);					
					
				}
				else {
					text("No demographics data available", x+3, y+17);
				} 					
				popStyle();
			}//showTitle

	

}
