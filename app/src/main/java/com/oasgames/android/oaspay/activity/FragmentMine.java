package com.oasgames.android.oaspay.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.tools.BasesApplication;
import com.base.tools.entity.UserInfo;
import com.base.tools.utils.BasesUtils;
import com.base.tools.utils.DisplayUtil;
import com.mopub.volley.Response;
import com.mopub.volley.VolleyError;
import com.mopub.volley.toolbox.ImageRequest;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.tools.ReportUtils;


/**
 * 我 功能页
 */
public class FragmentMine extends Fragment {
	final String TAG = FragmentMine.class.getSimpleName();
	Integer[] loginedStr = new Integer[]{R.string.fragment_shop_function_order, R.string.fragment_mine_head_list_2, R.string.fragment_mine_head_list_3};
	Integer[] unLoginStr = new Integer[]{R.string.fragment_mine_head_list_2, R.string.fragment_mine_head_list_3};
	LinearLayout list, touxiang;
	TextView exit, username;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_main_mine, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		list = (LinearLayout)getActivity().findViewById(R.id.fragment_mine_list);
		exit = (TextView)getActivity().findViewById(R.id.fragment_mine_exit);
		username = (TextView) getActivity().findViewById(R.id.fragment_mine_head_username);
		touxiang = (LinearLayout)getActivity().findViewById(R.id.fragment_mine_head_touxiang);
	}
	public void updateUserInfo(){

		if(BasesUtils.isLogin()){// 已登录
			BasesApplication basesApplication = (BasesApplication)getActivity().getApplication();
			UserInfo userInfo = basesApplication.userInfo;
			username.setText(getString(R.string.login_edit_user) +" "+(TextUtils.isEmpty(userInfo.nickname) ? ((TextUtils.isEmpty(userInfo.username)||"null".equals(userInfo.username) || BasesApplication.userInfo.username.contains("@"+BasesApplication.userInfo.platform))?userInfo.uid:userInfo.username) : userInfo.nickname));
			touxiang.getChildAt(0).setVisibility(View.VISIBLE);
			touxiang.getChildAt(1).setVisibility(View.GONE);

			// 设置默认头像
			((ImageView)touxiang.getChildAt(0)).setBackgroundResource(R.mipmap.fragment_mine_head_bg_touxiang_default);
			((ImageView)touxiang.getChildAt(0)).setImageBitmap(null);
			if(!TextUtils.isEmpty(userInfo.avatar_pic)){

				ImageRequest iq = new ImageRequest(userInfo.avatar_pic,

						new Response.Listener<Bitmap>() {
							@Override
							public void onResponse(Bitmap arg0) {
								if(arg0 != null){
									arg0 = BasesUtils.toRoundCorner(arg0, 30);
									((ImageView)touxiang.getChildAt(0)).setImageBitmap(arg0);
									((ImageView)touxiang.getChildAt(0)).postInvalidate();
								}
							}
						},
						DisplayUtil.dip2px(66, BasesUtils.getDisplayMetrics(getActivity()).density), // 以布局文件为准，200dip
						DisplayUtil.dip2px(66, BasesUtils.getDisplayMetrics(getActivity()).density),
						Bitmap.Config.ARGB_8888,
						new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError arg0) {
							}

						});
				BasesApplication.volleyRequestQueue.add(iq);
			}

			exit.setVisibility(View.VISIBLE);
			exit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((MyApplication)getActivity().getApplication()).clearUserInfo();
					updateUserInfo();
				}
			});
			initList(true);
			return;
		}

		exit.setVisibility(View.INVISIBLE);
		username.setText("");

		touxiang.getChildAt(0).setVisibility(View.GONE);
		touxiang.getChildAt(1).setVisibility(View.VISIBLE);
		touxiang.getChildAt(1).setBackgroundResource(R.mipmap.fragment_mine_head_bg_touxiang);
		touxiang.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(BasesUtils.isLogin())
					updateUserInfo();
				else
					startActivity(new Intent().setClass(getActivity(), ActivityLogin.class));
			}
		});
		initList(false);
	}

	private void initList(boolean flag){
		list.removeAllViews();
		Integer[] data = flag?loginedStr:unLoginStr;
		int count = data.length;
		for (int i = 0; i < count; i++) {
			View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_main_mine_item, null);
			TextView tv = (TextView)v.findViewById(R.id.fragment_mine_item_title);

			int str = data[i];
			tv.setText(getResources().getText(str));
			v.setTag(str);
//			if(str == R.string.fragment_mine_head_list_2){
//				try {
//					String size = DataCleanManager.getCacheSizeByThisApp(getActivity());
//					BasesUtils.logError("123321", "123321+=" + size);
//
//					TextView notice = (TextView)v.findViewById(R.id.fragment_mine_item_notice);
//					notice.setText("0.0M");
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
			v.setOnClickListener(new MyListener());
			list.addView(v);
		}
	}

	class MyListener implements View.OnClickListener{
		View view ;
		@Override
		public void onClick(View v) {
			if(BasesUtils.isFastDoubleClick())
				return;
			this.view = v;
			int tag = (int)v.getTag();
			switch (tag){
				case R.string.fragment_shop_function_order:
					startActivityForResult(new Intent().setClass(((ActivityMain)getActivity()), ActivityOrderListSlide.class), 111);
					ReportUtils.add(ReportUtils.DEFAULTEVENT_FMETMYLIST, null, null);
					break;
				case R.string.fragment_mine_head_list_2:
					BasesUtils.showDialogBySystemUI(getActivity(), getString(R.string.fragment_mine_head_list_2_1), getString(R.string.search_title_sub1), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}, getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							view.findViewById(R.id.fragment_mine_item_notice).setVisibility(View.INVISIBLE);
							BasesUtils.showMsg(getActivity(), getString(R.string.fragment_mine_head_list_2_2));
						}
					}, "", null);
					break;
				case R.string.fragment_mine_head_list_3:
					startActivity(new Intent().setClass(getActivity(), ActivityAbout.class));
					break;
			}
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		updateUserInfo();
	}
}
