package com.pgmacdesign.starbucksassistant;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class Main extends FragmentActivity implements ActionBar.TabListener{
	 
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	
	// Tab titles
	private String[] tabs = { "Cards", "Rewards", "Stores"};
	
	
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	
	    // ActionBar
	    ActionBar actionbar = getActionBar();
	    actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	
	    
      viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	} 
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
              MenuInflater inflater = getMenuInflater();
              inflater.inflate(R.menu.tabs_menu, menu);
              return true;
  }
  
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
              switch (item.getItemId()) {
              case R.id.menuitem_search:
                  Toast.makeText(this, "Search", Toast.LENGTH_LONG).show();
                  return true;
                  
              case R.id.menuitem_send:
                  Toast.makeText(this, "Send", Toast.LENGTH_LONG).show();
                  return true;
                  
              case R.id.menuitem_add:
                  Toast.makeText(this, "Add", Toast.LENGTH_LONG).show();
                  return true;
		                    
		        case R.id.menuitem_share:
                  Toast.makeText(this, "Share", Toast.LENGTH_LONG).show();
                  return true;
		                    
		        case R.id.menuitem_feedback:
		            Toast.makeText(this, "Feedback", Toast.LENGTH_LONG).show();
		            return true;
		            
				case R.id.menuitem_about:
		            Toast.makeText(this, "About", Toast.LENGTH_LONG).show();
		            return true;
				    
				case R.id.menuitem_quit:
				    Toast.makeText(this, "Quit", Toast.LENGTH_LONG).show();
				    return true;
				    
				    
              /*
               * case R.id.menuitem_quit:
               
                          Toast.makeText(this, getString(R.string.ui_menu_quit),
                                                  Toast.LENGTH_SHORT).show();
                          finish(); // close the activity
                          return true;
                   */
              }
              return false;
  }
  
	//onSaveInstanceState() is used to "remember" the current state when a
  // configuration change occurs such screen orientation change. This
  // is not meant for "long term persistence". We store the tab navigation

  @Override
  protected void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);

  }
  
  



	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
		
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		//viewPager.setCurrentItem(tab.getPosition());
		
	}




}



