package com.jc.position2.logic.login;

import android.util.Log;

import com.jc.position2.base.callback.CallBack;
import com.jc.position2.base.callback.ServiceResult;
import com.jc.position2.base.network.Const;
import com.jc.position2.base.network.NetWorkService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by tconan on 16/4/13.
 */
public class LoginService extends NetWorkService {

    public static LoginNetWork getLoginNetWork() {
        Retrofit retrofit = getRetrofit();

        LoginNetWork loginNetWork = retrofit.create(LoginNetWork.class);
        //retrofit.

        return loginNetWork;
    }

    public static void login(Map<String, String> options, final CallBack callBack) {

        Call<UserNetWork> call = getLoginNetWork().login(options);

        call.enqueue(new Callback<UserNetWork>(){

            @Override
            public void onResponse(Call<UserNetWork> call, Response<UserNetWork> response) {
                UserNetWork userNetWork = response.body();

                if (response.code()==200 && userNetWork!=null && userNetWork.isSuccess()) {
                    if (callBack!=null) {
                        LoginService.callback(callBack, true, Const.CB_SUCCESS, userNetWork.getDataObject(UserBean.class));
                    }
                } else {
                    if (callBack!=null) {
                        LoginService.callback(callBack, false, response.code() + ":" + String.valueOf(userNetWork.getErrormsg()), null);
                    }
                }

            }

            @Override
            public void onFailure(Call<UserNetWork> call, Throwable t) {
                if (callBack!=null) {
                    LoginService.callback(callBack, false, Const.CB_NETERROR, null);
                    Log.e("error", "error", t);
                }
            }
        });

    }

    public static void callback(CallBack callBack, boolean isSuccess, String message, Object tag) {
        ServiceResult result = new ServiceResult();
        result.setSuccess(isSuccess);
        result.setMessage(message);
        result.setTag(tag);

        if (callBack!=null) {
            callBack.callBack(result);
        }
    }

}
