package com.javaweb.activiti.mapper;

import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @ClassName MyCustomerMapper
 * @Description 自定义sql
 * @Author YuKai Fan
 * @Date 2019/7/30 21:25
 * @Version 1.0
 **/
public interface MyCustomerMapper {

    @Select("SELECT * FROM ACT_RU_TASK")
    List<Map<String, Object>> findAll();
}
