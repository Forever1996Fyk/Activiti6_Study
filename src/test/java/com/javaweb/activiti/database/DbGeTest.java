package com.javaweb.activiti.database;

import org.activiti.engine.ManagementService;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ByteArrayEntity;
import org.activiti.engine.impl.persistence.entity.ByteArrayEntityImpl;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @ClassName DbGeTest
 * @Description 通用数据表结构创建
 * @Author YuKai Fan
 * @Date 2019/7/31 20:18
 * @Version 1.0
 **/
public class DbGeTest {

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
    public void testByteArray() {
        activitiRule.getRepositoryService().createDeployment()
                .name("测试部署")
                .addClasspathResource("bpmn/my-process.bpmn20.xml")
                .deploy();
    }

    /**
     * @Description 向表结构中插入一条数据
     *
     * @Author YuKai Fan
     * @Date 20:22 2019/7/31
     * @Param
     * @return
     **/
    @Test
    public void testByteArrayInsert() {
        ManagementService managementService = activitiRule.getManagementService();

        Object o = managementService.executeCommand(new Command<Object>() {
            public Object execute(CommandContext commandContext) {
                ByteArrayEntity entity = new ByteArrayEntityImpl();
                entity.setName("test");
                entity.setBytes("test message".getBytes());
                commandContext.getByteArrayEntityManager().insert(entity);
                return null;
            }
        });
    }
}
