package com.example.fuelqueue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationHolder> {

    private Context context;
    private List<Station> stationList;

    public StationAdapter(Context context, List<Station> stations){
        this.context = context;
        stationList = stations;
    }

    @NonNull
    @Override
    public StationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new StationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StationHolder holder, int position) {

        Station station = stationList.get(position);
        holder.station_name.setText(station.getName());
        holder.location.setText(station.getLocation());

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SingleStation.class);

                Bundle bundle = new Bundle();
                bundle.putString("name", station.getName());
                bundle.putString("location", station.getLocation());
                bundle.putString("arrival_time", station.getArrivalTime());
                bundle.putString("finish_time", station.getFinishTime());

                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stationList.size();
    }

    public class StationHolder extends RecyclerView.ViewHolder{

        TextView station_name, location;
        ConstraintLayout constraintLayout;

        public StationHolder(@NonNull View itemView) {
            super(itemView);

            station_name = itemView.findViewById(R.id.station_name_single);
            location = itemView.findViewById(R.id.location);
            constraintLayout = itemView.findViewById(R.id.main_layout);
        }
    }
}
