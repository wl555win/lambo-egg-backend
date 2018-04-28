package com.lambo.ndp.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lambo.common.annotation.BaseService;
import com.lambo.common.base.BaseServiceImpl;
import com.lambo.ndp.constant.NdpResult;
import com.lambo.ndp.constant.NdpResultConstant;
import com.lambo.ndp.dao.api.SubjectMapper;
import com.lambo.ndp.model.Subject;
import com.lambo.ndp.model.SubjectExample;
import com.lambo.ndp.service.api.SubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * SubjectService实现
 * Created by zxc on 2018/2/28.
*/
@Service
@Transactional
@BaseService
public class SubjectServiceImpl extends BaseServiceImpl<SubjectMapper,Subject, SubjectExample> implements SubjectService {

    private static Logger logger = LoggerFactory.getLogger(SubjectServiceImpl.class);

    @Autowired
    SubjectMapper subjectMapper;

//    public List<Map<String, Object>> querySubject(Map<String, Object> param) {
//        return subjectMapper.querySubject(param);
//    }

//    public Map<String, Object> getSubject(int subjectId) {
//        return subjectMapper.getSubject(subjectId);
//    }
//
//    public List<Map<String, Object>> querySubjectColumn(int subjectId) {
//        return subjectMapper.querySubjectColumn(subjectId);
//    }
    @Override
    public Object insertSubject(int categoryId, String tableCode, int tableId, String subjectDesc, String subjectName, String subjectColumns,String subjectType,String subjectTime,String subjectOrgan,String subjectTag) {

        System.out.println("categoryId=" + categoryId + "tableCode=" + tableCode + ",tableId=" + tableId + "subjectDesc=" + subjectDesc + ",subjectName=" + subjectName);
        System.out.println("subjectTag=" + subjectTag+",subjectType=" + subjectType);
        System.out.println("subjectTime=" + subjectTime+",subjectOrgan=" + subjectOrgan);
        Subject subject = new Subject();
        subject.setCategoryId(categoryId);
        subject.setSubjectDesc(subjectDesc);
        subject.setTableId(tableId);
        subject.setTableCode(tableCode);
        subject.setSubjectName(subjectName);
        subject.setOrganType(subjectOrgan);
        subject.setPeriodType(subjectTime);
        subject.setSubjectType(subjectType);
        Date day=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        subject.setCreateTime(df.format(day).toString());
        int con = subjectMapper.insert(subject);
        int subjectId = subject.getSubjectId();
        //指标插入

        subjectTag=subjectTag.replace("[","");
        subjectTag=subjectTag.replace("]","");
        subjectTag=subjectTag.replace("\"","");
        System.out.println("subjectTag11="+subjectTag);
        String[] tagChar=subjectTag.split(",");
        if(tagChar.length>1){
            for(int a=0;a<tagChar.length;a++){
                Map<String,Object> tag=new HashMap<String,Object>();
                tag.put("subjectId",subjectId);
                tag.put("tagName",tagChar[a]);
                subjectMapper.insertTag(tag);
            }
        }
        //列插入
        JSONArray json = JSONArray.parseArray(subjectColumns);
        if (json.size() > 0) {
            if("1".equals(subjectType)){ //库表，表格
                for (int i = 0; i < json.size(); i++) {
                    // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                    JSONObject jobData = json.getJSONObject(i);
                    Map<String,Object> parmData = new HashMap();
                    parmData.put("columnName",jobData.get("cellName"));
                    parmData.put("cellId",jobData.get("cellId"));
                    parmData.put("searchCondition",jobData.get("searchCondition"));
                    parmData.put("searchSetting",jobData.get("searchSetting"));
                    parmData.put("columnOrder",jobData.get("columnOrder"));
                    if (jobData.get("isShow") == null) {
                        parmData.put("isShow","0");
                    }else{
                        parmData.put("isShow",jobData.get("isShow"));
                    }
                    parmData.put("subjectId",subjectId);
                    subjectMapper.insertSubjectColumn(parmData);

                }
            }else if("2".equals(subjectType)){ //文件
                for (int i = 0; i < json.size(); i++) {
                    JSONObject job = json.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                    Map<String,Object> parm = new HashMap();
                    parm.put("docName",job.get("docName"));
                    parm.put("docSaveId",job.get("docId"));
                    parm.put("subjectId",subjectId);
                    subjectMapper.insertSubjectDoc(parm);
                }
            }
        }
            return con;

    }
    @Override
    public Object updateSubject(int subjectId,int categoryId, String tableCode, int tableId, String subjectDesc, String subjectName, String subjectColumns,String subjectType,String subjectTime,String subjectOrgan,String subjectTag) {
        System.out.println("categoryId=" + categoryId + "tableCode=" + tableCode + ",tableId=" + tableId + "subjectDesc=" + subjectDesc + ",subjectName=" + subjectName);
        System.out.println("subjectTag=" + subjectTag+",subjectType=" + subjectType);
        System.out.println("subjectTime=" + subjectTime+",subjectOrgan=" + subjectOrgan);
        Subject subject = new Subject();
        subject.setCategoryId(categoryId);
        subject.setSubjectDesc(subjectDesc);
        subject.setTableId(tableId);
        subject.setTableCode(tableCode);
        subject.setSubjectName(subjectName);
        subject.setSubjectId(subjectId);
        subject.setOrganType(subjectType);
        subject.setPeriodType(subjectTime);
        subject.setSubjectType(subjectType);
        Date day=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        subject.setCreateTime(df.format(day).toString());
        // subjectMapper.updateSubject(subject);
         int con=subjectMapper.updateByPrimaryKeySelective(subject);
        JSONArray json = JSONArray.parseArray(subjectColumns);
         if("1".equals(subjectType)){
             subjectMapper.deleteSubjectColumnBySubjectId(subjectId);
             for (int i = 0; i < json.size(); i++) {
                 JSONObject jobData = json.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                 Map<String,Object> parmData = new HashMap();
                 parmData.put("columnName",jobData.get("cellName"));
                 parmData.put("cellId",jobData.get("cellId"));
                 parmData.put("searchCondition",jobData.get("searchCondition"));
                 parmData.put("searchSetting",jobData.get("searchSetting"));
                 parmData.put("columnOrder",jobData.get("columnOrder"));
                 if (jobData.get("isShow") == null) {
                     parmData.put("isShow","0");
                 }else{
                     parmData.put("isShow",jobData.get("isShow"));
                 }
                 parmData.put("subjectId",subjectId);
                 subjectMapper.insertSubjectColumn(parmData);

             }

         }else if("2".equals(subjectType)){
             subjectMapper.deleteSubjectDocBySubjectId(subjectId);
             for (int i = 0; i < json.size(); i++) {
                 JSONObject job = json.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                 Map<String,Object> parm = new HashMap();
                 parm.put("docName",job.get("docName"));
                 parm.put("docId",job.get("docId"));
                 parm.put("subjectId",subjectId);
                 subjectMapper.insertSubjectDoc(parm);
             }
         }
        return con;

    }
    @Override
    public int deleteSubjectBySubjectId(Integer subjectId){
        int con=1;
        //删除专题列
        con=subjectMapper.deleteSubjectColumnBySubjectId(subjectId);
        //删除专题
        //con=subjectMapper.deleteSubjectBySubjectId(subjectId);
        con=subjectMapper.deleteByPrimaryKey(subjectId);
        return con;
    }
    @Override
    public Object getSubjectById(int subjectId){
        Map<String,Object> param = subjectMapper.getSubject(subjectId);
        List<Map<String,Object>> dataTag=subjectMapper.getTagData(subjectId);
        System.out.println("param111="+param);
        ArrayList<String> tag = new ArrayList<String>();
        for(Map obj : dataTag){
            String tagName= (String) obj.get("tagName");
            tag.add(tagName);
        }
        param.put("subjectTag",tag);
        System.out.println("param="+param);
        String subjectType= (String) param.get("subjectType");
        List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();
        if("1".equals(subjectType)){
            data = subjectMapper.querySubjectColumn(subjectId);
        }else if("2".equals(subjectType)){
           data=subjectMapper.querySubjectDoc(subjectId);
        }else{
            return new NdpResult(NdpResultConstant.FAILED, data);
        }
        data.add(0,param);
        return new NdpResult(NdpResultConstant.SUCCESS, data);
    }
    @Override
    public Object initSubject(){
        String period="PERIOD_TYPE_ID";
        List<Map<String,Object>> dataPeriod = subjectMapper.getDictData(period);
        System.out.println("dataPeriod="+dataPeriod);

        String organ="ORGAN_TYPE_ID";
        List<Map<String,Object>> dataOrgan = subjectMapper.getDictData(organ);
        System.out.println("dataOrgan="+dataOrgan);
        List<List<Map<String,Object>>> data=new ArrayList<>();
        data.add(0,dataPeriod);
        data.add(1,dataOrgan);
        System.out.println("data="+data);
        return new NdpResult(NdpResultConstant.SUCCESS, data);
    }
}