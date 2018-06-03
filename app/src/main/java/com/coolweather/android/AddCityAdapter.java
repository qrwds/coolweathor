package com.coolweather.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.AddCity;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.gson.WeatherCond;
import com.coolweather.android.util.Utility;
import com.coolweather.android.util.WeatherIconJud;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qr on 2018/5/10.
 */

public class AddCityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<AddCity> mAddCityList;
    private Typeface mIconfont;
    private WeatherActivity mActivity;
    private List<Boolean> isClicks;
    public Map<String,Integer> cityNameIndexMap;
    private boolean isLocationSwitch=false;
    private boolean isFirst=true;
    public void setLocationSwitch(boolean isLocationSwitch,boolean isShow,String nowLocCityName){
        this.isLocationSwitch=isLocationSwitch;
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(mActivity).edit();
        AddCity addCity=null;
        if (isLocationSwitch) {
            if (cityNameIndexMap.get(nowLocCityName) != null) {
                int num = cityNameIndexMap.get(nowLocCityName);
                if (num != 0) {
                     addCity= mAddCityList.get(num);
                    mAddCityList.remove(num);
                    mAddCityList.add(0, addCity);
                    if (isShow) {
                        String weatherString = addCity.getResponseText();
                        String weatherCondString = addCity.getResponseCondText();
                        editor.putString("weatherCond", weatherCondString);
                        editor.putString("weather", weatherString);
                        editor.apply();
                        WeatherCond weatherCond = Utility.handlerWeatherNowResponse(weatherCondString);
                        Weather weather = Utility.handlerWeatherResponse(weatherString);
                        mActivity.setmWeatherId(weatherCond.basic.weatherId);
                        mActivity.showWeatherCondInfo(weatherCond);
                        mActivity.showWeatherInfo(weather);
                    }
                }
            } else {
               addCity=DataSupport.where("cityname=?", nowLocCityName).find(AddCity.class).get(0);
                    mAddCityList.add(0, addCity);
                String weatherString=addCity.getResponseText();
                String weatherCondString=addCity.getResponseCondText();
                editor.putString("weatherCond",weatherCondString);
                editor.putString("weather",weatherString);
                editor.apply();
                WeatherCond weatherCond=Utility.handlerWeatherNowResponse(weatherCondString);
                Weather weather=Utility.handlerWeatherResponse(weatherString);
                mActivity.setmWeatherId(weatherCond.basic.weatherId);
                mActivity.showWeatherCondInfo(weatherCond);
                mActivity.showWeatherInfo(weather);
            }
        }else {
            //if (mAddCityList.get(0).getCityName()==nowLocCityName) {
                if (mAddCityList.size() > 1) {
                    if (isClicks.get(0) == true) {
                        mAddCityList.get(0).delete();
                        mAddCityList.remove(0);
                        //DataSupport.deleteAll(AddCity.class, "cityname=?", nowLocCityName);
                       /* int i = DataSupport.findAll(AddCity.class).size();
                        Toast.makeText(mActivity, "" + i, Toast.LENGTH_SHORT).show();*/
                        AddCity city = mAddCityList.get(0);
                        String weatherString = city.getResponseText();
                        String weatherCondString = city.getResponseCondText();
                        editor.putString("weatherCond", weatherCondString);
                        editor.putString("weather", weatherString);
                        editor.apply();
                        WeatherCond weatherCond = Utility.handlerWeatherNowResponse(weatherCondString);
                        Weather weather = Utility.handlerWeatherResponse(weatherString);
                        mActivity.setmWeatherId(weatherCond.basic.weatherId);
                        mActivity.showWeatherCondInfo(weatherCond);
                        mActivity.showWeatherInfo(weather);
                    } else {
                        mAddCityList.get(0).delete();
                        mAddCityList.remove(0);
                    }
                } else {
                    mAddCityList.get(0).delete();
                    // DataSupport.deleteAll(AddCity.class,"cityname=?",nowLocCityName);
                    /*int i = DataSupport.findAll(AddCity.class).size();
                    Toast.makeText(mActivity, "" + i, Toast.LENGTH_SHORT).show();*/
                    editor.clear();
                    editor.apply();
                    Intent intent1 = new Intent(CoolweatherAppWidgetReceiver.UPDATE_ACTION);
                    mActivity.sendBroadcast(intent1);
                    Intent intent = new Intent(mActivity, CityChoose.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivity.startActivity(intent);
                   // mActivity.finish();
                }
           // }
        }
        cityNameIndexMap.clear();
        for (int i = 0; i < mAddCityList.size(); i++) {
            String cityName1 = mAddCityList.get(i).getCityName();
            cityNameIndexMap.put(cityName1, i);
        }
        /*if(isLocationSwitch) {
            isClicks.add(0, true);
            isFirst=true;
        }else {
            isClicks.remove(0);
        }*/
    }
    @Override
    public int getItemViewType(int position){
        if (isLocationSwitch){
            if(position==0){
                return 1;
            }else {
                return 0;
            }
        }else {
            return 0;
        }
    }
    static class ViewHolder1 extends RecyclerView.ViewHolder{
        View cityItemView;
        TextView icon;
        TextView cityName;
        TextView cityTemp;
        Button button;

        public ViewHolder1(View view){
            super(view);
            cityItemView=view;
            icon=(TextView)view.findViewById(R.id.mylocation_weather_icon);
            cityName=(TextView)view.findViewById(R.id.mylocation_city_name);
            cityTemp=(TextView)view.findViewById(R.id.mylocation_temp);
            button=(Button) view.findViewById(R.id.mylocation_button);
        }
    }
    static class ViewHolder0 extends RecyclerView.ViewHolder{
        View cityItemView;
        TextView icon;
        TextView cityName;
        TextView cityTemp;
        Button delete;

        public ViewHolder0(View view){
            super(view);
            cityItemView=view;
            icon=(TextView)view.findViewById(R.id.add_weather_icon);
            cityName=(TextView)view.findViewById(R.id.add_city_name);
            cityTemp=(TextView)view.findViewById(R.id.add_temp);
            delete=(Button)view.findViewById(R.id.delete_city);
        }
    }
    public AddCityAdapter(List<AddCity> addCityList, Typeface typeface, Activity activity){
        mAddCityList=addCityList;
        mIconfont=typeface;
        mActivity=(WeatherActivity) activity;
        isClicks=new ArrayList<Boolean>();
        cityNameIndexMap=new HashMap<String, Integer>();
        for (int i=0;i<mAddCityList.size();i++){
            String cityName=mAddCityList.get(i).getCityName();
            isClicks.add(false);
            cityNameIndexMap.put(cityName,i);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.add_locatin,parent,false);
        return  new ViewHolder0(view);
        /*}else if (viewType==1){
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.my_location,parent,false);
            return  new ViewHolder1(view);
        }
        return null;*/
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position){
        /*if (holder instanceof ViewHolder0) {
            final int position;
            if (isLocationSwitch){
              position=ii-1;
            }else {
                position=ii;
            }*/
            ViewHolder0 viewHolder=(ViewHolder0)holder;
            AddCity addCity = mAddCityList.get(position);
        WeatherIconJud.getWeathetIcon(mIconfont, viewHolder.icon, addCity.getWeatherInfo());
           if (isLocationSwitch&&position==0) {
            viewHolder.cityName.setText(addCity.getCityName()+"(我的位置)");
            viewHolder.delete.setBackgroundResource(R.drawable.ic_location);
        }else {
            viewHolder.cityName.setText(addCity.getCityName());
        }
            viewHolder.cityTemp.setText(addCity.getWeatherTmp() + "°");
            SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(mActivity);
            String weatherCondString=prefs.getString("weatherCond",null);
            int num=0;
            int isClicksLength=isClicks.size();
           /* if (isLocationSwitch){
                isClicksLength=isClicks.size()-1;
            }else {
                isClicksLength=
            }*/
            if((num=mAddCityList.size()-isClicksLength)>0){
                for (int i=0;i<num;i++){
                    String cityName=mAddCityList.get(mAddCityList.size()-num+i).getCityName();
                    cityNameIndexMap.put(cityName,isClicks.size());
                    isClicks.add(false);
                }
            }

            if (weatherCondString!=null){
                String cityName=Utility.handlerWeatherNowResponse(weatherCondString).basic.cityName;
                int i=cityNameIndexMap.get(cityName);
               /* if (isLocationSwitch){
                    i=i+1;
                }*/
                for(int x = 0; x <isClicks.size();x++){
                    isClicks.set(x,false);
                }
                isClicks.set(i,true);
            }
                if (isClicks.get(position)) {
                    viewHolder.cityItemView.setBackgroundColor(Color.parseColor("#54808080"));
                } else {
                    viewHolder.cityItemView.setBackgroundColor(Color.parseColor("#ffffff"));
                }
            viewHolder.cityItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int i = 0; i <isClicks.size();i++){
                        isClicks.set(i,false);
                    }
                    isClicks.set(position,true);
                    SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(mActivity).edit();
                    AddCity addCity=mAddCityList.get(position);
                    String weatherString=addCity.getResponseText();
                    String weatherCondString=addCity.getResponseCondText();
                    editor.putString("weatherCond",weatherCondString);
                    editor.putString("weather",weatherString);
                    editor.apply();
                    WeatherCond weatherCond=Utility.handlerWeatherNowResponse(weatherCondString);
                    Weather weather=Utility.handlerWeatherResponse(weatherString);
                    mActivity.setmWeatherId(weatherCond.basic.weatherId);
                    mActivity.showWeatherCondInfo(weatherCond);
                    mActivity.showWeatherInfo(weather);
                    mActivity.drawerLayout.closeDrawers();
                    notifyDataSetChanged();
                }
            });
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isLocationSwitch&&position==0){
                        return;
                    }
                    SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(mActivity);
                    SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(mActivity).edit();
                    AddCity addCity=mAddCityList.get(position);
                    String cityName=addCity.getCityName();
                    if(mAddCityList.size()==1){
                        if (!isLocationSwitch) {
                            DataSupport.deleteAll(AddCity.class, "cityname=?", cityName);
                            editor.clear();
                            editor.apply();
                            Intent intent1 = new Intent(CoolweatherAppWidgetReceiver.UPDATE_ACTION);
                            mActivity.sendBroadcast(intent1);
                            Intent intent = new Intent(mActivity, CityChoose.class);
                            mActivity.startActivity(intent);
                            mActivity.finish();
                        }/*else {
                            DataSupport.deleteAll(AddCity.class,"cityname=?",cityName);
                            mAddCityList.remove(position);
                            isClicks.remove(position);
                            String nowLocationWeathercond=prefs.getString("now_location_weathercond",null);
                           String nowLocationWeather=prefs.getString("now_location_weather",null);
                             WeatherCond weatherCond = Utility.handlerWeatherNowResponse(nowLocationWeathercond);
                            Weather weather=Utility.handlerWeatherResponse(nowLocationWeather);
                            editor.putString("weatherCond",nowLocationWeathercond );
                            editor.putString("weather", nowLocationWeather);
                            editor.apply();
                            mActivity.setmWeatherId(weatherCond.basic.weatherId);
                            mActivity.showWeatherCondInfo(weatherCond);
                            mActivity.showWeatherInfo(weather);
                            notifyDataSetChanged();
                        }*/
                    }else {
                        if (isClicks.get(position)){
                            String weatherString=null;
                            String weatherCondString=null;
                            if (position==0){
                              //  isClicks.set(position+1,true);
                                weatherString=mAddCityList.get(position+1).getResponseText();
                                weatherCondString=mAddCityList.get(position+1).getResponseCondText();
                                // holder.cityItemView.setBackgroundColor(Color.parseColor("#ffffff"));
                            }else {
                               // isClicks.set(position-1,true);
                                weatherString=mAddCityList.get(position-1).getResponseText();
                                weatherCondString=mAddCityList.get(position-1).getResponseCondText();
                            }
                            editor.putString("weatherCond",weatherCondString);
                            editor.putString("weather",weatherString);
                            editor.apply();
                            WeatherCond weatherCond=Utility.handlerWeatherNowResponse(weatherCondString);
                            Weather weather=Utility.handlerWeatherResponse(weatherString);
                            mActivity.setmWeatherId(weatherCond.basic.weatherId);
                            mActivity.showWeatherCondInfo(weatherCond);
                            mActivity.showWeatherInfo(weather);
                        }
                        DataSupport.deleteAll(AddCity.class,"cityname=?",cityName);
                        mAddCityList.remove(position);
                        isClicks.remove(position);
                        cityNameIndexMap.clear();
                        for (int i=0;i<mAddCityList.size();i++){
                            String cityName1=mAddCityList.get(i).getCityName();
                            cityNameIndexMap.put(cityName1,i);
                        }
                        notifyDataSetChanged();
                    }
                }
            });
    /*else if (holder instanceof ViewHolder1){
            final int position=ii;
            ViewHolder1 viewHolder=(ViewHolder1)holder;
            SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(mActivity);
            final String nowLocationWeathercond=prefs.getString("now_location_weathercond",null);
            final String nowLocationWeather=prefs.getString("now_location_weather",null);
            if (nowLocationWeathercond!=null&&nowLocationWeather!=null) {
                final WeatherCond weatherCond = Utility.handlerWeatherNowResponse(nowLocationWeathercond);
                final Weather weather=Utility.handlerWeatherResponse(nowLocationWeather);
                WeatherIconJud.getWeathetIcon(mIconfont, viewHolder.icon, weatherCond.now.weatherInfo);
                viewHolder.cityName.setText(weatherCond.basic.cityName+"(我的位置)");
                viewHolder.cityTemp.setText(weatherCond.now.tmp + "°");
                //isClicks.set(0,true);
                if (isClicks.get(position)) {
                    viewHolder.cityItemView.setBackgroundColor(Color.parseColor("#54808080"));
                } else {
                    viewHolder.cityItemView.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                viewHolder.cityItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < isClicks.size(); i++) {
                            isClicks.set(i, false);
                        }
                        isClicks.set(position, true);
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mActivity).edit();
                        editor.putString("weatherCond",nowLocationWeathercond );
                        editor.putString("weather", nowLocationWeather);
                        editor.apply();
                        mActivity.setmWeatherId(weatherCond.basic.weatherId);
                        mActivity.showWeatherCondInfo(weatherCond);
                        mActivity.showWeatherInfo(weather);
                        mActivity.drawerLayout.closeDrawers();
                        notifyDataSetChanged();
                    }
                });
            }
        }*/
    }
    @Override
    public int getItemCount() {
       /*if(isLocationSwitch){
           return mAddCityList.size()+1;
       }else {

       }*/
        return mAddCityList.size();
    }
}
