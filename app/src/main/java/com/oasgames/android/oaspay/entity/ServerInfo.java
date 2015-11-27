package com.oasgames.android.oaspay.entity;

import java.io.Serializable;

/**
 * 服列表 详细信息
 * @author xdb
 *
 */
public class ServerInfo implements Serializable{
	private static final long serialVersionUID = 1598605839985469091L;

	public String serverid;			//服id
	public String servername;		//服名称
	public String charge_status;	//充值状态
	public String rolename;			//角色名


	public void setServerid(String serverid) {
		this.serverid = serverid;
	}

	public void setServername(String servername) {
		this.servername = servername;
	}

	public void setCharge_status(String charge_status) {
		this.charge_status = charge_status;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
}
