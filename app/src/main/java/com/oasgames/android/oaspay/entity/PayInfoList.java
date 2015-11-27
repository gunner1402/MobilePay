package com.oasgames.android.oaspay.entity;

import java.util.List;

/**
 * 支付套餐集合 (含套餐、服信息)
 * @author Administrator
 *
 */
public class PayInfoList {
	public List<PayInfoDetail> list;
	public List<ServerInfo> servers;
	
	public void setList(List list) {
		this.list = list;
	}

	public void setServers(List servers) {
		this.servers = servers;
	}
}
