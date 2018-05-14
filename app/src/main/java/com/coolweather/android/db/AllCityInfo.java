package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by qr on 2018/5/7.
 */

public class AllCityInfo extends DataSupport {
    private int id;
    private String cityCoding;
    private String cityEnglishName;
    private String cityChineseName;
    private String countryCode;
    private String countryEnglish;
    private String countryChineae;
    private String provinceEnglish;
    private String provinceChinese;
    private String belongToSuperiorCityEnglish;
    private String belongToSuperiorCityChinese;
    private String latitude;
    private String longitude;
    private String adCode;

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getCityCoding(){
        return cityCoding;
    }
    public void setCityCoding(String cityCoding){
        this.cityCoding=cityCoding;
    }
    public String getCityEnglishName(){
        return cityEnglishName;
    }
    public void setCityEnglishName(String cityEnglishName){
        this.cityEnglishName=cityEnglishName;
    }
    public String getCityChineseName(){
        return cityChineseName;
    }
    public void setCityChineseName(String cityChineseName){
        this.cityChineseName=cityChineseName;
    }
    public String getCountryCode(){
        return countryCode;
    }
    public void setCountryCode(String countryCode){
        this.countryCode=countryCode;
    }
    public String getCountryEnglish(){
        return countryEnglish;
    }
    public void setCountryEnglish(String countryEnglish){
        this.countryEnglish=countryEnglish;
    }
    public String getCountryChineae(){
        return countryChineae;
    }
    public void setCountryChineae(String countryChineae){
        this.countryChineae=countryChineae;
    }
    public String getProvinceEnglish(){
        return provinceEnglish;
    }
    public void setProvinceEnglish(String provinceEnglish){
        this.provinceEnglish=provinceEnglish;
    }
    public String getProvinceChinese(){
        return provinceChinese;
    }
    public void setProvinceChinese(String provinceChinese){
        this.provinceChinese=provinceChinese;
    }
    public String getBelongToSuperiorCityEnglish(){
        return belongToSuperiorCityEnglish;
    }
    public void setBelongToSuperiorCityEnglish(String belongToSuperiorCityEnglish){
        this.belongToSuperiorCityEnglish=belongToSuperiorCityEnglish;
    }
    public String getBelongToSuperiorCityChinese(){
        return belongToSuperiorCityChinese;
    }
    public void setBelongToSuperiorCityChinese(String belongToSuperiorCityChinese){
        this.belongToSuperiorCityChinese=belongToSuperiorCityChinese;
    }
    public String getLatitude(){
        return latitude;
    }
    public void setLatitude(String latitude){
        this.latitude=latitude;
    }
    public String getLongitude(){
        return longitude;
    }
    public void setLongitude(String longitude){
        this.longitude=longitude;
    }
    public String getAdCode(){
        return adCode;
    }
    public void setAdCode(String adCode){
        this.adCode=adCode;
    }
}
