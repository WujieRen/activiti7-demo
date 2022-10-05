package cn.rwj.demo.activiti7.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author rwj
 * @Date 2022/10/5
 * @Description
 */
@RestController
public class HeloController {
    @GetMapping(value = "/hello")
    public String hello() {
        return "Activiti7 欢迎你";
    }
}
