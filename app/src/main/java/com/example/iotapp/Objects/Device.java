package com.example.iotapp.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Device implements Serializable, Parcelable
{
    private String id;
    private String name;
    private String irCodes;
    private String deviceCateID;
    private String photo;


    // Các phương thức getter và setter
    public Device(String name, String id, String deviceCateID, String irCodes, String photo) {
        this.name = name;
        this.id = id;
        this.deviceCateID = deviceCateID;
        this.irCodes = irCodes;
        this.photo = photo;
    }

    // Phương thức để triển khai Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(irCodes);
        dest.writeString(deviceCateID);
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    protected Device(Parcel in) {
        id = in.readString();
        name = in.readString();
        irCodes = in.readString();
        deviceCateID = in.readString();
    }
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getIrCodes() {
        return irCodes;
    }

    public String getDeviceCateID() {
        return deviceCateID;
    }
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
