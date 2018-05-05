package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by qr on 2018/5/1.
 */

public class Basic {
    @SerializedName("cid")
    public String weatherId;
    @SerializedName("location")
    public String cityName;
    @SerializedName("admin_area")
    public String adminArea;
    @SerializedName("cnty")
    public String countyName;
    @SerializedName("lon")
    public String longitude;
    @SerializedName("lat")
    public String latitude;
    @SerializedName("tz")
    public String timeZone;
    @SerializedName("parent_city")
    public String parentCity;
}
