package cn.rwj.demo.activiti7.controller;

import cn.rwj.demo.activiti7.model.User;
import cn.rwj.demo.activiti7.secuexam.SecurityUtil;
import cn.rwj.demo.activiti7.util.AjaxResponse;
import cn.rwj.demo.activiti7.util.GlobalConfig;
import org.activiti.api.model.shared.model.VariableInstance;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.GetProcessInstancesPayloadBuilder;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.model.payloads.GetProcessInstancesPayload;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author rwj
 * @Date 2022/10/6
 * @Description 流程实例控制器，旧API
 */
@RestController
@RequestMapping("/processInstance")
public class ProcessInstanceController {
    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;


    @GetMapping(value = "getInstances")
    public AjaxResponse getInstances(@AuthenticationPrincipal User user) {
        Page<ProcessInstance> processInstances = null;
        try {
            List<org.activiti.engine.runtime.ProcessInstance> processInstancesList = runtimeService.createProcessInstanceQuery().list();
            List<HashMap<String, Object>> listMap = new ArrayList<HashMap<String, Object>>();
            for (org.activiti.engine.runtime.ProcessInstance pi : processInstancesList) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", pi.getId());
                hashMap.put("name", pi.getName());
                hashMap.put("isSuspended", pi.isSuspended());
                hashMap.put("processDefinitionId", pi.getProcessDefinitionId());
                hashMap.put("processDefinitionKey", pi.getProcessDefinitionKey());
                hashMap.put("startDate", pi.getStartTime());
                hashMap.put("processDefinitionVersion", pi.getProcessDefinitionVersion());
                //因为processRuntime.processDefinition("流程部署ID")查询的结果没有部署流程与部署ID，所以用repositoryService查询
                ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(pi.getProcessDefinitionId())
                        .singleResult();
                hashMap.put("resourceName", pd.getResourceName());
                hashMap.put("deploymentId", pd.getDeploymentId());
                listMap.add(hashMap);
            }

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), listMap);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(),
                    "获取流程实例失败", e.toString());
        }


    }


    //启动
    @GetMapping("startProcess")
    public AjaxResponse startProcess(@RequestParam("processDefinitionKey") String processDefinitionKey,
                                     @RequestParam("instanceName") String instanceName,
                                     @RequestParam("instanceVariable") String instanceVariable) {
        try {
            org.activiti.engine.runtime.ProcessInstance processInstance = runtimeService
                    .createProcessInstanceBuilder()
                    .processDefinitionKey(processDefinitionKey)
                    .name(instanceName)
                    .variable("content", instanceVariable)
                    .start();
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), processInstance.getName() + "；" + processInstance.getId());
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(),
                    "创建流程实例失败", e.toString());
        }
    }

    //删除
    @GetMapping(value = "/deleteInstance")
    public AjaxResponse deleteInstance(@RequestParam("instanceId") String instanceId) {
        try {
            org.activiti.engine.runtime.ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
            runtimeService.deleteProcessInstance(instanceId, "测试删除");
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), processInstance.getName());
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(),
                    "删除流程实例失败", e.toString());
        }
    }

    //挂起冷冻
    @GetMapping(value = "suspendInstance")
    public AjaxResponse suspendInstance(@RequestParam("instanceId") String instanceId) {
        try {
            org.activiti.engine.runtime.ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
            runtimeService.suspendProcessInstanceById(instanceId);
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), processInstance.getName());
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(),
                    "挂起流程实例失败", e.toString());
        }
    }

    //激活
    @GetMapping(value = "resumeInstance")
    public AjaxResponse resumeInstance(@RequestParam("instanceId") String instanceId) {
        try {
            org.activiti.engine.runtime.ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
            runtimeService.activateProcessInstanceById(instanceId);
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), processInstance.getName());
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(),
                    "激活流程实例失败", e.toString());
        }
    }

    //获取参数
    @GetMapping(value = "getVariables")
    public AjaxResponse variables(@RequestParam("instanceId") String instanceId) {
        try {
            Map<String, Object> variables = runtimeService.getVariables(instanceId);
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), variables);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(),
                    "获取流程参数失败", e.toString());
        }
    }
}
