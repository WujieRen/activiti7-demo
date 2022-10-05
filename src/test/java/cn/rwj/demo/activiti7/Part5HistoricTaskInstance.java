package cn.rwj.demo.activiti7;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author rwj
 * @Date 2022/10/5
 * @Description
 */
@SpringBootTest
public class Part5HistoricTaskInstance {

    @Resource
    private HistoryService historyService;

    //根据用户名查询历史记录
    @Test
    public void HistoricTaskInstanceByUser(){
        List<HistoricTaskInstance> list = historyService
                .createHistoricTaskInstanceQuery()
                .orderByHistoricTaskInstanceEndTime().asc()
//                .taskAssignee("yuangong")
                .list();
        for(HistoricTaskInstance hi : list){
            System.out.println("Id："+ hi.getId());
            System.out.println("ProcessInstanceId："+ hi.getProcessInstanceId());
            System.out.println("Name："+ hi.getName());

        }
    }


    //根据流程实例ID查询历史
    @Test
    public void HistoricTaskInstanceByPiID(){
        List<HistoricTaskInstance> list = historyService
                .createHistoricTaskInstanceQuery()
                .orderByHistoricTaskInstanceEndTime().asc()
                .processInstanceId("da08f27a-44a7-11ed-820c-a036bc09649b")
                .list();
        for(HistoricTaskInstance hi : list){
            System.out.println("Id："+ hi.getId());
            System.out.println("ProcessInstanceId："+ hi.getProcessInstanceId());
            System.out.println("Name："+ hi.getName());

        }
    }
}
