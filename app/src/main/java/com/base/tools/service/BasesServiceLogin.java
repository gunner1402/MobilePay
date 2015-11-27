package com.base.tools.service;

import android.text.TextUtils;
import android.util.Log;

import com.base.tools.BasesApplication;
import com.base.tools.entity.MemberBaseInfo;
import com.base.tools.entity.PhoneInfo;
import com.base.tools.entity.UserInfo;
import com.base.tools.exception.BasesDataErrorException;
import com.base.tools.exception.BasesNetworkErrorException;
import com.base.tools.http.CallbackResultForActivity;
import com.base.tools.http.HttpClient;
import com.base.tools.utils.AESUtils;
import com.base.tools.utils.BasesConstant;
import com.base.tools.utils.BasesUtils;
import com.base.tools.utils.MD5Encrypt;
import com.mopub.volley.Response;
import com.mopub.volley.VolleyError;
import com.oasgames.android.oaspay.activity.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by xdb on 2015/10/12.
 */
public class BasesServiceLogin {
    final String TAG = BasesServiceLogin.class.getSimpleName();
    /**
     * 最近登录的用户信息
     */
    public static final String SHAREDPREFERENCES_RECENTLYUSERINFOS = "recentlyuserinfos";
    private static final String SPLITCHAR = "OASUSER";// 分隔符，避免截取出错
    /**
     * 使用最近登录账号进行登录，只支持登录过的注册账号(即新注册账号)
     * @param url   请求地址
     * @param callback  回调方法
     */
    public void loginWithRecentlyRegitUser(StringBuffer url,CallbackResultForActivity callback){

        List<MemberBaseInfo> list = BasesUtils.getSPMembers();
        if(list!=null && list.size() > 0 && null != list.get(0)){
            String username = list.get(0).memberName;
            String password = list.get(0).password;
            if( !TextUtils.isEmpty(username)
                    && !TextUtils.isEmpty(password)){
                password = AESUtils.decrypt(password);
                if(!TextUtils.isEmpty(password)){
                    login(url,MemberBaseInfo.USER_REGISTED, username, password, username, callback);// OAS账号
                }
            }
        }
    }
    public void loginWithRecentlyToken(StringBuffer url,CallbackResultForActivity callback){
        String[] oldUserInfo = getCacheUserInfo();
        if(oldUserInfo == null) {
            callback.fail(BasesConstant.RESULT_FAIL, "");
            return;
        }
//        uid, token, username, password, platform, oasnickname
        loginByToken(url, oldUserInfo[0], oldUserInfo[1], oldUserInfo[2], oldUserInfo[3], oldUserInfo[4], oldUserInfo.length>5?oldUserInfo[5]:"", callback);
    }
    /**
     * 自动登录（二次登录），通过某唯一标示验证
     * @param url   请求地址
     * @param callback  回调方法
     * @param password	 usertype=2时，玩家输入的密码，
     * @param uid       登录账号uid
     * @param oasToken  登录验证的唯一标示
     * @param username  用户名
     * @param password  密码
     * @param platform  账号平台类型
     * @param nickName  昵称（各平台上的昵称）
     */
    private void loginByToken(StringBuffer url, String uid, String oasToken, String username, String password, String platform, String nickName, CallbackResultForActivity callback){
        int usertype = MemberBaseInfo.checkUserType(platform);
        url.append("&usertype="+usertype);
        url.append("&oas_token="+oasToken);
        url.append("&uid="+uid);

        if(MemberBaseInfo.isOther(platform))
            url.append("&platform="+platform);

        url.append("&sign="+ MD5Encrypt.StringToMD5(PhoneInfo.instance().mobilecode+ PhoneInfo.instance().gamecode+usertype+oasToken+ MyApplication.PUBLICKEY));
        loginAndRegist(platform, username, password, nickName, url, callback);
    }
    /**
     * 注册新用户
     * @param requestUrl    请求地址
     * @param username      用户名
     * @param password      密码
     * @return  成功	{status:"ok",uid:"20000000012345678",type:”2”,token:"690c122e35e2681fb34f9fef236396d0"}；type=1 是免注册用户，type=2 是正常用户
     * 			失败     {status:"fail",error:"错误编号",err_msg:”错误描述”}
     */
    public void register(StringBuffer requestUrl, String username, String password, CallbackResultForActivity callback){

        loginAndRegist(MemberBaseInfo.USER_REGISTED, username, password, username, requestUrl, callback);

    }

    /**
     * 登录和注册
     * @param username			usertype=1时，传手机唯一码
                                usertype=2时，已注册账号（用户名）
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
     */
    public void login(StringBuffer requestUrl, String platform, String username, String password, String oasNickName, CallbackResultForActivity callback){

        loginAndRegist(platform, username, password, oasNickName, requestUrl, callback);

    }

    private void loginAndRegist(final String platform, final String username, final String password, final String oasNickName, StringBuffer url, final CallbackResultForActivity callback) {
        String result = "";
        BasesUtils.logError(TAG, "登录URL："+url.toString());
        new BasesDao().post(url.toString(), null, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject json = new JSONObject(s);
                    handLoginData(json, platform, username, password, oasNickName);
                    callback.success(BasesApplication.userInfo, BasesConstant.RESULT_SUCCESS, "success");
                } catch (Exception e) {
                    callback.fail(BasesConstant.RESULT_EXCEPTION, e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.exception(new BasesNetworkErrorException(volleyError.getMessage()));
            }
        });
    }


    private void handLoginData(JSONObject o, String platform, String username, String password, String oasNickName) throws BasesDataErrorException {
        try {
            if(BasesApplication.userInfo == null)
                BasesApplication.userInfo = new UserInfo();

            if("ok".equalsIgnoreCase(o.getString("status"))){

                // 登录成功，缓存用户信息
                BasesApplication.userInfo.setStatus("ok");
                BasesApplication.userInfo.setUid(o.getString("uid"));
                BasesApplication.userInfo.setToken(o.getString("token"));
                if(o.has("user_type"))
                    BasesApplication.userInfo.setUser_type(""+o.getInt("user_type"));
                BasesApplication.userInfo.setError("");
                BasesApplication.userInfo.setErr_msg("");


                if(o.has("platform"))
                    BasesApplication.userInfo.setPlatform(o.getString("platform"));
                else
                    BasesApplication.userInfo.setPlatform(platform);

                if(BasesApplication.userInfo.platform.equals(MemberBaseInfo.USER_NONE))
                    BasesApplication.userInfo.setPlatform_token("");
                else if(BasesApplication.userInfo.platform.equals(MemberBaseInfo.USER_REGISTED))
                    BasesApplication.userInfo.setPlatform_token("");
                else
                    BasesApplication.userInfo.setPlatform_token(password);// 第三方token

                if(o.has("username"))
                    BasesApplication.userInfo.setUsername(o.getString("username"));
                else
                    BasesApplication.userInfo.setUsername(username);

                if(o.has("nickname"))
                    BasesApplication.userInfo.setNickname(o.getString("nickname"));
                else
                    BasesApplication.userInfo.setNickname(oasNickName);

                if(o.has("avatar_pic"))
                    BasesApplication.userInfo.setAvatar_pic(o.getString("avatar_pic"));

                cacheUserInfo(BasesApplication.userInfo.uid, BasesApplication.userInfo.token, BasesApplication.userInfo.username, password, BasesApplication.userInfo.platform, BasesApplication.userInfo.nickname);
            }else{
                // 登录失败，在当前缓存上更新状态
                BasesApplication.userInfo.setStatus("fail");

                BasesApplication.userInfo.setError(o.getString("error"));
                BasesApplication.userInfo.setErr_msg(o.getString("err_msg"));
            }

        } catch (JSONException e) {
            Log.e("HttpService", "Result not json. Init BasesApplication.userInfo fail!");
            throw new BasesDataErrorException("Login fail. Return data format error.");
        }
    }
    /**
     * 缓存用户信息
     * @param username			usertype=1时，传手机唯一码
    usertype=2时，玩家输入的OAS平台账号（邮箱格式）
    usertype=3时，传入第三方的平台代码，例如facebook、twiiter、google
     * @param password			usertype=2时，玩家输入的密码，
    usertype=3时，传入第三方的平台token
     * @param platform			登录平台
     * @param oasNickName			oas昵称
     */
    private void cacheUserInfo(String uid, String token, String username, String password, String platform, String oasNickName){
        // 保存最近登录用户的信息，作为下次登录验证的凭据
        StringBuffer buf = new StringBuffer();
//        buf.append(userType);
//        buf.append(SPLITCHAR);
        buf.append(uid);
        buf.append(SPLITCHAR);
        buf.append(token);
        buf.append(SPLITCHAR);
        buf.append(username);
        buf.append(SPLITCHAR);
        buf.append(password);
        buf.append(SPLITCHAR);
        buf.append(platform);
        buf.append(SPLITCHAR);
        buf.append(oasNickName);
        buf.append(SPLITCHAR);
        BasesApplication.settingEditor.putString(SHAREDPREFERENCES_RECENTLYUSERINFOS, buf.toString());
        BasesApplication.settingEditor.commit();

        if(platform.equals(MemberBaseInfo.USER_REGISTED))// 非OAS账号登录或有异常，直接返回结果，不保存OAS用户信息
            BasesUtils.cacheUserInfo(username, password);
    }

    public void deleteCacheUserInfo(){
        BasesApplication.settingEditor.putString(SHAREDPREFERENCES_RECENTLYUSERINFOS, "");
        BasesApplication.settingEditor.commit();
    }
    /**
     * uid, token, username, password, platform, oasnickname
     * @return
     */
    private String[] getCacheUserInfo(){
        String oldUserInfo = BasesApplication.setting.getString(SHAREDPREFERENCES_RECENTLYUSERINFOS, "");
        if(TextUtils.isEmpty(oldUserInfo))
            return null;

        return oldUserInfo.split(SPLITCHAR);
    }
    /**
     * 修改密码
     * @throws BasesNetworkErrorException
     * @throws BasesDataErrorException
     */
    public void modifyPwd(StringBuffer reqeustUrl, final String newpassword, final CallbackResultForActivity callback) throws BasesNetworkErrorException, BasesDataErrorException {

        new HttpClient(reqeustUrl.toString(), null, new HttpClient.Callback() {

            @Override
            public void handleResultData(String result) {

                try {
                    JSONObject json = new JSONObject(result);
                    if("ok".equalsIgnoreCase(json.getString("status"))){
                        BasesApplication.userInfo.setToken(json.getString("token"));// 此处要更新原有token，修改密码会导致旧token失效。
                        cacheUserInfo(BasesApplication.userInfo.uid, BasesApplication.userInfo.token,
                                BasesApplication.userInfo.username, newpassword, BasesApplication.userInfo.platform, BasesApplication.userInfo.nickname);
                        callback.success(BasesApplication.userInfo, BasesConstant.RESULT_SUCCESS, "success");
                    }else{
                        callback.fail(BasesConstant.RESULT_FAIL_DATAERROR, json.has("msg")?json.getString("msg"):"Not error message!");
                    }
                } catch (Exception e) {
                    callback.fail(BasesConstant.RESULT_EXCEPTION, e.getMessage());
                }
            }

            @Override
            public void handleErorrData(VolleyError error) {
                callback.exception(new BasesNetworkErrorException(error.getMessage()));
            }
        }).submitGet();

    }
}
