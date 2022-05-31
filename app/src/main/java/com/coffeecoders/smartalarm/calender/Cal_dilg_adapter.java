package com.coffeecoders.smartalarm.calender;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecoders.smartalarm.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Cal_dilg_adapter extends RecyclerView.Adapter<Cal_dilg_adapter.ViewHolder>{

    ArrayList<String> acc_list = new ArrayList<>();
    public Cal_dilg_adapter(ArrayList<String> list){
    acc_list = list;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_cal_account ,parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.acc_name.setText(acc_list.get(position));

    }

    @Override
    public int getItemCount() {
        return acc_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView acc_name;
        RadioButton radioButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            acc_name = itemView.findViewById(R.id.acc_name);
            radioButton = itemView.findViewById(R.id.acc_radio_bt);
        }
    }
}
