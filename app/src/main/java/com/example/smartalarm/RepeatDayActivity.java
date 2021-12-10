package com.example.smartalarm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smartalarm.data.AlarmContract;

import java.util.ArrayList;

public class RepeatDayActivity extends AppCompatActivity{

    private ArrayList<String> arrayList;
    private RepeatDaysAdapter adapter;
    private Uri editUri;
    Intent in;
    Button okButton;
    String arr[] = {"Sunday" , "Monday" , "Tuesday" , "Wednesday" , "Thursday" , "Friday" , "Saturday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat_day);
        setTitle("Repeat days");
        in = getIntent();
        arrayList = in.getStringArrayListExtra("arrayList");
        editUri = in.getData();
        ListView listView = findViewById(R.id.daysList);
        adapter = new RepeatDaysAdapter(this ,R.layout.list_days , arr);
        listView.setAdapter(adapter);
        okButton = findViewById(R.id.daysOk);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RepeatDayActivity.this, AddAlarm_Activity.class);
                intent.putStringArrayListExtra("arrayList" , arrayList);
                intent.setAction("from repeatDayActivity");
                intent.setData(editUri);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }
        });
    }



    class RepeatDaysAdapter extends ArrayAdapter<String> {
        TextView textView;
        CheckBox checkBox;
        String TotalDays[];
        public RepeatDaysAdapter(@NonNull Context context, int resource, String[] arr) {
            super(context, resource , arr);
            TotalDays = arr;
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_days,parent,false);

            textView = convertView.findViewById(R.id.sundayTxt);
            checkBox = convertView.findViewById(R.id.sundayCheckB);
            textView.setText(TotalDays[position]);

            if (arrayList.contains(TotalDays[position]))
                checkBox.setChecked(true);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                    {
                        arrayList.add(TotalDays[position]);

                    }
                    else
                    {
                        arrayList.remove((TotalDays[position]));
                    }
                }
            });



            return  convertView;
        }
    }


}