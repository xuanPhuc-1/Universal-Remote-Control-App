package com.example.iotapp;

import com.google.gson.annotations.SerializedName;

public class Main {
    @SerializedName("temp")
    private double temperature;

    // Thêm các trường khác cần thiết

    public double getTemperature() {
        return temperature;
    }

    // Thêm getter cho các trường khác
}

