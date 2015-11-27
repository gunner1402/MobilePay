package com.oasgames.android.oaspay.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.tools.slide.SlideBaseAdapter;
import com.base.tools.slide.SlideListView.SlideMode;
import com.base.tools.utils.BasesUtils;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.activity.ActivityOrderListSlide;
import com.oasgames.android.oaspay.entity.OrderInfo;
import com.oasgames.android.oaspay.entity.OrderList;

public class AdapterOrderListSlide extends SlideBaseAdapter {
	public OrderList mData;
	private ActivityOrderListSlide activity;

	public AdapterOrderListSlide(Context context, OrderList data) {
		super(context);
		activity = (ActivityOrderListSlide)context;
		mData = data;
	}

	
	@Override
	public SlideMode getSlideModeInPosition(int position) {
//		if (position == 1) {
//			return SlideMode.LEFT;
//		}
//		if (position == 2) {
//			return SlideMode.NONE;
//		}
		return super.getSlideModeInPosition(position);
	}

	@Override
	public int getFrontViewId(int position) {
		return R.layout.page_order_list_slide_item;
	}

	@Override
	public int getLeftBackViewId(int position) {
//		if (position % 2 == 0) {
			return R.layout.row_left_back_view;
//		}
//		return R.layout.row_right_back_view;
	}

	@Override
	public int getRightBackViewId(int position) {
		return R.layout.page_order_list_slide_item_back_right;
	}

	@Override
	public int getItemViewType(int position) {
//		if (position % 2 == 0) {
//			return 0;
//		}
		return 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		return mData.list.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(position == getCount()-1 && Integer.valueOf(mData.total_page) > Integer.valueOf(mData.cur_page)){
			activity.loadMore();
		}
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = createConvertView(position);
			holder = new ViewHolder();
			holder.orderid = (TextView) convertView.findViewById(R.id.order_list_item_orderid);
			holder.orderstatus = (TextView) convertView.findViewById(R.id.order_list_item_orderstate);
			holder.title = (TextView) convertView.findViewById(R.id.order_list_item_title);
			holder.money = (TextView) convertView.findViewById(R.id.order_list_item_money);
			holder.diamond = (TextView) convertView.findViewById(R.id.order_list_item_diamondcount);
			holder.diamondBg = (TextView) convertView.findViewById(R.id.order_list_item_diamondbg);
			holder.image = (ImageView) convertView.findViewById(R.id.order_list_item_image);
			holder.topay = (TextView) convertView.findViewById(R.id.order_list_item_topay);
			holder.cancel = (TextView) convertView.findViewById(R.id.order_list_item_cancel);
			holder.delete = (TextView) convertView.findViewById(R.id.order_list_item_delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final OrderInfo info = (OrderInfo)getItem(position);
		holder.orderid.setText(activity.getString(R.string.order_details_id) + "  "+info.order_id);
		if("1".equals(info.pay_status) || "3".equals(info.pay_status)) {//等待支付
			holder.orderstatus.setText(activity.getString(R.string.order_details_label_8));
			holder.orderstatus.setTextColor(activity.getResources().getColor(R.color.common_font_color_fb8f03));
		}else if("2".equals(info.pay_status)) {//已完成
			holder.orderstatus.setText(activity.getString(R.string.order_details_label_7));
			holder.orderstatus.setTextColor(activity.getResources().getColor(R.color.common_font_color_000000));
		}

		holder.title.setText(info.product_name);
		holder.money.setText(info.currency_show + info.amount_show);
		holder.diamond.setText(info.game_coins_show);
		if("payapp".equals(info.order_type)) {
			String disconunt = info.price_discount;
			if(disconunt != null && !"null".equals(disconunt) && !TextUtils.isEmpty(disconunt))
				holder.title.setText(info.price_discount + " + " + info.game_coins_show);
			else
				holder.title.setText(info.game_coins_show);
			holder.diamond.setVisibility(View.INVISIBLE);
			holder.diamondBg.setVisibility(View.INVISIBLE);

			holder.image.setImageResource(R.mipmap.common_diamond_bg);
		}else {
			BasesUtils.loadImg(activity, holder.image, info.product_img_url);
			holder.diamond.setVisibility(View.VISIBLE);
			holder.diamondBg.setVisibility(View.VISIBLE);
		}

		if("1".equals(info.pay_status))
			holder.topay.setVisibility(View.VISIBLE);
		else
			holder.topay.setVisibility(View.GONE);
		if (holder.topay != null) {
			holder.topay.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					activity.topay(info, position, 0);
				}
			});
		}

		if (holder.cancel != null) {
			holder.cancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					activity.showCancelDialog(position);
				}
			});
		}
		if (holder.delete != null) {
			holder.delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					activity.showDeleteDialog(position);
				}
			});
		}

		return convertView;
	}

	class ViewHolder {
		TextView orderid;
		TextView orderstatus;
		TextView title;
		TextView money;
		TextView diamond;
		TextView diamondBg;
		ImageView image;
		TextView topay;
		TextView cancel;
		TextView delete;
	}

}