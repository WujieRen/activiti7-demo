package cn.rwj.demo.activiti7;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
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
public class Part2ProcessDefinition {

    @Resource
    RepositoryService repositoryService;

    //查询流程定义
    @Test
    public void getDefinitions(){
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .list();
        for(ProcessDefinition pd : list){
            System.out.println("------流程定义--------");
            System.out.println("Name："+pd.getName());
            System.out.println("Key："+pd.getKey());
            System.out.println("ResourceName："+pd.getResourceName());
            System.out.println("DeploymentId："+pd.getDeploymentId());
            System.out.println("ProcessDefinitionId："+pd.getId());
            System.out.println("Version："+pd.getVersion());

        }

    }

    /**
     * 删除流程定义
     * 1. act_re_deployment表，删除 id=4fcb10eb-446f-11ed-b9a6-a036bc09649b 的记录
     * 2. act_re_procdef表，删除 deployment_id=4fcb10eb-446f-11ed-b9a6-a036bc09649b 的记录
     * 3. act_ge_bytearray表，删除 deployment_id=4fcb10eb-446f-11ed-b9a6-a036bc09649b 的记录
     */
    @Test
    public void delDefinition(){

        String pdID="d9473c80-4475-11ed-9e60-a036bc09649b";
        repositoryService.deleteDeployment(pdID,true);
        System.out.println("删除流程定义成功");
    }

}
