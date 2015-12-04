package com.oasgames.android.oaspay.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.base.tools.activity.BasesActivity;
import com.base.tools.http.CallbackResultForActivity;
import com.base.tools.utils.BasesUtils;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.entity.OrderInfo;
import com.oasgames.android.oaspay.service.HttpService;
import com.oasgames.android.oaspay.tools.ReportUtils;

/**
 * 手动输入订单号
 * Created by Administrator on 2015/10/16.
 */
public class ActivityCaptureInput extends BasesActivity {
    EditText et_orderID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_capture_input);
        initHead(true, true, null, false, getString(R.string.capture_scan_text2_3), false, null);
        et_orderID = (EditText)findViewById(R.id.capture_input_edit);

        setWaitScreen(false);
    }

    public void onClickViewToInput(View view){
        if(BasesUtils.isFastDoubleClick())
            return;
        check();
    }
    private void check(){
        String orderid = et_orderID.getText().toString().trim();
        if(TextUtils.isEmpty(orderid) || !BasesUtils.regexNum(orderid)){
//            BasesUtils.showDialogBySystemUI(this, getString(R.string.capture_scan_text5), getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            }, "", null, "", null);
            BasesUtils.showMsg(this, getString(R.string.capture_scan_text6));
            return;
        }

        InputMethodManager imm = (InputMethodManager)et_orderID.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_orderID.getWindowToken(), 0);

        if(BasesUtils.isLogin())
            GetOrderInfo(orderid);
        else
            startActivityForResult(new Intent().setClass(ActivityCaptureInput.this, ActivityLogin.class), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            check();
        }
    }

    private void GetOrderInfo(String orderid){
        ReportUtils.add(ReportUtils.DEFAULTEVENT_SCANCODEORDER, null, null);
        setWaitScreen(true);
        HttpService.instance().getOrderInfoByInput(orderid, new GetOrderInfoCallback(this));
    }
    class GetOrderInfoCallback implements CallbackResultForActivity {
        Activity activity;
        public GetOrderInfoCallback(Activity activity){
            this.activity = activity;
        }
        @Override
        public void success(Object data, int statusCode, String msg) {
            setWaitScreen(false);
            OrderInfo info = (OrderInfo)data;
            if(!"1".equals(info.order_status)){// 订单已被删除
                BasesUtils.showDialogBySystemUI(activity, getResources().getString(R.string.order_list_item_label9), getResources().getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }, "", null, "", null);
                return;
            }
            startActivity(new Intent().setClass(activity, ActivityOrderDetails.class).putExtra("orderinfo", info));
        }

        @Override
        public void fail(int statusCode, String msg) {
            setWaitScreen(false);
            if(!TextUtils.isEmpty(msg) && "-16".equals(msg)){
                BasesUtils.showDialogBySystemUI(activity, getResources().getString(R.string.capture_scan_text4), getString(R.string.search_title_sub1), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }, getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        startActivity(new Intent().setClass(activity, ActivityLogin.class));
                    }
                }, "", null);
                return;
            }
            if(!TextUtils.isEmpty(msg) && "-22".equals(msg)){// 不是google订单
                BasesUtils.showDialogBySystemUI(activity, getResources().getString(R.string.capture_scan_text7), getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }, "", null, "", null);
                return;
            }
            if(!TextUtils.isEmpty(msg) && "-23".equals(msg)){// 订单不存在
                BasesUtils.showDialogBySystemUI(activity, getResources().getString(R.string.order_list_item_label9), "", null, getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }, "", null);
                return;
            }
                BasesUtils.showDialogBySystemUI(activity, getString(R.string.capture_scan_text5), getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }, "", null, "", null);
                return;

        }

        @Override
        public void exception(Exception e) {
            setWaitScreen(false);
        }
    }
}
