package com.jc.position2.logic.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jc.position2.base.callback.CallBack;
import com.jc.position2.base.callback.ServiceResult;
import com.jc.position2.base.network.GPSRUtil;
import com.jc.position2.base.service.BaseService;
import com.jc.position2.logic.login.LoginService;

/**
 * Created by tconan on 16/4/15.
 */
public class OpenNetWorkService extends BaseService {

    private Context context;
    private CallBack callBack;
    private boolean isServiceOpen;

    public OpenNetWorkService(Context context, CallBack callBack) {
        this.context = context;
        this.callBack = callBack;
    }

    public void open4G() {

        GPSRUtil gpsrUtil = new GPSRUtil(context);
        if (gpsrUtil.isNetworkStart()) {
            if (callBack!=null) {
                callback(true);
            }
        } else {
            isServiceOpen = true;

            gpsrUtil.setNetworkStart();

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    NetWorkReceiver netWorkReceiver = new NetWorkReceiver();
                    netWorkReceiver.onReceive(context, null);
                }
            };

            handler.sendMessageDelayed(new Message(), 2000);
        }


    }

    // 如果是服务开启的网络，需要服务关闭网络
    public void close4GIfServiceOpen() {
        if (isServiceOpen) {
            Log.i("data11", "需要关闭网络");
            GPSRUtil gpsrUtil = new GPSRUtil(context);
            if (gpsrUtil.isNetworkStart()) {
                gpsrUtil.setNetworkClose();
            }
            isServiceOpen = false;
        } else {
            Log.i("data11", "不需要关闭网络");
        }
    }

    public class NetWorkReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent){

            Log.i("data11", "NetWorkReceiver");

            GPSRUtil gpsrUtil = new GPSRUtil(context);
            if (callBack != null) {
                if (gpsrUtil.isNetworkStart()) {
                    callback(true);
                } else {
                    callback(false);
                }
            }

            //context.unregisterReceiver(this);


        }
    }

    public void callback(boolean isSuccess) {
        if (callBack!=null) {
            ServiceResult serviceResult = new ServiceResult();
            serviceResult.setSuccess(isSuccess);

            callBack.callBack(serviceResult);
        }
    }
}
