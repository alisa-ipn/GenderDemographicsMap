package main;


import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import processing.core.PGraphics;

/** Implements a marker for countries carrying demographic information
 * 
 */

public class CountryMultiMarker extends MultiMarker {

		MultiMarker originalM = new MultiMarker(); 
	
		// Records whether this marker has been clicked (most recently)
		protected boolean clicked = false;
		
		public CountryMultiMarker() {
			super();
		}
		
		public CountryMultiMarker(MultiMarker multiM) {

			int len = multiM.getMarkers().size();
			Marker[] markerArr = new Marker[len]; 
			for (int i = 0; i < len; i++){			
			    markerArr[i] =  multiM.getMarkers().get(i);
			}
			
			this.addMarkers(markerArr);
			this.setProperties(multiM.getProperties());
			this.setId(multiM.getId());
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
		/*
		public void draw(PGraphics pg, float x, float y) {
						
			if (selected) {
					showTitle(pg, x, y);
				}			
		} */
		
		//public abstract void drawMarker(PGraphics pg, float x, float y);
		public void showTitle(PGraphics pg, float x, float y){
                
			String name = this.getStringProperty("name");
				
			pg.pushStyle();				
			pg.fill(255, 255, 255);
			pg.textSize(12);
			pg.textAlign(pg.LEFT, pg.CENTER);
			pg.text(name, x+3, y+3);
					
			if (this.getProperties().keySet().contains("fPop")) {
					String fPop = "Female Population: " + this.getStringProperty("fPop"); 
					String mPop = "Male Population: " + this.getStringProperty("mpop");
					Double delta = (Double)this.getProperty("delta");
				
					double deltaLong = Math.abs(Math.round(delta*100))/100; 
				
					String deltaString = "Delta, %" + deltaLong;
					
					pg.text(fPop, x+3, y+17);
					pg.text(mPop, x+3, y+32);
					pg.text(deltaString, x+3, y+45);					
				
			}
				
				//pg.rectMode(PConstants.CORNER);
				//pg.rect(x, y-TRI_SIZE-39, Math.max(pg.textWidth(name), pg.textWidth(pop)) + 6, 39);
				//pg.fill(0, 0, 0);
				//pg.textAlign(PConstants.LEFT, PConstants.TOP);
			else {
				pg.text("No demographics data available", x+3, y+17);
			} 	
			
			pg.popStyle();
			}//showTitle
			
}
