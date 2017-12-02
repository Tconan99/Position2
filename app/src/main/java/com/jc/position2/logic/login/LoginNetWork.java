package com.jc.position2.logic.login;

import com.jc.position2.base.network.BaseNetWork;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by tconan on 16/4/13.
 */
public interface LoginNetWork {
    @POST("android/position/login/login.action")
    Call<UserNetWork> login(@QueryMap Map<String, String> options);


}
