package corp.seedling.seven.letters.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;
import corp.seedling.seven.letters.R;
import corp.seedling.seven.letters.utils.Utils;

public class CustomTextViewHeading
  extends TextView
{
  private static Typeface FONT_NAME ;
  
  public CustomTextViewHeading(Context paramContext) 
  {
    super(paramContext);
    if (FONT_NAME == null) {
      FONT_NAME = Typeface.createFromAsset(paramContext.getResources().getAssets(), "h1.otf");
    }
    setTypeface(FONT_NAME);
    setTextSize(25);
    setTextColor(getResources().getColor(R.color.game_dark_green));//ffff0000 -red
    setGravity(Gravity.CENTER);
  }
  
  public CustomTextViewHeading(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    if (FONT_NAME == null) {
        FONT_NAME = Typeface.createFromAsset(paramContext.getResources().getAssets(), "h1.otf");
      }
    setTypeface(FONT_NAME);
    setTextSize(25);
    setTextColor(getResources().getColor(R.color.game_dark_green));
    setGravity(Gravity.CENTER);
  }
  
  public CustomTextViewHeading(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    if (FONT_NAME == null) {
        FONT_NAME = Typeface.createFromAsset(paramContext.getResources().getAssets(), "h1.otf");
      }
    setTypeface(FONT_NAME);
    setTextSize(25);
    setTextColor(getResources().getColor(R.color.game_dark_green));
    setGravity(Gravity.CENTER);
  }
}
