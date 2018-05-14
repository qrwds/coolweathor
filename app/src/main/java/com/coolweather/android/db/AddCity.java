package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by qr on 2018/5/11.
 */

public class AddCity extends DataSupport {
    private int id;
    private String weatherId;
    private String cityName;
    private String weatherInfo;
    private String weatherTmp;
    private String responseCondText;
    private String responseText;
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getCityName(){
        return cityName;
    }
    public void setCityName(String cityName){
        this.cityName=cityName;
    }
    public String getWeatherId(){
        return weatherId;
    }
    public void setWeatherId(String weatherId){
        this.weatherId=weatherId;
    }
    public String getWeatherInfo(){
        return weatherInfo;
    }
    public void setWeatherInfo(String weatherInfo){
        this.weatherInfo=weatherInfo;
    }
    public String getWeatherTmp(){
        return weatherTmp;
    }
    public void setWeatherTmp(String weatherTmp){
        this.weatherTmp=weatherTmp;
    }
    public String getResponseText(){
        return responseText;
    }
    public void setResponseText(String responseText){
        this.responseText=responseText;
    }
    public String getResponseCondText(){
        return responseCondText;
    }
    public void setResponseCondText(String responseCondText){
        this.responseCondText=responseCondText;
    }
}
