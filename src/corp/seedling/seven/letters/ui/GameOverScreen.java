package corp.seedling.seven.letters.ui;

import java.lang.ref.WeakReference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;

import corp.seedling.seven.letters.R;
import corp.seedling.seven.letters.utils.Sound;

public class GameOverScreen extends BaseGameActivity{

	private static final int WHAT_ZOOM_ANSWER1_WITH_SOUND = 1;
	private static final int WHAT_ZOOM_ANSWER2_WITHOUT_SOUND = 3;
	private static final int WHAT_FLOAT_ANSWER = 2;
	LinearLayout layWord1, layWord2;
	CustomTextViewOver tvRestart , tvScore, tvCompletion , tvHighScore, tvLeader, tvAchieve;
	private static final int ANIM_TIME = 100;
	int score;
	int completion;
	int drawableBG;
	SharedPreferences preferences;
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
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_over_screen);

		initUI();  
		Bundle bundle = getIntent().getExtras();
		score = bundle.getInt("score", 0);
		completion = bundle.getInt("completion", 0);
		drawableBG = bundle.getInt("drawableBG", R.drawable.circle_red);

		String word1 = bundle.getStringArrayList("correct_answer").get(0);
		String word2 = bundle.getStringArrayList("correct_answer").get(1);

		showAnswers(word1, word2);
		setClickListeners();
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		//set stats values
		tvScore.setText(""+score);
		tvCompletion.setText(""+completion+"%");
		tvHighScore.setText(""+preferences.getInt("high_score", 0));
		
		preferences.edit().putInt("count", preferences.getInt("count", 0) + 1 ).apply();

		if(!mConnected)  {
			googleApiClient = getApiClient();
		}
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, StartingScreen.class));
		finish();
		super.onBackPressed();
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
		//		Sound.initSound();
		GameApp.getAppInstance().resumePlaying();
	}	

	private void setClickListeners() {
		tvRestart.setOnClickListener(clickListener);
		tvLeader.setOnClickListener(clickListener);
		tvAchieve.setOnClickListener(clickListener);
	}

	private void initUI() {
		layWord1 = (LinearLayout)findViewById(R.id.lay_word1);
		layWord2 = (LinearLayout)findViewById(R.id.lay_word2);
		tvRestart = (CustomTextViewOver)findViewById(R.id.tv_restart);
		tvScore = (CustomTextViewOver)findViewById(R.id.tv_score);
		tvHighScore = (CustomTextViewOver)findViewById(R.id.tv_high_score);
		tvCompletion = (CustomTextViewOver)findViewById(R.id.tv_dict);
		tvLeader = (CustomTextViewOver)findViewById(R.id.tv_leaderboard);
		tvAchieve = (CustomTextViewOver)findViewById(R.id.tv_achieve);
	}

	@Override
	protected void onDestroy() {

		if (handler != null){
			handler.removeCallbacksAndMessages(null);
		}
		
		tvRestart.setBackground(null);
		tvRestart.setOnClickListener(null);
		tvRestart= null;
		
		tvScore.setBackground(null);
		tvScore.setOnClickListener(null);
		tvScore= null;
		
		tvHighScore.setBackground(null);
		tvHighScore.setOnClickListener(null);
		tvHighScore= null;
		
		tvCompletion.setBackground(null);
		tvCompletion.setOnClickListener(null);
		tvCompletion = null;
		
		tvLeader.setBackground(null);
		tvLeader.setOnClickListener(null);
		tvLeader= null;
		
		tvAchieve.setBackground(null);
		tvAchieve.setOnClickListener(null);
		tvAchieve= null;
		
		for (int i = 0 ; i < layWord1.getChildCount() ; i++){
			
			TextView tv = (TextView)layWord1.getChildAt(i);
			tv.setAnimation(null);
			
			Drawable cellDrawable = tv.getBackground();
			if (cellDrawable != null){
				cellDrawable.setCallback(null);
				cellDrawable = null;
			}
			
			tv.setBackground(null);
			tv.setOnClickListener(null);
			tv = null;
		}

		for (int i = 0 ; i < layWord2.getChildCount() ; i++){
			
			TextView tv = (TextView)layWord2.getChildAt(i);
			tv.setAnimation(null);
			
			Drawable cellDrawable = tv.getBackground();
			if (cellDrawable != null){
				cellDrawable.setCallback(null);
				cellDrawable = null;
			}			
			
			tv.setOnClickListener(null);
			tv.setBackground(null);
			tv = null;
		}

		super.onDestroy();

	}
	
	private void showAnswers(String word1, String word2) {

		for(int i = 0 ; i < 4 ; i++){

			CustomTextView textView = (CustomTextView)layWord1.getChildAt(i);

			textView.setText("" + word1.charAt(i));
			textView.setBackgroundResource(drawableBG);
			handler.sendMessageDelayed(Message.obtain(handler, WHAT_ZOOM_ANSWER1_WITH_SOUND, textView), (i + 1) *ANIM_TIME);


			if (i == 0)
				handler.sendMessageDelayed(Message.obtain(handler, WHAT_FLOAT_ANSWER, textView), (i+1)* ANIM_TIME + 400);
			else if (i == 1)
				handler.sendMessageDelayed(Message.obtain(handler, WHAT_FLOAT_ANSWER, textView), (i+1)* ANIM_TIME + 950);
			else if (i == 2)
				handler.sendMessageDelayed(Message.obtain(handler, WHAT_FLOAT_ANSWER, textView), (i+1)* ANIM_TIME + 550);
			else if (i == 3)
				handler.sendMessageDelayed(Message.obtain(handler, WHAT_FLOAT_ANSWER, textView), (i+1)* ANIM_TIME + 650);
		}

		for(int i = 0 ; i < 4 ; i++){
			CustomTextView textView = (CustomTextView)layWord2.getChildAt(i);

			textView.setText("" + word2.charAt(i));
			textView.setBackgroundResource(drawableBG);
			handler.sendMessageDelayed(Message.obtain(handler, WHAT_ZOOM_ANSWER2_WITHOUT_SOUND, textView), (i+1) * ANIM_TIME);


			if (i == 0)
				handler.sendMessageDelayed(Message.obtain(handler, WHAT_FLOAT_ANSWER, textView), (i+1)* ANIM_TIME + 630);
			else if (i == 1)
				handler.sendMessageDelayed(Message.obtain(handler, WHAT_FLOAT_ANSWER, textView), (i+1)* ANIM_TIME + 350);
			else if (i == 2)
				handler.sendMessageDelayed(Message.obtain(handler, WHAT_FLOAT_ANSWER, textView), (i+1)* ANIM_TIME + 720);
			else if (i == 3)
				handler.sendMessageDelayed(Message.obtain(handler, WHAT_FLOAT_ANSWER, textView), (i+1)* ANIM_TIME + 450);
		}


		Sound.playSound(Sound.SOUND_SCORE);
	}


	private final ClickListener clickListener = new ClickListener(this);

	private static class ClickListener implements OnClickListener{

		private final WeakReference<GameOverScreen> actInstance ;

		public ClickListener(GameOverScreen activity){
			actInstance = new WeakReference<GameOverScreen>(activity);
		}

		@Override
		public void onClick(View v) {

			GameOverScreen gameScreen = actInstance.get();
			Sound.playSound(Sound.SOUND_GENERIC_PRESS);

			if (v.getId() == gameScreen.tvRestart.getId()){

				Intent intent = new Intent(gameScreen, GameScreen.class);
				intent.putExtra("show_ad", true);
				gameScreen.startActivity(intent);
				
				gameScreen.finish();
			}
			else if (v.getId() == gameScreen.tvLeader.getId()){
				
				if (gameScreen.mConnected)
				{
					gameScreen.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameScreen.googleApiClient, gameScreen.getResources().getString(R.string.leaderboard_7_letters)),11);
					return; 
				}
				gameScreen.beginUserInitiatedSignIn();
			}
			
			else if (v.getId() == gameScreen.tvAchieve.getId()){
				
				if (gameScreen.mConnected)
				{
					gameScreen.startActivityForResult(Games.Achievements.getAchievementsIntent(gameScreen.googleApiClient), 10 );
					return; 
				}
				gameScreen.beginUserInitiatedSignIn();
			}
		}
	}


	private final MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler{
		private final WeakReference<GameOverScreen> actInstance;

		public MyHandler(GameOverScreen activity){
			actInstance = new WeakReference<GameOverScreen>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			GameOverScreen gameScreen = actInstance.get();

			switch (msg.what) {

			case WHAT_FLOAT_ANSWER:

				TranslateAnimation floaty = new TranslateAnimation( 0, 0 , 0, 5);
				floaty.setInterpolator(new CycleInterpolator(1));
				floaty.setDuration(1750);
				floaty.setRepeatMode(Animation.RESTART);
				floaty.setRepeatCount(Animation.INFINITE);
				( (CustomTextView) msg.obj).startAnimation(floaty);
				break;

			case WHAT_ZOOM_ANSWER1_WITH_SOUND:

				Sound.playSound(Sound.SOUND_START);

				ScaleAnimation scale1 = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
				scale1.setFillAfter(true);
				scale1.setDuration(ANIM_TIME);
				( (CustomTextView) msg.obj).startAnimation(scale1);

				break;

			case WHAT_ZOOM_ANSWER2_WITHOUT_SOUND:
				ScaleAnimation scale33 = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
				scale33.setFillAfter(true);
				scale33.setDuration(ANIM_TIME);
				( (CustomTextView) msg.obj).startAnimation(scale33);

				break;

			default:
				break;
			}
		}
	}

}
