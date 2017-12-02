package com.jc.position2.base.network;

import com.google.gson.Gson;
import com.jc.position2.logic.login.UserBean;
import com.squareup.okhttp.Response;

import lombok.Data;


/**
 * Created by tconan on 16/4/13.
 */
@Data
public class BaseNetWork {
    private String state;

    private String sessionId;

    private String errormsg;

    private Object data;

    private Response response;

    public <T> T getDataObject(Class<T> clazz) {

        if (data==null) {
            return null;
        }

        Gson gson = new Gson();

        String json = gson.toJson(data, data.getClass());

        if (json==null) {
            return null;
        }

        T t = gson.fromJson(json, clazz);

        return t;
    }

    public boolean isSuccess() {
        boolean isSuccess = false;

        if (Const.CB_SUCCESS.equals(state)) {
            isSuccess = true;
        }

        return isSuccess;
    }
}
