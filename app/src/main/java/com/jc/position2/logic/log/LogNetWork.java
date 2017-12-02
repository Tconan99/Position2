package com.jc.position2.logic.log;

import com.jc.position2.base.network.BaseNetWork;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by tconan on 16/4/22.
 */
public interface LogNetWork {
    @FormUrlEncoded
    @POST("android/position/upload/log.action")
    Call<BaseNetWork> upload(@FieldMap Map<String, String> options);
}
