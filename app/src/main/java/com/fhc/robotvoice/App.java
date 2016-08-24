package com.fhc.robotvoice;

import android.app.Application;

import com.fhc.alarmManage.State;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechUtility;

import trikita.jedux.Action;
import trikita.jedux.Store;

/**
 * Created by Administrator on 2016-08-22.
 */
public class App extends Application{

    private static App instance;

    private Store<Action, State> store;

    @Override
    public void onCreate() {
        super.onCreate();

        // 创建全局实例
        SpeechUtility.createUtility(App.this, "appid=" + getString(R.string.app_id));
        // 关闭MSC Log
        Setting.setShowLog(false);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
