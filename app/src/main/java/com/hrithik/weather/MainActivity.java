package com.hrithik.weather;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final String prefs_key = "Preferences";
    private final String cities_key = "Cities";
    private final String current_city_key = "City";
    private final String feels_like_key = "Feels like";
    private final String humidity_key = "Humidity";
    private final String wind_key = "Wind";
    private final String pressure_key = "Pressure";
    private final String latitude_key = "Latitude";
    private final String longitude_key = "Longitude";
    private RelativeLayout background;
    private TextView name;
    private TextView text;
    private TextView temperature;
    private TextView minMax;
    private Button add_city;
    private ImageView weatherImage;
    private API_Interface api_interface;
    private SharedPreferences prefs;
    private ArrayList<String> cities = new ArrayList<>();
    private Map<String, String> map = new HashMap<>();
    private String lat;
    private String lon;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(prefs_key, MODE_PRIVATE);

        city = prefs.getString(current_city_key, "");
        lat = prefs.getString(latitude_key, "");
        lon = prefs.getString(longitude_key, "");
        setMap();

        background = findViewById(R.id.background);
        name = findViewById(R.id.name);
        text = findViewById(R.id.text);
        temperature = findViewById(R.id.temperature);
        minMax = findViewById(R.id.minMax);
        weatherImage = findViewById(R.id.weatherImage);
        add_city = findViewById(R.id.add_city);
        add_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddLocation.class));
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build();
        api_interface = retrofit.create(API_Interface.class);

        getWeather();

    }

    private void setMap() {
        map.put("q", city);
        map.put("lon", lon);
        map.put("lat", lat);
    }

    private void getWeather() {

        Call<Main> call1 = api_interface.getWeatherData1(map);
        call1.enqueue(new Callback<Main>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Main> call, Response<Main> response) {
                if (!response.isSuccessful()) {
                    showError(response.message(), response.code());
                    return;
                }
                ExtractData main = response.body().getMain();

                int temp = (int) main.getTemp();
                temperature.setText(String.valueOf(temp));
                int temp_max = (int) main.getTemp_max();
                int temp_min = (int) main.getTemp_min();
                String unit = getResources().getString(R.string.unit);
                minMax.setText(temp_max + unit + " / " + temp_min + unit);
                int feels_like_temp = (int) main.getFeels_like();
                setBottom(feels_like_key, String.valueOf(feels_like_temp));
                setBottom(humidity_key, main.getHumidity());
                setBottom(pressure_key, main.getPressure());

            }

            @Override
            public void onFailure(Call<Main> call, Throwable t) {
                showError("Some unknown error occurred!", 0);
            }
        });

        Call<Wind> call2 = api_interface.getWeatherData2(map);
        call2.enqueue(new Callback<Wind>() {
            @Override
            public void onResponse(Call<Wind> call, Response<Wind> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                ExtractData wind = response.body().getWind();
                setBottom(wind_key, wind.getSpeed());
            }

            @Override
            public void onFailure(Call<Wind> call, Throwable t) {
                showError("Some unknown error occurred!", 0);
            }
        });

        Call<Weather> call3 = api_interface.getWeatherData3(map);
        call3.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                List<ExtractData> weather = response.body().getWeather();
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd");
                int id = weather.get(0).getId();
                setBackground(id);
                String weather_description = weather.get(0).getDescription();
                String[] words = weather_description.split("\\s");
                String capitalizeWord = "";
                for (String w : words) {
                    String first = w.substring(0, 1);
                    String afterfirst = w.substring(1);
                    capitalizeWord += first.toUpperCase() + afterfirst + " ";
                }
                text.setText(dateFormat.format(date) + "\n\n" + capitalizeWord);

                String icon = weather.get(0).getIcon();
                String iconUrl = "http://openweathermap.org/img/w/" + icon + ".png";

                Glide.with(getApplicationContext()).load(iconUrl).into(weatherImage);

            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                showError("Some unknown error occurred!", 0);
            }
        });

        final Gson gson = new Gson();
        final String[] json = {prefs.getString(cities_key, null)};
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        cities = gson.fromJson(json[0], type);
        if (cities == null) {
            cities = new ArrayList<>();
        }

        Call<Name> call4 = api_interface.getWeatherData4(map);
        call4.enqueue(new Callback<Name>() {
            @Override
            public void onResponse(Call<Name> call, Response<Name> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                city = response.body().getName();

                if (!cities.contains(city))
                    cities.add(city);
                json[0] = gson.toJson(cities);
                prefs.edit().putString(cities_key, json[0]).apply();
                prefs.edit().putString(current_city_key, city).apply();

                name.setText(city);
            }

            @Override
            public void onFailure(Call<Name> call, Throwable t) {
                showError("Some unknown error occurred!", 0);
            }
        });

    }

    private void setBottom(String item, String value) {

        RelativeLayout relativeLayout;
        switch (item) {
            case feels_like_key: {
                relativeLayout = findViewById(R.id.feels_like);
                ImageView imageView = relativeLayout.findViewById(R.id.image);
                imageView.setBackgroundResource(R.drawable.thermometer);
                value += getResources().getString(R.string.unit);

                break;
            }
            case humidity_key: {
                relativeLayout = findViewById(R.id.humidity);
                ImageView imageView = relativeLayout.findViewById(R.id.image);
                imageView.setBackgroundResource(R.drawable.water_drops);
                value += "%";

                break;
            }
            case wind_key: {
                relativeLayout = findViewById(R.id.wind);
                ImageView imageView = relativeLayout.findViewById(R.id.image);
                imageView.setBackgroundResource(R.drawable.wind);
                value += " km/h";

                break;
            }
            default: {
                relativeLayout = findViewById(R.id.pressure);
                ImageView imageView = relativeLayout.findViewById(R.id.image);
                imageView.setBackgroundResource(R.drawable.barometer);
                value += " hPa";
                break;
            }
        }

        TextView text1 = relativeLayout.findViewById(R.id.text1);
        TextView text2 = relativeLayout.findViewById(R.id.text2);
        text1.setText(item);
        text2.setText(value);
    }

    private void showError(String error, final int code) {

        if (code == 404) {
            cities.remove(city);
            Gson gson = new Gson();
            String json = gson.toJson(cities);
            prefs.edit().putString(cities_key, json).apply();
            prefs.edit().putString(current_city_key, "").apply();
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Error " + (code == 0 ? "" : code) + "\n" + error).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        startActivity(new Intent(MainActivity.this, AddLocation.class));
                        finishAffinity();
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setBackground(int id) {
        if (id >= 200 && id < 600) {
            background.setBackground(ContextCompat.getDrawable(this, R.drawable.background2));
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.bg2Dark));
        } else if (id < 700) {
            background.setBackground(ContextCompat.getDrawable(this, R.drawable.background4));
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.bg4Dark));
        } else if (id < 800) {
            background.setBackground(ContextCompat.getDrawable(this, R.drawable.background3));
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.bg3Dark));
        } else {
            background.setBackground(ContextCompat.getDrawable(this, R.drawable.background1));
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.colorPrimaryDark));
        }
    }

}