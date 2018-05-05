package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.gson.WeatherCond;
import com.coolweather.android.gson.WeatherNow;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpCallbackListener;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView updateTimeText;
    private TextView currentWeatherText;
    private TextView weatherIconText;
    private TextView currentMax;
    private TextView currentMin;
    private TextView currentWeatherInfo;
    private TextView tigan;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private TextView fengSu;
    private TextView shiDu;
    private TextView fengSuIcon;
    private TextView shiDuIcon;
    private TextView ziWaiXianIcon;
    private Typeface iconfont;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefreshLayout;
    private String mWeatherId;
    public DrawerLayout drawerLayout;
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        updateTimeText=(TextView)findViewById(R.id.update_time);
        titleCity=(TextView)findViewById(R.id.title_city);
        currentWeatherText=(TextView)findViewById(R.id.temperature_current);
        currentWeatherInfo=(TextView)findViewById(R.id.weather_info);
        currentMax=(TextView)findViewById(R.id.temperature_max);
        currentMin=(TextView)findViewById(R.id.temperature_min);
        tigan=(TextView)findViewById(R.id.tigan);
        weatherIconText=(TextView)findViewById(R.id.weather_icon);
        forecastLayout=(LinearLayout)findViewById(R.id.forcecast_layout);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        comfortText=(TextView)findViewById(R.id.comfort_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);
        fengSu=(TextView)findViewById(R.id.fengsui_text);
        shiDu=(TextView)findViewById(R.id.shidu_text);
        shiDuIcon=(TextView)findViewById(R.id.shidu_icon);
        fengSuIcon=(TextView)findViewById(R.id.fengsu_icon);
        ziWaiXianIcon=(TextView)findViewById(R.id.ziwaixian_icon);
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navButton=(Button)findViewById(R.id.nav_button);
        iconfont=Typeface.createFromAsset(getAssets(),"iconfont/iconfont.ttf");
        shiDuIcon.setTypeface(iconfont);
        fengSuIcon.setTypeface(iconfont);
        ziWaiXianIcon.setTypeface(iconfont);
        navButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        String weatherCondString=prefs.getString("weatherCond",null);
        if (weatherCondString!=null){
            WeatherCond weatherCond=Utility.handlerWeatherNowResponse(weatherCondString);
            mWeatherId=weatherCond.basic.weatherId;
            showWeatherCondInfo(weatherCond);
        }else {
            mWeatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeatherCond(mWeatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                requestWeatherCond(mWeatherId);
            }
        });
        if(weatherString!=null){
            Weather weather= Utility.handlerWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }else {
            String weatherId=getIntent().getStringExtra("weather_id");
            requestWeather(weatherId);
        }
        String bingPic=prefs.getString("bing_pic",null);
        if (bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
            loadBingPic();
        }
    }
    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpResquest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                    final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
    public void requestWeather(final String weatherId){
        String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=f360abdf1f8a43e8bbb77cd68a3e8977";
        HttpUtil.sendOkHttpResquest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取预报天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                    final String responseText=response.body().string();
                    final Weather weather=Utility.handlerWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取预报天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    public void requestWeatherCond(final String weatherId){
        String weatherUrl="https://free-api.heweather.com/s6/weather/now?location="+weatherId+"&key=f360abdf1f8a43e8bbb77cd68a3e8977";
        HttpUtil.sendOkHttpResquest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取实况天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final WeatherCond weatherCond=Utility.handlerWeatherNowResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weatherCond!=null&&"ok".equals(weatherCond.status)){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weatherCond",responseText);
                            editor.apply();
                            mWeatherId=weatherCond.basic.weatherId;
                            showWeatherCondInfo(weatherCond);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取实况天气信息失败..",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }
    private void showWeatherCondInfo(WeatherCond weatherCond){
        String cityName=weatherCond.basic.cityName;
        String updateTime=weatherCond.update.updateLocTime.split(" ")[1];
        String currentTemperature=weatherCond.now.tmp+"°";
        String tiganTemperature=weatherCond.now.bobyTemperature;
        String weatherNowInfo=weatherCond.now.weatherInfo;
        String fengsuNow=(int)(Math.rint(Integer.parseInt(weatherCond.now.windSpd)/3.6))+"";
        String shiduNow=weatherCond.now.hum;

        titleCity.setText(cityName);
        currentWeatherText.setText(currentTemperature);
        currentWeatherInfo.setText(weatherNowInfo);
        tigan.setText("体感温度 "+tiganTemperature+"°");
        fengSu.setText(fengsuNow+"米/秒");
        shiDu.setText(shiduNow+"%");
        updateTimeText.setText("更新时间："+updateTime);
        if ("晴".equals(weatherNowInfo)){
            weatherIconText.setText(R.string.icon_qing);
            weatherIconText.setTypeface(iconfont);
        }else if ("多云".equals(weatherNowInfo)){
            weatherIconText.setText(R.string.icon_duoyun);
            weatherIconText.setTypeface(iconfont);
        }else if (weatherNowInfo.endsWith("雪")){
            if (!"雨夹雪".equals(weatherNowInfo)) {
                weatherIconText.setText(R.string.icon_xue);
                weatherIconText.setTypeface(iconfont);
            }
        }else if ("阴".equals(weatherNowInfo)){
            weatherIconText.setText(R.string.icon_yin);
            weatherIconText.setTypeface(iconfont);
        }else if (weatherNowInfo.endsWith("雨")){
            if ("小雨".equals(weatherNowInfo)||"中雨".equals(weatherNowInfo)||"雨夹雪".equals(weatherNowInfo)){
                weatherIconText.setText(R.string.icon_xiaoyu);
                weatherIconText.setTypeface(iconfont);
            }else {
                weatherIconText.setText(R.string.icon_dayu);
                weatherIconText.setTypeface(iconfont);
            }
        }else if (weatherNowInfo.indexOf("雷")!=-1){
            weatherIconText.setText(R.string.icon_lei);
            weatherIconText.setTypeface(iconfont);
        }
    }
    private void showWeatherInfo(Weather weather){
        forecastLayout.removeAllViews();
        int count=0;
        for (Forecast forecast:weather.forecastList){
         View view= LayoutInflater.from(this).inflate(R.layout.forcecast_item,forecastLayout,false);
            String yuaboInfo=forecast.more.info;
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView yobaoIcon=(TextView)view.findViewById(R.id.yubao_weather_icon);
            TextView minmaxTem=(TextView)view.findViewById(R.id.max_min_text);
            dateText.setText(forecast.date);
            minmaxTem.setText(forecast.temperature.max+"°/"+forecast.temperature.min+"°");
            if (count==0){
                currentMin.setText(forecast.temperature.min+"°");
                currentMax.setText(forecast.temperature.max+"°");
                count=1;
                continue;
            }
            if ("晴".equals(yuaboInfo)){
                yobaoIcon.setText(R.string.icon_qing);
                yobaoIcon.setTypeface(iconfont);
            }else if (yuaboInfo.endsWith("多云")){
                yobaoIcon.setText(R.string.icon_duoyun);
                yobaoIcon.setTypeface(iconfont);
            }else if (yuaboInfo.endsWith("雪")){
                if (!"雨夹雪".equals(yuaboInfo)) {
                    yobaoIcon.setText(R.string.icon_xue);
                    yobaoIcon.setTypeface(iconfont);
                }
            }else if ("阴".equals(yuaboInfo)){
                yobaoIcon.setText(R.string.icon_yin);
                yobaoIcon.setTypeface(iconfont);
            }else if (yuaboInfo.endsWith("雨")){
                if ("小雨".equals(yuaboInfo)||"中雨".equals(yuaboInfo)||"雨夹雪".equals(yuaboInfo)){
                    yobaoIcon.setText(R.string.icon_xiaoyu);
                    yobaoIcon.setTypeface(iconfont);
                }else {
                    yobaoIcon.setText(R.string.icon_dayu);
                    yobaoIcon.setTypeface(iconfont);
                }
            }else if (yuaboInfo.indexOf("雷")!=-1){
                yobaoIcon.setText(R.string.icon_lei);
                yobaoIcon.setTypeface(iconfont);
            }
            else {
                yobaoIcon.setText(yuaboInfo);
            }
            forecastLayout.addView(view);
        }
        if (weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort="舒适度: "+weather.suggestion.comfort.info;
        String carwash="洗车指数: "+weather.suggestion.carWash.info;
        String sport="运动指数: "+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carwash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}