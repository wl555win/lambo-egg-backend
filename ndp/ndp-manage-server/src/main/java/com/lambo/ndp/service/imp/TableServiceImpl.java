package com.lambo.ndp.service.imp;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.unbiz.fluentvalidator.ComplexResult;
import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.ResultCollectors;
import com.lambo.common.annotation.BaseService;
import com.lambo.common.db.DynamicDataSource;
import com.lambo.common.validator.LengthValidator;
import com.lambo.ndp.constant.DataSourceEnum;
import com.lambo.ndp.constant.NdpResult;
import com.lambo.ndp.constant.NdpResultConstant;
import com.lambo.ndp.dao.api.TableMapper;
import com.lambo.ndp.model.Table;
import com.lambo.ndp.model.TableCell;
import com.lambo.ndp.service.api.TableService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
* TableService实现
* Created by zxc on 2018/3/10.
*/
@Service

public class TableServiceImpl implements TableService {

    private static Logger logger = LoggerFactory.getLogger(TableServiceImpl.class);

    @Autowired
    TableMapper tableMapper;

    public List<Map<String,Object>> queryTable(Map<String,Object> param) {
        return tableMapper.queryTable(param);
    }
    public List<Map<String,Object>> queryTableCell(int tableId) {
        return tableMapper.queryTableCell(tableId);
    }
    public int deleteTableCellByPrimaryKey(int cellId){
        return tableMapper.deleteTableCellByPrimaryKey(cellId);
    }
    public List<Map<String,Object>> queryDbTable(Map<String, Object> param){
        List<Map<String,Object>> list=new ArrayList<>();
        try{
            DynamicDataSource.setDataSource(DataSourceEnum.GREENPLUM.getName());
            list= tableMapper.queryDbTable(param);
        }catch (Exception e){
            logger.error("Exception", e);
        }finally{
            DynamicDataSource.clearDataSource();
        }
        return list;
    }
    public List<Map<String,Object>> queryDbTableColumns(String tableName){

        try{
            DynamicDataSource.setDataSource(DataSourceEnum.GREENPLUM.getName());
            return tableMapper.queryDbTableColumns(tableName);
        }catch (Exception e){
            logger.error("Exception", e);
        }finally{
            DynamicDataSource.clearDataSource();
        }
        return null;
    }
    @Transactional
    public Object create(String tableCode,String tableName,String tableDesc,String TableCellss){
        ComplexResult result = FluentValidator.checkAll()
                .on(tableCode, new LengthValidator(1, 50, "表名"))
                .on(tableName, new LengthValidator(0, 50, "中文名"))
                .doValidate()
                .result(ResultCollectors.toComplex());
        if (!result.isSuccess()) {
            return new NdpResult(NdpResultConstant.INVALID_LENGTH, result.getErrors());
        }
        Subject subject = SecurityUtils.getSubject();
        String username = (String) subject.getPrincipal();
        Date day=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Table table=new Table();
        table.setCreateTime(df.format(day).toString());
        table.setCreateUser(username);
        table.setTableCode(tableCode);
        table.setTableDesc(tableDesc);
        table.setTableName(tableName);
        int con =tableMapper.insertTable(table);
        if (con!=1) {
            return new NdpResult(NdpResultConstant.INVALID_LENGTH, result.getErrors());
        }
        int tableId=table.getTableId();
        JSONArray json = JSONArray.parseArray(TableCellss);
        if(json.size()>0){
            for(int i=0;i<json.size();i++){
                JSONObject job = json.getJSONObject(i);
                TableCell tableCell=new TableCell();
                tableCell.setTableId(tableId);
                tableCell.setCellCode((String) job.get("cellcode"));
                tableCell.setCellName((String) job.get("cellName"));
                tableCell.setDictId((String) job.get("dictId"));
                tableCell.setDataDesc((String) job.get("dataDesc"));
                tableCell.setDataUnit((String) job.get("dataUnit"));
                int conCell =tableMapper.insertTableCell(tableCell);

            }
        }
        return new NdpResult(NdpResultConstant.SUCCESS, con);
    }
    @Transactional
    public Object update(int tableId,String tableCode,String tableName,String tableDesc,String TableCellss){
        int count = 0;
        ComplexResult result = FluentValidator.checkAll()
                .on(tableCode, new LengthValidator(1, 50, "表名"))
                .on(tableName, new LengthValidator(0, 50, "中文名"))
                .doValidate()
                .result(ResultCollectors.toComplex());
        if (!result.isSuccess()) {
            return new NdpResult(NdpResultConstant.INVALID_LENGTH, result.getErrors());
        }
        Subject subject = SecurityUtils.getSubject();
        String username = (String) subject.getPrincipal();
        if(tableId > 0){
            Map<String,Object> param = new HashMap<String, Object>();
            Date day=new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            param.put("tableId",tableId);
            param.put("tableCode",tableCode);
            param.put("tableName",tableName);
            param.put("createUser",username);
            param.put("tableDesc",tableDesc);
            param.put("createTime",df.format(day).toString());
            count = tableMapper.updateTable(param);
        }
        count = tableMapper.deleteTableCellByTableId(tableId);
        JSONArray json = JSONArray.parseArray(TableCellss);
        if(json.size()>0){
            for(int i=0;i<json.size();i++){
                JSONObject job = json.getJSONObject(i);
                System.out.println("job:"+job) ;
                TableCell tableCell=new TableCell();
                tableCell.setTableId(tableId);
                tableCell.setCellCode((String) job.get("cellcode"));
                tableCell.setCellName((String) job.get("cellName"));
                tableCell.setDictId((String) job.get("dictId"));
                tableCell.setDataDesc((String) job.get("dataDesc"));
                tableCell.setDataUnit((String) job.get("dataUnit"));
                int conCell =tableMapper.insertTableCell(tableCell);

            }
        }
        return new NdpResult(NdpResultConstant.SUCCESS, count);
    }
    @Transactional
    public Object deleteTable(int tableId){
        tableMapper.deleteTableCellByTableId(tableId);
        return tableMapper.deleteTableByTableId(tableId);
    }

    public Object get(int id){
        Map<String,Object> param = tableMapper.selectTableByPrimaryKey(id);
        List data = tableMapper.queryTableCell(id);
        data.add(0,param);
        return new NdpResult(NdpResultConstant.SUCCESS,data);
    }
    public Object queryTableColumns(String tableName){
        List<Map<String,Object>> data=new ArrayList<>();
        Map<String,Object> parm=new HashMap<String,Object>();
        parm.put("tableName",tableName);
        try{
            DynamicDataSource.setDataSource(DataSourceEnum.GREENPLUM.getName());
            data = tableMapper.queryTableColumns(parm);
        }catch (Exception e){
            logger.error("Exception", e);
        }finally{
            DynamicDataSource.clearDataSource();
        }

        for(int i=0;i<data.size();i++){
            data.get(i).put("cellName","");
        }
        return new NdpResult(NdpResultConstant.SUCCESS,data);
    }
}