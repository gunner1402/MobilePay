package com.oasgames.android.oaspay.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.tools.BasesApplication;
import com.base.tools.activity.BasesActivity;
import com.base.tools.entity.MemberBaseInfo;
import com.base.tools.http.CallbackResultForActivity;
import com.base.tools.utils.AESUtils;
import com.base.tools.utils.BasesUtils;
import com.base.tools.utils.DisplayUtil;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.adapter.LoginUserListAdapter;
import com.oasgames.android.oaspay.service.HttpService;
import com.oasgames.android.oaspay.tools.APPUtils;
import com.oasgames.android.oaspay.tools.FacebookUtils;
import com.oasgames.android.oaspay.tools.GoogleUtils;
import com.oasgames.android.oaspay.tools.ReportUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityLogin extends BasesActivity implements
GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener{

	private static final String TAG = ActivityLogin.class.getSimpleName();
	
	private static final int HANDLER_RESULT_REGIST = 10;
	private static final int HANDLER_SHOWVIEW = 200;
	private static final int UITYPE_PAGEAUTOLOGIN = -1;//自动登录
	private static final int UITYPE_PAGELOGINSELECT = 0;//登录方式选择界面
	private static final int UITYPE_PAGELOGININPUT = 1;//登录账号输入界面
	private static final int UITYPE_PAGEREGIST = 2;//OAS账号注册界面
	private static final int UITYPE_FACEBOOK = 3;//Facebook登录界面
	private static final int UITYPE_CHANGEUSER = 4;//用户切换首页
	
	
	private View curView;
	private TextView btnMoreUsers;
	private EditText et_login_u, et_login_p;
	private String username;
	private String password;

	private FacebookUtils fb;
	
	// 声明一个Handler对象
	public MyHandler myHandler = null;
//	private FacebookCallback fbCallback = null;
	
	List<MemberBaseInfo> listUsersLogined;
	PopupWindow pupWindow;
	LoginUserListAdapter adapter;
	
	/* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    private Boolean mIntentInProgress = false;
    private Boolean mSignInClicked = false;
    private Boolean mAuthException = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_login);
		initHead(true, true, null, true, getString(R.string.fragment_mine_head_login), false, null);
		myHandler = new MyHandler(this);
		
		fb = new FacebookUtils(this);
		fb.setFacebookCallbackInterface(new FacebookCallbackImpl(this));

		init();// 获取历史登录用户

        mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(new Scope("profile"))
        .build();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(fb != null)
			fb.onActivityResult(requestCode, resultCode, data);
		

		if (requestCode == RC_SIGN_IN) {
		    if (resultCode != RESULT_OK) {
		    	// 可以增加“登录失败”提示
		    	mSignInClicked = false;
		    	mIntentInProgress = false;
		    	mAuthException = false;
		    	setWaitScreen(false);
		    	return;
		    }

		    mIntentInProgress = false;

		    if (!mGoogleApiClient.isConnected()) {// 第一次点击总是无法点击成功，需要reconnect
		    	mGoogleApiClient.reconnect();
		    }
		  }
		if(requestCode == GoogleUtils.REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR){
			if (resultCode != RESULT_OK) {
				// 可以增加“授权失败”提示
				mSignInClicked = false;
				mIntentInProgress = false;
				mAuthException = false;
				setWaitScreen(false);
				return;
			}
			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnected()) {
				mGoogleApiClient.reconnect();
			}
		}
	}
	public void buttonOnClick(View v) {
		if(v == null)
			return;
		
		if(v.getId() != R.id.login_edit_moreuser && null != pupWindow && pupWindow.isShowing()){
			pupWindow.dismiss();
		}

		switch (v.getId()){
			case R.id.login_edit_moreuser:
				if(null == listUsersLogined || listUsersLogined.size() <= 0)
					return;
				popUserListWindow();
				break;
			case R.id.login_btn_submit:
				InputMethodManager imm = (InputMethodManager)et_login_u.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(et_login_u.getWindowToken(), 0);
				if(!check(false))
					return;
				mSignInClicked = false;
				if(check(true))
					loginByOAS();
				break;
			case R.id.login_btn_rule:
				openBrowser(0);
//				startActivity(new Intent().setClass(this, ActivityWebview.class).putExtra("type", 0));
				break;
			case R.id.login_btn_find_password:
				openBrowser(1);
//				startActivity(new Intent().setClass(this, ActivityWebview.class).putExtra("type", 1));
				break;
			case R.id.login_btn_otherstyle_facebook:
				mSignInClicked = false;
				FacebookUtils.logout();
				fb.login(this);
				curView = null;
				break;
			case R.id.login_btn_otherstyle_google:
				if (mGoogleApiClient.isConnected())
					mGoogleApiClient.clearDefaultAccountAndReconnect();

				setWaitScreen(true);
//			if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting()) {
				mSignInClicked = true;
				mGoogleApiClient.connect();
//			}else{
//				getProfileInformation();
//			}
				curView = null;
				break;
			case R.id.login_btn_register:// 跳链接
				openBrowser(2);
//				startActivity(new Intent().setClass(this, ActivityWebview.class).putExtra("type", 2));
				break;
		}

	}

	private void openBrowser(int type){
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = null;
		switch (type){
			case 0:// 协议条款
				content_url = Uri.parse("http://www.oasgames.com/TermsofService(AR).html");
				break;
			case 1:// 忘记密码
				content_url = Uri.parse("http://www.oasgames.com/?a=ucenter&m=findpwd&lang=ar");
				break;
			case 2:// 注册
				content_url = Uri.parse("http://www.oasgames.com/?a=widget&m=mob_reg_page&page_back=no&lang=ar-ar&third_login=no");
				break;
		}
		intent.setData(content_url);
		startActivity(intent);
	}
	/**
	 * 各数据项合法性验证
	 * @return
	 */
	private boolean check(boolean isNotice){
		username = et_login_u.getText().toString().trim();
		password = et_login_p.getText().toString().trim();

		if(TextUtils.isEmpty(username)){
			if (isNotice)
				BasesUtils.showMsg(this, getResources().getString(BasesUtils.getResourceValue("string", "oasisgames_sdk_login_hint_username")));
			return false;
		}
//		if(username.length() < 6 || username.length() > 50){
//			BasesUtils.showMsg(this, getResources().getString(BasesUtils.getResourceValue("string", "oasisgames_sdk_login_username_notice_error_length")));
//			return false;
//		}
		if(username.contains("@") && !BasesUtils.regexEmail(username)){//包含@符，并且不符合邮箱规则
			if (isNotice)
				BasesUtils.showMsg(this, getResources().getString(BasesUtils.getResourceValue("string", "oasisgames_sdk_login_username_notice_error")));
			return false;
		}else if(!username.contains("@")){//不包含@符，是普通账号
			if(BasesUtils.regexNum(username)){// 账号格式验证,不能为纯数字
				if (isNotice)
					BasesUtils.showMsg(this, getResources().getString(BasesUtils.getResourceValue("string", "oasisgames_sdk_login_username_notice_error1")));
				return false;
			}else if(!BasesUtils.regexAccount(username)){// 账号格式验证,只能包含 a-zA-Z0-9_
				if (isNotice)
					BasesUtils.showMsg(this, getResources().getString(BasesUtils.getResourceValue("string", "oasisgames_sdk_login_username_notice_error2")));
				return false;
			}
		}
		if(TextUtils.isEmpty(password)){
			if (isNotice)
				BasesUtils.showMsg(this, getResources().getString(BasesUtils.getResourceValue("string", "oasisgames_sdk_login_hint_password")));
			return false;
		}
		if(password.length() < 6 || password.length() > 15){
			if (isNotice)
				BasesUtils.showMsg(this, getResources().getString(BasesUtils.getResourceValue("string", "oasisgames_sdk_login_password_notice_error")));
			return false;
		}
		return true;
	}
	
	static class FacebookCallbackImpl implements FacebookUtils.FacebookCallbackInterface {

		// WeakReference to the outer class's instance.
		private WeakReference<ActivityLogin> mOuter;

		public FacebookCallbackImpl(ActivityLogin activity) {
			mOuter = new WeakReference<ActivityLogin>(activity);
		}

		@Override
		public void onSuccess(final LoginResult loginResult) {
			mOuter.get().myHandler.sendEmptyMessage(102);// loginFB
		}

		@Override
		public void onCancel() {
			BasesUtils.logDebug(TAG, "============FB login onCancel()");
		}

		@Override
		public void onError(FacebookException exception) {
			APPUtils.showErrorMessageByErrorCode(mOuter.get(), "-2000");
			BasesUtils.logDebug(TAG, "============FB login onError()");
		}
	}

	private void loginByFB(){
		setWaitScreen(true);

		Profile pro = Profile.getCurrentProfile();
		HttpService.instance().login(MemberBaseInfo.USER_FACEBOOK, MemberBaseInfo.USER_FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), pro != null ? pro.getName() : "", new MyCallBackLogin());
	}
	private void loginByGoogle(final String oasnickname, final String email, final String token){
		setWaitScreen(true);
		HttpService.instance().login(MemberBaseInfo.USER_GOOGLE, email, token, oasnickname, new MyCallBackLogin());
	}
	private void loginByOAS(){
		setWaitScreen(true);
		HttpService.instance().login(MemberBaseInfo.USER_REGISTED, username, password, username, new MyCallBackLogin());
//					if(Session.getActiveSession()!=null)
//						Session.getActiveSession().closeAndClearTokenInformation();
	}

	class MyCallBackLogin implements CallbackResultForActivity{
		@Override
		public void success(Object data, int statusCode, String msg) {
			myHandler.sendEmptyMessage(HANDLER_RESULT);
		}

		@Override
		public void exception(Exception e) {
			myHandler.sendEmptyMessage(HANDLER_EXCEPTION_NETWORK);
		}

		@Override
		public void fail(int statusCode, String msg) {
			myHandler.sendEmptyMessage(HANDLER_EXCEPTION);
		}
	}

	public static class MyHandler extends Handler {

		// WeakReference to the outer class's instance.
		private WeakReference<ActivityLogin> mOuter;

		public MyHandler(ActivityLogin activity) {
			mOuter = new WeakReference<ActivityLogin>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			ActivityLogin outer = mOuter.get();
			if (outer != null) {
				switch (msg.what) {
				case WAITDAILOG_OPEN:
					outer.setWaitScreen(false);
					break;
				case WAITDAILOG_CLOSE:
					outer.setWaitScreen(false);
					break;
				case HANDLER_RESULT:
					outer.setWaitScreen(false);
					/*if(MemberBaseInfo.USER_FACEBOOK.equals(BasesApplication.userInfo.platform)){
						FacebookUtils.logout();
					}else */if(MemberBaseInfo.USER_GOOGLE.equals(BasesApplication.userInfo.platform)){
						if(outer.mGoogleApiClient.isConnected())
							// 清除默认账号，在切换时重新选择登录的账号
							outer.mGoogleApiClient.clearDefaultAccountAndReconnect().setResultCallback(new ResultCallback<Status>() {
								
								@Override
								public void onResult(Status arg0) {
									System.out.println(arg0.isSuccess()+"  "+arg0.getStatusMessage());
								}
							});
					}
					if(null != BasesApplication.userInfo && "ok".equals(BasesApplication.userInfo.status)){
//						BasesUtils.showMsg(outer, outer.getResources().getString(BasesUtils.getResourceValue("string", "oasisgames_sdk_login_result_1")));

						APPUtils.clearInfoForLogout();// 登录、切换成功后，清楚服id、角色id
						outer.myHandler.sendEmptyMessage(HANDLER_SUCECCES);
					}else{
						APPUtils.showErrorMessageByErrorCode(outer, BasesApplication.userInfo.error);
						/*if("-4".equals(BasesApplication.userInfo.error)){
							APPUtils.showErrorMessageByErrorCode(outer, BasesApplication.userInfo.error);
						}else if("-18".equals(BasesApplication.userInfo.error)){// 第三方token失效
							APPUtils.showDisableDialog(outer, "oasisgames_sdk_error_exception");
						}else if("-13".equals(BasesApplication.userInfo.error)){
							APPUtils.showDisableDialog(outer, "oasisgames_sdk_common_errorcode_negative_13");
						}else if("-14".equals(BasesApplication.userInfo.error)){
							if(!outer.UITypeRank.isEmpty() && outer.UITypeRank.get(0) == UITYPE_CHANGEUSER)
								APPUtils.showDisableDialog(outer, "oasisgames_sdk_login_notice_14");//(切换的账号被封)
							else
								APPUtils.showDisableDialog(outer, "oasisgames_sdk_common_errorcode_negative_14");
						}else{
							BasesUtils.showMsg(outer, outer.getString(BasesUtils.getResourceValue("string", "oasisgames_sdk_common_errorcode_negative_999")) + ".Error code:" + BasesApplication.userInfo.error);
						}*/
					}
						
					break;
				case HANDLER_SUCECCES:
					outer.setResult(Activity.RESULT_OK);// 为“来源是否是个人中心”提供判断依据

					Map<String, String> paras = new HashMap<>();
					paras.put("logintype", BasesUtils.isLogin() ? BasesApplication.userInfo.platform : "loginno");
					ReportUtils.add(ReportUtils.DEFAULTEVENT_LOGIN, paras, null);

					outer.finish();
					break;
				case HANDLER_FAIL:
					
					break;
				case HANDLER_EXCEPTION:
					outer.setWaitScreen(false);
					BasesUtils.showMsg(outer, outer.getResources().getString(R.string.common_nowifi));
					break;
				case HANDLER_EXCEPTION_NETWORK:
					outer.setWaitScreen(false);
					BasesUtils.showMsg(outer, outer.getResources().getString(R.string.common_nowifi));
					break;
				case 101:
					String[] data = ((String)msg.obj).split("oasistag");
					outer.loginByGoogle(data[0], data[1], data[2]);
					
					outer.mSignInClicked = false;// 状态重置
					break;
				case 102:
					mOuter.get().loginByFB();
					break;
				default:
					
					break;
				}
			}
		}
	}

	private void init(){

		listUsersLogined = BasesUtils.getSPMembers();
		et_login_u = (EditText) findViewById(R.id.login_edit_user);
		et_login_p = (EditText) findViewById(R.id.login_edit_password);
		btnMoreUsers = (TextView)findViewById(R.id.login_edit_moreuser);

		et_login_u.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1 && null != pupWindow && pupWindow.isShowing()) {
					pupWindow.dismiss();
				}
			}
		});
		et_login_p.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1 && null != pupWindow && pupWindow.isShowing()) {
					pupWindow.dismiss();
				}
			}
		});
		et_login_u.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				checkSubmitButtonStatus();
			}
		});
		et_login_p.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				checkSubmitButtonStatus();
			}
		});

		
		if(null == listUsersLogined || listUsersLogined.size() <= 0){
			btnMoreUsers.setVisibility(View.GONE);
		}else{
			if(listUsersLogined.size() == 1){
				btnMoreUsers.setVisibility(View.GONE);
			}
			showUserInfo(listUsersLogined.get(0));
		}

		checkSubmitButtonStatus();
				
	}
	private void checkSubmitButtonStatus(){
		if(check(false)){
			findViewById(R.id.login_btn_submit).setBackgroundResource(R.drawable.common_button_1_selector);
		}else
			findViewById(R.id.login_btn_submit).setBackgroundResource(R.drawable.common_bg_a5a4a4);

	}
	private void popUserListWindow(){
		if(null != pupWindow && pupWindow.isShowing()){ 
			pupWindow.dismiss();
			return;
		}
		
		// 下拉框展开时，密码输入框获得焦点
		et_login_p.requestFocus();
		
		View pupView = this.getLayoutInflater().inflate(R.layout.page_login_user_list, null);
		pupWindow = new PopupWindow(pupView, ((RelativeLayout)et_login_u.getParent()).getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
		pupWindow.setOutsideTouchable(false);
		pupWindow.setFocusable(false);
		pupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				btnMoreUsers.setBackgroundResource(R.mipmap.common_arrow_down);
			}
		});
		ListView lv = (ListView) pupView.findViewById(R.id.login_user_list);

		adapter = new LoginUserListAdapter(this, listUsersLogined, 1, null);
		lv.setAdapter(adapter);

		pupWindow.showAsDropDown(et_login_u, 0, DisplayUtil.dip2px(5, BasesUtils.getDisplayMetrics(this).density));

		btnMoreUsers.setBackgroundResource(R.mipmap.common_arrow_up);
	}
	public void showUserInfo(MemberBaseInfo info){
		et_login_u.setText(info.memberName);
		et_login_p.setText(AESUtils.decrypt(info.password));
		if(null != pupWindow && pupWindow.isShowing())
			pupWindow.dismiss();
	}

	public boolean onTouchEvent(MotionEvent event) {
		   if (pupWindow != null && pupWindow.isShowing()) {
			   pupWindow.dismiss();
		   }
		   return super.onTouchEvent(event);
		}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	}

	/**
	 * 用户取消操作
	 */
	private void setResultForCancle(){
		setResult(Activity.RESULT_CANCELED);
	}

	class MyGoogleLoginCallback implements GoogleUtils.GoogleLoginCallback{
		@Override
		public void success(Person p, String email, String token) {
			String personName = "";
			if(p != null)
				personName = p.getDisplayName();
			System.out.println("========Name="+personName +"; email="+email+";  token="+token);
//			loginByGoogle(personName, email, token);
			Message msg = new Message();
			msg.what = 101;
			msg.obj = personName+"oasistag"+email+"oasistag"+token;
			myHandler.sendMessage(msg);
		}

		@Override
		public void exception(Exception e) {
			
			if(e instanceof UserRecoverableAuthException){
				Log.e(TAG, "Google Exception:UserRecoverableAuthException ");
				e.printStackTrace();
				Intent intent = ((UserRecoverableAuthException)e).getIntent();
                startActivityForResult(intent, GoogleUtils.REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                mAuthException = true;
			}else if(e instanceof GoogleAuthException){
				Log.e(TAG, "Google Exception:GoogleAuthException ");
				e.printStackTrace();
				myHandler.sendEmptyMessage(HANDLER_EXCEPTION);
				mSignInClicked = false;
				mAuthException = false;
			}else if(e instanceof IOException){
				Log.e(TAG, "Google Exception:IOException ");
				e.printStackTrace();
				myHandler.sendEmptyMessage(HANDLER_EXCEPTION);
				mSignInClicked = false;
				mAuthException = false;
			}
			
		}
	}
	/**
	 * Fetching user's information name, email, profile pic
	 * */
	private void getProfileInformation() {
		final MyGoogleLoginCallback callback = new MyGoogleLoginCallback();
	    	final Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
	    	final String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
	    	BasesUtils.logDebug(TAG, "email: " + email);
	    	if(!TextUtils.isEmpty(email)){
	    		new Thread(new Runnable() {
					
					@Override
					public void run() {
						String token;
						try {
							token = GoogleAuthUtil.getToken(ActivityLogin.this, email, "oauth2:"+Scopes.PROFILE+" https://www.googleapis.com/auth/userinfo.profile");
							BasesUtils.logDebug(TAG, "token: " + token);
							
							callback.success(currentPerson, email, token);
						} catch(Exception e){
							callback.exception(e);
						}
						
					}
				}).start();
	    	}
	        if (currentPerson != null) {
	            
	            String personName = currentPerson.getDisplayName();
	            String personPhotoUrl = currentPerson.getImage().getUrl();
	            String personGooglePlusProfile = currentPerson.getUrl();
	 
	            Log.d(TAG, "Name: " + personName + ", plusProfile: "
	                    + personGooglePlusProfile + ", email: " + email
	                    + ", Image: " + personPhotoUrl);
	 
//	            txtName.setText(personName);
//	            txtEmail.setText(email);
	 
	            // by default the profile url gives 50x50 px image only
	            // we can replace the value with whatever dimension we want by
	            // replacing sz=X
//	            personPhotoUrl = personPhotoUrl.substring(0,
//	                    personPhotoUrl.length() - 2)
//	                    + PROFILE_PIC_SIZE;
//	 
//	            new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);
	 
	        } 
	}

	@Override
	protected void onResume() {
		if(curView != null)
			buttonOnClick(curView);
		super.onResume();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		/*注释此代码，是不想一进来就connect，希望是用户想connect时再执行
		 * if(!mAuthException)
		 * 	mGoogleApiClient.connect();
		*/	
	}

	@Override
	protected void onStop() {
		super.onStop();
		mGoogleApiClient.disconnect();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		myHandler.removeCallbacksAndMessages(null);
	}
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!mIntentInProgress) {
			if (mSignInClicked) {
			    if (result.hasResolution()) {
			      // The user has already clicked 'sign-in' so we attempt to resolve all
			      // errors until the user is signed in, or they cancel.
			      try {
			        result.startResolutionForResult(this, RC_SIGN_IN);
			        mIntentInProgress = true;
			      } catch (SendIntentException e) {
			        // The intent was canceled before it was sent.  Return to the default
			        // state and attempt to connect to get an updated ConnectionResult.
						mIntentInProgress = false;
						mAuthException = false;
						mSignInClicked = false;
						setWaitScreen(false);
						myHandler.sendEmptyMessage(HANDLER_EXCEPTION);
			      }
			    }else{
			    	mIntentInProgress = false;
			    	mAuthException = false;
			    	mSignInClicked = false;
			    	setWaitScreen(false);
			    	myHandler.sendEmptyMessage(HANDLER_EXCEPTION);
			    }
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		if(mSignInClicked){
			getProfileInformation();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
	}
}
