package com.javaweb.activiti.Service;

import com.javaweb.activiti.mapper.MyCustomerMapper;
import org.activiti.engine.ManagementService;
import org.activiti.engine.impl.cmd.AbstractCustomSqlExecution;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.management.TablePage;
import org.activiti.engine.runtime.DeadLetterJobQuery;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.JobQuery;
import org.activiti.engine.runtime.SuspendedJobQuery;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ManageServiceTest
 * @Description 管理服务
 * @Author YuKai Fan
 * @Date 2019/7/30 21:14
 * @Version 1.0
 **/
public class ManageServiceTest {
    private static  final Logger logger = LoggerFactory.getLogger(HistoryServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti_job.cfg.xml");

    @Test
    @Deployment(resources = {"my-process-job.bpmn20.xml"})
    public void testJobQuery() {
        ManagementService managementService = activitiRule.getManagementService();

        List<Job> jobs = managementService.createTimerJobQuery().listPage(0, 100);
        for (Job job : jobs) {
            logger.info("job = {}", job);
        }

        JobQuery jobQuery = managementService.createJobQuery();

        SuspendedJobQuery suspendedJobQuery = managementService.createSuspendedJobQuery();

        //没有执行的job
        DeadLetterJobQuery deadLetterJobQuery = managementService.createDeadLetterJobQuery();

    }

    /**
     * @Description 通用表查询(TablePageQuery)
     *
     * @Author YuKai Fan
     * @Date 21:23 2019/7/30
     * @Param
     * @return
     **/
    @Test
    @Deployment(resources = {"my-process-job.bpmn20.xml"})
    public void testTablePageQuery() {
        ManagementService managementService = activitiRule.getManagementService();
        TablePage tablePage = managementService.createTablePageQuery()
                .tableName(managementService.getTableName(ProcessDefinitionEntity.class))//参数表示需要查询的实体对象
                .listPage(0, 100);

        List<Map<String, Object>> rows = tablePage.getRows();
        for (Map<String, Object> row : rows) {
            logger.info("row = {}", row);
        }
    }

    /**
     * @Description  执行自定义的Sql查询(executeCustomSql)
     *
     * @Author YuKai Fan
     * @Date 21:24 2019/7/30
     * @Param 
     * @return 
     **/
    @Test
    @Deployment(resources = {"my-process.bpmn20.xml"})
    public void testExecuteCustomSql() {
        activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");

        ManagementService managementService = activitiRule.getManagementService();
        List<Map<String, Object>> mapList = managementService.executeCustomSql(new AbstractCustomSqlExecution<MyCustomerMapper, List<Map<String, Object>>>(MyCustomerMapper.class) {
            public List<Map<String, Object>> execute(MyCustomerMapper myCustomerMapper) {
                return myCustomerMapper.findAll();
            }
        });

        for (Map<String, Object> map : mapList) {
            logger.info("map = {}", map);
        }

    }

    /**
     * @Description 执行流程引擎命令(Command)
     *
     * @Author YuKai Fan
     * @Date 21:31 2019/7/30
     * @Param 
     * @return 
     **/
    @Test
    @Deployment(resources = {"my-process.bpmn20.xml"})
    public void testCommand() {
        ManagementService managementService = activitiRule.getManagementService();

        //通过执行command的方式获取流程定义实体
        ProcessDefinitionEntity processDefinitionEntity = managementService.executeCommand(new Command<ProcessDefinitionEntity>() {
            public ProcessDefinitionEntity execute(CommandContext commandContext) {
                ProcessDefinitionEntity processDefinitionEntity = commandContext.getProcessDefinitionEntityManager()
                        .findLatestProcessDefinitionByKey("my-process");
                return processDefinitionEntity;
            }
        });

        logger.info("processDefinitionEntity = {}", processDefinitionEntity);
    }
}
