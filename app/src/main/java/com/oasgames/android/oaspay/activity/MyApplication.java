package com.oasgames.android.oaspay.activity;

import android.text.TextUtils;
import android.util.Log;

import com.base.tools.BasesApplication;
import com.base.tools.entity.PhoneInfo;
import com.base.tools.service.BasesServiceLogin;
import com.base.tools.utils.BasesConstant;
import com.base.tools.utils.BasesUtils;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.oasgames.android.oaspay.entity.SearchKeywordInfo;

import java.util.List;

/**
 * Created by Administrator on 2015/10/29.
 */
public class MyApplication extends BasesApplication {
    final String TAG = MyApplication.class.getSimpleName();

    boolean isReLoadOderList = true;// 默认为true，加载过一次后为false，当某订单有变化时为true
    List<SearchKeywordInfo> keywordInfoList;// 搜索关键词集合，每次应用生命周期内，只取一次
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }
    private void init(){
    // 从meta中获取 gamecode,publickey, paykey,这些值由游戏端在清单中配置。
        String str = null;
        try {
            PhoneInfo.instance().setMobilecode(BasesUtils.getMobileCode());
            str = getResources().getString(BasesUtils.getResourceValue("string", "app_code"));
            if(str != null && !TextUtils.isEmpty(str)){
                PhoneInfo.instance().setGamecode(str);
            }else
                Log.e(TAG, "app_code don't setup!");

            str = getResources().getString(BasesUtils.getResourceValue("string", "app_mdata_appid"));
            if(str != null && !TextUtils.isEmpty(str)){
                PhoneInfo.instance().setMdataAppID(str);
            }else
                Log.e(TAG, "app_code don't setup!");

            str = getResources().getString(BasesUtils.getResourceValue("string", "app_publickey"));
            if(str != null && !TextUtils.isEmpty(str)){
                PUBLICKEY = str;
            }else
                Log.e(TAG, "PublicKey don't setup!");

            str = getResources().getString(BasesUtils.getResourceValue("string", "app_environment"));
            if(str != null && !TextUtils.isEmpty(str)){
                if(!TextUtils.isEmpty(str) && BasesConstant.ENVIRONMENT_SANDBOX.equals(str))
                    OASISSDK_ENVIRONMENT_SANDBOX = true;
                else
                    OASISSDK_ENVIRONMENT_SANDBOX = false;
            }else
                Log.e(TAG, "Environment don't setup!");

            str = getResources().getString(BasesUtils.getResourceValue("string", "app_mode"));
            if(str != null && !TextUtils.isEmpty(str)){
                if(!TextUtils.isEmpty(str) && BasesConstant.MODE_OFFLINE.equals(str))
                    MODE = BasesConstant.MODE_OFFLINE;
                else
                    MODE = BasesConstant.MODE_ONLINE;
            }else
                Log.e(TAG, "Environment don't setup!");

        } catch (Exception e1) {
            Log.e(TAG, "Init is fail");
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplicationContext());
    }
    public void clearUserInfo(){
        userInfo = null;
        new BasesServiceLogin().deleteCacheUserInfo();
    }
}

