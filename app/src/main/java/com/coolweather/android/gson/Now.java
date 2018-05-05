package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by qr on 2018/5/1.
 */

public class Now {
    @SerializedName("cond_code")
    public String condCode;
    @SerializedName("cond_txt")
    public String weatherInfo;
    @SerializedName("fl")
    public String bobyTemperature;
    public String hum;
    public String pcpn;
    public String pres;
    public String vis;
    public String cloud;
    public String tmp;
    @SerializedName("wind_deg")
    public String windDeg;
    @SerializedName("wind_dir")
    public String windDir;
    @SerializedName("wind_sc")
    public String windSc;
    @SerializedName("wind_spd")
    public String windSpd;
}
