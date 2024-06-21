package cn.itcast.user.handler;

public class GlobalFallBackHandler {
    public static String fallbackHandler(int flag) {
        return "业务出现异常 - 全局处理";
    }
}
