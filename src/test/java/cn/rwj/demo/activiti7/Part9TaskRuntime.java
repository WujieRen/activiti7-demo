package cn.rwj.demo.activiti7;

import cn.rwj.demo.activiti7.secuexam.SecurityUtil;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
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
public class Part9TaskRuntime {

    @Resource
    private SecurityUtil securityUtil;

    @Resource
    private TaskRuntime taskRuntime;

    //获取当前登录用户任务
    @Test
    public void getTasks() {
        securityUtil.logInAs("yuangong");
        /**
         * 该方法会查出来 Asignee=yuangong 和 candidate包含yuangong 的所有任务
         *  如果只是 candidate包含yuangong 的话
         *      1. Asignee=null，说明该任务带拾取
         *      2. Asignee已经有值
         */
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0,100));
        List<Task> list=tasks.getContent();
        for(Task tk : list){
            System.out.println("-------------------");
            System.out.println("getId："+ tk.getId());
            System.out.println("getName："+ tk.getName());
            System.out.println("getStatus："+ tk.getStatus());
            System.out.println("getCreatedDate："+ tk.getCreatedDate());
            if(tk.getAssignee() == null){
                //候选人为当前登录用户，null的时候需要前端拾取
                System.out.println("Assignee：待拾取任务");
            }else{
                System.out.println("Assignee："+ tk.getAssignee());
            }
        }
    }

    //完成任务
    @Test
    public void completeTask() {
        securityUtil.logInAs("yuangong");
        Task task = taskRuntime.task("bbc2ad6c-453e-11ed-a679-a036bc09649b");
        if(task.getAssignee() == null){
            taskRuntime.claim(TaskPayloadBuilder.claim()
                    .withTaskId(task.getId())
                    .build());
        }
        taskRuntime.complete(TaskPayloadBuilder
                .complete()
                .withTaskId(task.getId())
                .build());
        System.out.println("任务执行完成");
    }}
