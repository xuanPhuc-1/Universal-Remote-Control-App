package com.example.iotapp.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iotapp.Constant;
import com.example.iotapp.Objects.Device;
import com.example.iotapp.R;
import com.example.iotapp.Listeners.SelectDeviceListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    private List<Device> DeviceList;
    private SelectDeviceListener listener;

    public DeviceAdapter(List<Device> DeviceList, SelectDeviceListener listener) {
        this.DeviceList = DeviceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { // set text cho từng item
        Device Device = DeviceList.get(position);
        if (Device.getPhoto() != null) {
            Picasso.get().load(Constant.URL + "/storage/devices/" + Device.getPhoto()).into(holder.deviceImage);
        } else {
            Toast.makeText(holder.itemView.getContext(), "Photo is null", Toast.LENGTH_SHORT).show();
        }
        holder.deviceName.setText(Device.getName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(DeviceList.get(position));
            } // khi click vào item thì sẽ chuyển sang màn hình remote
        });
    }

    @Override
    public int getItemCount() {
        return DeviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        public CardView cardView;
        public ImageView deviceImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceName);
            cardView = itemView.findViewById(R.id.main_device_container);
            deviceImage = itemView.findViewById(R.id.imgDevice);
        }
    }
}
