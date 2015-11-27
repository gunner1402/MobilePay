package com.oasgames.android.oaspay.entity;

import android.text.TextUtils;

import com.base.tools.BasesApplication;
import com.base.tools.entity.PhoneInfo;
import com.base.tools.utils.BasesUtils;
import com.base.tools.utils.MD5Encrypt;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * 数据上报信息
 * @author Administrator
 *
 */
public class ReportMdataInfo extends ReportInfo{

	public Map<String, String> params;	//上报APP自定义参数
	public Map<String, String> status;	//上报APP自定义参数
	public String content;// 上报内容

	
	public ReportMdataInfo(String eventName, Map<String, String> params, Map<String, String> status) {
		super.type = 2;
		super.eventName = eventName;
		super.createTime = System.currentTimeMillis();
		this.params = params;
		this.status = status;
		this.content = getMdataJsonInfo();
	}
	
	private String getMdataJsonInfo(){
		
//		StringBuffer o = new StringBuffer("{");
//
//		o.append("\"appid\":\""+ PhoneInfo.instance().mdataAppID+"\"");
//		o.append(",\"uuid\":\""+(BasesApplication.userInfo!=null&&!TextUtils.isEmpty(BasesApplication.userInfo.uid)?BasesApplication.userInfo.uid: MD5Encrypt.StringToMD5(BasesUtils.getMobileCode()))+"\"");// 未登陆 mobilecode，已登陆uid
//		o.append(",\"udid\":\""+ BasesUtils.getMobileCode()+"\"");
//		o.append(",\"server_id\":\""+(BasesApplication.userInfo!=null&&!TextUtils.isEmpty(BasesApplication.userInfo.serverID)?BasesApplication.userInfo.serverID:"")+"\"");
//		o.append(",\"__time_shift\":\""+(createTime - System.currentTimeMillis())/1000+"\"");// 转换为秒
//
//		o.append(",\"channel\":\"oaspay\"");
//		o.append(",\"locale\":\"ar\"");//"+PhoneInfo.instance().locale+"
//		o.append(",\"version\":\""+PhoneInfo.instance().bundleversion+"\"");//PhoneInfo.instance().softwareVersion
////		o.append(",\"country\":\"" + PhoneInfo.instance().getIpToCountry() + "\"");
//		o.append(",\"os\":\""+"android"+"\"");
//		o.append(",\"browser\":\"\"");
//		o.append(",\"screen\":\""+PhoneInfo.instance().screen+"\"");
//
//		o.append(",\"event\":\""+eventName+"\"");
		JSONObject o = new JSONObject();

		try {
			o.put("appid", PhoneInfo.instance().mdataAppID);
			o.put("uuid", (BasesApplication.userInfo != null && !TextUtils.isEmpty(BasesApplication.userInfo.uid) ? BasesApplication.userInfo.uid : MD5Encrypt.StringToMD5(BasesUtils.getMobileCode())));// 未登陆 mobilecode，已登陆uid
			o.put("udid", BasesUtils.getMobileCode());
			o.put("server_id", (BasesApplication.userInfo != null && !TextUtils.isEmpty(BasesApplication.userInfo.serverID) ? BasesApplication.userInfo.serverID : ""));
			o.put("__time_shift", (createTime - System.currentTimeMillis()) / 1000);// 转换为秒

			o.put("channel", "oaspay");
			o.put("locale", "ar");//"+PhoneInfo.instance().locale+"
			o.put("version", PhoneInfo.instance().bundleversion);//PhoneInfo.instance().softwareVersion
//			o.put("country", PhoneInfo.instance().getIpToCountry());
			o.put("os", "android");
			o.put("browser", "");
			o.put("screen", PhoneInfo.instance().screen);

			o.put("event", eventName);
		} catch (JSONException e) {
			e.printStackTrace();
		}


		JSONObject pJson = new JSONObject();// 事件级参数
		try {
			if(params != null){
				for (Map.Entry<String, String> iter : params.entrySet()) {
					pJson.put(iter.getKey(), iter.getValue());
				}
			}
			o.put("params", pJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}


		JSONObject sJson = new JSONObject();// 应用级参数
		try {
			sJson.put("gamecode", PhoneInfo.instance().gamecode);
			if(status != null){
				for (Map.Entry<String, String> iter : status.entrySet()) {
					sJson.put(iter.getKey(), iter.getValue());
				}
			}
			o.put("status", sJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}


		BasesUtils.logDebug("Mdata", o.toString());
		return o.toString();
	}
}
