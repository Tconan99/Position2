package com.jc.position2.logic.alarm;

import com.jc.position2.base.callback.BaseResult;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by tconan on 16/4/29.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LocationResult extends BaseResult {
    private int state = 0;
    private String locationDescribe = "";
}
