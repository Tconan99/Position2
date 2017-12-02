package com.jc.position2.base.network;

import android.content.Context;
import android.net.ConnectivityManager;

import java.lang.reflect.Method;

/**
 * Created by tconan on 16/3/21.
 */
public class GPSRUtil extends NetworkUtil {

    private ConnectivityManager connectivityManager;
    private Context context;

    public GPSRUtil(Context context) {
        this.context = context;
        this.connectivityManager = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    //打开或关闭GPRS
    private boolean gprsEnabled(boolean bEnable)
    {
        Object[] argObjects = null;

        boolean isOpen = this.gprsIsOpenMethod();
        if(isOpen == !bEnable)
        {
            setGprsEnable("setMobileDataEnabled", bEnable);
        }

        return isOpen;
    }

    //检测GPRS是否打开
    private boolean gprsIsOpenMethod()
    {
        String methodName = "getMobileDataEnabled";
        Class cmClass = connectivityManager.getClass();
        Class[] argClasses = null;
        Object[] argObject = null;

        Boolean isOpen = false;
        try
        {
            Method method = cmClass.getMethod(methodName, argClasses);

            isOpen = (Boolean) method.invoke(connectivityManager, argObject);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return isOpen;
    }

    //开启/关闭GPRS
    private void setGprsEnable(String methodName, boolean isEnable)
    {
        Class cmClass = connectivityManager.getClass();
        Class[] argClasses = new Class[1];
        argClasses[0] = boolean.class;

        try
        {
            Method method = cmClass.getMethod(methodName, argClasses);
            method.invoke(connectivityManager, isEnable);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void toggleMobileData(boolean enabled){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Method setMobileDataEnabl;
        try {
            setMobileDataEnabl = connectivityManager.getClass().getDeclaredMethod("setMobileDataEnabled", boolean.class);
            setMobileDataEnabl.invoke(connectivityManager, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isNetworkStart() {
        return gprsIsOpenMethod();
    }

    @Override
    public void setNetworkStart() {
        gprsEnabled(true);
    }

    @Override
    public void setNetworkClose() {
        gprsEnabled(false);
    }
}
