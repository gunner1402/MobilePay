package com.oasgames.android.oaspay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.base.tools.http.CallbackResultForActivity;
import com.base.tools.utils.BasesUtils;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.adapter.AdapterNewsList;
import com.oasgames.android.oaspay.entity.NewsInfo;
import com.oasgames.android.oaspay.entity.NewsList;
import com.oasgames.android.oaspay.service.HttpService;

import java.util.ArrayList;


public class FragmentNews extends Fragment {
	final String TAG = FragmentNews.class.getSimpleName();
	final int PAGESIZE = 20;
	ListView listView;
	AdapterNewsList adapter;

	View root;

	NewsList list = null;
	boolean isLoading = false;
	View main_news_wait;
	View networkError;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		root = inflater.inflate(R.layout.fragment_main_news, null);
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		root.findViewById(R.id.common_head_tool).setVisibility(View.INVISIBLE);
		main_news_wait = getActivity().findViewById(R.id.main_news_wait);
		networkError = getActivity().findViewById(R.id.main_news_network_error);

		listView = (ListView)getActivity().findViewById(R.id.main_news_list);
		adapter = new AdapterNewsList(this, new ArrayList<NewsInfo>(), 1, null);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(BasesUtils.isFastDoubleClick())
					return;
				startActivity(new Intent().setClass(getActivity(), ActivityNewsDetails.class).putExtra("link", adapter.getItem(position).detail_url));
			}
		});
		loadNews();

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void loadNews(){
		main_news_wait.setVisibility(View.VISIBLE);
		HttpService.instance().getNewsList((list==null|| TextUtils.isEmpty(list.cur_page))?1:Integer.valueOf(list.cur_page) + 1, PAGESIZE, new MyCallBack());
	}
	public void loadMoreNews(){
		if(isLoading)
			return;
		isLoading = true;
		loadNews();
	}
	public void retry(){
		loadNews();
	}
	class MyCallBack implements CallbackResultForActivity{
		@Override
		public void success(Object data, int statusCode, String msg) {
			main_news_wait.setVisibility(View.INVISIBLE);

			list = (NewsList)data;
			if(list != null) {
				adapter.currentPage = Integer.valueOf(list.cur_page);
				adapter.pages = Integer.valueOf(list.total_page);

				if (adapter.data == null)
					adapter.data = list.list;
				else
					adapter.data.addAll(list.list);
				adapter.notifyDataSetChanged();
			}
			isLoading = false;
		}

		@Override
		public void fail(int statusCode, String msg) {
			main_news_wait.setVisibility(View.INVISIBLE);
			isLoading = false;
			if(list == null) {
				networkError.setVisibility(View.VISIBLE);
				networkError.findViewById(R.id.common_network_retry).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						networkError.setVisibility(View.INVISIBLE);
						loadNews();
					}
				});
			}
		}

		@Override
		public void exception(Exception e) {
			main_news_wait.setVisibility(View.INVISIBLE);
			isLoading = false;

			if(list == null) {
				networkError.setVisibility(View.VISIBLE);
				networkError.findViewById(R.id.common_network_retry).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						networkError.setVisibility(View.INVISIBLE);
						loadNews();
					}
				});
			}
		}
	}
}
