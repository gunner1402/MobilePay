package com.oasgames.android.oaspay.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.tools.activity.BasesActivity;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.tools.APPUtils;

/**
 * 界面 新闻详细
 * Created by Administrator on 2015/10/16.
 */
public class ActivityNewsDetails extends BasesActivity implements View.OnTouchListener, GestureDetector.OnGestureListener {
    String newslink;
    WebView webView;

    private LinearLayout layout_fuc;
    private TextView tv_back, tv_forward;
    Boolean isShow = true;
    GestureDetector mGestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_news_details);

        initHead(true, true, null, false, getString(R.string.fragment_news_title), false, null);

        newslink = getIntent().getExtras().getString("link");

        layout_fuc = (LinearLayout) findViewById(R.id.webview_fuc);
        tv_back = (TextView) findViewById(R.id.webview_fuc_back);
        tv_forward = (TextView) findViewById(R.id.webview_fuc_forward);

        initGesture();

        webView = (WebView) findViewById(R.id.news_details_webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setWaitScreen(false);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                setWaitScreen(true);
            }
        });

        //启用支持javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        if (TextUtils.isEmpty(newslink)){
            APPUtils.showErrorMessageByErrorCode(this, "-2000");
            finish();
        }
        webView.loadUrl(TextUtils.isEmpty(newslink)?"http://www.google.com":newslink);

    }
    private void initGesture() {
        mGestureDetector = new GestureDetector((GestureDetector.OnGestureListener) this);
    }

    private void reSetup(){
        if(webView != null && webView.canGoBack())
            tv_back.setBackgroundResource(R.mipmap.webview_fuc_back_able);
        else
            tv_back.setBackgroundResource(R.mipmap.webview_fuc_back_unable);

        if(webView != null && webView.canGoForward())
            tv_forward.setBackgroundResource(R.mipmap.webview_fuc_forward_able);
        else
            tv_forward.setBackgroundResource(R.mipmap.webview_fuc_forward_unable);
    }
    public void onButtonClick_back(View v){
        webView.goBack();
        reSetup();// 设置按钮的状态
    }
    public void onButtonClick_forward(View v){
        webView.goForward();
        reSetup();// 设置按钮的状态
    }
    public void onButtonClick_refresh(View v){
        webView.reload();
    }
    public void showView() {
        if(isShow)
            return;
        layout_fuc.clearAnimation();
        TranslateAnimation animation  = new TranslateAnimation(0, 0, layout_fuc.getHeight(), 0);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(500);
        animation.setStartOffset(0);
        animation.setFillAfter(false);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layout_fuc.setVisibility(View.VISIBLE);
                isShow = true;
            }
        });

        layout_fuc.startAnimation(animation);
    }
    public void hideView() {
        if(!isShow)
            return;
        layout_fuc.clearAnimation();
        TranslateAnimation animation  = new TranslateAnimation(0, 0, 0, layout_fuc.getHeight());
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(500);
        animation.setStartOffset(0);
        animation.setFillAfter(false);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                layout_fuc.setVisibility(View.GONE);
                isShow = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });

        layout_fuc.startAnimation(animation);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        return false;
    }
    @Override
    public void onLongPress(MotionEvent e) {}
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        if (distanceY > 0) {
            hideView();
        } else {
            showView();
        }
        return false;
    }
    @Override
    public void onShowPress(MotionEvent e) {

    }
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
