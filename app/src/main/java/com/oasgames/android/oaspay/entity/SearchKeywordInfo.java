package com.oasgames.android.oaspay.entity;

import java.io.Serializable;

/**
 * 默认搜索关键词 详细信息
 * @author xdb
 *
 */
public class SearchKeywordInfo implements Serializable{
	private static final long serialVersionUID = 1598605839985469091L;

	public String id;			//主键 以创建时间代替
	public String keyword;		//名称
	public String createtime;	//创建时间
	public String ext1;			//扩展1
	public String ext2;			//扩展2
	public String px;			//扩展3

	public void setId(String id) {
		this.id = id;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}

	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}

	public void setPx(String px) {
		this.px = px;
	}
}
