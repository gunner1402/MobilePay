package com.oasgames.android.oaspay.entity;

import android.app.Activity;
import android.content.Intent;

import com.oasgames.android.oaspay.activity.ActivityNewsDetails;
import com.oasgames.android.oaspay.activity.ActivityProductDetails;

/**
 * 商城 - 轮播图
 * @author Administrator
 *
 */
public class ShopFocus {

	public String focus_id;
	public String focus_img_url;
	public String focus_type;// 1:资讯	2:礼包
	public String focus_product_id;// 第三方主要参数
	public String link;


	public void setFocus_img_url(String focus_img_url) {
		this.focus_img_url = focus_img_url;
	}

	public void setFocus_type(String type) {
		this.focus_type = type;
	}

	public void setFocus_id(String focus_id) {
		this.focus_id = focus_id;
	}

	public void startActivity(Activity activity){
		if(this.focus_type.equals("1"))
			activity.startActivity(new Intent().setClass(activity, ActivityNewsDetails.class).putExtra("link", this.focus_product_id));// 条件不足，无法跳转
		else if(this.focus_type.equals("2"))
			activity.startActivity(new Intent().setClass(activity, ActivityProductDetails.class).putExtra("id", this.focus_product_id));

	}

	public void setFocus_product_id(String focus_product_id) {
		this.focus_product_id = focus_product_id;
	}
}
