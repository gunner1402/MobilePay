/**
 * 应用工具类
 */
package com.oasgames.android.oaspay.tools;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.base.tools.BasesApplication;
import com.base.tools.utils.BasesUtils;
import com.oasgames.android.oaspay.entity.SearchInfo;
import com.oasgames.android.oaspay.service.SearchUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * @author xdb
 * 
 */
public class APPUtils {

    /**
     * 注销时，清除服ID、角色ID
     * @return
     */
    public static boolean clearInfoForLogout(){
        if(BasesApplication.userInfo != null){
            BasesApplication.userInfo.setServerID("");
            BasesApplication.userInfo.setRoleID("");
        }
        return true;
    }

    /**
     * 获取本地的搜索记录
     * @return
     */
    public static List<SearchInfo> getLocalSearchHistory(int maxCount){
        List<SearchInfo> list = new ArrayList<SearchInfo>();
        try {

            list = SearchUtil.getAll(maxCount);
        }catch (Exception e){
            Log.d("", e.getMessage());
        }
        return list;
    }
    /**
     * 删除本地所有搜索记录
     * @return
     */
    public static boolean deleteAllSearchHistory(){

        return SearchUtil.deleteAll();
    }
    /**
     * 新增本地搜索记录
     * @return
     */
    public static long insertToSearchHistory(String keyword){

        return SearchUtil.insert(keyword);
    }

    /**
     * Toast 显示错误信息
     * -4       用户名密码错误
     * -2000    网络异常
     * @param activity
     * @param errorCode
     */
    public static void showErrorMessageByErrorCode(Activity activity, String errorCode){
        ;
        BasesUtils.showMsg(activity, activity.getString(BasesUtils.getResourceValue(activity, "string", "common_error_notice" + errorCode.replace("-", "_"))));
    }

    /**
     * 测量listView的高度
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}
