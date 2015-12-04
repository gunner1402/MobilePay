/* Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oasgames.android.oaspay.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.base.tools.BasesApplication;
import com.base.tools.activity.BasesActivity;
import com.base.tools.entity.PhoneInfo;
import com.base.tools.google.GoogleBillingUtils;
import com.base.tools.google.IabHelper;
import com.base.tools.google.IabResult;
import com.base.tools.google.Inventory;
import com.base.tools.google.Purchase;
import com.base.tools.google.SkuDetails;
import com.base.tools.http.CallbackResultForActivity;
import com.base.tools.utils.BasesConstant;
import com.base.tools.utils.BasesUtils;
import com.base.tools.utils.MD5Encrypt;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.entity.OrderInfo;
import com.oasgames.android.oaspay.service.HttpService;
import com.oasgames.android.oaspay.tools.APPUtils;
import com.oasgames.android.oaspay.tools.ReportUtils;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Example game using in-app billing version 3.
 *
 * Before attempting to run this sample, please read the README file. It
 * contains important information on how to set up this project.
 *
 * All the game-specific logic is implemented here in MainActivity, while the
 * general-purpose boilerplate that can be reused in any app is provided in the
 * classes in the util/ subdirectory. When implementing your own application,
 * you can copy over util/*.java to make use of those utility classes.
 *
 * This game is a simple "driving" game where the player can buy gas
 * and drive. The car has a tank which stores gas. When the player purchases
 * gas, the tank fills up (1/4 tank at a time). When the player drives, the gas
 * in the tank diminishes (also 1/4 tank at a time).
 *
 * The user can also purchase a "premium upgrade" that gives them a red car
 * instead of the standard blue one (exciting!).
 *
 * The user can also purchase a subscription ("infinite gas") that allows them
 * to drive without using up any gas while that subscription is active.
 *
 * It's important to note the consumption mechanics for each item.
 *
 * PREMIUM: the item is purchased and NEVER consumed. So, after the original
 * purchase, the player will always own that item. The application knows to
 * display the red car instead of the blue one because it queries whether
 * the premium "item" is owned or not.
 *
 * INFINITE GAS: this is a subscription, and subscriptions can't be consumed.
 *
 * GAS: when gas is purchased, the "gas" item is then owned. We consume it
 * when we apply that item's effects to our app's world, which to us means
 * filling up 1/4 of the tank. This happens immediately after purchase!
 * It's at this point (and not when the user drives) that the "gas"
 * item is CONSUMED. Consumption should always happen when your game
 * world was safely updated to apply the effect of the purchase. So,
 * in an example scenario:
 *
 * BEFORE:      tank at 1/2
 * ON PURCHASE: tank at 1/2, "gas" item is owned
 * IMMEDIATELY: "gas" is consumed, tank goes to 3/4
 * AFTER:       tank at 3/4, "gas" item NOT owned any more
 *
 * Another important point to notice is that it may so happen that
 * the application crashed (or anything else happened) after the user
 * purchased the "gas" item, but before it was consumed. That's why,
 * on startup, we check if we own the "gas" item, and, if so,
 * we have to apply its effects to our world and consume it. This
 * is also very important!
 *
 * @author Bruno Oliveira (Google)
 */
public class ActivityGooglePlayBilling extends BasesActivity {
    // Debug tag, for logging
    static final String TAG = "GooglePlayBilling";
	final String PAY_GOON ="goon";
	final String PAY_ONCE ="once";
	public static  final Integer HANDLER_QUERYINVENTORY = 101;
	public static  final Integer HANDLER_NOTICE = 0;

    private String base64EncodedPublicKey = "";
   
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    // 验证google play 是否可用
    static final int RC_VERIFYGOOGLEPLAY  = 10002;

    // The helper object
    IabHelper mHelper;
    
    OrderInfo order;
    String ext="";// 游戏需要透传的扩展参数
    MyHandler myHandler ;
	List<Purchase> oldOrderList = null;//未完成订单
	List<String> handedOrderIDS = new ArrayList<String>();//已处理过得未完成订单号

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(BasesUtils.getResourceValue(this, "layout", "pay_google"));
        
        myHandler = new MyHandler(this);

        /* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
         * (that you got from the Google Play developer console). This is not your
         * developer public key, it's the *app-specific* public key.
         *
         * Instead of just storing the entire literal string here embedded in the
         * program,  construct the key at runtime from pieces or
         * use bit manipulation (for example, XOR with some other string) to hide
         * the actual key.  The key itself is not secret information, but we don't
         * want to make it easy for an attacker to replace the public key with one
         * of their own and then fake messages from the server.
         */
//        String base64EncodedPublicKey = "CONSTRUCT_YOUR_KEY_AND_PLACE_IT_HERE";
        
        if(TextUtils.isEmpty(base64EncodedPublicKey)){
        	ApplicationInfo appInfo;
			try {
				appInfo = getPackageManager().getApplicationInfo(getApplicationContext().getPackageName(),PackageManager.GET_META_DATA);
				base64EncodedPublicKey = appInfo.metaData.getString("com.googleplay.ApplicationId");
			} catch (NameNotFoundException e) {
				BasesUtils.logDebug(TAG, "Please put your app's public key in AndroidManifest.xml.");
			}
        }
        if(TextUtils.isEmpty(base64EncodedPublicKey)){
        	BasesUtils.logError(TAG, "Please put your app's public key in AndroidManifest.xml.");
        	complain("Please put your app's public key in AndroidManifest.xml.");
        	return;
        }

        order = (OrderInfo)getIntent().getExtras().get("orderinfo");
        ext = getIntent().getStringExtra("ext");
        
        if(TextUtils.isEmpty(order.price_product_id)){
        	BasesUtils.logError(TAG, "Please put product id.");
        	complain("Please put product id.");
        	return;
        }
//		order.setPrice_product_id("oas.oaspay.120.3927");// 测试套餐

        setWaitScreen(true);

        // Create the helper, passing it our context and the public key to verify signatures with
        BasesUtils.logDebug(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(BasesApplication.OASISSDK_ENVIRONMENT_SANDBOX);

    	int isGooglePlayAvail = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(isGooglePlayAvail == ConnectionResult.SUCCESS){
        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        BasesUtils.logDebug(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
            	if (isPageClose()) {
            		isPageCloseHandler();
            		return;
				}
                BasesUtils.logDebug(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                	BasesUtils.logError(TAG, "Problem setting up in-app billing: " + IabHelper.getResponseDesc(result.getResponse()));
                	Message msg = new Message();
                	msg.what = HANDLER_NOTICE;
                	msg.obj = getResources().getString(R.string.google_error_notice1); // 支付初始化失败
                    myHandler.sendMessage(msg);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, Start purchase.
                try {
					oldOrderList = GoogleBillingUtils.getPurchasedList();
				} catch (JSONException e) {
					e.printStackTrace();
				}
                checkALLOrder();
            }
        });
        }else{

        	Dialog d = GooglePlayServicesUtil.getErrorDialog(isGooglePlayAvail, this, RC_VERIFYGOOGLEPLAY);
        	d.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface arg0) {
					BasesUtils.logError(TAG, "GooglePlayServicesUtil.showErrorDialogFragment");
					arg0.dismiss();
					
					myHandler.sendEmptyMessageDelayed(-1, 500);
				}
			});
        	d.show();

        }
    }
    /**
     * 检查所有未完成订单
     */
    private void checkALLOrder(){
		if(oldOrderList == null)
			oldOrderList = new ArrayList<>();

		Purchase payedOrder = null;
		int oldOrderSize = oldOrderList.size();
		for (int i = 0; i < oldOrderSize; i++) {
			Purchase old = oldOrderList.get(i);
			String payload = old.getDeveloperPayload();
			if(payload.contains(GoogleBillingUtils.SEPARATE+order.order_id+GoogleBillingUtils.SEPARATE)){
				payedOrder = old;
				break;
			}
		}
		if(payedOrder != null)
			check(payedOrder, PAY_ONCE);
		else
			queryInventory();

    }
    private void queryInventory(){
    	if (isPageClose()) {
    		isPageCloseHandler();
    		return;
		}
    	List<String> moreSkus = new ArrayList<String>();
        moreSkus.add(order.price_product_id);
        mHelper.queryInventoryAsync(true, moreSkus, new IabHelper.QueryInventoryFinishedListener() {

			@Override
			public void onQueryInventoryFinished(IabResult result, Inventory inv) {
				if (isPageClose()) {
					isPageCloseHandler();
					return;
				}
				// Is it a failure?
				if (result.isFailure() || inv == null) {
					BasesUtils.logError(TAG, "Failed to query inventory: " + IabHelper.getResponseDesc(result.getResponse()) + "\n" + result.toString());
					Message msg = new Message();
					msg.what = HANDLER_NOTICE;
					msg.obj = getResources().getString(R.string.google_error_notice2); // 获取套餐失败，请稍后再试
					myHandler.sendMessage(msg);
					return;
				}

				SkuDetails sku = inv.getSkuDetails(order.price_product_id);
				if (sku == null || TextUtils.isEmpty(sku.getPrice())) {
					BasesUtils.logError(TAG, "Don't find SkuDetails by " + order.price_product_id);
					Message msg = new Message();
					msg.what = HANDLER_NOTICE;
					msg.obj = getResources().getString(R.string.google_error_notice4); // 未找到商品，请稍后再试
					myHandler.sendMessage(msg);
					return;
				}

				final Purchase p = inv.getPurchase(order.price_product_id);
				if(p != null){
					String payload = p.getDeveloperPayload();
					BasesUtils.logDebug(TAG, "Have old Purchase.Purchase="+p.toString());
					if(!TextUtils.isEmpty(payload) && payload.contains(GoogleBillingUtils.SEPARATE+order.order_id+GoogleBillingUtils.SEPARATE)){// 属于当前订单，继续执行
						check(p, PAY_ONCE);
						return;
					}else{
						BasesUtils.logDebug(TAG, "Have not contain this Purchase."+p.getPurchaseState());
//						// 不属于当前订单，擦除
//						if (p.getPurchaseState() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED) {
//							inv.erasePurchase(order.price_product_id);
//						}else if(p.getPurchaseState() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED){
							check(p, PAY_GOON);
							return;
//						}
					}
				}

				if("3".equals(order.pay_status)){// 订单已完成支付，但发钻失败，提示“该订单发现异常，请联系客服！”，此时已无订单数据，无法再次发送发钻请求
					BasesUtils.logDebug(TAG, "Order is error.order.pay_status="+order.pay_status+";order.order_id="+order.order_id);
					BasesUtils.showDialogBySystemUI(ActivityGooglePlayBilling.this, getResources().getString(R.string.google_error_notice7), getResources().getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							close();
						}
					}, "", null, "", null);
					return;
				}

				// 订单状态正常，且本地无该订单记录，开启新的支付流程
				launchPurchaseFlow();

			}
		});
    }

    // 向google提交支付请求
    private void launchPurchaseFlow() {
    	if (isPageClose()) {
    		isPageCloseHandler();
    		return;
		}
		setWaitScreen(false);

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
       
        String payload = BasesApplication.userInfo.uid+GoogleBillingUtils.SEPARATE+order.server_id+GoogleBillingUtils.SEPARATE+BasesApplication.userInfo.roleID+GoogleBillingUtils.SEPARATE+ext+GoogleBillingUtils.SEPARATE+""+GoogleBillingUtils.SEPARATE+"serverType"+GoogleBillingUtils.SEPARATE+order.order_id+GoogleBillingUtils.SEPARATE+ MD5Encrypt.StringToMD5(BasesApplication.PUBLICKEY + PhoneInfo.instance().gamecode + order.server_id + BasesApplication.userInfo.uid + order.price_product_id + (TextUtils.isEmpty(ext) ? "" : ext) + order.order_id);

		BasesUtils.logDebug(TAG, "Start purchase " + order.price_product_id);
		mHelper.launchPurchaseFlow(this, order.price_product_id, RC_REQUEST,
				mPurchaseFinishedListener, payload);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        BasesUtils.logDebug(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            BasesUtils.logDebug(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        
        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */
        payload = payload.substring(payload.lastIndexOf(GoogleBillingUtils.SEPARATE)+GoogleBillingUtils.SEPARATE.length());
        
        if(MD5Encrypt.StringToMD5(BasesApplication.PUBLICKEY+PhoneInfo.instance().gamecode+order.server_id+BasesApplication.userInfo.uid+order.price_product_id+(TextUtils.isEmpty(ext)?"":ext)+order.order_id).equals(payload))
        	return true;
        
        return false;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            BasesUtils.logDebug(TAG, "Purchase finished: " + result.toString() + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null){// 页面被退出，此时如果支付成功，保存改订单
            	if(result.isSuccess())
            		checkAndAddPurchase(purchase);
            	return;
            }

            if (result.isFailure()) {
            	BasesUtils.logError(TAG, "Error purchasing: " + IabHelper.getResponseDesc(result.getResponse()));
            	if(result.getResponse() == -1005){// User canceled
            		myHandler.sendEmptyMessage(-1);
            		setResultInfo(BasesConstant.RESULT_CANCEL, "User canceled");
            		return;
            	}
            	Message msg = new Message();
            	msg.what = HANDLER_NOTICE;
            	msg.obj = getResources().getString(R.string.google_error_notice5);// -1003  数据验证失败
                myHandler.sendMessage(msg);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
            	Message msg = new Message();
            	msg.what = HANDLER_NOTICE;
            	msg.obj = getResources().getString(R.string.google_error_notice5);// 数据验证失败
                myHandler.sendMessage(msg);
                BasesUtils.logError(TAG, "Error purchasing. Authenticity verification failed.");
                return;
            }

            BasesUtils.logDebug(TAG, "Purchase successful." + purchase.toString());

            checkAndAddPurchase(purchase);
            check(purchase, PAY_ONCE);

        }
    };

    /**
	 * 检查当前订单是否已入库，没有入库时，插入
	 * @param p	订单信息
	 * @return
	 */
	public void checkAndAddPurchase(Purchase p){
		long id = 0;
		if(GoogleBillingUtils.checkPurchaseIsExist(p)){
			BasesUtils.logError(TAG, "支付订单保存至数据库成功1。");
		}else{
			id = GoogleBillingUtils.addPurchase(p);
			if(id > 0)
				BasesUtils.logError(TAG, "支付订单保存至数据库成功2。");
			else
				BasesUtils.logError(TAG, "支付订单保存至数据库失败。");
		}
	}


    private void consume(Purchase purchase, final String code){
    	mHelper.consumeAsync(purchase, new IabHelper.OnConsumeFinishedListener() {
			public void onConsumeFinished(Purchase p, IabResult result) {
				BasesUtils.logDebug(TAG, "Consumption finished. Purchase: " + p + ", result: " + result);

				// if we were disposed of in the meantime, quit.
				if (mHelper == null) return;

				if (result.isSuccess()) {
					BasesUtils.logDebug(TAG, "Consumption successful. Provisioning. Purchase.orderID=" + p.getOrderId());
//                  case 1000000://交易成功且发钻成功
//    				case 1000002://该购买交易已发钻成功，因客户端未消费成功，所以重复2次验证

					if (GoogleBillingUtils.deletePurchase(p.getOrderId()) <= 0) {// 消费成功，删除数据库记录
						BasesUtils.logError(TAG, "delete by orderid=" + p.getOrderId());
					}
					if(!isPageClose() && code.equals(PAY_GOON)){
						myHandler.sendEmptyMessage(HANDLER_QUERYINVENTORY);
						return;
					}
					Message msg = new Message();
					msg.what = HANDLER_SUCECCES;
					myHandler.sendMessage(msg);
					BasesUtils.logDebug(TAG, "End consumption flow.");
					return;
				}

				close();

			}
        });
    }
    /**
     * 二次服务器验证
     */
    private void check(Purchase purchase, String type){
		setWaitScreen(true);
		HttpService.instance().checkPurchaseForGoogle(purchase, GoogleBillingUtils.SEPARATE, new GoogleCheck(purchase, type));

//    	* 			1000001:验证信息错误（key无效）
//   	 * 			1000002:该购买交易已发钻成功
//   	 * 			1000003:支付完成，验证连接失败-连接超时或者无法连接上Google服务器，
//   	 * 			1000004：ProductID错误—一般不会出现BUG
//   	 * 			1000005:支付成功，但是发钻不成功，
//   	 * 			1000100:未知错误，
    }
	class GoogleCheck implements CallbackResultForActivity{
		Purchase purchase;
		String type;// type=PAY_GOON表示该商品继续支付一次
		public GoogleCheck(Purchase purchase, String type){
			this.purchase = purchase;
			this.type = type;
		}
		@Override
		public void success(Object data, int statusCode, String msg) {
			if(!type.equals(PAY_GOON))// 不继续支付时，更新order
				order = (OrderInfo)data;

			((MyApplication)getApplication()).isReLoadOderList = true;

			if("3".equals(order.pay_status)){// 订单已完成支付，但发钻失败；发钻失败时，不消耗该商品
				String[] info = purchase.getDeveloperPayload().split(GoogleBillingUtils.SEPARATE);
				Map<String, String> paras = new HashMap<>();
				if(info.length >= 7)
					paras.put("orderid", info[6]);
				paras.put("uid", info[0]);
				ReportUtils.add(ReportUtils.DEFAULTEVENT_SDIAMONDFAIL, paras, null);

				Message message = new Message();
				message.what = 0;
				message.obj = getString(R.string.google_error_notice7);// 提示：该订单发现异常，请联系客服！
				myHandler.sendMessage(message);
				return;
			}

			if(!isPageClose())
				consume(purchase, type);// 消耗当前商品
		}

		@Override
		public void fail(int statusCode, String msg) {
//			consume(purchase, type);// 发钻失败，意味着服务端已收到消息，估要执行consume
			((MyApplication)getApplication()).isReLoadOderList = true;
			if(!isPageClose()) {
				APPUtils.showErrorMessageByErrorCode(ActivityGooglePlayBilling.this, "-2000");
			}
			close();// 先暂时close，后续决定是否添加重试功能
		}

		@Override
		public void exception(Exception e) {
//			showNetWrokError();
//			check(purchase, type);
			((MyApplication)getApplication()).isReLoadOderList = true;
			if(!isPageClose()) {
				APPUtils.showErrorMessageByErrorCode(ActivityGooglePlayBilling.this, "-2000");
			}
			close();// 先暂时close，后续决定是否添加重试功能
		}
	}

	public static class MyHandler extends Handler {

		// WeakReference to the outer class's instance.
		private WeakReference<ActivityGooglePlayBilling> mOuter;

		public MyHandler(ActivityGooglePlayBilling activity) {
			mOuter = new WeakReference<ActivityGooglePlayBilling>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			ActivityGooglePlayBilling outer = mOuter.get();
			if (outer != null) {
				switch (msg.what) {
				case HANDLER_SUCECCES:
					BasesUtils.showMsg(outer, outer.getResources().getString(R.string.google_error_notice6));
					outer.setResultInfo(BasesConstant.RESULT_SUCCESS, "验证成功，并发钻成功");
					break;
				case 0:
					outer.complain((String) msg.obj);
					break;
				case 101:
					outer.queryInventory();
					break;
				case -1:
					outer.close();
					break;
				case -2:
					BasesUtils.showMsg(outer, (String) msg.obj);
					break;
				default:
					
					break;
				}
			}
		}
	}
    private void setResultInfo(int statusCode, String errorMessage){
        close();
    }
    @Override
    protected void onResume() {
		super.onResume();
    	
    }
    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
		super.onDestroy();

        // very important:
        BasesUtils.logDebug(TAG, "Destroying helper.");
        if (mHelper != null) {
        	try {				
        		mHelper.dispose();
			} catch (Exception e) {
				BasesUtils.logError(TAG, "Google onDestroy() exception:" + e.getMessage());
			}
            mHelper = null;
        }
    }

    void complain(String message) {
        BasesUtils.logError(TAG, "**** TrivialDrive Error: " + message);
//        alert(message);
        BasesUtils.showMsg(this, message);
        close();
	}

	void close(){
    	setWaitScreen(false);
        finish();
		startActivity(new Intent().setClass(this, ActivityOrderDetails.class).putExtra("orderinfo", order));
    }
    /**
     * 当页面被用户关闭后，不再做其他操作
     */
    private void isPageCloseHandler(){
//    	myHandler.sendEmptyMessage(-1);
    	setResultInfo(BasesConstant.RESULT_CANCEL, "用户取消操作");
    }

}
