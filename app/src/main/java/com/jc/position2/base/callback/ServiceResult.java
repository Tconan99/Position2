package com.jc.position2.base.callback;

import com.jc.position2.base.service.BaseService;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by tconan on 16/4/19.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ServiceResult extends BaseResult {

    private BaseService baseService;

    public long getId() {
        if (baseService==null) {
            return 0;
        }

        return baseService.getId();
    }
}
