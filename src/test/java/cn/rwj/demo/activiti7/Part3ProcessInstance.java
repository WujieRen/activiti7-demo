package cn.rwj.demo.activiti7;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
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
public class Part3ProcessInstance {
    @Resource
    private RuntimeService runtimeService;

    /**
     * 初始化流程实例
     *     1. act_ru_execution表，表运行时流程执行实例
     *          初始化时会生成两条数据，一个是开始节点的，一个是开始节点随后节点的.
     *          后续执行到那个节点就会使那个节点的数据，执行到那里就是哪里，之行结束就没了。
     *     2. act_ru_task表，运行时任务节点
     *     3. act_hi_actinst表，执行历史
     *     4. act_hi_procinst表，流程实例历史
     *     5. act_hi_taskinst表，任务实例历史
     *     TODO: 另外，看有些博客和课程说到 act_ru_identitylink 表也会有变化，我这里没发现有变化，后面用NPMNJS测试了再补充。
     *          我用的 7.1.0.M6 版本和 Acti BPMN Visualizer 插件。
     *     解决上次提交疑问，我这里提交没有在 act_ru_identitylink 表生成记录是因为我没有给  UserTask 设置 Asignee。
     *     6. act_ru_identitylink表，从该表中可根据流程实例ID查询 user_id，group_id，type等
     *
     */
    @Test
    public void initProcessInstance(){
        //1、获取页面表单填报的内容，请假时间，请假事由，String formData
        //2、formData 写入业务表，返回业务表主键ID==businessKey
        //3、把业务数据与Activiti7流程数据关联
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("part1_deployment_1","businessKey_p1d1");
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("part4_task_1","businessKey_p4");
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("part4_task_claim","businessKey_p4_claim");
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("part6_uel_v2","businessKey_p6_uel");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("part6_uel_v3","businessKey_p6_uel");
//        ProcessInstance start = runtimeService.createProcessInstanceBuilder().name("test").processDefinitionKey("part6_uel_v3").businessKey("businessKey_p6_uel").start();
        System.out.println("流程实例ID："+processInstance.getProcessDefinitionId());
    }

    //获取流程实例列表
    @Test
    public void getProcessInstances(){
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        for(ProcessInstance pi : list){
            System.out.println("--------流程实例------");
            System.out.println("ProcessInstanceId："+pi.getProcessInstanceId());
            System.out.println("ProcessDefinitionId："+pi.getProcessDefinitionId()); //从这里可以看出，每次部署Deployment，如果key不变，deployment会增加版本，且启动实例时用的是该key对应的最新版本的流程
            System.out.println("isEnded"+pi.isEnded());
            System.out.println("isSuspended："+pi.isSuspended());

        }
        /*List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().processInstanceId("97b1cfe7-4492-11ed-908f-a036bc09649b").list();
        ProcessInstance processInstance = list.get(0); //如果记录不存在 IndexOutOfBoundsException: Index: 0, Size: 0
        System.out.println("isSuspended："+processInstance.isSuspended());*/
    }


    //暂停与激活流程实例
    @Test
    public void activitieProcessInstance(){
        runtimeService.suspendProcessInstanceById("97b1cfe7-4492-11ed-908f-a036bc09649b");
        System.out.println("挂起流程实例");
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId("97b1cfe7-4492-11ed-908f-a036bc09649b").list().get(0);
        System.out.println("实例挂起状态：" + processInstance.isSuspended());


        runtimeService.activateProcessInstanceById("97b1cfe7-4492-11ed-908f-a036bc09649b");
        System.out.println("激活流程实例");
        processInstance = runtimeService.createProcessInstanceQuery().processInstanceId("97b1cfe7-4492-11ed-908f-a036bc09649b").list().get(0);
        System.out.println("实例挂起状态：" + processInstance.isSuspended());
    }

    //删除流程实例
    @Test
    public void delProcessInstance(){
        runtimeService.deleteProcessInstance("97b1cfe7-4492-11ed-908f-a036bc09649b","测试删除");
        System.out.println("删除流程实例");
    }


}
