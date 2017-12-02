package com.jc.position2.logic.log;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jc.position2.base.store.StoreService;
import com.jc.position2.logic.position.JCLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tconan on 16/4/22.
 */
public class LogStoreService extends StoreService {

    private static final String name = "Log";
    private static final String key = "bdLocationList";
    private Gson gson = new Gson();
    public static boolean isChanged = true; // 缓存标记，减少IO次数
    private static List<JCLog> list;

    public LogStoreService(Context context) {
        super(context);
    }

    public void save(List<JCLog> list) {
        isChanged = true;
        String json = gson.toJson(list, new TypeToken<List<JCLog>>(){}.getType());
        save(name, key, json);
    }

    public void add(JCLog log) {
        List<JCLog> logs = load();
        logs.add(log);
        save(logs);
    }

    public List<JCLog> load() {

        if (list==null || isChanged) {
            String json = load(name, key);
            list = gson.fromJson(json, new TypeToken<List<JCLog>>() {}.getType());
        }

        isChanged = false;

        if (list==null) {
            list = new ArrayList<>();
        }

        // 返回新的数组
        List<JCLog> list = new ArrayList<>(this.list);
        return list;
    }
}
