package com.coffeecoders.smartalarm.calender;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecoders.smartalarm.R;

import java.util.ArrayList;

public class Calender_dialogBox extends AppCompatDialogFragment {
    private  ArrayList<String> cal_acc_list;

    public Calender_dialogBox(ArrayList<String> list){
        cal_acc_list = list;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.cal_acc_dialog_box , null);

        builder.setView(view)
                .setTitle("choose the account")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        RecyclerView dBox_recyclerView = view.findViewById(R.id.cal_dialog_recycleV);
        Cal_dilg_adapter cal_dilg_adapter = new Cal_dilg_adapter(cal_acc_list);
        dBox_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dBox_recyclerView.setAdapter(cal_dilg_adapter);
        return builder.create();
    }
}
