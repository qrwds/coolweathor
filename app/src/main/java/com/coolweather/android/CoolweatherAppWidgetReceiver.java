package com.coolweather.android;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.app.PendingIntent;
import android.widget.TextView;

import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.gson.WeatherCond;
import com.coolweather.android.service.ClockService;

import com.coolweather.android.R;
import com.coolweather.android.WeatherActivity;
import com.coolweather.android.util.BitmapAlpha;
import com.coolweather.android.util.FontToBitmap;
import com.coolweather.android.util.Utility;
import com.coolweather.android.util.WeatherIconJud;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by qr on 2018/5/19.
 */

public class CoolweatherAppWidgetReceiver extends AppWidgetProvider {
    public static final String UPDATE_ACTION = "com.coolweather.android.action.UPDATE";
    public static final String CHANGE_ALPHA_ACTION="com.coolweather.android.action.CHANGE_ALPHA";
    private int bgImageId=-1;
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            RemoteViews rViews = new RemoteViews(context.getPackageName(), R.layout.coolweather_widget_layout);
            switch (intent.getAction()){
                case CoolweatherAppWidgetReceiver.UPDATE_ACTION:
                    // 显示当前事件
                    showWeather(context, rViews);
                    // 刷新
                    break;
                case CoolweatherAppWidgetReceiver.CHANGE_ALPHA_ACTION:
                    int alphaValue=intent.getIntExtra("widget_alpha",0);
                    Bitmap bitmap1= BitmapAlpha.getTransparentBitmap(BitmapFactory.decodeResource(context.getResources(),bgImageId),alphaValue);
                    rViews.setImageViewBitmap(R.id.backgroup_image,bitmap1);
                    break;
                default:
                    break;
            }
            AppWidgetManager manager = AppWidgetManager
                    .getInstance(context);
            ComponentName cName = new ComponentName(context, CoolweatherAppWidgetReceiver.class);
            manager.updateAppWidget(cName, rViews);
        }

        @Override
        public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            super.onUpdate(context, appWidgetManager, appWidgetIds);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.coolweather_widget_layout);
            PendingIntent i = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_layout, i);
                showWeather(context,remoteViews);
                appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        }

        private void showWeather(Context context, RemoteViews remoteViews) {
            WeatherCond weatherCond = null;
            Weather weather=null;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String weatherCondString = prefs.getString("weatherCond", null);
            String weatherString=prefs.getString("weather",null);
            if (weatherCondString != null) {
                remoteViews.setViewVisibility(R.id.widget_right_layout, View.VISIBLE);
                //remoteViews.setViewVisibility(R.id.widget_left_layout,View.VISIBLE);
                remoteViews.setViewVisibility(R.id.widget_center_text, View.GONE);
                weatherCond = Utility.handlerWeatherNowResponse(weatherCondString);
            }else {
               remoteViews.setViewVisibility(R.id.widget_center_text, View.VISIBLE);
               remoteViews.setViewVisibility(R.id.widget_right_layout, View.INVISIBLE);
               // remoteViews.setViewVisibility(R.id.widget_left_layout,View.INVISIBLE);
               return;
            }
            if (weatherString!=null){
                weather=Utility.handlerWeatherResponse(weatherString);
            }
            String cityName = weatherCond.basic.cityName;
            String currentTemperature = weatherCond.now.tmp + "° ";
            String tiganTemperature = weatherCond.now.bobyTemperature;
            String weatherInfo = weatherCond.now.weatherInfo;
            String weatherIconUnicode=WeatherIconJud.getWeatherIconFontUnicode(weatherInfo);
            String maxTemp=weather.forecastList.get(1).temperature.max;
            String minTemp=weather.forecastList.get(1).temperature.min;
            remoteViews.setTextViewText(R.id.widget_city_name, cityName);
            remoteViews.setTextViewText(R.id.widget_temp,currentTemperature);
            remoteViews.setTextViewText(R.id.widget_temp_min,minTemp + "°");
            remoteViews.setTextViewText(R.id.widget_temp_max,maxTemp + "°");
            remoteViews.setTextViewText(R.id.widget_tigan_temp, "体感温度 " + tiganTemperature + "°");
            remoteViews.setTextViewText(R.id.widget_weather_info, weatherInfo);
            remoteViews.setImageViewResource(R.id.backgroup_image,bgImageId=backgroundSelect(weatherInfo));
            if (!weatherInfo.equals("no_find")){
                remoteViews.setImageViewBitmap(R.id.widget_weather_icon, FontToBitmap.buildUpdate(weatherIconUnicode,context));
            }
        }

        @Override
        public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
            super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        }

        @Override
        public void onDeleted(Context context, int[] appWidgetIds) {
            super.onDeleted(context, appWidgetIds);
        }

        @Override
        public void onEnabled(Context context) {
            super.onEnabled(context);
            context.startService(new Intent(context, ClockService.class));

        }

        @Override
        public void onDisabled(Context context) {
            super.onDisabled(context);
            context.stopService(new Intent(context, ClockService.class));
            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putInt("widget_alpha",0);
            editor.apply();
        }

        @Override
        public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
            super.onRestored(context, oldWidgetIds, newWidgetIds);

        }
    private int backgroundSelect(String weatherInfo){
        Calendar c= Calendar.getInstance();
        int mons=c.get(Calendar.MONTH)+1;
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String time = sdf.format(new Date());
        if (Integer.parseInt(time)>20){
                if (weatherInfo.equals("多云") || weatherInfo.equals("晴转多云")){
                    return R.drawable.night_partlycloudy_2x4;
                }else if (weatherInfo.equals("阴")){
                    return R.drawable.night_cloudy_2x4;
                }else if(weatherInfo.equals("晴")||weatherInfo.equals("多云转晴")){
                    return R.drawable.night_clearsky_2x4;
                }else if (weatherInfo.indexOf('雨')!=-1 && !weatherInfo.equals("雨夹雪")){
                    return R.drawable.night_rain_2x4;
                }else if (weatherInfo.indexOf('雪')!=-1){
                    return R.drawable.night_snow_2x4;
                }
        }else if(mons>=3&&mons<=8){
            if (weatherInfo.equals("多云") || weatherInfo.equals("晴转多云")){
                return R.drawable.day_cloudy_2x4;
            }else if (weatherInfo.equals("阴")){
                return R.drawable.day_cloudy_2x4;
            }else if(weatherInfo.equals("晴")||weatherInfo.equals("多云转晴")){
                return R.drawable.day_clearsky_2x4;
            }else if (weatherInfo.indexOf('雨')!=-1 && !weatherInfo.equals("雨夹雪")){
                return R.drawable.day_rain_2x4;
            }else if (weatherInfo.indexOf('雪')!=-1){
                return R.drawable.day_snow_2x4;
            }else if (weatherInfo.indexOf('雾')!=-1){
                return R.drawable.day_fog_2x4;
            }
        }else {
            if (weatherInfo.equals("多云") || weatherInfo.equals("晴转多云")){
                return R.drawable.winter_day_cloudy_2x4;
            }else if (weatherInfo.equals("阴")){
                return R.drawable.winter_day_cloudy_2x4;
            }else if(weatherInfo.equals("晴")||weatherInfo.equals("多云转晴")){
                return R.drawable.winter_day_clearsky_2x4;
            }else if (weatherInfo.indexOf('雨')!=-1 && !weatherInfo.equals("雨夹雪")){
                return R.drawable.winter_day_rain_2x4;
            }else if (weatherInfo.indexOf('雪')!=-1){
                return R.drawable.day_snow_2x4;
            }else if (weatherInfo.indexOf('雾')!=-1){
                return R.drawable.winter_day_fog_2x4;
            }
        }
        return -1;
    }
    }

