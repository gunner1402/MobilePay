package com.oasgames.android.oaspay.service;

import android.content.ContentValues;
import android.database.Cursor;

import com.base.tools.BasesApplication;
import com.base.tools.exception.BasesDataErrorException;
import com.oasgames.android.oaspay.entity.SearchInfo;

import java.util.List;

/**
 * 搜索本地数据服务类
 * Created by Administrator on 2015/10/26.
 */
public class SearchUtil {
    public static final String TABLENAME = "search";
    public static final String COLUMNS_ID = "id";
    public static final String COLUMNS_KEYWORD = "keyword";
    public static final String COLUMNS_TIME = "createtime";
    public static final String COLUMNS_EXT1 = "ext1";
    public static final String COLUMNS_EXT2 = "ext2";
    private static final String[] COLUMNS = new String[]{COLUMNS_ID, COLUMNS_KEYWORD, COLUMNS_TIME, COLUMNS_EXT1, COLUMNS_EXT2};

    public static List<SearchInfo> getAll(int limit) throws BasesDataErrorException{
//        Cursor cur = BasesApplication.dbHelper.loadByWhere(TABLENAME, COLUMNS, COLUMNS_ID+"=?", new String[]{limit});

        try {
            return BasesApplication.dbHelper.loadByWhere2List(TABLENAME, COLUMNS, new SearchInfo(), "", null, null, COLUMNS_TIME + " desc", " "+limit);

        }catch (Exception e){
            throw new BasesDataErrorException("");
        }
    }

    public static int deleteIfExist(String word){
        Cursor cur = BasesApplication.dbHelper.loadByWhere(TABLENAME, COLUMNS, COLUMNS_KEYWORD + "=?", new String[]{word});
        if(cur.getCount()<=0){
            cur.close();
            return 1;
        }
        for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext())
        {
            int nameColumn = cur.getColumnIndex(COLUMNS_ID);
            String id = cur.getString(nameColumn);
            BasesApplication.dbHelper.delete(TABLENAME, COLUMNS_ID + "=?", new String[]{id});
        }
        cur.close();

        return 1;
    }

    public static boolean deleteAll(){
        return BasesApplication.dbHelper.delete(TABLENAME, "");
    }

    public static long insert(String text){
        ContentValues cv = new ContentValues();
        cv.put(COLUMNS_ID, ""+System.nanoTime());
        cv.put(COLUMNS_TIME, ""+System.nanoTime());
        cv.put(COLUMNS_KEYWORD, text);
        return BasesApplication.dbHelper.insert(TABLENAME, cv);
    }
}
