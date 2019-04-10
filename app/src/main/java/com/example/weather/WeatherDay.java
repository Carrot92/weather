package com.example.weather;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

public class WeatherDay {
    public class WeatherTemp {
        Double temp;
        Double pressure;
        Double humidity;
    }

    public class WeatherDescription {
        String icon;
        String main;
    }

    @SerializedName("main")
    private WeatherTemp temp;

    @SerializedName("weather")
    private List<WeatherDescription> desctiption;

    @SerializedName("name")
    private String city;

    @SerializedName("dt")
    private long timestamp;

    public WeatherDay(WeatherTemp temp, List<WeatherDescription> desctiption) {
        this.temp = temp;
        this.desctiption = desctiption;
    }

    Date getDate() {
        return new Date(timestamp * 1000);
    }
    DateTime getDateTime(){
        return new DateTime(timestamp*1000);
    }

    String getPressure(){return String.valueOf(temp.pressure);}

    String getHumidity(){return String.valueOf(temp.humidity);}

    String getTempWithDegree() { return String.valueOf(temp.temp.intValue()-273) + "\u00B0"; }

    String getCity() { return city; }

    String getDesc(){return String.valueOf(desctiption.get(0).main);}

    String getIconUrl() {
        return "http://openweathermap.org/img/w/" + desctiption.get(0).icon + ".png";
    }
}
