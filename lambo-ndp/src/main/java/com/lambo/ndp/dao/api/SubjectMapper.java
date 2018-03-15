package com.lambo.ndp.dao.api;

import com.lambo.ndp.model.Subject;
import com.lambo.ndp.model.SubjectExample;
import java.util.List;
import java.util.Map;

public interface SubjectMapper {
    int deleteByPrimaryKey(Integer subjectId);

    int insert(Subject record);

    int insertSelective(Subject record);

    List<Subject> selectByExample(SubjectExample example);

    Subject selectByPrimaryKey(Integer subjectId);

    int updateByPrimaryKeySelective(Subject record);

    int updateByPrimaryKey(Subject record);
    List<Map<String,Object>> querySubject(Map<String, Object> param);
}