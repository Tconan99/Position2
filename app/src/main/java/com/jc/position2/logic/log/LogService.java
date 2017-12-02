package com.jc.position2.logic.log;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jc.position2.base.callback.CallBack;
import com.jc.position2.base.callback.ServiceResult;
import com.jc.position2.base.network.BaseNetWork;
import com.jc.position2.base.network.NetWorkService;
import com.jc.position2.logic.login.UserBean;
import com.jc.position2.logic.login.UserStoreService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by tconan on 16/4/22.
 */
public class LogService extends NetWorkService {
    private Context context;
    private CallBack callBack;
    private List<JCLog> logs;
    List<JCLog> needUpdateLog;

    public static LogNetWork getLogNetWork() {
        Retrofit retrofit = getRetrofit();

        LogNetWork logNetWork = retrofit.create(LogNetWork.class);

        return logNetWork;
    }

    public LogService(Context context, CallBack callBack) {
        this.context = context;
        this.callBack = callBack;
    }

    public void uploadAuto() {

        UserStoreService user = new UserStoreService(context);
        UserBean userBean = user.load();

        if (userBean==null) {
            Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
            callBack(false);
            return;
        }

        LogStoreService logStoreService = new LogStoreService(context);
        logs = logStoreService.load();

        if (logs==null || logs.size()==0) {
            Toast.makeText(context, "没有日志", Toast.LENGTH_SHORT).show();
            callBack(true);
            return;
        }

        Gson gson = new Gson();

        needUpdateLog = new ArrayList<>();
        for (int i=0; i<logs.size(); ++i) {
            if (logs.get(i).isUpload()==false) {
                needUpdateLog.add(logs.get(i));
            }
        }

        Collections.reverse(needUpdateLog);

        if (needUpdateLog.size()==0) {
            callBack(true);
            return;
        }

        String log = gson.toJson(needUpdateLog);

        Map<String, String> options = new HashMap<>();
        options.put("userId", String.valueOf(userBean.getId()));
        options.put("log", log);
        upload(options);
    }

    public void upload(Map<String, String> options) {

        Call<BaseNetWork> call = getLogNetWork().upload(options);

        call.enqueue(new Callback<BaseNetWork>(){

            @Override
            public void onResponse(Call<BaseNetWork> call, Response<BaseNetWork> response) {
                BaseNetWork baseNetWork = response.body();

                if (response.code()==200 && baseNetWork!=null && baseNetWork.isSuccess()) {

                    if (needUpdateLog!=null && needUpdateLog.size()>0) {
                        for (JCLog log : needUpdateLog) {
                            log.setUpload(true);
                        }

                        // positions
                        LogStoreService logStoreService = new LogStoreService(context);
                        logStoreService.save(logs);
                    }

                    callBack(true);

                } else {
                    callBack(false);
                }

            }

            @Override
            public void onFailure(Call<BaseNetWork> call, Throwable t) {
                callBack(false);
                Log.e("error", "error", t);
            }
        });

    }

    public void callBack(boolean isSuccess) {
        if (callBack!=null) {
            ServiceResult result = new ServiceResult();
            result.setSuccess(isSuccess);
            result.setMessage("");
            result.setBaseService(this);
            callBack.callBack(result);
        }
    }
}
