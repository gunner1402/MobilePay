package com.oasgames.android.oaspay.entity;

import java.util.List;

/**
 * 商品（礼包）列表集合
 * @author Administrator
 *
 */
public class ProductList {
	public String cur_page;			//当前页号
	public String total_page;		//总页数
	public String every_page_count;			//每页记录数
	public List list;


	public void setCur_page(String cur_page) {
		this.cur_page = cur_page;
	}

	public void setTotal_page(String total_page) {
		this.total_page = total_page;
	}

	public void setEvery_page_count(String every_page_count) {
		this.every_page_count = every_page_count;
	}

	public void setList(List list) {
		this.list = list;
	}
}
