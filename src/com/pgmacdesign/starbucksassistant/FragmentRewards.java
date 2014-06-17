package com.pgmacdesign.starbucksassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


public class FragmentRewards extends Fragment {
	
	ListView earnings_list;
	TextView earnings_updated_as_of;
	static TextView earnings_temp_text_view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_rewards, container, false);
		// View rootView = inflater.inflate(R.layout.fragment_earnings,
		// container, false);

		
		return rootView;
	}
		
}
