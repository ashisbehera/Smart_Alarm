package com.coffeecoders.smartalarm;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.coffeecoders.smartalarm.data.AlarmContract.AlarmEntry;

import java.util.ArrayList;


public class Ringtone extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    MediaPlayer mediaPlayer;
    private static final int RINGTONE_LOADER = 0;
    private ArrayList<String> arrayListInRing;
    private android.media.Ringtone ringtone;
    private RingtoneListAdapter rAdapter;
    private Uri prevUri;
    private AlarmConstraints ringtoneAlarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone);
        setTitle("Ringtone");
        Intent in = getIntent();
        prevUri = in.getData();
        /** will collect the the repeat day list **/
        arrayListInRing = in.getStringArrayListExtra("arrayList");
        ListView listView = findViewById(R.id.listView);
        rAdapter = new RingtoneListAdapter(this , null);
        listView.setAdapter(rAdapter);

        getLoaderManager().initLoader(RINGTONE_LOADER, null, this);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
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
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
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
                    intent.putExtra("ringtoneUri",ringtoneUri);
                    /** will return the repeat day list **/
                    intent.putStringArrayListExtra("arrayList" ,arrayListInRing);
                    intent.setData(prevUri);
                    intent.setAction("from ringtoneActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
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