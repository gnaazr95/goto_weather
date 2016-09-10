package com.example.go_toweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends Activity{
	public MediaPlayer mp1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//set content view AFTER ABOVE sequence (to avoid crash)
		this.setContentView(R.layout.splash); 
		Thread timer = new Thread()
		{
			public void run()
			{
				try
				{
					sleep(3000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				finally
				{
					Class ourClass = null;
					try {
						ourClass = Class.forName("com.example.go_toweather.MainMenu");
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Intent ourIntent = new Intent(Splash.this, ourClass);
					startActivity(ourIntent);
				}
			}
		};
		timer.start();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//mp1.release();
		finish();
	}
	
}