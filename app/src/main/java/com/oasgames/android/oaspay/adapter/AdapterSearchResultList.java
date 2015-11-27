package com.oasgames.android.oaspay.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.tools.list.BasesListAdapter;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.entity.ProductInfo;

import java.util.List;


/**
 * 搜索结果 列表 适配器
 * @author Administrator
 *
 */
public class AdapterSearchResultList extends BasesListAdapter<ProductInfo> {
	Activity c;
	public AdapterSearchResultList(Activity activity, List<ProductInfo> data,
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
			convertView = c.getLayoutInflater().inflate(R.layout.fragment_main_shop_review_item, null);
			hoder = new ViewHoder();
			hoder.title = (TextView) convertView.findViewById(R.id.product_item_title);
			hoder.img = (ImageView) convertView.findViewById(R.id.product_item_img);
			hoder.diamond = (TextView) convertView.findViewById(R.id.product_item_diamond_count);
			hoder.pay = (View) convertView.findViewById(R.id.product_item_charge);
			convertView.setTag(hoder);
		}else{
			hoder = (ViewHoder) convertView.getTag();
		}

		final ProductInfo info = getItem(position);
		hoder.title.setText(info.product_name);
		hoder.pay.setVisibility(View.INVISIBLE);
		return convertView;
	}

	static class ViewHoder{
		TextView title;
		ImageView img;
		TextView diamond;
		View pay;


	}
	
}
