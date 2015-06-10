package corp.seedling.seven.letters.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;

import corp.seedling.seven.letters.R;
import corp.seedling.seven.letters.data.WordsStorage;
import corp.seedling.seven.letters.utils.AchievementsUnlocked;
import corp.seedling.seven.letters.utils.Sound;
import corp.seedling.seven.letters.utils.Utils;

public class GameScreen extends BaseGameActivity {

	private AchievementsUnlocked mAchievementsUnlocked;
	private GoogleApiClient googleApiClient;
	public boolean mConnected = false;
	RelativeLayout layoutSeven , layoutWord, layoutAntonym, layoutParent, layoutAd ; 
	CustomTextView tvWord1 ,tvWord2 ,tvWord3 ,tvWord4 ;
	CustomTextView tvWordAnto1 ,tvWordAnto2 ,tvWordAnto3 ,tvWordAnto4 ;
	CustomTextView tvSeven1 ,tvSeven2 ,tvSeven3 ,tvSeven4, tvSeven5, tvSeven6, tvSeven7 ;
	private AdView mAdView; 
	//whats
	private static final int WHAT_ZOOM_SEVEN = 1;
	private static final int WHAT_WRONG_ANS_WORD = 2;
	private static final int WHAT_WRONG_ANS_ANTO = 3;
	private static final int WHAT_RESET_WORD = 4;
	private static final int WHAT_RESET_ANTO = 5;
	private static final int WHAT_CORRECT_ANS_WORD = 6;
	private static final int WHAT_CORRECT_ANS_ANTO = 7;
	private static final int WHAT_CLEANUP_AND_RELOAD = 8;
	private static final int WHAT_FLOAT = 9;
	private static final int WHAT_TIME_UP = 10;
	private static final int WHAT_GAME_OVER = 11;
	private static final int WHAT_GAME_WON = 12;
	private static final int WHAT_KILL_ACT = 13;

	private static final int gameTime = 30000;
	private static final int bonusTime = 10000;
	static int score = 0;
	static int completion = 0;
	ProgressBar mProgressBar;
	CountDownTimer mCountDownTimer;
	Animation blinkAnimation;
	Context ctxt;
	PopupWindow timeupPopup;
	static long timeLeft = 0 ;

	String word, wordOrig, wordAnto;
	String wordUnique;
	String wordJumbled ;
	boolean easyWordsOver = false; 
	static int wordsUsed1 = 0;
	static int wordsUsed2 = 0;
	static int wordsUsed = 0;
	static int drawableBG;
	static int progress = 100;
	ArrayList<String> currentPair = new ArrayList<String>();
	SharedPreferences preferences ;
	private static final int ANIM_TIME = 100;


	@Override
	public void onSignInSucceeded() {
		System.out.println("Ankur sign in success");
		mConnected = true;
		if(mAchievementsUnlocked.mGoogleApiClient == null && mConnected){
			mAchievementsUnlocked.mGoogleApiClient = getApiClient();
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
		ctxt = this;
		setContentView(R.layout.game_screen);
		initUI();

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		if ( 	getIntent().getBooleanExtra("show_ad", true)
				&& GameApp.getAppInstance().interstitialAds.isLoaded() 
				&&  ( (preferences.getInt("count", 0)) % 2 == 0 )
				&&  (preferences.getInt("count", 0) > 2)
				)
			GameApp.getAppInstance().interstitialAds.show();


		if (savedInstanceState == null ){
			preferences.edit().putBoolean("isClockRunning", false).apply();
			preferences.edit().putInt("progress", 100).apply();
			preferences.edit().putLong("timeLeft", gameTime).apply();
			preferences.edit().putLong("score", 0).apply();

			score = 0;
			progress = 100;
			timeLeft = gameTime;
		}
		else{
			score = 	preferences.getInt("score", 0);
			progress = preferences.getInt("progress", 100);
			timeLeft = preferences.getLong("timeLeft", gameTime) ; 
			startTimer(timeLeft);

			isClockRunning = preferences.getBoolean("isClockRunning",false);
			if (isClockRunning == true)
				Sound.playSound(Sound.SOUND_CLOCK_START);
		}

		newGame();

		//load banner ad
		AdRequest.Builder builder = new AdRequest.Builder();
		//		builder.addTestDevice("BE0B283D9DB35079AECC55005262BED2");//TODO: Remove
		this.mAdView.loadAd(builder.build());

		if(!mConnected)  {
			googleApiClient = getApiClient();
		}
		mAchievementsUnlocked = new AchievementsUnlocked(this, googleApiClient);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		preferences.edit().putInt("score", score).apply();
		//completion not needed as it is calculated with latest score value for every game over
		preferences.edit().putBoolean("isClockRunning", isClockRunning).apply();
		preferences.edit().putInt("progress", progress).apply();
		preferences.edit().putLong("timeLeft", timeLeft).apply();

	}

	@Override
	protected void onResume() {
		super.onResume();

		//		Sound.initSound();

		GameApp.getAppInstance().resumePlaying();

		progress = preferences.getInt("progress", 100);
		timeLeft = preferences.getLong("timeLeft", gameTime) ; 
		startTimer(timeLeft);

		isClockRunning = preferences.getBoolean("isClockRunning",false);
		if (isClockRunning == true)
			Sound.playSound(Sound.SOUND_CLOCK_START);

		if(mAchievementsUnlocked.mGoogleApiClient == null && mConnected){
			mAchievementsUnlocked.mGoogleApiClient = getApiClient();
		}
	}	

	@Override
	protected void onPause() {
		System.out.println("ankur onPause game screen, prog = " +progress);
		super.onPause();

		GameApp.getAppInstance().stopPlaying();

		preferences.edit().putBoolean("isClockRunning", isClockRunning).apply();
		preferences.edit().putInt("progress", progress).apply();
		preferences.edit().putLong("timeLeft", timeLeft).apply();

		if (mCountDownTimer != null){
			mCountDownTimer.cancel();
			mCountDownTimer = null;
		}

		if (isClockRunning == true)
			Sound.pauseSound(Sound.SOUND_CLOCK_START);
		//			Sound.unloadSound();
	}

	@Override
	protected void onDestroy() {
		System.out.println("ankur ondestroy game screen");
		wordsUsed = 0;
		wordsUsed1 = 0;
		wordsUsed2 = 0;
		isClockRunning = false;
		easyWordsOver = false;

		if (handler != null){
			handler.removeCallbacksAndMessages(null);
		}


		if (mProgressBar != null){

			Drawable cellDrawable = mProgressBar.getBackground();
			if (cellDrawable != null){
				cellDrawable.setCallback(null);
				cellDrawable = null;
			}
			mProgressBar.setBackground(null);
			mProgressBar = null;
		}

		for (int i = 0 ; i < layoutWord.getChildCount() ; i++){
			TextView tv = (TextView)layoutWord.getChildAt(i);
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

		for (int i = 0 ; i < layoutAntonym.getChildCount() ; i++){
			TextView tv = (TextView)layoutAntonym.getChildAt(i);
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

		for (int i = 0 ; i < layoutSeven.getChildCount() ; i++){
			TextView tv = (TextView)layoutSeven.getChildAt(i);
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

	private static boolean isClockRunning = false;

	private void startTimer(final long startFrom) {

		System.out.println("ankur starttimer enter");
		mProgressBar.setProgress((int) ( 100*   (float)startFrom / gameTime ));
		mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.rectangular_progress_bar));

		if (mCountDownTimer != null){

			mCountDownTimer.cancel();
			mCountDownTimer = null;

			mCountDownTimer=new CountDownTimer(startFrom,1) {

				@Override
				public void onTick(long millisUntilFinished) {

					progress =  (int) ( 100*   (float) ( millisUntilFinished) / gameTime ) ;


					if (progress < 25){
						mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.rectangular_progress_bar_red));

						if ( mProgressBar.getAnimation() == null)
							mProgressBar.startAnimation(blinkAnimation);

						if (isClockRunning == false){
							Sound.playSound(Sound.SOUND_CLOCK_START);
							isClockRunning = true;
						}
						GameApp.getAppInstance().loadAd();
					}
					else{
						if ( mProgressBar.getAnimation() != null)
							mProgressBar.clearAnimation();

						if (isClockRunning == true){
							Sound.pauseSound(Sound.SOUND_CLOCK_START);
							isClockRunning = false;
						}
					}

					mProgressBar.setProgress(progress);
					timeLeft = millisUntilFinished;
				}

				@Override
				public void onFinish() {

					progress = 0;
					mProgressBar.setProgress(progress);
					mProgressBar.clearAnimation();

					handler.sendEmptyMessage(WHAT_TIME_UP);
					handler.sendEmptyMessageDelayed(WHAT_GAME_OVER, 1500);

					isClockRunning = false;
					preferences.edit().putBoolean("isClockRunning", isClockRunning).apply();
					preferences.edit().putInt("progress", progress).apply();

					timeLeft = 0;
					mCountDownTimer = null;
				}
			};

			mCountDownTimer.start();

		}
		else{
			mCountDownTimer=new CountDownTimer(startFrom,1) {

				@Override
				public void onTick(long millisUntilFinished) {

					progress =  (int) ( 100*   (float)millisUntilFinished / gameTime ) ;

					if (progress < 25){
						mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.rectangular_progress_bar_red));

						if ( mProgressBar.getAnimation() == null)
							mProgressBar.startAnimation(blinkAnimation);

						if (isClockRunning == false){
							Sound.playSound(Sound.SOUND_CLOCK_START);
							isClockRunning = true;
						}
						GameApp.getAppInstance().loadAd();
					}

					else{
						if ( mProgressBar.getAnimation() != null)
							mProgressBar.clearAnimation();

						if (isClockRunning == true){
							Sound.pauseSound(Sound.SOUND_CLOCK_START);
							isClockRunning = false;
						}
					}


					mProgressBar.setProgress(progress);
					timeLeft = millisUntilFinished;
				}

				@Override
				public void onFinish() {
					progress = 0;
					mProgressBar.setProgress(progress);
					mProgressBar.clearAnimation();

					handler.sendEmptyMessage(WHAT_TIME_UP);
					handler.sendEmptyMessageDelayed(WHAT_GAME_OVER, 1500);

					isClockRunning = false;
					preferences.edit().putBoolean("isClockRunning", isClockRunning).apply();
					preferences.edit().putInt("progress", progress).apply();

					timeLeft = 0;
					mCountDownTimer = null;
				}
			};

			mCountDownTimer.start();
		}
	}

	private String repeatedLetter(){
		for(int i = 0 ; i < word.length() - 1 ; i++){

			for(int j = i+1  ; j < word.length() ; j++){

				if (word.charAt(i) == word.charAt(j))
					return String.valueOf(word.charAt(i));
			}
		}
		return null;
	}


	private void newGame(){
		System.out.println("ankur newGame enter");

		WordsStorage.load();
		//		score = 0;
		completion = 0;
		wordsUsed = 0;
		wordsUsed1 = 0;
		wordsUsed2 = 0;
		firstWordFinalized = false;
		setupQuest();
	}

	private void setupQuest() {
		System.out.println("ankur setupQuest enter" );

		layoutWord.setTag(0);
		layoutAntonym.setTag(0);
		firstWordFinalized = false;

		if (easyWordsOver == false){
			currentPair = WordsStorage.words.get(wordsUsed1);
			wordsUsed1++;
		}
		else{
			currentPair = WordsStorage.words2.get(wordsUsed2);
			wordsUsed2++;
		}

		wordOrig = currentPair.get(0);
		wordAnto = currentPair.get(1);
		wordsUsed++;

		if (wordsUsed1 == WordsStorage.words.size()){ //this logic ll only work when word1 list has greater size 
			easyWordsOver = true;
			wordsUsed1 = 0;
		}

		word = wordOrig + wordAnto;

		wordUnique = removeDuplicates(wordOrig + wordAnto);

		if (wordUnique.length() != 7)
			Toast.makeText(this, "INVALID PAIR ! : " + wordOrig + " , " + wordAnto, Toast.LENGTH_LONG).show();

		shuffleQuestion();
	}

	void shuffleQuestion(){

		wordJumbled = shuffle(wordUnique);
		System.out.println("wordOrig = " +wordOrig + ", wordAnto = "+ wordAnto + " ,word = " +word );

		drawableBG = Utils.getRandomColor();

		for (int i = 0 ; i < layoutSeven.getChildCount(); i++){

			CustomTextView textView = (CustomTextView)layoutSeven.getChildAt(i);

			textView.setText("" + wordJumbled.charAt(i));

			if ( ("" + wordJumbled.charAt(i)).equalsIgnoreCase(repeatedLetter()) ){
				textView.setTag(2);
				textView.setBackgroundResource(R.drawable.circle_yellow );
			}
			else{
				textView.setTag(1);
				textView.setBackgroundResource(drawableBG);
			}

			textView.setEnabled(true);

			//zoom in anim
			handler.sendMessageDelayed(Message.obtain(handler, WHAT_ZOOM_SEVEN, textView), (i+1) * ANIM_TIME);
		}
	}

	void reshuffleQuestion(){

		wordJumbled = shuffle(wordUnique);
		System.out.println("wordOrig = " +wordOrig + ", wordAnto = "+ wordAnto + " ,word = " +word );

		ScaleAnimation scale1 = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
		scale1.setFillAfter(true);
		scale1.setDuration(ANIM_TIME);

		for (int i = 0 ; i < layoutSeven.getChildCount(); i++){

			CustomTextView textView = (CustomTextView)layoutSeven.getChildAt(i);

			textView.setText("" + wordJumbled.charAt(i));

			if ( ("" + wordJumbled.charAt(i)).equalsIgnoreCase(repeatedLetter()) ){
				textView.setTag(2);
				textView.setBackgroundResource(R.drawable.circle_yellow );
			}
			else{
				textView.setTag(1);
				textView.setBackgroundResource(drawableBG);
			}

			textView.setEnabled(true);
			textView.startAnimation(scale1);
		}
	}

	String roundOffTo1DecPlaces(float val)
	{
		return String.format("%.1f", val);
	}

	private final MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler{
		private final WeakReference<GameScreen> actInstance;

		public MyHandler(GameScreen activity){
			actInstance = new WeakReference<GameScreen>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			GameScreen gameScreen = actInstance.get();

			switch (msg.what) {

			case WHAT_FLOAT:

				TranslateAnimation floaty = new TranslateAnimation( 0, 5 , 0, 0);
				floaty.setInterpolator(new CycleInterpolator(1));
				floaty.setDuration(2000);
				floaty.setRepeatMode(Animation.RESTART);
				floaty.setRepeatCount(Animation.INFINITE);
				( (CustomTextView) msg.obj).startAnimation(floaty);
				break;

			case WHAT_ZOOM_SEVEN:
				Sound.playSound(Sound.SOUND_START);
				ScaleAnimation scale1 = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
				scale1.setFillAfter(true);
				scale1.setDuration(ANIM_TIME);
				( (CustomTextView) msg.obj).startAnimation(scale1);
				break;

			case WHAT_GAME_OVER:

//				completion = Float.valueOf ( gameScreen.roundOffTo1DecPlaces( (score * 100f )/ (WordsStorage.words.size() + WordsStorage.words2.size()) )) ;
				completion = (int) (   (score * 100f ) / ( WordsStorage.words.size() + WordsStorage.words2.size() ) );

				gameScreen.preferences.edit().putInt("score", score).apply();
				gameScreen.preferences.edit().putInt("completion", completion).apply();

				if (gameScreen.preferences.getInt("high_score", 0) < score)
					gameScreen.preferences.edit().putInt("high_score", score).apply();


				gameScreen.timeupPopup.dismiss();

				Intent intent = new Intent(gameScreen, GameOverScreen.class);
				intent.putExtra("correct_answer", gameScreen.currentPair);
				intent.putExtra("score", score);
				intent.putExtra("completion", completion);
				intent.putExtra("drawableBG", drawableBG);

				gameScreen.startActivity(intent);

				//game over..checkin score
				if(gameScreen.mConnected){
					Games.Leaderboards.submitScore(gameScreen.googleApiClient, 
							gameScreen.getResources().getString(R.string.leaderboard_7_letters), score);
				}

				gameScreen.finish();

				break;

			case WHAT_TIME_UP:

				Sound.pauseSound(Sound.SOUND_CLOCK_START);
				Sound.playSound(Sound.SOUND_TIME_UP);

				for(int i = 0 ; i < gameScreen.layoutSeven.getChildCount() ; i++){

					CustomTextView textView = (CustomTextView)gameScreen.layoutSeven.getChildAt(i);

					ScaleAnimation scale2 = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
					scale2.setFillAfter(true);
					scale2.setDuration(500);
					textView.startAnimation(scale2);
				}

				//show timeup popup
				LayoutInflater inflater = (LayoutInflater)
						gameScreen.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				gameScreen.timeupPopup = new PopupWindow(
						inflater.inflate(R.layout.popup_timeup, null, false), 
						LayoutParams.WRAP_CONTENT,  
						LayoutParams.WRAP_CONTENT, 
						true);

				gameScreen.timeupPopup.setAnimationStyle(R.style.Animation);
				gameScreen.timeupPopup.showAtLocation(gameScreen.findViewById(R.id.lay_seven), Gravity.CENTER, 0, 0); 


				break;

			case WHAT_GAME_WON:

				gameScreen.mProgressBar.clearAnimation();
				gameScreen.mCountDownTimer.cancel();
				gameScreen.mCountDownTimer = null;

				Sound.playSound(Sound.SOUND_GAME_WON);

				for(int i = 0 ; i < gameScreen.layoutSeven.getChildCount() ; i++){

					CustomTextView textView = (CustomTextView)gameScreen.layoutSeven.getChildAt(i);

					ScaleAnimation scale2 = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
					scale2.setFillAfter(true);
					scale2.setDuration(500);
					textView.startAnimation(scale2);
				}

				//show timeup popup
				LayoutInflater inflater1 = (LayoutInflater)
						gameScreen.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				gameScreen.timeupPopup = new PopupWindow(
						inflater1.inflate(R.layout.popup_timeup, null, false), 
						LayoutParams.WRAP_CONTENT,  
						LayoutParams.WRAP_CONTENT, 
						true);

				((TextView) gameScreen.timeupPopup.getContentView().
						findViewById(R.id.tv_popup)).setText("YOU WON!");

				gameScreen.timeupPopup.setAnimationStyle(R.style.Animation);
				gameScreen.timeupPopup.showAtLocation(gameScreen.findViewById(R.id.lay_seven), Gravity.CENTER, 0, 0); 


				break;

			case WHAT_KILL_ACT:

//				completion = Float.valueOf ( gameScreen.roundOffTo1DecPlaces( (score * 100f )/ (WordsStorage.words.size() + WordsStorage.words2.size()) )) ;
				completion = (int) (   (score * 100f ) / ( WordsStorage.words.size() + WordsStorage.words2.size() ) ) ;

				gameScreen.preferences.edit().putInt("score", score).apply();
				gameScreen.preferences.edit().putInt("completion", completion).apply();

				if (gameScreen.preferences.getInt("high_score", 0) < score)
					gameScreen.preferences.edit().putInt("high_score", score).apply();


				gameScreen.timeupPopup.dismiss();

				Intent intent1 = new Intent(gameScreen, GameOverScreen.class);
				intent1.putExtra("correct_answer", new ArrayList<String>() {{ add("WORD") ; add("BOOK"); }} );
				intent1.putExtra("score", score);
				intent1.putExtra("completion", completion);
				intent1.putExtra("drawableBG", drawableBG);

				gameScreen.startActivity(intent1);
				gameScreen.finish();				

				break;

			case WHAT_WRONG_ANS_WORD:
				AnimationSet animSet = new AnimationSet(false);
				animSet.setDuration(500);

				//zoom out
				ScaleAnimation scale33 = new ScaleAnimation(1f, 1.25f, 1f, 1.25f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
				animSet.addAnimation(scale33);

				//shake
				TranslateAnimation translate33 = new TranslateAnimation( 0, 20 , 0, 0);
				translate33.setInterpolator(new CycleInterpolator(4));
				animSet.addAnimation(translate33);

				//zoom in
				ScaleAnimation scaleIn33 = new ScaleAnimation(1.25f, 0f, 1.25f, 0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
				scaleIn33.setFillAfter(false);
				animSet.addAnimation(scaleIn33);

				( (CustomTextView) msg.obj).startAnimation(animSet);
				break;

			case WHAT_RESET_WORD:

				CustomTextView tvClicked = ( (CustomTextView) msg.obj);

				if (tvClicked.getTag() != null){

					//remove floating anim frm the cliked butt
					tvClicked.clearAnimation();

					CustomTextView quesTv = (CustomTextView)gameScreen.findViewById(Integer.parseInt(tvClicked.getTag().toString()));

					if (tvClicked.getText().toString().equalsIgnoreCase(gameScreen.repeatedLetter())){

						quesTv.setBackgroundResource(R.drawable.circle_yellow);

					}else{

						quesTv.setBackgroundResource(drawableBG);
					}
					if(quesTv.getTag().toString().equalsIgnoreCase("1") && tvClicked.getText().toString().equalsIgnoreCase(gameScreen.repeatedLetter()) )
						quesTv.setTag(2);
					else
						quesTv.setTag(1);

					quesTv.setEnabled(true);
					ScaleAnimation scale11 = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
					scale11.setFillAfter(true);
					scale11.setDuration(ANIM_TIME);
					quesTv.startAnimation(scale11);

					tvClicked.setBackgroundResource(R.drawable.circle_white);
					tvClicked.setText("");
					tvClicked.setTag(null);
				}

				break;

			case WHAT_WRONG_ANS_ANTO:

				for(int i = 0 ; i < gameScreen.layoutAntonym.getChildCount() ; i++){

					AnimationSet animSet2 = new AnimationSet(false);
					animSet2.setDuration(500);

					CustomTextView textView = (CustomTextView)gameScreen.layoutAntonym.getChildAt(i);

					//zoom out
					ScaleAnimation scale = new ScaleAnimation(1f, 1.25f, 1f, 1.25f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
					animSet2.addAnimation(scale);

					//shake
					TranslateAnimation translate = new TranslateAnimation( 0, 20 , 0, 0);
					translate.setInterpolator(new CycleInterpolator(4));
					animSet2.addAnimation(translate);

					//zoom in
					ScaleAnimation scaleIn = new ScaleAnimation(1.25f, 0f, 1.25f, 0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
					scaleIn.setFillAfter(false);
					animSet2.addAnimation(scaleIn);

					textView.startAnimation(animSet2);
				}
				break;

			case WHAT_RESET_ANTO:

				//place all the letters of this back to Quest
				for(int i = 0 ; i < gameScreen.layoutAntonym.getChildCount() ; i++){

					CustomTextView textView = (CustomTextView)gameScreen.layoutAntonym.getChildAt(i);
					textView.setBackgroundResource(R.drawable.circle_white);

					CustomTextView quesTv = (CustomTextView)gameScreen.findViewById(Integer.parseInt(textView.getTag().toString()));

					if (textView.getText().toString().equalsIgnoreCase(gameScreen.repeatedLetter())){
						quesTv.setBackgroundResource(R.drawable.circle_yellow);
					}
					else{
						quesTv.setBackgroundResource(drawableBG);
					}

					if((quesTv.getTag().toString().equalsIgnoreCase("1")) && (textView.getText().toString().equalsIgnoreCase(gameScreen.repeatedLetter())))
						quesTv.setTag(2);
					else
						quesTv.setTag(1); 

					quesTv.setEnabled(true);

					textView.setText(""); 
					textView.setTag(null);

					ScaleAnimation scale2 = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
					scale2.setFillAfter(true);
					scale2.setDuration(500);
					quesTv.startAnimation(scale2);
				}
				break;

			case WHAT_CORRECT_ANS_WORD:

				for(int i = 0 ; i < 4 ; i++){

					AnimationSet animSet3 = new AnimationSet(false);
					animSet3.setDuration(500);

					CustomTextView textView = (CustomTextView)gameScreen.layoutWord.getChildAt(i); 

					//					textView.setBackgroundResource(R.drawable.circle_green_dark);
					//					textView.setEnabled(false);

					//zoom out
					ScaleAnimation scale3 = new ScaleAnimation(1f, 1.25f, 1f, 1.25f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
					scale3.setDuration(500);
					animSet3.addAnimation(scale3);

					//zoom in
					ScaleAnimation scaleIn = new ScaleAnimation(1.25f, 0f, 1.25f, 0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
					scaleIn.setFillAfter(false);
					animSet3.addAnimation(scaleIn);

					textView.startAnimation(animSet3);
				}

				break;

			case WHAT_CORRECT_ANS_ANTO:

				for(int i = 0 ; i < 4 ; i++){

					AnimationSet animSet4 = new AnimationSet(false);
					animSet4.setDuration(500);

					CustomTextView textView = (CustomTextView)gameScreen.layoutAntonym.getChildAt(i); 

					//					textView.setBackgroundResource(R.drawable.circle_green_dark);
					textView.setEnabled(false);

					//zoom out
					ScaleAnimation scale3 = new ScaleAnimation(1f, 1.25f, 1f, 1.25f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
					scale3.setDuration(500);
					animSet4.addAnimation(scale3);

					//zoom in
					ScaleAnimation scaleIn = new ScaleAnimation(1.25f, 0f, 1.25f, 0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
					scaleIn.setFillAfter(false);
					animSet4.addAnimation(scaleIn);

					textView.startAnimation(animSet4);
				}
				break;

			case WHAT_CLEANUP_AND_RELOAD:

				gameScreen.cleanSlate();
				gameScreen.setupQuest();
				break;

			default:
				break;
			}
		}
	}

	private void checkPuzzleComplete(){

		//check word in layoutWord
		String layWordString = "" , layAntoString = "";

		for(int i = 0 ; i < 4 ; i++){
			layWordString += ( (CustomTextView)layoutWord.getChildAt(i) ).getText();
			layAntoString += ( (CustomTextView)layoutAntonym.getChildAt(i) ).getText();
		}

		if( 
				( layWordString.equalsIgnoreCase(wordOrig) && layAntoString.equalsIgnoreCase(wordAnto)) ||
				( layAntoString.equalsIgnoreCase(wordOrig) && layWordString.equalsIgnoreCase(wordAnto)) 
				) {
			//PUZZLE SOLVED !!

			score++;
//			completion = Float.valueOf ( roundOffTo1DecPlaces( (score * 100f )/ (WordsStorage.words.size() + WordsStorage.words2.size()) )) ;
			completion = (int) (   (score * 100f ) / ( WordsStorage.words.size() + WordsStorage.words2.size() ) );

			int dictionaryProgress =  (int) (   (score * 100f ) / ( WordsStorage.words.size() + WordsStorage.words2.size() ) );

			if(mConnected){
				switch (dictionaryProgress) {
				case 1:
					mAchievementsUnlocked.case1();
					break;
				case 2:
					mAchievementsUnlocked.case2();
					break;
				case 3:
					mAchievementsUnlocked.case3();
					break;
				case 4:
					mAchievementsUnlocked.case4();
					break;
				case 5:
					mAchievementsUnlocked.case5();
					break;
				case 10:
					mAchievementsUnlocked.case10();
					break;
				case 25:
					mAchievementsUnlocked.case25();
					break;
				case 50:
					mAchievementsUnlocked.case50();
					break;
				case 75:
					mAchievementsUnlocked.case75();
					break;
				case 100:
					mAchievementsUnlocked.case100();
					break;
				default:
					break;
				}
			}

			if ( wordsUsed == (WordsStorage.words.size()  + WordsStorage.words2.size() )){
				//if game won !!
				handler.sendEmptyMessageDelayed(WHAT_GAME_WON , 500);
				handler.sendEmptyMessageDelayed(WHAT_KILL_ACT , 3000);

				//game won..checkin score
				if(mConnected){
					Games.Leaderboards.submitScore(googleApiClient, 
							getResources().getString(R.string.leaderboard_7_letters), score);
				}
			}
			else{

				handler.sendEmptyMessage(WHAT_CORRECT_ANS_WORD);
				handler.sendEmptyMessage(WHAT_CORRECT_ANS_ANTO);
				handler.sendEmptyMessageDelayed(WHAT_CLEANUP_AND_RELOAD , 500);
			}

			if (timeLeft >= (gameTime - bonusTime) )
				startTimer(gameTime);
			else
				startTimer(timeLeft + bonusTime);


		}
	}

	private void cleanSlate(){
		System.out.println("ankur enter cleanSlate");
		for(int i = 0 ; i < 4 ; i++){

			( (CustomTextView)layoutWord.getChildAt(i) ).setText("");
			( (CustomTextView)layoutAntonym.getChildAt(i) ).setText("");

			( (CustomTextView)layoutWord.getChildAt(i) ).setTag(null);
			( (CustomTextView)layoutAntonym.getChildAt(i) ).setTag(null);

			( (CustomTextView)layoutWord.getChildAt(i) ).setBackgroundResource(R.drawable.circle_white);
			( (CustomTextView)layoutAntonym.getChildAt(i) ).setBackgroundResource(R.drawable.circle_white);

			( (CustomTextView)layoutWord.getChildAt(i) ).setEnabled(true);
			( (CustomTextView)layoutAntonym.getChildAt(i) ).setEnabled(true);

		}

		for (int i = 0 ; i < 7 ; i++){
			( (CustomTextView)layoutSeven.getChildAt(i) ).clearAnimation();
			( (CustomTextView)layoutSeven.getChildAt(i) ).setVisibility(View.INVISIBLE);
		}
		System.out.println("ankur Done cleaning");
	}

	boolean firstWordFinalized = false;

	boolean validateEntry(CustomTextView nextAvailableTV, String text){

		CustomTextView textView;

		//check in layoutWord
		for (int i = 0 ; i < 4 ; i++){

			textView = (CustomTextView)layoutWord.getChildAt(i);

			if (textView.getId() == nextAvailableTV.getId()){

				//decide if its the common word.. then still allotment cant be done

				if(  (text.equalsIgnoreCase( wordOrig.charAt(i) + "")) && (firstWordFinalized == false) ){

					layoutWord.setTag(1);
					layoutAntonym.setTag(2);
					firstWordFinalized = true;
				}
				else if( (text.equalsIgnoreCase( wordAnto.charAt(i) + "")) && (firstWordFinalized == false) ){

					layoutWord.setTag(2);
					layoutAntonym.setTag(1);
					firstWordFinalized = true;
				}

				//check if origWord belongs to layoutWord and selected letter fits
				if ( (layoutWord.getTag() == Integer.valueOf("1")) && (text.equalsIgnoreCase( wordOrig.charAt(i) + "")) ){
					nextAvailableTV.setBackgroundResource(R.drawable.circle_green_dark);
					nextAvailableTV.setEnabled(false);
					Sound.playSound(Sound.SOUND_CORRECT);
					return true;
				}

				//check if origWord belongs to layoutAnto and selected letter fits
				else if ( (layoutWord.getTag() == Integer.valueOf("2")) && (text.equalsIgnoreCase( wordAnto.charAt(i) + "")) ){
					nextAvailableTV.setBackgroundResource(R.drawable.circle_green_dark);
					nextAvailableTV.setEnabled(false);
					Sound.playSound(Sound.SOUND_CORRECT);
					return true;
				}
			}
		}

		//check in layoutAnto
		for (int i = 0 ; i < 4 ; i++){

			textView = (CustomTextView)layoutAntonym.getChildAt(i);

			if (textView.getId() == nextAvailableTV.getId()){

				if( (text.equalsIgnoreCase( wordOrig.charAt(i) + "")) && (firstWordFinalized == false) ){

					layoutWord.setTag(2);
					layoutAntonym.setTag(1);
				}
				else if( (text.equalsIgnoreCase( wordAnto.charAt(i) + "")) && (firstWordFinalized == false) ){

					layoutWord.setTag(1);
					layoutAntonym.setTag(2);
					firstWordFinalized = true;
				}				

				//check if origWord belongs to layoutWord and selected letter fits
				if ( (layoutAntonym.getTag() == Integer.valueOf("1")) && (text.equalsIgnoreCase( wordOrig.charAt(i) + "")) ){
					nextAvailableTV.setBackgroundResource(R.drawable.circle_green_dark);
					nextAvailableTV.setEnabled(false);
					Sound.playSound(Sound.SOUND_CORRECT);
					return true;
				}
				//check if origWord belongs to layoutWord and selected letter fits
				else if ( (layoutAntonym.getTag() == Integer.valueOf("2")) && (text.equalsIgnoreCase( wordAnto.charAt(i) + "")) ){
					nextAvailableTV.setBackgroundResource(R.drawable.circle_green_dark);

					nextAvailableTV.setEnabled(false);
					Sound.playSound(Sound.SOUND_CORRECT);
					return true;
				}
			}
		}

		//INCORRECT LETTER
		nextAvailableTV.setBackgroundResource(drawableBG);
		Sound.playSound(Sound.SOUND_WRONG);
		handler.sendMessageDelayed(Message.obtain(handler, WHAT_WRONG_ANS_WORD, nextAvailableTV), 100);
		handler.sendMessageDelayed(Message.obtain(handler, WHAT_RESET_WORD, nextAvailableTV), 600);
		return false;

	}

	private final ClickListener clickListener = new ClickListener(this);
	private static class ClickListener implements OnClickListener{

		private final WeakReference<GameScreen> actInstance ;

		public ClickListener(GameScreen activity){
			actInstance = new WeakReference<GameScreen>(activity);
		}

		@Override
		public void onClick(View v) {

			GameScreen mainActivity = actInstance.get();

			CustomTextView tvClicked = (CustomTextView)v;

			if (tvClicked.isEnabled() == true){
				CustomTextView nextAvailableTv = mainActivity.getNextAvailableTv();

				//****** Something on QUESTION clicked *********//
				if (mainActivity.isPartOfSeven(tvClicked)){

					//if the button carrying repeat char clicked
					if (tvClicked.getTag().toString().equalsIgnoreCase("2") ){

						if ( tvClicked.getText().toString().equalsIgnoreCase(mainActivity.repeatedLetter())){
							tvClicked.setBackgroundResource(R.drawable.circle_yellow);
						}
						else{

							tvClicked.setBackgroundResource(drawableBG);
						}
						tvClicked.setTag(1);
					}

					else if (tvClicked.getTag().toString().equalsIgnoreCase("1") ){   

						tvClicked.setTag(0);
						tvClicked.setBackgroundResource(R.drawable.circle_white);
						tvClicked.setEnabled(false);
					}


					nextAvailableTv.setText("" + tvClicked.getText() );

					mainActivity.validateEntry(nextAvailableTv , "" + tvClicked.getText());

					nextAvailableTv.setTag(tvClicked.getId());

					//make word and auto buttons float
					mainActivity.handler.sendMessage(Message.obtain(mainActivity.handler, WHAT_FLOAT, nextAvailableTv));

					//check for correct ans 
					if (nextAvailableTv.getId() == mainActivity.tvWordAnto4.getId())
						mainActivity.checkPuzzleComplete();

				}
			}
		}
	}

	private boolean isPartOfSeven(CustomTextView tvClicked) {
		for(int i = 0 ; i < layoutSeven.getChildCount(); i++){
			if (tvClicked.getId() == layoutSeven.getChildAt(i).getId())
				return true;
		}

		return false;
	}


	private CustomTextView getNextAvailableTv(){

		for(int i = 0 ; i < layoutWord.getChildCount() ; i++){

			CustomTextView textView = (CustomTextView)layoutWord.getChildAt(i);

			if (textView.getText().length() == 0){
				return textView;
			}
		}

		for(int i = 0 ; i < layoutAntonym.getChildCount() ; i++){

			CustomTextView textView = (CustomTextView)layoutAntonym.getChildAt(i);

			if (textView.getText().length() == 0){
				return textView;
			}
		}

		return null;
	}

	private String removeDuplicates(String userKeyword){

		int charLength = userKeyword.length();
		String modifiedKeyword="";
		for(int i=0;i<charLength;i++)
		{
			if(!modifiedKeyword.contains(userKeyword.charAt(i)+""))
				modifiedKeyword+=userKeyword.charAt(i);
		}
		return modifiedKeyword;
	}

	private String shuffle(String input){
		List<Character> characters = new ArrayList<Character>();
		for(char c:input.toCharArray()){
			characters.add(c);
		}
		StringBuilder output = new StringBuilder(input.length());
		while(characters.size()!=0){
			int randPicker = (int)(Math.random()*characters.size());
			output.append(characters.remove(randPicker));
		}
		return output.toString();
	}

	private void initUI(){

		blinkAnimation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
		blinkAnimation.setDuration(300); // duration - half a second
		blinkAnimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		blinkAnimation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
		blinkAnimation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in

		this.mAdView = ((AdView)findViewById(R.id.google_ad_main));

		layoutAd  = (RelativeLayout)findViewById(R.id.lay_ad);
		//		layoutParent = (RelativeLayout)findViewById(R.id.lay_game_screen);
		layoutWord = (RelativeLayout)findViewById(R.id.lay_word);
		layoutAntonym = (RelativeLayout)findViewById(R.id.lay_antonym);

		mProgressBar = (ProgressBar) findViewById(R.id.progressBarToday);

		tvWord1 = (CustomTextView)findViewById(R.id.tv_word1);
		tvWord2 = (CustomTextView)findViewById(R.id.tv_word2);
		tvWord3 = (CustomTextView)findViewById(R.id.tv_word3);
		tvWord4 = (CustomTextView)findViewById(R.id.tv_word4);

		tvWordAnto1 = (CustomTextView)findViewById(R.id.tv_anto1);
		tvWordAnto2 = (CustomTextView)findViewById(R.id.tv_anto2);
		tvWordAnto3 = (CustomTextView)findViewById(R.id.tv_anto3);
		tvWordAnto4 = (CustomTextView)findViewById(R.id.tv_anto4);

		layoutSeven = (RelativeLayout)findViewById(R.id.lay_seven);
		tvSeven1 = (CustomTextView)findViewById(R.id.test0);
		tvSeven2 = (CustomTextView)findViewById(R.id.test1);
		tvSeven3 = (CustomTextView)findViewById(R.id.test2);
		tvSeven4 = (CustomTextView)findViewById(R.id.test3);
		tvSeven5 = (CustomTextView)findViewById(R.id.test4);
		tvSeven6 = (CustomTextView)findViewById(R.id.test5);
		tvSeven7 = (CustomTextView)findViewById(R.id.test6);
		tvSeven1.setOnClickListener(clickListener);
		tvSeven2.setOnClickListener(clickListener);
		tvSeven3.setOnClickListener(clickListener);
		tvSeven4.setOnClickListener(clickListener);
		tvSeven5.setOnClickListener(clickListener);
		tvSeven6.setOnClickListener(clickListener);
		tvSeven7.setOnClickListener(clickListener);

		tvWord1.setOnClickListener(clickListener);
		tvWord2.setOnClickListener(clickListener);
		tvWord3.setOnClickListener(clickListener);
		tvWord4.setOnClickListener(clickListener);

		tvWordAnto1.setOnClickListener(clickListener);
		tvWordAnto2.setOnClickListener(clickListener);
		tvWordAnto3.setOnClickListener(clickListener);
		tvWordAnto4 .setOnClickListener(clickListener);



	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, StartingScreen.class));
		finish();
		super.onBackPressed();
	}

}
