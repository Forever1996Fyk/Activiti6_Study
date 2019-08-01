package com.javaweb.activiti.Service;

import com.google.common.collect.Maps;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @ClassName RuntimeServiceTest
 * @Description 流程运行服务测试类
 * @Author YuKai Fan
 * @Date 2019/7/26 20:20
 * @Version 1.0
 **/
public class RuntimeServiceTest {
    private static  final Logger logger = LoggerFactory.getLogger(RepositoryServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    /**
     * @Description 根据key启动流程
     * @Author YuKai Fan
     * @Date 20:22 2019/7/26
     * @Param
     * @return
     **/
    @Test
    @org.activiti.engine.test.Deployment(resources = {"bpmn/my-process.bpmn20.xml"})
    public void testStartProcessByKey() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");

        //通过key启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process", variables);
        logger.info("processInstance = {}", processInstance);
    }

    /**
     * @Description 根据id启动流程
     *
     * @Author YuKai Fan
     * @Date 20:22 2019/7/26
     * @Param
     * @return
     **/
    @Test
    @org.activiti.engine.test.Deployment(resources = {"bpmn/my-process.bpmn20.xml"})
    public void testStartProcessById() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.
                createProcessDefinitionQuery().
                singleResult();

        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");

        //通过id启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), variables);
        logger.info("processInstance = {}", processInstance);
    }

    /**
     * @Description 根据ProcessInstanceBuilder启动流程
     *
     * @Author YuKai Fan
     * @Date 20:22 2019/7/26
     * @Param
     * @return
     **/
    @Test
    @org.activiti.engine.test.Deployment(resources = {"bpmn/my-process.bpmn20.xml"})
    public void testProcessInstanceBuilder() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");

        ProcessInstanceBuilder processInstanceBuilder = runtimeService.createProcessInstanceBuilder();
        ProcessInstance processInstance = processInstanceBuilder.businessKey("businessKey001")
                .processDefinitionKey("my-process")
                .variables(variables)
                .start();

        logger.info("processInstance = {}", processInstance);
    }

    /**
     * @Description 启动的流程中修改变量
     * @Author YuKai Fan
     * @Date 20:22 2019/7/26
     * @Param
     * @return
     **/
    @Test
    @org.activiti.engine.test.Deployment(resources = {"bpmn/my-process.bpmn20.xml"})
    public void testVariables() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");
        variables.put("key2", "value2");

        //通过key启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process", variables);
        logger.info("processInstance = {}", processInstance);

        //添加新的变量
        runtimeService.setVariable(processInstance.getId(), "key3", "value3");
        //对key2的变量进行修改
        runtimeService.setVariable(processInstance.getId(), "key2", "value2-1");
        //获取执行任务的变量
        Map<String, Object> variables1 = runtimeService.getVariables(processInstance.getId());
        logger.info("variables1 = {}", variables1);

    }

    /**
     * @Description 查询流程实例(执行流)
     * @Author YuKai Fan
     * @Date 20:22 2019/7/26
     * @Param
     * @return
     **/
    @Test
    @org.activiti.engine.test.Deployment(resources = {"bpmn/my-process.bpmn20.xml"})
    public void testProcessInstance() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");
        variables.put("key2", "value2");

        //通过key启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process", variables);
        logger.info("processInstance = {}", processInstance);

        //根据流程实例id获取执行流,但是在实际操作中一般是不用流程实例id获取，而用businessKey
        ProcessInstance processInstance1 = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();


    }

    /**
     * @Description 查询流程执行对象
     * @Author YuKai Fan
     * @Date 20:22 2019/7/26
     * @Param
     * @return
     **/
    @Test
    @org.activiti.engine.test.Deployment(resources = {"bpmn/my-process.bpmn20.xml"})
    public void testExecutionQuery() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", "value1");
        variables.put("key2", "value2");

        //通过key启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process", variables);
        logger.info("processInstance = {}", processInstance);

        //根据流程实例id获取执行对象流标
        List<Execution> executions = runtimeService.createExecutionQuery()
                .processInstanceId(processInstance.getId())
                .listPage(0, 100);

        for (Execution execution : executions) {
            logger.info("execution = {}", execution);
        }
    }

    /**
     * @Description 使用trigger触发ReceiveTask节点
     *
     * @Author YuKai Fan
     * @Date 21:01 2019/7/26
     * @Param 
     * @return 
     **/
    @Test
    @org.activiti.engine.test.Deployment(resources = {"bpmn/my-process-trigger.bpmn20.xml"})
    public void testTrigger() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");

        Execution execution = runtimeService.createExecutionQuery()
                .activityId("someTask")
                .singleResult();
        logger.info("execution = {}", execution);

        runtimeService.trigger(execution.getId());
        execution = runtimeService.createExecutionQuery()
                .activityId("someTask")
                .singleResult();
        logger.info("execution = {}", execution);
    }

    /**
     * @Description 触发信号捕获事件signalEventReceived
     *
     * @Author YuKai Fan
     * @Date 21:01 2019/7/26
     * @Param
     * @return
     **/
    @Test
    @org.activiti.engine.test.Deployment(resources = {"bpmn/my-process-signal-received.bpmn20.xml"})
    public void testSignalEventReceived() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");

        Execution execution = runtimeService.createExecutionQuery()
                .signalEventSubscriptionName("my-signal")
                .singleResult();
        logger.info("execution = {}", execution);

        runtimeService.signalEventReceived("my-signal");

        execution = runtimeService.createExecutionQuery()
                .signalEventSubscriptionName("my-signal")
                .singleResult();
        logger.info("execution = {}", execution);


    }

    /**
     * @Description 触发消息捕获事件messageEventReceived
     *
     * @Author YuKai Fan
     * @Date 21:01 2019/7/26
     * @Param
     * @return
     **/
    @Test
    @org.activiti.engine.test.Deployment(resources = {"bpmn/my-process-message-received.bpmn20.xml"})
    public void testMessageEventReceived() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");

        Execution execution = runtimeService.createExecutionQuery()
                .messageEventSubscriptionName("my-message")
                .singleResult();
        logger.info("execution = {}", execution);

        //这是消息触发与信号触发的不同点，消息触发必须要执行id
        runtimeService.messageEventReceived("my-message", execution.getId());

        execution = runtimeService.createExecutionQuery()
                .messageEventSubscriptionName("my-message")
                .singleResult();
        logger.info("execution = {}", execution);

    }

    /**
     * @Description 基于message启动流程
     *
     * @Author YuKai Fan
     * @Date 21:01 2019/7/26
     * @Param
     * @return
     **/
    @Test
    @org.activiti.engine.test.Deployment(resources = {"bpmn/my-process-message.bpmn20.xml"})
    public void testMessageStart() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        /*基于message方式启动流程，会在数据库事件订阅表中插入一条记录
          这种方式是根据消息信号的方式在事件订阅中找到订阅信号的id，再根据id找到流程定义的key，再根据key启动流程
         */
        ProcessInstance processInstance = runtimeService
//                .startProcessInstanceByKey("my-process");
                .startProcessInstanceByMessage("my-message");

        logger.info("processInstance = {}", processInstance);



    }
}
