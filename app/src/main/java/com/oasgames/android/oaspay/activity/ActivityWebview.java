package com.oasgames.android.oaspay.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.base.tools.activity.BasesActivity;
import com.oasgames.android.oaspay.R;

/**
 * 界面 公共界面  （使用协议、忘记密码、注册）
 * Created by Administrator on 2015/10/16.
 */
public class ActivityWebview extends BasesActivity {
    final int TYPE_DEFAULT = -1;
    final int TYPE_USERULE = 0;
    final int TYPE_FORGETPW = 1;
    final int TYPE_USERREGIST = 2;
    WebView webView;
    int type = TYPE_DEFAULT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_common_webview);

        webView = (WebView)findViewById(R.id.common_webview);
        //启用支持javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                setWaitScreen(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setWaitScreen(false);
            }
        });
//        webView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if(keyCode == KeyEvent.KEYCODE_BACK){
//                    if(webView.canGoBack())
//                        webView.goBack();
//                    else
//                        finish();
//                    return true;
//                }
//                return false;
//            }
//        });

        String title = "";
        String url = "";
        type = getIntent().getIntExtra("type", -1);
        switch (type){
            case TYPE_USERULE:
                title = getString(R.string.login_other_rule);
                url = "http://www.baidu.com/";
                break;
            case TYPE_FORGETPW:
                title = getString(R.string.login_other_findpwd);
                url = "http://www.sohu.com/";
                break;
            case TYPE_USERREGIST:
                title = getString(R.string.login_register);
                url = "http://www.sina.com.cn/";
                break;
            case 3:
                break;
            case TYPE_DEFAULT:
            default:
                break;
        }

        initHead(true, true, null, false, title, false, null);

        if(!TextUtils.isEmpty(url))
            webView.loadUrl(url);

    }
}
