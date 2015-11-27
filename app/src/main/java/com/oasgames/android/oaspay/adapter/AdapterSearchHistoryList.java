package com.oasgames.android.oaspay.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.tools.list.BasesListAdapter;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.entity.SearchInfo;

import java.util.List;


/**
 * 搜索历史记录 列表 适配器
 * @author Administrator
 *
 */
public class AdapterSearchHistoryList extends BasesListAdapter<SearchInfo> {
	Activity c;
	public AdapterSearchHistoryList(Activity activity, List<SearchInfo> data,
									int count, LinearLayout footerView) {
		super(activity, data, count, footerView);
		this.c = activity;
	}

	@Override
	public void loadMore() {
		// no more
	}

	@Override
	public View getRowView(int position, View convertView, ViewGroup parent) {
		ViewHoder hoder = null;
		if(null == convertView){
			convertView = c.getLayoutInflater().inflate(R.layout.page_search_history_list_item, null);
			hoder = new ViewHoder();
			hoder.keyword = (TextView) convertView.findViewById(R.id.search_history_list_item_name);

			convertView.setTag(hoder);
		}else{
			hoder = (ViewHoder) convertView.getTag();
		}

		final SearchInfo info = getItem(position);
		hoder.keyword.setText(info.keyword);
		return convertView;
	}

	static class ViewHoder{
		TextView keyword;

	}
	
}
