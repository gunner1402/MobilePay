package com.oasgames.android.oaspay.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.base.tools.BasesApplication;
import com.base.tools.activity.BasesActivity;
import com.base.tools.http.CallbackResultForActivity;
import com.base.tools.utils.BasesUtils;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.adapter.AdapterPayPackageList;
import com.oasgames.android.oaspay.entity.OrderInfo;
import com.oasgames.android.oaspay.entity.PayInfoDetail;
import com.oasgames.android.oaspay.entity.PayInfoList;
import com.oasgames.android.oaspay.entity.ServerInfo;
import com.oasgames.android.oaspay.service.HttpService;
import com.oasgames.android.oaspay.tools.APPUtils;
import com.oasgames.android.oaspay.tools.ReportUtils;

/**
 * 界面 支付套餐列表
 * Created by Administrator on 2015/10/16.
 */
public class ActivityPayPackageList extends BasesActivity {

    public PayInfoDetail selectedPayInfo;
    String serverID, str_servername, str_roleName;
    ListView listView;
    AdapterPayPackageList adapter;

    PayInfoList paylist;
    TextView username, servername, rolename, price, topay;
    String[] serverList = null;
    int defaultCheckedIndex = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_pay_package_list);

        initHead(true, true, null, false, getString(R.string.pay_package_list_title), false, null);

        listView = (ListView)findViewById(R.id.pay_package_list);
        loadData();
        adapter = new AdapterPayPackageList(this, null, 1, null);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPayInfo = (PayInfoDetail) parent.getAdapter().getItem(position);
                price.setText(selectedPayInfo.currency_show + selectedPayInfo.amount_show);
                adapter.notifyDataSetChanged();
                checkData();
            }
        });

        username = (TextView)findViewById(R.id.pay_package_list_name);
        if(TextUtils.isEmpty(BasesApplication.userInfo.username) || "null".equals(BasesApplication.userInfo.username) || BasesApplication.userInfo.username.contains("@"+BasesApplication.userInfo.platform))
            username.setText(BasesApplication.userInfo.uid);
        else
            username.setText(BasesApplication.userInfo.username);
        servername = (TextView)findViewById(R.id.pay_package_list_servername);
        rolename = (TextView)findViewById(R.id.pay_package_list_rolename);
        price = (TextView)findViewById(R.id.pay_package_list_sum);
        findViewById(R.id.pay_package_list_servernamelayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasesUtils.showSingleChoiceDialogListBySystemUI(ActivityPayPackageList.this, serverList, defaultCheckedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        updateUserInfo(which);
                        checkData();
                    }
                });
            }
        });

        ReportUtils.add(ReportUtils.DEFAULTEVENT_GAMEPAY, null, null);
    }

    private void updateUserInfo(int which){
        ServerInfo s = paylist.servers.get(which);
        serverID = s.serverid;
        str_servername = s.servername;
        str_roleName = s.rolename;

        rolename.setText(TextUtils.isEmpty(s.rolename)?getString(R.string.pay_package_list_sub8):s.rolename);
        servername.setText(s.servername);
        defaultCheckedIndex = which;
    }
    private void updateLayout(Object data){
        setWaitScreen(false);
        paylist = (PayInfoList)data;

        if(paylist.list != null && paylist.list.size()>0) {
            selectedPayInfo = paylist.list.get(0);
            price.setText(selectedPayInfo.currency_show + selectedPayInfo.amount_show);
        }else{
            selectedPayInfo = null;
        }

        adapter.data = paylist.list;
        adapter.notifyDataSetChanged();

        APPUtils.setListViewHeightBasedOnChildren(listView);

        findViewById(R.id.pay_package_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.pay_package_topay).setVisibility(View.VISIBLE);

        int count = paylist.servers.size();
        serverList = new String[count];
        for (int i = 0; i < count; i++) {
            ServerInfo s = paylist.servers.get(i);
            serverList[i] = s.servername;
            if("Y".equalsIgnoreCase(s.charge_status)){
                defaultCheckedIndex = i;
                updateUserInfo(i);
            }
        }
        checkData();
    }

    private void loadData(){
        setWaitScreen(true);

        HttpService.instance().getPayKindsInfo(new MyCallback());
    }

    class MyCallback implements CallbackResultForActivity{
        @Override
        public void success(Object data, int statusCode, String msg) {
            updateLayout(data);
        }

        @Override
        public void fail(int statusCode, String msg) {
            setWaitScreen(false);
            showNetWrokError();
        }

        @Override
        public void exception(Exception e) {
            setWaitScreen(false);
            showNetWrokError();
        }
    }

    @Override
    public void retry() {
        super.retry();
        loadData();
    }

    /**
     * 检查数据有效性，有效后方可支付
     */
    private void checkData(){
        if(topay == null)
            topay = (TextView)findViewById(R.id.pay_package_topay);
        if(selectedPayInfo != null && !TextUtils.isEmpty(serverID) && str_roleName!=null && !TextUtils.isEmpty(str_roleName)){
            topay.setBackgroundResource(R.drawable.common_button_1_selector);
            topay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOrderId();
                }
            });
        }
    }
    private void getOrderId(){
        setWaitScreen(true);
        HttpService.instance().sendOrder(selectedPayInfo.id, serverID, str_servername, str_roleName, "payapp", new MyCallbackForGetOrderId());
    }
    class MyCallbackForGetOrderId implements CallbackResultForActivity{
        @Override
        public void success(Object data, int statusCode, String msg) {
            setWaitScreen(false);
            Intent in = new Intent().setClass(ActivityPayPackageList.this, ActivityGooglePlayBilling.class);
//            in.putExtra("inAppProductID", selectedPayInfo.price_product_id);
//            in.putExtra("revenue", selectedPayInfo.amount_show);
//            in.putExtra("oasOrderid", ((OrderInfo)data).order_id);
            in.putExtra("orderinfo", ((OrderInfo)data));
            startActivity(in);
        }

        @Override
        public void fail(int statusCode, String msg) {
            setWaitScreen(false);
            APPUtils.showErrorMessageByErrorCode(ActivityPayPackageList.this, "-2000");
        }

        @Override
        public void exception(Exception e) {
            setWaitScreen(false);
            APPUtils.showErrorMessageByErrorCode(ActivityPayPackageList.this, "-2000");
        }
    }
}
