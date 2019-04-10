package com.example.weather;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

class RemoteFetch {


    private static Retrofit retrofit;
    static String city = "Moscow";
    public interface ApiInterface {
        @GET("weather")
        Call<WeatherDay> getToday(
                @Query("q") String q,
                @Query("appid") String appid
        );

        @GET("forecast")
        Call<WeatherForecast> getForecast(
                @Query("q") String q,
                @Query("appid") String appid
        );
    }

    static Retrofit getClient() {
        if (retrofit == null) {
            String BASE_URL = "http://api.openweathermap.org/data/2.5/";
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
