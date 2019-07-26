package com.javaweb.activiti.Service;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @ClassName RespositoryServiceTest
 * @Description TODO
 * @Author YuKai Fan
 * @Date 2019/7/25 21:43
 * @Version 1.0
 **/
public class RepositoryServiceTest {
    private static  final Logger logger = LoggerFactory.getLogger(RepositoryServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    /**
     * @Description 流程部署
     *
     * @Author YuKai Fan
     * @Date 22:13 2019/7/25
     * @Param
     * @return
     **/
    @Test
    public void testRepository() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();

        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.name("测试部署资源1")
                .addClasspathResource("my-process.bpmn20.xml")
                .addClasspathResource("second_approve.bpmn20.xml");

        Deployment deploy = deploymentBuilder.deploy();
        logger.info("deploy = {}", deploy);

        DeploymentBuilder deploymentBuilder1 = repositoryService.createDeployment();
        deploymentBuilder1.name("测试部署资源2")
                .addClasspathResource("my-process.bpmn20.xml")
                .addClasspathResource("second_approve.bpmn20.xml");
        deploymentBuilder1.deploy();


        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
        //查询流程部署信息，与上面的deploy应该是一致的
        List<Deployment> deployments = deploymentQuery.orderByDeploymenTime().asc()
                .listPage(0, 100);
        for (Deployment deployment : deployments) {
            logger.info("deployment = {}", deployment);
        }
        logger.info("deployments.size = {}", deployments.size());
        //查询流程定义
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
//                .deploymentId(deployment.getId())
                .orderByProcessDefinitionKey().asc()
                .listPage(0, 100);
        for (ProcessDefinition processDefinition : processDefinitions) {
            logger.info("processDefinition = {}, version = {}, key = {}, id = {}", processDefinition,
                    processDefinition.getVersion(),
                    processDefinition.getKey(),
                    processDefinition.getId());
        }
    }

    /**
     * @Description 暂停/挂起 流程部署
     *
     * @Author YuKai Fan
     * @Date  2019/7/25
     * @Param
     * @return
     **/
    @Test
    @org.activiti.engine.test.Deployment(resources = {"my-process.bpmn20.xml"})
    public void testSuspend(){
        RepositoryService repositoryService = activitiRule.getRepositoryService();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();
        logger.info("processDefinition.id = {}", processDefinition.getId());

        repositoryService.suspendProcessDefinitionById(processDefinition.getId());
        try {
            logger.info("开始启动");
            activitiRule.getRuntimeService().startProcessInstanceById(processDefinition.getId());
            logger.info("启动成功");
        } catch (Exception e) {
            logger.info("启动失败");
            logger.info(e.getMessage(), e);
        }

        repositoryService.activateProcessDefinitionById(processDefinition.getId());
        logger.info("开始启动");
    }

    /**
     * @Description 指定用户或用户组部署
     *
     * @Author YuKai Fan
     * @Date 22:14 2019/7/25
     * @Param 
     * @return 
     **/
    @Test
    @org.activiti.engine.test.Deployment(resources = {"my-process.bpmn20.xml"})
    public void testCandidateStarter(){
        RepositoryService repositoryService = activitiRule.getRepositoryService();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();

        logger.info("processDefinition.id = {}", processDefinition.getId());

        repositoryService.addCandidateStarterUser(processDefinition.getId(), "user");
        repositoryService.addCandidateStarterGroup(processDefinition.getId(), "groupM");

        //根据流程定义获取流程的关系
        List<IdentityLink> identityLinks = repositoryService.getIdentityLinksForProcessDefinition(processDefinition.getId());
        for (IdentityLink identityLink : identityLinks) {
            logger.info("identityLink = {}", identityLink);
        }

        //删除指定用户，用户组
        repositoryService.deleteCandidateStarterGroup(processDefinition.getId(), "groupM");
        repositoryService.deleteCandidateStarterUser(processDefinition.getId(), "user");
    }
}
