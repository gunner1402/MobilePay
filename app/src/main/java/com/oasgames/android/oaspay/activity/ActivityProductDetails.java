package com.oasgames.android.oaspay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.tools.activity.BasesActivity;
import com.base.tools.http.CallbackResultForActivity;
import com.base.tools.utils.BasesUtils;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.entity.OrderInfo;
import com.oasgames.android.oaspay.entity.ProductInfo;
import com.oasgames.android.oaspay.service.HttpService;
import com.oasgames.android.oaspay.tools.APPUtils;
import com.oasgames.android.oaspay.tools.ReportUtils;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * 界面 商品（礼品）详细
 * Created by Administrator on 2015/10/16.
 */
public class ActivityProductDetails extends BasesActivity {
    ProductInfo info;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_product_details);

        initHead(true, true, null, false, getString(R.string.product_details_title), false, null);

        id = getIntent().getExtras().getString("id");

        loadInfoById();

    }
    private void loadInfoById(){
        setWaitScreen(true);
        HttpService.instance().getProductDetails(id, new MyCallback());
    }
    class MyCallback implements CallbackResultForActivity{
        @Override
        public void success(Object data, int statusCode, String msg) {
            info = (ProductInfo) data;
            updateView();
            setWaitScreen(false);

            Map<String, String> paras = new HashMap<>();
            paras.put("gitid", info.product_id);
            ReportUtils.add(ReportUtils.DEFAULTEVENT_GITDETAILS, paras, null);
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
        if(info == null){
            loadInfoById();
        }else{
            getOrderID();
        }
    }

    private void updateView(){
        findViewById(R.id.product_details_layout).setVisibility(View.VISIBLE);
        final ImageView img = (ImageView)findViewById(R.id.product_item_img);
        BasesUtils.loadImg(this, img, info.product_img_url);

        TextView tv = (TextView)findViewById(R.id.product_item_title);
        tv.setText(info.product_name);

        TextView diamond = (TextView)findViewById(R.id.product_item_diamond_count);
        diamond.setText(info.game_coins_show);

        TextView price = (TextView)findViewById(R.id.product_details_price);
        if(!TextUtils.isEmpty(info.amount_show))
            price.setText(info.currency_show+info.amount_show);

        findViewById(R.id.product_item_charge).setVisibility(View.INVISIBLE);

        LinearLayout notice = (LinearLayout)findViewById(R.id.product_details_notice);// 添加说明
        if(!TextUtils.isEmpty(info.content_info)){
            try {
                JSONArray ja = new JSONArray(info.content_info);
                int count = ja.length();

                for (int i = 0; i <count; i++) {
                    View view = getLayoutInflater().inflate(R.layout.page_product_details_item, null);
                    TextView tv_notice = (TextView)view.findViewById(R.id.product_details_item_notice);
                    tv_notice.setText(ja.getString(i));
                    notice.addView(view);
                }
            }catch (Exception e){

            }
        }
    }
    public void onClickViewTopay(View view){
        if(BasesUtils.isFastDoubleClick())
            return;
        if(BasesUtils.isLogin())
            getOrderID();
        else
            startActivity(new Intent().setClass(this, ActivityLogin.class));
    }
    private void getOrderID(){
        setWaitScreen(true);
        HttpService.instance().sendOrder(id, "", "", "", "giftapp", new GetOrderIDCallback());
    }

    class GetOrderIDCallback implements CallbackResultForActivity{
        @Override
        public void success(Object data, int statusCode, String msg) {
            if(!isPageClose()) {
                Intent in = new Intent().setClass(ActivityProductDetails.this, ActivityGooglePlayBilling.class);
                in.putExtra("orderinfo", (OrderInfo) data);
                startActivity(in);
            }
            setWaitScreen(false);
        }

        @Override
        public void fail(int statusCode, String msg) {
            setWaitScreen(false);
//            showNetWrokError();
            if(!isPageClose())
                APPUtils.showErrorMessageByErrorCode(ActivityProductDetails.this, "-2000");
        }

        @Override
        public void exception(Exception e) {
            setWaitScreen(false);
            showNetWrokError();
        }
    }
}
