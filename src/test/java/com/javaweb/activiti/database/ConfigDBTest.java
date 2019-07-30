package com.javaweb.activiti.database;

import com.google.common.collect.Lists;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * @ClassName ConfigDBTest
 * @Description 数据库配置
 * @Author YuKai Fan
 * @Date 2019/7/24 21:15
 * @Version 1.0
 **/
public class ConfigDBTest {
    private static final Logger logger = LoggerFactory.getLogger(ConfigDBTest.class);

    @Test
    public void testDbConfig() {
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault()
                .buildProcessEngine();

        ManagementService managementService = processEngine.getManagementService();

        //获取表数据的数量
        Map<String, Long> tableCount = managementService.getTableCount();

        ArrayList<String> tableNames = Lists.newArrayList(tableCount.keySet());
        Collections.sort(tableNames);
        for (String tableName : tableNames) {
            logger.info("tableName = {}", tableName);
        }
        logger.info("tableCount.size = {}", tableCount.size());
    }
}
