package cn.rwj.demo.activiti7;

import cn.rwj.demo.activiti7.secuexam.SecurityUtil;
import org.activiti.api.model.shared.model.VariableInstance;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author rwj
 * @Date 2022/10/6
 * @Description
 */
@SpringBootTest
public class Part8ProcessRuntime {
    @Resource
    private ProcessRuntime processRuntime;

    @Resource
    private SecurityUtil securityUtil;

    //获取流程实例
    @Test
    public void getProcessInstance() {
        securityUtil.logInAs("yuangong");
        Page<ProcessInstance> processInstancePage = processRuntime
                .processInstances(Pageable.of(0, 100));
        System.out.println("流程实例数量：" + processInstancePage.getTotalItems());
        List<ProcessInstance> list = processInstancePage.getContent();
        for (ProcessInstance pi : list) {
            System.out.println("-----------------------");
            System.out.println("getId：" + pi.getId());
            System.out.println("getName：" + pi.getName());
            System.out.println("getStartDate：" + pi.getStartDate());
            System.out.println("getStatus：" + pi.getStatus());
            System.out.println("getProcessDefinitionId：" + pi.getProcessDefinitionId());
            System.out.println("getProcessDefinitionKey：" + pi.getProcessDefinitionKey());

        }


    }

    @Resource
    RepositoryService repositoryService;
    /**
     *  启动流程实例
     *      TODO: 7.1.0.M6这个无法启动，原因不明，像是个bug。
     *      报错信息：org.activiti.engine.ActivitiException: Query return 2 results instead of max 1
     *
     */
    @Test
    public void startProcessInstance() {
        String filename = "bpmn/part8_processruntime.bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(filename)
                .name("测试Activiti7新特性ProcessRuntime")
                .deploy();
        System.out.println(deployment.getName());

        securityUtil.logInAs("yuangong");
        ProcessInstance pi = processRuntime.start(
                ProcessPayloadBuilder
                        .start()
                        .withProcessDefinitionKey("part8_processruntime")
                        .withName("测试ProcessRuntime流程实例")
                        //.withVariable("","")
                        .withBusinessKey("businessKey_p8")
                        .build()
        );
    }

    //删除流程实例
    @Test
    public void delProcessInstance() {
        securityUtil.logInAs("yuangong");
        ProcessInstance processInstance = processRuntime.delete(ProcessPayloadBuilder
                .delete()
                .withProcessInstanceId("6fcecbdb-d3e0-11ea-a6c9-dcfb4875e032")
                .build()
        );
    }

    //挂起流程实例
    @Test
    public void suspendProcessInstance() {
        securityUtil.logInAs("yuangong");
        ProcessInstance processInstance = processRuntime.suspend(ProcessPayloadBuilder
                .suspend()
                .withProcessInstanceId("1f2314cb-cefa-11ea-84aa-dcfb4875e032")
                .build()
        );
    }

    //激活流程实例
    @Test
    public void resumeProcessInstance() {
        securityUtil.logInAs("yuangong");
        ProcessInstance processInstance = processRuntime.resume(ProcessPayloadBuilder
                .resume()
                .withProcessInstanceId("1f2314cb-cefa-11ea-84aa-dcfb4875e032")
                .build()
        );
    }

    //流程实例参数
    @Test
    public void getVariables() {
        securityUtil.logInAs("yuangong");
        List<VariableInstance> list = processRuntime.variables(ProcessPayloadBuilder
                .variables()
                .withProcessInstanceId("2b2d3990-d3ca-11ea-ae96-dcfb4875e032")
                .build()
        );
        for (VariableInstance vi : list) {
            System.out.println("-------------------");
            System.out.println("getName：" + vi.getName());
            System.out.println("getValue：" + vi.getValue());
            System.out.println("getTaskId：" + vi.getTaskId());
            System.out.println("getProcessInstanceId：" + vi.getProcessInstanceId());
        }
    }

}
