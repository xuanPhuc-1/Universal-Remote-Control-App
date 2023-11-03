package com.example.iotapp.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iotapp.Objects.DeviceCategory;
import com.example.iotapp.R;
import com.example.iotapp.Listeners.SelectDeviceCateListener;

import java.util.List;

public class DeviceCategoryAdapter extends RecyclerView.Adapter<DeviceCategoryAdapter.ViewHolder> {
    private List<DeviceCategory> deviceCategoryList;
    private SelectDeviceCateListener listener;

    public DeviceCategoryAdapter(List<DeviceCategory> deviceCategoryList, SelectDeviceCateListener listener) {
        this.deviceCategoryList = deviceCategoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_cate_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { // set text cho từng item
        DeviceCategory deviceCategory = deviceCategoryList.get(position);
        holder.deviceCateName.setText(deviceCategory.getName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(deviceCategoryList.get(position));
            } // khi click vào item thì sẽ chuyển sang màn hình remote
        });
    }

    @Override
    public int getItemCount() {
        return deviceCategoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView deviceCateName;
        public CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceCateName = itemView.findViewById(R.id.deviceCateName);
            cardView = itemView.findViewById(R.id.main_device_cate_container);
        }
    }
}
