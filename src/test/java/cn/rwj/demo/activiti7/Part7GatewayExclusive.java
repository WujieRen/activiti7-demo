package cn.rwj.demo.activiti7;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author rwj
 * @Date 2022/10/5
 * @Description 排他网关要有执行条件，如果没有执行条件，会按照绘制连线顺序选择第一个执行。
 *                  排他网关用得比较多的场景是：同意走一条路，不同意周另一条路。
 */
@SpringBootTest
public class Part7GatewayExclusive {
    @Resource
    RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;



    @Test
    public void initProcessInstance() {
        //部署流程
        String filename = "bpmn/part7_gateway_exclusive.bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(filename)
                .name("排他网关")
                .key("p7")
                .deploy();
        System.out.println("deploymentId" + deployment.getId());
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("part7_gateway_exclusive");
        System.out.println("流程实例ID："+processInstance.getProcessDefinitionId());
        // 然后这里可以看到 排他网关的申请任务
        listTasks();
//        Task[id=cff254e7-44c3-11ed-88de-a036bc09649b, name=排他网关_yuangong请假]
    }

    @Test
    public void completeTask() {
        Task task = taskService.createTaskQuery().taskName("排他网关_yuangong请假").singleResult();
        //完成任务
        Map<String, Object> dayMap = new HashMap<>();
        dayMap.put("day", 5);
        taskService.complete(task.getId(), dayMap);
        listTasks();    //这里可以看到生成了 排他网关_lingdao审核 task
        //完成剩余任务
        task = taskService.createTaskQuery().taskName("排他网关_lingdao审核").singleResult();
        taskService.complete(task.getId());
        listTasks();
    }

    void listTasks() {
        taskService.createTaskQuery().list().forEach(System.out::println);
    }

}
