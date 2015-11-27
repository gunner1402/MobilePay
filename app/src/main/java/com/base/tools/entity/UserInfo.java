package com.base.tools.entity;

/**
 * 用户基本信息
 * @author Administrator
 *
 */
public class UserInfo {
	
	public String status;
	public String uid;
	public String user_type;
	public String token;
	public String platform;
	public String platform_token;
	public String error;
	public String err_msg;
	public String avatar_pic;// 头像

	public String serverID;
	public String serverName;
	public String serverType;
	public String roleID;
	public String username;
	public String nickname;// 昵称
	public String gameNickname;// 游戏昵称

	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getPlatform_token() {
		return platform_token;
	}
	public void setPlatform_token(String platform_token) {
		this.platform_token = platform_token;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getErr_msg() {
		return err_msg;
	}
	public void setErr_msg(String err_msg) {
		this.err_msg = err_msg;
	}
	public String getServerID() {
		return serverID;
	}
	public void setServerID(String serverID) {
		this.serverID = serverID;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerType() {
		return serverType;
	}
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	public String getRoleID() {
		return roleID;
	}
	public void setRoleID(String roleID) {
		this.roleID = roleID;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String paltform) {
		this.platform = paltform;
	}
	public String getGameNickname() {
		return gameNickname;
	}
	public void setGameNickname(String gameNickname) {
		this.gameNickname = gameNickname;
	}

	public void setAvatar_pic(String avatar_pic) {
		this.avatar_pic = avatar_pic;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}
