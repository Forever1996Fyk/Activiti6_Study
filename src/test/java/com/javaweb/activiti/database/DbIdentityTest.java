package com.javaweb.activiti.database;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName DbGeTest
 * @Description 身份数据表结构
 * @Author YuKai Fan
 * @Date 2019/7/31 20:18
 * @Version 1.0
 **/
public class DbIdentityTest {

    private Logger logger = LoggerFactory.getLogger(DbIdentityTest.class);

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
    public void testIdentity() {
        IdentityService identityService = activitiRule.getIdentityService();
        User user1 = identityService.newUser("user1");
        user1.setFirstName("firstName");
        user1.setLastName("lastName");
        user1.setEmail("user1@126.com");
        user1.setPassword("pwd");
        identityService.saveUser(user1);

        User user2 = identityService.newUser("user2");
        identityService.saveUser(user2);

        Group group1 = identityService.newGroup("group1");
        identityService.saveGroup(group1);

        identityService.createMembership(user1.getId(), group1.getId());
        identityService.createMembership(user2.getId(), group1.getId());

        identityService.setUserInfo(user1.getId(), "age", "18");
        identityService.setUserInfo(user1.getId(), "address", "北京");
    }

}
