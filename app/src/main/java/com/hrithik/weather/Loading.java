package com.hrithik.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Loading extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    private final String prefs_key = "Preferences";
    private final String latitude_key = "Latitude";
    private final String longitude_key = "Longitude";
    private LocationManager locationManager;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        prefs = getSharedPreferences(prefs_key, MODE_PRIVATE);

        getLocationAccess();
    }

    public void getLocationAccess() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    private void onGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(getApplicationContext(), "Please set location manually", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Loading.this, AddLocation.class));
                finishAffinity();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @SuppressLint("ApplySharedPref")
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (locationGPS != null) {
                prefs.edit().putString(latitude_key, String.valueOf(locationGPS.getLatitude())).commit();
                prefs.edit().putString(longitude_key, String.valueOf(locationGPS.getLongitude())).commit();
            } else if (locationNetwork != null) {
                prefs.edit().putString(latitude_key, String.valueOf(locationNetwork.getLatitude())).commit();
                prefs.edit().putString(longitude_key, String.valueOf(locationNetwork.getLongitude())).commit();
            } else if (locationPassive != null) {
                prefs.edit().putString(latitude_key, String.valueOf(locationPassive.getLatitude())).commit();
                prefs.edit().putString(longitude_key, String.valueOf(locationPassive.getLongitude())).commit();
            } else {
                Toast.makeText(getApplicationContext(), "Unable to find location. Please set location manually", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AddLocation.class));
                finishAffinity();
                return;
            }

            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull final int[] grantResults) {

        if (requestCode == REQUEST_LOCATION) {
            for (String ignored : permissions) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        onGPS();
                    } else
                        getLocation();
                else {
                    Toast.makeText(getApplicationContext(), "Permission Denied. Please set location manually", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Loading.this, AddLocation.class));
                    finishAffinity();
                }
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            getLocation();
        else {
            Toast.makeText(getApplicationContext(), "Cannot access location. Please set location manually", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Loading.this, AddLocation.class));
            finishAffinity();
        }
    }
}