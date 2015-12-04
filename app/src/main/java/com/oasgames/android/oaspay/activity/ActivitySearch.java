package com.oasgames.android.oaspay.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.base.tools.activity.BasesActivity;
import com.base.tools.http.CallbackResultForActivity;
import com.base.tools.utils.BasesUtils;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.adapter.AdapterProdcutList;
import com.oasgames.android.oaspay.adapter.AdapterSearchHistoryList;
import com.oasgames.android.oaspay.entity.ProductList;
import com.oasgames.android.oaspay.entity.SearchInfo;
import com.oasgames.android.oaspay.entity.SearchKeywordInfo;
import com.oasgames.android.oaspay.service.HttpService;
import com.oasgames.android.oaspay.tools.APPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 界面 搜索
 * Created by Administrator on 2015/10/16.
 */
public class ActivitySearch extends BasesActivity {
    final int MaxHistoryCount = 10;// 历史搜索记录最多显示10条
    final int MAXPAGESIZE = 20;// 搜索结果每页最大记录数

    List<SearchKeywordInfo> keywordInfoList;

    LinearLayout recommend, history;
    ListView listViewHistory, listViewResult;
    AdapterSearchHistoryList adapterSearchHistory;
    List<SearchInfo> historyDatas;

    AdapterProdcutList adapterSearchResult;

    EditText searchEditText;
    String keyword;

    boolean isLoading = false;// 正在加载数据
    ProductList productList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_search);

        recommend = (LinearLayout)findViewById(R.id.search_recommend_layout);// 如果有推荐，在recommend添加view
        recommend.setVisibility(View.GONE);

        initHistory();

        searchEditText = (EditText)findViewById(R.id.search_edittext);
//        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId== EditorInfo.IME_ACTION_SEARCH ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) {
//                    keyword = v.getText().toString().trim();
//
//                    if(TextUtils.isEmpty(keyword))
//                        return true;
//
//                    InputMethodManager imm = (InputMethodManager)searchEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
//
//                    APPUtils.insertToSearchHistory(keyword);
//
//                    loadSearchResult();
//                    return true;
//                }
//                return false;
//            }
//        });
        listViewResult = (ListView)findViewById(R.id.search_result_list);
        adapterSearchResult = new AdapterProdcutList(this, null, 1, null);
        View resultEmptyView = findViewById(R.id.search_result_list_empty);
        listViewResult.setAdapter(adapterSearchResult);
        listViewResult.setEmptyView(resultEmptyView);
        listViewResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(BasesUtils.isFastDoubleClick())
                    return;
                startActivity(new Intent().setClass(ActivitySearch.this, ActivityProductDetails.class).putExtra("id", adapterSearchResult.getItem(position).product_id));
            }
        });
        loadDefaultKeyword();
    }
    public void onClickViewToSearch(View view){
        if(isLoading)
            return;
        if(BasesUtils.isFastDoubleClick())
            return;
        keyword = searchEditText.getText().toString().trim();

        if(!TextUtils.isEmpty(keyword)){
            InputMethodManager imm = (InputMethodManager)searchEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

            loadSearchResult("1");//每次搜索时，默认为第一页
        }

    }
    private void loadDefaultKeyword(){
        MyApplication application = (MyApplication)getApplication();
        if(application.keywordInfoList == null || application.keywordInfoList.size() <= 0) {
            setWaitScreen(true);
            HttpService.instance().getSearchKeyword(new GetDefaultKeyword());
        }else {
            setWaitScreen(false);
            keywordInfoList = application.keywordInfoList;
            initKeywordView();
        }
    }
    class GetDefaultKeyword implements CallbackResultForActivity{
        @Override
        public void success(Object data, int statusCode, String msg) {
            setWaitScreen(false);
            keywordInfoList = (List<SearchKeywordInfo>)data;
            ((MyApplication)getApplication()).keywordInfoList = keywordInfoList;// 暂存下来，以待下次使用
            initKeywordView();
        }

        @Override
        public void fail(int statusCode, String msg) {
            recommend.setVisibility(View.GONE);
            setWaitScreen(false);
        }

        @Override
        public void exception(Exception e) {
            recommend.setVisibility(View.GONE);
            setWaitScreen(false);
        }
    }
    private void initKeywordView(){
        if(keywordInfoList == null || keywordInfoList.size() <= 0) {
            recommend.setVisibility(View.GONE);
            return;
        }
        recommend.setVisibility(View.VISIBLE);
        TableLayout table = (TableLayout)recommend.getChildAt(1);
        int rowcount = table.getChildCount();
        for (int i = 0; i < rowcount; i++) {
            TableRow row = (TableRow)table.getChildAt(i);
            int unitCount = row.getChildCount();
            if(keywordInfoList.size() <= i*unitCount ){
                row.setVisibility(View.GONE);
                break;
            }
            for (int y = 0; y < unitCount; y++) {
                TextView tv = (TextView)row.getChildAt(y);
                int index = i*unitCount + y;
                if(index >= keywordInfoList.size())
                    tv.setVisibility(View.INVISIBLE);
                else {
                    tv.setText(keywordInfoList.get(index).keyword);
                    tv.setOnClickListener(new DefaultKeywordClick(index));
                }
            }
        }
    }
    class DefaultKeywordClick implements View.OnClickListener{
        int index ;
        public DefaultKeywordClick(int index){
            this.index = index;
        }
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager)searchEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
            keyword = keywordInfoList.get(index).keyword;
            searchEditText.setText(keyword);
        }
    }
    private void loadSearchResult(String curpage){
        if(isLoading)
            return;
        isLoading = true;
        if("1".equals(curpage))
            productList = null; // 每次搜索第一页时，初始化

        setWaitScreen(true);

        APPUtils.insertToSearchHistory(keyword);

        HttpService.instance().getProductList("", keyword, Integer.valueOf(curpage), MAXPAGESIZE, new MySearchResultCallBack(this));
    }
    public void loadSearchResultMore(){
        if(isLoading)
            return;
        isLoading = true;
        setWaitScreen(true);
        HttpService.instance().getProductList("", keyword, Integer.valueOf(productList.cur_page)+1, MAXPAGESIZE, new MySearchResultCallBack(this));
    }
    private void initHistory(){
        historyDatas = new ArrayList<>();
        historyDatas = APPUtils.getLocalSearchHistory(MaxHistoryCount);

        history = (LinearLayout)findViewById(R.id.search_history_layout);
        if(historyDatas.size() <= 0) {
            history.setVisibility(View.GONE);
            return;
        }

        listViewHistory = (ListView)findViewById(R.id.search_history);
        adapterSearchHistory = new AdapterSearchHistoryList(this, historyDatas, 1, null);
        View historyEnd = getLayoutInflater().inflate(R.layout.page_search_history_list_endview, null);
        listViewHistory.addFooterView(historyEnd);
        listViewHistory.setAdapter(adapterSearchHistory);
        listViewHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position >= adapterSearchHistory.data.size()){
                    APPUtils.deleteAllSearchHistory();
//                    BasesUtils.showMsg(ActivitySearch.this, "将清空历史记录");
                    initHistory();
                    return;
                }
                InputMethodManager imm = (InputMethodManager)searchEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                keyword = adapterSearchHistory.getItem(position).keyword;
//                APPUtils.insertToSearchHistory(keyword);
                searchEditText.setText(keyword);
//                loadSearchResult();
            }
        });
    }

    class MySearchResultCallBack implements CallbackResultForActivity{
        Activity a;
        public MySearchResultCallBack(Activity activity){
            this.a = activity;
        }
        @Override
        public void success(Object data, int statusCode, String msg) {
            initHistory();// 刷新 搜索历史记录
            setWaitScreen(false);
            ProductList list = (ProductList)data;
            if(productList == null)
                productList = list;
            else{
                productList.total_page = list.total_page;
                productList.cur_page = list.cur_page;
                productList.list = list.list;
                productList.every_page_count = list.every_page_count;
            }
            initResultView();
            isLoading = false;
        }

        @Override
        public void fail(int statusCode, String msg) {
            setWaitScreen(false);
            isLoading = false;
        }

        @Override
        public void exception(Exception e) {
            setWaitScreen(false);
            isLoading = false;
            if(productList == null)
                showNetWrokError();
        }
    }

    @Override
    public void retry() {
        super.retry();

        loadSearchResult(productList==null?"1":productList.cur_page);
    }

    private void initResultView(){

        FrameLayout resultLayout = (FrameLayout)findViewById(R.id.search_result_list_layout);
        resultLayout.setVisibility(View.VISIBLE);

//        List<ProductInfo> list = new ArrayList<ProductInfo>();
//        for (int i = 0; i < 50; i++) {
//            ProductInfo productInfo = new ProductInfo();
//            productInfo.setProduct_id("id"+i);
//            productInfo.setProduct_name("title"+i);
//
//            list.add(productInfo);
//        }

        setWaitScreen(false);

        if(productList == null || productList.list == null || productList.list.size() <= 0){
            adapterSearchResult.data = new ArrayList<>();
            adapterSearchResult.pages = 0;
            adapterSearchResult.currentPage = 1;
            adapterSearchResult.notifyDataSetChanged();
            return;
        }
        int curpage = Integer.valueOf(productList.cur_page);
        if(curpage > 1){
            adapterSearchResult.data.addAll(productList.list);
        }else{
            adapterSearchResult.data = productList.list;
        }
        adapterSearchResult.pages = Integer.valueOf(productList.total_page);
        adapterSearchResult.currentPage = curpage;
        adapterSearchResult.notifyDataSetChanged();


    }
    public void onClickCancel(View view){
        finish();
    }
}
