package com.alarm;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;

import com.fhc.alarmManage.Actions;
import com.fhc.alarmManage.App;
import com.ui.Theme;

import trikita.anvil.RenderableView;
import trikita.jedux.Action;

import static trikita.anvil.BaseDSL.FILL;
import static trikita.anvil.BaseDSL.dip;
import static trikita.anvil.BaseDSL.size;
import static trikita.anvil.BaseDSL.text;
import static trikita.anvil.BaseDSL.textSize;
import static trikita.anvil.DSL.backgroundColor;
import static trikita.anvil.DSL.onClick;
import static trikita.anvil.DSL.textColor;


public class AlarmActivity extends Activity {
    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "AlarmActivity");
        mWakeLock.acquire();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        // fill status bar with a theme dark color on post-Lollipop devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Theme.get(App.getState().settings().theme()).primaryDarkColor);
        }

        setContentView(new RenderableView(this) {
            public void view() {
                Theme.materialIcon(() -> {
                    size(FILL, FILL);
                    text("\ue857"); // "alarm off"
                    textColor(Theme.get(App.getState().settings().theme()).accentColor);
                    textSize(dip(128));
                    backgroundColor(Theme.get(App.getState().settings().theme()).backgroundColor);
                    onClick(v -> stopAlarm());
                });
            }
        });
    }

    @Override
    protected void onUserLeaveHint() {
        stopAlarm();
        super.onUserLeaveHint();
    }

    @Override
    public void onBackPressed() {
        stopAlarm();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
    }

    private void stopAlarm() {
        App.dispatch(new Action<>(Actions.Alarm.DISMISS));
        finish();
    }
}
