package com.oasgames.android.oaspay.tools;

import com.base.tools.utils.BasesUtils;
import com.oasgames.android.oaspay.entity.ReportInfo;
import com.oasgames.android.oaspay.entity.ReportMdataInfo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;

public class ReportUtils {
	private static final String TAG = ReportUtils.class.getSimpleName();
	
	/**
	 * 1.	进入商城TAB（含每次切换） event值：mall
	 * ActivityMain reportEvent()
	 */
	public static final String DEFAULTEVENT_MALL = "mall";
	/**
	 * 2.	进入资讯TAB（含每次切换） event值：information
	 * ActivityMain reportEvent()
	 */
	public static final String DEFAULTEVENT_INFORMATION = "information";
	/**
	 * 3.	进入我TAB（含每次切换）  event值：me
	 * ActivityMain reportEvent()
	 */
	public static final String DEFAULTEVENT_ME = "me";
	/**
	 * 4.	进入商品列表页   event值：productlist
	 * 含“全部道具”、“最新-更多”、“最热-更多”、“最近-更多”
	 * ActivityProductList onCreat();
	 */
	public static final String DEFAULTEVENT_PRODUCTLIST = "productlist";
	/**
	 * 5.	进入我的订单页面（从主菜单进入）event值：fmenutmylist
	 * FragmentShop 点击 “我的订单”项
	 * ActivityMain onClickView();
	 */
	public static final String DEFAULTEVENT_FMENUTMYLIST = "fmenutmylist";
	/**
	 * 6.	进入我的订单页面（从个人中心进入） event值：fmetmylist
	 * FragmentMine 点击 “我的订单”项
	 * FragmentMine MyListener
	 */
	public static final String DEFAULTEVENT_FMETMYLIST = "fmetmylist";
	/**
	 * 7.	进入游戏充值页面  event值：gamepay
	 * ActivityPayPackageList onCreat()
	 */
	public static final String DEFAULTEVENT_GAMEPAY = "gamepay";
	/**
	 * 8.	进入扫码页面   event值：scancode
	 * ActivityCapture onCreat();
	 */
	public static final String DEFAULTEVENT_SCANCODE = "scancode";
	/**
	 * 9.	通过扫码页扫码的数量  event值：scancodenum
	 * ActivityCapture handleDecode();// 扫码成功的数量（不管结果是否正确）
	 */
	public static final String DEFAULTEVENT_SCANCODENUM = "scancodenum";
	/**
	 * 10.	通过扫码页手动输入订单号的数量  event值：scancodeorder
	 * ActivityCaptureInput check();//输入完成，经判断后、向服务器发送请求时，记录该事件
	 */
	public static final String DEFAULTEVENT_SCANCODEORDER = "scancodeorder";
	/**
	 *11.	通过扫码页扫无效码的数量  event值：scancodefail
	 * ActivityCapture handleDecode();// 验证失败的二维码
	 */
	public static final String DEFAULTEVENT_SCANCODEFAIL = "scancodefail";
	/**
	 *12.	支付成功但发钻失败的数量  event值：sdiamondfail
	 * params：orderid（OAS订单id）； uid（用户id）
	 * ActivityGooglePlayBilling check();
	 */
	public static final String DEFAULTEVENT_SDIAMONDFAIL = "sdiamondfail";
	/**
	 *13.	查看礼包详情的数量  event值：gitdetails
	 * params：gitid(礼包id)
	 * ActivityProductDetails onCreate();
	 */
	public static final String DEFAULTEVENT_GITDETAILS = "gitdetails";
	/**
	 *14.	登陆成功的数量  event值：login
	 * 	paras: logintype (未登录：loginno，OAS帐号登录：oas，Facebook帐号登录：facebook，Google帐号登录：google；Twitter帐号登录：twitter)
	 * ActivityLogo 自动登录Callback
	 * ActivityLogin 登录Callback
	 */
	public static final String DEFAULTEVENT_LOGIN = "login";

	
	/**
	 * 数据上报定时器
	 */
	public static Timer reportTimer = new Timer();
	
	public static Queue<ReportInfo> queue = new LinkedList<ReportInfo>();
	public static void add(String eventName, Map<String, String> params, Map<String, String> status){
		synchronized (queue) {

			boolean isSuc = queue.offer(new ReportMdataInfo(eventName, params, status));
			if(isSuc){
				BasesUtils.logDebug(TAG, eventName + " is created success for Mdata！");
			}else{
				BasesUtils.logDebug(TAG, eventName + " is created fail for Mdata！");
			}
		}
	}

	public static Map<String, Integer> localeMap = new HashMap<String, Integer>();

	static{
		localeMap.put("", 2048);// 默认值
		localeMap.put("af", 1078);
		localeMap.put("sq", 1052);
		localeMap.put("ar", 1025);// 自定义ar值
		localeMap.put("ar-sa", 1025);
		localeMap.put("ar-iq", 2049);
		localeMap.put("ar-eg", 3073);
		localeMap.put("ar-ly", 4097);
		localeMap.put("ar-dz", 5121);
		localeMap.put("ar-ma", 6145);
		localeMap.put("ar-tn", 7169);
		localeMap.put("ar-om", 8193);
		localeMap.put("ar-ye", 9217);
		localeMap.put("ar-sy", 10241);
		localeMap.put("ar-jo", 11265);
		localeMap.put("ar-lb", 12289);
		localeMap.put("ar-kw", 13313);
		localeMap.put("ar-ae", 14337);
		localeMap.put("ar-bh", 15361);
		localeMap.put("ar-qa", 16385);
		
		localeMap.put("eu", 1069);		
		localeMap.put("bg", 1026);
		localeMap.put("be", 1059);
		localeMap.put("ca", 1027);
		localeMap.put("zh", 2052);
		localeMap.put("zh-tw", 1028);
		localeMap.put("zh-cn", 2052);
		localeMap.put("zh-hk", 3076);
		localeMap.put("zh-sg", 4100);
		localeMap.put("hr", 1050);
		localeMap.put("cs", 1029);
		localeMap.put("da", 1030);
		localeMap.put("n", 1043);
		localeMap.put("nl-be", 2067);	
		
		localeMap.put("en", 9);
		localeMap.put("en-us", 1033);
		localeMap.put("en-gb", 2057);
		localeMap.put("en-au", 3081);
		localeMap.put("en-ca", 4105);
		localeMap.put("en-nz", 5129);
		localeMap.put("en-ie", 6153);
		localeMap.put("en-za", 7177);
		localeMap.put("en-jm", 8201);
//		locale.put("en", 9225); // 处理为 locale.put("en", 9);
		localeMap.put("en-bz", 10249);
		localeMap.put("en-tt", 11273);
		localeMap.put("et", 1061);
		localeMap.put("fo", 1080);
		localeMap.put("fa", 1065);
		localeMap.put("fi", 1035);
		localeMap.put("fr", 1036);
		localeMap.put("fr-be", 2060);
		localeMap.put("fr-ca", 3084);
		localeMap.put("fr-ch", 4108);
		localeMap.put("fr-lu", 5132);
		localeMap.put("mk", 1071);
		localeMap.put("gd", 1084);
		localeMap.put("gd-ie", 2108);
		
		localeMap.put("de", 1031);
		localeMap.put("de-ch", 2055);
		localeMap.put("de-at", 3079);
		localeMap.put("de-lu", 4103);
		localeMap.put("de-li", 5127);
		localeMap.put("el-gr", 1032);
		localeMap.put("he", 1037);
		localeMap.put("hi", 1081);
		localeMap.put("hu", 1038);
		localeMap.put("is", 1039);
		localeMap.put("in", 1057);
		localeMap.put("it", 1040);
		localeMap.put("it-ch", 2064);
		localeMap.put("ja", 1041);
		localeMap.put("ko", 1042);
//		locale.put("ko", 2066);// 处理为 locale.put("ko", 1042);
		
		localeMap.put("lv", 1062);
		localeMap.put("lt", 1063);
		localeMap.put("ms", 1086);
		localeMap.put("mt", 1082);
		localeMap.put("no", 1044);
//		locale.put("no", 2068);// 处理为 locale.put("no", 1044);
		localeMap.put("p", 1045);
		localeMap.put("pt-br", 1046);
		localeMap.put("pt", 2070);
		localeMap.put("rm", 1047);
		localeMap.put("ro", 1048);
		localeMap.put("ro-mo", 2072);
		localeMap.put("ru", 1049);
		localeMap.put("ru-mo", 2073);
		localeMap.put("sz", 1083);
		localeMap.put("sr", 3098);
//		locale.put("sr", 2074);	// 处理为 locale.put("sr", 3098);
		localeMap.put("sk", 1051);
		localeMap.put("s", 1060);
		localeMap.put("sb", 1070);
		
		localeMap.put("es", 1034);//
		localeMap.put("es-mx", 2058);
//		locale.put("es", 3082);// 处理为 locale.put("es", 1034);
		localeMap.put("es-gt", 4106);
		localeMap.put("es-cr", 5130);
		localeMap.put("es-pa", 6154);
		localeMap.put("es-do", 7178);
		localeMap.put("es-ve", 8202);
		localeMap.put("es-co", 9226);
		localeMap.put("es-pe", 10250);
		localeMap.put("es-ar", 11274);
		localeMap.put("es-ec", 12298);
		localeMap.put("es-c", 13322);
		localeMap.put("es-uy", 14346);
		localeMap.put("es-py", 15370);
		localeMap.put("es-bo", 16394);
		localeMap.put("es-sv", 17418);
		localeMap.put("es-hn", 18442);
		localeMap.put("es-ni", 19466);
		localeMap.put("es-pr", 20490);
		
		localeMap.put("sx", 1072);
		localeMap.put("sv", 1053);
		localeMap.put("sv-fi", 2077);
		localeMap.put("th", 1054);
		localeMap.put("ts", 1073);
		localeMap.put("tn", 1074);
		localeMap.put("tr", 1055);
		localeMap.put("uk", 1058);
		localeMap.put("ur", 1056);
		localeMap.put("ve", 1075);
		localeMap.put("vi", 1066);
		localeMap.put("xh", 1076);
		localeMap.put("ji", 1085);
		localeMap.put("zu", 1077);
	}
}
