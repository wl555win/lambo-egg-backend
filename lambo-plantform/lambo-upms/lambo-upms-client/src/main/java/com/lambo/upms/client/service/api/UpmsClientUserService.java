package com.lambo.upms.client.service.api;

import com.lambo.upms.client.dao.model.UpmsUser;
import com.lambo.upms.client.dao.model.UpmsUserExample;
import com.lambo.common.base.BaseService;

/**
* UpmsUserService接口
* Created by lambo on 2017/3/20.
*/
public interface UpmsClientUserService extends BaseService<UpmsUser, UpmsUserExample> {

    UpmsUser createUser(UpmsUser upmsUser);

}