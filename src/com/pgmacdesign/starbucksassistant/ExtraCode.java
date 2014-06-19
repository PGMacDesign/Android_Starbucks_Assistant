package com.pgmacdesign.starbucksassistant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class ExtraCode {

	/*
	 * 
	 
	 	
	double[] nearbyStores;
	
	nearbyStores = new double[2];
	finalStarbucksStuff();
	 
	 
		//This is returning an address in Colorado (Starbucks way). Will have to change to Google Places
	private class findSomeCoffee extends AsyncTask <String, Long, Void> {
		private final ProgressDialog dialog = new ProgressDialog(getActivity());
		
		// can use UI thread here
		protected void onPreExecute() {
			Toast.makeText(getActivity(), "Looking for Starbucks...", Toast.LENGTH_SHORT).show();
		}
		
		// automatically done on worker thread (separate from UI thread)
		//Searches for a location based on your zip code. IE, Find me a Jamba Juice near 90605
		protected Void doInBackground(final String... args) {
			
			//final String searchString = "Starbucks"; //Unused atm as it is hardcoded 5 lines below
			
			try{

				final URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?address="
			            + URLEncoder.encode("Starbucks") + "&sensor=true");
			
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
		        
		        nearbyStores[0] = latitude;
		        nearbyStores[1] = longitude;
		        
		        Log.d("VIVZ", "IT WORKS!" + Double.toString(nearbyStores[0]));
		        Log.d("VIVZ", Double.toString(nearbyStores[0]));
		        Log.d("VIVZ", Double.toString(nearbyStores[1]));
		        
			} catch (IOException e) {
				String error = e.toString();
				Log.d("VIVZ", error);
			} catch (Exception e){
				String error = e.toString();
				Log.d("VIVZ", error);
			}
			return null;
		}

	
		// periodic updates - it is OK to change UI
		@Override
		protected void onProgressUpdate(Long... value) {
			super.onProgressUpdate(value);
			
		}
		
		// can use UI thread here
		protected void onPostExecute(final Void unused) {
		}
	}//AsyncTask
	
	
	
		public void finalStarbucksStuff(){

		//Find local Starbucks
		try {
			new findSomeCoffee().execute();
		} catch (Exception e){
			String error = e.toString();
			Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
		}
		
		//Add the starbucks to the map via marker
		try {
			
			Handler handler1 = new Handler(Looper.getMainLooper());
			handler1.post(new Runnable(){

				@Override
				public void run() {
					LatLng closestStarbucks = new LatLng(nearbyStores[0], nearbyStores[1]);
					
					map.addMarker(new MarkerOptions()
					.title("Starbucks")
			        .position(closestStarbucks) //Positions the dot at the closest starbucks
			        .flat(true)
			        .rotation(245));
				} 
			   // your UI code here 
			});

			Log.d("VIVZ", "Line 250 Worked");
			
		} catch (Exception e){
			String error = e.toString();
			Log.d("VIVZ", error);
		}	
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
	 
	 
	 
	 
	 
	 
	 
	 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
}
