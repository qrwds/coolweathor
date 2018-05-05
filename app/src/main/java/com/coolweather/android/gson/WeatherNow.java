package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by qr on 2018/5/1.
 */

public class WeatherNow {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public String info;
    }
}
