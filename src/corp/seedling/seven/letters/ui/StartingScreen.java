package corp.seedling.seven.letters.ui;

import java.lang.ref.WeakReference;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.internal.cr;
import com.google.example.games.basegameutils.BaseGameActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.CycleInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import corp.seedling.seven.letters.R;
import corp.seedling.seven.letters.utils.AchievementsUnlocked;
import corp.seedling.seven.letters.utils.Sound;
import corp.seedling.seven.letters.utils.Utils;

public class StartingScreen extends BaseGameActivity{

	RelativeLayout parentLayout;
	CustomTextView play, settings, share, rate, crossPromo, googlePlayServices, howToPlay;
	private static final int WHAT_ZOOM_MENU = 1;
	private static final int WHAT_ZOOM_LETTER_TEXT = 3;
	private static final int WHAT_FLOAT_ANSWER = 2;
	public static final int REQUEST_CODE_LAUNCH_HELP = 7;
	SharedPreferences preferences;
	private GoogleApiClient googleApiClient;
	public boolean mConnected = false; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.starting_screen);

		initUI();
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		for (int i = 0 ; i < 7; i++){

			CustomTextView textView = (CustomTextView)parentLayout.getChildAt(i);

			switch (i) {

			case 0:
				textView.setBackground(getResources().getDrawable(R.drawable.circle_play));
				break;

			case 1:
				textView.setBackground(getResources().getDrawable(R.drawable.circle_help));
				break;

			case 2:
				textView.setBackground(getResources().getDrawable(R.drawable.circle_settings));
				break;

			case 3:
				textView.setBackground(getResources().getDrawable(R.drawable.circle_share));
				break;

			case 4:
				textView.setBackground(getResources().getDrawable(R.drawable.circle_rate));
				break;

			case 5:
				textView.setBackground(getResources().getDrawable(R.drawable.circle_statistics));
				break;

			case 6:
				textView.setBackground(getResources().getDrawable(R.drawable.circle_crosspromo));
				break;

			default:
				break;
			}

			//zoom in anim
			handler.sendMessageDelayed(Message.obtain(handler, WHAT_ZOOM_MENU, textView), (i+1) * 100);
		}
		//zoom in anim "lettters..."
		handler.sendMessageDelayed(Message.obtain(handler, WHAT_ZOOM_LETTER_TEXT, (TextView)findViewById(R.id.tv_letters)), (7+1) * 100);
		//float anim
		handler.sendEmptyMessageDelayed(WHAT_FLOAT_ANSWER, 700);
		setClickListeners();

		if(!mConnected)  {
			googleApiClient = getApiClient();
		}


	}


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
	}


	private final MyClickListener listener = new MyClickListener(this);
	private static class MyClickListener implements OnClickListener{
		private final WeakReference<StartingScreen> actInstance;


		public MyClickListener(StartingScreen activity) {
			actInstance = new WeakReference<StartingScreen>(activity);
		}

		@Override
		public void onClick(View v) {

			StartingScreen gameScreen = actInstance.get();

			Sound.playSound(Sound.SOUND_GENERIC_PRESS);

			if (v.getId() == gameScreen.play.getId()){

				if (gameScreen.preferences.getBoolean("first_launch", true) == true){
					gameScreen.preferences.edit().putBoolean("first_launch", false).apply();

					Intent intent = new Intent(gameScreen, HelpScreen.class);
					intent.putExtra("launched_over_play", true);
					gameScreen.startActivity(intent);
				}
				else{
					Intent intent = new Intent(gameScreen, GameScreen.class);
					intent.putExtra("show_ad", false);
					gameScreen.startActivity(intent);
				}
				
				gameScreen.finish();
			}

			else if (v.getId() == gameScreen.howToPlay.getId()){
				
				Intent intent = new Intent(gameScreen, HelpScreen.class);
				
				gameScreen.startActivity(intent);
				gameScreen.finish();
			}

			else if (v.getId() == gameScreen.settings.getId()){
				gameScreen.startActivity(new Intent(gameScreen, SettingsPopup.class));

			}

			else if (v.getId() == gameScreen.share.getId()){
				gameScreen.shareit();
			}

			else if (v.getId() == gameScreen.rate.getId()){
				gameScreen.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.SEVEN_LETTERS_URL)));
			}

			else if (v.getId() == gameScreen.googlePlayServices.getId()){
				gameScreen.startActivity(new Intent(gameScreen, GooglePlayServicesPopup.class));
			}

			else if (v.getId() == gameScreen.crossPromo.getId()){
				gameScreen.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.PUBLISHER_URL)));
			}
		}
	}

	private void shareit(){ 

		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		emailIntent.putExtra("android.intent.extra.TEXT", "Hey word lover, try this out: "+Utils.SEVEN_LETTERS_URL_HTTP);
		emailIntent.setType("text/plain");

		startActivity(Intent.createChooser(emailIntent, "Share!"));
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

	@Override
	protected void onDestroy() {

		if (handler != null){
			handler.removeCallbacksAndMessages(null);
		}

		if (scale1 != null){
			scale1.cancel();
			scale1 = null;
		}
		if (scale2 != null){
			scale2.cancel();
			scale2 = null;
		}
		if (floaty_vert != null){
			floaty_vert.cancel();
			floaty_vert = null;
		}

		Drawable drawable = play.getBackground();

		if (drawable != null ){
			drawable.setCallback(null);
			drawable = null;
		}

		Drawable drawable2 = howToPlay.getBackground();

		if (drawable2 != null ){
			drawable2.setCallback(null);
			drawable2 = null;
		}

		Drawable drawable3 = settings.getBackground();

		if (drawable3 != null ){
			drawable3.setCallback(null);
			drawable3 = null;
		}

		Drawable drawable4 = share.getBackground();

		if (drawable4 != null ){
			drawable4.setCallback(null);
			drawable4 = null;
		}

		Drawable drawable5 = rate.getBackground();

		if (drawable5 != null ){
			drawable5.setCallback(null);
			drawable5 = null;
		}

		Drawable drawable6 = googlePlayServices.getBackground();

		if (drawable6 != null ){
			drawable6.setCallback(null);
			drawable6 = null;
		}

		Drawable drawable7 = crossPromo.getBackground();

		if (drawable7 != null ){
			drawable7.setCallback(null);
			drawable7 = null;
		}


		play.setBackground(null);
		play.setOnClickListener(null);
		play= null;

		settings.setBackground(null);
		settings.setOnClickListener(null);
		settings= null;

		howToPlay.setBackground(null);
		howToPlay.setOnClickListener(null);
		howToPlay= null;

		share.setBackground(null);
		share.setOnClickListener(null);
		share= null;

		rate.setBackground(null);
		rate.setOnClickListener(null);
		rate= null;

		googlePlayServices.setBackground(null);
		googlePlayServices.setOnClickListener(null);
		googlePlayServices= null;

		crossPromo.setBackground(null);
		crossPromo.setOnClickListener(null);
		crossPromo = null;

		super.onDestroy();
		//		Sound.unloadSound();
	}

	private void setClickListeners() {
		play.setOnClickListener(listener);
		settings.setOnClickListener(listener);
		howToPlay.setOnClickListener(listener);
		share.setOnClickListener(listener);
		rate.setOnClickListener(listener);
		googlePlayServices.setOnClickListener(listener);
		crossPromo.setOnClickListener(listener);
	}


	private void initUI() {
		parentLayout = (RelativeLayout)findViewById(R.id.lay_main_menu);
		play = (CustomTextView)findViewById(R.id.tv_play);
		share = (CustomTextView)findViewById(R.id.tv_share);
		rate = (CustomTextView)findViewById(R.id.tv_rate);
		googlePlayServices = (CustomTextView)findViewById(R.id.tv_googleplayservices);
		settings = (CustomTextView)findViewById(R.id.tv_music);
		howToPlay = (CustomTextView)findViewById(R.id.tv_help);
		crossPromo = (CustomTextView)findViewById(R.id.tv_crosspromo);

		TextView tvLetters = (TextView)findViewById(R.id.tv_letters);
		tvLetters.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "BALLW___.TTF"));

	}

	ScaleAnimation scale1, scale2;
	TranslateAnimation floaty_vert;

	private final MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler{
		private final WeakReference<StartingScreen> actInstance;

		public MyHandler(StartingScreen activity){
			actInstance = new WeakReference<StartingScreen>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			StartingScreen gameScreen = actInstance.get();

			switch (msg.what) {

			case WHAT_ZOOM_MENU:
				gameScreen.scale1 = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
				gameScreen.scale1.setFillAfter(true);
				gameScreen.scale1.setDuration(100);
				( (CustomTextView) msg.obj).startAnimation(gameScreen.scale1);
				break;

			case WHAT_ZOOM_LETTER_TEXT:
				gameScreen.scale2 = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
				gameScreen.scale2.setFillAfter(true);
				gameScreen.scale2.setDuration(100);
				( (TextView) msg.obj).startAnimation(gameScreen.scale2);
				break;

			case WHAT_FLOAT_ANSWER:
				gameScreen.floaty_vert = new TranslateAnimation( 0, 0 , 0, 10);
				gameScreen.floaty_vert.setInterpolator(new CycleInterpolator(1));
				gameScreen.floaty_vert.setDuration(2000);
				gameScreen.floaty_vert.setRepeatMode(Animation.RESTART);
				gameScreen.floaty_vert.setRepeatCount(Animation.INFINITE);

				( (CustomTextView)gameScreen.parentLayout.getChildAt(0) ).startAnimation(gameScreen.floaty_vert);
				break;

			default:
				break;
			}
		}
	}
}
