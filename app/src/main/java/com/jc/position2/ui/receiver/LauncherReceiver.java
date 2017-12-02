package com.jc.position2.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jc.position2.logic.alarm.AlarmService;

public class LauncherReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            Log.i("data11", "Boot Message!!! I'm back");
            AlarmService alarmService = new AlarmService(context);
            alarmService.start();
        }
    }
}
