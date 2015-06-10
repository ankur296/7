package corp.seedling.seven.letters.ui;

import java.lang.ref.WeakReference;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import corp.seedling.seven.letters.R;
import corp.seedling.seven.letters.utils.Sound;

public class GooglePlayServicesPopup extends BaseGameActivity{

	CustomTextView leader , achieve;
	private GoogleApiClient googleApiClient;
	public boolean mConnected = false;

	@Override
	public void onSignInSucceeded() {
		System.out.println("Ankur sign in success");
		mConnected = true;
		if( (googleApiClient == null) && mConnected){
			googleApiClient = getApiClient();
		}
	}

	@Override
	public void onSignInFailed() {
		System.out.println("Ankur sign in fail");
		mConnected = false;
		googleApiClient = null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_google_play_services);

		leader = (CustomTextView)findViewById(R.id.tv_sett_leader);
		achieve = (CustomTextView)findViewById(R.id.tv_sett_achieve);

		leader.setOnClickListener(listener);
		achieve.setOnClickListener(listener);

		if(!mConnected)  {
			googleApiClient = getApiClient();
		}
	}

	@Override
	protected void onDestroy() {
		leader.setBackground(null);
		leader.setOnClickListener(null);
		leader= null;
		
		achieve.setBackground(null);
		achieve.setOnClickListener(null);
		achieve= null;
		
		super.onDestroy();

	}
	
	private final MyClickListener listener = new MyClickListener(this);
	private static class MyClickListener implements OnClickListener{
		private final WeakReference<GooglePlayServicesPopup> actInstance;

		public MyClickListener(GooglePlayServicesPopup activity) {
			actInstance = new WeakReference<GooglePlayServicesPopup>(activity);
		}

		@Override
		public void onClick(View v) {

			GooglePlayServicesPopup gameScreen = actInstance.get();

			Sound.playSound(Sound.SOUND_GENERIC_PRESS);

			if (v.getId() == gameScreen.leader.getId()){

				if (gameScreen.mConnected)
				{
					gameScreen.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameScreen.googleApiClient, gameScreen.getResources().getString(R.string.leaderboard_7_letters)),11);
					return; 
				}
				gameScreen.beginUserInitiatedSignIn();
			}

			else if (v.getId() == gameScreen.achieve.getId()){
				if (gameScreen.mConnected)
				{
					gameScreen.startActivityForResult(Games.Achievements.getAchievementsIntent(gameScreen.googleApiClient), 10 );
					return; 
				}
				gameScreen.beginUserInitiatedSignIn();
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
