package com.lambo.auth.client.controller;

import com.lambo.auth.client.dao.model.UpmsStUser;
import com.lambo.auth.client.dao.model.UpmsStUserExample;
import com.lambo.auth.client.service.api.UpmsStUserService;
import com.lambo.common.annotation.EnableExportTable;
import com.lambo.common.annotation.LogAround;
import com.lambo.common.base.BaseController;
import com.lambo.common.base.BaseResult;
import com.lambo.common.base.BaseResultConstant;
import com.lambo.common.utils.lang.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UpmsStUsercontroller
 * Author wangjierj on 2018/7/17.
 */
@Controller
@RequestMapping("/manage/upmsStUser")
@Api(value = "UpmsStUser控制器", description = "UpmsStUser管理")
public class UpmsStUserController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(UpmsStUserController.class);

    @Autowired
    private UpmsStUserService upmsStUserService;

    @ApiOperation(value = "UpmsStUser列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    @LogAround("UpmsStUser分页数据")
    public Object list(
            @RequestParam(required = false, defaultValue = "0", value = "offset") int offset,
            @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
            @RequestParam(required = false, value = "sort") String sort,
            @RequestParam(required = false, value = "order") String order,
            @RequestParam(required = false, defaultValue = "", value = "search") String search) {

        UpmsStUserExample upmsStUserExample = new UpmsStUserExample();
        if (!StringUtils.isBlank(sort) && !StringUtils.isBlank(order)) {
            upmsStUserExample.setOrderByClause(sort + " " + order);
        }
        if (StringUtils.isNotBlank(search)) {
            upmsStUserExample.or().andUserIdEqualTo(search);
        }
        List<UpmsStUser> rows = upmsStUserService.selectByExampleForOffsetPage(upmsStUserExample, offset, limit);
        int total = upmsStUserService.countByExample(upmsStUserExample);
        Map<String, Object> result = new HashMap<>();
        result.put("rows", rows);
        result.put("total", total);
        return new BaseResult(BaseResultConstant.SUCCESS, result);
    }

    @ApiOperation(value = "UpmsStUser导出表格")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    @EnableExportTable
    @LogAround("UpmsStUser导出表格")
    public Object listExport(
            @RequestParam(required = false, defaultValue = "0", value = "offset") int offset,
            @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
            @RequestParam(required = false, value = "sort") String sort,
            @RequestParam(required = false, value = "order") String order,
            @RequestParam(required = false, defaultValue = "", value = "search") String search) {
        return ((BaseResult) list(offset, limit, sort, order, search)).data;
    }

    @ApiOperation(value = "根据UpmsStUser主键查询数据")
    @RequestMapping(value = "/get/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public Object get(@PathVariable("userId") String userId) {
        UpmsStUserExample upmsStUserExample = new UpmsStUserExample();
        upmsStUserExample.or().andUserIdEqualTo(userId);
        return upmsStUserService.selectFirstByExample(upmsStUserExample);
    }


    @ApiOperation(value = "新增UpmsStUser数据")
    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Object create(
            @RequestParam(required = true, defaultValue = "", value = "userId") String userId,
            @RequestParam(required = false, defaultValue = "", value = "nickName") String nickName,
            @RequestParam(required = false, defaultValue = "", value = "refId") String refId,
            @RequestParam(required = false, defaultValue = "", value = "userType") String userType,
            @RequestParam(required = false, defaultValue = "", value = "loginInfo") String loginInfo
    ) {

        UpmsStUser upmsStUser = new UpmsStUser();
        upmsStUser.setUserId(userId);
        upmsStUser.setNickName(nickName);
        upmsStUser.setRefId(refId);
        upmsStUser.setUserType(userType);
        upmsStUser.setLoginInfo(loginInfo);
        int count = upmsStUserService.insertSelective(upmsStUser);
        if (count <= 0) {
            return new BaseResult(BaseResultConstant.FAILED, 0);
        }
        return new BaseResult(BaseResultConstant.SUCCESS, 1);
    }

    @ApiOperation(value = "更新UpmsStUser数据")
    @ResponseBody
    @RequestMapping(value = "/update/{userId}", method = RequestMethod.POST)
    public Object update(
            @PathVariable("userId") String userId,
            @RequestParam(required = false, defaultValue = "", value = "nickName") String nickName,
            @RequestParam(required = false, defaultValue = "", value = "refId") String refId,
            @RequestParam(required = false, defaultValue = "", value = "userType") String userType,
            @RequestParam(required = false, defaultValue = "", value = "loginInfo") String loginInfo
    ) {

        UpmsStUser upmsStUser = new UpmsStUser();
        upmsStUser.setUserId(userId);
        if (!StringUtils.isBlank(nickName)) {
            upmsStUser.setNickName(nickName);
        }
        if (!StringUtils.isBlank(refId)) {
            upmsStUser.setRefId(refId);
        }
        if (!StringUtils.isBlank(userType)) {
            upmsStUser.setUserType(userType);
        }
        if (!StringUtils.isBlank(loginInfo)) {
            upmsStUser.setLoginInfo(loginInfo);
        }
        int count = upmsStUserService.updateByPrimaryKeySelective(upmsStUser);
        return new BaseResult(BaseResultConstant.SUCCESS, count);
    }

    @ApiOperation(value = "删除UpmsStUser数据")
    @RequestMapping(value = "/delete/{userIds}", method = RequestMethod.GET)
    @ResponseBody
    @LogAround("删除UpmsStUser数据")
    public Object delete(@PathVariable("userIds") String userIds) {
        int count = 0;
        if (StringUtils.isNotBlank(userIds)) {
            String[] ids = null;
            if (userIds.indexOf(",") > 0) {
                ids = userIds.split(",");
            } else {
                ids = new String[]{userIds};
            }
            for (int i = 0; i < ids.length; i++) {
                UpmsStUserExample upmsStUserExample = new UpmsStUserExample();
                upmsStUserExample.or().andUserIdEqualTo(ids[i]);
                count += upmsStUserService.deleteByExample(upmsStUserExample);
            }
        }
        return new BaseResult(BaseResultConstant.SUCCESS, count);
    }
}