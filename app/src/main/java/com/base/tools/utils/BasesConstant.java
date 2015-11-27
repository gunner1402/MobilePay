package com.base.tools.utils;

/**
 * 基础 常量
 * Created by Administrator on 2015/10/15.
 */
public class BasesConstant {

    public static String ENVIRONMENT_SANDBOX = "sandbox";
    public static String ENVIRONMENT_PRODUCTION = "production";

    public static String MODE_ONLINE = "online";
    public static String MODE_OFFLINE = "offline";


    /**
     * 数据格式异常
     */
    public static final int RESULT_FAIL_DATAERROR = -2;
    /**
     * 成功
     */
    public static final int RESULT_SUCCESS = -1;
    /**
     * 失败
     */
    public static final int RESULT_FAIL = 0;
    /**
     * 异常
     */
    public static final int RESULT_EXCEPTION = 1;
    /**
     * 取消
     */
    public static final int RESULT_CANCEL = 2;
    /**
     * 操作结果未知（此code一般由server to server通知）
     */
    public static final int RESULT_PENDING = 3;
    /**
     * 支付成功，但发钻失败，用户不愿意重试的情况下得结果码
     */
    public static final int RESULT_EXCEPTION_GOOGLEPAY_EXCEPTION = 11;
}
