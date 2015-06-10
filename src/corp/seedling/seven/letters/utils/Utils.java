package corp.seedling.seven.letters.utils;

import java.util.Random;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.DisplayMetrics;
import corp.seedling.seven.letters.R;
import corp.seedling.seven.letters.ui.GameApp;

public class Utils {

	public static final int SDK_VERSION = Build.VERSION.SDK_INT;
	
	public static final String SEVEN_LETTERS_URL_HTTP = "https://play.google.com/store/apps/details?id=corp.seedling.seven.letters";
	public static final String SEVEN_LETTERS_URL = "market://details?id=corp.seedling.seven.letters";
	public static final String PUBLISHER_URL = "market://search?q=pub:Seedling Corp";
	
	public static final int[] COLOR_RES = {
		R.drawable.circle_blue,//0
		R.drawable.circle_blue_cyan,//1
		R.drawable.circle_blue_dark,//2
		R.drawable.circle_blue_purple,//3
		R.drawable.circle_red,//4
		R.drawable.circle_pink,//5
		R.drawable.circle_pink_light,//6
		R.drawable.circle_orange,//7
		R.drawable.circle_purple,//8
		R.drawable.circle_blue_royal//9
		
		
	};

//		R.drawable.circle_green_mehndi,//4
//		R.drawable.circle_green_dark,//10
//		R.drawable.circle_green_pak,//11
//		R.drawable.circle_green_shine,//12
//		R.drawable.circle_yellow_dirty,//15
	static int rand = -1;
	public static int getRandomColor(){
		
		Random r = new Random();
		int randInt = r.nextInt(10 - 0) + 0;
		
		while (rand == randInt){
			 randInt = r.nextInt(10 - 0) + 0;
		}
		
		rand = randInt;
		return COLOR_RES[rand] ;
	}
	
	

	public static Typeface getFont(Context paramContext)
	{
		return Typeface.createFromAsset(paramContext.getResources().getAssets(), "ArchivoBlack.otf");
	}

	public static int getSizeInPixels(int dp){
		return (int) ( (dp * GameApp.getAppInstance().getResources().getDisplayMetrics().density)  + 0.5f) ; 
	}

	public static int getSizeInDp(float px){
		DisplayMetrics metrics = GameApp.getAppInstance().getResources().getDisplayMetrics();
		return (int) (px / (metrics.densityDpi / 160f) );
	}

}
