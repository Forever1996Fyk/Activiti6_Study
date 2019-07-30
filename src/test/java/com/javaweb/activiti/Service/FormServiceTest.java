package com.javaweb.activiti.Service;

import com.google.common.collect.Maps;
import org.activiti.engine.FormService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FormServiceTest
 * @Description 表单管理服务
 * @Author YuKai Fan
 * @Date 2019/7/30 20:06
 * @Version 1.0
 **/
public class FormServiceTest {
    private static  final Logger logger = LoggerFactory.getLogger(FormServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();


    @Test
    @Deployment(resources = {"my-process-form.bpmn20.xml"})
    public void testFormService() {
        FormService formService = activitiRule.getFormService();
        ProcessDefinition processDefinition = activitiRule.getRepositoryService().createProcessDefinitionQuery().singleResult();

        //获取startFormKey
        String startFormKey = formService.getStartFormKey(processDefinition.getId());
        logger.info("startFormKey = {}", startFormKey);

        //获取form属性
        StartFormData startFormData = formService.getStartFormData(processDefinition.getId());
        List<FormProperty> formProperties = startFormData.getFormProperties();
        for (FormProperty formProperty : formProperties) {
            logger.info("formProperty = {}", formProperty.toString());
        }

        //启动流程
        Map<String, String> properties = Maps.newHashMap();
        properties.put("message", "my test message");
        ProcessInstance processInstance = formService.submitStartFormData(processDefinition.getId(), properties);

        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();

        //通过taskId获取对应的userTask对应的属性配置
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        List<FormProperty> taskFormDataFormProperties = taskFormData.getFormProperties();
        for (FormProperty taskFormDataFormProperty : taskFormDataFormProperties) {
            logger.info("taskFormDataFormProperty = {}", taskFormDataFormProperty.toString());
        }

        HashMap<String, String> properties1 = Maps.<String, String>newHashMap();
        properties1.put("yesORno", "yes");
        formService.submitTaskFormData(task.getId(), properties1);

        Task task1 = activitiRule.getTaskService().createTaskQuery().taskId(task.getId()).singleResult();
        logger.info("task1 = {}", task1);
    }
}
