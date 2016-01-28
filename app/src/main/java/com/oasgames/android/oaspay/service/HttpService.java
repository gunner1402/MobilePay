package com.oasgames.android.oaspay.service;

import android.util.Log;

import com.base.tools.BasesApplication;
import com.base.tools.entity.MemberBaseInfo;
import com.base.tools.entity.PhoneInfo;
import com.base.tools.exception.BasesDataErrorException;
import com.base.tools.exception.BasesNetworkErrorException;
import com.base.tools.google.Purchase;
import com.base.tools.http.CallbackResultForActivity;
import com.base.tools.json.BasesJsonParser;
import com.base.tools.service.BasesDao;
import com.base.tools.service.BasesServiceLogin;
import com.base.tools.utils.BasesConstant;
import com.base.tools.utils.BasesUtils;
import com.base.tools.utils.MD5Encrypt;
import com.mopub.volley.Response;
import com.mopub.volley.VolleyError;
import com.oasgames.android.oaspay.activity.MyApplication;
import com.oasgames.android.oaspay.entity.AppVersionInfo;
import com.oasgames.android.oaspay.entity.NewsInfo;
import com.oasgames.android.oaspay.entity.NewsList;
import com.oasgames.android.oaspay.entity.OrderInfo;
import com.oasgames.android.oaspay.entity.OrderList;
import com.oasgames.android.oaspay.entity.PayHistoryInfoDetail;
import com.oasgames.android.oaspay.entity.PayHistoryList;
import com.oasgames.android.oaspay.entity.PayInfoDetail;
import com.oasgames.android.oaspay.entity.PayInfoList;
import com.oasgames.android.oaspay.entity.ProductInfo;
import com.oasgames.android.oaspay.entity.ProductList;
import com.oasgames.android.oaspay.entity.ReportMdataInfo;
import com.oasgames.android.oaspay.entity.SearchKeywordInfo;
import com.oasgames.android.oaspay.entity.ServerInfo;
import com.oasgames.android.oaspay.entity.ShopFocus;
import com.oasgames.android.oaspay.entity.ShopList;
import com.oasgames.android.oaspay.tools.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http service
 * 处理请求参数
 * 处理请求结果
 * @author Xdb
 * 
 */
public class HttpService {
	private static final String SPLITCHAR = "OASUSER";// 分隔符，避免截取出错
	private final static String TAG = "OAS_HttpService";
	private final static HttpService HTTP_SERVICE = new HttpService();

	private HttpService() {
	}

	/**
	 * @return 返回逻辑的实例.
	 */
	public static HttpService instance() {

		return HTTP_SERVICE;
	}

	/**
	 * 注册新用户
	 * @param username	
	 * @param password	
	 * @return  成功	{status:"ok",uid:"20000000012345678",type:”2”,token:"690c122e35e2681fb34f9fef236396d0"}；type=1 是免注册用户，type=2 是正常用户
	 * 			失败     {status:"fail",error:"错误编号",err_msg:”错误描述”}
	 * @throws JSONException 
	 * @throws BasesNetworkErrorException
	 * @throws BasesDataErrorException
	 * @throws UnsupportedEncodingException 
	 */
	public void register(String username, String password, CallbackResultForActivity callback){
		
		StringBuffer url = new StringBuffer("a=Regist&m=NewUser");

		url.append("&username="+username);
		try {
			url.append("&password="+URLEncoder.encode(password, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			url.append("&password="+password);
		}

		url.append("&sign=" + MD5Encrypt.StringToMD5(BasesUtils.getMobileCode() + PhoneInfo.instance().gamecode + username + password + BasesApplication.PUBLICKEY));

		new BasesServiceLogin().register(url, username, password, callback);
	}

	/**
	 * 使用Token登录
	 * @param callback
	 */
	public void loginByToken(CallbackResultForActivity callback){
		new BasesServiceLogin().loginWithRecentlyToken(getUrlForLogin("a=Login&m=AutoLogin"), callback);
	}
	/**
	 * 登录和注册
	 * @param username			usertype=1时，传手机唯一码
								usertype=2时，玩家输入的OAS平台账号（邮箱格式）
								usertype=3时，传入第三方的平台代码，例如facebook、twiiter、google
	 * @param password			usertype=2时，玩家输入的密码，
								usertype=3时，传入第三方的平台token
	 * @return  成功	{status:"ok",uid:"20000000012345678",type:”2”,token:"690c122e35e2681fb34f9fef236396d0"}；type=1 是免注册用户，type=2 是正常用户
	 * 			失败     {status:"fail",error:"错误编号",err_msg:”错误描述”}
	 * 				err_msg:	-1	签名未通过
	 * 							-2	OAS用户名或密码错误
	 * 							-3	注册的username已经存在
	 * 							-4	未知错误
	 * 							-5	oas_token过期
	 * @throws BasesNetworkErrorException
	 * @throws BasesDataErrorException
	 */
	public void login(String platform, String username, String password, String oasNickName, CallbackResultForActivity callback){
		
		StringBuffer url = getUrlForLogin("a=Login&m=UserLogin");
		int userType = 0;
		if(platform.equals(MemberBaseInfo.USER_NONE)) {
			userType = 0;
			url.append("&sign="+MD5Encrypt.StringToMD5(BasesUtils.getMobileCode()+PhoneInfo.instance().gamecode+userType+BasesApplication.PUBLICKEY));
		}else if(platform.equals(MemberBaseInfo.USER_REGISTED)) {
			userType = 1;
			url.append("&username=" + username);
			try {
				url.append("&password=" + URLEncoder.encode(password, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				url.append("&password=" + password);
			}
			url.append("&sign=" + MD5Encrypt.StringToMD5(BasesUtils.getMobileCode() + PhoneInfo.instance().gamecode + userType + username + password + BasesApplication.PUBLICKEY));
		}else {
			userType = 2;
			url.append("&platform="+platform);
			url.append("&platform_token="+password);

			url.append("&sign="+MD5Encrypt.StringToMD5(BasesUtils.getMobileCode()+PhoneInfo.instance().gamecode+userType+platform+BasesApplication.PUBLICKEY));
		}

		url.append("&usertype=" + userType);


		new BasesServiceLogin().login(url, platform, username, password, oasNickName, callback);
	}
	

	/**
	 * 修改密码
	 * @throws BasesNetworkErrorException
	 * @throws BasesDataErrorException
	 */
	public void modifyPwd(String password, String newPwd, String newPwd_repeat, CallbackResultForActivity callBack) throws BasesNetworkErrorException, BasesDataErrorException {
		StringBuffer url = new StringBuffer("a=Login&m=Updatepasswd");
		url.append("&username="+BasesApplication.userInfo.username);
		try {
			url.append("&password="+URLEncoder.encode(password, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			url.append("&password="+password);
		}
		try {
			url.append("&newpassword="+URLEncoder.encode(newPwd, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			url.append("&newpassword="+newPwd);
		}
		try {
			url.append("&newpassword_repeat="+URLEncoder.encode(newPwd_repeat, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			url.append("&newpassword_repeat="+newPwd_repeat);
		}
		url.append("&sign=" + MD5Encrypt.StringToMD5(BasesApplication.userInfo.username + password + BasesApplication.PUBLICKEY));
		new BasesServiceLogin().modifyPwd(url, newPwd, callBack);
	}

	/**
	 * 检查应用版本
	 * @param callback
	 */
	public void checkAppVersion(final CallbackResultForActivity callback){
		new BasesDao().post(getUrl("a=system&m=version").toString(), null, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				boolean status = isSuccess("checkAppVersion", s, callback);
				if (!status) {
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
					return;
				}
				AppVersionInfo app = new AppVersionInfo();
				try {
					// 解析json
					BasesJsonParser.newInstance().parserJson2Obj(new JSONObject(s).get("version").toString(), app);
				} catch (Exception e) {
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
					return;
				}
				callback.success(app, BasesConstant.RESULT_SUCCESS, s);

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callback.exception(new BasesNetworkErrorException(""));
			}
		});
	}

	/**
	 * 获取默认搜索关键词
	 * @param callback
	 */
	public void getSearchKeyword(final CallbackResultForActivity callback){
		StringBuffer url = getUrl("a=Product&m=GetKeywords");
		url.append("&sign="+MD5Encrypt.StringToMD5(PhoneInfo.instance().gamecode+BasesApplication.PUBLICKEY));

		new BasesDao().post(url.toString(), null, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				boolean status = isSuccess("getSearchKeyword", s, callback);
//
				if (!status) {
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
					return;
				}
//
				SearchKeywordInfo ki = new SearchKeywordInfo();
				List list = new ArrayList();
				try{
					// 解析json
					list = BasesJsonParser.newInstance().parserJSONObj2ObjList(s, ki, "keywords_list");
				}catch (Exception e){
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
					return;
				}
				callback.success(list, BasesConstant.RESULT_SUCCESS, s);

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callback.exception(new BasesNetworkErrorException(""));
			}
		});
	}
	/**
	 * 获取商城页面所有数据
	 * @param callback
	 */
	public void getShopInfo(final CallbackResultForActivity callback){
		StringBuffer url = getUrl("a=Product&m=Index");
		url.append("&sign="+MD5Encrypt.StringToMD5(PhoneInfo.instance().mobilecode+PhoneInfo.instance().gamecode+BasesApplication.PUBLICKEY));

		new BasesDao().post(url.toString(), null, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				boolean status = isSuccess("getShopInfo", s, callback);

				if (!status) {
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
					return;
				}

				ShopList list = new ShopList();
				try {
					JSONObject o = new JSONObject(s);
					if(o.has("focus_img")){
						list.setFocusList(BasesJsonParser.newInstance().parserJSON2ObjList(o.getString("focus_img"), new ShopFocus()));
					}
					if(o.has("new_product")){
						list.setNewestList(BasesJsonParser.newInstance().parserJSON2ObjList(o.getString("new_product"), new ProductInfo()));
					}
					if(o.has("hot_product")){
						list.setHotList(BasesJsonParser.newInstance().parserJSON2ObjList(o.getString("hot_product"), new ProductInfo()));
					}
					if(o.has("browse_product")){
						list.setBrowseList(BasesJsonParser.newInstance().parserJSON2ObjList(o.getString("browse_product"), new ProductInfo()));
					}

				}catch (Exception e){
					BasesUtils.logError(TAG, "getShopInfo Json解析出错。" + e.getMessage());
				}

				// 解析json
				callback.success(list, BasesConstant.RESULT_SUCCESS, s);

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callback.exception(new BasesNetworkErrorException(""));
			}
		});
	}

	/**
	 * 根据条件，获取商品列表（含 搜索列表、商城中商品列表、）
	 * @param product_type		商品类型 1为最新上架 2为热门推荐 3为最近浏览
	 * @param keyword		关键词
	 * @param pageNo		当前页号
	 * @param pageSize		每页条数
	 * @param callback		回调
	 */
	public void getProductList(String product_type, String keyword, int pageNo, int pageSize, final CallbackResultForActivity callback){
		StringBuffer sb = getUrl("a=Product&m=ProductList");
//		if(!TextUtils.isEmpty(product_type) && !"null".equals(product_type))//不区分此参数
//			sb.append("&product_type="+product_type);

		try{
			sb.append("&keywords="+URLEncoder.encode(keyword, "UTF-8"));
		}catch (Exception e){}
		sb.append("&cur_page="+pageNo);
		sb.append("&every_page_count="+pageSize);
		sb.append("&sign="+MD5Encrypt.StringToMD5(PhoneInfo.instance().gamecode+MyApplication.PUBLICKEY));

		new BasesDao().post(sb.toString(), null, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				boolean status = isSuccess("getProductList", s, callback);

				if (!status) {
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
					return;
				}

				ProductList list = new ProductList();
				try {
					BasesJsonParser.newInstance().parserJson2Obj(s, list);
					list.list = BasesJsonParser.newInstance().parserJSONObj2ObjList(s, new ProductInfo(), "product_list");
				}catch (Exception e){
					BasesUtils.logError(TAG, "getProductList 数据解析错误。"+e.getMessage());
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
				}
				// 解析json
				callback.success(list, BasesConstant.RESULT_SUCCESS, s);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callback.exception(new BasesNetworkErrorException(""));
			}
		});
	}

	/**
	 * 根据商品ID获取详细信息
	 * @param id
	 * @param callback
	 */
	public void getProductDetails(String id, final CallbackResultForActivity callback){
		StringBuffer sb = getUrl("a=Product&m=ProductInfo");
		sb.append("&product_id="+id);
		sb.append("&sign="+MD5Encrypt.StringToMD5(id + MyApplication.PUBLICKEY));
		new BasesDao().post(sb.toString(), null, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				boolean status = isSuccess("getProductDetails", s, callback);

				if (!status) {
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
					return;
				}

				ProductInfo info = null;
				try {
					info = new ProductInfo();
					BasesJsonParser.newInstance().parserJson2Obj(s, info);
				}catch (Exception e){
					BasesUtils.logError(TAG, "getProductDetails 数据解析错误。" + e.getMessage());
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
				}
				// 解析json
				callback.success(info, BasesConstant.RESULT_SUCCESS, s);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callback.exception(new BasesNetworkErrorException(""));
			}
		});
	}

	/**
	 * 交易前，先下订单
	 * @param productid 	应用内商品id
	 * @param serverid		 	服id
	 * @param servername		 服名称
	 * @param rolename		 	角色名
	 * @param orderType		 	订单类型
	 *
	 * @throws BasesNetworkErrorException
	 */
	public void sendOrder(String productid, String serverid, String servername, String rolename ,String orderType,final CallbackResultForActivity callback){
		StringBuffer url = getUrl("a=Order&m=PlaceOrder");
//		url.append("&uid="+BasesApplication.userInfo.uid);
		url.append("&product_id="+productid);
		url.append("&server_id="+serverid);
		try {
			url.append("&server_name="+URLEncoder.encode(servername, "UTF-8"));
		} catch (Exception e){
			url.append("&server_name="+servername);
		}
		url.append("&rolename="+rolename);
		url.append("&order_type="+orderType);
		url.append("&sign="+MD5Encrypt.StringToMD5(PhoneInfo.instance().gamecode+productid+BasesApplication.PUBLICKEY));

		new BasesDao().post(url.toString(), null, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				boolean flag = isSuccess("sendOrder", s, callback);
				if (!flag) {
					try {
						JSONObject o = new JSONObject(s);
						if(o.has("error")){
							callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, ""+o.getInt("error"));
						}else
							callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
					} catch (JSONException e) {

						callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
					}
					return;
				}

				OrderInfo info = new OrderInfo();
				try {
					BasesJsonParser.newInstance().parserJson2Obj(s, info);
				}catch (Exception e){
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
				}
				callback.success(info, BasesConstant.RESULT_SUCCESS, "");
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callback.exception(new BasesNetworkErrorException(""));
			}
		});

	}
	/**
	 * 获取订单列表
	 * @param type		订单类型（历史订单、当月订单）
	 * @param pageNo	当前页号
	 * @param pageSize	每页记录数
	 * @param callback	回调
	 */
	public void getOrderList(int type, final int pageNo, final int pageSize, final CallbackResultForActivity callback){
		StringBuffer sb = getUrl("a=Order&m=OrderList");
//		sb.append("&uid="+BasesApplication.userInfo.uid);
		sb.append("&date_type="+type);
		sb.append("&cur_page="+pageNo);
		sb.append("&every_page_count="+pageSize);
		sb.append("&sign="+MD5Encrypt.StringToMD5(BasesApplication.userInfo.uid+PhoneInfo.instance().gamecode+MyApplication.PUBLICKEY));
		new BasesDao().post(sb.toString(), null, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				boolean flag = isSuccess("getOrderList", s, callback);
				if (!flag) {
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
					return;
				}

				OrderList list = new OrderList();
				try {
					BasesJsonParser.newInstance().parserJson2Obj(s, list);
					list.list = BasesJsonParser.newInstance().parserJSONObj2ObjList(s, new OrderInfo(), "order_list");
				}catch (Exception e){
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, s);
					return;
				}
				// 解析json
				callback.success(list, BasesConstant.RESULT_SUCCESS, s);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callback.exception(new BasesNetworkErrorException(""));
			}
		});
	}
	public void getOrderInfoByID(String orderId, final CallbackResultForActivity callback){
		StringBuffer sb = getUrl("a=Order&m=OrderInfo");
//		sb.append("&uid="+BasesApplication.userInfo.uid);
		sb.append("&order_id="+orderId);
		sb.append("&sign="+MD5Encrypt.StringToMD5(BasesApplication.userInfo.uid+orderId+MyApplication.PUBLICKEY));
		new BasesDao().post(sb.toString(), null, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				boolean flag = isSuccess("getOrderInfoByID", s, callback);
				if (!flag) {
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
					return;
				}
				OrderInfo info = null;
				try {
					info = new OrderInfo();
					BasesJsonParser.newInstance().parserJson2Obj(s, info);
				}catch (Exception e){
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
				}
				callback.success(info, BasesConstant.RESULT_SUCCESS, "");
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callback.exception(new BasesNetworkErrorException(""));
			}
		});
	}
	public void getOrderInfoByQR(String data, final CallbackResultForActivity callback){
		StringBuffer sb = getUrl("a=Order&m=QrcodePlaceOrder");

		Map<String, String> paras = new HashMap<>();
		String orderinfo = "";
		try{
			orderinfo =URLEncoder.encode(data, "UTF-8");
		}catch (Exception e){}
		paras.put("order_info", orderinfo);

//		try{
//			sb.append("&order_info="+ URLEncoder.encode(data, "UTF-8"));
//		}catch (Exception e){}

		sb.append("&sign="+MD5Encrypt.StringToMD5(BasesApplication.userInfo.uid+MyApplication.PUBLICKEY));
		new BasesDao().post(sb.toString(), paras, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				OrderInfo info = null;
				try {
					JSONObject o = new JSONObject(s);
					if(!"ok".equalsIgnoreCase(o.getString("status"))){
						callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, o.getString("error"));
						return;
					}
					info = new OrderInfo();
					BasesJsonParser.newInstance().parserJson2Obj(s, info);
				}catch (Exception e){
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
				}
				callback.success(info, BasesConstant.RESULT_SUCCESS, "");
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callback.exception(new BasesNetworkErrorException(""));
			}
		});
	}
	public void getOrderInfoByInput(String orderid, final CallbackResultForActivity callback){// 手动输入订单号
		StringBuffer sb = getUrl("a=Order&m=InputPlaceOrder");
//		sb.append("&uid="+BasesApplication.userInfo.uid);//"100002155542648");//
		sb.append("&order_id="+ orderid);

		sb.append("&sign="+MD5Encrypt.StringToMD5(BasesApplication.userInfo.uid+MyApplication.PUBLICKEY));
		new BasesDao().post(sb.toString(), null, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				OrderInfo info = null;
				try {
					JSONObject o = new JSONObject(s);
					if(!"ok".equalsIgnoreCase(o.getString("status"))){
						callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, o.getString("error"));
						return;
					}
					info = new OrderInfo();
					BasesJsonParser.newInstance().parserJson2Obj(s, info);
				}catch (Exception e){
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
				}
				callback.success(info, BasesConstant.RESULT_SUCCESS, "");
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callback.exception(new BasesNetworkErrorException(""));
			}
		});
	}
	public void deleteOrderByID(String id, String type, final CallbackResultForActivity callback){
		StringBuffer sb = getUrl("a=Order&m=OrderStatus");
//		sb.append("&uid="+BasesApplication.userInfo.uid);
		sb.append("&order_id="+id);
		sb.append("&operate_type="+type);
		sb.append("&sign="+MD5Encrypt.StringToMD5(BasesApplication.userInfo.uid+id+MyApplication.PUBLICKEY));
		new BasesDao().post(sb.toString(), null, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				boolean flag = isSuccess("deleteOrderByID", s, callback);
				if (!flag) {
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
					return;
				}
				callback.success("true", BasesConstant.RESULT_SUCCESS, "");
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callback.exception(new BasesNetworkErrorException(""));
			}
		});
	}
	/**
	 * 获取资讯列表
	 * @param pageNo
	 * @param pageSize
	 * @param callback
	 */
	public void getNewsList(final int pageNo, final int pageSize, final CallbackResultForActivity callback){
		StringBuffer url = getUrl("a=system&m=Getnewlist");
		url.append("&cur_page="+pageNo);
		url.append("&every_page_count="+pageSize);
		url.append("&sign="+MD5Encrypt.StringToMD5(PhoneInfo.instance().gamecode+pageNo+pageSize+MyApplication.PUBLICKEY));
		new BasesDao().post(url.toString(), null, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				boolean flag = isSuccess("getNewsList", s, callback);
				if (!flag) {
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
					return;
				}

				NewsList list = new NewsList();
				try {
					BasesJsonParser.newInstance().parserJson2Obj(s, list);
					list.list = BasesJsonParser.newInstance().parserJSONObj2ObjList(s, new NewsInfo(), "new_list");
				}catch (Exception e){
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, s);
					return;
				}
				// 解析json
				callback.success(list, BasesConstant.RESULT_SUCCESS, s);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callback.exception(new BasesNetworkErrorException(""));
			}
		});
	}

	/**
	 * 提交服务器验证支付是否成功，(发游戏币)
	 * @throws BasesNetworkErrorException
	 * 
	 * return int  
	 * 			1000000:成功，
	 * 			1000001:验证信息错误（key无效）
	 * 			1000002:该购买交易成功并且已发钻成功
	 * 			1000003:支付完成，验证连接失败-连接超时或者无法连接上Google服务器，
	 * 			1000004：ProductID错误—一般不会出现BUG
	 * 			1000005:支付成功，但是发钻不成功，
	 * 			1000100:未知错误，
	 */
	public void checkPurchaseForGoogle(Purchase p, String separate, final CallbackResultForActivity callback) {
		String[] info = p.getDeveloperPayload().split(separate);
		StringBuffer url = getUrl("a=Order&m=AndroidPaymentSuccess");
//		url.append("&order_id="+p.getOrderId());
//		url.append("&token="+p.getToken());
//		url.append("&product_id="+p.getSku());
//		url.append("&uid="+info[0]);
//		url.append("&sid="+info[1]);
//		if(info.length >= 6 && ("android".equalsIgnoreCase(info[5]) || "all".equalsIgnoreCase(info[5]) || "test".equalsIgnoreCase(info[5]) ))
//			url.append("&stype="+info[5]);
//		else
//			url.append("&stype="+BasesApplication.userInfo.serverType);
//		if(info.length >= 7)
//			url.append("&oas_orderid="+info[6]);
//
//		url.append("&roleid="+info[2]);
//		url.append("&ext="+info[3]);
//		url.append("&trace_signture="+URLEncoder.encode(p.getSignature()));
//		url.append("&trace_data="+p.getOriginalJson());
//		url.append("&sign="+MD5Encrypt.StringToMD5(PhoneInfo.instance().gamecode + MyApplication.PUBLICKEY + p.getOrderId() + p.getToken() + p.getSku() + info[0] + info[1] + info[2]));

		Map paras = new HashMap();
		paras.put("order_id",p.getOrderId());
		paras.put("token", p.getToken());
		paras.put("product_id",p.getSku());
		paras.put("order_uid",info[0]);
		paras.put("sid",info[1]);
		paras.put("stype","");
		if(info.length >= 7)
			paras.put("oas_orderid",info[6]);

		paras.put("roleid",info[2]);
		paras.put("ext",info[3]);
		paras.put("trace_signture",URLEncoder.encode(p.getSignature()));
		paras.put("trace_data",p.getOriginalJson());
		paras.put("sign",MD5Encrypt.StringToMD5(PhoneInfo.instance().gamecode + MyApplication.PUBLICKEY + p.getOrderId() + p.getToken() + p.getSku() + info[0] + info[1] + info[2]));
		new BasesDao().post(url.toString(), paras, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				boolean flag = isSuccess("checkPurchaseForGoogle",s ,callback);
				if(!flag) {
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
					return;
				}
				OrderInfo info = new OrderInfo();
				try {
					BasesJsonParser.newInstance().parserJson2Obj(s, info);
				}catch (Exception e){
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
					return;
				}
				callback.success(info, BasesConstant.RESULT_SUCCESS, "");
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callback.exception(new BasesNetworkErrorException(""));
			}
		});
//		result = HttpDao.instance().submit(new RequestEntity(url.toString(), true));
//		BasesUtils.logDebug("OAS-HttpService", "checkPurchaseForGoogle() return result:" + result);
//		try {
//			JSONObject o = new JSONObject(result);
//
////			if("ok".equalsIgnoreCase(o.getString("status"))){
////				return 0;
////			}else{
//				String errorCode = o.getString("code");
//
//				BasesUtils.logError(TAG, "发钻请求结果：OasisOrderid=" + (info.length >= 7 ? info[6] : "") + ", GoogleOrderid=" + p.getOrderId() + ", uid=" + info[0] + ", sid=" + info[1] + ", roleid=" + info[2] + ", ext=" + info[3] + ", Result Code=" + errorCode + "");
//
//				if(TextUtils.isEmpty(errorCode)){
//					return 1000100;
//				}
//				return Integer.valueOf(errorCode);
////			}
//		} catch (JSONException e) {
//			return 1000100;// 请求发送成功，但服务器返回格式不正确，处理为1000100，失败
//		}
	}

	/**
	 * 提交服务器验证支付是否被允许
	 * @throws BasesNetworkErrorException
	 *
	 */
	public void checkPayAuth(String orderid, final CallbackResultForActivity callback) {

		StringBuffer url = getUrl("a=Order&m=PayAuth");
		url.append("&order_id="+orderid);
		url.append("&sign="+MD5Encrypt.StringToMD5(MyApplication.userInfo.uid + orderid + MyApplication.PUBLICKEY));

		new BasesDao().post(url.toString(), null, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				BasesUtils.logError(TAG, "请求源：checkPayAuth" +  "\n" + "响应结果" + s);
				try {
					JSONObject o = new JSONObject(s);
					if(!"ok".equalsIgnoreCase(o.getString("status"))){
						callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, o.getString("error"));
						return;
					}

				}catch (Exception e){
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "");
				}

				callback.success(null, BasesConstant.RESULT_SUCCESS, "");
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callback.exception(new BasesNetworkErrorException(""));
			}
		});

	}

	/**
	 * 获取第三方交易套餐
	 * @throws BasesNetworkErrorException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getPayKindsInfo(final CallbackResultForActivity callback) {

		StringBuffer url = getUrl("a=System&m=GetServersRole");

//		url.append("&uid=" + BasesApplication.userInfo.uid);//"20000005619435");//
		url.append("&ostype=android");
		url.append("&sign=" + MD5Encrypt.StringToMD5(PhoneInfo.instance().gamecode + BasesApplication.PUBLICKEY));
		BasesUtils.logError(TAG, url.toString());
		new BasesDao().post(url.toString(), null, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				boolean flag = isSuccess("getPayKindsInfo", s, callback);
				if (!flag) {
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "数据格式异常");
					return;
				}
				PayInfoList pil = new PayInfoList();
				try {
					JSONObject o = new JSONObject(s);
					pil.setList(BasesJsonParser.newInstance().parserJSON2ObjList(o.getString("gifts"), new PayInfoDetail()));
					pil.setServers(BasesJsonParser.newInstance().parserJSON2ObjList(o.getString("serverlist"), new ServerInfo()));
				} catch (Exception e) {
					callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, "数据格式异常");
					return;
				}
				callback.success(pil, BasesConstant.RESULT_SUCCESS, "成功");
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callback.exception(new BasesNetworkErrorException(""));
			}
		});

	}
	
	/**
	 * 扫描结束后，根据扫描结果请求更新充值界面
	 * @param code	扫描结果码
	 * @return
	 * @throws BasesNetworkErrorException
	 */
	public boolean toPcRecharge(String code) throws BasesNetworkErrorException {

		StringBuffer url = new StringBuffer("a=pay&m=setPayWish");
		url.append("&sid="+BasesApplication.userInfo.serverID);
		url.append("&roleid="+BasesApplication.userInfo.roleID);
		url.append("&wcode=" + code);
		url.append("&token=" + BasesApplication.userInfo.token);
		url.append("&sign="+MD5Encrypt.StringToMD5(code+BasesApplication.userInfo.uid+PhoneInfo.instance().gamecode+BasesApplication.userInfo.serverID+BasesApplication.userInfo.roleID+BasesApplication.userInfo.token+BasesApplication.PUBLICKEY));
		
		String result = "";
//		result = HttpDao.instance().submit(getOldUrl(url.toString()));
		try {
			JSONObject o = new JSONObject(result);
			if("ok".equalsIgnoreCase(o.getString("status"))){
				return true;
			}
		} catch (JSONException e) {
			Log.e("HttpService", "toPcRecharge() fail!");
			return false;
		} 
		return false;
	}
	/**
	 * 支付信息日志
	 * @param page 页数
	 * @param page_size 每页记录数
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PayHistoryList paymentLog(int page, int page_size) throws BasesNetworkErrorException, JSONException{
		StringBuffer url = new StringBuffer("http://pay.oasgames.com/oasadmin/api/getRechargeOrders.php?msg=getRecharge");
		String uid = BasesApplication.userInfo.uid;//"200000053568227";//
		url.append("&page="+page);
		url.append("&page_size="+page_size);
		
		url.append("&token="+MD5Encrypt.StringToMD5(uid+PhoneInfo.instance().gamecode+page+"d9411ce0301eb928632daacf1431ec9f"+page_size));
		String res = "";
//		res = HttpDao.instance().submit(new RequestEntity(url.toString()));
		JSONObject json = new JSONObject(res);
		if("ok".equalsIgnoreCase(json.getString("status"))){
			PayHistoryList list = new PayHistoryList();
			list.setGame_code(json.getString("game_code"));
			list.setPage(json.getInt("page"));
			list.setPage_size(json.getInt("page_size"));
			try {
				list.setMsg((List) BasesJsonParser.newInstance().parserJSON2ObjList(json.getJSONArray("msg").toString(), new PayHistoryInfoDetail()));
			} catch (Exception e) {
				throw new BasesNetworkErrorException(e.getMessage());
			}
			return list;
		}
		return null;
	}
	
	/**
	 * 玩家登录游戏服事件接收接口
	 * 为OAS服务器发送玩家数据
	 * @return
	 * @throws BasesNetworkErrorException
	 */
	public String game_play_log() throws BasesNetworkErrorException {
		StringBuffer url = new StringBuffer("a=gamelogin&m=game_play_log");
		
		url.append("&server_id=" + BasesApplication.userInfo.serverID);
		url.append("&role_id="+BasesApplication.userInfo.roleID);

		url.append("&sign="+MD5Encrypt.StringToMD5(PhoneInfo.instance().gamecode+BasesApplication.userInfo.serverID+BasesApplication.userInfo.uid+BasesApplication.PUBLICKEY));
		String res = "";
//		res = HttpDao.instance().submit(getOldUrl(url.toString()));
		return res;
	}
	/**
	 * 根据IP获取国家或地区
	 * @return
	 */
	public void getConutryCodeByIP(){
		new BasesDao().get("http://ipinfo.io/json", new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				try {
					JSONObject o = new JSONObject(s);

					PhoneInfo.instance().setIpToCountry(o.getString("country"));
					BasesUtils.logDebug("getConutryCodeByIp()", "Get country by ipinfo is failed." + o.getString("country"));
				} catch (Exception e) {
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				BasesUtils.logDebug("getConutryCodeByIp()", "Get country by ipinfo is failed.");
			}
		});

	}
	/**
	 * 向MData发送数据
	 * @param info
	 * @throws BasesNetworkErrorException
	 */
	public void sendToMdataInfo(final ReportMdataInfo info) throws BasesNetworkErrorException {
//		RequestEntity re = new RequestEntity("http://10.1.9.135/r2.php");
		String main = "us.";
		if("tr.".equals(PhoneInfo.instance().getIpToCountryWithHttp()) ||
				"us.".equals(PhoneInfo.instance().getIpToCountryWithHttp()) ||
				"br.".equals(PhoneInfo.instance().getIpToCountryWithHttp()) ||
				"cn.".equals(PhoneInfo.instance().getIpToCountryWithHttp()) ){
			main = PhoneInfo.instance().getIpToCountryWithHttp();
		}

		new BasesDao().postMdata("http://" + main + "mdata.cool/mdata.php", info.content, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				BasesUtils.logError(TAG, "sendToMdataInfo:success.eventName=" + info.eventName + ";" + s);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {

				BasesUtils.logError(TAG, "sendToMdataInfo:exception." + info.eventName);
			}
		});
	}

	private Boolean isSuccess(String methodName, String result, CallbackResultForActivity callback){
		JSONObject o = null;
		boolean isScucess = true;
		try {
			o = new JSONObject(result);
			if(!"ok".equalsIgnoreCase(o.getString("status"))){
				isScucess = false;
			}
		}catch (Exception e){
			isScucess = false;
		}
		BasesUtils.logError(TAG, "请求源：" + methodName + "\n" + "响应结果" + result);
//		if(!isScucess) {
//			callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, result);
//		}
		return isScucess;
	}

	private StringBuffer getUrl(String url){
		StringBuffer sb = new StringBuffer(Constant.BASEURL+url);
		return sb.append(PhoneInfo.instance().toString());
	}
	private StringBuffer getUrlForLogin(String url){
		StringBuffer sb = new StringBuffer(Constant.BASEURL+url);
		return sb.append(PhoneInfo.instance().toStringForLogin());
	}
}
