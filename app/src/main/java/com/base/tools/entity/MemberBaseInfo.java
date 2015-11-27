package com.base.tools.entity;

public class MemberBaseInfo extends UserInfo{
	public static final String USER_FACEBOOK = "facebook";// Facebook
	public static final String USER_GOOGLE = "google";
	public static final String USER_TWITTER = "twitter";
	/**
	 * 游客
	 */
	public static final String USER_NONE = "auto";
	/**
	 * 已注册用户
	 */
	public static final String USER_REGISTED = "oas";

	
	public String memberName;
	public String password;

	public static int checkUserType(String platform){
		if(platform.equals(USER_NONE))
			return 0;
		else if(platform.equals(USER_REGISTED))
			return 1;
		return 2;
	}
	public static boolean isOther(String platform){
		if(platform.equals(USER_NONE) || platform.equals(USER_REGISTED))
			return false;
		return true;
	}
}
