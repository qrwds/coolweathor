package com.coolweather.android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.coolweather.android.db.AddCity;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.gson.WeatherCond;
import com.coolweather.android.gson.WeatherNow;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpCallbackListener;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;
import com.coolweather.android.util.WeatherIconJud;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefreshLayout;
    private String mWeatherId;
    public DrawerLayout drawerLayout;
    private Typeface iconfont;
    private Button navButton;
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private RecyclerView recyclerView;
    private List<AddCity> addCityList;
    private AddCityAdapter adapter;
    private LinearLayout addLocation;
    private LinearLayout setLayout;
    private  boolean locationSwitch=false;
    private String nowLocCityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.coolweather.android.LOCAL_BROADCAST");
        intentFilter.addAction("com.coolweather.android.LOCAL_BROADCAST_UPDATE_SHOW");
        intentFilter.addAction("com.coolweather.android.LOCAL_BROADCAST_LOCATION_STATE");
        localReceiver=new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);
        addCityList= DataSupport.findAll(AddCity.class);

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
        addLocation=(LinearLayout) findViewById(R.id.add_city_layout);
        setLayout=(LinearLayout)findViewById(R.id.set_layout);
        recyclerView=(RecyclerView)findViewById(R.id.add_city_recyclerview);
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
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(WeatherActivity.this,CityChoose.class);
                startActivity(intent);
            }
        });
        setLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(WeatherActivity.this,SetActivity.class);
                startActivity(intent);
            }
        });
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        locationSwitch=prefs.getBoolean("switch_state",false);
        nowLocCityName=prefs.getString("loc_city_name",null);
        //Toast.makeText(this,nowLocCityName,Toast.LENGTH_SHORT).show();
        adapter=new AddCityAdapter(addCityList,iconfont,this);
        if (locationSwitch&&nowLocCityName!=null){
            adapter.setLocationSwitch(locationSwitch,true,nowLocCityName);
        }
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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
        if(weatherString!=null){
            Weather weather= Utility.handlerWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }else {
            String weatherId=getIntent().getStringExtra("weather_id");
            requestWeather(weatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                requestWeatherCond(mWeatherId);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(WeatherActivity.this, AutoUpdateService.class);
                startService(intent);
            }
        });

      // View view= LayoutInflater.from(this).inflate(R.layout.add_locatin,recyclerView,false);
        //AddCityAdapter.ViewHolder viewHolder=new AddCityAdapter.ViewHolder(view);
      // recyclerView.addView(view,0);
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
                            if (DataSupport.where("cityname=?",weather.basic.cityName).find(AddCity.class).size()==0) {
                                AddCity addCity = new AddCity();
                                addCity.setResponseText(responseText);
                                addCity.save();
                            }else {

                                AddCity addCity = new AddCity();
                                addCity.setResponseText(responseText);
                                addCity.updateAll("cityname=?",weather.basic.cityName);
                            }
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
                            if (DataSupport.where("cityname=?",weatherCond.basic.cityName).find(AddCity.class).size()==0) {
                                AddCity addCity = new AddCity();
                                addCity.setWeatherId(weatherCond.basic.weatherId);
                                addCity.setCityName(weatherCond.basic.cityName);
                                addCity.setWeatherInfo(weatherCond.now.weatherInfo);
                                addCity.setWeatherTmp(weatherCond.now.tmp);
                                addCity.setResponseCondText(responseText);
                                addCityList.add(addCity);
                                addCity.save();
                               // adapter.notifyItemInserted(addCityList.size()-1);
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(addCityList.size()-1);
                            }else {
                                AddCity addCity = new AddCity();
                                addCity.setWeatherInfo(weatherCond.now.weatherInfo);
                                addCity.setWeatherTmp(weatherCond.now.tmp);
                                addCity.setResponseCondText(responseText);
                                addCity.updateAll("cityname=?",weatherCond.basic.cityName);
                                setAddCityListRefresh();
                                adapter.notifyDataSetChanged();
                            }
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取实况天气信息失败..",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }
    public void showWeatherCondInfo(WeatherCond weatherCond){
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
        WeatherIconJud.getWeathetIcon(iconfont,weatherIconText,weatherNowInfo);
        int bgImageId=-1;
       if ((bgImageId=backgroundSelect(weatherNowInfo))!=-1){
           bingPicImg.setImageResource(bgImageId);
       }else {
           SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
           String bingPic = prefs.getString("bing_pic", null);
           if (bingPic != null) {
               Glide.with(this).load(bingPic).into(bingPicImg);
           } else {
               loadBingPic();
           }
       }
    }
    public void showWeatherInfo(Weather weather){
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
            WeatherIconJud.getWeathetIcon(iconfont,yobaoIcon,yuaboInfo);
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
        Intent intent = new Intent(CoolweatherAppWidgetReceiver.UPDATE_ACTION);
        sendBroadcast(intent);
    }
   public void setAddCityListRefresh(){
           addCityList.clear();
           List<AddCity> list = DataSupport.findAll(AddCity.class);
       int i=0;  adapter.cityNameIndexMap.clear();
           for (AddCity addCity : list) {
               addCityList.add(addCity);
               adapter.cityNameIndexMap.put(addCity.getCityName(),i);
               i++;
             }
       if (locationSwitch){
            adapter.setLocationSwitch(locationSwitch,false,nowLocCityName);
       }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }
    private int backgroundSelect(String weatherInfo){
        Calendar c= Calendar.getInstance();
        int mons=c.get(Calendar.MONTH)+1;
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String time = sdf.format(new Date());
        if (Integer.parseInt(time)>20||Integer.parseInt(time)<3){
           if(mons>=3&&mons<=8) {
               if (weatherInfo.equals("多云") || weatherInfo.equals("晴转多云")) {
                   return R.drawable.night_partlycloudy;
               } else if (weatherInfo.equals("阴")) {
                   return R.drawable.night_cloudy;
               } else if (weatherInfo.equals("晴") || weatherInfo.equals("多云转晴")) {
                   return R.drawable.night_clearsky;
               } else if (weatherInfo.indexOf('雨') != -1 && !weatherInfo.equals("雨夹雪")) {
                   return R.drawable.night_rain;
               } else if (weatherInfo.indexOf('雪') != -1) {
                   return R.drawable.night_snow;
               }
           } else {
                   if (weatherInfo.equals("多云") || weatherInfo.equals("晴转多云")){
                       return R.drawable.winter_night_clearsky;
                   }else if (weatherInfo.equals("阴")){
                       return R.drawable.winter_night_cloudy;
                   }else if(weatherInfo.equals("晴")||weatherInfo.equals("多云转晴")){
                       return R.drawable.winter_night_clearsky;
                   }else if (weatherInfo.indexOf('雨')!=-1 && !weatherInfo.equals("雨夹雪")){
                       return R.drawable.winter_night_rain;
                   }else if (weatherInfo.indexOf('雪')!=-1){
                       return R.drawable.day_snow;
                   }
                }
        }
        else {
            if(mons>=3&&mons<=8) {
                if (weatherInfo.equals("多云") || weatherInfo.equals("晴转多云")) {
                    return R.drawable.day_partlycloudy;
                } else if (weatherInfo.equals("阴")) {
                    return R.drawable.day_cloudy;
                } else if (weatherInfo.equals("晴") || weatherInfo.equals("多云转晴")) {
                    return R.drawable.day_clearsky;
                } else if (weatherInfo.indexOf('雨') != -1 && !weatherInfo.equals("雨夹雪")) {
                    return R.drawable.day_rain;
                } else if (weatherInfo.indexOf('雪') != -1) {
                    return R.drawable.day_snow;
                }else if (weatherInfo.indexOf('雾')!=-1){
                    return R.drawable.day_fog;
                }
            } else {
                if (weatherInfo.equals("多云") || weatherInfo.equals("晴转多云")){
                    return R.drawable.winter_day_clearsky;
                }else if (weatherInfo.equals("阴")){
                    return R.drawable.winter_day_cloudy;
                }else if(weatherInfo.equals("晴")||weatherInfo.equals("多云转晴")){
                    return R.drawable.winter_day_clearsky;
                }else if (weatherInfo.indexOf('雨')!=-1 && !weatherInfo.equals("雨夹雪")){
                    return R.drawable.winter_day_rain;
                }else if (weatherInfo.indexOf('雪')!=-1){
                    return R.drawable.day_snow;
                }else if (weatherInfo.indexOf('雾')!=-1){
                    return R.drawable.winter_day_fog;
                }
            }
        }
        return -1;
    }
    public void setmWeatherId(String weatherId){
        mWeatherId=weatherId;
    }
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
           String weatherString=prefs.getString("weather",null);
          String weatherCondString=prefs.getString("weatherCond",null);
            switch (intent.getAction()){
                case "com.coolweather.android.LOCAL_BROADCAST":
                    String weatherId=intent.getStringExtra("weather_id");
                    requestWeatherCond(weatherId);
                    requestWeather(weatherId);
                    break;
                    /*String weatherId=prefs.getString("weather_id",null);
                    if (weatherId!=null) {
                        List<AddCity> list = DataSupport.where("weatherid=?", weatherId).find(AddCity.class);
                        if (list.size() != 0) {
                            if (weatherCondString != null) {
                                WeatherCond weatherCond = Utility.handlerWeatherNowResponse(weatherCondString);
                                mWeatherId = weatherCond.basic.weatherId;
                                showWeatherCondInfo(weatherCond);
                            } else {
                                requestWeatherCond(weatherId);
                            }
                            if (weatherString != null) {
                                Weather weather = Utility.handlerWeatherResponse(weatherString);
                                showWeatherInfo(weather);
                            } else {
                                requestWeather(weatherId);
                            }
                        }else {
                            requestWeatherCond(weatherId);
                            requestWeather(weatherId);
                        }
                    }*/
                case "com.coolweather.android.LOCAL_BROADCAST_UPDATE_SHOW":
                    if (weatherCondString!=null&&weatherString!=null){
                        WeatherCond weatherCond = Utility.handlerWeatherNowResponse(weatherCondString);
                        mWeatherId=weatherCond.basic.weatherId;
                        showWeatherCondInfo(weatherCond);
                        Weather weather = Utility.handlerWeatherResponse(weatherString);
                        showWeatherInfo(weather);
                        setAddCityListRefresh();
                        adapter.notifyDataSetChanged();
                        //recyclerView.add
                    }
                    break;
                case "com.coolweather.android.LOCAL_BROADCAST_LOCATION_STATE":
                    locationSwitch=intent.getBooleanExtra("is_switch",false);
                    String nowLocWeatherId=intent.getStringExtra("weather_id");
                    nowLocCityName=intent.getStringExtra("city_name");
                    mWeatherId=nowLocWeatherId;
                    adapter.setLocationSwitch(locationSwitch,true,nowLocCityName);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }
}