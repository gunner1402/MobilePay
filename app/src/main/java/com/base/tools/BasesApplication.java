package com.base.tools;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.base.tools.db.BasesDBHelper;
import com.base.tools.entity.UserInfo;
import com.base.tools.exception.CrashHandler;
import com.base.tools.google.GoogleBillingUtils;
import com.base.tools.utils.BasesUtils;
import com.mopub.volley.RequestQueue;
import com.mopub.volley.toolbox.Volley;
import com.oasgames.android.oaspay.service.HttpService;
import com.oasgames.android.oaspay.tools.Constant;

import java.util.List;

/**
 * Created by Administrator on 2015/10/15.
 */
public class BasesApplication extends Application {
    final String TAG = BasesApplication.class.getSimpleName();
    /**
     * 登录用户信息，游戏端调用个体UserInfo获取该对象
     */
    public static UserInfo userInfo;
    /**
     * 请求消息队列
     */
    public static RequestQueue volleyRequestQueue;

    /**
     * 数据库操作类
     */
    public static BasesDBHelper dbHelper;
    public static SharedPreferences setting;
    public static SharedPreferences.Editor settingEditor;

    /**
     * 应用包名，用于读取R文件
     */
    public static String packageName;
    /**
     * SDK request response 信息打印
     */
    public static boolean SDKMODE_SANDBOX_REQEUST_RESPONSE = false;
    /**
     * SDK 沙盒模式
     */
    public static boolean OASISSDK_ENVIRONMENT_SANDBOX = true;

    /**
     * 游戏模式
     */
    public static String MODE = "online";

    /**
     * 公共key
     */
    public static String PUBLICKEY;

    /**
     * 离线模式下，本地登录用户信息
     * 该信息将在用户正式登录成功后，清空该对象
     */
    public static UserInfo localInfo;

    public static boolean NetworkisAvailable = true;

    public static String NetworkExtraInfo = "";

    /**
     * 退出应用的标志
     */
    public static boolean isExit = false;

    /**
     * 运行日志信息-缓存
     */
    public static List<String> logLists = null;
    /**
     * 游戏调用SDK的日志信息-将存入SD卡
     */
    public static List<String> logListsSD = null;
    @Override
    public void onCreate() {
        super.onCreate();
        packageName = getPackageName();

        volleyRequestQueue = Volley.newRequestQueue(this);

        dbHelper = new BasesDBHelper(this, Constant.createTables, Constant.dropTables);
        dbHelper.open();

        setting = getSharedPreferences(packageName.replace(".", "_"), Context.MODE_PRIVATE);
        settingEditor = setting.edit();

        BasesUtils.getPhoneInfo(this);// 初始化设备相关信息
        GoogleBillingUtils.getADIDThread(this);// 初始化Google 广告ＩＤ
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpService.instance().getConutryCodeByIP();
            }
        }).start();

        CrashHandler handler = CrashHandler.getInstance();
        handler.init(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
