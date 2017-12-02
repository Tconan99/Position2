package com.jc.position2.logic.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.jc.position2.base.application.BaseApplication;
import com.jc.position2.base.ui.BaseActivity;
import com.jc.position2.logic.login.UserBean;
import com.jc.position2.logic.login.UserStoreService;

import java.util.GregorianCalendar;

/**
 * Created by tconan on 16/3/23.
 */
public class AlarmService {

    private Context context;

    public AlarmService(Context context) {
        this.context = context;
    }

    public void start() {

        UserStoreService user = new UserStoreService(context);
        UserBean userBean = user.load();

        if (userBean==null) {
            // Toast.makeText(context, "没有登录", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i("data11", "AlarmService.start().Success");

        Intent intent = new Intent("android.intent.action.ALARM_RECEIVER");
        intent.putExtra("packageName", context.getApplicationInfo().packageName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 111, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(gregorianCalendar.getTimeInMillis() + 2000L);

        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, gregorianCalendar.getTimeInMillis(), AlarmTimeUtils.TIME_RUN, pendingIntent);

    }

    public void close() {
        Intent intent = new Intent("android.intent.action.ALARM_RECEIVER");
        intent.putExtra("packageName", context.getApplicationInfo().packageName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 111, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
