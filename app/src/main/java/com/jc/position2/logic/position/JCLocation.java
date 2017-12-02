package com.jc.position2.logic.position;

import android.util.Log;

import com.baidu.location.BDLocation;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import lombok.Data;

/**
 * Created by tconan on 16/4/18.
 */
@Data
public class JCLocation {
    private boolean isUpload = false;
    private long createTime = -1;
    private BDLocation location;
    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public long getCreateTime() {

        if (createTime<0) {
            createTime = 0L;
            try {
                createTime = simpleDateFormat.parse(getLocation().getTime()).getTime();
            } catch (Exception e) {
                Log.e("Error", "Parse string time error!", e);
            }
        }
        return createTime;
    }
}
