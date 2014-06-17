package com.pgmacdesign.starbucksassistant;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			//Notes fragment activity
			return new FragmentCards();
		case 1:
			//Events fragment activity
			return new FragmentRewards();
		case 2:
			//Tasks fragment activity
			return new FragmentStores();
		
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3; //ADJUST
	}

}
