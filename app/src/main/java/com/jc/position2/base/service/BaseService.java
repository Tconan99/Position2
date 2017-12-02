package com.jc.position2.base.service;

import com.jc.position2.base.network.BaseNetWork;

import java.util.Date;

import lombok.Data;

/**
 * Created by tconan on 16/4/19.
 */
public class BaseService {
    private long id = 0;

    public synchronized long getId() {

        if (id==0) {
            id = ClassIdUtil.getId();
        }

        return id;
    }

    public static long getId(BaseService baseService) {
        if (baseService==null) {
            return 0;
        }

        return baseService.getId();
    }

    public BaseService service() {
        return this;
    }
}
