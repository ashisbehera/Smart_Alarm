package com.example.smartalarm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class About extends AppCompatActivity {
    TextView dev1, dev2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        dev1 = findViewById(R.id.dev1);
        dev2 = findViewById(R.id.dev2);
        dev1.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/ashis-behera-6457a6156/"));
            startActivity(intent);
        });
        dev2.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/ankan-mondal-2b2b59138/"));
            startActivity(intent);
        });
    }
}