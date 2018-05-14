package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by qr on 2018/5/10.
 */

public class StorageCity extends DataSupport{
    private int id;
    private String cityNameInfo;
    private String countryChineseName;
    private String provinceName;
    private String weatherId;
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getCityNameInfo(){
        return cityNameInfo;
    }
    public void setCityNameInfo(String cityNameInfo){
        this.cityNameInfo=cityNameInfo;
    }
    public String getCountryChineseName(){
        return countryChineseName;
    }
    public void setCountryChineseName(String countryChineseName){
        this.countryChineseName=countryChineseName;
    }
    public String getProvinceName(){
        return provinceName;
    }
    public void setProvinceName(String provinceName){
        this.provinceName=provinceName;
    }
    public String getWeatherId(){
        return weatherId;
    }
    public void setWeatherId(String weatherId){
        this.weatherId=weatherId;
    }
}
