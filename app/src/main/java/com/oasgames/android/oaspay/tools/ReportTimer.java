package com.oasgames.android.oaspay.tools;

import android.text.TextUtils;

import com.base.tools.entity.PhoneInfo;
import com.base.tools.utils.BasesUtils;
import com.oasgames.android.oaspay.entity.ReportInfo;
import com.oasgames.android.oaspay.entity.ReportMdataInfo;
import com.oasgames.android.oaspay.service.HttpService;

import java.util.TimerTask;

public class ReportTimer extends TimerTask {

	private static final String TAG = ReportTimer.class.getSimpleName();
	@Override
	public void run() {
		do{
			
			synchronized (ReportUtils.queue) {// 同步
				ReportInfo info = ReportUtils.queue.peek();
				if(info != null){
					reportMdata((ReportMdataInfo)info);

					ReportUtils.queue.poll();
					
					BasesUtils.logDebug(TAG, "ReportInfo queue poll success;eventname " + info.eventName);

				}else{
					BasesUtils.logDebug(TAG, "ReportInfo queue is null;");
				}
			}
		}while(ReportUtils.queue.peek()!=null);
		
//		if(SystemCache.logListsSD != null){
//			synchronized (SystemCache.logListsSD) {// 同步
//				if(SystemCache.logListsSD != null && SystemCache.logListsSD.size() > 0){
//					List<String> log = new ArrayList<String>(SystemCache.logListsSD);
//					SystemCache.logListsSD.clear();
//					for (String str : log) {
//						FileUtils.writeLogToStore(str);
//					}
//				}
//			}
//		}
	}

	private void reportMdata(final ReportMdataInfo info){
//		if(BasesUtils.isSandBox())
//			return;
		/**	Mdata */
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if(!TextUtils.isEmpty(PhoneInfo.instance().mdataAppID)){
					BasesUtils.logDebug(TAG, "MData queue eventname "+info.eventName);
					try {
						HttpService.instance().sendToMdataInfo(info);
					} catch (Exception e) {
						BasesUtils.logDebug(TAG, "MData send fail. Event Name:"+info.eventName);
						return;
					}								
				}else{
					BasesUtils.logDebug(TAG, "MData appid is null.");
					return;
				}
			}
		}).start();
	}

}
