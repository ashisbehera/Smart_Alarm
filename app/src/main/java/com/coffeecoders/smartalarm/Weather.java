package com.coffeecoders.smartalarm;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Weather extends AppCompatActivity {
    TextView tv;
    EditText et;
    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        tv = findViewById(R.id.tv);
        et = findViewById(R.id.et);
        city = et.getText().toString();
    }

    public void get(View v) {
        city = et.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city.toLowerCase() + "&appid=375ef2cd332149cf9cc28fc0464b57af&units=metric";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject array = (JSONObject) response.get("main");
                String s = array.getString("temp");
                String str = s + "°C";
                tv.setText(str);
            } catch (JSONException e) {
                tv.setText(e.getMessage());
                e.printStackTrace();
            }
        }, error -> {
            tv.setText(error.toString());
            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        queue.add(request);
    }
}