package com.lambo.upms.common.constant;

import com.lambo.common.base.BaseResult;

/**
 * upms系统常量枚举类
 * Created by lambo on 2017/2/18.
 */
public class UpmsResult extends BaseResult {

    public UpmsResult(UpmsResultConstant upmsResultConstant, Object data) {
        super(upmsResultConstant.getCode(), upmsResultConstant.getMessage(), data);
    }

}
