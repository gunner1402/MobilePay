package com.oasgames.android.oaspay.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.base.tools.BasesApplication;
import com.base.tools.activity.BasesActivity;
import com.base.tools.http.CallbackResultForActivity;
import com.base.tools.utils.BasesUtils;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.entity.AppVersionInfo;
import com.oasgames.android.oaspay.service.HttpService;
import com.oasgames.android.oaspay.tools.ReportUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 启动界面
 * Created by Administrator on 2015/10/16.
 */
public class ActivityLogo extends BasesActivity {

    MyHandler myHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_logo);

        myHandler = new MyHandler(new WeakReference<ActivityLogo>(this));

        BasesUtils.getFBKeyHash(this);

        myHandler.sendEmptyMessage(100);
    }
    private void checkAppVersion(){
        HttpService.instance().checkAppVersion(new CheckAppVersionCallback());
    }

    int flag = 0;
    class CheckAppVersionCallback implements CallbackResultForActivity{
        @Override
        public void success(Object data, int statusCode, String msg) {
            final AppVersionInfo version = (AppVersionInfo)data;
            BasesUtils.showDialogBySystemUI(ActivityLogo.this, version.version_intro, version.isForceUpdate()?"":getString(R.string.app_version_8), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        flag = 1;
                        dialog.cancel();
                        testLogin();
                    }
                }, getString(R.string.app_version_7), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        flag = 2;
                        dialog.cancel();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(version.version_url)));// "market://details?id=" + appPackageName
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BasesApplication.packageName)));
                        } catch (Exception anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BasesApplication.packageName)));
                        }
                        finish();
                    }
                }, "", null);
//				String appPackageName = "com.thirdplat.test3";
//				try {
//					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//				} catch (android.content.ActivityNotFoundException anfe) {
//					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//				}

        }

        @Override
        public void fail(int statusCode, String msg) {
            myHandler.sendEmptyMessage(101);
        }

        @Override
        public void exception(Exception e) {
            myHandler.sendEmptyMessage(101);
        }
    }

    /**
     * 尝试登录，登录TOKEN
     */
    private void testLogin(){
        HttpService.instance().loginByToken(new LoginCallBack());
    }

    class LoginCallBack implements CallbackResultForActivity{
        @Override
        public void success(Object data, int statusCode, String msg) {
            Map<String, String> paras = new HashMap<>();
            paras.put("logintype", BasesUtils.isLogin() ? BasesApplication.userInfo.platform : "loginno");
            ReportUtils.add(ReportUtils.DEFAULTEVENT_LOGIN, paras, null);
            myHandler.sendEmptyMessage(102);

        }

        @Override
        public void fail(int statusCode, String msg) {
            myHandler.sendEmptyMessage(102);
            Map<String, String> paras = new HashMap<>();
            paras.put("logintype", "loginno");
            ReportUtils.add(ReportUtils.DEFAULTEVENT_LOGIN, paras, null);
        }

        @Override
        public void exception(Exception e) {
            myHandler.sendEmptyMessage(102);
            Map<String, String> paras = new HashMap<>();
            paras.put("logintype", "loginno");
            ReportUtils.add(ReportUtils.DEFAULTEVENT_LOGIN, paras, null);
        }
    }
    private static class MyHandler extends Handler{
        //使用弱引用避免Handler泄露.这里的泛型参数可以不是Activity，也可以是Fragment等
        private WeakReference<ActivityLogo> weakReference;

        protected MyHandler(WeakReference<ActivityLogo> wk){
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    weakReference.get().checkAppVersion();
                    break;
                case 101:
                    weakReference.get().testLogin();
                    break;
                case 102:
                    weakReference.get().startActivity(new Intent().setClass(weakReference.get(), ActivityMain.class));
                    weakReference.get().finish();
                    break;
                default:
                    break;
            }
        }
    }

}
