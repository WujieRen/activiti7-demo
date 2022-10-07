package cn.rwj.demo.activiti7.controller;

import cn.rwj.demo.activiti7.model.User;
import cn.rwj.demo.activiti7.util.AjaxResponse;
import cn.rwj.demo.activiti7.util.GlobalConfig;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author rwj
 * @Date 2022/10/7
 * @Description
 */
@RestController
@RequestMapping("/activitiHistory")
public class ActivitiHistoryController {

    @Resource
    private HistoryService historyService;
    @Resource
    private RepositoryService repositoryService;

    //用户历史
    @GetMapping(value = "getInstancesByUserName")
    public AjaxResponse InstancesByUser(String userName) {
        try {
//            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().orderByHistoricTaskInstanceEndTime().asc().taskAssignee(userName).list();
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(), GlobalConfig.ResponseCode.SUCCESS.getDesc(), historicTaskInstances);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(), "获取历史任务失败", e.toString());
        }
    }

    //任务实例历史
    @GetMapping(value = "getInstancesByPiId")
    public AjaxResponse getInstancesByPiID(@RequestParam("processInstanceId") String processInstanceId) {
        try {
            //--------------------------------------------另一种写法-------------------------
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().orderByHistoricTaskInstanceEndTime().asc().processInstanceId(processInstanceId).list();
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(), GlobalConfig.ResponseCode.SUCCESS.getDesc(), historicTaskInstances);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(), "获取历史任务失败", e.toString());
        }
    }

    //流程图高亮
    @GetMapping("gethighLine")
//    public AjaxResponse gethighLine(@RequestParam("instanceId") String instanceId, @AuthenticationPrincipal User user) {
    public AjaxResponse gethighLine(@RequestParam("instanceId") String instanceId, @AuthenticationPrincipal User user) {
        try {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(instanceId).singleResult();
            //获取bpmnModel对象
            BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
            //这里因为只定义了一个<Process> 所以获取集合中的第一个即可
            Process process = bpmnModel.getProcesses().get(0);
//            Process process = bpmnModel.getProcessById("ProcessId");   // 如果一个bpmn中有多个process，让前端传要高量的id过来
//            List<Process> processes = bpmnModel.getProcesses();         //或者所有的都高亮
            //获取所有的FlowElement信息
            Collection<FlowElement> flowElements = process.getFlowElements();

            Map<String, String> flowMap = new HashMap<>();
            for (FlowElement flowElement : flowElements) {
                //判断是否是连线，SequensFlow对象保存了节点间的连线关系  source -> target
                if (flowElement instanceof SequenceFlow) {
                    SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                    String sourceRef = sequenceFlow.getSourceRef();
                    String targetRef = sequenceFlow.getTargetRef();
                    flowMap.put(sourceRef + targetRef, sequenceFlow.getId());
                }
            }

            //获取流程实例 历史节点(全部)
            List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId(instanceId).list();
            //各个历史节点   两两组合 key
            Set<String> keyList = new HashSet<>();
            for (HistoricActivityInstance i : list) {
                for (HistoricActivityInstance j : list) {
                    if (i != j) {
                        keyList.add(i.getActivityId() + j.getActivityId());
                    }
                }
            }
            //高亮连线ID
            Set<String> highLine = new HashSet<>();
            keyList.forEach(s -> highLine.add(flowMap.get(s))); //如果flowMap.get(s)有值，说明flow已经完成（走过）


            //获取流程实例 历史节点（已完成）
            List<HistoricActivityInstance> listFinished = historyService.createHistoricActivityInstanceQuery().processInstanceId(instanceId).finished().list();
            //高亮节点ID
            Set<String> highPoint = new HashSet<>();
            listFinished.forEach(s -> highPoint.add(s.getActivityId()));

            //获取流程实例 历史节点（待办节点）
            List<HistoricActivityInstance> listUnFinished = historyService.createHistoricActivityInstanceQuery().processInstanceId(instanceId).unfinished().list();

            //需要移除的高亮连线
            Set<String> set = new HashSet<>();
            //待办高亮节点
            Set<String> waitingToDo = new HashSet<>();
            listUnFinished.forEach(s -> {
                waitingToDo.add(s.getActivityId());

                for (FlowElement flowElement : flowElements) {
                    //判断是否是 用户节点
                    if (flowElement instanceof UserTask) {
                        UserTask userTask = (UserTask) flowElement;

                        if (Objects.equals(userTask.getId(), s.getActivityId())) {
                            List<SequenceFlow> outgoingFlows = userTask.getOutgoingFlows();
                            //因为 高亮连线查询的是所有节点  两两组合 把待 办 之后 往外发出的连线 也包含进去了  所以要把高亮待办节点 之后 即出的连线去掉
                            if (outgoingFlows != null && outgoingFlows.size() > 0) {
                                outgoingFlows.forEach(a -> {
                                    if (a.getSourceRef().equals(s.getActivityId())) {
                                        set.add(a.getId());
                                    }
                                });
                            }
                        }
                    }
                }
            });

            highLine.removeAll(set);

            //当前用户已完成的任务
            String AssigneeName = null;
            if (GlobalConfig.Test) {
                AssigneeName = "yuangong";
            } else {
//                AssigneeName = user.getUsername();
                AssigneeName = SecurityContextHolder.getContext().getAuthentication().getName();
            }
            //获取当前用户
            //User sysUser = getSysUser();
            Set<String> iDid = new HashSet<>(); //存放 高亮 我的办理节点
            List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery().taskAssignee(AssigneeName).finished().processInstanceId(instanceId).list();

            taskInstanceList.forEach(a -> iDid.add(a.getTaskDefinitionKey()));

            Map<String, Object> reMap = new HashMap<>();
            reMap.put("highPoint", highPoint);
            reMap.put("highLine", highLine);
            reMap.put("waitingToDo", waitingToDo);
            reMap.put("iDid", iDid);

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(), GlobalConfig.ResponseCode.SUCCESS.getDesc(), reMap);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(), "渲染历史流程失败", e.toString());
        }
    }

}
