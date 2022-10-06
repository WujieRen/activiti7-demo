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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author rwj
 * @Date 2022/10/5
 * @Description 包含网关需要有触发条件，且有始有终。
 *                  且只要符合条件的分支都可以被触发。
 */
@SpringBootTest
public class Part7GatewayInclusive {
    @Resource
    RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Test
    public void initProcessInstance() {
        //部署流程
        String filename = "bpmn/part7_gateway_inclusive.bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(filename)
                .name("包含网关")
                .key("p7")
                .deploy();
        System.out.println("deploymentId" + deployment.getId());
        //启动实例
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("part7_gateway_inclusive");
        System.out.println("流程实例ID："+processInstance.getProcessDefinitionId());
        // 然后这里可以看到 yuangong 发起的包含网关的申请任务——包含网关_yuangong发起申请
        listTasks();
//        Task[id=cff254e7-44c3-11ed-88de-a036bc09649b, name=排他网关_yuangong请假]
    }

    @Test
    public void completeTask() {
        Task task = taskService.createTaskQuery().taskName("包含网关_yuangong发起申请").singleResult();
        //完成任务
        Map<String, Object> dayMap = new HashMap<>();
        dayMap.put("day", 1);       //因为 day < 3 && day < 6 ，所以应该可以看到task生成了 zhuguan 和 lingdao
        taskService.complete(task.getId(), dayMap);
        listTasks();    //
        //完成剩余任务
        List<Task> taskList = taskService.createTaskQuery().taskNameIn(Arrays.asList("包含网关_zhuguan审核", "包含网关_lingdao审核")).list();
        taskList.forEach(t -> taskService.complete(t.getId()));
        listTasks();
    }

    void listTasks() {
        taskService.createTaskQuery().list().forEach(System.out::println);
    }

}
