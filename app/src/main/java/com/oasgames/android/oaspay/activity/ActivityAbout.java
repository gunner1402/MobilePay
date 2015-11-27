package com.oasgames.android.oaspay.activity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.base.tools.activity.BasesActivity;
import com.oasgames.android.oaspay.R;

/**
 * 界面 关于我们
 * Created by Administrator on 2015/10/16.
 */
public class ActivityAbout extends BasesActivity {
    WebView webView;
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_about);
        initHead(true, true, null, false, getString(R.string.fragment_mine_head_list_3),false,null);

        webView = (WebView)findViewById(R.id.about_webview);
        webView.loadUrl("file:///android_asset/about.html");

        //启用支持javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
    }
}
