package com.oasgames.android.oaspay.tools;

import com.oasgames.android.oaspay.service.SearchUtil;
import com.base.tools.google.GoogleBillingUtils;

import java.util.Hashtable;



/**
 * 常量.
 * 
 * @author xdb
 * 
 */
public class Constant {
	private static final String VERSION = "3.1";
	/**
	 * SDK 当前版本号
	 */
	public static final String SDKVERSION = VERSION + ".5";

	public static final String BASEURL = "http://arapp.mobile.oasgames.com/?";
//	public static final String BASEURL_SANDBOX = "http://apisdk.mobile.oasgames.com/sandbox/?";

	public static final Hashtable<Integer, String> http_statuscode_errorMsg = new Hashtable<Integer, String>();
	static {
		http_statuscode_errorMsg.put(0, "未知异常(可能需要设置代理)");
		http_statuscode_errorMsg.put(400, "错误请求");
		http_statuscode_errorMsg.put(408, "Request Timeout/请求超时");
		http_statuscode_errorMsg.put(500, "Internal Server Error/内部服务器错误");
		http_statuscode_errorMsg.put(503, "Service Unavailable/服务无法获得");
		http_statuscode_errorMsg.put(504, "Gateway Timeout/网关超时");

	}
	
	public static final String[] createTables = new String[]{
		"create table if not exists "+ GoogleBillingUtils.TABLENAME+" ("+ GoogleBillingUtils.COLUMNS_ID+" varchar(100) primary key, "+ GoogleBillingUtils.COLUMNS_DATA+" text not null, "+ GoogleBillingUtils.COLUMNS_SIGN+" text not null, "+ GoogleBillingUtils.COLUMNS_TIME+" varchar not null, "+ GoogleBillingUtils.COLUMNS_STATUS+" varchar(10), "+ GoogleBillingUtils.COLUMNS_EXT1+" varchar(100), "+ GoogleBillingUtils.COLUMNS_EXT2+" text);",
		"create table if not exists "+ SearchUtil.TABLENAME +" ("+SearchUtil.COLUMNS_ID+" varchar(100) primary key, "+SearchUtil.COLUMNS_KEYWORD+" text not null, "+SearchUtil.COLUMNS_TIME+" varchar not null, "+SearchUtil.COLUMNS_EXT1+" varchar(100), "+SearchUtil.COLUMNS_EXT2+" text);"
	};
	public static final String[] dropTables = new String[]{
//		"drop table googleorder;"
	};
}