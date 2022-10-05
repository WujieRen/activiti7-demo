package cn.rwj.demo.activiti7.service;

import cn.rwj.demo.activiti7.mapper.UserMapper;
import cn.rwj.demo.activiti7.model.User;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Author rwj
 * @Date 2022/10/5
 * @Description
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {
}
