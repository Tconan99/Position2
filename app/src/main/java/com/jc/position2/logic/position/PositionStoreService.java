package com.jc.position2.logic.position;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jc.position2.base.store.StoreService;
import com.jc.position2.base.ui.BaseActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tconan on 16/3/23.
 */
public class PositionStoreService extends StoreService {

    private static final String name = "Position";
    private static final String key = "bdLocationList";
    private Gson gson = new Gson();
    public static boolean isChanged = true; // 缓存标记，减少IO次数
    private static List<JCLocation> list;

    public PositionStoreService(Context context) {
        super(context);
    }

    public void save(List<JCLocation> list) {
        isChanged = true;
        BaseActivity.dataUpdateTime = new Date().getTime();
        String json = gson.toJson(list, new TypeToken<List<JCLocation>>(){}.getType());
        save(name, key, json);
    }

    public List<JCLocation> load() {

        if (list==null || isChanged) {
            String json = load(name, key);
            list = gson.fromJson(json, new TypeToken<List<JCLocation>>() {}.getType());
        }

        // TODO 数据bug
        isChanged = false;

        if (list==null) {
            list = new ArrayList<>();
        }

        // 返回新的数组
        List<JCLocation> list = new ArrayList<>(this.list);
        return list;
    }


}
