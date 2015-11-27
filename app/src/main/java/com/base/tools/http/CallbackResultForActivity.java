package com.base.tools.http;



public interface CallbackResultForActivity {

	/**
	 * 处理正确结果
	 */
	abstract void success(Object data, int statusCode, String msg);
	
	/**
	 * 异常
	 * @param e
	 */
	abstract void exception(Exception e);
	
	/**
	 * 处理错误结果
	 */
	abstract void fail(int statusCode, String msg);
}