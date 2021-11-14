package com.example.smartalarm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Ringtone extends AppCompatActivity {
    MediaPlayer mediaPlayer;
   // ArrayList<File> myMusic;
    ArrayList<Uri> local_ringtone;
    android.media.Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone);
        ListView listView = findViewById(R.id.listView);
        // permission to use external storage for local music
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                       // myMusic = fetchMusic(Environment.getExternalStorageDirectory());
                        // get music titles
                        local_ringtone = (ArrayList<Uri>) loadLocalRingtonesUris();
                        ArrayList<String> items = new ArrayList<>();
                        for (int i = 0; i < local_ringtone.size(); i++) {
                            ringtone = RingtoneManager.getRingtone(getApplicationContext(), local_ringtone.get(i));
                            items.add(ringtone.getTitle(getApplicationContext()));
                        }

                        // set custom adapter for music title and play button
                        listView.setAdapter(new RingtoneListAdapter(Ringtone.this, R.layout.ringtone_custom_list, items));
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
        // set song title in the other activity
        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent intent = new Intent(Ringtone.this, AddAlarm_Activity.class);
            String musicName = listView.getItemAtPosition(position).toString();
            intent.putExtra("songList", local_ringtone);
            intent.putExtra("currentMusic", musicName);
            intent.putExtra("position", position);
            startActivity(intent);
        });
    }

    // return all music files
//    public ArrayList<File> fetchMusic(File file) {
//        ArrayList arrayList = new ArrayList();
//        File[] songs = file.listFiles();
//        if (songs != null) {
//            for (File myFile : songs) {
//                if (!myFile.isHidden() && myFile.isDirectory()) {
//                    arrayList.addAll(fetchMusic(myFile));
//                } else {
//                    if (myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")) {
//                        arrayList.add(myFile);
//                    }
//                }
//            }
//        }
//        return arrayList;
//    }

    private List<Uri> loadLocalRingtonesUris() {
        List<Uri> alarms = new ArrayList<>();
        try {
            RingtoneManager ringtoneMgr = new RingtoneManager(getApplicationContext());
            ringtoneMgr.setType(RingtoneManager.TYPE_RINGTONE | RingtoneManager.TYPE_ALARM);

            Cursor alarmsCursor = ringtoneMgr.getCursor();
            int alarmsCount = alarmsCursor.getCount();
            if (alarmsCount == 0 && !alarmsCursor.moveToFirst()) {
                alarmsCursor.close();
                return null;
            }

            while (!alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) {
                int currentPosition = alarmsCursor.getPosition();
                alarms.add(ringtoneMgr.getRingtoneUri(currentPosition));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return alarms;
    }

    private class RingtoneListAdapter extends ArrayAdapter<String> {
        private final int layout;

        // custom adapter
        private RingtoneListAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewholder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.listen = convertView.findViewById(R.id.listen);
                viewHolder.title = convertView.findViewById(R.id.music_name);
                convertView.setTag(viewHolder);
            }
            viewholder = (ViewHolder) convertView.getTag();
            viewholder.listen.setOnClickListener(v -> {
               // Uri uri = Uri.parse(local_ringtone.get(position).toString());
                mediaPlayer = MediaPlayer.create(Ringtone.this, local_ringtone.get(position));
                mediaPlayer.start();
            });
            viewholder.title.setText(getItem(position));
            return convertView;
        }
    }

    public static class ViewHolder {
        ImageView listen;
        TextView title;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}