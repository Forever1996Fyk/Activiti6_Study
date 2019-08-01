package com.javaweb.activiti.bpmn20;

import com.google.common.collect.Maps;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ScriptTaskTest
 * @Description 脚本任务事件
 * @Author YuKai Fan
 * @Date 2019/8/1 20:49
 * @Version 1.0
 **/
public class ScriptTaskTest {

    private static final Logger logger = LoggerFactory.getLogger(ScriptTaskTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    /**
     * @Description groovy脚本执行
     *
     * @Author YuKai Fan
     * @Date 22:21 2019/8/1
     * @Param
     * @return
     **/
    @Test
    @Deployment(resources = {"bpmn/bpmn2.0/my-process-scripttask1.bpmn20.xml"})
    public void testScriptTask() {
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");

        HistoryService historyService = activitiRule.getHistoryService();
        List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .orderByVariableName().asc()
                .listPage(0, 100);

        for (HistoricVariableInstance historicVariableInstance : historicVariableInstances) {
            logger.info("variable = {}", historicVariableInstance);
        }

        logger.info("variables.size = {}", historicVariableInstances.size());
    }

    /**
     * @Description juel脚本执行
     *
     * @Author YuKai Fan
     * @Date 22:22 2019/8/1
     * @Param
     * @return
     **/
    @Test
    @Deployment(resources = {"bpmn/bpmn2.0/my-process-scripttask2.bpmn20.xml"})
    public void testScriptTask2() {

        //通过juel脚本返回值 int值相加返回的是String
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", 3);
        variables.put("key2", 5);
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", variables);

        HistoryService historyService = activitiRule.getHistoryService();
        List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .orderByVariableName().asc()
                .listPage(0, 100);

        for (HistoricVariableInstance historicVariableInstance : historicVariableInstances) {
            logger.info("variable = {}", historicVariableInstance);
        }

        logger.info("variables.size = {}", historicVariableInstances.size());
    }

    /**
     * @Description JavaScript脚本执行
     *
     * @Author YuKai Fan
     * @Date 22:22 2019/8/1
     * @Param
     * @return
     **/
    @Test
    @Deployment(resources = {"bpmn/bpmn2.0/my-process-scripttask3.bpmn20.xml"})
    public void testScriptTask3() {

        //通过JavaScript脚本返回值 int值相加返回的是double
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("key1", 3);
        variables.put("key2", 5);
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process", variables);

        HistoryService historyService = activitiRule.getHistoryService();
        List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .orderByVariableName().desc()
                .listPage(0, 100);

        for (HistoricVariableInstance historicVariableInstance : historicVariableInstances) {
            logger.info("variable = {}", historicVariableInstance);
        }

        logger.info("variables.size = {}", historicVariableInstances.size());
    }
    
    /**
     * @Description 测试脚本，避免脚本错误在Activiti中发生
     *
     * @Author YuKai Fan
     * @Date 22:28 2019/8/1
     * @Param 
     * @return 
     **/
    @Test
    public void testScriptEngine() throws ScriptException {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("juel");

        //所以的脚本结果返回为Object
        Object eval = scriptEngine.eval("${1 + 2}");

        logger.info("value = {}", eval);
    }

}
