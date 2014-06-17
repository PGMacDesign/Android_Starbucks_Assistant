package com.pgmacdesign.starbucksassistant;

import java.math.BigDecimal;
import java.math.RoundingMode;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentCards extends Fragment implements OnClickListener {
	
	//Global Variables
	Button card_pay, card_reload;
	TextView card_balance, card_date;
	double cardAmount = -1.1;
	
	//Shared Preferences
	public static final String PREFS_NAME = "Starbucks";	
	SharedPrefs sp = new SharedPrefs();
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_cards, container, false);

		//Shared Preferences Stuff
		settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		
		//For initial setup
		if (cardAmount == -1.1){
			sp.putDouble(editor, "gift_card", 50.00);
			editor.commit();
		}
		//Match the card amount listed in shared preferences
		cardAmount = sp.getDouble(settings, "gift_card", 0.0);
		
		//TextViews
		card_balance = (TextView) rootView.findViewById(R.id.card_balance);
		card_date = (TextView) rootView.findViewById(R.id.card_date);
		
		//Buttons
		card_pay = (Button) rootView.findViewById(R.id.card_pay);
		card_reload = (Button) rootView.findViewById(R.id.card_reload);
		
		//Setup the onclicklisteners
		card_pay.setOnClickListener(this);
		card_reload.setOnClickListener(this);
		
		//Round the number to 2 decimal places
		cardAmount = round(cardAmount, 2);
		//Convert the number to a string
		String str1 = Double.toString(cardAmount);
		//Set the card balance to said amount
		card_balance.setText("$"+str1);
		
		//Sets the current time
		setUpdatedTime();
		
		return rootView;
	}

	//OnClick method
	@Override
	public void onClick(View v) {
		
		switch (v.getId()){
		
		case (R.id.card_pay):
		
			if (cardAmount > 0){
				Toast.makeText(getActivity(), "You paid $10", Toast.LENGTH_SHORT).show();
				cardAmount -=10;
				sp.putDouble(editor, "gift_card", cardAmount);
				editor.commit();
			} else {
				Toast.makeText(getActivity(), "You don't have any money!", Toast.LENGTH_SHORT).show();
			}
			
			break;
		
		//Reloads the card balance and updated time
		case (R.id.card_reload):
			reloadCardBalance();
			setUpdatedTime();
			break;
		
		}
		
	}
	
	//Checks the shared preferences and sets the textView for the card balance 
	public void reloadCardBalance(){
		
		//Match the card amount listed in shared preferences
		cardAmount = sp.getDouble(settings, "gift_card", 0.0);
		//Round the number to 2 decimal places
		cardAmount = round(cardAmount, 2);
		//Convert the number to a string
		String str1 = Double.toString(cardAmount);
		//Set the card balance to said amount
		card_balance.setText("$"+str1);
	}
	
	//This method is for rounding numbers
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	//Sets the current time
	public void setUpdatedTime(){
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		
		int month = today.month + 1;
		card_date.setText("Updated as of " + today.year + "-" + month + "-" + today.monthDay + " : " + today.format("%k:%M"));
	}
		
}
