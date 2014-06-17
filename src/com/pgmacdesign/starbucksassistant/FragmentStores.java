package com.pgmacdesign.starbucksassistant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//Uses Google maps to find local Starbucks

public class FragmentStores extends Fragment {

	//Global Variables
	GoogleMap map;
	
	String address = null;
	String placesSearchStr = null;
	double startLat, startLon;
	static double endLat;
	static double endLng;
	
	LatLng closestStarbucks;

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

		
		
		
		//Popup a dialog after 5 seconds with option to start directions
		Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() { 
	         public void run() { 
	        	 //If a popup is needed after the map loads
	        	 //NavigationPopup(); 
	         } 
	    }, (1000*5));
	    
		
	
	    return rootView;
	}
	
	

	//Pops up an indicator that allows people to use the navigation8
	private void NavigationPopup() {
		
		//If the address was entered correctly, pop up a dialog window
		if (address.equals(null)){
			//Nothing to see here... move along...
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setCancelable(true);
			builder.setTitle("Navigation?");
			builder.setMessage("If you would like driving directions to one of the locations, that is an option here!"
					+ "");
			builder.setInverseBackgroundForced(true);
			builder.setPositiveButton("Dismiss",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                int which) {
                            dialog.dismiss();
                        }
                    });
			builder.setNegativeButton("Show Me", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
				        dialog.cancel();
				        Intent navigation = new Intent(
				                Intent.ACTION_VIEW,
				                Uri.parse("http://maps.google.com/maps?saddr="+startLat+","+startLon+"&daddr="+endLat+","+endLng));
				        startActivity(navigation);	
				   }
				});
            AlertDialog alert = builder.create();
            alert.show();	
		}
		
	}



	//Determines the current location via their location and zooms in to it
	private void GetCurrentLocation() {
		double[] d = Getlocation();
		
		startLat = d[0];
		startLon = d[1];
		
		LatLng mapCenter = new LatLng(d[0], d[1]);

		double[] nearbyStores = searchForStuff();
		closestStarbucks = new LatLng(nearbyStores[0], nearbyStores[1]);
		
		
		//Allows for the cross-hairs button which will zoom into you
		map.setMyLocationEnabled(true);
		
		//Adds an image to center of the location of the person//
		map.addMarker(new MarkerOptions()
		//.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot)) //Sets the icon where you are located
        .position(mapCenter) //Positions the dot at the center of the location of the user
        .flat(true)
        .rotation(245));
		
		map.addMarker(new MarkerOptions()
		.title("Starbucks")
        .position(closestStarbucks) //Positions the dot at the closest starbucks
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


	//Initialize Variables
	private void findStarbucks(String location1){

		String location = location1;
		address = location; 
		ConvertAddress(address);
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
	
	//Searches for a location based on your zip code. IE, Find me a Jamba Juice near 90605
	@SuppressWarnings("deprecation")
	public double[] searchForStuff(){
		final String searchString = "Starbucks";
		
		final double[] placeLocations = new double[2];

		
		
		Thread backgroundThread = new Thread (new Runnable(){
			public void run(){
				try{

					final URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?address="
				            + URLEncoder.encode(searchString) + "&sensor=true");
				
					URLConnection conn = url.openConnection();
			        InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
	
			        BufferedReader br = new BufferedReader(streamReader);
			        StringBuilder sb = new StringBuilder();
			        String line = null;
			        while ((line = br.readLine()) != null) {
			            sb.append(line);
			            sb.append("\n");
			        }
			        br.close();
	
			        JSONObject mainObject = new JSONObject(sb.toString());
			        JSONObject result = mainObject.getJSONArray("results").getJSONObject(0); // Array of location objects
			        JSONObject geometry = result.getJSONObject("geometry");
			        JSONObject location = geometry.getJSONObject("location");
	
			        double latitude = location.getDouble("lat");
			        double longitude = location.getDouble("lng");
			        
			        placeLocations[0] = latitude;
			        placeLocations[1] = longitude;
			        
			        
				} catch (IOException e) {
					String error = e.toString();
				} catch (Exception e){
					String error = e.toString();
				}

			}	
		});// 
			

		backgroundThread.start();
			
		Toast.makeText(getActivity(), "Is this working? " + placeLocations[0], Toast.LENGTH_LONG);
		return placeLocations;
	       
		
	        
	}
	

}
