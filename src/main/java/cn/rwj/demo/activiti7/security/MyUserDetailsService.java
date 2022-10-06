package cn.rwj.demo.activiti7.security;

import cn.rwj.demo.activiti7.model.User;
import cn.rwj.demo.activiti7.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author rwj
 * @Date 2022/10/5
 * @Description
 */
@Component
public class MyUserDetailsService implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //-------------------------读取数据库判断登录-----------------------
/*        SysUser sysUser = sysUserService.queryByUsername(username);

        if (Objects.nonNull(sysUser)) {
            return User.withUsername(username).password(sysUser.getEncodePassword())
                    .authorities(AuthorityUtils.NO_AUTHORITIES)
                    .build();
        }
        throw new UsernameNotFoundException("username: " + username + " notfound");*/


//------------------------根据code写死用户登录------------------------------

        /*
        //-----------------------正常的代码-----------------
        logger.info("登录用户名：" + username);
        String passWord=passwordEncoder().encode("111");
        logger.info("密码：" + passWord);
        return new User(username,passWord, AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ACTIVITI_USER"));
        //-----------------------正常的代码-----------------
*/
        //页面默认会对密码加密，数据库里如果在用户注册时，用的是加密过的密码，则直接读取比较即可
        //return new User(username,"$2a$10$YFZDTqyBqwHkV/vTxKrhtuyIQCMD/joeIylCs8wbvXnhOYRgD/kDq", AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));

        //-------------------根据自定义用户属性登录-----------------------------
        User user = userService.lambdaQuery().eq(User::getUsername, username).one();
        if (Objects.isNull(user)) throw new UsernameNotFoundException("该用户不存在");
        logger.info("userInfo: {}", user);
//        List<GrantedAuthority> authority = new ArrayList<>();
//        authority.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(user.getRoles().split(","));
        authorityList.add(new SimpleGrantedAuthority("ROLE_ACTIVITI_ADMIN"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorityList
        );
        /*return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.getPassword())
                .authorities(AuthorityUtils.NO_AUTHORITIES)
                .build();*/
    }
}
