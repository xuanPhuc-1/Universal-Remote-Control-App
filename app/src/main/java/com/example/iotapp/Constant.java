package com.example.iotapp;

public class Constant {
    public static final String URL = "http://iotdomain.giize.com"; //url of laravel
    //public static final String URL = "http://192.168.82.116:8000";

    public static final String MQTTURL = "tcp://iotdomain.giize.com";
    public static final String HOME = URL + "/api";
    public static final String LOGIN =  HOME + "/login";
    public static final String LOGOUT =  HOME + "/logout";
    public static final String RESGISTER = HOME + "/signup";
    public static final String SAVE_USER_INFO = HOME + "/save_user_info";

    public static final String ADD_HUB = HOME + "/hubs/pick";

    public static final String PICK_LOCATION = HOME + "/locations";

    public static final String CREATE_CATEGORY = HOME + "/device_categories/add";

    public static final String ADD_DEVICE = HOME + "/devices/add";

    public static final String[] DEVICE_CATEGORY_SUPPORT = {"Tivi", "Air Conditioner", "Projector"};
}
