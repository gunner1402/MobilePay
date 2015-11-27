package com.base.tools.service;

import com.base.tools.BasesApplication;
import com.base.tools.http.MultipartEntity;
import com.base.tools.http.MultipartRequest;
import com.base.tools.utils.BasesUtils;
import com.mopub.volley.NetworkResponse;
import com.mopub.volley.Request;
import com.mopub.volley.Response;
import com.mopub.volley.RetryPolicy;
import com.mopub.volley.VolleyError;
import com.mopub.volley.toolbox.HttpHeaderParser;
import com.mopub.volley.toolbox.JsonRequest;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * http DAO
 * 
 * @author Xdb
 * 
 */
public class BasesDao {
	final String TAG = BasesDao.class.getSimpleName();
//	private final static HttpDao HTTP_DAO = new HttpDao();
//
//	private HttpDao() {
//	}
//
//	/**
//	 * @return 返回逻辑的实例.
//	 */
//	public static HttpDao instance() {
//
//		return HTTP_DAO;
//	}

	public void post(String url, Map<String, String> paras, Response.Listener<String> listener, Response.ErrorListener error){
		BasesUtils.logError(TAG, "请求地址："+url);
		MultipartRequest multipartRequest = new MultipartRequest(
				url, listener, error);
		multipartRequest.setRetryPolicy(new RetryPolicy() {

			@Override
			public void retry(VolleyError arg0) throws VolleyError {
			}

			@Override
			public int getCurrentTimeout() {
				return 30000;
			}

			@Override
			public int getCurrentRetryCount() {
				return 0;
			}
		});

		// 添加header
		multipartRequest.addHeader("header-name", "value");

		// 通过MultipartEntity来设置参数
		MultipartEntity multi = multipartRequest.getMultiPartEntity();

		// 文本参数
		if(paras != null && paras.size() >0) {
			StringBuffer sbuf = new StringBuffer(url+"?");
			for (Map.Entry<String, String> en : paras.entrySet()) {
				multi.addStringPart(en.getKey(), en.getValue());
				sbuf.append("&"+en.getKey()+"="+en.getValue());
			}
			BasesUtils.logError(TAG, sbuf.toString());
		}
		multipartRequest.setShouldCache(false);
		// 将请求添加到队列中
		BasesApplication.volleyRequestQueue.add(multipartRequest);
	}
	public void postMdata(String url, String paras, Response.Listener<String> listener, Response.ErrorListener error){
		BasesUtils.logError(TAG, "请求地址：" + url);
		MyJsonRequest stringRequest = new MyJsonRequest(Request.Method.POST, url, paras, listener, error);
		stringRequest.setRetryPolicy(new RetryPolicy() {

			@Override
			public void retry(VolleyError arg0) throws VolleyError {
			}

			@Override
			public int getCurrentTimeout() {
				return 30000;
			}

			@Override
			public int getCurrentRetryCount() {
				return 0;
			}
		});

		stringRequest.setShouldCache(false);
		// 将请求添加到队列中
		BasesApplication.volleyRequestQueue.add(stringRequest);
	}

	class MyJsonRequest extends JsonRequest<String>{
		 public MyJsonRequest(int method, String url, String content, Response.Listener<String> listener, Response.ErrorListener error){
			 super(method, url, content, listener,error);
		 }

		@Override
		protected Response<String> parseNetworkResponse(NetworkResponse response) {
			String parsed = "";
			try {
				parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			} catch (UnsupportedEncodingException e) {
				parsed = new String(response.data);
			}
			return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
		}
	}

	public void get(String url, Response.Listener<String> listener, Response.ErrorListener error){
		BasesUtils.logError(TAG, "请求地址：" + url);
		MyJsonRequest stringRequest = new MyJsonRequest(Request.Method.GET, url, null, listener, error);
		stringRequest.setRetryPolicy(new RetryPolicy() {

			@Override
			public void retry(VolleyError arg0) throws VolleyError {
			}

			@Override
			public int getCurrentTimeout() {
				return 30000;
			}

			@Override
			public int getCurrentRetryCount() {
				return 0;
			}
		});

		stringRequest.setShouldCache(false);
		// 将请求添加到队列中
		BasesApplication.volleyRequestQueue.add(stringRequest);
	}
}
