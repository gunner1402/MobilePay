/**
 * 应用工具类
 */
package com.base.tools.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.base.tools.BasesApplication;
import com.base.tools.entity.MemberBaseInfo;
import com.base.tools.entity.PhoneInfo;
import com.mopub.volley.Response;
import com.mopub.volley.VolleyError;
import com.mopub.volley.toolbox.ImageRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @author xdb
 * 
 */
public class BasesUtils {
	/**
	 * 获取FB的key hash
	 */
	public static void getFBKeyHash(Activity activity){

		//获取当前应用的 key hash（分为签名与非签名两种）
		try {
			PackageInfo info = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String sign = Base64
						.encodeToString(md.digest(), Base64.DEFAULT);
				Log.e("FB KEY HASH:", sign);
			}
		} catch (NameNotFoundException e) {
		} catch (NoSuchAlgorithmException e) {
		}
	}
	/**
	 * 获取手机基本信息
	 * @param c
	 * @return
	 */
	public static PhoneInfo getPhoneInfo(Context c){
		PhoneInfo info = PhoneInfo.instance();
//		TelephonyManager tm = (TelephonyManager) c.getSystemService(Service.TELEPHONY_SERVICE);
//		info.setDeviceId(tm.getDeviceId());
		info.setModel(android.os.Build.MODEL);
		info.setBrand(android.os.Build.BRAND);
		info.setSoftwareType("android");//android.os.Build.TYPE
		info.setSoftwareVersion(android.os.Build.VERSION.RELEASE);

		info.setAndroidID(android.provider.Settings.Secure.getString(c.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID));

		/**
		 * 获取版本号
		 */
		try {
			PackageManager manager = c.getPackageManager();
			PackageInfo packinfo = manager.getPackageInfo(c.getPackageName(), 0);
			info.setBundleid(packinfo.packageName);
			info.setBundleversion(packinfo.versionName);
			info.setBundleversioncode(""+packinfo.versionCode);
		} catch (Exception e) {
			e.printStackTrace();
		}

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
		info.setReferrer(preferences.getString("referrer", ""));
		return info;
	}
	/**
	 * 获取手机唯一码
	 * 优先顺序：	1、游戏方传来的唯一码（肯能为任意字符）
	 * 			2、DeviceID
	 * 			3、AndroidID
	 * 			4、"OAS_ANDROID_"+System.nanoTime()
	 * @return
	 */
	public static String getMobileCode(){
		if(!TextUtils.isEmpty(PhoneInfo.instance().mobilecode))
			return PhoneInfo.instance().mobilecode;

		String mobileCode = (BasesApplication.setting==null)?"":BasesApplication.setting.getString("notRegistUserName", "");

		if(TextUtils.isEmpty(mobileCode))
			mobileCode = PhoneInfo.instance().androidID;

		if(TextUtils.isEmpty(mobileCode))
			mobileCode = "OAS_ANDROID_"+System.nanoTime();

		PhoneInfo.instance().setMobilecode(mobileCode);

		BasesApplication.settingEditor.putString("notRegistUserName", mobileCode);
		BasesApplication.settingEditor.commit();

		return mobileCode;
	}
	private static void showMsg(Activity activity, String msg, int type) {
		if (type == Toast.LENGTH_LONG)
			Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
		else if (type == Toast.LENGTH_SHORT)
			Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
	}
	/**
	 * Toast显示提示信息
	 *
	 * @param activity
	 * @param msg
	 *            显示内容
	 */
	public static void showMsg(Activity activity, String msg) {
		showMsg(activity, msg, Toast.LENGTH_LONG);
	}

	/**
	 * 加载更多
	 * 
	 * @return
	 */
	public static View getLoadMoreFootView(Activity activity, int layout) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View view = inflater.inflate(layout, null);
		return view;
	}

	/**
	 * 最后一行
	 * 
	 * @return
	 */
	public static View getEndViewFootView(Activity activity, int layout) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View view = inflater.inflate(layout, null);
		return view;

	}

	/**
	 * 没有数据
	 * 
	 * @return
	 */
	public static View getNotDataFootView(Activity activity, int layout) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View view = inflater.inflate(layout, null);
		return view;

	}

	/**
	 * 判断存储卡是否存在 
	 * @return
	 */
	public static boolean checkSDCard() {

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取SD卡剩余空间.
	 * 
	 * @return
	 */
	public static long getSDCardAvailableBlocks() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			return sf.getAvailableBlocks();
		} else
			return 0;
	}


	/**
	 * 公共消息提示框（系统UI）,如果某些按钮不用，请将“按钮显示内容”置为null
	 * @param c
	 * @param message   提示内容
	 * @param negativeText   按钮显示内容（消极, 左边）
	 * @param neutralText   按钮显示内容（中性, 中间）
	 * @param positiveText   按钮显示内容（积极，, 右边）
	 */
	public static void showDialogBySystemUI(Context c, String message,
											String negativeText, DialogInterface.OnClickListener negativeListener,
											String neutralText, DialogInterface.OnClickListener neutralListener,
											String positiveText, DialogInterface.OnClickListener positiveListener){
		AlertDialog.Builder d = new AlertDialog.Builder(c);

		d.setCancelable(false);
		d.setMessage(message);
		if(!TextUtils.isEmpty(negativeText))
			d.setNegativeButton(negativeText, negativeListener);
		if(!TextUtils.isEmpty(positiveText))
			d.setPositiveButton(positiveText, positiveListener);
		if(!TextUtils.isEmpty(neutralText))
			d.setNeutralButton(neutralText, neutralListener);

		d.show();
	}// end of showDialogBySystemUI

	/**
	 * 单选列表对话框（系统UI）
	 * @param c
	 * @param items		列表展示数据
	 * @param checkedItem	默认选中Item索引（-1表示不选中任何项）
	 * @param listener		选中监听事件
	 */
	public static void showSingleChoiceDialogListBySystemUI(Context c, String[] items, int checkedItem, DialogInterface.OnClickListener listener){
		AlertDialog.Builder d = new AlertDialog.Builder(c);
		d.setSingleChoiceItems(items, checkedItem, listener);

		d.show();
	}// end of showSingleChoiceDialogListBySystemUI
	/**
	 * 
	 * 进度加载方法
	 */
	public static ProgressDialog loadProgress(Activity activity) {
		// 带进度条的对话框
		ProgressDialog mydialog = new ProgressDialog(activity);
		mydialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		mydialog.setTitle("数据加载提示");
//		mydialog.setMessage("Loading ...");
		mydialog.setIndeterminate(true);
		mydialog.show();
		mydialog.setContentView(BasesUtils.getResourceValue("layout", "oasisgames_sdk_common_waiting_anim"));
		return mydialog;
	}
	
	/**
	 * 
	 * @param activity
	 * @param layout
	 * @return
	 */
	public static AlertDialog createWaitDialog(Activity activity, int layout) {
		AlertDialog dialog_wait = new AlertDialog.Builder(activity).create();
		dialog_wait.show();
		if (layout == -1)// 默认样式
			layout = BasesUtils.getResourceValue(activity, "layout", "oasisgames_sdk_common_waiting_anim");
		dialog_wait.setContentView(layout);
		dialog_wait.setCanceledOnTouchOutside(false);
		return dialog_wait;
	}

	public static DisplayMetrics getDisplayMetrics(Activity a){
		if(PhoneInfo.instance().dm == null) {
			DisplayMetrics outMetrics = new DisplayMetrics();
			a.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
			PhoneInfo.instance().dm = outMetrics;
		}
		return PhoneInfo.instance().dm;
	}

	/**
	 * 账号注册时特殊字符验证
	 * @param text
	 * @return
	 */
	public static boolean regexSpecilChar(String text){
		
		return RegexName(text, "^[^&#]+");
	}
	/**
	 * 纯数字格式验证
	 * @param text
	 * @return
	 */
	public static boolean regexNum(String text){
		
		return RegexName(text, "^[0-9]+");
	}
	/**
	 * 账号格式验证  0-9a-zA-Z 下划线
	 * @param text
	 * @return
	 */
	public static boolean regexAccount(String text){
		
		return RegexName(text, "^[a-zA-Z0-9_]+");
	}
	static String emailRegEx = "^([a-z0-9A-Z]+[-|\\._]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"; 
	/**
	 * 邮箱格式验证
	 * @param email
	 * @return
	 */
	public static boolean regexEmail(String email){
		
		return RegexName(email, emailRegEx);
	}
	/**
	 * 正则验证
	 * 
	 * @return
	 */
	public static boolean RegexName(String str, String eg) {
		Matcher m = Pattern.compile(eg,
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(str);
		return m.matches();
	}

	/**
	 * 通过Bitmap形式设置背景，在activity销毁时，一定调用distoryBackgroudByBitmap
	 * 
	 * @param r
	 * @param view
	 * @param imageId
	 */
	public static void setBackgroudByBitmap(Resources r, View view, int imageId) {
		if (null != view) {
			Bitmap bm = BitmapFactory.decodeResource(r, imageId);
			BitmapDrawable bd = new BitmapDrawable(r, bm);
			view.setBackgroundDrawable(bd);
		}
	}

	/**
	 * 销毁Bitmap，减少内存
	 * 
	 * @param r
	 * @param view
	 */
	public static void distoryBackgroudByBitmap(Resources r, View view) {
		BitmapDrawable bd = null;
		if (null != view) {
			bd = (BitmapDrawable) view.getBackground();
			view.setBackgroundResource(0);// 别忘了把背景设为null，避免onDraw刷新背景时候出现used a
											// recycled bitmap错误
			bd.setCallback(null);
			bd.getBitmap().recycle();
		}
	}

	public static void cacheUserInfo(String memberName, String memberPwd) {
		if(TextUtils.isEmpty(memberPwd))// 密码为空，不报存账户信息 
			return;
		
		// 需要考虑更加安全的方式

		// OAS 账号，需要保存账号及密码，其余类型不用处理
		List<MemberBaseInfo> memberBaseInfos = getSPMembers();
		String userinfos = memberName + "/"+AESUtils.encrypt(memberPwd);
		if(null == memberBaseInfos || memberBaseInfos.size() <= 0){
			BasesApplication.settingEditor.putString("members", userinfos);
			BasesApplication.settingEditor.commit();// 编辑器提交保存
			return;
		} 

		checkUser(memberName, memberPwd, memberBaseInfos);// 检查是否已存在相同用户信息，存在先移除该用户信息
		int index = 0;
		// 最终保存的用户信息都在list中
		for (MemberBaseInfo user : memberBaseInfos) {
			if(index >= 2)// 最多存3个，所以此处为2
				continue;
			String uname = user.memberName;
			String pwd = user.password;
			String userinfo = uname + "/" + pwd;
			if (userinfos == "") {
				userinfos = userinfo;
			} else {
				userinfos += "," + userinfo;
			}
			index ++;
		}

		BasesApplication.settingEditor.putString("members", userinfos);
		BasesApplication.settingEditor.commit();// 编辑器提交保存
	}

	public static void deleteUserInfo(String memberName, String memberPwd){
		List<MemberBaseInfo> memberBaseInfos = getSPMembers();
		if(null == memberBaseInfos || memberBaseInfos.size() <= 0){
			return;
		}
		String userinfos = "";
		for (MemberBaseInfo user : memberBaseInfos) {
			
			String uname = user.memberName;
			String pwd = user.password;
			
			if(uname.equals(memberName)){
				continue;
			}
			
			String userinfo = uname + "/" + pwd;
			if ("".equals(userinfos)) {
				userinfos = userinfo;
			} else {
				userinfos += "," + userinfo;
			}
		}

		BasesApplication.settingEditor.putString("members", userinfos);
		BasesApplication.settingEditor.commit();
	}
	// 得到用户信息
	public static List<MemberBaseInfo> getSPMembers() {
		List<MemberBaseInfo> memberBaseInfos = new ArrayList<MemberBaseInfo>();// 用于保存用户列表信息
		String userinfos = (BasesApplication.setting==null)?"":BasesApplication.setting.getString("members", "");// 取得所有用户信息
		// 获得用户字串
		if (userinfos != "")// 有数据
		{
			// name1/pwd1,name2/pwd2
			if (userinfos.contains(","))// 判断有无, 逗号代表用户每个用户分割点
			{
				String[] users = userinfos.split(",");
				for (String str : users) {
					MemberBaseInfo memberBaseInfo = new MemberBaseInfo();
					String[] user = str.split("/");
					if(user.length>=1)
						memberBaseInfo.memberName = TextUtils.isEmpty(user[0])?"":user[0];// 用户名
					if(user.length>=2)	
						memberBaseInfo.password = TextUtils.isEmpty(user[1])?"":user[1];// 密码
					memberBaseInfos.add(memberBaseInfo);
				}
			} else {
				// 没有, 代表只有一个用户
				MemberBaseInfo memberBaseInfo = new MemberBaseInfo();
				String[] user = userinfos.split("/");
				if(user.length>=1)
					memberBaseInfo.memberName = TextUtils.isEmpty(user[0])?"":user[0];// 用户名
				if(user.length>=2)	
					memberBaseInfo.password = TextUtils.isEmpty(user[1])?"":user[1];// 密码
				memberBaseInfos.add(memberBaseInfo);
			}
			return memberBaseInfos;
		} else {
//			MemberBaseInfo memberBaseInfo = new MemberBaseInfo();
//			memberBaseInfo.memberName = "user1";
//			memberBaseInfo.password ="1";
//			memberBaseInfos.add(memberBaseInfo);
//			
//			memberBaseInfo = new MemberBaseInfo();
//			memberBaseInfo.memberName = "user2";
//			memberBaseInfo.password ="2";
//			memberBaseInfos.add(memberBaseInfo);
			return memberBaseInfos;
		}
	}

	// 检查是否包含此用户名 没有包含就保存到?
	private static void checkUser(String memberName, String memberPwd,
			List<MemberBaseInfo> memberBaseInfos) {
		int position = -1;
		int num = memberBaseInfos.size();
		for (int i = 0; i <num; i++) {
			if (memberName.equals(memberBaseInfos.get(i).memberName)) {
				position = i;
				break;
			}
		}
		if (position >= 0) {// 已存在
			memberBaseInfos.remove(position);
		}
//		MemberBaseInfo memberBaseInfo = new MemberBaseInfo();
//		memberBaseInfo.memberName = memberName;
//		memberBaseInfo.password = memberPwd;
//		memberBaseInfos.add(memberBaseInfo);
	}
	
	/**
	 * 动态获取资源
	 * @param type
	 * @param name
	 * @return
	 */
	public static int getResourceValue(String type, String name){
		Class r = null;
        int id = 0;
        try
        {
        	r = Class.forName(BasesApplication.packageName + ".R");

            Class[] classes = r.getClasses();
            Class desireClass = null;

            for (int i = 0; i < classes.length; i++)
            {
                if (classes[i].getName().split("\\$")[1].equals(type))
                {
                    desireClass = classes[i];
                    break;
                }
            }

            if (desireClass != null)
                id = desireClass.getField(name).getInt(desireClass);
        }catch(Exception e){
        	e.printStackTrace();
        }
		return id;
	}
	public static int getResourceValue(Activity c, String type, String name){
		if(TextUtils.isEmpty(BasesApplication.packageName)){
			PackageManager manager = c.getPackageManager();
	        try {
				PackageInfo packinfo = manager.getPackageInfo(c.getPackageName(), 0);
				BasesApplication.packageName = packinfo.packageName;
			} catch (NameNotFoundException e) {
			}
		}
		
		return getResourceValue(type, name);
	}
	public static int[] getResourceValueByStyleable(String name){
		Class r = null;
		int[] id = null;
		try
		{
			r = Class.forName(BasesApplication.packageName + ".R");
			
			Class[] classes = r.getClasses();
			Class desireClass = null;
			
			for (int i = 0; i < classes.length; i++)
			{
				if (classes[i].getName().split("\\$")[1].equals("styleable"))
				{
					desireClass = classes[i];
					break;
				}
			}
			if ((desireClass != null) && (desireClass.getField(name).get(desireClass) != null) && (desireClass.getField(name).get(desireClass).getClass().isArray()))  
		        id = (int[])desireClass.getField(name).get(desireClass);
		}catch(Exception e){
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * 日志打印
	 * @param logLevel 日志级别
	 * @param tag		日志tag
	 * @param msg		日志消息
	 */
	private static void printLog(int logLevel, String tag, String msg){
		if(Log.DEBUG == logLevel){
			if(BasesApplication.OASISSDK_ENVIRONMENT_SANDBOX)
				Log.d(tag, msg);
		}else if(Log.WARN == logLevel){
			Log.w(tag, msg);
		}else if(Log.ERROR == logLevel){
			Log.e(tag, msg);
		}
	}
	public static void logDebug(String tag, String msg){
		printLog(Log.DEBUG, tag, msg);
	}
	public static void logWarn(String tag, String msg){
		printLog(Log.WARN, tag, msg);
	}
	public static void logError(String tag, String msg){
		printLog(Log.ERROR, tag, msg);
	}
	/**
	 * 是否登录成功，通过BasesApplication.userInfo 判断是否登录成功
	 * @return
	 */
	public static boolean isLogin(){
		if(BasesApplication.userInfo != null && !TextUtils.isEmpty(BasesApplication.userInfo.uid) && !TextUtils.isEmpty(BasesApplication.userInfo.token))
			return true;
		return false;
	}
	
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static void cacheLog(int type, String log){
		if(BasesApplication.OASISSDK_ENVIRONMENT_SANDBOX){
			if(BasesApplication.logLists == null)
				BasesApplication.logLists = new ArrayList<String>();
			BasesApplication.logLists.add(0, "<B>【"+sdf.format(new Date())+"】【"+(type==1?"GAME":"SDK")+"】</B>"+log+"<br>");
		}
		
		if(BasesApplication.logListsSD == null)
			BasesApplication.logListsSD = new ArrayList<String>();
		
		if(type == 1)// 将游戏调用的接口顺序作为日志存入SD卡，通过 ReportTimer完成写文件操作
			BasesApplication.logListsSD.add("【"+sdf.format(new Date())+"】"+log+"\r\n\r\n");
	}
	public static String Bitmap2Base64String(String path) {
		if(TextUtils.isEmpty(path))
			return "";
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeFile(path);// 处理内存溢出
		} catch (OutOfMemoryError e1) {
			bm = getSmallBitmap(path, 480, 800);
		}
		if(bm == null)
			return "";
		
		String farmat = path.substring(path.lastIndexOf(".")+1);
		CompressFormat cf = CompressFormat.JPEG;
		if("jpg".equals(farmat) || "jpeg".equals(farmat)
				|| "JPG".equals(farmat) || "JPEG".equals(farmat))
			cf = CompressFormat.JPEG;
		else if("png".equals(farmat) || "PNG".equals(farmat))
			cf = CompressFormat.PNG;
			
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			int options = 100;
			bm.compress(cf, options, baos);
			double maxSize = 1024*500; // 2015-09-09将此值更新为500K  
			
			long size = baos.toByteArray().length;
			    
			if(size > maxSize){
				double f = maxSize/size;
				int v = (int)(f*100);// 得到压缩百分比
				
				if(v>0 && v<100){
					baos.reset();
					bm.compress(cf, v, baos);
				}
			}
		} catch (Exception e) {
			if(bm != null)
				bm.recycle();
			bm = null;
			return "";
		}
		if(bm != null)
			bm.recycle();
		bm = null;
		return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
	}
	public static String Bitmap2Base64String(Bitmap bm) {
		return Base64.encodeToString(Bitmap2Bytes(bm), Base64.DEFAULT);
	}
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 100;
		bm.compress(CompressFormat.JPEG, options, baos);
		double maxSize = 1024*1024; // 1M  
		
		long size = baos.toByteArray().length;
	        
		if(size > maxSize){
			double f = maxSize/size;
			int v = (int)(f*100);// 得到压缩百分比
			
			if(v>0 && v<100){
				baos.reset();
				bm.compress(CompressFormat.JPEG, v, baos);
			}
		}
		
		return baos.toByteArray();
	}
	//计算图片的缩放值
	public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {
	             final int heightRatio = Math.round((float) height/ (float) reqHeight);
	             final int widthRatio = Math.round((float) width / (float) reqWidth);
	             inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	        return inSampleSize;
	}
	// 根据路径获得图片并压缩，返回bitmap用于显示
	public static Bitmap getSmallBitmap(String filePath, int showWidth, int showHeight) {
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        Bitmap b = null;
	        try {
				b = BitmapFactory.decodeFile(filePath, options);
			} catch (OutOfMemoryError e) {
				return null;
			}

	        // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, showWidth, showHeight);

	        // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;

	    try {
			b = BitmapFactory.decodeFile(filePath, options);
		} catch (OutOfMemoryError e) {
			return null;
		}
	    return b;
	    }
	public static boolean checkCameraDevice(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}
	public static void loadImg(Activity activity, final ImageView imageView, String imgUrl){
		if(TextUtils.isEmpty(imgUrl) || "null".equals(imgUrl))
			return;
		ImageRequest iq = new ImageRequest(imgUrl,

				new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap arg0) {
						if(arg0 != null){
							imageView.setImageBitmap(arg0);
							imageView.postInvalidate();
						}
					}
				},
				getDisplayMetrics(activity).widthPixels,//DisplayUtil.dip2px(200, BaseUtils.getDensity()), // 以布局文件为准，200dip
				getDisplayMetrics(activity).heightPixels,//DisplayUtil.dip2px(200, BaseUtils.getDensity()),
				Bitmap.Config.ARGB_8888,
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
					}

				});
		BasesApplication.volleyRequestQueue.add(iq);
	}
	/**
	 * 获取圆角位图的方法
	 * @param bitmap 需要转化成圆角的位图
	 * @param pixels 圆角的度数，数值越大，圆角越大
	 * @return 处理后的圆角位图
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
}
