package cn.rwj.demo.activiti7.controller;

import cn.rwj.demo.activiti7.mapper.FormDataMapper;
import cn.rwj.demo.activiti7.secuexam.SecurityUtil;
import cn.rwj.demo.activiti7.util.AjaxResponse;
import cn.rwj.demo.activiti7.util.GlobalConfig;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.RepositoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author rwj
 * @Date 2022/10/7
 * @Description
 */
@RestController
@RequestMapping("/task")
public class TaskController {
    @Resource
    private TaskRuntime taskRuntime;

    @Resource
    private SecurityUtil securityUtil;

    @Resource
    private ProcessRuntime processRuntime;

    //获取我的代办任务
    @GetMapping(value = "getTasks")
    public AjaxResponse getTasks() {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("yuangong");
            }
            Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 100));
            List<HashMap<String, Object>> listMap = new ArrayList<HashMap<String, Object>>();
            for (Task tk : tasks.getContent()) {
                ProcessInstance processInstance = processRuntime.processInstance(tk.getProcessInstanceId());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", tk.getId());
                hashMap.put("name", tk.getName());
                hashMap.put("status", tk.getStatus());
                hashMap.put("createdDate", tk.getCreatedDate());
                if (tk.getAssignee() == null) {//执行人，null时前台显示未拾取
                    hashMap.put("assignee", "待拾取任务");
                } else {
                    hashMap.put("assignee", tk.getAssignee());//
                }

                hashMap.put("instanceName", processInstance.getName());
                listMap.add(hashMap);
            }

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), listMap);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(),
                    "获取我的代办任务失败", e.toString());
        }
    }

    //完成待办任务
    @GetMapping(value = "completeTask")
    public AjaxResponse completeTask(@RequestParam("taskId") String taskId) {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("yuangong");
            }

            Task task = taskRuntime.task(taskId);
            if (task.getAssignee() == null) {
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
            }
            taskRuntime.complete(
                    TaskPayloadBuilder
                            .complete()
                            .withTaskId(task.getId())
                            //.withVariable("num", "2")//执行环节设置变量
                            .build()
            );

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), null);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(),
                    "完成失败", e.toString());
        }
    }

}
