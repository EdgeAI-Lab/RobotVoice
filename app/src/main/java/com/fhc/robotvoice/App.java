package com.fhc.robotvoice;

import android.app.Application;

import com.alarm.AlarmController;
import com.alarm.PersistanceController;
import com.fhc.alarmManage.State;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechUtility;

import trikita.anvil.Anvil;
import trikita.jedux.Action;
import trikita.jedux.Logger;
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

        App.instance = this;

        PersistanceController persistanceController = new PersistanceController(this);
        State initialState = persistanceController.getSavedState();
        if (initialState == null) {
            initialState = State.Default.build();
        }

        this.store = new Store<>(new State.Reducer(),
                initialState,
                new Logger<>("Talalarmo"),
                persistanceController,
                new AlarmController(this));

        this.store.subscribe(Anvil::render);

        // 创建全局实例
        SpeechUtility.createUtility(App.this, "appid=" + getString(R.string.app_id));
        // 关闭MSC Log
        Setting.setShowLog(false);

    }

    public static State dispatch(Action action) {
        return instance.store.dispatch(action);
    }

    public static State getState() {
        return instance.store.getState();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
