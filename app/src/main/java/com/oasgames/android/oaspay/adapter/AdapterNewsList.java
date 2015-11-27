package com.oasgames.android.oaspay.adapter;

import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.tools.list.BasesListAdapter;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.activity.FragmentNews;
import com.oasgames.android.oaspay.entity.NewsInfo;

import java.util.List;

/**
 * 资讯列表
 * @author Administrator
 *
 */
public class AdapterNewsList extends BasesListAdapter<NewsInfo> {
	FragmentNews c;
	public AdapterNewsList(FragmentNews activity, List<NewsInfo> data,
						   int count, LinearLayout footerView) {
		super(activity.getActivity(), data, count, footerView);
		this.c = (FragmentNews)activity;
	}

	@Override
	public void loadMore() {
		// no more
		c.loadMoreNews();
	}

	@Override
	public View getRowView(int position, View convertView, ViewGroup parent) {
		ViewHoder hoder = null;
		if(null == convertView){
			convertView = c.getActivity().getLayoutInflater().inflate(R.layout.fragment_main_news_item, null);
			hoder = new ViewHoder();
			hoder.title = (TextView) convertView.findViewById(R.id.fragment_news_item_title);
			hoder.discrip = (TextView) convertView.findViewById(R.id.fragment_news_item_discrip);
			hoder.time = (TextView) convertView.findViewById(R.id.fragment_news_item_date);
			convertView.setTag(hoder);
		}else{
			hoder = (ViewHoder) convertView.getTag();
		}
		
		final NewsInfo info = getItem(position);
		hoder.title.setText(info.title);
		hoder.discrip.setText(info.summary);
		hoder.time.setText(DateFormat.format("MM-dd", Long.parseLong(info.start_time + "000")));

		return convertView;
	}

	static class ViewHoder{
		TextView title;					// 标题
		TextView discrip;				// 描述
		TextView time;					// 时间
	}
}
