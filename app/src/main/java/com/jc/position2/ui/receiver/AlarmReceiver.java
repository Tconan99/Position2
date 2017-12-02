package com.jc.position2.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jc.position2.base.application.BaseApplication;
import com.jc.position2.base.callback.CallBack;
import com.jc.position2.base.callback.ServiceResult;
import com.jc.position2.logic.alarm.AlarmTimeUtils;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String packageName = intent.getStringExtra("packageName");
        if (!context.getApplicationInfo().packageName.equals(packageName)) {
            Log.i("data11", "不是同一个应用" + (packageName==null?"null":packageName));
            return;
        }

        // 后台定位判断方式改变，
        if (AlarmTimeUtils.getEvent().getState() == AlarmTimeUtils.STATE_SIGNING) {
            Log.i("data11", "AlarmTimeUtils.STATE_SIGNING");

            BaseApplication.start(new CallBack() {
                @Override
                public void callBack(ServiceResult result) {
                    Log.i("data11", "AlarmReceiver.BaseApplication.END!!!");
                }
            });

        } else {
            Log.i("data11", "AlarmTimeUtils.STATE_OTHER");
        }

    }

}
