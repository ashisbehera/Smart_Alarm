package com.example.smartalarm;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartalarm.data.AlarmContract.AlarmEntry;
import com.example.smartalarm.data.Alarm_Database;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Ringtone extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    MediaPlayer mediaPlayer;
    private static final int RINGTONE_LOADER = 0;
    ArrayList<Uri> local_ringtone;
    android.media.Ringtone ringtone;
    RingtoneListAdapter rAdapter;
    Uri prevUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone);
        Intent in = getIntent();
        prevUri = in.getData();
        ListView listView = findViewById(R.id.listView);
        rAdapter = new RingtoneListAdapter(this , null);
        listView.setAdapter(rAdapter);

        getLoaderManager().initLoader(RINGTONE_LOADER, null, this);

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
    /**custom ringtoe adapter **/
    private class RingtoneListAdapter extends CursorAdapter{

        public RingtoneListAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(context).
                    inflate(R.layout.ringtone_custom_list, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView ringtoneNameTextView = (TextView) view.findViewById(R.id.music_name);
            ImageView playPause = view.findViewById(R.id.listen);

            int ringtoneNameColumnIndex = cursor.getColumnIndex(AlarmEntry.RINGTONE_NAME);
            int ringtoneUriColumnIndex = cursor.getColumnIndex(AlarmEntry.RINGTONE_URI);
            String ringtoneName = cursor.getString(ringtoneNameColumnIndex);
            String ringtoneUri = cursor.getString(ringtoneUriColumnIndex);
            /** convert the ringtone string to uri **/
            Uri uri = Uri.parse(ringtoneUri);
            ringtoneNameTextView.setText(ringtoneName);
            /** play button click listener **/
            playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mediaPlayer = MediaPlayer.create(Ringtone.this,
                                            uri);
                    mediaPlayer.start();
                }
            });

            ringtoneNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Ringtone.this, AddAlarm_Activity.class);
                    intent.putExtra("ringtoneName",ringtoneName);
                    intent.putExtra("ringtoneUri",uri);
                    intent.setData(prevUri);
                    startActivity(intent);
                }
            });


        }
    }

    /** background thread **/
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AlarmEntry.RINGTONE_ID,
                AlarmEntry.RINGTONE_NAME,
                AlarmEntry.RINGTONE_URI};

        return new CursorLoader(this,
                /**ringtone content uri
                 * @AlarmContract -> AlarmEntry
                 * **/
                AlarmEntry.RINGTONE_CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
      rAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        rAdapter.swapCursor(null);
    }
}