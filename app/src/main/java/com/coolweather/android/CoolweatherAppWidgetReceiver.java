package com.coolweather.android;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.app.PendingIntent;
import android.widget.TextView;

import com.coolweather.android.gson.WeatherCond;
import com.coolweather.android.service.ClockService;

import com.coolweather.android.R;
import com.coolweather.android.WeatherActivity;
import com.coolweather.android.util.Utility;
import com.coolweather.android.util.WeatherIconJud;

/**
 * Created by qr on 2018/5/19.
 */

public class CoolweatherAppWidgetReceiver extends AppWidgetProvider {
    public static final String CLICK_ACTION = "com.coolweather.android.action.UPDATE";
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            Log.i("zyq", "AppWidgetReceiver:onReceive");
            RemoteViews rViews = new RemoteViews(context.getPackageName(), R.layout.coolweather_widget_layout);
            // 显示当前事件
           showWeather(context,rViews);
            // 刷新
            AppWidgetManager manager = AppWidgetManager
                    .getInstance(context);
            ComponentName cName = new ComponentName(context,CoolweatherAppWidgetReceiver.class);
            manager.updateAppWidget(cName, rViews);
        }

        @Override
        public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            super.onUpdate(context, appWidgetManager, appWidgetIds);
            Log.i("zyq", "AppWidgetReceiver:onUpdate");
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.coolweather_widget_layout);
            PendingIntent i = PendingIntent.getActivity(context, 0, new Intent(context, WeatherActivity.class), 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_layout, i);
                showWeather(context,remoteViews);
                appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        }

        private void showWeather(Context context, RemoteViews remoteViews) {
            WeatherCond weatherCond = null;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String weatherCondString = prefs.getString("weatherCond", null);
            if (weatherCondString != null) {
                weatherCond = Utility.handlerWeatherNowResponse(weatherCondString);
            }
            String cityName = weatherCond.basic.cityName;
            String currentTemperature = weatherCond.now.tmp + "°";
            String tiganTemperature = weatherCond.now.bobyTemperature;
            String weatherInfo = weatherCond.now.weatherInfo;
            String maxTemp;
            remoteViews.setTextViewText(R.id.widget_city_name, cityName);
            remoteViews.setTextViewText(R.id.widget_temp,currentTemperature);
            remoteViews.setTextViewText(R.id.widget_tigan_temp, "体感温度 " + tiganTemperature + "°");
            remoteViews.setTextViewText(R.id.widget_weather_info, weatherInfo);
        }

        @Override
        public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
            super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        }

        @Override
        public void onDeleted(Context context, int[] appWidgetIds) {
            super.onDeleted(context, appWidgetIds);
            Log.i("zyq", "AppWidgetReceiver:onDeleted");
        }

        @Override
        public void onEnabled(Context context) {
            super.onEnabled(context);
            context.startService(new Intent(context, ClockService.class));
            Log.i("zyq", "AppWidgetReceiver:onEnabled");
        }

        @Override
        public void onDisabled(Context context) {
            super.onDisabled(context);
            context.stopService(new Intent(context, ClockService.class));
            Log.i("zyq", "AppWidgetReceiver:onDisabled");
        }

        @Override
        public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
            super.onRestored(context, oldWidgetIds, newWidgetIds);
            Log.i("zyq", "AppWidgetReceiver:onRestored");
        }
    }

