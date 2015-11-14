package parsing;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PApplet;
import processing.data.XML;

public class ParseFeed {


	/*
	 * This method is to parse a GeoRSS feed corresponding to earthquakes around
	 * the globe.
	 * 
	 * @param p - PApplet being used
	 * @param fileName - file name or URL for data source
	 */
	public static List<PointFeature> parseEarthquake(PApplet p, String fileName) {
		List<PointFeature> features = new ArrayList<PointFeature>();

		XML rss = p.loadXML(fileName);
		// Get all items
		XML[] itemXML = rss.getChildren("entry");
		PointFeature point;
		
		for (int i = 0; i < itemXML.length; i++) {
			
				// get location and create feature
				Location location = getLocationFromPoint(itemXML[i]);
				
				// if successful create PointFeature and add to list
				if( location != null) {
					point = new PointFeature(location);
					features.add(point);
				}
				else {
					continue;
				}

				// Sets title if existing
				String titleStr = getStringVal(itemXML[i], "title");
				if (titleStr != null) {
					point.putProperty("title", titleStr);
					// get magnitude from title
					point.putProperty("magnitude", Float.parseFloat(titleStr.substring(2, 5)));
				}

				// Sets depth(elevation) if existing
				float depthVal = getFloatVal(itemXML[i], "georss:elev");
				
				// NOT SURE ABOUT CHECKING ERR CONDITION BECAUSE 0 COULD BE VALID?
				// get one decimal place when converting to km
				int interVal = (int)(depthVal/100);
				depthVal = (float) interVal/10;
				point.putProperty("depth", Math.abs((depthVal)));
				

				// Sets age if existing
				XML[] catXML = itemXML[i].getChildren("category");
				for (int c = 0; c < catXML.length; c++) {
					String label = catXML[c].getString("label");
					if ("Age".equals(label)) {
						String ageStr = catXML[c].getString("term");
						point.putProperty("age", ageStr);
					}
				}
		

			}
		
			return features;
		}

	
	/*
	 * Gets location from georss:point tag
	 * 
	 * @param XML Node which has point as child
	 * 
	 * @return Location object corresponding to point
	 */
	private static Location getLocationFromPoint(XML itemXML) {
		// set loc to null in case of failure
		Location loc = null;
		XML pointXML = itemXML.getChild("georss:point");
		
		// set location if existing
		if (pointXML != null && pointXML.getContent() != null) {
			String pointStr = pointXML.getContent();
			String[] latLon = pointStr.split(" ");
			float lat = Float.valueOf(latLon[0]);
			float lon = Float.valueOf(latLon[1]);

			loc = new Location(lat, lon);
		}
		
		return loc;
	}	
	
	/*
	 * Get String content from child node.
	 */
	private static String getStringVal(XML itemXML, String tagName) {
		// Sets title if existing
		String str = null;
		XML strXML = itemXML.getChild(tagName);
		
		// check if node exists and has content
		if (strXML != null && strXML.getContent() != null) {
			str = strXML.getContent();
		}
		
		return str;
	}
	
	/*
	 * Get float value from child node
	 */
	private static float getFloatVal(XML itemXML, String tagName) {
		return Float.parseFloat(getStringVal(itemXML, tagName));
	}
	

	
	/*
	 * This method is to parse a file containing gender demographics information from
	 * the world bank.  
	 * The original data can be found: 
	 * http://data.worldbank.org/data-catalog/gender-statistics
	 * I took it on Oct 14, 2015, and "cleaned up" the data by leaving only 
	 * 15-64 yrs old male/female population statistics. The file is in the data folder. 
	 * 
	 * 
	 * @param p - PApplet being used
	 * @param fileName - file name or URL for data source
	 * @return A HashMap of country->array[female population, male population]
	 */
	public static HashMap<String, long[]> loadGenderDemographicsFromCSV(PApplet p, String fileName) {
		// HashMap key: country ID and  data: lifeExp at birth
		HashMap<String, long[]> genderDemogrMap = new HashMap<String, long[]>();

		// get lines of csv file
		String[] rows = p.loadStrings(fileName);
		
		// Reads country name and population density value from CSV row
		for (String row : rows) {
			// split row by commas not in quotations
			String[] columns = row.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			
			// check if there is any gender demographics data from any year, get most recent			
			for(int i = columns.length - 1; i > 3; i--) {
				
				// check if value exists for year
				if(!columns[i].equals("..")) {
					if (genderDemogrMap.containsKey(columns[1]))					
						genderDemogrMap.get(columns[1])[1] = Long.parseLong(columns[i]);
					else {
						long[] arr = new long[2]; 								
						genderDemogrMap.put(columns[1], arr);
						genderDemogrMap.get(columns[1])[0] = Long.parseLong(columns[i]);  						
					}
					// break once most recent data is found
					break;
				}
			}
			
		}

		return genderDemogrMap;
	}//loadGenderDemographics

	
	// testing
	// outputs 228 countries or parts of the world
	public static void main (String args[]){
	
		PApplet p = new PApplet(); 		
		HashMap<String, long[]> genderDemo = loadGenderDemographicsFromCSV(p, "./data/GenderDemographics_Data.csv");
        
		for (String key : genderDemo.keySet()){
			System.out.println(key+": "+ genderDemo.get(key)[0]+", "+ genderDemo.get(key)[1]);
			
		}
		
	}
	
	
	

}