package com.jc.position2.logic.upload;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.jc.position2.base.application.BaseApplication;
import com.jc.position2.base.callback.CallBack;
import com.jc.position2.base.callback.ServiceResult;
import com.jc.position2.base.network.BaseNetWork;
import com.jc.position2.base.network.NetWorkService;
import com.jc.position2.logic.login.UserBean;
import com.jc.position2.logic.position.JCLocation;
import com.jc.position2.logic.position.PositionStoreService;
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
 * Created by tconan on 16/4/15.
 */
public class UploadService extends NetWorkService {

    private Context context;
    private CallBack callBack;
    private List<JCLocation> positions;
    List<JCLocation> needUpdateLocation;

    public static UploadNetWork getLoginNetWork() {
        Retrofit retrofit = getRetrofit();

        UploadNetWork uploadNetWork = retrofit.create(UploadNetWork.class);

        return uploadNetWork;
    }

    public UploadService(Context context, CallBack callBack) {
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

        PositionStoreService position = new PositionStoreService(context);
        positions = position.load();

        if (positions==null || positions.size()==0) {
            Toast.makeText(context, "没有位置", Toast.LENGTH_SHORT).show();
            callBack(true);
            return;
        }

        Gson gson = new Gson();

        needUpdateLocation = new ArrayList<>();
        List<BDLocation> needUploadLocation = new ArrayList<>();
        for (int i=0; i<positions.size(); ++i) {
            if (positions.get(i).isUpload()==false) {
                needUploadLocation.add(positions.get(i).getLocation());
                needUpdateLocation.add(positions.get(i));
            }
        }

        Collections.reverse(needUploadLocation);

        if (needUploadLocation.size()==0) {
            callBack(true);
            return;
        }

        String positionStr = gson.toJson(needUploadLocation);

        String imei = BaseApplication.getImei();

        Map<String, String> options = new HashMap<>();
        options.put("userId", String.valueOf(userBean.getId()));
        options.put("imeiNo", imei);
        options.put("position", positionStr);
        upload(options);
    }

    public void upload(Map<String, String> options) {

        Call<BaseNetWork> call = getLoginNetWork().upload(options);

        call.enqueue(new Callback<BaseNetWork>(){

            @Override
            public void onResponse(Call<BaseNetWork> call, Response<BaseNetWork> response) {
                BaseNetWork baseNetWork = response.body();

                if (response.code()==200 && baseNetWork!=null && baseNetWork.isSuccess()) {

                    if (needUpdateLocation!=null && needUpdateLocation.size()>0) {
                        for (JCLocation location : needUpdateLocation) {
                            location.setUpload(true);
                        }

                        // positions
                        PositionStoreService position = new PositionStoreService(context);
                        position.save(positions);
                    }

                    callBack(true);

                } else {
                    callBack(false, response.code() + response.toString());
                }

            }

            @Override
            public void onFailure(Call<BaseNetWork> call, Throwable t) {
                callBack(false, t.getMessage());
                Log.e("error", "error", t);
            }
        });

    }

    public void callBack(boolean isSuccess) {
        callBack(isSuccess, "");
    }

    public void callBack(boolean isSuccess, String message) {
        if (callBack!=null) {
            ServiceResult result = new ServiceResult();
            result.setSuccess(isSuccess);
            result.setMessage(message);
            result.setBaseService(this);
            //result.setTag();
            callBack.callBack(result);
        }
    }

}
