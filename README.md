## Gender Demographics Map
Interactive Gender Demographics Map by Country

[//]: # (Screenshots of the functionalities of the application)

### Description

This is an Interactive Gender Demographics Map. Initially, it shows countries colored according to the magnitude of the difference between female and male population in a country, countries with more female population being more red, while countries with more male population being more blue (see Fig. 1). The color legend and a short usage instruction are present in the legend box.  

The interactive functionality is as follows: 
* When you hover a mouse cursor over a country, it will show gender demographics data about that country dynamically, i.e. it will follow the cursor (Fig 2).
* If you click on a country, it will show that information statically. You can click on various countries, and it will show you information about each of them. If you click on a country in a different place, it will move the description but will always show only  1 description per country (Fig 3).
* To clear all descriptions, you can press any key (Fig 4).
* Also, if there is no gender demographics information about a country, it will show a corresponding message when you hover or click on a corresponding country (Fig 4).


### Technical details 

In  ./data folder:
*	I added gender demographics data downloaded from http://data.worldbank.org. I also had to do some preliminary data cleaning so that there were only the data about male and female population for a country in a format similar to the Life Expectancy data.

In ParseFeed class:
*	Wrote a new parsing method public static HashMap<String, long[]> loadGenderDemographicsFromCSV(PApplet p, String fileName). It reads in the data from a data file and returns a hashmap that maps a country code to a 2-element array for female and male populations. 

In GenderDemographicsMap class (similar to LifeExpectancy.java):
*	Modified private void shadeCountries() method to set colors to country markers according to the gender population difference. In the same run, it adds population data into countryMarker properties as elements of Markers' Properties hash table.
*	Introduced a helper method private float calcDelta(long a, long b). It calculates the difference between male and female population and converts it into a relative value used for coloring. 
*	Introduced private Map<Marker, float[]> lastClicked structure that stores the clicked countries  along with a 2 element array of the mouse cursor coordinates where the click was done (used for static info display at a particular point on the map).
*	Modified public void mouseClicked(). It finds whether a click was within a country, and in my version it stores that information in lastClicked data structure
*	Modified public void draw(). Now it handles the display of information when hovering over a country and when a country is clicked.
*	Modified public void showTitle(Marker country, float x, float y) is called from draw() to shows the information  about the country passed as a parameter at the position x, y.
*	Introduced public void keyPressed(). It clears the lastClicked structure, by this clearing the static information displayed on the map.