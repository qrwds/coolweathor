package com.coolweather.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.coolweather.android.R;

import org.w3c.dom.Text;

/**
 * Created by qr on 2018/5/21.
 */

public class FontToBitmap {
   public static Bitmap buildUpdate(String time, Context context){
        Bitmap myBitmap = Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_4444);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        Typeface tf= Typeface.createFromAsset(context.getAssets(),"iconfont/iconfont.ttf");
        paint.setAntiAlias(true);
        paint.setAlpha(255);//取值范围为0~255，值越小越透明
        paint.setSubpixelText(true);
        paint.setTypeface(tf);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(95);
        paint.setTextAlign(Paint.Align.CENTER);
        myCanvas.drawText(time, 55,78, paint);
        return myBitmap;
    }
}
