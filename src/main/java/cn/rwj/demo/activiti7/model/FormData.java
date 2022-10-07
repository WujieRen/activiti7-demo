package cn.rwj.demo.activiti7.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

/**
 * @Author rwj
 * @Date 2022/10/7
 * @Description
 */
@ToString
@Data
@TableName("formdata")
public class FormData {

    private String proc_def_id_;
    private String proc_inst_id_;
    private String form_key_;
    private String control_id_;
    private String control_value_;

}
