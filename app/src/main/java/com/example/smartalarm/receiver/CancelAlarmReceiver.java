package com.example.smartalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.smartalarm.AlarmConstraints;
import com.example.smartalarm.ControlAlarm;

public class CancelAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmConstraints alarm=(AlarmConstraints)intent.getBundleExtra
                (AlarmConstraints.ALARM_KEY).getParcelable(AlarmConstraints.ALARM_KEY);
        ControlAlarm controlAlarm = new ControlAlarm();
        controlAlarm.stopAlarm((AlarmConstraints)alarm , context);
    }
}
