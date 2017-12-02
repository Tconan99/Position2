package com.jc.position2.base.store;

import android.content.Context;
import android.content.SharedPreferences;

import com.jc.position2.logic.login.UserBean;
import com.jc.position2.logic.login.UserStoreService;

/**
 * Created by tconan on 16/3/23.
 */
public class StoreService {

    private Context context;

    public StoreService(Context context) {
        this.context = context;
    }

    protected void save(String name, String key, String json) {
        save(name, key, json, true);
    }

    protected void save(String name, String key, String json, boolean haveSuff) {

        if (haveSuff) {
            name = getSuff(name);
        }

        SharedPreferences sp = this.context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, json);
        editor.commit();
    }

    protected String load(String name, String key) {
        return load(name, key, true);
    }

    protected String load(String name, String key, boolean haveSuff) {

        if (haveSuff) {
            name = getSuff(name);
        }

        SharedPreferences sp = this.context.getSharedPreferences(name, Context.MODE_PRIVATE);
        String json = sp.getString(key, "");
        return json;
    }

    protected void remove(String name, String key) {
        remove(name, key, true);
    }

    protected void remove(String name, String key, boolean haveSuff) {

        if (haveSuff) {
            name = getSuff(name);
        }

        SharedPreferences sp = this.context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    protected String getSuff(String name) {
        UserStoreService userStoreService = new UserStoreService(context);
        UserBean userBean = userStoreService.load();

        if (userBean==null) {
            return name + "-" + "null";
        }

        return name + "-" + userBean.getId();
    }

}
