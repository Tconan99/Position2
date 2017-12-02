package com.jc.position2.logic.upload;

import com.jc.position2.base.network.BaseNetWork;
import com.jc.position2.logic.login.UserNetWork;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by tconan on 16/4/13.
 */
public interface UploadNetWork {
    @FormUrlEncoded
    @POST("android/position/upload/upload.action")
    Call<BaseNetWork> upload(@FieldMap Map<String, String> options);

}
