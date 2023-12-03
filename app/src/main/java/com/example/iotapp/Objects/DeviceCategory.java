package com.example.iotapp.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class DeviceCategory implements Serializable, Parcelable
{
    private String id;
    private String name;
    private String photo;


    // Các phương thức getter và setter
    public DeviceCategory(String name, String id, String photo) {
        this.name = name;
        this.id = id;
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
    }

    public static final Creator<DeviceCategory> CREATOR = new Creator<DeviceCategory>() {
        @Override
        public DeviceCategory createFromParcel(Parcel in) {
            return new DeviceCategory(in);
        }

        @Override
        public DeviceCategory[] newArray(int size) {
            return new DeviceCategory[size];
        }
    };

    protected DeviceCategory(Parcel in) {
        id = in.readString();
        name = in.readString();
    }
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
