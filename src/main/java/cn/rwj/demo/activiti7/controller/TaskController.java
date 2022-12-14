package cn.rwj.demo.activiti7.controller;

import cn.rwj.demo.activiti7.mapper.FormDataMapper;
import cn.rwj.demo.activiti7.model.FormData;
import cn.rwj.demo.activiti7.secuexam.SecurityUtil;
import cn.rwj.demo.activiti7.service.FormDataService;
import cn.rwj.demo.activiti7.util.AjaxResponse;
import cn.rwj.demo.activiti7.util.GlobalConfig;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.FormValue;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author rwj
 * @Date 2022/10/7
 * @Description
 */
@RestController
@RequestMapping("/task")
public class TaskController {
    @Resource
    private TaskRuntime taskRuntime;
    @Resource
    private SecurityUtil securityUtil;
    @Resource
    private ProcessRuntime processRuntime;
    @Resource
    private FormDataService formDataService;
    @Resource
    private RepositoryService repositoryService;

    //获取我的代办任务
    @GetMapping(value = "getTasks")
    public AjaxResponse getTasks() {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("yuangong");
            }
            Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 100));
            List<HashMap<String, Object>> listMap = new ArrayList<HashMap<String, Object>>();
            for (Task tk : tasks.getContent()) {
                ProcessInstance processInstance = processRuntime.processInstance(tk.getProcessInstanceId());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", tk.getId());
                hashMap.put("name", tk.getName());
                hashMap.put("status", tk.getStatus());
                hashMap.put("createdDate", tk.getCreatedDate());
                if (tk.getAssignee() == null) {//执行人，null时前台显示未拾取
                    hashMap.put("assignee", "待拾取任务");
                } else {
                    hashMap.put("assignee", tk.getAssignee());//
                }

                hashMap.put("instanceName", processInstance.getName());
                listMap.add(hashMap);
            }

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), listMap);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(),
                    "获取我的代办任务失败", e.toString());
        }
    }

    //完成待办任务
    @GetMapping(value = "completeTask")
    public AjaxResponse completeTask(@RequestParam("taskId") String taskId) {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("yuangong");
            }

            Task task = taskRuntime.task(taskId);
            if (task.getAssignee() == null) {
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
            }
            taskRuntime.complete(
                    TaskPayloadBuilder
                            .complete()
                            .withTaskId(task.getId())
                            //.withVariable("num", "2")//执行环节设置变量
                            .build()
            );

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), null);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(),
                    "完成失败", e.toString());
        }
    }

    /**
     * 经测试， Activity 7.4.0 可以取到表单的所有属性和值
     *
     * @param taskId
     */
    @GetMapping(value = "testGetForm")
    public void testGetForm(@RequestParam("taskId") String taskId) {
        if (GlobalConfig.Test) {
            securityUtil.logInAs("yuangong");
        }
        Task task = taskRuntime.task(taskId);
        UserTask userTask = (UserTask) repositoryService.getBpmnModel(task.getProcessDefinitionId())
//                .getFlowElement(task.getFormKey());   //这样获取要求 formKey的值必须 = <userTask id="sid-d421394f-5be5-478d-b4e1-94fc526e8762">Id的值
                .getFlowElement(task.getTaskDefinitionKey());    //这里建议直接用taskDefinitoinKey去获取 FormProperties不用formKey去获取
        List<FormProperty> formProperties = userTask.getFormProperties();
        formProperties.forEach(i -> {
            System.out.println("Form property ID:  " + i.getId());
            System.out.println("Property name:  " + i.getName());
            System.out.println("type:  " + i.getType());
            System.out.println("variable:  " + i.getVariable());
            System.out.println("Default value:  " + i.getDefaultExpression());
            System.out.println("Expression:  " + i.getExpression());
            List<FormValue> formValues = i.getFormValues();
            formValues.forEach(f -> {
                System.out.println("Form property value -> Value ID: " + f.getId());
            });
        });
    }

    //保存表单
    @PostMapping(value = "oldFormDataSave")
    public AjaxResponse oldFormDataSave(@RequestParam("taskId") String taskId,
                                        @RequestParam("formData") String formData) {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("zhuguan");
            }

            Task task = taskRuntime.task(taskId);

            //formData:控件id-_!控件值-_!是否参数!_!控件id-_!控件值-_!是否参数
            //FormProperty_0lovri0-_!不是参数-_!f!_!FormProperty_1iu6onu-_!数字参数-_!s


            HashMap<String, Object> variables = new HashMap<String, Object>();
            Boolean hasVariables = false;//没有任何参数


            List<HashMap<String, Object>> listMap = new ArrayList<>();

            //前端传来的字符串，拆分成每个控件
            String[] formDataList = formData.split("!_!");//
            for (String controlItem : formDataList) {
                String[] formDataItem = controlItem.split("-_!");

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("PROC_DEF_ID_", task.getProcessDefinitionId());
                hashMap.put("PROC_INST_ID_", task.getProcessInstanceId());
                hashMap.put("FORM_KEY_", task.getFormKey());
                hashMap.put("Control_ID_", formDataItem[0]);
                hashMap.put("Control_VALUE_", formDataItem[1]);
                listMap.add(hashMap);

                //构建参数集合
                switch (formDataItem[2]) {
                    case "f":
                        System.out.println("控件值不作为参数");
                        break;
                    case "s":
                        variables.put(formDataItem[0], formDataItem[1]);
                        hasVariables = true;
                        break;
                    case "t":
                        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        variables.put(formDataItem[0], timeFormat.parse(formDataItem[2]));
                        hasVariables = true;
                        break;
                    case "b":
                        variables.put(formDataItem[0], BooleanUtils.toBoolean(formDataItem[2]));
                        hasVariables = true;
                        break;
                    default:
                        System.out.println("控件参数类型配置错误：" + formDataItem[0] + "的参数类型不存在，" + formDataItem[2]);
                }
            }//for结束

            if (hasVariables) {
                //带参数完成任务
                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(taskId)
                        .withVariables(variables)
                        .build());
            } else {
                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(taskId)
                        .build());
            }

            //写入数据库
            int result = formDataService.getBaseMapper().insertFormData(listMap);

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), listMap);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(),
                    "失败", e.toString());
        }
    }

    @PostMapping(value = "newFormDataSave")
    public AjaxResponse newFormDataSave(@RequestParam("taskId") String taskId) {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("yuangong");
            }

            Task task = taskRuntime.task(taskId);
            UserTask userTask = (UserTask) repositoryService.getBpmnModel(
                    task.getProcessDefinitionId()).getFlowElement(task.getTaskDefinitionKey()
            );

            HashMap<String, Object> variables = new HashMap<String, Object>();

            List<FormData> formDataList = userTask
                    .getFormProperties()
                    .stream()
                    .map(fp -> {
                        if (StringUtils.isNoneBlank()) {
                            //变量具体是要作为参数完成任务并往下传递，但是目前FormProperty这个对象中各个属性和含义我还没太搞清楚，
                            //留待日后BPMNJS弄好了再回来看
                            variables.put(fp.getId(), fp.getVariable());
                        }
                        FormData formData = new FormData();
                        formData.setProc_def_id_(task.getProcessDefinitionId());
                        formData.setProc_inst_id_(task.getProcessInstanceId());
                        formData.setForm_key_(task.getFormKey());
                        formData.setControl_id_(fp.getId());
                        formData.setControl_value_(JSONObject.toJSONString(fp.getFormValues()));
                        return formData;
                    }).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(variables)) {
                //带参数完成任务
                taskRuntime.complete(
                        TaskPayloadBuilder.complete()
                                .withTaskId(taskId)
                                .withVariables(variables)
                                .build()
                );
            } else {
                taskRuntime.complete(
                        TaskPayloadBuilder.complete().withTaskId(taskId).build()
                );
            }

            //写入数据库
            boolean result = formDataService.saveBatch(formDataList);

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), result);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(),
                    "失败", e.toString());
        }
    }

    /**
     * 7.1.0.M4 版本无法获取表单，所以自定义了一些规则，用以下方式获取表单数据并返回给前端，
     * 现在使用的 7.4.0 版本可以获取到表单数据，所以该方法无用
     * @param taskID
     * @return
     */
    @GetMapping(value = "oldFormDataShow")
    public AjaxResponse oldFormDataShow(@RequestParam("taskID") String taskID) {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("yuangong");
            }
            Task task = taskRuntime.task(taskID);

//-----------------------构建表单控件历史数据字典------------------------------------------------
            //本实例所有保存的表单数据HashMap，为了快速读取控件以前环节存储的值
            HashMap<String, String> controlistMap = new HashMap<>();
            //本实例所有保存的表单数据
            List<HashMap<String, Object>> tempControlList = formDataService.getBaseMapper().selectFormDataByProcessInstanceId(task.getProcessInstanceId());
            for (HashMap ls : tempControlList) {
                //String Control_ID = ls.get("Control_ID_").toString();
                //String Control_VALUE = ls.get("Control_VALUE_").toString();
                controlistMap.put(ls.get("Control_ID_").toString(), ls.get("Control_VALUE_").toString());
            }
            //String controlistMapValue = controlistMap.get("控件ID");
            //controlistMap.containsKey()

            //

/*  ------------------------------------------------------------------------------
            FormProperty_0ueitp2-_!类型-_!名称-_!默认值-_!是否参数
            例子：
            FormProperty_0lovri0-_!string-_!姓名-_!请输入姓名-_!f
            FormProperty_1iu6onu-_!int-_!年龄-_!请输入年龄-_!s

            默认值：无、字符常量、FormProperty_开头定义过的控件ID
            是否参数：f为不是参数，s是字符，t是时间(不需要int，因为这里int等价于string)
            注：类型是可以获取到的，但是为了统一配置原则，都配置到
            */

            //注意!!!!!!!!:表单Key必须要任务编号一模一样，因为参数需要任务key，但是无法获取，只能获取表单key“task.getFormKey()”当做任务key
            UserTask userTask = (UserTask) repositoryService.getBpmnModel(task.getProcessDefinitionId())
                    .getFlowElement(task.getFormKey());

            if (userTask == null) {
                return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                        GlobalConfig.ResponseCode.SUCCESS.getDesc(), "无表单");
            }
            List<FormProperty> formProperties = userTask.getFormProperties();
            List<HashMap<String, Object>> listMap = new ArrayList<HashMap<String, Object>>();
            for (FormProperty fp : formProperties) {
                String[] splitFP = fp.getId().split("-_!");

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", splitFP[0]);
                hashMap.put("controlType", splitFP[1]);
                hashMap.put("controlLable", splitFP[2]);


                //默认值如果是表单控件ID
                if (splitFP[3].startsWith("FormProperty_")) {
                    //控件ID存在
                    if (controlistMap.containsKey(splitFP[3])) {
                        hashMap.put("controlDefValue", controlistMap.get(splitFP[3]));
                    } else {
                        //控件ID不存在
                        hashMap.put("controlDefValue", "读取失败，检查" + splitFP[0] + "配置");
                    }
                } else {
                    //默认值如果不是表单控件ID则写入默认值
                    hashMap.put("controlDefValue", splitFP[3]);
                }


                hashMap.put("controlIsParam", splitFP[4]);
                listMap.add(hashMap);
            }

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), listMap);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(),
                    "失败", e.toString());
        }
    }

}
