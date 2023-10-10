package com.example.iotapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iotapp.Location;
import com.example.iotapp.R;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private List<Location> locationList;

    public LocationAdapter(List<Location> locationList) {
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Location location = locationList.get(position);
        holder.locationName.setText(location.getName());
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView locationName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.locationName);
        }
    }
}
