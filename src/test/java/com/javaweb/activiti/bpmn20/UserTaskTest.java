package com.javaweb.activiti.bpmn20;

import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @ClassName UserTaskTest
 * @Description 用户任务事件
 * @Author YuKai Fan
 * @Date 2019/8/1 20:49
 * @Version 1.0
 **/
public class UserTaskTest {

    private static final Logger logger = LoggerFactory.getLogger(UserTaskTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = {"bpmn/bpmn2.0/my-process-usertask.bpmn20.xml"})
    public void testUserTask() {
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");


        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().taskCandidateUser("user1").singleResult();
        logger.info("find by user1 task = {}", task);
        task = taskService.createTaskQuery().taskCandidateUser("user2").singleResult();
        logger.info("find by user2 task = {}", task);
        task = taskService.createTaskQuery().taskCandidateGroup("group1").singleResult();
        logger.info("find by group1 task = {}", task);

        //设置代理人(推荐:会进行校验，如果该用户已经被指定代理人，会抛出异常)
        taskService.claim(task.getId(), "user2");
        logger.info("claim task.id = {} by user2", task.getId());

        //设置指定处理人
        //taskService.setAssignee(task.getId(), "user2");

        task = taskService.createTaskQuery().taskCandidateOrAssigned("user1").singleResult();
        logger.info("find by user1 task = {}", task);

        task = taskService.createTaskQuery().taskCandidateOrAssigned("user2").singleResult();
        logger.info("find by user2 task = {}", task);
    }

    /**
     * @Description 通过监听器设置用户/用户组，变量
     *
     * @Author YuKai Fan
     * @Date 21:52 2019/8/1
     * @Param
     * @return
     **/
    @Test
    @Deployment(resources = {"bpmn/bpmn2.0/my-process-usertask2.bpmn20.xml"})
    public void testUserTask2() {
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");

        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().taskCandidateUser("user1").singleResult();
        logger.info("find by user1 task = {}", task);
        taskService.complete(task.getId());
    }
}
