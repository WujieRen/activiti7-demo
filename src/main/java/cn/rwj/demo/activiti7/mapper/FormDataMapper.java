package cn.rwj.demo.activiti7.mapper;

import cn.rwj.demo.activiti7.model.FormData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.HashMap;
import java.util.List;

/**
 * @Author rwj
 * @Date 2022/10/6
 * @Description
 */
@Mapper
public interface FormDataMapper extends BaseMapper<FormData> {

    @Select("SELECT Control_ID_,Control_VALUE_ from formdata where PROC_INST_ID_ = #{PROC_INST_ID}")
    List<HashMap<String,Object>> selectFormDataByProcessInstanceId(@Param("PROC_INST_ID") String PROC_INST_ID);


    //写入表单
    @Insert("<script> " +
            "    insert into formdata (PROC_DEF_ID_,PROC_INST_ID_,FORM_KEY_,Control_ID_,Control_VALUE_)" +
            "    values " +
            "    <foreach collection=\"maps\" item=\"formData\" index=\"index\" separator=\",\">" +
            "          (#{formData.PROC_DEF_ID_,jdbcType=VARCHAR},#{formData.PROC_INST_ID_,jdbcType=VARCHAR}," +
            "           #{formData.FORM_KEY_,jdbcType=VARCHAR}, #{formData.Control_ID_,jdbcType=VARCHAR}," +
            "           #{formData.Control_VALUE_,jdbcType=VARCHAR})" +
            "    </foreach> " +
            "</script>")
    int insertFormData(@Param("maps") List<HashMap<String, Object>> maps);

    //删除表单
    @Delete("DELETE FROM formdata WHERE PROC_DEF_ID_ = #{PROC_DEF_ID}")
    void DeleteFormData(@Param("PROC_DEF_ID") String PROC_DEF_ID);

}
