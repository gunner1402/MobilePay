package com.oasgames.android.oaspay.entity;

import java.io.Serializable;

/**
 * 应用版本信息
 * @author xdb
 *
 */
public class AppVersionInfo implements Serializable{
	private static final long serialVersionUID = 1598605839985469091L;

	public String title;		//名称
	public String version_intro;		//版本更新信息
	public String version_url;		//更新地址
	public String bundleversioncode;		//当前版本号
	public String force_update;		//是否强更


	public void setTitle(String title) {
		this.title = title;
	}

	public void setVersion_intro(String version_intro) {
		this.version_intro = version_intro;
	}

	public void setVersion_url(String version_url) {
		this.version_url = version_url;
	}

	public void setBundleversioncode(String bundleversioncode) {
		this.bundleversioncode = bundleversioncode;
	}

	public void setForce_update(String force_update) {
		this.force_update = force_update;
	}
	public boolean isForceUpdate(){
		if("Y".equalsIgnoreCase(force_update))
			return true;
		return false;
	}
}
