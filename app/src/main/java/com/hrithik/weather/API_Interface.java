package com.hrithik.weather;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface API_Interface {

    @GET("weather?appid=2e38d6e40898f9c7677c9f0c3da44c3a&units=metric")
    Call<Main> getWeatherData1(@QueryMap Map<String, String> map);

    @GET("weather?appid=2e38d6e40898f9c7677c9f0c3da44c3a&units=metric")
    Call<Wind> getWeatherData2(@QueryMap Map<String, String> map);

    @GET("weather?appid=2e38d6e40898f9c7677c9f0c3da44c3a&units=metric")
    Call<Weather> getWeatherData3(@QueryMap Map<String, String> map);

    @GET("weather?appid=2e38d6e40898f9c7677c9f0c3da44c3a&units=metric")
    Call<Name> getWeatherData4(@QueryMap Map<String, String> map);
}
