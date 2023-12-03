package com.example.iotapp.Adapters;

import android.annotation.SuppressLint;
import android.util.Log;
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
import com.example.iotapp.Objects.DeviceCategory;
import com.example.iotapp.R;
import com.example.iotapp.Listeners.SelectDeviceCateListener;
import com.squareup.picasso.Picasso;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeviceCategory deviceCategory = deviceCategoryList.get(position);

        // Kiểm tra xem deviceCategory.getPhoto() có phải là null không
        if (deviceCategory.getPhoto() != null) {
            Picasso.get().load(Constant.URL + "/storage/categories/" + deviceCategory.getPhoto()).into(holder.deviceCateImage);
        } else {
            Toast.makeText(holder.itemView.getContext(), "Photo is null", Toast.LENGTH_SHORT).show();
        }

        holder.deviceCateName.setText(deviceCategory.getName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(deviceCategoryList.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return deviceCategoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView deviceCateName;
        public CardView cardView;
        public ImageView deviceCateImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceCateName = itemView.findViewById(R.id.deviceCateName);
            deviceCateImage = itemView.findViewById(R.id.imgDeviceCate);
            cardView = itemView.findViewById(R.id.main_device_cate_container);
        }
    }
}
