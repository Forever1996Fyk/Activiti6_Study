package com.javaweb.activiti.database;

import com.google.common.collect.Maps;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @ClassName DbGeTest
 * @Description 运行时流程数据表结构
 * @Author YuKai Fan
 * @Date 2019/7/31 20:18
 * @Version 1.0
 **/
public class DbRuntimeTest {

    private Logger logger = LoggerFactory.getLogger(DbRuntimeTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-mysql.cfg.xml");

    /**
     * @Description 运行时流程
     *
     * @Author YuKai Fan
     * @Date 20:21 2019/7/31
     * @Param
     * @return
     **/
    @Test
    public void testRuntime() {
        activitiRule.getRepositoryService().createDeployment()
                .name("二次审批流程")
                .addClasspathResource("second_approve.bpmn20.xml")
                .deploy();

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");
        ProcessInstance second_approve = activitiRule.getRuntimeService()
                .startProcessInstanceByKey("second_approve", variables);
    }

    /**
     * @Description 设置参与者
     *
     * @Author YuKai Fan
     * @Date 21:33 2019/7/31
     * @Param
     * @return
     **/
    @Test
    public void testSetOwner() {
        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().processDefinitionKey("second_approve").singleResult();
        taskService.setOwner(task.getId(), "user1");
    }

    /**
     * @Description 基于流程定义的订阅事件
     *
     * @Author YuKai Fan
     * @Date 21:36 2019/7/31
     * @Param
     * @return
     **/
    @Test
    public void testMessage() {
        activitiRule.getRepositoryService().
                createDeployment().
                addClasspathResource("bpmn/my-process-message.bpmn20.xml")
                .deploy();
    }

    /**
     * @Description 基于流程实例的订阅事件
     *
     * @Author YuKai Fan
     * @Date 21:36 2019/7/31
     * @Param
     * @return
     **/
    @Test
    public void testMessageReceived() {
        activitiRule.getRepositoryService().createDeployment()
                .addClasspathResource("bpmn/my-process-message-received.bpmn20.xml")
                .deploy();

        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");

    }

    /**
     * @Description 定时任务事件
     *
     * @Author YuKai Fan
     * @Date 21:36 2019/7/31
     * @Param
     * @return
     **/
    @Test
    public void testJob() throws InterruptedException {
        activitiRule.getRepositoryService().createDeployment()
                .addClasspathResource("bpmn/my-process-job.bpmn20.xml")
                .deploy();

        Thread.sleep(1000 * 30L);

    }

}
