package com.oasgames.android.oaspay.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.base.tools.activity.BasesActivity;
import com.base.tools.http.CallbackResultForActivity;
import com.base.tools.slide.SlideListView;
import com.base.tools.slide.SlideListView.SlideMode;
import com.base.tools.utils.BasesUtils;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.adapter.AdapterOrderListSlide;
import com.oasgames.android.oaspay.entity.OrderInfo;
import com.oasgames.android.oaspay.entity.OrderList;
import com.oasgames.android.oaspay.service.HttpService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ActivityOrderListSlide extends BasesActivity {
    final int TYPE_HISTORY = 1; // 历史订单
    final int TYPE_MONTH = 2;   // 当月订单
    final int PAGESIZE = 20;    // 每页记录数
    boolean isLoadMore = false; // 是否正在加载下一页数据
    int currentType = TYPE_MONTH;
    RadioGroup radioGroup;
    private SlideListView mSlideListViewHistory, mSlideListViewMonth;
    private AdapterOrderListSlide mAdapterHistory, mAdapterMonth;

    Map<Integer, OrderList> map_type;// key为type，value为该type下的数据

    public Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_order_list_slide);
        initHead(true, true, null, false, getString(R.string.fragment_shop_function_order), false, null);

        MyApplication application = (MyApplication)getApplication();
        application.isReLoadOderList = true;// 每次onCreate默认重新加载所有数据

        radioGroup = (RadioGroup)findViewById(R.id.order_list_radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateRadioButton(checkedId);
            }
        });

        mSlideListViewHistory = ((SlideListView) findViewById(R.id.list_view));
        mSlideListViewMonth = ((SlideListView) findViewById(R.id.list_view_month));

        toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication myApplication = (MyApplication)getApplication();
        if(myApplication.isReLoadOderList){
            reLoadData();
        }
    }
    private void init(){
        if (map_type == null)
            map_type = new HashMap<Integer, OrderList>();
        map_type.clear();

        OrderList orderList = new OrderList();
        orderList.list = new ArrayList<>();
        orderList.setTotal_page("0");
        orderList.setCur_page("0");
        orderList.setEvery_page_count("" + PAGESIZE);
        map_type.put(TYPE_HISTORY, orderList);

        mAdapterHistory = new AdapterOrderListSlide(this, orderList.list);
        mSlideListViewHistory.setAdapter(mAdapterHistory);
        mSlideListViewHistory.setOnItemClickListener(new MyOnItemClickLisnter());
        mSlideListViewHistory.setEmptyView(findViewById(R.id.order_list_slide_empty));

        orderList = new OrderList();
        orderList.list = new ArrayList<>();
        orderList.setTotal_page("0");
        orderList.setCur_page("0");
        orderList.setEvery_page_count("" + PAGESIZE);
        map_type.put(TYPE_MONTH, orderList);

        mAdapterMonth = new AdapterOrderListSlide(this, orderList.list);
        mSlideListViewMonth.setAdapter(mAdapterMonth);
        mSlideListViewMonth.setOnItemClickListener(new MyOnItemClickLisnter());
        mSlideListViewMonth.setEmptyView(findViewById(R.id.order_list_slide_empty));
        ((MyApplication)getApplication()).isReLoadOderList = false;// 设置为false，表示重新获取过数据一次
    }
    private void reLoadData(){
        init();
        loadNewData();
    }
    private void loadNewData(){
        setWaitScreen(true);
//        randomCreateTestData();
        OrderList cur = map_type.get(currentType);
        HttpService.instance().getOrderList(currentType, Integer.valueOf(cur.cur_page) + 1, PAGESIZE, new MyCallBack());
    }
    public void loadMore(){
        OrderList list = map_type.get(currentType);
        if(Integer.valueOf(list.total_page) <= Integer.valueOf(list.cur_page))
            return;
        if(Integer.valueOf(list.cur_page) > 1){
            toast.setText(getString(R.string.order_list_item_label11));
            toast.show();
            return;
        }
        if(isLoadMore)
            return;
        isLoadMore = true;
        OrderList cur = map_type.get(currentType);
        if(Integer.valueOf(cur.total_page) <= Integer.valueOf(cur.cur_page)){
            isLoadMore = false;
            return;
        }
        HttpService.instance().getOrderList(currentType, Integer.valueOf(cur.cur_page)+1, PAGESIZE, new MyCallBack());
    }
    private void showCurrentData(){
        if(map_type == null){
            reLoadData();
            return;
        }
        OrderList cur = map_type.get(currentType);
        if(cur!=null && "0".equals(cur.cur_page)){// 第一次没有订单时，再次加载数据,以cur_page是否为0判断，是0表示从未加载过数据，不是0表示已加载过数据
            loadNewData();
            return;
        }
        if(currentType == TYPE_HISTORY) {
            mSlideListViewHistory.setVisibility(View.VISIBLE);
            mSlideListViewMonth.setVisibility(View.INVISIBLE);

            mAdapterHistory.list = cur.list;
            mAdapterHistory.notifyDataSetChanged();
        }else if(currentType == TYPE_MONTH) {
            mSlideListViewMonth.setVisibility(View.VISIBLE);
            mSlideListViewHistory.setVisibility(View.INVISIBLE);

            mAdapterMonth.list = cur.list;
            mAdapterMonth.notifyDataSetChanged();
        }

        if("1".equals(cur.cur_page) && cur.list.isEmpty()){// 第一页 且 无数据时 ，显示提示信息
            findViewById(R.id.order_list_slide_empty).setVisibility(View.VISIBLE);
        }else
            findViewById(R.id.order_list_slide_empty).setVisibility(View.GONE);

        setWaitScreen(false);
        isLoadMore = false;
    }

    class MyOnItemClickLisnter implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            OrderInfo order = (OrderInfo) parent.getAdapter().getItem(position);
            toPay(order, position, 1);
        }
    }
    class MyCallBack implements CallbackResultForActivity{
        @Override
        public void success(Object data, int statusCode, String msg) {
            OrderList list = (OrderList)data;
            OrderList cur = map_type.get(currentType);
            cur.setCur_page(list.cur_page);
            cur.setTotal_page(list.total_page);
            if (cur.list == null)
                cur.list = new ArrayList<>();
            cur.list.addAll(list.list);
            map_type.put(currentType, cur);
            showCurrentData();
        }

        @Override
        public void fail(int statusCode, String msg) {
            setWaitScreen(false);
        }

        @Override
        public void exception(Exception e) {
            setWaitScreen(false);
            OrderList cur = map_type.get(currentType);
            if(cur == null || cur.isEmpty()){// 表示第一次获取数据，网络有问题
//                findViewById(R.id.order_list_slide_empty).setVisibility(View.VISIBLE);
                showNetWrokError();
            }
        }
    }

    @Override
    public void retry() {
        super.retry();
        loadNewData();
    }

    private void updateRadioButton(int checkedId){
        int count = radioGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            RadioButton radioButton = (RadioButton)radioGroup.getChildAt(i);
            if(checkedId == radioButton.getId())
                radioButton.setTextColor(getResources().getColor(R.color.common_font_color_ffffff));
            else
                radioButton.setTextColor(getResources().getColor(R.color.common_button_bg_unselected));
        }
        radioGroup.check(checkedId);

        switch (checkedId){
            case R.id.order_list_radiogroup_month:
                currentType = TYPE_MONTH;
                break;
            case R.id.order_list_radiogroup_history:
                currentType = TYPE_HISTORY;
                break;
        }
        showCurrentData();// 切换时，展示已获取的数据
    }

    /**
     * 去逛逛，无历史订单时，前往商品列表
     * @param view
     */
    public void toproduct_list(View view){
        Intent in = new Intent();
        in.putExtra("requestcode", 111);
        setResult(Activity.RESULT_OK, in);
        finish();
    }

    public void showCancelDialog(final int position){
        BasesUtils.showDialogBySystemUI(this, getString(R.string.order_list_item_label8), getString(R.string.search_title_sub1), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }, "", null, getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                deleteOrder(position, "2");
            }
        });
    }
    public void showDeleteDialog(final int position){
        BasesUtils.showDialogBySystemUI(this, getString(R.string.order_list_item_label7), getString(R.string.search_title_sub1), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }, "", null, getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                deleteOrder(position, "1");
            }
        });
    }
    private void deleteOrder(int position, String operate_type){
        setWaitScreen(true);
        OrderInfo info = null;
        if(currentType == TYPE_MONTH){
            info = (OrderInfo)mAdapterMonth.getItem(position);
        }else  if(currentType == TYPE_HISTORY){
            info = (OrderInfo)mAdapterHistory.getItem(position);
        }
        HttpService.instance().deleteOrderByID(info.order_id, operate_type, new DeleteOrderCallback(this, info, position));
    }

    class DeleteOrderCallback implements CallbackResultForActivity{
        Activity c;
        OrderInfo info;
        int position ;
        public DeleteOrderCallback(Activity activity, OrderInfo info, int position){
            this.c = activity;
            this.info = info;
            this.position = position;
        }
        @Override
        public void success(Object data, int statusCode, String msg) {
            updateListForDelete(position, info);
            setWaitScreen(false);
        }

        @Override
        public void fail(int statusCode, String msg) {
            setWaitScreen(false);
            BasesUtils.showMsg(c, c.getString(R.string.common_nowifi));
        }

        @Override
        public void exception(Exception e) {
            setWaitScreen(false);
            BasesUtils.showMsg(c, c.getString(R.string.common_nowifi));
        }
    }
    private void updateListForDelete(int position, OrderInfo info){
        if(currentType == TYPE_MONTH){
            OrderList list = map_type.get(currentType);
            list.list.remove(position);
            mAdapterMonth = new AdapterOrderListSlide(this, list.list);
            mSlideListViewMonth.setAdapter(mAdapterMonth);
            mSlideListViewMonth.setEmptyView(findViewById(R.id.order_list_slide_empty));
            mAdapterMonth.notifyDataSetChanged();

            list = map_type.get(TYPE_HISTORY);// 订单已删除或取消，需要同步历史订单
            if(list != null && list.list != null && list.list.size() > 0){
                int size = list.list.size();
                for (int i = 0; i < size; i++) {
                    OrderInfo orderInfo = (OrderInfo)list.list.get(i);
                    if(orderInfo.order_id.equals(info.order_id)) {
                        list.list.remove(i);
                        break;
                    }
                }
            }
        }else  if(currentType == TYPE_HISTORY){
            OrderList list = map_type.get(currentType);
            list.list.remove(position);

            mAdapterHistory = new AdapterOrderListSlide(this, list.list);
            mSlideListViewHistory.setAdapter(mAdapterHistory);
            mSlideListViewHistory.setEmptyView(findViewById(R.id.order_list_slide_empty));
            mAdapterHistory.notifyDataSetChanged();

            list = map_type.get(TYPE_MONTH);// 订单已删除或取消，需要同步当月订单
            if(list != null && list.list != null && list.list.size() > 0){
                int size = list.list.size();
                for (int i = 0; i < size; i++) {
                    OrderInfo orderInfo = (OrderInfo)list.list.get(i);
                    if(orderInfo.order_id.equals(info.order_id)) {
                        list.list.remove(i);
                        break;
                    }
                }
            }
        }

    }
    /**
     * 继续支付、点击列表每个Item触发
     * @param info
     */
    public void toPay(OrderInfo info, int position, int type){
        setWaitScreen(true);
        HttpService.instance().getOrderInfoByID(info.order_id, new GetOrderInfoCallback(position, type));
    }

    class GetOrderInfoCallback implements CallbackResultForActivity{
        int position = -1;
        int type = -1;// 0:继续支付  1:查看详细信息
        public GetOrderInfoCallback(int position, int type){
            this.position = position;
            this.type = type;
        }
        @Override
        public void success(Object data, int statusCode, String msg) {
            setWaitScreen(false);
            final OrderInfo info = (OrderInfo)data;

            if(type == 0 && !"1".equals(info.order_status)){// 订单已被删除
                BasesUtils.showDialogBySystemUI(ActivityOrderListSlide.this, getResources().getString(R.string.order_list_item_label9), getResources().getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        updateListForDelete(position, info);
                    }
                }, "", null, "", null);
                return;
            }
            if(type == 0 && "2".equals(info.pay_status)){// 订单已完成，提示“该订单已完成支付！”
                BasesUtils.showDialogBySystemUI(ActivityOrderListSlide.this, getResources().getString(R.string.order_list_item_label10), getResources().getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if(currentType == TYPE_MONTH){
                            mAdapterMonth.list.set(position, info);
                            mAdapterMonth.notifyDataSetChanged();

                            OrderList list = map_type.get(TYPE_HISTORY);// 订单支付完成，需要同步历史订单
                            if(list != null && list.list != null && list.list.size() > 0){
                                int size = list.list.size();
                                for (int i = 0; i < size; i++) {
                                    OrderInfo orderInfo = (OrderInfo)list.list.get(i);
                                    if(orderInfo.order_id.equals(info.order_id)) {
                                        list.list.set(i, info);
                                        break;
                                    }
                                }
                            }
                        }else if(currentType == TYPE_HISTORY){
                            mAdapterHistory.list.set(position, info);
                            mAdapterHistory.notifyDataSetChanged();

                            OrderList list = map_type.get(TYPE_MONTH);// 订单支付完成，需要同步当月订单
                            if(list != null && list.list != null && list.list.size() > 0){
                                int size = list.list.size();
                                for (int i = 0; i < size; i++) {
                                    OrderInfo orderInfo = (OrderInfo)list.list.get(i);
                                    if(orderInfo.order_id.equals(info.order_id)) {
                                        list.list.set(i, info);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }, "", null, "",null);
                return;
            }

            if(TextUtils.isEmpty(info.online_status) || "2".equals(info.online_status)){// 商品已下架
                BasesUtils.showDialogBySystemUI(ActivityOrderListSlide.this, getResources().getString(R.string.order_list_item_label12), getResources().getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }, "", null, "", null);
                return;
            }

            if (info != null && !TextUtils.isEmpty(info.order_id)) {// 直接转到详细界面
                startActivity(new Intent().setClass(ActivityOrderListSlide.this, ActivityOrderDetails.class).putExtra("orderinfo", info));
                return;
            }
        }

        @Override
        public void fail(int statusCode, String msg) {
            setWaitScreen(false);

        }

        @Override
        public void exception(Exception e) {
            setWaitScreen(false);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // SlideMode
        MenuItem item = menu.findItem(R.id.menu_slide_mode);
        item.setTitle("切换SlideMode:" + mSlideListViewHistory.getSlideMode().toString());
        // SlideLeftAction
        item = menu.findItem(R.id.menu_slide_left_action);
        item.setTitle("切换SlideLeftAction:" + mSlideListViewHistory.getSlideLeftAction().toString());
        // SlideRightAction
        item = menu.findItem(R.id.menu_slide_right_action);
        item.setTitle("切换SlideRightAction:" + mSlideListViewHistory.getSlideRightAction().toString());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_slide_mode) {
            changeSlideMode(item);
        } else if (id == R.id.menu_slide_left_action) {
            changeSlideLeftAction(item);
        } else if (id == R.id.menu_slide_right_action) {
            changeSlideRightAction(item);
        } else if (id == R.id.menu_data_change) {
//            randomCreateTestData();
            mAdapterHistory.notifyDataSetChanged();
        } else if (id == R.id.menu_adapter_change) {
//            randomCreateTestData();
//            mAdapter2 = new AdapterOrderListSlide(this, mTestData);
            mSlideListViewHistory.setAdapter(mAdapterHistory);
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeSlideMode(MenuItem item) {
        if (mSlideListViewHistory.getSlideMode() == SlideMode.BOTH) {
            mSlideListViewHistory.setSlideMode(SlideMode.LEFT);
        } else if (mSlideListViewHistory.getSlideMode() == SlideMode.LEFT) {
            mSlideListViewHistory.setSlideMode(SlideMode.RIGHT);
        } else if (mSlideListViewHistory.getSlideMode() == SlideMode.RIGHT) {
            mSlideListViewHistory.setSlideMode(SlideMode.NONE);
        } else if (mSlideListViewHistory.getSlideMode() == SlideMode.NONE) {
            mSlideListViewHistory.setSlideMode(SlideMode.BOTH);
        }
        if (item != null) {
            item.setTitle("切换SlideMode:" + mSlideListViewHistory.getSlideMode().toString());
        }
        Toast.makeText(this, "切换SlideMode:" + mSlideListViewHistory.getSlideMode().toString(), Toast.LENGTH_SHORT).show();
    }

    private void changeSlideLeftAction(MenuItem item) {
        if (mSlideListViewHistory.getSlideLeftAction() == SlideListView.SlideAction.SCROLL) {
            mSlideListViewHistory.setSlideLeftAction(SlideListView.SlideAction.REVEAL);
        } else {
            mSlideListViewHistory.setSlideLeftAction(SlideListView.SlideAction.SCROLL);
        }
        if (item != null) {
            item.setTitle("切换SlideLeftAction:" + mSlideListViewHistory.getSlideLeftAction().toString());
        }
        Toast.makeText(this, "切换SlideLeftAction:" + mSlideListViewHistory.getSlideLeftAction().toString(), Toast.LENGTH_SHORT).show();
    }

    private void changeSlideRightAction(MenuItem item) {
        if (mSlideListViewHistory.getSlideRightAction() == SlideListView.SlideAction.SCROLL) {
            mSlideListViewHistory.setSlideRightAction(SlideListView.SlideAction.REVEAL);
        } else {
            mSlideListViewHistory.setSlideRightAction(SlideListView.SlideAction.SCROLL);
        }
        if (item != null) {
            item.setTitle("切换SlideRightAction:" + mSlideListViewHistory.getSlideRightAction().toString());
        }
        Toast.makeText(this, "切换SlideRightAction:" + mSlideListViewHistory.getSlideRightAction().toString(), Toast.LENGTH_SHORT).show();
    }
}

