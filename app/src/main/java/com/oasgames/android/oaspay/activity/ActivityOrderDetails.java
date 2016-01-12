package com.oasgames.android.oaspay.activity;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.base.tools.activity.BasesActivity;
import com.base.tools.http.CallbackResultForActivity;
import com.base.tools.utils.BasesUtils;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.entity.OrderInfo;
import com.oasgames.android.oaspay.service.HttpService;
import com.oasgames.android.oaspay.tools.APPUtils;

import org.json.JSONArray;

import java.text.SimpleDateFormat;


/**
 * 界面 订单详细信息
 * orderinfo（订单信息）
 * Created by Administrator on 2015/10/16.
 */
public class ActivityOrderDetails extends BasesActivity {
    OrderInfo order;
    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_order_details);
        initHead(true, true, null, false, getString(R.string.order_details_title), false, null);

        order = (OrderInfo)getIntent().getExtras().get("orderinfo");

        toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        updateView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        order = (OrderInfo)intent.getExtras().get("orderinfo");
        updateView();
    }
    private void updateView(){
        ((TextView)findViewById(R.id.order_details_id)).setText(order.order_id);

        findViewById(R.id.product_item_charge).setVisibility(View.INVISIBLE);
        if("payapp".equalsIgnoreCase(order.order_type)) {
            if(TextUtils.isEmpty(order.product_img_url))
                findViewById(R.id.product_item_img).setBackgroundResource(R.mipmap.common_diamond_bg);
            else
                BasesUtils.loadImg(this, (ImageView) findViewById(R.id.product_item_img), order.product_img_url);
        }else
            BasesUtils.loadImg(this, (ImageView) findViewById(R.id.product_item_img), order.product_img_url);
        ((TextView)findViewById(R.id.product_item_title)).setText(order.product_name);
        ((TextView)findViewById(R.id.product_item_diamond_count)).setText(order.game_coins_show);
        if("payapp".equals(order.order_type)) {
            String disconunt = order.price_discount;
            if(disconunt != null && !"null".equals(disconunt) && !TextUtils.isEmpty(disconunt))
                ((TextView)findViewById(R.id.product_item_title)).setText(order.price_discount + " + " + order.game_coins_show);
            else
                ((TextView)findViewById(R.id.product_item_title)).setText(order.game_coins_show);
            findViewById(R.id.product_item_diamond_count).setVisibility(View.INVISIBLE);
            findViewById(R.id.product_item_diamond_bg).setVisibility(View.INVISIBLE);
        }

        LinearLayout codeLayout = (LinearLayout)findViewById(R.id.order_details_code_layout);// 兑换码区域
        if("giftapp".equalsIgnoreCase(order.order_type) && "2".equals(order.pay_status)) {
            codeLayout.setVisibility(View.VISIBLE);// 是礼包，并且支付成功才显示
            if(TextUtils.isEmpty(order.exchange_code)){
                ((TextView)findViewById(R.id.order_details_code)).setText(getString(R.string.order_details_label_12));
                findViewById(R.id.order_details_code_copy).setVisibility(View.INVISIBLE);
            }else{
                ((TextView)findViewById(R.id.order_details_code)).setText(order.exchange_code);
                findViewById(R.id.order_details_code_copy).setVisibility(View.VISIBLE);
            }
        }else
            codeLayout.setVisibility(View.GONE);

        LinearLayout notice = (LinearLayout)findViewById(R.id.order_details_notice);// 添加说明  ZX
        notice.removeAllViews();
        if(!TextUtils.isEmpty(order.content_info)){
            try {
                JSONArray ja = new JSONArray(order.content_info);
                int count = ja.length();

                for (int i = 0; i <count; i++) {
                    View view = getLayoutInflater().inflate(R.layout.page_product_details_item, null);
                    TextView tv_notice = (TextView)view.findViewById(R.id.product_details_item_notice);
                    tv_notice.setText(ja.getString(i));
                    notice.addView(view);
                }
            }catch (Exception e){}
        }

        if("giftapp".equalsIgnoreCase(order.order_type) ){
            findViewById(R.id.order_details_game).setVisibility(View.GONE);
        }else{
            findViewById(R.id.order_details_game).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.order_details_game_servername)).setText(order.server_name);
            ((TextView)findViewById(R.id.order_details_game_rolename)).setText(order.rolename);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ((TextView)findViewById(R.id.order_details_status)).setText(getString(R.string.order_details_label_3) + "  "+(("1".equals(order.pay_status) || "3".equals(order.pay_status)) ? getString(R.string.order_details_label_8) : getString(R.string.order_details_label_7)));
        ((TextView)findViewById(R.id.order_details_downtime)).setText(getString(R.string.order_details_label_4) + "  "+sdf.format(Long.parseLong(order.create_time + "000")));
        if("2".equals(order.pay_status) && !TextUtils.isEmpty(order.pay_time)) {
            TextView paytime = (TextView)findViewById(R.id.order_details_paytime);
            paytime.setVisibility(View.VISIBLE);
            paytime.setText(getString(R.string.order_details_label_5) + "  "+sdf.format(Long.parseLong(order.pay_time + "000")));
        }else
            ((TextView)findViewById(R.id.order_details_paytime)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.order_details_paystyle)).setText(getString(R.string.order_details_label_6) +"  "+ ("google".equalsIgnoreCase(order.ostype)?"Google Play":"AppStore" ));

        ((TextView)findViewById(R.id.order_details_price)).setText(order.currency_show +"  "+ order.amount_show);

        if("2".equals(order.pay_status))
            findViewById(R.id.order_details_topay).setVisibility(View.GONE);
        else
            findViewById(R.id.order_details_topay).setVisibility(View.VISIBLE);

        setWaitScreen(false);
    }

    public void onClickViewCodeCopy(View view){
        if(BasesUtils.isFastDoubleClick())
            return;
        if(TextUtils.isEmpty(order.exchange_code)){
            toast.setText(getString(R.string.order_details_label_12));
            toast.show();
            return;
        }
        // 点击复制按钮  复制 兑换码
        ClipboardManager cm = (ClipboardManager)this.getSystemService(CLIPBOARD_SERVICE);
        cm.setText(order.exchange_code);

        BasesUtils.showMsg(this, getString(R.string.order_details_label_11));
    }

    public void onClickViewTopay(View view){
        if(BasesUtils.isFastDoubleClick())
            return;
        if(TextUtils.isEmpty(order.ostype) || !"google".equalsIgnoreCase(order.ostype)){
            toast.setText(getString(R.string.capture_scan_text8));
            toast.show();
            return;
        }
        // 点击支付按钮
        setWaitScreen(true);
        HttpService.instance().getOrderInfoByID(order.order_id, new GetOrderInfo(this));
    }
    class GetOrderInfo implements CallbackResultForActivity{
        Activity activity;
        public GetOrderInfo(Activity activity){
            this.activity = activity;
        }
        @Override
        public void success(final Object data, int statusCode, String msg) {
            setWaitScreen(false);
            if(isPageClose())
                return;

            order = (OrderInfo)data;
            if(!"1".equals(order.order_status)){// 订单已被删除
                BasesUtils.showDialogBySystemUI(activity, getResources().getString(R.string.order_list_item_label9), getResources().getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ((MyApplication)activity.getApplication()).isReLoadOderList = true;// 数据有变化，请求重新刷新列表
                        finish();
                    }
                }, "", null, "", null);
                return;
            }
            if("2".equals(order.pay_status)){// 订单已完成，提示“该订单已完成支付！”
                BasesUtils.showDialogBySystemUI(activity, getResources().getString(R.string.order_list_item_label10), getResources().getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ((MyApplication)activity.getApplication()).isReLoadOderList = true;// 数据有变化，请求重新刷新列表
                        updateView();
                    }
                }, "", null, "",null);
                return;
            }
            if(TextUtils.isEmpty(order.online_status) || "2".equals(order.online_status)){// 商品已下架
                BasesUtils.showDialogBySystemUI(activity, getResources().getString(R.string.order_list_item_label12), getResources().getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }, "", null, "", null);
                return;
            }
            startActivity(new Intent().setClass(activity, ActivityGooglePlayBilling.class).putExtra("orderinfo", order));
        }

        @Override
        public void fail(int statusCode, String msg) {
            setWaitScreen(false);
            if(!isPageClose())
                APPUtils.showErrorMessageByErrorCode(activity, "-2000");
        }

        @Override
        public void exception(Exception e) {
            setWaitScreen(false);
            if(!isPageClose())
                APPUtils.showErrorMessageByErrorCode(activity, "-2000");
        }
    }
}
