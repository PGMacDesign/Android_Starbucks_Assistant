package com.pgmacdesign.starbucksassistant;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

public class Splash extends Activity {

MediaPlayer ourIntroSong; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		//Intro song to be played on splash screen
		//ourIntroSong = MediaPlayer.create(SplashScreen.this, R.raw.intro);
		
	
		//Determines length of time splash screen is open
		Thread timer = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					sleep(1000);
				} catch (InterruptedException e01) {
					String error_in_splash = e01.toString(); //For Debugging purposes
					e01.printStackTrace();
				} finally {
					//After thread has run, transition to main class via implicit intent
					Intent openMain = new Intent("com.pgmadesign.starbucksassistant.MAIN");
					startActivity(openMain);
					
					//Intent intent7 = new Intent(getBaseContext(), ThreadingActivity.class);
			        //startActivity(intent7);
				}
			}
		};

		timer.start();
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		//This kills the music so it isn't carried over between splash screens
		//ourIntroSong.release();
		
		finish(); 
	}	
}