package com.jc.position2.logic.log;

import java.util.Date;

import lombok.Data;

/**
 * Created by tconan on 16/4/22.
 */
@Data
public class JCLog {
    private boolean isUpload = false;
    private Long saveTimeStap;
    private StringBuffer log = new StringBuffer();
    public void add(String str) {
        log.append(str + ";");
    }
}
