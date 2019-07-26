package com.javaweb.activiti.helloworld;

import com.google.common.collect.Maps;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName DemoMain
 * @Description 启动类
 * @Author YuKai Fan
 * @Date 2019/7/23 21:20
 * @Version 1.0
 **/
public class DemoMain {
    private static final Logger logger = LoggerFactory.getLogger(DemoMain.class);

    /**
     * @Description 
     *
     * @Author YuKai Fan
     * @Date 21:35 2019/7/23
     * @Param 
     * @return 
     **/
    public static void main(String[] args) throws ParseException {
        logger.info("启动程序");
        //1.创建流程引擎
        ProcessEngine processEngine = getProcessEngine();

        //2.部署流程定义文件

        //对流程定义库的操作
        ProcessDefinition processDefinition = getProcessDefinition(processEngine);

        //3.启动运行流程
        ProcessInstance processInstance = getProcessInstance(processEngine, processDefinition);

        //4.处理流程任务
        processTask(processEngine, processInstance);
        logger.info("结束程序");
    }

    private static void processTask(ProcessEngine processEngine, ProcessInstance processInstance) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        while (processInstance != null && !processInstance.isEnded()) {
            //处理流程任务对象taskService
            TaskService taskService = processEngine.getTaskService();
            List<Task> list = taskService.createTaskQuery().list();
            for (Task task : list) {

                logger.info("待处理任务 [{}]", task.getName());
                Map<String, Object> variables = getFormMap(processEngine, scanner, task);

                taskService.complete(task.getId(), variables);
                processInstance = processEngine.getRuntimeService()
                        .createProcessInstanceQuery()
                        .processInstanceId(processInstance.getId())
                        .singleResult();
            }
            logger.info("待处理任务数量 [{}]", list.size());
        }
        scanner.close();
    }

    private static Map<String, Object> getFormMap(ProcessEngine processEngine, Scanner scanner, Task task) throws ParseException {
        FormService formService = processEngine.getFormService();
        //根据任务id taskId获取form表单中的数据
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        List<FormProperty> formProperties = taskFormData.getFormProperties();

        //将输入的属性存起来
        HashMap<String, Object> variables = Maps.newHashMap();
        for (FormProperty property : formProperties) {
            String line = null;
            if (StringFormType.class.isInstance(property.getType())) {
                logger.info("请输入 {} ?", property.getName());
                line = scanner.nextLine();
                variables.put(property.getId(), line);
            } else if (DateFormType.class.isInstance(property.getType())) {
                logger.info("请输入 {} 格式 (yyyy-MM-dd)?", property.getName());
                line = scanner.nextLine();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(line);
                variables.put(property.getId(), date);
            } else {
                logger.info("类型暂不支持");
            }
            logger.info("您输入的内容是 [{}]", line);

        }
        return variables;
    }

    private static ProcessInstance getProcessInstance(ProcessEngine processEngine, ProcessDefinition processDefinition) {
        //运行时对象runtimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //根据流程定义id, 启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        logger.info("启动流程 [{}]", processInstance.getProcessDefinitionKey());
        return  processInstance;
    }

    private static ProcessDefinition getProcessDefinition(ProcessEngine processEngine) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder deployment = repositoryService.createDeployment();
        deployment.addClasspathResource("second_approve.bpmn20.xml");
        Deployment deploy = deployment.deploy();
        String deployId = deploy.getId();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployId)
                .singleResult();

        logger.info("流程定义文件 [{}], 流程ID [{}]", processDefinition.getName(), processDefinition.getId());
        return processDefinition;
    }

    private static ProcessEngine getProcessEngine() {
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
        ProcessEngine processEngine = cfg.buildProcessEngine();
        String name = processEngine.getName();
        String version = ProcessEngine.VERSION;

        logger.info("流程引擎名称 [{}], 版本 [{}]", name, version);
        return processEngine;
    }
}
