package com.lambo.ndp.dao.api;

import java.util.List;
import java.util.Map;

public interface OverviewMapper {
    Map<String,Object> getCategoryOverview(Map<String, Object> param);
}