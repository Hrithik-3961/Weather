package com.hrithik.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AddLocation extends AppCompatActivity {

    private final String prefs_key = "Preferences";
    private final String cities_key = "Cities";
    private final String current_city_key = "City";
    private EditText searchBar;
    private Button search;
    private SharedPreferences prefs;
    private ArrayList<String> cities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_location);

        prefs = getSharedPreferences(prefs_key, MODE_PRIVATE);
        final Gson gson = new Gson();
        String json = prefs.getString(cities_key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        cities = gson.fromJson(json, type);
        if (cities == null) {
            cities = new ArrayList<>();
        }
        addCityCard();

        searchBar = findViewById(R.id.searchBar);
        search = findViewById(R.id.search);

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE))
                    search.performClick();
                return false;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = searchBar.getText().toString().trim();
                if (!text.equals("")) {
                    text = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
                    if (!cities.contains(text))
                        cities.add(text);

                    String json = gson.toJson(cities);
                    prefs.edit().putString(cities_key, json).apply();
                    prefs.edit().putString(current_city_key, text).apply();

                    startActivity(new Intent(AddLocation.this, MainActivity.class));
                }
            }
        });
    }

    private void addCityCard() {

        LinearLayout linearLayout = findViewById(R.id.linear);
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                getResources().getDisplayMetrics());
        for (int i = 0; i < cities.size(); i++) {
            final TextView location = new TextView(this);
            location.setTextColor(Color.BLACK);
            location.setTextSize(2, 35);
            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prefs.edit().putString(current_city_key, location.getText().toString()).apply();
                    startActivity(new Intent(AddLocation.this, MainActivity.class));
                    finishAffinity();
                }
            });

            if (cities.get(i).equals(prefs.getString(current_city_key, ""))) {
                String city = cities.get(i) + "  ";
                SpannableStringBuilder ssb = new SpannableStringBuilder(city);
                ssb.setSpan(new ImageSpan(this, R.drawable.current_location), city.length() - 1, city.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                location.setText(ssb, TextView.BufferType.SPANNABLE);
            } else
                location.setText(cities.get(i));

            View view = new View(this);
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp * 4));
            linearLayout.addView(location);
            linearLayout.addView(view);
        }
    }

    public void back(View v) {
        finish();
    }

}