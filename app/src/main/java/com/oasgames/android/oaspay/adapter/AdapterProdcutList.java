package com.oasgames.android.oaspay.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.tools.list.BasesListAdapter;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.activity.ActivityMain;
import com.oasgames.android.oaspay.activity.ActivityProductDetails;
import com.oasgames.android.oaspay.activity.ActivityProductList;
import com.oasgames.android.oaspay.activity.ActivitySearch;
import com.oasgames.android.oaspay.entity.ProductInfo;

import java.util.List;


/**
 * 商品 礼包 列表 适配器
 * @author Administrator
 *
 */
public class AdapterProdcutList extends BasesListAdapter<ProductInfo> {
	Activity c;
	public AdapterProdcutList(Activity activity, List<ProductInfo> data,
							  int count, LinearLayout footerView) {
		super(activity, data, count, footerView);
		this.c = activity;
	}

	@Override
	public void loadMore() {
		// no more
		if(c instanceof ActivityProductList){
			((ActivityProductList)c).loadDataForNextPage();
		}else if( c instanceof ActivitySearch){
			((ActivitySearch)c).loadSearchResultMore();

		}
	}

	@Override
	public View getRowView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if(null == convertView){
			convertView = c.getLayoutInflater().inflate(R.layout.fragment_main_shop_review_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.product_item_title);
			holder.img = (ImageView) convertView.findViewById(R.id.product_item_img);
			holder.diamond = (TextView) convertView.findViewById(R.id.product_item_diamond_count);
			holder.diamondBg = (TextView) convertView.findViewById(R.id.product_item_diamond_bg);
			holder.pay = (View) convertView.findViewById(R.id.product_item_charge);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		final ProductInfo info = getItem(position);
		holder.title.setText(info.product_name);
		holder.diamond.setText(info.game_coins_show);
		loadImg(holder.img, info.product_img_url);

		if(c instanceof ActivityProductList || c instanceof ActivitySearch){
			holder.pay.setVisibility(View.INVISIBLE);
		}else if(c instanceof ActivityMain){
			holder.pay.setVisibility(View.VISIBLE);
			holder.pay.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					c.startActivity(new Intent().setClass(c, ActivityProductDetails.class).putExtra("id", info.product_id));
				}
			});
		}
		return convertView;
	}

	static class ViewHolder {
		TextView title;
		ImageView img;
		TextView diamond;
		TextView diamondBg;
		View pay;

	}
	
}
