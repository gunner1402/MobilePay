package com.oasgames.android.oaspay.entity;

import java.io.Serializable;

/**
 * 订单详细信息
 * @author xdb
 *
 */
public class OrderInfo implements Serializable{
	public static final long serialVersionUID = 1598605839985469091L;
	
	public String uid;				//用户id
	public String order_id;			//订单id
	public String product_id;		//OAS商品id
	public String product_name;		//套餐或礼包名称
	public String product_img_url;	//套餐或礼包图片
	public String content_info;		//套餐或礼包介绍
	public String price_product_id;	//第三方商店id
	public String server_id;		//服务器ID
	public String server_name;		//服务器名称
	public String rolename;			//角色名称

	public String game_coins;			//游戏币数量
	public String game_coins_show;		//展示游戏币数量
	public String price_discount;		//赠送游戏币数量


	public String amount;			//价格
	public String amount_show;		//展示金额
	public String currency;			//币种
	public String currency_show;	//展示币种

	public String exchange_code;	//兑换码


	public String order_type;		//订单类型	礼包:giftapp    套餐：payapp
	public String order_status;		//订单状态  1正常  2删除 3取消 4下单失败
	public String pay_status;		//支付状态   1等待支付 2完成支付
	public String online_status;	//上架状态 1上架 2下架

	public String create_time;		//下单时间
	public String pay_time;			//完成支付时间

	public String ostype;			//支付所需的系统类型


	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public void setProduct_img_url(String product_img_url) {
		this.product_img_url = product_img_url;
	}

	public void setContent_info(String content_info) {
		this.content_info = content_info;
	}

	public void setPrice_product_id(String price_product_id) {
		this.price_product_id = price_product_id;
	}

	public void setServer_id(String server_id) {
		this.server_id = server_id;
	}

	public void setServer_name(String server_name) {
		this.server_name = server_name;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
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

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}

	public void setPay_status(String pay_status) {
		this.pay_status = pay_status;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public void setPay_time(String pay_time) {
		this.pay_time = pay_time;
	}

	public void setExchange_code(String exchange_code) {
		this.exchange_code = exchange_code;
	}

	public void setOstype(String ostype) {
		this.ostype = ostype;
	}


	public void setOnline_status(String online_status) {
		this.online_status = online_status;
	}
}
