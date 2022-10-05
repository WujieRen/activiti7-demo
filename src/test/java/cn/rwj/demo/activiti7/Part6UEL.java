package cn.rwj.demo.activiti7;

import cn.rwj.demo.activiti7.pojo.UEL_POJO;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author rwj
 * @Date 2022/10/5
 * @Description 注意！在activiti7.1.0M4版本中，bpmn变量参数名必须全是小写，如果大小写混排是无法是别的
 */
@SpringBootTest
public class Part6UEL {

    @Resource
    RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;

    /**
     * 启动流程实例带参数，执行人
     *      act_ru_variable     运行时参数表
     *      act_hi_taskinst     历史参数表
     */
    @Test
    public void initProcessInstanceWithArgs() {
        //创建流程
//        String filename = "bpmn/part6_uel_v1.bpmn20.xml";
//        Deployment deployment = repositoryService.createDeployment()
//                .addClasspathResource(filename)
//                .name("测试流程UEL表达式")
//                .key("p6")
//                .deploy();
        //流程变量
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("zhixingren", "zhuguan");
        //创建实例
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(
                        "part6_uel_v1"
                        , "businessKey_p6_uel"
                        , variables);
        System.out.println("流程实例ID：" + processInstance.getProcessDefinitionId());
        // 然后去查看 act_ru_task 表，发现 business_key_=businessKey_p6_uel 的task的 ASIGNEE_=zhuguan（即上面变量给定的值）
    }

    //完成任务带参数，指定流程变量测试
    @Test
    public void completeTaskWithArgs() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("pay", "101");
        //到这里，先去task哪里查一下，会看到任务实例启动后，yuangong提交报销申请的task记录
        taskService.complete("934e887a-44b5-11ed-9714-a036bc09649b", variables);
        // 这里因为pay>100，所以会让lingdao审批，即会产生lingdao审批task，而如果pay<=100，则产生zhuguan审批task
        System.out.println("完成任务");
    }

    //启动流程实例带参数，使用实体类作为参数
    @Test
    public void initProcessInstanceWithClassArgs() {
        //部署流程
        String filename = "bpmn/part6_uel_v3.bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(filename)
                //.addClasspathResource(pngname)//图片
//                .name("测试流程UEL表达式v3_")
                .name("测试流程UEL表达式v3_测试大小写混排变量名")
                .key("p6")
                .deploy();
        System.out.println("deploymentId" + deployment.getId());

        UEL_POJO uel_pojo = new UEL_POJO();
        uel_pojo.setZhixingren("yuangong");
        //对象作为流程变量
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("uelpojo", uel_pojo);

        //启动实例
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(
                        "part6_uel_v3"
                        , "businessKey_p6_uel"
                        , variables);
        System.out.println("流程实例ID：" + processInstance.getProcessDefinitionId());
        // 然后去查询task就可以看到 实体类任务task，taskAsignee=yuangong（上面设置的pojo中的值）
    }

    //任务完成环节带参数，指定多个候选人，执行上面 initProcessInstanceWithClassArgs() 后再执行这个
    @Test
    public void initProcessInstanceWithCandiDateArgs() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("houxuanren", "wukong,tangseng");
        taskService.complete("a470c07a-44b7-11ed-a567-a036bc09649b", variables);    // 这里的taskId就是上面 initProcessInstanceWithClassArgs 后生成的task.getId()
        System.out.println("完成任务");
        //然后去查询task，可以看到在 实体类任务task（节点） 执行结束后，到达了候选人task（节点），此时该节点的Asignee=null
        //原因是候选人任务需要被拾取后，才会设置Asignee
    }

    //直接指定流程变量
    @Test
    public void otherArgs() {
        runtimeService.setVariable("4f6c9e23-d3ae-11ea-82ba-dcfb4875e032", "pay", "101");
//        runtimeService.setVariables();
//        taskService.setVariable();
//        taskService.setVariables();

    }

    /**
     * 局部变量，以上全是全局变量。局部变量只在当前环节有效
     */
    @Test
    public void otherLocalArgs() {
        runtimeService.setVariableLocal("4f6c9e23-d3ae-11ea-82ba-dcfb4875e032", "pay", "101");
//        runtimeService.setVariablesLocal();
//        taskService.setVariableLocal();
//        taskService.setVariablesLocal();
    }
}
