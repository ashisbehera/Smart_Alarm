package com.example.smartalarm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class Ringtone extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone);
        ListView listView = findViewById(R.id.listView);
        // reading external directory
        ArrayList<File> myMusic = fetchMusic(Environment.getExternalStorageDirectory());
        // storing song name in items
        String[] items = new String[myMusic.size()];
        // getting music file names without displaying ".mp3"
        for (int i = 0; i < myMusic.size(); i++) {
            items[i] = myMusic.get(i).getName().replace(".mp3", "");
        }
        // list of media files
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Ringtone.this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        // fetching information of songs
        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent intent = new Intent(Ringtone.this, AddAlarm_Activity.class);
            String musicName = listView.getItemAtPosition(position).toString();
            intent.putExtra("songList", myMusic);
            intent.putExtra("currentMusic", musicName);
            intent.putExtra("position", position);
            startActivity(intent);
        });
    }

    // will return all music files
    public ArrayList<File> fetchMusic(File file) {
        ArrayList arrayList = new ArrayList();
        File[] songs = file.listFiles();
        if (songs != null) {
            for (File myFile : songs) {
                if (!myFile.isHidden() && myFile.isDirectory()) {
                    arrayList.addAll(fetchMusic(myFile));
                } else {
                    if (myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")) {
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }
}