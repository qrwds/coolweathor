package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by qr on 2018/5/1.
 */

public class WeatherBasic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    @SerializedName("update")
    public WeatherUpdate weatherUpdate;
   public class WeatherUpdate{
        @SerializedName("loc")
        public String updateTime;
    }
}
