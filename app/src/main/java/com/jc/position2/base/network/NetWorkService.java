package com.jc.position2.base.network;

import com.jc.position2.base.service.BaseService;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tconan on 16/4/13.
 */
public class NetWorkService extends BaseService {

    public static Lock lock = new ReentrantLock();// 锁
    public static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit==null) {
            lock.lock();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Const.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            lock.unlock();
        }

        return retrofit;
    }
}
