package cn.rwj.demo.activiti7.util;

public class GlobalConfig {
    /**
     * 测试场景
     */
    public static final Boolean Test = false;

    //windows路径
    public static final String BPMN_PathMapping = "E:\\activiti7-demo\\src\\main\\resources\\bpmn-upload";

    //Liunx路径
    //public static final String BPMN_PathMapping = "/opt/activiti7-demo/src/main/resources/bpmn-upload";

    public enum ResponseCode {
        SUCCESS(0, "成功"),
        ERROR(1, "错误");

        private final int code;
        private final String desc;

        ResponseCode(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

}
