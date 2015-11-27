package com.oasgames.android.oaspay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.base.tools.activity.BasesActivity;
import com.base.tools.utils.BasesUtils;
import com.base.tools.utils.DisplayUtil;
import com.oasgames.android.oaspay.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付订单列表
 * Created by Administrator on 2015/10/13.
 */
public class ActivityOrderList extends BasesActivity{

    RadioGroup radioGroup;
    RecyclerView recyclerView;


    List personList;
    PersonAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_order_list_recyclerview);
        initHead(true, true, null, false, getString(R.string.fragment_shop_function_order), false, null);

        radioGroup = (RadioGroup)findViewById(R.id.order_list_radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateRadioButton(checkedId);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.order_list);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//        //添加分割线
//        recyclerView.addItemDecoration(new DividerItemDecoration(
//                getActivity(), DividerItemDecoration.HORIZONTAL_LIST));

        initData();
        adapter = new PersonAdapter(this,personList);
//        adapter.setOnRecyclerViewListener(this);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.order_list_item:// item 点击事件
                        BasesUtils.showMsg(ActivityOrderList.this, " item 点击事件");
                        break;
                    case R.id.order_list_item_topay:// item 继续支付 点击事件
                        BasesUtils.showMsg(ActivityOrderList.this, " item 继续支付 点击事件");
                        break;
                    case R.id.order_list_item_cancel:// item 取消订单 点击事件
                        BasesUtils.showMsg(ActivityOrderList.this, " item 取消订单 点击事件");
                        break;
                    case R.id.order_list_item_delete:// item 删除 点击事件
                        BasesUtils.showMsg(ActivityOrderList.this, " item 删除 点击事件");
                        break;
                }
            }
        });

        recyclerView.setAdapter(adapter);

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
        loadData();
    }
    private void loadData(){

    }
    private void initData(){
        personList = new ArrayList();

        for (int count=0;count<50;count++){
            Person person = new Person();
            person.id = count;
            person.name = "聊是减肥了时间飞逝都叫我恶如我饿ur物品扔威迫我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我irsmvxmcvsljflsdjf"+count;
            person.age = count;
            personList.add(person);
        }
    }

    class Person {
        int id;
        String name;
        int age;
    }

    class PersonAdapter extends RecyclerView.Adapter{
        List persons = null;
        ActivityOrderList activity;
        View.OnClickListener clickListener;
        public PersonAdapter(ActivityOrderList activity, List data){
            this.persons = data;
            this.activity = activity;
        }
        public void setOnClickListener(View.OnClickListener listener){
            this.clickListener = listener;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.page_order_list_slide_item, null);

            return new PersonHolderView(v, clickListener);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
            final PersonHolderView holder = (PersonHolderView) viewHolder;
            final Person person = (Person) persons.get(i);
            holder.tv_title.setText(person.name);
            holder.tv_money.setText(person.age + "岁");


            System.out.println("即将删除" + holder.tv_del.isShown());

            if(!holder.tv_del.isShown()) {
                holder.v_allItem.setLongClickable(true);
            holder.v_allItem.setOnTouchListener(new View.OnTouchListener() {
                    float x, y;
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                x = event.getX();

                                System.out.println("OnTouchListener down  " + x);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (holder.tv_del.isShown()) {
                                    return true;
                                }
                                System.out.println("OnTouchListener move  " + event.getX());
                                break;
                            case MotionEvent.ACTION_UP:
                                if (Math.abs(event.getX() - x) < 10 ) {// 整个item点击事件
                                    if (holder.tv_del.isShown()) {
//                                        TranslateAnimation animation = new TranslateAnimation(-(DisplayUtil.dip2px(64, BasesUtils.getDisplayMetrics(activity).scaledDensity)), 0, 0, 0);
//                                        animation.setDuration(200);
//                                        animation.setFillAfter(true);
//                                        v.startAnimation(animation);
                                        holder.tv_del.setVisibility(View.GONE);
                                        v.scrollBy(-(DisplayUtil.dip2px(64, BasesUtils.getDisplayMetrics(activity).scaledDensity)), 0);
                                        v.postInvalidate();
                                        return false;
                                    }
                                    activity.startActivity(new Intent().setClass(activity, ActivityOrderDetails.class).putExtra("orderid", person.id));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                    return false;
                                }
                                holder.tv_del.setVisibility(View.VISIBLE);
                                v.scrollBy((DisplayUtil.dip2px(64, BasesUtils.getDisplayMetrics(activity).scaledDensity)), 0);
                                v.postInvalidate();

//                                TranslateAnimation animation = new TranslateAnimation(0, -(DisplayUtil.dip2px(64, BasesUtils.getDisplayMetrics(activity).scaledDensity)), 0, 0);
//                                animation.setDuration(200);
//                                animation.setFillAfter(true);
//                                v.startAnimation(animation);

//                                    if (holder.tv_del.isShown()) {
//                                        holder.tv_del.setVisibility(View.GONE);
//                                        return true;
//                                    }
//                                    holder.tv_del.setVisibility(View.VISIBLE);
//                                    holder.tv_del.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            int size = adapter.persons.size();
//                                            for (int i = 0; i < size; i++) {
//                                                Person p = (Person) adapter.persons.get(i);
//                                                if (p.id == person.id) {
//                                                    adapter.persons.remove(p);
//                                                    adapter.notifyItemRemoved(i);
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                    });

                                System.out.println("OnTouchListener up");
                                break;
                        }
                        return false;
//                        return new GestureDetector(activity.getApplicationContext(), new MyGesture(holder.tv_del, i, person.id)).onTouchEvent(event);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            if(persons==null)
                return 0;
            return persons.size();
        }

        @Override
        public long getItemId(int position) {
            return ((Person)persons.get(position)).id;
        }
    }

    class PersonHolderView extends RecyclerView.ViewHolder{
        LinearLayout v_allItem;
        TextView tv_title;
        TextView tv_money;
        TextView tv_del;
        TextView tv_topay;
        TextView tv_cancel;
        public PersonHolderView(View v, View.OnClickListener listener){
            super(v);
            v_allItem = (LinearLayout)v.findViewById(R.id.order_list_item);
            tv_title = (TextView)v.findViewById(R.id.order_list_item_title);
            tv_money = (TextView)v.findViewById(R.id.order_list_item_money);
            tv_del = (TextView)v.findViewById(R.id.order_list_item_delete);
            tv_topay = (TextView)v.findViewById(R.id.order_list_item_topay);
            tv_cancel = (TextView)v.findViewById(R.id.order_list_item_cancel);

//            v_allItem.setOnClickListener(listener);
            tv_topay.setOnClickListener(listener);
            tv_cancel.setOnClickListener(listener);
            tv_del.setOnClickListener(listener);

        }
    }

    class MyGesture extends GestureDetector.SimpleOnGestureListener {
        int curPosition = -1;
        int id ;
        View v;
        public MyGesture(View v, int position, int id){
            this.curPosition = position;
            this.v = v;
            this.id = id;

        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            System.out.println("onFling"+velocityX);
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            System.out.println("onScroll"+distanceX);
            if(v.isShown()) {
                v.setVisibility(View.GONE);
                return true;
            }
            v.setVisibility(View.VISIBLE);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int size = adapter.persons.size();
                    for (int i = 0; i < size; i++) {
                        Person p = (Person) adapter.persons.get(i);
                        if (p.id == id) {
                            adapter.persons.remove(p);
                            adapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                }
            });
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            System.out.println("onSingleTapUp"+e.getAction());
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            super.onSingleTapConfirmed(e);
            System.out.println("onSingleTapConfirmed up   作为点击事件 " + e.getX());
            if(!v.isShown()) {
            }
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setContentView(R.layout.common_null);
        adapter = null;
        personList = null;
    }
}
