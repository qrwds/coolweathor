package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by qr on 2018/5/1.
 */

public class Weather {
    public String status;
    @SerializedName("basic")
    public WeatherBasic basic;
    public AQI aqi;
    @SerializedName("now")
    public WeatherNow now;
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
