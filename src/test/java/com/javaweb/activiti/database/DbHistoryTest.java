package com.javaweb.activiti.database;

import com.google.common.collect.Maps;
import org.activiti.engine.FormService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ByteArrayEntity;
import org.activiti.engine.impl.persistence.entity.ByteArrayEntityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

/**
 * @ClassName DbGeTest
 * @Description 历史数据表结构
 * @Author YuKai Fan
 * @Date 2019/7/31 20:18
 * @Version 1.0
 **/
public class DbHistoryTest {

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
                .addClasspathResource("my-process.bpmn20.xml")
                .deploy();

        RuntimeService runtimeService = activitiRule.getRuntimeService();


        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key0", "value0");
        variables.put("key1", "value1");
        variables.put("key2", "value2");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process", variables);

        runtimeService.setVariable(processInstance.getId(), "key1", "value1_1");

        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        taskService.setOwner(task.getId(), "user1");

        taskService.createAttachment("url", task.getId(),
                processInstance.getId(), "name", "desc", "/url/test.png");

        taskService.addComment(task.getId(), task.getProcessInstanceId(), "record note1");
        taskService.addComment(task.getId(), task.getProcessInstanceId(), "record note2");

        FormService formService = activitiRule.getFormService();
        Map<String, String> properties = Maps.newHashMap();
        properties.put("key2", "value2_1");
        properties.put("key3", "value3");
        formService.submitTaskFormData(task.getId(), properties);
    }

}
