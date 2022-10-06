package cn.rwj.demo.activiti7;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @Author rwj
 * @Date 2022/10/5
 * @Description 并行网关，有始有终。
 */
@SpringBootTest
public class Part7GatewayParallel {

    @Resource
    RepositoryService   repositoryService;
    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;



    @Test
    public void initProcessInstance() {
        //部署流程
        String filename = "bpmn/part7_gateway_parallel.bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(filename)
                .name("测试并行网关") //效果应该是yuangong提交申请task后，主管和领导都能看到task
                .key("p7")
                .deploy();
        System.out.println("deploymentId" + deployment.getId());
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("part7_gateway_parallel");
        System.out.println("流程实例ID："+processInstance.getProcessDefinitionId());
        // 然后这里可以看到 并行网关的申请任务
    }

    @Test
    public void completeTask() {
//        taskService.complete("51e4fdc2-44be-11ed-99de-a036bc09649b");   // 员工任务申请完成 name: 并行网关_yuangong请假
        //然后就可以看到task中多出来了并行网关中间的两个节点任务
        taskService.createTaskQuery().list().forEach(System.out::println);
        //结果如下
//        Task[id=ddb31724-44be-11ed-bfdb-a036bc09649b, name=并行网关_zhuguan审核]
//        Task[id=ddb31726-44be-11ed-bfdb-a036bc09649b, name=并行网关_lingdao审核]
        //完成剩余任务
        taskService.complete("ddb31724-44be-11ed-bfdb-a036bc09649b");
        taskService.complete("ddb31726-44be-11ed-bfdb-a036bc09649b");
    }
}
