package cn.rwj.demo.activiti7;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author rwj
 * @Date 2022/10/5
 * @Description 任务环节要操作的表：
 *                  act_ru_task    运行时任务节点
 *                  act_ru_identitylink     运行时参与人员与节点关系表
 *                  act_ru_variable（如果加了参数，该表会有记录）     运行时流程变量
 *                  act_hi_actiinst
 *                  act_hi_identitylink
 *                  act_hi_taskinst
 *                  流程的所有执行完成后，该流程在 act_ru_task 和 act_ru_identitylink 和 act_ru_variable 中的数据就都没了
 */
@SpringBootTest
public class Part4Task {
    @Resource
    private TaskService taskService;

    //任务查询
    @Test
    public void getTasks(){
        List<Task> list = taskService.createTaskQuery().list();
        for(Task tk : list){
            System.out.println("Id："+tk.getId());
            System.out.println("Name："+tk.getName());
            System.out.println("Assignee："+tk.getAssignee());
        }
    }

    //查询我的待办任务
    @Test
    public void getTasksByAssignee(){
        List<Task> list = taskService.createTaskQuery()
//                .taskAssignee("zhuguan")
                .taskAssignee("yuangong")
                .list();
        for(Task tk : list){
            System.out.println("Id："+tk.getId());
            System.out.println("Name："+tk.getName());
            System.out.println("Assignee："+tk.getAssignee());
        }

    }

    //执行任务
    @Test
    public void completeTask(){
        taskService.complete("a009bf56-44a1-11ed-afb7-a036bc09649b");
        System.out.println("完成任务");

    }

    /**
     * 候选人任务测试      拾取任务      part4_task_claim
     *      在主管拾取任务之前，getTasksByAssignee 通过 Asignee= 是查询不到 task 的
     *      拾取后就可以了
     */
    @Test
    public void claimTask (){
        Task task = taskService.createTaskQuery().taskId("da0d112e-44a7-11ed-820c-a036bc09649b").singleResult();
        taskService.claim(task.getId(), "zhuguan");
    }

    /**
     * 归还与交办任务
     *      主管拾取了以后发现这个task应该交给yuangong办理，于是归还该任务，或者交办该任务给yuangong
     */
    @Test
    public void setTaskAssignee(){
        Task task = taskService.createTaskQuery().taskId("da0d112e-44a7-11ed-820c-a036bc09649b").singleResult();
//        taskService.setAssignee(task.getId(),null);//归还候选任务，归还后再用 getTasksByAssignee 通过 Asignee=zhuguan 是查询不到 task 的
        taskService.setAssignee(task.getId(),"yuangong");//交办给yuangong，然后通 getTasksByAssignee 通过 Asignee=yuangong 即可查询到该 task
    }
}
