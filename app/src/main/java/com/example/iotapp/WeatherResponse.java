package com.example.iotapp;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("main")
    private Main main;

    // Thêm các trường khác cần thiết

    public Main getMain() {
        return main;
    }

    // Thêm getter cho các trường khác
}

