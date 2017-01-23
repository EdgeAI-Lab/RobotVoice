package com.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import com.fhc.alarmManage.Actions;
import com.fhc.alarmManage.State;
import com.fhc.robotvoice.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import trikita.jedux.Action;
import trikita.jedux.Store;

public class AlarmController implements Store.Middleware<Action, State> {

    private final Context mContext;

    public AlarmController(Context c) {
        mContext = c;
    }

    private int alarmCode = 0;

    @Override
    public void dispatch(Store<Action, State> store, Action action, Store.NextDispatcher<Action> next) {

        if (action.type instanceof Actions.Alarm) {
            Actions.Alarm type = (Actions.Alarm) action.type;
            switch (type) {
                case SET_HOUR:
                case SET_MINUTE:
                case SET_AM_PM:
                case RESTART_ALARM:
                case ON:
                    restartAlarm();
                    break;
                case WAKEUP:
                    wakeupAlarm();
                    break;
                case DISMISS:
                    dismissAlarm();
                    restartAlarm();
                    break;
                case OFF:
                    cancelAlarm();
                    break;
            }
        }
    }

    private void restartAlarm() {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.AM_PM, 0);
        c.set(Calendar.HOUR, MainActivity.hour);
        c.set(Calendar.MINUTE, MainActivity.minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (System.currentTimeMillis() >= c.getTimeInMillis()) {
            c.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(mContext, alarmCode++, intent, 0);

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {        // KITKAT and later
                am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
            }
            intent = new Intent("android.intent.action.ALARM_CHANGED");
            intent.putExtra("alarmSet", true);
            mContext.sendBroadcast(intent);
            SimpleDateFormat fmt = new SimpleDateFormat("E HH:mm");
            Settings.System.putString(mContext.getContentResolver(),
                    Settings.System.NEXT_ALARM_FORMATTED,
                    fmt.format(c.getTime()));
        } else {
            Intent showIntent = new Intent(mContext, MainActivity.class);
            PendingIntent showOperation = PendingIntent.getActivity(mContext, alarmCode++, showIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(c.getTimeInMillis(), showOperation);
            am.setAlarmClock(alarmClockInfo, sender);
        }
    }

    private void cancelAlarm() {
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            intent = new Intent("android.intent.action.ALARM_CHANGED");
            intent.putExtra("alarmSet", false);
            mContext.sendBroadcast(intent);
            Settings.System.putString(mContext.getContentResolver(),
                    Settings.System.NEXT_ALARM_FORMATTED, "");
        }
    }

    private void wakeupAlarm() {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl =
                pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "AlarmReceiver");
        wl.acquire(5000);
        mContext.startService(new Intent(mContext, AlarmService.class));
    }

    private void dismissAlarm() {
        mContext.stopService(new Intent(mContext, AlarmService.class));
    }
}
