package cn.rwj.demo.activiti7.controller;

import cn.rwj.demo.activiti7.util.AjaxResponse;
import cn.rwj.demo.activiti7.util.GlobalConfig;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricTaskInstance;
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

    //用户历史
    @GetMapping(value = "getInstancesByUserName")
    public AjaxResponse InstancesByUser(String userName) {
        try {
//            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                    .orderByHistoricTaskInstanceEndTime().asc()
                    .taskAssignee(userName)
                    .list();
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), historicTaskInstances);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(),
                    "获取历史任务失败", e.toString());
        }
    }

    //任务实例历史
    @GetMapping(value = "getInstancesByPiId")
    public AjaxResponse getInstancesByPiID(@RequestParam("processInstanceId") String processInstanceId) {
        try {
            //--------------------------------------------另一种写法-------------------------
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                    .orderByHistoricTaskInstanceEndTime().asc()
                    .processInstanceId(processInstanceId)
                    .list();
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), historicTaskInstances);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(),
                    "获取历史任务失败", e.toString());
        }
    }


}
