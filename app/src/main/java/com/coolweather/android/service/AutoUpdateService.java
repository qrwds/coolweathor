package com.coolweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.MediaMetadataCompat;
import android.widget.Toast;

import com.coolweather.android.CoolweatherAppWidgetReceiver;
import com.coolweather.android.WeatherActivity;
import com.coolweather.android.db.AddCity;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.gson.WeatherCond;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
   private boolean flag=false;
    private boolean isRequestWeather=false;
    private boolean isRequestWeatherCond=false;
    private boolean isError=false;
    public AutoUpdateService() {
    }
    @Override
    public void onCreate() {
        /*AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour=3*60*60*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);*/
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        //updateWeatherCond();
       // updateWeather();
        //updateBingPic();
            updateAddCity();
        try{ Thread.sleep(3000);}catch (Exception e){}
        Intent i1 = new Intent("com.coolweather.android.LOCAL_BROADCAST_UPDATE_SHOW");
        LocalBroadcastManager.getInstance(this).sendBroadcast(i1);
        Intent intent1 = new Intent(CoolweatherAppWidgetReceiver.UPDATE_ACTION);
        this.sendBroadcast(intent1);
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            int anHour = 3 * 60 * 60 * 1000;
            long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
            Intent i = new Intent(this, AutoUpdateService.class);
            PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
            manager.cancel(pi);
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent,flags,startId);
    }

    private void updateWeatherCond() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weatherCond", null);
        if (weatherString != null) {
            WeatherCond weatherCond = Utility.handlerWeatherNowResponse(weatherString);
            String weatherId = weatherCond.basic.weatherId;
            String weatherUrl = "https://free-api.heweather.com/s6/weather/now?location=" + weatherId + "&key=f360abdf1f8a43e8bbb77cd68a3e8977";
            HttpUtil.sendOkHttpResquest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseText = response.body().string();
                    final WeatherCond weatherCond = Utility.handlerWeatherNowResponse(responseText);
                    if (weatherCond != null && "ok".equals(weatherCond.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weatherCond", responseText);
                        editor.apply();
                    }
                }
            });
        }
    }
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString!= null) {
            Weather weather=Utility.handlerWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=f360abdf1f8a43e8bbb77cd68a3e8977";
            HttpUtil.sendOkHttpResquest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseText = response.body().string();
                    final Weather weather = Utility.handlerWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                        AddCity addCity = new AddCity();
                        addCity.setResponseText(responseText);
                        addCity.updateAll("cityname=?",weather.basic.cityName);
                    }
                }
            });
        }
    }
    private void updateAddCity(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weatherCond", null);
        if (weatherString!=null) {
            List<AddCity> addCityList= DataSupport.findAll(AddCity.class);
            WeatherCond weatherCond = Utility.handlerWeatherNowResponse(weatherString);
            final String weatherNowId = weatherCond.basic.weatherId;
            for (AddCity addCity : addCityList) {
                final String weatherId = addCity.getWeatherId();
                String weatherUrl = "https://free-api.heweather.com/s6/weather/now?location=" + weatherId + "&key=f360abdf1f8a43e8bbb77cd68a3e8977";
                HttpUtil.sendOkHttpResquest(weatherUrl, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        isError=true;
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseText = response.body().string();
                        final WeatherCond weatherCond = Utility.handlerWeatherNowResponse(responseText);
                        if (weatherCond != null && "ok".equals(weatherCond.status)) {
                            if (weatherNowId.equals(weatherId)) {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                                editor.putString("weatherCond", responseText);
                                editor.apply();
                            }
                            AddCity addCity = new AddCity();
                            addCity.setWeatherInfo(weatherCond.now.weatherInfo);
                            addCity.setWeatherTmp(weatherCond.now.tmp);
                            addCity.setResponseCondText(responseText);
                            addCity.updateAll("cityname=?", weatherCond.basic.cityName);
                            isRequestWeatherCond=true;
                        }else {
                            isError=true;
                        }
                    }
                });
                String weatherUrl1 = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=f360abdf1f8a43e8bbb77cd68a3e8977";
                HttpUtil.sendOkHttpResquest(weatherUrl1, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        isError=true;
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseText = response.body().string();
                        final Weather weather = Utility.handlerWeatherResponse(responseText);
                        if (weather != null && "ok".equals(weather.status)) {
                            if (weatherNowId.equals(weatherId)) {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                                editor.putString("weather", responseText);
                                editor.apply();
                            }
                            AddCity addCity = new AddCity();
                            addCity.setResponseText(responseText);
                            addCity.updateAll("cityname=?",weather.basic.cityName);
                            isRequestWeather=true;
                        }else {
                            isError=true;
                        }
                    }
                });
            }
        }
    }
    private void updateBingPic(){
        String requestBingPic="http://guolin.tech./api/bing_pic";
        HttpUtil.sendOkHttpResquest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                        String bingPic=response.body().string();
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
