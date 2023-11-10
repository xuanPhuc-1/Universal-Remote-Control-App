package com.example.iotapp.Objects;
import com.example.iotapp.Objects.DeviceCategory;
public class Device {
    private String name, id;
    //declare a Image variable to store the image


    public Device(String name, String id) {
        this.name = name;
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String  getId() {
        return id;
    }
}
