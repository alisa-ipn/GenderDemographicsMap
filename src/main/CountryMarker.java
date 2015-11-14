package main;


import java.util.List;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;
import processing.core.PGraphics;

/** Implements a marker for countries carrying demographic information
 * 
 */

public class CountryMarker extends SimplePolygonMarker {

		// Records whether this marker has been clicked (most recently)
		protected boolean clicked = false;
		
		public CountryMarker(List<Location> locations) {
			super(locations);
		}
						
		public CountryMarker(List<Location> locations, java.util.HashMap<java.lang.String,java.lang.Object> properties) {
			super(locations, properties);
		}
		
		public CountryMarker(SimplePolygonMarker marker) {
						
			this.setLocations(marker.getLocations());
			this.setProperties(marker.getProperties());
			this.setId(marker.getId());
		}
		
		// Getter method for clicked field
		public boolean getClicked() {
			return clicked;
		}
		
		// Setter method for clicked field
		public void setClicked(boolean state) {
			clicked = state;
		}
		
		// Common piece of drawing method for markers; 
		// YOU WILL IMPLEMENT. 
		// Note that you should implement this by making calls 
		// drawMarker and showTitle, which are abstract methods 
		// implemented in subclasses
		public void draw(PGraphics pg, float x, float y) {
				
			pg.fill(0, 0, 0);
			pg.rect(x, y, 20, 20);
			
			showTitle(pg, x, y);
			if (selected) {
				    System.out.println(this.getStringProperty("name") + " got selected!");
					showTitle(pg, x, y);
				}			
		}
		
		//public abstract void drawMarker(PGraphics pg, float x, float y);
		public void showTitle(PGraphics pg, float x, float y){             
                
				String name = this.getStringProperty("name");
					
				pg.pushStyle();				
				pg.fill(0, 0, 0);
				pg.textAlign(pg.LEFT, pg.CENTER);
				pg.textSize(12);				
				pg.text(name, x+3, y+3);
						
				if (this.getProperties().keySet().contains("fPop")) {
						String fPop = "Female Population: " + getProperty("fPop"); 
						String mPop = "Male Population: " + getProperty("mPop");
						Double delta = (Double)getProperty("delta");
					
						String deltaString = "Delta,%: " + delta;
						
						pg.text(fPop, x+3, y+17);
						pg.text(mPop, x+3, y+32);
						pg.text(deltaString, x+3, y+45);					
					
				}
				else {
					pg.text("No demographics data available", x+3, y+17);
				} 					
				pg.popStyle();
			}//showTitle		
			
}
