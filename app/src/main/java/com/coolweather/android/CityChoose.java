package com.coolweather.android;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.coolweather.android.db.AllCityInfo;
import com.coolweather.android.db.StorageCity;
import com.coolweather.android.util.DataBaseInit;
import com.coolweather.android.util.MyEditTextChangeListener;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;

public class CityChoose extends AppCompatActivity {

    private List<String> dataList=new ArrayList<String>();
    private List<AllCityInfo> allCityInfoList=new ArrayList<AllCityInfo>();
    private List<StorageCity> storageCityList=new ArrayList<StorageCity>();
    private LocalBroadcastManager localBroadcastManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_city);
        allCityInfoList=DataSupport.where("citycoding=?","CN101340406").find(AllCityInfo.class);
        storageCityList=DataSupport.findAll(StorageCity.class);
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        Button back=(Button)findViewById(R.id.choose_city_back);
        Button qingChu=(Button)findViewById(R.id.qingchu_button);
        final EditText cityEditText=(EditText)findViewById(R.id.choose_input_edit);
        final ListView listView=(ListView)findViewById(R.id.info_list);
        final ArrayAdapter adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        if (allCityInfoList.size()==0){
            final ProgressDialog progressDialog=new ProgressDialog(CityChoose.this);
            progressDialog.setTitle("正初始化数据库");
            progressDialog.setMessage("请稍等...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DataBaseInit.initDataBase(CityChoose.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                }
            }).start();
        }
        allCityInfoList.clear();
        if(storageCityList.size()>0){
            for (StorageCity storageCity:storageCityList){
                dataList.add(storageCity.getCityNameInfo()+"/"+storageCity.getCountryChineseName()+"("+storageCity.getProvinceName()+")");
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String weatherId=null;
                if (allCityInfoList.size()>0){
                    weatherId=allCityInfoList.get(i).getCityCoding();
                    if (DataSupport.where("weatherid=?",weatherId).find(StorageCity.class).size()==0) {
                        StorageCity storageCity=new StorageCity();
                        storageCity.setWeatherId(weatherId);
                        storageCity.setCityNameInfo(allCityInfoList.get(i).getCityChineseName());
                        storageCity.setCountryChineseName(allCityInfoList.get(i).getCountryChineae());
                        storageCity.setProvinceName(allCityInfoList.get(i).getProvinceChinese());
                        storageCity.save();
                    }
                    SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(CityChoose.this);
                   SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(CityChoose.this).edit();
                    if (prefs.getString("weather",null)!=null&&prefs.getString("weatherCond",null)!=null) {
                        editor.putString("weather_id", weatherId);
                        editor.apply();
                        Intent intent = new Intent("com.coolweather.android.LOCAL_BROADCAST");
                        localBroadcastManager.sendBroadcast(intent);
                        finish();
                    }else {
                        Intent intent=new Intent(CityChoose.this,WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        finish();
                    }
                }else {
                    weatherId=storageCityList.get(i).getWeatherId();
                    SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(CityChoose.this);
                    SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(CityChoose.this).edit();
                    if (prefs.getString("weather",null)!=null&&prefs.getString("weatherCond",null)!=null) {
                        editor.putString("weather_id", weatherId);
                        editor.apply();
                        Intent intent = new Intent("com.coolweather.android.LOCAL_BROADCAST");
                        localBroadcastManager.sendBroadcast(intent);
                        finish();
                    }else {
                        Intent intent=new Intent(CityChoose.this,WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        qingChu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityEditText.setText("");
            }
        });
        cityEditText.addTextChangedListener(new MyEditTextChangeListener(){
            @Override
            public void onTextChanged(CharSequence charSequence,int i,int i1,int i2){
                String inputInfo=charSequence.toString();
                if (!inputInfo.equals("")){
                    allCityInfoList.clear();
                    allCityInfoList= DataSupport.where("citychinesename REGEXP ?",inputInfo+".*").find(AllCityInfo.class);
                    if (allCityInfoList.size()>0){
                        dataList.clear();
                        for (AllCityInfo allCityInfo:allCityInfoList){
                            dataList.add(allCityInfo.getCityChineseName()+"/"+allCityInfo.getCountryChineae()+"("+allCityInfo.getProvinceChinese()+")");
                          // Log.d("CityChoose",allCityInfo.getCityChineseName());
                        }
                        adapter.notifyDataSetChanged();
                        listView.setSelection(0);
                }
                }else {
                    dataList.clear();
                    allCityInfoList.clear();
                    if(storageCityList.size()>0){
                        for (StorageCity storageCity:storageCityList){
                            dataList.add(storageCity.getCityNameInfo()+"/"+storageCity.getCountryChineseName()+"("+storageCity.getProvinceName()+")");
                        }
                        adapter.notifyDataSetChanged();
                        listView.setSelection(0);
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence,int i,int i1,int i2){
            }
        });
    }
}
