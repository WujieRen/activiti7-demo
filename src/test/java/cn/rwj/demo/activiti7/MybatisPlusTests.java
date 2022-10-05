package cn.rwj.demo.activiti7;

import cn.rwj.demo.activiti7.mapper.UserMapper;
import cn.rwj.demo.activiti7.model.User;
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
public class MybatisPlusTests {

    @Resource
    UserMapper userMapper;

    @Test
    void listUser() {
        List<User> users = userMapper.selectList(null);
        users.forEach(System.out::println);
    }

}
