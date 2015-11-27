package com.oasgames.android.oaspay.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 订单列表集合
 * @author xdb
 *
 */
public class OrderList implements Serializable{
	private static final long serialVersionUID = 1598605839985469091L;
	
	public String cur_page;			//当前页号
	public String total_page;		//总页数
	public String every_page_count;			//每页记录数
	public List list;	//数据集合


	public boolean isEmpty(){
		if(list == null || list.size()<= 0)
			return true;
		return false;
	}

	public void setCur_page(String cur_page) {
		this.cur_page = cur_page;
	}

	public void setTotal_page(String total_page) {
		this.total_page = total_page;
	}

	public void setEvery_page_count(String every_page_count) {
		this.every_page_count = every_page_count;
	}
}
