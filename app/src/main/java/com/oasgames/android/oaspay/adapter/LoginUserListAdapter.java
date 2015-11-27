package com.oasgames.android.oaspay.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.tools.entity.MemberBaseInfo;
import com.base.tools.list.BasesListAdapter;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.activity.ActivityLogin;

import java.util.List;


/**
 * 已登录用户列表，用于登录页面展示
 * @author Administrator
 *
 */
public class LoginUserListAdapter extends BasesListAdapter<MemberBaseInfo> {
	ActivityLogin c;
	public LoginUserListAdapter(Activity activity, List<MemberBaseInfo> data,
			int count, LinearLayout footerView) {
		super(activity, data, count, footerView);
		this.c = (ActivityLogin)activity;
	}

	@Override
	public void loadMore() {
		// no more
	}

	@Override
	public View getRowView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(null == convertView){
			convertView = c.getLayoutInflater().inflate(R.layout.page_login_user_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.login_user_list_item_name);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		final MemberBaseInfo info = getItem(position);
		holder.name.setText(info.memberName);
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				c.showUserInfo(info);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		TextView name;
	}
}
