package com.hrithik.weather;

public class ExtractData {


    private double temp;
    private double feels_like;
    private double temp_min;
    private double temp_max;
    private String pressure;
    private String humidity;

    private int id;
    private String description;

    private String icon;

    private String speed;

    public double getTemp() {
        return temp;
    }

    public double getFeels_like() {
        return feels_like;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public String getPressure() {
        return pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getSpeed() {
        return speed;
    }

}
