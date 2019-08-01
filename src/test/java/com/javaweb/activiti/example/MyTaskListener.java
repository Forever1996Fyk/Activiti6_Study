package com.javaweb.activiti.example;

import com.google.common.collect.Lists;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName MyTaskListener
 * @Description 任务监听
 * @Author YuKai Fan
 * @Date 2019/8/1 21:47
 * @Version 1.0
 **/
public class MyTaskListener implements TaskListener {
    private static final Logger logger = LoggerFactory.getLogger(MyTaskListener.class);

    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        if (StringUtils.equals("create", eventName)) {
            logger.info("config by listener");

            delegateTask.addCandidateUsers(Lists.newArrayList("user1", "user2"));
            delegateTask.addCandidateGroup("group1");
            delegateTask.setVariable("key1", "value1");

            delegateTask.setDueDate(DateTime.now().plusDays(3).toDate());
        } else if (StringUtils.equals("complete", eventName)) {
            logger.info("task complete");
        }
    }
}
