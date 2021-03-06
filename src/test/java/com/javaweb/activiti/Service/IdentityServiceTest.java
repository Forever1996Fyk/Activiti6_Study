package com.javaweb.activiti.Service;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @ClassName IdentityServiceTest
 * @Description 身份认证服务
 * @Author YuKai Fan
 * @Date 2019/7/29 22:00
 * @Version 1.0
 **/
public class IdentityServiceTest {
    private static  final Logger logger = LoggerFactory.getLogger(IdentityServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    /**
     * @Description
     *
     * @Author YuKai Fan
     * @Date 22:00 2019/7/29
     * @Param
     * @return
     **/
    @Test
    public void testIdentity() {
        IdentityService identityService = activitiRule.getIdentityService();
        User user1 = identityService.newUser("user1");
        user1.setEmail("user1@126.com");
        User user2 = identityService.newUser("user2");
        user2.setEmail("user2@126.com");

        identityService.saveUser(user1);
        identityService.saveUser(user2);

        Group group1 = identityService.newGroup("group1");
        identityService.saveGroup(group1);
        Group group2 = identityService.newGroup("group2");
        identityService.saveGroup(group2);

        identityService.createMembership("user1", "group1");
        identityService.createMembership("user2", "group1");
        identityService.createMembership("user1", "group2");

        User user11 = identityService.createUserQuery().userId("user1").singleResult();
        user11.setLastName("kai");
        identityService.saveUser(user11);
        List<User> userList = identityService.createUserQuery().memberOfGroup("group1").listPage(0, 100);
        for (User user : userList) {
            logger.info("user = {}", ToStringBuilder.reflectionToString(user, ToStringStyle.SIMPLE_STYLE));
        }

        List<Group> groupList = identityService.createGroupQuery().groupMember("user1").listPage(0, 100);
        for (Group group : groupList) {
            logger.info("group = {}", ToStringBuilder.reflectionToString(group, ToStringStyle.SIMPLE_STYLE));
        }
    }
}
