package com.coffeecoders.smartalarm.calender;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecoders.smartalarm.R;

import java.util.ArrayList;

public class Calender_dialogBox extends AppCompatDialogFragment implements Acc_clickListener {
    private static final String TAG = "Calender_dialogBox";
    private  ArrayList<String> cal_acc_list;
    Get_Cal_accName get_cal_accName;
    private String selected_radioButton;
    private int radioB_position;
    public Calender_dialogBox(ArrayList<String> list, String sel_cal_acc_name){
        cal_acc_list = list;
        selected_radioButton = sel_cal_acc_name;
    }

    private String selectedAccName = "";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        get_cal_accName = (Get_Cal_accName) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.cal_acc_dialog_box , null);

        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        get_cal_accName.getCalAccName(selectedAccName);
                    }
                });

        radioB_position = cal_acc_list.indexOf(selected_radioButton);
        RecyclerView dBox_recyclerView = view.findViewById(R.id.cal_dialog_recycleV);
        Cal_dilg_adapter cal_dilg_adapter = new Cal_dilg_adapter(cal_acc_list , this , radioB_position);
        dBox_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dBox_recyclerView.setAdapter(cal_dilg_adapter);
        return builder.create();
    }

    @Override
    public void sendAccountName(String str) {
        selectedAccName = str;
        Log.e(TAG, "sendAccountName: "+str );
    }

    public interface Get_Cal_accName {
        void getCalAccName(String st);
    }
}
