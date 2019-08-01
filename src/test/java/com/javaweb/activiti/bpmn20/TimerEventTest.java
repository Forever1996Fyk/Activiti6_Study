package com.javaweb.activiti.bpmn20;

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
 * @ClassName TimerEventTest
 * @Description 定时边界事件
 * @Author YuKai Fan
 * @Date 2019/8/1 20:49
 * @Version 1.0
 **/
public class TimerEventTest {

    private static final Logger logger = LoggerFactory.getLogger(TimerEventTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = {"bpmn/bpmn2.0/my-process-timer-boundary.bpmn20.xml"})
    public void testTimerBoundary() throws InterruptedException {
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");

        List<Task> tasks = activitiRule.getTaskService().createTaskQuery()
                    .listPage(0, 100);
        for (Task task : tasks) {
            logger.info("task.name = {}", task.getName());
        }
        logger.info("tasks.size = {}", tasks.size());

        Thread.sleep(1000 * 15);

        tasks = activitiRule.getTaskService().createTaskQuery()
                .listPage(0, 100);
        for (Task task : tasks) {
            logger.info("task.name = {}", task.getName());
        }
        logger.info("tasks.size = {}", tasks.size());

    }
}
