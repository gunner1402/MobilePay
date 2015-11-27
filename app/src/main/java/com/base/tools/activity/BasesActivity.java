package com.base.tools.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.tools.utils.BasesUtils;
import com.oasgames.android.oaspay.R;

public class BasesActivity extends Activity {

	public static final int WAITDAILOG_OPEN = -1;
	public static final int WAITDAILOG_CLOSE = -2;
	
	public static final int HANDLER_RESULT = 0;
	public static final int HANDLER_SUCECCES = 1;
	public static final int HANDLER_FAIL = 2;
	public static final int HANDLER_EXCEPTION = 3;
	public static final int HANDLER_ERROR = 4;
	public static final int HANDLER_EXCEPTION_NETWORK = 5;
	
	private View wait_dialog;
	private boolean isPageClose = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		String languageToLoad = "zh";
//		String coun = "cn";
//		Locale locale = new Locale(languageToLoad, coun);
////		Locale.setDefault(locale);
//		Configuration config = getResources().getConfiguration();
//		DisplayMetrics metrics = getResources().getDisplayMetrics();
////		config.locale = Locale.SIMPLIFIED_CHINESE;
//		config.locale = locale;
//		getResources().updateConfiguration(config, metrics);
		//不显示程序的标题栏
        requestWindowFeature( Window.FEATURE_NO_TITLE );
       
        //不显示系统的标题栏          
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                              WindowManager.LayoutParams.FLAG_FULLSCREEN );
        
//        if(TextUtils.isEmpty(SystemCache.packageName))// 如果为空，重新获取packageName
//        	SystemCache.packageName = getApplicationContext().getPackageName();
        
//		if(null == wait_dialog)
//			wait_dialog = BaseUtils.createWaitDialog(this, -1);
	}

	private void openWaitDialog(){
		if(null == wait_dialog){
			wait_dialog = getLayoutInflater().inflate(BasesUtils.getResourceValue("layout", "common_waiting_anim"), null);
			wait_dialog.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});
			addContentView(wait_dialog, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//			wait_dialog = BaseUtils.createWaitDialog(this, -1);
		}
		if(!wait_dialog.isShown())
			wait_dialog.setVisibility(View.VISIBLE);
		
//		if(!isPageClose)
//			wait_dialog.show();
	}
	
	private void closeWaitDialog(){
		if(wait_dialog != null)
			wait_dialog.setVisibility(View.INVISIBLE);
	}

	public void setWaitScreen(boolean type){
		if(isPageClose){
			return;
		}
		if(type)
			openWaitDialog();
		else
			closeWaitDialog();
	}
	/**
	 * 检测当前页面是否被关闭
	 * @return 返回true为已关闭，返回false为未关闭
	 */
	public boolean isPageClose(){
		return isPageClose;
	}
	
	public void initHead(boolean headIsShowView, boolean backIsShow, OnClickListener backListener, boolean isNeedLogo, String title, boolean toolIsShow, OnClickListener toolsListener){
		View head = findViewById(BasesUtils.getResourceValue("id", "common_head"));
		if(head == null)
			return;
		if(headIsShowView)
			head.setVisibility(View.VISIBLE);
		else
			head.setVisibility(View.GONE);
		View back = findViewById(BasesUtils.getResourceValue("id", "common_head_back"));
		if(backIsShow) {
			back.setVisibility(View.VISIBLE);
			if (null != backListener)
				back.setOnClickListener(backListener);
			else
				back.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						finish();
					}
				});
		}

		TextView tv_logo = (TextView)findViewById(BasesUtils.getResourceValue("id", "common_head_logo"));
		if(isNeedLogo){
			tv_logo.setVisibility(View.VISIBLE);
		}else
			tv_logo.setVisibility(View.GONE);
		
		TextView tv_title = (TextView)findViewById(BasesUtils.getResourceValue("id", "common_head_title"));
		tv_title.setText(title);

		TextView tools = (TextView)findViewById(BasesUtils.getResourceValue("id", "common_head_tool"));
		if(toolIsShow){
			tools.setVisibility(View.VISIBLE);
			if(toolsListener != null)
				tools.setOnClickListener(toolsListener);
		}else
			tools.setVisibility(View.INVISIBLE);


	}
	public void setHeadTitle(String title){
		TextView tv_title = (TextView)findViewById(BasesUtils.getResourceValue("id", "common_head_title"));
		tv_title.setText(title);
	}

	LinearLayout networkErrorView;

	/**
	 * 网络异常时，提示
	 */
	public void showNetWrokError(){
		networkErrorView = (LinearLayout)findViewById(R.id.common_network_error);
		networkErrorView.setVisibility(View.VISIBLE);
		TextView retry = (TextView)findViewById(R.id.common_network_retry);
		retry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				retry();
			}
		});
	}
	public void closeNetWorkError(){
		if(networkErrorView != null)
			networkErrorView.setVisibility(View.INVISIBLE);
	}

	/**
	 * 网络异常时，重试
	 */
	public void retry(){
		closeNetWorkError();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isPageClose = false;
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		isPageClose = true;
		super.onDestroy();
	}
}
