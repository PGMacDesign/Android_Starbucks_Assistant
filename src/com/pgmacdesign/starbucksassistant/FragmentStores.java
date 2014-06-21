package com.pgmacdesign.starbucksassistant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

//Uses Google maps to find local Starbucks

public class FragmentStores extends Fragment {

	//Global Variables
	GoogleMap map;
	
	String address = null;
	String placesSearchStr = null;
	double startLat, startLon;
	static double endLat;
	static double endLng;
	
	double testLat, testLng;

	LatLng closestStarbucks;
	
	//The following are all for Google Places
	// flag for Internet connection status
	Boolean isInternetPresent = false;

	// Connection detector class
	ConnectionDetector cd;
	
	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	// Google Places
	GooglePlaces googlePlaces;

	// Places List
	PlacesList nearPlaces;
	
	// Place Details
	PlaceDetails placeDetails;

	// GPS Location
	GPSTracker gps;

	// Button
	Button btnShowOnMap;

	// Progress dialog
	ProgressDialog pDialog;
	
	// Places Listview
	ListView lv;
	
	// ListItems data
	ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String,String>>();
	
	//Shared Preferences
	public static final String PREFS_NAME = "StarbucksAssistant";	
	SharedPrefs sp = new SharedPrefs();
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	
	// KEY Strings
	public static String KEY_REFERENCE = "reference"; // id of the place
	public static String KEY_NAME = "name"; // name of the place
	public static String KEY_VICINITY = "vicinity"; // Place area name
	
	// Map view
	MapView mapView;

	// Map overlay items
	List<Overlay> mapOverlays;

	AddItemizedOverlay itemizedOverlay;

	GeoPoint geoPoint;
	// Map controllers
	MapController mc;
	
	OverlayItem overlayitem;


	//Main - When the activity starts
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_stores, container, false);
	
		map = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
		//map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		//map.setMapType(GoogleMap.MAP_TYPE_NONE);
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		//map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		//map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		
		//Get Current Location and set it to center of map
		try {
			GetCurrentLocation();
		} catch (Exception e){
			//Discovered this bug via htc phones. I guess they frequently need reboots with regards to gps and location based services
			Toast.makeText(getActivity(), "If you are seeing this error, it likely means you need to reboot your phone!", Toast.LENGTH_SHORT).show();
		}


		cd = new ConnectionDetector(getActivity().getApplicationContext());

		// Check if Internet present
		isInternetPresent = cd.isConnectingToInternet();
		if (!isInternetPresent) {
			// Internet Connection is not present
			alert.showAlertDialog(getActivity(), "Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return rootView;
		}

		// creating GPS Class object
		gps = new GPSTracker(getActivity());

		// check if GPS location can get
		if (gps.canGetLocation()) {
			Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
		} else {
			// Can't get user's current location
			alert.showAlertDialog(getActivity(), "GPS Status",
					"Couldn't get location information. Please enable GPS",
					false);
			// stop executing code by return
			return rootView;
		}


		// Getting listview
		lv = (ListView) rootView.findViewById(R.id.list);
		
		// button show on map
		btnShowOnMap = (Button) rootView.findViewById(R.id.btn_show_map);

		// calling background Async task to load Google Places
		// After getting places from Google all the data is shown in listview
		new LoadPlaces().execute();

		//For using Shared Preferences
		/*
		sp.putDouble(editor, "user_latitude", gps.getLatitude());
		sp.putDouble(editor, "user_longitude", gps.getLongitude());
		//sp.putString(editor, "near_places", nearPlaces); //Need to make a shared prefs method for Lists
		
		String user_latitude = Double.toString(sp.getDouble(settings, "user_latitude", 0.0));
		String user_longitude = Double.toString(sp.getDouble(settings, "user_longitude", 0.0));
		*/
		
		
		/** Button click event for shown on map 
		btnShowOnMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity().getApplicationContext(),
						PlacesMapActivity.class);
				// Sending user current geo location
				i.putExtra("user_latitude", Double.toString(gps.getLatitude()));
				i.putExtra("user_longitude", Double.toString(gps.getLongitude()));
				
				// passing near places to map activity
				i.putExtra("near_places", nearPlaces);
				// staring activity
				startActivity(i);
			}
		});
		*/
		
		/**
		 * ListItem click event
		 * On selecting a listitem SinglePlaceActivity is launched
		 * 
		lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	// getting values from selected ListItem
                String reference = ((TextView) view.findViewById(R.id.reference)).getText().toString();
                
                // Starting new intent
                Intent in = new Intent(getActivity().getApplicationContext(),
                        SinglePlaceActivity.class);
                
                // Sending place refrence id to single place activity
                // place refrence id used to get "Place full details"
                in.putExtra(KEY_REFERENCE, reference);
                startActivity(in);
            }
        });
        */
	
		
		//Popup a dialog after 3 seconds with option to start directions
		Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() { 
	         public void run() { 
	        	 
	        	 //
	        	 executeOrder66();
	        	 //
	         } 
	    }, (1000*3));
	    
		
	
	    return rootView;
	}
	

	//Determines the current location via their location and zooms in to it
	private void GetCurrentLocation() {
		
		double[] d = Getlocation();
		
		startLat = d[0];
		startLon = d[1];
		
		LatLng mapCenter = new LatLng(d[0], d[1]);

		//Allows for the cross-hairs button which will zoom into you
		map.setMyLocationEnabled(true);
		
		//Adds an image to center of the location of the person//
		map.addMarker(new MarkerOptions()
		//.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot)) //Sets the icon where you are located
        .position(mapCenter) //Positions the dot at the center of the location of the user
        .flat(true)
        .rotation(245));

		//Other method for location and zoom in:
		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(mapCenter)     	    // Sets the center of the location of the user
	    .zoom(17)                   // Sets the zoom
	    .bearing(0)                 // Sets the orientation of the camera to North (90 for east, 180 for south)
	    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
	    .build();                   // Creates a CameraPosition from the builder
		
		//UiSettings.setMyLocationButtonEnabled(true).
		
		//Animate the change in camera view over 2.5 seconds
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2500, null);
		
		
		
	}

	
	//Convert the address string into latitude and longitude points
	public void ConvertAddress(String address) {
    	try {
    		
    		Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
    		List<Address> addressList = geoCoder.getFromLocationName(this.address, 1) ;
    		
        	endLat = addressList.get(0).getLatitude();
            endLng = addressList.get(0).getLongitude();
            
        } catch (Exception e) {
            String error = e.toString();
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        } // end catch
        
    } // end if
	
	
	//Calculates the current position of the user and returns Latitude/ Longitude via a double array
	public double[] Getlocation() {
	    LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
	    List<String> providers = lm.getProviders(true);

	    Location l = null;
	    for (int i = 0; i < providers.size(); i++) {
	        l = lm.getLastKnownLocation(providers.get(i));
	        if (l != null)
	            break;
	    }
	    double[] gps = new double[2];

	    if (l != null) {
	        //Current location of latitude and longitude
	    	gps[0] = l.getLatitude();
	        gps[1] = l.getLongitude();
	     
	        
	        
	    }
	    return gps;
	}
	
	
	/**
	 * Background Async Task to Load Google places
	 * */
	class LoadPlaces extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading Places..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... args) {
			// creating Places class object
			googlePlaces = new GooglePlaces();

			
			try {
				// Separeate your place types by PIPE symbol "|"
				// If you want all types places make it as null
				// Check list of types supported by google
				// 
				String types = "cafe"; //cafe|restaurant //Listing places only cafes, restaurants  ///////////////////////////////////////////////
				String name = "starbucks";
				
				// Radius in meters - increase this value if you don't find any places
				double radius = 1000; // 1000 meters 
				
				double[] dy = Getlocation();
				
				// get nearest places
				nearPlaces = googlePlaces.search(dy[0],
						dy[1], radius, name); //Last parameter Should be types, doing a workaround
				
				testLat = gps.getLatitude();
				testLng = gps.getLongitude();
				
				Log.d("Test Latitude", Double.toString(testLat));
				Log.d("Test Longitude", Double.toString(testLng));
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * and show the data in UI
		 * Always use runOnUiThread(new Runnable()) to update UI from background
		 * thread, otherwise you will get error
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					
									
					/**
					 * Updating parsed Places into LISTVIEW
					 * */
					// Get json response status
					String status = nearPlaces.status;
					
					// Check for all possible status
					if(status.equals("OK")){
						// Successfully got places details
						
						if (nearPlaces.results != null) {
							// loop through each place
							for (Place p : nearPlaces.results) {
								HashMap<String, String> map = new HashMap<String, String>();
								
								// Place reference won't display in listview - it will be hidden
								// Place reference is used to get "place full details"
								map.put(KEY_REFERENCE, p.reference);
								
								// Place name
								map.put(KEY_NAME, p.name);
								
								
								// adding HashMap to ArrayList
								placesListItems.add(map);
							}
							// list adapter
							ListAdapter adapter = new SimpleAdapter(getActivity(), placesListItems,
					                R.layout.list_item,
					                new String[] { KEY_REFERENCE, KEY_NAME}, new int[] {
					                        R.id.reference, R.id.name });
							
							// Adding data into listview
							//lv.setAdapter(adapter);
						}
					}
					else if(status.equals("ZERO_RESULTS")){
						// Zero results found
						alert.showAlertDialog(getActivity(), "Near Places",
								"Sorry no places found. Try to change the types of places",
								false);
					}
					else if(status.equals("UNKNOWN_ERROR"))
					{
						alert.showAlertDialog(getActivity(), "Places Error",
								"Sorry unknown error occured.",
								false);
					}
					else if(status.equals("OVER_QUERY_LIMIT"))
					{
						alert.showAlertDialog(getActivity(), "Places Error",
								"Sorry query limit to google places is reached",
								false);
					}
					else if(status.equals("REQUEST_DENIED"))
					{
						alert.showAlertDialog(getActivity(), "Places Error",
								"Sorry error occured. Request is denied",
								false);
						Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
						Log.d("ERROR Yo", "Line 432");
					}
					else if(status.equals("INVALID_REQUEST"))
					{
						alert.showAlertDialog(getActivity(), "Places Error",
								"Sorry error occured. Invalid Request",
								false);
						Log.d("ERROR Yo", "Line 439");
					}
					else
					{
						alert.showAlertDialog(getActivity(), "Places Error",
								"Sorry error occured.",
								false);
						Log.d("ERROR Yo", "Line 446");
					}
				}
			});

		}

	}


	public void executeOrder66(){
		

		// check for null in case it is null
		if (nearPlaces.results != null) {
			// loop through all the places
			for (Place place : nearPlaces.results) {
				
				double[] dx = Getlocation();
				
				LatLng starbucksHopefully = new LatLng(dx[0], dx[1]);

				//Allows for the cross-hairs button which will zoom into you
				map.setMyLocationEnabled(true);
				
				//Adds an image to center of the location of the person//
				map.addMarker(new MarkerOptions()
				//.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot)) //Sets the icon where you are located
		        .position(starbucksHopefully) //Positions the dot at the center of the location of the user
		        .flat(true)
		        .rotation(245));
			}
			
		}
				
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getActivity().getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	


}
