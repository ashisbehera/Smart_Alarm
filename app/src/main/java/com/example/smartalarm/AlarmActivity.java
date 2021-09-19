package com.example.smartalarm;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity);

        Intent intent = getIntent();
        setTitle("Alarms");
        //floating button for @AddAlarm_Activity
        FloatingActionButton add_alarm_fab = findViewById(R.id.add_alarm_fb);
        // sending intent to @AddAlarm_Activity
        add_alarm_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alarm_intent = new Intent
                        (AlarmActivity.this,AddAlarm_Activity.class );
                startActivity(alarm_intent);
            }
        });
    }

}
