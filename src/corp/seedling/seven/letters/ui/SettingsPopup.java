package corp.seedling.seven.letters.ui;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import corp.seedling.seven.letters.R;
import corp.seedling.seven.letters.utils.Sound;

public class SettingsPopup extends Activity{

	CustomTextViewSettings music , sfx;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popup_settings);
		
		music = (CustomTextViewSettings)findViewById(R.id.tv_sett_music);
		sfx = (CustomTextViewSettings)findViewById(R.id.tv_sett_sfx);
		
		music.setOnClickListener(listener);
		sfx.setOnClickListener(listener);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		if (preferences.getBoolean("music", true) == true)
			music.setBackground(getResources().getDrawable(R.drawable.circle_settings_music));
		else
			music.setBackground(getResources().getDrawable(R.drawable.circle_settings_music_off));
		
		if (preferences.getBoolean("sfx", true) == true)
			sfx.setText("ON");
		else
			sfx.setText("OFF");
		
	}
	
	@Override
	protected void onDestroy() {
		music.setBackground(null);
		music.setOnClickListener(null);
		music= null;
		
		sfx.setBackground(null);
		sfx.setOnClickListener(null);
		sfx= null;
		
		super.onDestroy();

	}
	
	
	SharedPreferences preferences;
	private final MyClickListener listener = new MyClickListener(this);
	private static class MyClickListener implements OnClickListener{
		private final WeakReference<SettingsPopup> actInstance;


		public MyClickListener(SettingsPopup activity) {
			actInstance = new WeakReference<SettingsPopup>(activity);
		}

		@Override
		public void onClick(View v) {

			SettingsPopup gameScreen = actInstance.get();

			Sound.playSound(Sound.SOUND_GENERIC_PRESS);

			if (v.getId() == gameScreen.music.getId()){
				
				if (gameScreen.preferences.getBoolean("music", true) == true ){
					
					GameApp.getAppInstance().stopPlaying();
					v.setBackground(gameScreen.getResources().getDrawable(R.drawable.circle_settings_music_off));
					gameScreen.preferences.edit().putBoolean("music", false).apply();
					
				}
				else if (gameScreen.preferences.getBoolean("music", true) == false ){
					
					v.setBackground(gameScreen.getResources().getDrawable(R.drawable.circle_settings_music));
					gameScreen.preferences.edit().putBoolean("music", true).apply();
					GameApp.getAppInstance().resumePlaying();
					
				} 
			}

			else if (v.getId() == gameScreen.sfx.getId()){
				
				if (gameScreen.preferences.getBoolean("sfx", true) == true ){
					
					gameScreen.sfx.setText("OFF");
					gameScreen.preferences.edit().putBoolean("sfx", false).apply();
					Sound.sfx = false;
					
				}
				else if (gameScreen.preferences.getBoolean("sfx", true) == false ){
					
					Sound.sfx = true;
					gameScreen.sfx.setText("ON");
					gameScreen.preferences.edit().putBoolean("sfx", true).apply();
				} 
			}

		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		GameApp.getAppInstance().stopPlaying();
		//		Sound.unloadSound();
	}

	@Override
	protected void onResume() {
		super.onResume();
		GameApp.getAppInstance().resumePlaying();
		//		Sound.initSound();
	}	
}
