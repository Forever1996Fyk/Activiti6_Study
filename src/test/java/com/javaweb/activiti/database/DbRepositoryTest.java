package com.javaweb.activiti.database;

import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ByteArrayEntity;
import org.activiti.engine.impl.persistence.entity.ByteArrayEntityImpl;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName DbGeTest
 * @Description 通用数据表结构创建
 * @Author YuKai Fan
 * @Date 2019/7/31 20:18
 * @Version 1.0
 **/
public class DbRepositoryTest {

    private Logger logger = LoggerFactory.getLogger(DbRepositoryTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

    /**
     * @Description 部署流程，就自动创建表结构
     *
     * @Author YuKai Fan
     * @Date 20:21 2019/7/31
     * @Param
     * @return
     **/
    @Test
    public void testDeploy() {
        activitiRule.getRepositoryService().createDeployment()
                .name("二次审批流程")
                .addClasspathResource("second_approve.bpmn20.xml")
                .deploy();
    }

    /**
     * @Description 挂起流程
     *
     * @Author YuKai Fan
     * @Date 20:43 2019/7/31
     * @Param
     * @return
     **/
    @Test
    public void testSuspend() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();

        repositoryService.suspendProcessDefinitionById("second_approve:2:7504");

        boolean suspended = repositoryService.isProcessDefinitionSuspended("second_approve:2:7504");
        logger.info("suspended = {}", suspended);
    }

}
