package com.oasgames.android.oaspay.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.tools.list.BasesListAdapter;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.activity.ActivityPayPackageList;
import com.oasgames.android.oaspay.entity.PayInfoDetail;

import java.util.List;

/**
 * 支付套餐列表
 * @author Administrator
 *
 */
public class AdapterPayPackageList extends BasesListAdapter<PayInfoDetail> {
	ActivityPayPackageList c;
	public AdapterPayPackageList(Activity activity, List<PayInfoDetail> data,
								 int count, LinearLayout footerView) {
		super(activity, data, count, footerView);
		this.c = (ActivityPayPackageList)activity;
	}

	@Override
	public void loadMore() {
		// no more
	}

	@Override
	public View getRowView(int position, View convertView, ViewGroup parent) {
		ViewHolder hoder = null;
		if(null == convertView){
			convertView = c.getLayoutInflater().inflate(R.layout.page_pay_package_list_item, null);
			hoder = new ViewHolder();
			hoder.price = (TextView) convertView.findViewById(R.id.pay_package_list_item_price);
			hoder.coins = (TextView) convertView.findViewById(R.id.pay_package_list_item_diamond_blue);
			hoder.addCoinsLayout = (RelativeLayout) convertView.findViewById(R.id.pay_package_list_item_diamond_red_layout);
			hoder.addCoins = (TextView) convertView.findViewById(R.id.pay_package_list_item_diamond_red);

			convertView.setTag(hoder);
		}else{
			hoder = (ViewHolder) convertView.getTag();
		}
		
		final PayInfoDetail info = getItem(position);
		if(c.selectedPayInfo == info){
			convertView.setBackgroundResource(R.color.common_list_item_bg_selected);
		}else{
			convertView.setBackgroundResource(R.color.common_list_item_bg_unselected);
		}
		hoder.price.setText(info.currency_show + info.amount_show);
		int addCoins = 0;
		try {
			if(!TextUtils.isEmpty(info.price_discount)){
				addCoins = Integer.parseInt(info.price_discount);
			}
		} catch (NumberFormatException e) {
			addCoins = 0;
		}
		hoder.coins.setText(info.game_coins_show);
		
		if(addCoins > 0){
			hoder.addCoins.setText(info.price_discount);
			hoder.addCoinsLayout.setVisibility(View.VISIBLE);
		}else{
			hoder.addCoinsLayout.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}

	static class ViewHolder {
		TextView price;					// 套餐价格
		TextView coins;					// 蓝钻
		RelativeLayout addCoinsLayout;	// 赠送钻石布局
		TextView addCoins;				// 赠送钻石
	}
}
