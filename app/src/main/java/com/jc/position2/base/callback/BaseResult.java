package com.jc.position2.base.callback;

import lombok.Data;

/**
 * Created by tconan on 16/4/19.
 */
@Data
public class BaseResult {
    private boolean success = false;
    private String message = "";
    private Object tag;
}
