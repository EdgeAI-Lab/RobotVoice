package com.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fhc.alarmManage.Actions;
import com.fhc.robotvoice.App;

import trikita.jedux.Action;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        App.dispatch(new Action<>(Actions.Alarm.WAKEUP));
        System.out.println("alarm on!");
    }
}
