package com.jc.position2.base.application;

import android.app.Application;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.jc.position2.base.callback.CallBack;
import com.jc.position2.base.callback.ServiceResult;
import com.jc.position2.logic.alarm.AlarmService;
import com.jc.position2.logic.alarm.AlarmTimeUtils;
import com.jc.position2.logic.alarm.LocationResult;
import com.jc.position2.logic.login.UserBean;
import com.jc.position2.logic.signin.SignInService;
import com.jc.position2.logic.login.UserStoreService;

import java.util.GregorianCalendar;

import im.fir.sdk.FIR;

/**
 * Created by tconan on 16/4/26.
 */
public class BaseApplication extends Application {

    public static BaseApplication baseApplication;
    public static SignInService signInService;
    public static AlarmService alarmService;

    @Override
    public void onCreate() {
        super.onCreate();

        FIR.init(this);

        baseApplication = this;
        initService();
    }

    public static void initService() {
        // 定位服务
        signInService = new SignInService(baseApplication, new CallBack() {
            @Override
            public void callBack(ServiceResult result) {
                Log.i("data11", "BaseApplication SignInService result->" + result.isSuccess());
            }
        });

        // 闹钟服务
        alarmService = new AlarmService(baseApplication);
    }

    public static void start(CallBack callBack) {
        start(callBack, true);
    }

    /**
     * 开始签到服务
     * @param callBack 回调方法
     * @param checkTime 是否强制开始(不强制开始会判断签到时间)
     */
    public static void start(CallBack callBack, boolean checkTime) {

        if (checkTime) {

            boolean isSignInTime = false;
            String message = "不是签到时间";

            LocationResult locationResult = AlarmTimeUtils.getStyle(new GregorianCalendar(), true);
            int style = locationResult.getState();
            if (style == AlarmTimeUtils.STATE_SIGNING) {
                isSignInTime = true;
            } else if (style == AlarmTimeUtils.STATE_SIGNED) {
                message = "已有签到记录, 请不要重复签到";
            } else {
                message = "现在不是签到时间";
            }

            if (!isSignInTime) {

                if (callBack!=null) {
                    ServiceResult result = new ServiceResult();
                    result.setSuccess(false);
                    result.setBaseService(signInService);
                    result.setMessage(message);
                    callBack.callBack(result);
                }
                return;
            }

        }

        if (signInService==null) {
            initService();
        }

        signInService.start(callBack);
    }


    public static String getImei() {
        TelephonyManager tm = (TelephonyManager) baseApplication.getSystemService(TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();

        UserStoreService userStoreService = new UserStoreService(baseApplication);
        UserBean userBean = userStoreService.load();

        //String newImei = SecurityUtil.Encrypt(imei, userBean.getUsername());
        //Log.i("data11", "newImei=" + newImei);
        //String oldImei = SecurityUtil.DecryptToString(newImei, userBean.getUsername());
        //Log.i("data11", "oldImei=" + oldImei);


        return imei;
    }




}
