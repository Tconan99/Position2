package com.jc.position2.logic.login;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jc.position2.base.store.StoreService;
import com.jc.position2.logic.login.UserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tconan on 16/4/14.
 */
public class UserStoreService extends StoreService {

    private static final String name = "User";
    private static final String key = "userInfo";
    private Gson gson = new Gson();
    private static boolean isChanged = true; // 缓存标记，减少IO次数
    private static UserBean userBean;

    public UserStoreService(Context context) {
        super(context);
    }

    public void save(UserBean userBean) {
        isChanged = true;
        String json = gson.toJson(userBean, UserBean.class);
        save(name, key, json, false);
    }

    public UserBean load() {

        if (userBean==null || isChanged) {
            String json = load(name, key, false);
            userBean = gson.fromJson(json, UserBean.class);
        }

        isChanged = false;

        if (userBean!=null) {
            return userBean.clone();
        } else {
            return null;
        }
    }

    public void remove() {
        isChanged = true;
        userBean = new UserBean();
        remove(name, key, false);
    }

    public boolean isLogined() {
        String json = load(name, key, false);

        if ("".equals(json)) {
            return false;
        }

        return true;
    }


}