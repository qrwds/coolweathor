package com.coolweather.android.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import com.coolweather.android.R;

/**
 * Created by qr on 2018/5/11.
 */

public class WeatherIconJud {

    public static void  getWeathetIcon(Typeface iconfont, TextView textView,String weatherInfo){
        if ("晴".equals(weatherInfo)){
            textView.setText(R.string.icon_qing);
            textView.setTypeface(iconfont);
        }else if ("多云".equals(weatherInfo)){
            textView.setText(R.string.icon_duoyun);
            textView.setTypeface(iconfont);
        }else if (weatherInfo.endsWith("雪")){
            if (!"雨夹雪".equals(weatherInfo)) {
                textView.setText(R.string.icon_xue);
                textView.setTypeface(iconfont);
            }
        }else if ("阴".equals(weatherInfo)){
            textView.setText(R.string.icon_yin);
            textView.setTypeface(iconfont);
        }else if (weatherInfo.endsWith("雨")){
            if ("小雨".equals(weatherInfo)||"中雨".equals(weatherInfo)||"雨夹雪".equals(weatherInfo)){
                textView.setText(R.string.icon_xiaoyu);
                textView.setTypeface(iconfont);
            }else {
                textView.setText(R.string.icon_dayu);
                textView.setTypeface(iconfont);
            }
        }else if (weatherInfo.indexOf("雷")!=-1){
            textView.setText(R.string.icon_lei);
            textView.setTypeface(iconfont);
        }
    }
}
