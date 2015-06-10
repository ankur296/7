package corp.seedling.seven.letters.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;
import corp.seedling.seven.letters.utils.Utils;

public class CustomTextViewSettings
  extends TextView
{
  private static Typeface FONT_NAME;
  
  public CustomTextViewSettings(Context paramContext) 
  {
    super(paramContext);
    if (FONT_NAME == null) {
      FONT_NAME = Utils.getFont(paramContext);
    }
    setTypeface(FONT_NAME);
    setTextSize(20);
    setTextColor(Color.WHITE);//ffff0000 -red
    setGravity(Gravity.CENTER);
  }
  
  public CustomTextViewSettings(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    if (FONT_NAME == null) {
      FONT_NAME = Utils.getFont(paramContext);
    }
    setTypeface(FONT_NAME);
    setTextSize(20);
    setTextColor(Color.WHITE);
    setGravity(Gravity.CENTER);
  }
  
  public CustomTextViewSettings(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    if (FONT_NAME == null) {
      FONT_NAME = Utils.getFont(paramContext);
    }
    setTypeface(FONT_NAME);
    setTextSize(20);
    setTextColor(Color.WHITE);
    setGravity(Gravity.CENTER);
  }
}
