package com.oasgames.android.oaspay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.base.tools.activity.BasesActivity;
import com.base.tools.http.CallbackResultForActivity;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.adapter.AdapterProdcutList;
import com.oasgames.android.oaspay.entity.ProductInfo;
import com.oasgames.android.oaspay.entity.ProductList;
import com.oasgames.android.oaspay.service.HttpService;
import com.oasgames.android.oaspay.tools.ReportUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 界面 商品（礼品）列表
 * Created by Administrator on 2015/10/16.
 */
public class ActivityProductList extends BasesActivity {
    final int MAXPAGESIZE = 20;
    boolean isLoading = false;
    MyHandler myHandler;
    ListView listView;
    AdapterProdcutList adapter;

    ProductList productList;
    String product_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_product_list);

        initHead(true, true, null, false, getString(R.string.product_list_title), true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ActivityProductList.this, ActivitySearch.class));
            }
        });

        product_type = getIntent().getStringExtra("product_type");

        listView = (ListView)findViewById(R.id.product_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent().setClass(ActivityProductList.this, ActivityProductDetails.class).putExtra("id", ((ProductInfo) productList.list.get(position)).product_id));
            }
        });
        List<ProductInfo> list = new ArrayList<ProductInfo>();
        adapter = new AdapterProdcutList(this, list, 1, null);
        listView.setAdapter(adapter);

        myHandler = new MyHandler(new WeakReference<ActivityProductList>(this));

        setWaitScreen(true);
        myHandler.sendEmptyMessage(100);

        ReportUtils.add(ReportUtils.DEFAULTEVENT_PRODUCTLIST, null, null);
    }

    private void loadDataForCurPage(){
        loadData(1);
    }
    public void loadDataForNextPage(){
        loadData(Integer.valueOf(productList.cur_page)+1);
    }
    private void loadData(int pageNo){
        if(isLoading)
            return;
        isLoading = true;
        setWaitScreen(true);
        HttpService.instance().getProductList(product_type, "", pageNo, MAXPAGESIZE, new MyCallback());
    }
    class MyCallback implements CallbackResultForActivity{
        @Override
        public void success(Object data, int statusCode, String msg) {
            isLoading = false;
            setWaitScreen(false);
            productList = (ProductList)data;
            if(productList != null && productList.list != null && productList.list.size()>0) {
                adapter.pages = Integer.valueOf(productList.total_page);
                adapter.currentPage = Integer.valueOf(productList.cur_page);
                adapter.data.addAll(productList.list);
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void fail(int statusCode, String msg) {
            isLoading = false;
            setWaitScreen(false);
        }

        @Override
        public void exception(Exception e) {
            isLoading = false;
            setWaitScreen(false);
            if(productList == null)
                showNetWrokError();
        }
    }

    @Override
    public void retry() {
        super.retry();
        loadDataForCurPage();
    }

    private static class MyHandler extends Handler {


        //使用弱引用避免Handler泄露.这里的泛型参数可以不是Activity，也可以是Fragment等
        private WeakReference<ActivityProductList> weakReference;

        protected MyHandler(WeakReference<ActivityProductList> wk){
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    weakReference.get().loadDataForCurPage();
                    break;
                default:
                    break;
            }
        }
    }
}
