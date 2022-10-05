package cn.rwj.demo.activiti7.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

/**
 * @Author rwj
 * @Date 2022/10/5
 * @Description 测试Mybatis Plus
 */
@Data
@ToString
@TableName("user")
public class User {

    private Long id;
    public String name;
    private String address;
    private String username;
    private String password;
    private String roles;

}
