package corp.seedling.seven.letters.ui;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import corp.seedling.seven.letters.R;
import corp.seedling.seven.letters.utils.Sound;

public class HelpScreen extends Activity{

	public static final int WHAT_REMOVE_WORD1 = 1;
	public static final int WHAT_SEND_FIRST_LETTER = 11;
	public static final int WHAT_SHAKE_WRONG_ANS = 12;
	public static final int WHAT_BRING_BACK_WRONG_ANS = 13;
	public static final int WHAT_BRING_BACK_WRONG_ANS_2 = 14;
	public static final int WHAT_SEND_SEC_LETTER = 15;
	public static final int WHAT_SEND_THIRD_LETTER = 16;
	public static final int WHAT_SEND_FOURTH_LETTER = 17;
	public static final int WHAT_SEND_FIFTH_LETTER = 18;
	public static final int WHAT_SEND_SIXTH_LETTER = 19;
	public static final int WHAT_SHOW_PLAY = 20;

	Animation blinkAnimation;
	RelativeLayout layoutSeven , layoutWord, layoutAntonym, layoutParent, layoutAd ; 
	CustomTextView tvWord1 ,tvWord2 ,tvWord3 ,tvWord4 ;
	CustomTextView tvWordAnto1 ,tvWordAnto2 ,tvWordAnto3 ,tvWordAnto4 , tvNext ;
	CustomTextView tvSeven1 ,tvSeven2 ,tvSeven3 ,tvSeven4, tvSeven5, tvSeven6, tvSeven7 ;
	CustomTextViewHelp tvHeading;
	static int drawableBG;
	int pressCounter = 0;
	SharedPreferences preferences;
	boolean launched_over_play = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_screen);
		initUI();
		zoomAnswers();

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		launched_over_play = getIntent().getBooleanExtra("launched_over_play", false);
	}

	@Override
	public void onBackPressed() {
		if (launched_over_play == true)
			startActivity(new Intent(this, GameScreen.class));
		else
			startActivity(new Intent(this, StartingScreen.class));
		super.onBackPressed();

	}
	private void zoomAnswers() {

		for (int i = 0 ; i < 4 ; i++){
			( (CustomTextView)layoutWord.getChildAt(i) ).startAnimation(zoom);
			( (CustomTextView)layoutAntonym.getChildAt(i) ).startAnimation(zoom);
		}
	}


	private final MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler{
		private final WeakReference<HelpScreen> actInstance;

		public MyHandler(HelpScreen activity){
			actInstance = new WeakReference<HelpScreen>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			HelpScreen gameScreen = actInstance.get();

			switch (msg.what) {

			case WHAT_SEND_FIRST_LETTER:
				gameScreen.tvSeven4.setBackgroundResource(R.drawable.circle_white);
				gameScreen.tvWord1.setBackgroundResource(R.drawable.circle_pink_light);
				gameScreen.tvWord1.setText(""+"P");
				break;

			case WHAT_SEND_SEC_LETTER:
				gameScreen.tvSeven2.setBackgroundResource(R.drawable.circle_white);
				gameScreen.tvWord1.setBackgroundResource(R.drawable.circle_green_dark);
				gameScreen.tvWord1.setText(""+"S");
				break;

			case WHAT_SEND_THIRD_LETTER:
				gameScreen.tvSeven1.setBackgroundResource(R.drawable.circle_white);
				gameScreen.tvWord2.setBackgroundResource(R.drawable.circle_pink_light);
				gameScreen.tvWord2.setText(""+"A");
				break;

			case WHAT_SEND_FOURTH_LETTER:
				gameScreen.tvSeven5.setBackgroundResource(R.drawable.circle_yellow);
				gameScreen.tvWord2.setBackgroundResource(R.drawable.circle_green_dark);
				gameScreen.tvWord2.setText(""+"T");
				break;

			case WHAT_SEND_FIFTH_LETTER:
				gameScreen.tvSeven3.setBackgroundResource(R.drawable.circle_pink_light);
				gameScreen.tvWord3.setBackgroundResource(R.drawable.circle_green_dark);
				gameScreen.tvWord3.setText(""+"O");
				break;

			case WHAT_SEND_SIXTH_LETTER:
				gameScreen.tvSeven4.setBackgroundResource(R.drawable.circle_pink_light);
				gameScreen.tvWord4.setBackgroundResource(R.drawable.circle_green_dark);
				gameScreen.tvWord4.setText(""+"P");
				break;

			case WHAT_SHOW_PLAY:
				gameScreen.tvNext.setBackgroundResource(R.drawable.circle_play);
				ScaleAnimation zoom = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
				zoom.setFillAfter(true);
				zoom.setDuration(1000);
				zoom.setRepeatMode(Animation.RESTART);
				zoom.setRepeatCount(Animation.INFINITE);
				gameScreen.tvNext.startAnimation(zoom);

				break;

			case WHAT_SHAKE_WRONG_ANS:
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

			case WHAT_BRING_BACK_WRONG_ANS:

				gameScreen.tvWord1.setBackgroundResource(R.drawable.circle_white);
				gameScreen.tvWord1.setText("");
				ScaleAnimation scale11 = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
				scale11.setFillAfter(true);
				scale11.setDuration(500);
				gameScreen.tvSeven4.startAnimation(scale11);
				gameScreen.tvSeven4.setBackgroundResource(R.drawable.circle_pink_light);
				break;

			case WHAT_BRING_BACK_WRONG_ANS_2:

				gameScreen.tvWord2.setBackgroundResource(R.drawable.circle_white);
				gameScreen.tvWord2.setText("");
				ScaleAnimation scale22 = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
				scale22.setFillAfter(true);
				scale22.setDuration(500);
				gameScreen.tvSeven1.startAnimation(scale22);
				gameScreen.tvSeven1.setBackgroundResource(R.drawable.circle_pink_light);
				break;
			}
		}
	}

	private final MyClickListener listener = new MyClickListener(this);
	private static class MyClickListener implements OnClickListener{
		private final WeakReference<HelpScreen> actInstance;


		public MyClickListener(HelpScreen activity) {
			actInstance = new WeakReference<HelpScreen>(activity);
		}

		@Override
		public void onClick(View v) {

			HelpScreen gameScreen = actInstance.get();

			Sound.playSound(Sound.SOUND_GENERIC_PRESS);

			if (v.getId() == gameScreen.tvNext.getId()){

				gameScreen.pressCounter ++ ;

				switch (gameScreen.pressCounter) {

				case 1:
					gameScreen.tvHeading.setText("HIGHLIGHTED letter can\nbe used TWICE");

					for (int i = 0 ; i < 4 ; i++){
						( (CustomTextView)gameScreen.layoutWord.getChildAt(i) ).clearAnimation();
						( (CustomTextView)gameScreen.layoutAntonym.getChildAt(i) ).clearAnimation();
					}

					gameScreen.tvSeven5.startAnimation(gameScreen.zoom);
					break;

				case 2:
					gameScreen.tvHeading.setText("Letters at the correct\nplace will turn GREEN");

					//1st
					gameScreen.handler.sendEmptyMessageDelayed(WHAT_SEND_FIRST_LETTER, 1000);
					gameScreen.handler.sendMessageDelayed(Message.obtain(gameScreen.handler, WHAT_SHAKE_WRONG_ANS, gameScreen.tvWord1), 2000);
					gameScreen.handler.sendEmptyMessageDelayed(WHAT_BRING_BACK_WRONG_ANS, 2500);

					//2nd
					gameScreen.handler.sendEmptyMessageDelayed(WHAT_SEND_SEC_LETTER, 4000);

					//3rd
					gameScreen.handler.sendEmptyMessageDelayed(WHAT_SEND_THIRD_LETTER, 5000);
					gameScreen.handler.sendMessageDelayed(Message.obtain(gameScreen.handler, WHAT_SHAKE_WRONG_ANS, gameScreen.tvWord2), 6000);
					gameScreen.handler.sendEmptyMessageDelayed(WHAT_BRING_BACK_WRONG_ANS_2, 6500);

					//4th
					gameScreen.handler.sendEmptyMessageDelayed(WHAT_SEND_FOURTH_LETTER, 8000);

					//5th
					gameScreen.handler.sendEmptyMessageDelayed(WHAT_SEND_FIFTH_LETTER, 9000);

					//6th
					gameScreen.handler.sendEmptyMessageDelayed(WHAT_SEND_SIXTH_LETTER, 10000);

					//show play
					gameScreen.handler.sendEmptyMessageDelayed(WHAT_SHOW_PLAY, 11000);

					gameScreen.tvSeven5.clearAnimation();


					break;

				case 3:
					gameScreen.startActivity(new Intent(gameScreen, GameScreen.class));
					gameScreen.finish();
					break;

				default:
					break;
				}
			}
		}
	}

	ScaleAnimation zoom ;
	TranslateAnimation floaty;
	private void initUI(){

		zoom = new ScaleAnimation(0.5f, 1.2f, 0.5f, 1.2f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
		zoom.setFillAfter(true);
		zoom.setDuration(1000);
		zoom.setRepeatMode(Animation.RESTART);
		zoom.setRepeatCount(Animation.INFINITE);

		floaty = new TranslateAnimation( 0, 10 , 0, 0);
		floaty.setInterpolator(new CycleInterpolator(1));
		floaty.setDuration(1000);
		floaty.setRepeatMode(Animation.RESTART);
		floaty.setRepeatCount(Animation.INFINITE);

		blinkAnimation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
		blinkAnimation.setDuration(1000); // duration - half a second
		blinkAnimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		blinkAnimation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
		blinkAnimation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in

		layoutWord = (RelativeLayout)findViewById(R.id.lay_word_help);
		layoutAntonym = (RelativeLayout)findViewById(R.id.lay_antonym_help);

		tvNext = (CustomTextView)findViewById(R.id.tv_help_next);
		tvNext.setOnClickListener(listener);
		tvHeading = (CustomTextViewHelp)findViewById(R.id.tv_text_help);
		tvHeading.setText("Form 2 SYNONYMS from\nthe given 7 letters");

		tvWord1 = (CustomTextView)findViewById(R.id.tv_word1_help);
		tvWord2 = (CustomTextView)findViewById(R.id.tv_word2_help);
		tvWord3 = (CustomTextView)findViewById(R.id.tv_word3_help);
		tvWord4 = (CustomTextView)findViewById(R.id.tv_word4_help);

		tvWordAnto1 = (CustomTextView)findViewById(R.id.tv_anto1_help);
		tvWordAnto2 = (CustomTextView)findViewById(R.id.tv_anto2_help);
		tvWordAnto3 = (CustomTextView)findViewById(R.id.tv_anto3_help);
		tvWordAnto4 = (CustomTextView)findViewById(R.id.tv_anto4_help);

		layoutSeven = (RelativeLayout)findViewById(R.id.lay_seven_help);
		tvSeven1 = (CustomTextView)findViewById(R.id.test0_help);
		tvSeven1.setText(""+"A");
		tvSeven2 = (CustomTextView)findViewById(R.id.test1_help);
		tvSeven2.setText(""+"S");
		tvSeven3 = (CustomTextView)findViewById(R.id.test2_help);
		tvSeven3.setText(""+"O");
		tvSeven4 = (CustomTextView)findViewById(R.id.test3_help);
		tvSeven4.setText(""+"P");
		tvSeven5 = (CustomTextView)findViewById(R.id.test4_help);
		tvSeven5.setText(""+"T");
		tvSeven5.setBackgroundResource(R.drawable.circle_yellow );
		tvSeven6 = (CustomTextView)findViewById(R.id.test5_help);
		tvSeven6.setText(""+"L");
		tvSeven7 = (CustomTextView)findViewById(R.id.test6_help);
		tvSeven7.setText(""+"H");
	}

	@Override
	protected void onDestroy() {

		if (handler != null){
			handler.removeCallbacksAndMessages(null);
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

}
