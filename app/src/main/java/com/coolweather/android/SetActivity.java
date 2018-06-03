package com.coolweather.android;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.coolweather.android.db.AddCity;
import com.coolweather.android.db.AllCityInfo;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.gson.WeatherCond;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SetActivity extends AppCompatActivity {
    private Switch aSwitch;
    private boolean isSwitch=false;
    public LocationClient mLocationClient=null;
    private boolean isRequestWeatherCond=false;
    private boolean isRequestWeather=false;
    private boolean isError=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set);
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(this).edit();
        isSwitch=preferences.getBoolean("switch_state",false);
        aSwitch=(Switch)findViewById(R.id.location_switch);
        aSwitch.setChecked(isSwitch);
        Typeface iconfont= Typeface.createFromAsset(getAssets(),"iconfont/iconfont.ttf");
        TextView tmdText=(TextView)findViewById(R.id.toumingdu_fonticon);
        tmdText.setTypeface(iconfont);
        int progress=preferences.getInt("widget_alpha",0);
        SeekBar seekBar=(SeekBar)findViewById(R.id.seek_bar);
        seekBar.setMax(100);
        seekBar.setProgress(progress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(SetActivity.this).edit();
                int widgetAlpha=100-seekBar.getProgress();
                editor.putInt("widget_alpha",seekBar.getProgress());
                editor.apply();
                Intent intent=new Intent("com.coolweather.android.action.CHANGE_ALPHA");
                intent.putExtra("widget_alpha",widgetAlpha);
                sendBroadcast(intent);
            }
        });
        RelativeLayout layout=(RelativeLayout)findViewById(R.id.location_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitch.isChecked()) {
                    aSwitch.setChecked(false);
                } else {
                    aSwitch.setChecked(true);
                }
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(SetActivity.this);
                if (b){
                    requestLocation();
                }else {
                    editor.putBoolean("switch_state",false);
                    editor.apply();
                    if (mLocationClient!=null) {
                        mLocationClient.stop();
                    }
                    if (!isError) {
                        Intent intent = new Intent("com.coolweather.android.LOCAL_BROADCAST_LOCATION_STATE");
                        intent.putExtra("is_switch", false);
                        localBroadcastManager.sendBroadcast(intent);
                    }
                }
            }
        });
    }
    private void requestLocation(){
        mLocationClient=new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        List<String> permissionList=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this,permissions,1);
        } else {
            requestLocations();
        }
    }
    private void requestLocations(){
        initLocation();
        mLocationClient.start();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for(int garentResult:grantResults){
                        if(garentResult!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(SetActivity.this,"拒绝此权限将无法使用位置功能",Toast.LENGTH_SHORT).show();
                            aSwitch.setChecked(false);
                            return;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(SetActivity.this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            String cityname=location.getCity().substring(0,location.getCity().length()-1);
            List<AllCityInfo> list = DataSupport.where("citychinesename=?",cityname).find(AllCityInfo.class);
            //Log.d("city_name",location.getCity());
            String weatherId=list.get(0).getCityCoding();
            requestNowLocationWeatherCond(weatherId);
            requestNowLocationWeather(weatherId);
            while (true){
                if (isError){
                    Toast.makeText(SetActivity.this,"当前位置天气请求失败",Toast.LENGTH_SHORT).show();
                    aSwitch.setChecked(false);
                    break;
                }else {
                    if (isRequestWeatherCond && isRequestWeather) {
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(SetActivity.this).edit();
                        editor.putBoolean("switch_state",true);
                        editor.apply();
                        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(SetActivity.this);
                        Intent intent = new Intent("com.coolweather.android.LOCAL_BROADCAST_LOCATION_STATE");
                        intent.putExtra("is_switch", true);
                        intent.putExtra("city_name", cityname);
                        intent.putExtra("weather_id", weatherId);
                        localBroadcastManager.sendBroadcast(intent);
                        break;
                    }
                }
            }
           /* runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StringBuilder currentPosition=new StringBuilder();
                    currentPosition.append("纬度:").append(location.getLatitude()).append("\n");
                    currentPosition.append("经度:").append(location.getLongitude()).append("\n");
                    currentPosition.append("国家:").append(location.getCountry()).append("\n");
                    currentPosition.append("省:").append(location.getProvince()).append("\n");
                    currentPosition.append("市:").append(location.getCity()).append("\n");
                    currentPosition.append("区:").append(location.getDistrict()).append("\n");
                    currentPosition.append("街道:").append(location.getStreet()).append("\n");
                    currentPosition.append("定位方式:");
                    if (location.getLocType()==BDLocation.TypeGpsLocation){
                        currentPosition.append("GPS");
                    }else {
                        currentPosition.append("网络");
                    }
                    positionText.setText(currentPosition);
                }
            });*/
        }
    }
    private void initLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setScanSpan(1000*60*60);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();

    }
   private void requestNowLocationWeatherCond(final String weatherId){
       String weatherUrl="https://free-api.heweather.com/s6/weather/now?location="+weatherId+"&key=f360abdf1f8a43e8bbb77cd68a3e8977";
       HttpUtil.sendOkHttpResquest(weatherUrl, new Callback() {
           @Override
           public void onFailure(Call call, IOException e) {
               e.printStackTrace();
               isError=true;
             /* runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       Toast.makeText(SetActivity.this,"获取实况天气信息失败",Toast.LENGTH_SHORT).show();
                   }
               });*/
           }

           @Override
           public void onResponse(Call call, Response response) throws IOException {
               final String responseText=response.body().string();
               final WeatherCond weatherCond=Utility.handlerWeatherNowResponse(responseText);
                       if(weatherCond!=null&&"ok".equals(weatherCond.status)){
                           SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(SetActivity.this).edit();
                           editor.putString("weatherCond",responseText);
                           editor.putString("loc_city_name",weatherCond.basic.cityName);
                           editor.apply();
                           if (DataSupport.where("cityname=?",weatherCond.basic.cityName).find(AddCity.class).size()==0) {
                               AddCity addCity = new AddCity();
                               addCity.setWeatherId(weatherCond.basic.weatherId);
                               addCity.setCityName(weatherCond.basic.cityName);
                               addCity.setWeatherInfo(weatherCond.now.weatherInfo);
                               addCity.setWeatherTmp(weatherCond.now.tmp);
                               addCity.setResponseCondText(responseText);
                               addCity.save();
                           }
                               isRequestWeatherCond=true;
                       }else {
                           isError=true;
                          // Toast.makeText(SetActivity.this,"获取实况天气信息失败..",Toast.LENGTH_SHORT).show();
                       }
           }
       });
   }
    private void requestNowLocationWeather(final String weatherId){
        String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=f360abdf1f8a43e8bbb77cd68a3e8977";
        HttpUtil.sendOkHttpResquest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                isError=true;
               /* runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SetActivity.this,"获取预报天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });*/
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=Utility.handlerWeatherResponse(responseText);
                        if(weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(SetActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            if (DataSupport.where("cityname=?",weather.basic.cityName).find(AddCity.class).size()==0) {
                                AddCity addCity = new AddCity();
                                addCity.setResponseText(responseText);
                                addCity.save();
                            }else {
                                AddCity addCity = new AddCity();
                                addCity.setResponseText(responseText);
                                addCity.updateAll("cityname=?",weather.basic.cityName);
                            }
                            isRequestWeather=true;
                        }else {
                            isError=true;
                           // Toast.makeText(SetActivity.this,"获取预报天气信息失败",Toast.LENGTH_SHORT).show();
                        }
            }
        });
    }
}
