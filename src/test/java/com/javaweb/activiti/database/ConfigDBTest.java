package com.javaweb.activiti.database;

import com.google.common.collect.Lists;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
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

    /**
     * @Description 创建表结构
     *
     * @Author YuKai Fan
     * @Date 20:16 2019/7/31
     * @Param
     * @return
     **/
    @Test
    public void testDbConfig() {
        ProcessEngine processEngine = ProcessEngineConfiguration.
                createProcessEngineConfigurationFromResource("activiti-mysql.cfg.xml")
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

    /**
     * @Description 清理表结构
     *
     * @Author YuKai Fan
     * @Date 20:16 2019/7/31
     * @Param
     * @return
     **/
    @Test
    public void dropTest() {
        ProcessEngine processEngine = ProcessEngineConfiguration.
                createProcessEngineConfigurationFromResource("activiti-mysql.cfg.xml")
                .buildProcessEngine();

        ManagementService managementService = processEngine.getManagementService();

        Object o = managementService.executeCommand(new Command<Object>() {
            public Object execute(CommandContext commandContext) {
                commandContext.getDbSqlSession().dbSchemaDrop();
                logger.info("删除表结构");
                return null;
            }
        });
    }
}
