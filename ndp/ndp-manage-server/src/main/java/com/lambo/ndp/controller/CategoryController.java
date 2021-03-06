package com.lambo.ndp.controller;

import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.ResultCollectors;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lambo.common.annotation.EnableExportTable;
import com.lambo.common.annotation.LogAround;
import com.lambo.common.base.BaseController;

import com.lambo.common.utils.lang.StringUtils;
import com.lambo.ndp.constant.NdpResult;
import com.lambo.ndp.constant.NdpResultConstant;
import com.lambo.ndp.model.Category;
import com.lambo.ndp.model.CategoryExample;
import com.lambo.ndp.service.api.CategoryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.lambo.common.validator.LengthValidator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类controller
 * Created by z xc on 2018/3/14.
 */
@Controller
@Api(value = "数据分类", description = "数据分类")
@RequestMapping("/manage/category")
public class CategoryController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "数据分类列表")
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    @LogAround("请求列表数据")
    public Object list(
            @RequestParam(required = false, defaultValue = "0", value = "offset") int offset,
            @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
            @ApiParam(name="sort", value = "排序字段")
            @RequestParam(required = false, value = "sort") String sort,
            @ApiParam(name="order", value = "排序方式")
            @RequestParam(required = false, value = "order") String order,
            @RequestParam(required = false, defaultValue = "", value = "search") String search) {

        CategoryExample CategoryExample = new CategoryExample();
        if(StringUtils.isBlank(sort)){
            sort = "category_order";
        }
        if(StringUtils.isBlank(order)){
            order = "asc";
        }
        CategoryExample.setOrderByClause(StringUtils.humpToLine(sort) + " " + order);
        if (StringUtils.isNotBlank(search)) {
            CategoryExample.or().andCategoryNameLike("%" + search + "%");
        }
        //物理分页
        PageHelper.offsetPage(offset, limit);
        List<Category> data = categoryService.selectByExample(CategoryExample);
        PageInfo page = new PageInfo(data);
        Map<String, Object> result = new HashMap<>();
        result.put("rows", page.getList());
        result.put("total", page.getTotal());
        return new NdpResult(NdpResultConstant.SUCCESS,result);

    }
    @ApiOperation(value = "数据分类列表")
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    @ResponseBody
    @EnableExportTable
    @LogAround("请求列表数据")
    public Object listExport(
            @RequestParam(required = false, defaultValue = "0", value = "offset") int offset,
            @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
            @ApiParam(name="sort", value = "排序字段")
            @RequestParam(required = false, value = "sort") String sort,
            @ApiParam(name="order", value = "排序方式")
            @RequestParam(required = false, value = "order") String order,
            @RequestParam(required = false, defaultValue = "", value = "search") String search) {

        return ((NdpResult)list(offset,limit,sort,order,search)).data;

    }


    @ApiOperation(value = "新增数据分类")
    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Object create(
            @RequestParam(required = true, value = "categoryName") String categoryName,
            @RequestParam(required = true, value = "categoryOrder") int categoryOrder,
            @RequestParam(required = true, value = "categoryImg") String categoryImg,
            @RequestParam(required = false, value = "categoryDesc") String categoryDesc) {

        Category category = new Category();
        ComplexResult result = FluentValidator.checkAll()
                .on(categoryName, new LengthValidator(1, 50, "名称"))
                .on(categoryDesc, new LengthValidator(0, 100, "描述"))
                .doValidate()
                .result(ResultCollectors.toComplex());
        if (!result.isSuccess()) {
            return new NdpResult(NdpResultConstant.INVALID_LENGTH, result.getErrors());
        }
        Date day=new Date();
        Subject subject = SecurityUtils.getSubject();
        String username = (String) subject.getPrincipal();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        category.setCategoryDesc(categoryDesc);
        category.setCategoryName(categoryName);
        category.setCategoryOrder(categoryOrder);
        category.setCategoryImg(categoryImg);
        category.setCreateUser(username);
        category.setCreateTime(df.format(day).toString());
        int count = categoryService.insertSelective(category);
        if(count==1){
            return new NdpResult(NdpResultConstant.SUCCESS, count);
        }else{
            return new NdpResult(NdpResultConstant.FAILED, count);
        }
    }


    @ApiOperation(value = "删除数据分类")
    @RequestMapping(value = "/delete/{ids}",method = RequestMethod.GET)
    @ResponseBody
    public Object delete(@PathVariable("ids") String ids) {
        int count = categoryService.deleteByPrimaryKeys(ids);
        return new NdpResult(NdpResultConstant.SUCCESS, count);
    }


    @ApiOperation(value = "修改数据分类")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Object update(
            @PathVariable("id") int id,
            @RequestParam(required = true, value = "categoryName") String categoryName,
            @RequestParam(required = true, value = "categoryOrder") int categoryOrder,
            @RequestParam(required = true, value = "categoryImg") String categoryImg,
            @RequestParam(required = false, value = "categoryDesc") String categoryDesc) {

        Category category = new Category();
        if (StringUtils.isNotBlank(categoryDesc)) {
            category.setCategoryDesc(categoryDesc);
        }
        category.setCategoryName(categoryName);
        ComplexResult result = FluentValidator.checkAll()
                .on(categoryName, new LengthValidator(1, 50, "名称"))
                .on(categoryDesc, new LengthValidator(0, 100, "描述"))
                .doValidate()
                .result(ResultCollectors.toComplex());
        if (!result.isSuccess()) {
            return new NdpResult(NdpResultConstant.INVALID_LENGTH, result.getErrors());
        }
        Date day=new Date();
        Subject subject = SecurityUtils.getSubject();
        String username = (String) subject.getPrincipal();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        category.setCreateUser(username);
        category.setCreateTime(df.format(day).toString());
        category.setCategoryImg(categoryImg);
        category.setCategoryOrder(categoryOrder);
        category.setCategoryId(id);
        int count = categoryService.updateByPrimaryKeySelective(category);
        return new NdpResult(NdpResultConstant.SUCCESS, count);
    }


    @ApiOperation(value = "根据ID查询分类")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Object get(@PathVariable("id") int id) {
        Category Category = categoryService.selectByPrimaryKey(id);
        return new NdpResult(NdpResultConstant.SUCCESS, Category);
    }
}