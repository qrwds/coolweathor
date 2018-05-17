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
import android.widget.TextView;

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

public class AddCityAdapter extends RecyclerView.Adapter<AddCityAdapter.ViewHolder> {
    private List<AddCity> mAddCityList;
    private Typeface mIconfont;
    private WeatherActivity mActivity;
    private List<Boolean> isClicks;
    private Map<String,Integer> cityNameIndexMap;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View cityItemView;
        TextView icon;
        TextView cityName;
        TextView cityTemp;
        Button delete;

        public ViewHolder(View view){
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
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.add_locatin,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,final int position){
        AddCity addCity=mAddCityList.get(position);
        WeatherIconJud.getWeathetIcon(mIconfont,holder.icon,addCity.getWeatherInfo());
        holder.cityName.setText(addCity.getCityName());
        holder.cityTemp.setText(addCity.getWeatherTmp()+"°");
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(mActivity);
        String weatherCondString=prefs.getString("weatherCond",null);
        int num=0;
        if((num=mAddCityList.size()-isClicks.size())>0){
            for (int i=0;i<num;i++){
                String cityName=mAddCityList.get(mAddCityList.size()-num+i).getCityName();
                cityNameIndexMap.put(cityName,isClicks.size());
                isClicks.add(false);
            }
        }
        if (weatherCondString!=null){
            String cityName=Utility.handlerWeatherNowResponse(weatherCondString).basic.cityName;
            int i=cityNameIndexMap.get(cityName);
            for(int x = 0; x <isClicks.size();x++){
                isClicks.set(x,false);
            }
            isClicks.set(i,true);
        }
        if(isClicks.get(position)){
            holder.cityItemView.setBackgroundColor(Color.parseColor("#54808080"));
        }else{
            holder.cityItemView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        holder.cityItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i <isClicks.size();i++){
                    isClicks.set(i,false);
                }
                isClicks.set(position,true);
                notifyDataSetChanged();
                LocalBroadcastManager localBroadcastManager=LocalBroadcastManager.getInstance(mActivity);
                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(mActivity).edit();
                AddCity addCity=mAddCityList.get(position);
                String weatherString=addCity.getResponseText();
                String weatherCondString=addCity.getResponseCondText();
                String weatherId=addCity.getWeatherId();
                editor.putString("weather_id",weatherId);
                editor.putString("weatherCond",weatherCondString);
                editor.putString("weather",weatherString);
                editor.apply();
                Intent intent = new Intent("com.coolweather.android.LOCAL_BROADCAST");
                localBroadcastManager.sendBroadcast(intent);
                mActivity.drawerLayout.closeDrawers();
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalBroadcastManager localBroadcastManager=LocalBroadcastManager.getInstance(mActivity);
                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(mActivity).edit();
                SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(mActivity);
                AddCity addCity=mAddCityList.get(position);
                String cityName=addCity.getCityName();
                String weatherId=addCity.getWeatherId();
                String weatherNowId=preferences.getString("weather_id",null);
                if(mAddCityList.size()==1){
                    DataSupport.deleteAll(AddCity.class,"cityname=?",cityName);
                    editor.clear();
                    editor.apply();
                    Intent intent=new Intent(mActivity,CityChoose.class);
                    mActivity.startActivity(intent);
                    mActivity.finish();
                }else {
                    if (weatherId.equals(weatherNowId)){
                        String weatherString=null;
                        String weatherCondString=null;
                        if (position==0){
                            weatherId=mAddCityList.get(position+1).getWeatherId();
                            weatherString=mAddCityList.get(position+1).getResponseText();
                            weatherCondString=mAddCityList.get(position+1).getResponseCondText();
                            // holder.cityItemView.setBackgroundColor(Color.parseColor("#ffffff"));
                        }else {
                            weatherId=mAddCityList.get(position-1).getWeatherId();
                            weatherString=mAddCityList.get(position-1).getResponseText();
                            weatherCondString=mAddCityList.get(position-1).getResponseCondText();
                        }
                        editor.putString("weather_id", weatherId);
                        editor.putString("weatherCond",weatherCondString);
                        editor.putString("weather",weatherString);
                        editor.apply();
                        Intent intent = new Intent("com.coolweather.android.LOCAL_BROADCAST");
                        localBroadcastManager.sendBroadcast(intent);
                    }
                    DataSupport.deleteAll(AddCity.class,"cityname=?",cityName);
                    mAddCityList.remove(position);
                    isClicks.remove(position);
                    for (int i=0;i<mAddCityList.size();i++){
                        String cityName1=mAddCityList.get(i).getCityName();
                        cityNameIndexMap.put(cityName1,i);
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }
    @Override
    public int getItemCount(){
        return mAddCityList.size();
    }
}
