package com.coolweather.android.util;

import android.content.Context;
import android.util.Log;

import com.coolweather.android.db.AllCityInfo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by qr on 2018/5/7.
 */

public class DataBaseInit {
    private static BufferedReader bufr;
    public static void initDataBase(Context context){
        AllCityInfo allCityInfo=null;
        try {
            bufr=new BufferedReader(new InputStreamReader(context.getAssets().open("all_city_info1.txt")));
            String line=null;
            while ((line=bufr.readLine())!=null) {
                allCityInfo=new AllCityInfo();
                String[] datas = line.split(" +|\t");
                for (int i = 0; i < datas.length; i++) {
                    if (i == 0) {
                        allCityInfo.setCityCoding(datas[i]);
                    } else if (i == 1) {
                        allCityInfo.setCityEnglishName(datas[i]);
                    } else if (i == 2) {
                        allCityInfo.setCityChineseName(datas[i]);
                    } else if (i == 3) {

                        allCityInfo.setCountryCode(datas[i]);
                    } else if (i == 4) {
                        allCityInfo.setCountryEnglish(datas[i]);
                    } else if (i == 5) {
                        allCityInfo.setCountryChineae(datas[i]);
                    } else if (i == 6) {
                        allCityInfo.setProvinceEnglish(datas[i]);
                    } else if (i == 7) {
                        allCityInfo.setProvinceChinese(datas[i]);
                    } else if (i == 8) {
                        allCityInfo.setBelongToSuperiorCityEnglish(datas[i]);
                    } else if (i == 9) {
                        allCityInfo.setBelongToSuperiorCityChinese(datas[i]);
                    } else if (i == 10) {
                        allCityInfo.setLatitude(datas[i]);
                    } else if (i == 11) {
                        allCityInfo.setLongitude(datas[i]);
                    } else if (i == 12) {
                        allCityInfo.setAdCode(datas[i]);
                    }
                }
                allCityInfo.save();
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (bufr!=null){
                try {
                    bufr.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
