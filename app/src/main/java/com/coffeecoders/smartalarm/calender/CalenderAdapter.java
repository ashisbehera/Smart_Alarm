package com.coffeecoders.smartalarm.calender;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecoders.smartalarm.AlarmAdapter;
import com.coffeecoders.smartalarm.R;

import java.util.ArrayList;

public class CalenderAdapter extends RecyclerView.Adapter<CalenderAdapter.ViewHolder> {
    private ArrayList<Events> cal_data_list = new ArrayList<>();
    private Context context;
    public CalenderAdapter(Context context , ArrayList<Events> data){
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
        Events newEvent = cal_data_list.get(position);
        holder.eventNameView.setText(newEvent.getEvent_name());
        holder.eventSTimeView.setText(newEvent.getEvent_s_time());
        holder.eventETimeView.setText(newEvent.getEvent_e_time());
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
