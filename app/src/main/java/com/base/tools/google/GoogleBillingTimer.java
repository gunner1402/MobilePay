package com.base.tools.google;

import com.base.tools.http.CallbackResultForActivity;
import com.base.tools.utils.BasesUtils;
import com.oasgames.android.oaspay.entity.OrderInfo;
import com.oasgames.android.oaspay.service.HttpService;
import com.oasgames.android.oaspay.tools.ReportUtils;

import org.json.JSONException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;


public class GoogleBillingTimer extends TimerTask {

	private static final String TAG = GoogleBillingTimer.class.getName();
	int period = 1;
	public void setPeriod() {  
        //缩短周期，执行频率就提高  
        setDeclaredField(TimerTask.class, this, "period", (long)(Math.pow(2, period)*1000));  
        if(period == 8){
        	period = 1;
        }else{
        	period++;
        }
    }  
      
    //通过反射修改字段的值  
    static boolean setDeclaredField(Class<?> clazz, Object obj,  
            String name, Object value) {  
        try {  
            Field field = clazz.getDeclaredField(name);  
            field.setAccessible(true);  
            field.set(obj, value);  
            return true;  
        } catch (Exception ex) {  
            ex.printStackTrace();  
            return false;  
        }  
    }  
    
	@Override
	public void run() {
		BasesUtils.logDebug(TAG, "There are currently no outstanding orders.");
		setPeriod();
		List<Purchase> orderList = null;
		try {
			orderList = GoogleBillingUtils.getPurchasedListByStatus(GoogleBillingUtils.ORDER_STATUS_UNFINISHED);
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		
		if(orderList == null || orderList.size() <= 0){
			BasesUtils.logDebug(TAG, "There are currently no outstanding orders.");
			return;
		}
		
		int size = orderList.size();
		for (int i = 0; i < size; i++) {
			Purchase purchase = orderList.get(i);
			HttpService.instance().checkPurchaseForGoogle(purchase, GoogleBillingUtils.SEPARATE, new MyCallback(purchase));

		}//end for
	}

	class MyCallback implements CallbackResultForActivity{
		Purchase purchase;
		public MyCallback(Purchase purchase){
			this.purchase = purchase;
		}
		@Override
		public void success(Object data, int statusCode, String msg) {
			OrderInfo order = (OrderInfo)data;
			if(null != order && "3".equals(order.pay_status)){
				String[] info = purchase.getDeveloperPayload().split(GoogleBillingUtils.SEPARATE);

				Map<String, String> paras = new HashMap<>();
				if(info.length >= 7)
					paras.put("orderid", info[6]);
				paras.put("uid", info[0]);
				ReportUtils.add(ReportUtils.DEFAULTEVENT_SDIAMONDFAIL, paras, null);
			}
			GoogleBillingUtils.updatePurchase(purchase);
		}

		@Override
		public void fail(int statusCode, String msg) {

		}

		@Override
		public void exception(Exception e) {

		}
	}
}
