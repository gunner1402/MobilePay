package com.oasgames.android.oaspay.entity;

import java.io.Serializable;

/**
 * 新闻详细信息
 * @author xdb
 *
 */
public class NewsInfo implements Serializable{
	private static final long serialVersionUID = 1598605839985469091L;

	public String id;			//订单id
	public String title;		//名称
	public String summary;		//描述
	public String start_time;		//日期
	public String detail_url;		//链接


	public void setId(String id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public void setDetail_url(String detail_url) {
		this.detail_url = detail_url;
	}
}
