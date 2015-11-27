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
 * 商城 最新 最热 列表 适配器
 * @author Administrator
 *
 */
public class AdapterShopNewestHotProductList extends BasesListAdapter<ProductInfo> {
	Activity c;
	ViewHoder hoder = null;
	public AdapterShopNewestHotProductList(Activity activity, List<ProductInfo> data,
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
		ViewHoder holder = null;
		if(null == convertView){
			convertView = c.getLayoutInflater().inflate(R.layout.fragment_main_shop_category_item, null);
			holder = new ViewHoder();
			holder.title = (TextView) convertView.findViewById(R.id.fragment_shop_category_item_title);
			holder.img = (ImageView) convertView.findViewById(R.id.fragment_shop_category_item_image);
			holder.diamond = (TextView) convertView.findViewById(R.id.fragment_shop_category_item_diamond);

			convertView.setTag(holder);
		}else{
			holder = (ViewHoder) convertView.getTag();
		}

		final ProductInfo info = getItem(position);
		holder.title.setText(info.product_name);
		holder.diamond.setText(info.game_coins_show);
		loadImg(holder.img, info.product_img_url);
		return convertView;
	}

	static class ViewHoder{
		TextView title;
		ImageView img;
		TextView diamond;

	}
}
