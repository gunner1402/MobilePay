package com.oasgames.android.oaspay.entity;

import java.util.List;

/**
 * 商城数据集合（首页）
 * @author Administrator
 *
 */
public class ShopList {
	public List<ShopFocus> focusList;
	public List<ProductInfo> newestList;
	public List<ProductInfo> hotList;
	public List<ProductInfo> browseList;

	public void setFocusList(List focusList) {
		this.focusList = focusList;
	}

	public void setNewestList(List newestList) {
		this.newestList = newestList;
	}

	public void setHotList(List hotList) {
		this.hotList = hotList;
	}

	public void setBrowseList(List browseList) {
		this.browseList = browseList;
	}
}
