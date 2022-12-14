package cn.rwj.demo.activiti7;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * @Author rwj
 * @Date 2022/10/5
 * @Description
 */
@SpringBootTest
public class Part1Deployment {

    @Resource
    RepositoryService repositoryService;

    /**
     * 通过bpmn文件部署流程
     *      Activiti7默认引入了SpringSecurity，所以需要处理集成SpringSecurity，否则会报错:
     *          No qualifying bean of type 'org.springframework.security.core.userdetails.UserDetailsService' available
     *      也会导致应用无法启动：
     *          Consider defining a bean of type 'org.springframework.security.core.userdetails.UserDetailsService' in your configuratio
     *      处理SpringSecurity
     *          1.实现 UserDetailsService 接口，重写 loadUserByUsername 方法
     * 部署流程导致：
     *      1. act_re_deployment表，部署，每一次部署，新增一条记录
     *      2. act_re_procdef表，流程定义表。每次部署时，如果key不变，则增加版本号，表示是同一个流程；
     *          但是流程定义是可变的，即可以修改流程的内容。如：以前是超过半小时迟到扣钱，现在是弹性上下班。流程内容变了，只要key不变，那就代表是同一个流程。
     *          这里的key对应表中的key字段，值为BPMN流程文件中的流程id： <process id="part1_deployment" name="流程定义名称" isExecutable="true">
     *      3. act_ge_bytearray表，存储bpmn文件内容
     */
    @Test
    public void initDeploymentBPMN() {
        // String pngname="BPMN/Part1_Deployment.png";
//        String filename = "bpmn/part6_uel_v1.bpmn20.xml";
//        String filename = "bpmn/part6_uel_v2.bpmn20.xml";
        String filename = "bpmn/part6_uel_v3.bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(filename)
                //.addClasspathResource(pngname)//图片
                .name("测试流程UEL表达式v3")
                .key("p6")
                .deploy();
        System.out.println(deployment.getName());
    }

    /**
     * 通过ZIP部署流程
     * 有时候需要上传多个流程文件
     */
    @Test
    public void initDeploymentZIP() {
        InputStream fileInputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("BPMN/part1_deployment.bpmn20.zip");
        ZipInputStream zip=new ZipInputStream(fileInputStream);
        Deployment deployment=repositoryService.createDeployment()
                .addZipInputStream(zip)
                .name("流程部署测试zip")
                .deploy();
        System.out.println(deployment.getName());
    }

    //查询流程部署
    @Test
    public void getDeployments() {
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for(Deployment dep : list){
            System.out.println("Id："+dep.getId());
            System.out.println("Name："+dep.getName());
            System.out.println("DeploymentTime："+dep.getDeploymentTime());
            System.out.println("Key："+dep.getKey());
        }

    }


}
