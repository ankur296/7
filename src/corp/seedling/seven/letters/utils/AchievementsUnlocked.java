package corp.seedling.seven.letters.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import corp.seedling.seven.letters.R;

public class AchievementsUnlocked {

	private Activity mActivity;
	public GoogleApiClient mGoogleApiClient;
	private SharedPreferences preferences;

	public AchievementsUnlocked(Activity app, GoogleApiClient googleApiClient) {
		mActivity = app;
		mGoogleApiClient = googleApiClient;
		preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
	}

	public void case1() {

		if (preferences.getBoolean("Achievement_1", true) && mGoogleApiClient != null) {
			preferences.edit().putBoolean("Achievement_1", false).apply();
			UnlockAchievements(mActivity.getString(R.string.achievement_word_adept));
		}

	}

	public void case2() {

		if (preferences.getBoolean("Achievement_2", true) && mGoogleApiClient != null) {
			preferences.edit().putBoolean("Achievement_2", false).apply();
			UnlockAchievements(mActivity.getString(R.string.achievement_quite_wordly));
		}

	}
	public void case3() {

		if (preferences.getBoolean("Achievement_3", true) && mGoogleApiClient != null) {
			preferences.edit().putBoolean("Achievement_3", false).apply();
			UnlockAchievements(mActivity.getString(R.string.achievement_word_gifted));
		}

	}

	public void case4() {

		if (preferences.getBoolean("Achievement_4", true) && mGoogleApiClient != null) {
			preferences.edit().putBoolean("Achievement_4", false).apply();
			UnlockAchievements(mActivity.getString(R.string.achievement_word_whiz));
		}
	}

	public void case5() {

		if (preferences.getBoolean("Achievement_5", true) && mGoogleApiClient != null) {
			preferences.edit().putBoolean("Achievement_5", false).apply();
			UnlockAchievements(mActivity.getString(R.string.achievement_fast_fingers));
		}

	}

	public void case10() {

		if (preferences.getBoolean("Achievement_10", true) && mGoogleApiClient != null) {
			preferences.edit().putBoolean("Achievement_10", false).apply();
			UnlockAchievements(mActivity.getString(R.string.achievement_word_sensation));
		}

	}

	public void case25() {

		if (preferences.getBoolean("Achievement_25", true) && mGoogleApiClient != null) {
			preferences.edit().putBoolean("Achievement_25", false).apply();
			UnlockAchievements(mActivity.getString(R.string.achievement_word_book));
		}

	}

	public void case50() {

		if (preferences.getBoolean("Achievement_50", true) && mGoogleApiClient != null) {
			preferences.edit().putBoolean("Achievement_50", false).apply();
			UnlockAchievements(mActivity.getString(R.string.achievement_word_perfect));
		}

	}

	public void case75() {

		if (preferences.getBoolean("Achievement_75", true) && mGoogleApiClient != null) {
			preferences.edit().putBoolean("Achievement_75", false).apply();
			UnlockAchievements(mActivity.getString(R.string.achievement_defeat_dictionary));
		}

	}

	public void case100() {

		if (preferences.getBoolean("Achievement_100", true) && mGoogleApiClient != null) {
			preferences.edit().putBoolean("Achievement_100", false).apply();
			UnlockAchievements(mActivity.getString(R.string.achievement_human_lexicon));
		}
	}


	public void UnlockAchievements(String id) {
		if (mGoogleApiClient != null)
			Games.Achievements.unlock(mGoogleApiClient, id);

	}

}
