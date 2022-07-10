package com.coffeecoders.smartalarm.calender;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecoders.smartalarm.AddAlarm_Activity;
import com.coffeecoders.smartalarm.AlarmAdapter;
import com.coffeecoders.smartalarm.AlarmConstraints;
import com.coffeecoders.smartalarm.R;
import com.coffeecoders.smartalarm.data.AlarmContract;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CalenderAdapter extends RecyclerView.Adapter<CalenderAdapter.ViewHolder> {
    private List<AlarmConstraints> cal_data_list;
    private Context context;
    public CalenderAdapter(Context context , List<AlarmConstraints> data){
        this.context = context;
        cal_data_list = data;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.calender_card_view ,parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlarmConstraints newEvent = cal_data_list.get(position);
        holder.eventNameView.setText(newEvent.getLabel());
        holder.eventSTimeView.setText(newEvent.getEvent_start_full_time());
        holder.eventETimeView.setText(newEvent.getEvent_end_full_time());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext() , AddAlarm_Activity.class);
                intent.setAction("from calenderActivity");
                Uri editUri = ContentUris.withAppendedId(AlarmContract.AlarmEntry.CAL_EVENTS_CONTENT_URI, newEvent.getPKeyDB());
                intent.setData(editUri);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cal_data_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView eventNameView;
        TextView eventSTimeView;
        TextView eventETimeView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameView = itemView.findViewById(R.id.event_name);
            eventSTimeView = itemView.findViewById(R.id.event_start_time);
            eventETimeView = itemView.findViewById(R.id.event_end_time);
        }
    }
}
