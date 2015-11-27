package com.oasgames.android.oaspay.entity;

import java.io.Serializable;

/**
 * 商品（礼包）详细信息
 * @author xdb
 *
 */
public class ProductInfo implements Serializable{
	private static final long serialVersionUID = 1598605839985469091L;
	
	public String product_id;			//商品礼包id
	private String price_product_id;		//商店id
	public String product_name;			//名称
	public String product_img_url;		//图片地址
	public String game_coins;			//钻石数量
	public String game_coins_show;		//钻石数量
	public String price_discount;		//钻石数量
	public String amount;				//显示价格
	public String amount_show;			//显示价格
	public String currency;				//显示货币
	public String currency_show;		//显示货币
	public String content_info;			//商品说明 数组


	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public void setProduct_img_url(String product_img_url) {
		this.product_img_url = product_img_url;
	}

	public void setGame_coins(String game_coins) {
		this.game_coins = game_coins;
	}

	public void setGame_coins_show(String game_coins_show) {
		this.game_coins_show = game_coins_show;
	}

	public void setPrice_discount(String price_discount) {
		this.price_discount = price_discount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public void setAmount_show(String amount_show) {
		this.amount_show = amount_show;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setCurrency_show(String currency_show) {
		this.currency_show = currency_show;
	}

	public void setContent_info(String content_info) {
		this.content_info = content_info;
	}

	public void setPrice_product_id(String price_product_id) {
		this.price_product_id = price_product_id;
	}
}
