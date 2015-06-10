package corp.seedling.seven.letters.utils;

import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import corp.seedling.seven.letters.R;
import corp.seedling.seven.letters.ui.GameApp;

public class Sound {

	private static final int rIdCorrect = R.raw.touch;
	private static int sidCorrect = -1;
	private static final int rIdOver= R.raw.gameover;
	private static int sidOver = -1;
	private static final int rIdStart = R.raw.start;
	private static int sidStart = -1;
	private static final int rIdWrong = R.raw.wrong; 
	private static int sidWrong = -1;
	private static final int rIdClock = R.raw.clock;
	private static int sidClock = -1;
	private static final int rIdTimeup = R.raw.timeup;
	private static int sidTimeup = -1;
	private static final int rIdScore = R.raw.score;
	private static int sidScore = -1;
	private static final int rIdGenericPress = R.raw.coverbuttonpressed;
	private static int sidGenericPress = -1;
	private static int streamIdClock = -1;
	private static boolean loaded = false;
	private static SoundPool soundPool;
	public static final int SOUND_CORRECT = 2;
	public static final int SOUND_GAME_WON  = 3;
	public static final int SOUND_START = 4;
	public static final int SOUND_WRONG   = 5;
	public static final int SOUND_CLOCK_START   = 6;
	public static final int SOUND_TIME_UP   = 7;
	public static final int SOUND_SCORE   = 8;
	public static final int SOUND_GENERIC_PRESS   = 9;
	
	public static boolean sfx = true;

	public static SoundPool.OnLoadCompleteListener listener =  
			new SoundPool.OnLoadCompleteListener(){
		@Override
		public void onLoadComplete(SoundPool soundPool, int sid, int status){ // could check status value here also

			if (sidCorrect == sid || sidOver == sid || sidStart == sid || sidWrong == sid) {
				loaded = true;
			}
		}
	};

	public static void initSound() {
		System.out.println("ankur init sound");
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 100);
		soundPool.setOnLoadCompleteListener(listener);

		sidClock = soundPool.load(GameApp.getAppInstance(), rIdClock, 1);
		sidCorrect = soundPool.load(GameApp.getAppInstance(), rIdCorrect, 1);
		sidOver = soundPool.load(GameApp.getAppInstance(), rIdOver, 1);
		sidStart = soundPool.load(GameApp.getAppInstance(), rIdStart, 1);
		sidWrong = soundPool.load(GameApp.getAppInstance(), rIdWrong, 1);
		sidScore= soundPool.load(GameApp.getAppInstance(), rIdScore, 1);
		sidTimeup = soundPool.load(GameApp.getAppInstance(), rIdTimeup, 1);
		sidGenericPress = soundPool.load(GameApp.getAppInstance(), rIdGenericPress, 1);
	
		sfx = PreferenceManager.getDefaultSharedPreferences(GameApp.getAppInstance()).getBoolean("sfx", true);
	}

	public static void unloadSound() {
		soundPool.setOnLoadCompleteListener(null);
		soundPool.unload(sidCorrect); 
		soundPool.unload(sidOver); 
		soundPool.unload(sidStart); 
		soundPool.unload(sidWrong);
		soundPool.unload(sidClock);
		soundPool.unload(sidTimeup);
		soundPool.unload(sidScore);
		soundPool.unload(sidGenericPress);
		soundPool.release();
	}


	public static void pauseSound(final int soundType) {
		if (soundType == SOUND_CLOCK_START)
			soundPool.stop(streamIdClock);
	}

	public static void playSound(final int soundType) {

		if (sfx == true){

			new Thread(new Runnable() {
				@Override
				public void run() {
					if (loaded) {

						switch (soundType) {

						case SOUND_CORRECT:
							soundPool.play(sidCorrect, 1.0f, 1.0f, 0, 0, 1f);
							break;

						case SOUND_GAME_WON:
							soundPool.play(sidOver, 1.0f, 1.0f, 0, 0, 1f);
							break;

						case SOUND_START:
							soundPool.play(sidStart, 1.0f, 1.0f, 0, 0, 1f);
							break;

						case SOUND_WRONG:
							soundPool.play(sidWrong, 1.0f, 1.0f, 0, 0, 1f);
							break;

						case SOUND_TIME_UP:
							soundPool.play(sidTimeup, 1.0f, 1.0f, 0, 0, 1f);
							break;

						case SOUND_SCORE:
							soundPool.play(sidScore, 1.0f, 1.0f, 0, 0, 1f);
							break;

						case SOUND_GENERIC_PRESS:
							soundPool.play(sidGenericPress, 1.0f, 1.0f, 0, 0, 1f);
							break;

						case SOUND_CLOCK_START:
							streamIdClock = soundPool.play(sidClock, 1.0f, 1.0f, 1, -1, 2f);
							break;
						}

					}
				} }).start();

		}
	}
}
