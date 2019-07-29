package com.javaweb.activiti.Service;

import com.google.common.collect.Maps;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.*;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @ClassName TaskServiceTest
 * @Description 任务管理服务
 * @Author YuKai Fan
 * @Date 2019/7/29 21:07
 * @Version 1.0
 **/
public class TaskServiceTest {
    private static  final Logger logger = LoggerFactory.getLogger(TaskServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    /**
     * @Description 任务的创建，执行，参数的设置
     *
     * @Author YuKai Fan
     * @Date 21:27 2019/7/29
     * @Param
     * @return
     **/
    @Test
    @Deployment(resources = {"my-process-task.bpmn20.xml"})
    public void testTaskService() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("message", "my test message!!!");
        ProcessInstance processInstance = activitiRule.getRuntimeService()
                .startProcessInstanceByKey("my-process", variables);
        TaskService taskService = activitiRule.getTaskService();

        Task task = taskService.createTaskQuery().singleResult();
        logger.info("task = {}", ToStringBuilder.reflectionToString(task, ToStringStyle.DEFAULT_STYLE));
        logger.info("task.description = {}", task.getDescription());

        //设置普通变量
        taskService.setVariable(task.getId(), "key1", "vaule1");

        //设置本地变量
        taskService.setVariableLocal(task.getId(), "localKey1", "localValue1");

        //获取所以变量
        Map<String, Object> taskServiceVariables = taskService.getVariables(task.getId());

        //获取本地变量
        Map<String, Object> taskServiceVariablesLocal = taskService.getVariablesLocal(task.getId());

        //根据执行流id获取流程变量
        Map<String, Object> variables1 = activitiRule.getRuntimeService().getVariables(task.getExecutionId());

        logger.info("taskServiceVariables = {}", taskServiceVariables);
        logger.info("taskServiceVariablesLocal = {}", taskServiceVariablesLocal);
        logger.info("variables1 = {}", variables1);

        Map<String, Object> completeVar = Maps.newHashMap();
        completeVar.put("cKey1", "cValue1");
        //执行任务
        taskService.complete(task.getId(), completeVar);

        Task task1 = taskService.createTaskQuery().taskId(task.getId()).singleResult();
        logger.info("task1 = {}", task1);

    }

    /**
     * @Description 设置用户任务(UserTask)的权限信息(拥有者,候选人，办理人)
     *
     * @Author YuKai Fan
     * @Date 21:27 2019/7/29
     * @Param
     * @return
     **/
    @Test
    @Deployment(resources = {"my-process-task.bpmn20.xml"})
    public void testTaskServiceUser() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("message", "my test message !!!");
        activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", variables);
        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        logger.info("task = {}", task);
        logger.info("task.description = {}", task.getDescription());

        //设置任务拥有者
        taskService.setOwner(task.getId(), "user1");
        //设置代办人
//        taskService.setAssignee(task.getId(), "kaisa");不推荐这种直接指定的方式, 并没有对代办人进行校验，如果这个已经有其他权限的话，会出现问题

        List<Task> taskList = taskService.createTaskQuery().
                taskCandidateUser("kaisa").
                taskUnassigned().listPage(0, 100);

        for (Task task1 : taskList) {
            try {
                //使用这种方式，如果代办人已经有其他权限时，会抛出异常
                taskService.claim(task1.getId(), "kaisa");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        //用户与任务的关系
        List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(task.getId());
        for (IdentityLink identityLink : identityLinksForTask) {
            logger.info("identityLink = {}", identityLink);
        }

        List<Task> tasks = taskService.createTaskQuery().taskAssignee("kaisa").listPage(0, 100);
        for (Task task1 : tasks) {
            Map<String, Object> vars = Maps.newHashMap();
            vars.put("cKey1", "cValue1");
            taskService.complete(task1.getId(), vars);
        }

        tasks = taskService.createTaskQuery().taskAssignee("kaisa").listPage(0, 100);
        logger.info("是否存在 {}", CollectionUtils.isEmpty(tasks));
    }


    /**
     * @Description 针对用户任务添加任务附件
     *
     * @Author YuKai Fan
     * @Date 21:27 2019/7/29
     * @Param
     * @return
     **/
    @Test
    @Deployment(resources = {"my-process-task.bpmn20.xml"})
    public void testTaskServiceAttachment() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("message", "my test message !!!");
        activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", variables);
        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();

        taskService.createAttachment("url", task.getId(), task.getProcessInstanceId(), "name",
                "desc", "/url/test.png");

        List<Attachment> taskAttachments = taskService.getTaskAttachments(task.getId());
        for (Attachment taskAttachment : taskAttachments) {
            logger.info("taskAttachment = {}", ToStringBuilder.reflectionToString(taskAttachment, ToStringStyle.SIMPLE_STYLE));
        }
    }

    /**
     * @Description 针对用户任务添加任务评论,事件记录
     *
     * @Author YuKai Fan
     * @Date 21:27 2019/7/29
     * @Param
     * @return
     **/
    @Test
    @Deployment(resources = {"my-process-task.bpmn20.xml"})
    public void testTaskServiceCommit() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("message", "my test message !!!");
        activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", variables);
        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();

        taskService.setOwner(task.getId(), "user1");
        taskService.setAssignee(task.getId()," user2");

        taskService.addComment(task.getId(), task.getProcessInstanceId(),"record note 1");
        taskService.addComment(task.getId(), task.getProcessInstanceId(),"record note 2");

        List<Comment> taskComments = taskService.getTaskComments(task.getId());
        for (Comment taskComment : taskComments) {
            logger.info("taskComment = {}", ToStringBuilder.reflectionToString(taskComment, ToStringStyle.SIMPLE_STYLE));
        }

        List<Event> taskEvents = taskService.getTaskEvents(task.getId());
        for (Event taskEvent : taskEvents) {
            logger.info("taskEvent = {}", ToStringBuilder.reflectionToString(taskEvent, ToStringStyle.SIMPLE_STYLE));
        }
    }
}
