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

    public static final String CREATE_CATEGORY = HOME + "/device_categories/create";

    public static final String CREATE_DEVICE = HOME + "/devices/create";

    //public String GET_TEMP_HUMID = "http://iotdomain.giize.com/locations/" + id + "/get_info";
    //public String GET_DEVICES_CATEGORY = "http://iotdomain.giize.com/locations/" + id + "/device_categories";

}
