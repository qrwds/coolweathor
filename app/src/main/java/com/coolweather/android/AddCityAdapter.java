package com.coolweather.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.coolweather.android.db.AddCity;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.WeatherIconJud;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qr on 2018/5/10.
 */

public class AddCityAdapter extends RecyclerView.Adapter<AddCityAdapter.ViewHolder> {
    private List<AddCity> mAddCityList;
    private Typeface mIconfont;
    private WeatherActivity mActivity;
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
    }
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.add_locatin,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.cityItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position=holder.getAdapterPosition();
                LocalBroadcastManager localBroadcastManager=LocalBroadcastManager.getInstance(parent.getContext());
                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(parent.getContext()).edit();
                AddCity addCity=mAddCityList.get(position);
                String weatherId=addCity.getWeatherId();
                editor.putString("weather_id", weatherId);
                editor.apply();
                Intent intent = new Intent("com.coolweather.android.LOCAL_BROADCAST");
                localBroadcastManager.sendBroadcast(intent);
                mActivity.drawerLayout.closeDrawers();
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position=holder.getAdapterPosition();
                LocalBroadcastManager localBroadcastManager=LocalBroadcastManager.getInstance(parent.getContext());
                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(parent.getContext()).edit();
                SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(parent.getContext());
                AddCity addCity=mAddCityList.get(position);
                String cityName=addCity.getCityName();
                String weatherId=addCity.getWeatherId();
                String weatherNowId=preferences.getString("weather_id",null);
                if(mAddCityList.size()==1){
                    DataSupport.deleteAll(AddCity.class,"cityname=?",cityName);
                    editor.clear();
                    editor.apply();
                    Intent intent=new Intent(parent.getContext(),CityChoose.class);
                    parent.getContext().startActivity(intent);
                    mActivity.finish();
                }else {
                    if (weatherId.equals(weatherNowId)){
                        if (position==0){
                            weatherId=mAddCityList.get(position+1).getWeatherId();
                        }else {
                            weatherId=mAddCityList.get(position-1).getWeatherId();
                        }
                        editor.putString("weather_id", weatherId);
                        editor.apply();
                        Intent intent = new Intent("com.coolweather.android.LOCAL_BROADCAST");
                        localBroadcastManager.sendBroadcast(intent);
                    }
                    DataSupport.deleteAll(AddCity.class,"cityname=?",cityName);
                    mAddCityList.remove(position);
                    notifyDataSetChanged();
                }
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        AddCity addCity=mAddCityList.get(position);
        WeatherIconJud.getWeathetIcon(mIconfont,holder.icon,addCity.getWeatherInfo());
        holder.cityName.setText(addCity.getCityName());
        holder.cityTemp.setText(addCity.getWeatherTmp()+"°");
    }
    @Override
    public int getItemCount(){
        return mAddCityList.size();
    }
}
