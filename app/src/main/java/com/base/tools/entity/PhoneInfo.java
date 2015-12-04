package com.base.tools.entity;

import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.base.tools.BasesApplication;
import com.base.tools.utils.BasesUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class PhoneInfo {
	private final static PhoneInfo PHONEINFO = new PhoneInfo();

	private PhoneInfo() {
	}

	/**
	 * @return 返回逻辑的实例.
	 */
	public static PhoneInfo instance() {

		return PHONEINFO;
	}
	/**  
	   * 唯一的设备ID：   
	   * GSM手机的 IMEI 和 CDMA手机的 MEID.    
	   * Return null if device ID is not available.   
	   */   
	public String deviceId;
	/**
	 * 手机系统类型
	 */
	public String softwareType;
	/**  
	   * 设备的软件版本号：   
	   * 例如：the IMEI/SV(software version) for GSM phones.   
	   * Return null if the software version is not available.    
	   */  
	public String softwareVersion;
	/**   
	   * 手机号：   
	   * GSM手机的 MSISDN.   
	   * Return null if it is unavailable.    
	   */  
	public String line1Number;
	public String line2Number;
	
	 /**
	   * 当前使用的网络类型：   
	   * 例如： NETWORK_TYPE_UNKNOWN  网络类型未知  0   
	     NETWORK_TYPE_GPRS     GPRS网络  1   
	     NETWORK_TYPE_EDGE     EDGE网络  2   
	     NETWORK_TYPE_UMTS     UMTS网络  3   
	     NETWORK_TYPE_HSDPA    HSDPA网络  8    
	     NETWORK_TYPE_HSUPA    HSUPA网络  9   
	     NETWORK_TYPE_HSPA     HSPA网络  10   
	     NETWORK_TYPE_CDMA     CDMA网络,IS95A 或 IS95B.  4   
	     NETWORK_TYPE_EVDO_0   EVDO网络, revision 0.  5   
	     NETWORK_TYPE_EVDO_A   EVDO网络, revision A.  6   
	     NETWORK_TYPE_1xRTT    1xRTT网络  7   
	   */    
	public String networkType;//int  
	
	/**  
	   * 唯一的用户ID：   
	   * 例如：IMSI(国际移动用户识别码) for a GSM phone.   
	   * 需要权限：READ_PHONE_STATE   
	   */    
	public String subscriberId;
	
	/**
	 * 手机型号
	 */
	public String model;
	
	/**
	 * 手机品牌
	 */
	public String brand;
	/**
	 * 2位国家代码(来自sim卡)
	 */
	public String iso2Country;
//	model = android.os.Build.MODEL;   // 手机型号
//	sdk=android.os.Build.VERSION.SDK;    // SDK号
//	release=android.os.Build.VERSION.RELEASE;  // android系统版本号

	/**
	 * 包名
	 */
	public String bundleid;
	/**
	 * 版本号  2.4.5
	 */
	public String bundleversion;
	/**
	 * 版本号
	 */
	public String bundleversioncode;

	public String channel;// 渠道
	public String mdataAppID;// mData App id
	public String androidID;// android id
	public String googleAdid;// Google 广告id
	public String ipToCountry;//根据ip获取地区
	public String event;// 事件
	public int locale;// LCID, 语种对应的id，int类型；获取到国家（地区）后，根据语种获取LCID
	public String browser;
	public String screen;// 屏幕分辨率
	public String density;// 设备分辨率
	public String referrer;// 推广渠道信息
	public DisplayMetrics dm;// 屏幕相关数据
	public String gamecode;// 应用代码
	public String mobilecode;// 设备唯一码

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public void setSoftwareType(String softwareType) {
		this.softwareType = softwareType;
	}

	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	public void setLine1Number(String line1Number) {
		this.line1Number = line1Number;
	}

	public void setLine2Number(String line2Number) {
		this.line2Number = line2Number;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public void setIso2Country(String iso2Country) {
		this.iso2Country = iso2Country;
	}

	public void setDm(DisplayMetrics dm){
		this.dm = dm;
	}
	public String toString(){
		StringBuffer sb = new StringBuffer(toStringForLogin());

		if(BasesUtils.isLogin()) {
			sb.append("&uid=" + BasesApplication.userInfo.uid);
			sb.append("&oas_token=" + BasesApplication.userInfo.token);
			sb.append("&usertype=" + BasesApplication.userInfo.user_type);
			sb.append("&platform=" + BasesApplication.userInfo.platform);
		}
		return sb.toString();
	}
	public String toStringForLogin(){
		StringBuffer sb = new StringBuffer();
		sb.append("&phonebrand="+brand);
		try {
			sb.append("&phonemodel="+URLEncoder.encode(model, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		sb.append("&ostype="+softwareType);
		sb.append("&osversion="+softwareVersion);
		sb.append("&bundleid="+bundleid);// 包名
		sb.append("&bundleversion="+bundleversion);// 2.4.5
		sb.append("&bundleversioncode="+bundleversioncode);// versioncode
		sb.append("&androidid="+androidID);// android id
		try {
			sb.append("&referrer="+URLEncoder.encode(referrer, "UTF-8"));// 推广渠道信息
		} catch (UnsupportedEncodingException e) {
		}
		sb.append("&adid="+googleAdid);// 广告id
		sb.append("&game_code="+gamecode);
		sb.append("&mobile_code="+mobilecode);

		return sb.toString();
	}


	public void setChannel(String channel) {
		this.channel = channel;
	}

	public void setMdataAppID(String mdataAppID) {
		this.mdataAppID = mdataAppID;
	}

	public void setAndroidID(String androidID) {
		this.androidID = androidID;
	}

	public void setGoogleAdid(String adid) {
		this.googleAdid = adid;
	}

	public String getIpToCountry() {
		if(TextUtils.isEmpty(ipToCountry))
			return "";
		return ipToCountry.toLowerCase();
	}
	public String getIpToCountryWithHttp() {
		if(TextUtils.isEmpty(ipToCountry))
			return "";
		return ipToCountry.toLowerCase()+".";
	}

	public void setIpToCountry(String ipToCountry) {
		this.ipToCountry = ipToCountry;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public void setLocale(int locale) {
		this.locale = locale;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public void setScreen(String screen) {
		this.screen = screen;
	}

	public void setDensity(String density) {
		this.density = density;
	}

	public void setBundleid(String bundleid) {
		this.bundleid = bundleid;
	}

	public void setBundleversion(String bundleversion) {
		this.bundleversion = bundleversion;
	}

	public void setBundleversioncode(String bundleversioncode) {
		this.bundleversioncode = bundleversioncode;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}


	public void setGamecode(String gamecode) {
		this.gamecode = gamecode;
	}

	public void setMobilecode(String mobilecode) {
		this.mobilecode = mobilecode;
	}

}
