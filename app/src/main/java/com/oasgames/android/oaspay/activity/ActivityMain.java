package com.oasgames.android.oaspay.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.base.tools.google.GoogleBillingTimer;
import com.base.tools.google.GoogleBillingUtils;
import com.base.tools.utils.BasesUtils;
import com.base.tools.utils.DisplayUtil;
import com.facebook.FacebookSdk;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.tools.ReportTimer;
import com.oasgames.android.oaspay.tools.ReportUtils;

import java.util.Locale;
import java.util.Timer;


public class ActivityMain extends FragmentActivity {
	public final int FRAGMENT_SHOP = 0;
	public final int FRAGMENT_NEWS = 1;
	public final int FRAGMENT_MINE = 2;
	public final int REQUESTCODE = 111;
	private int curFragment = 0;

	Toast toast;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    
    RadioGroup tabGroup;

	TextView radioFlag;

	DisplayMetrics dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//不显示程序的标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		//不显示系统的标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		FacebookSdk.sdkInitialize(getApplicationContext());

		dm = BasesUtils.getDisplayMetrics(this);

		init();// 初始化

		setOnListener();// 设置监听事件

		try {
			GoogleBillingUtils.GoogleBillingTimer.schedule(new GoogleBillingTimer(), 10000, 2000);
		} catch (Exception e) {
			GoogleBillingUtils.GoogleBillingTimer = new Timer();
			GoogleBillingUtils.GoogleBillingTimer.schedule(new GoogleBillingTimer(), 10000, 2000);
		}
		try {
			ReportUtils.reportTimer.schedule(new ReportTimer(), 10000, 30000);
		} catch (Exception e) {
			ReportUtils.reportTimer = new Timer();
			ReportUtils.reportTimer.schedule(new ReportTimer(), 10000, 30000);
		}

		reportEvent();// 初始化默认上报一次
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data != null){
			int code = data.getIntExtra("requestcode", 0);

			if(code == REQUESTCODE && resultCode == Activity.RESULT_OK){// 确定是从 订单页面 “去逛逛”返回
				if(curFragment != FRAGMENT_SHOP)
					tabGroup.check(R.id.main_tab_0);
			}
		}
	}

	private void init(){
    	tabGroup = (RadioGroup) findViewById(R.id.main_tab_group);
		tabGroup.check(R.id.main_tab_0);
		RadioButton btn0 = ((RadioButton) tabGroup.getChildAt(0));
		btn0.setTextColor(getResources().getColor(R.color.common_font_color_49a81a));

		radioFlag = new TextView(this);
		radioFlag.setLayoutParams(new LinearLayout.LayoutParams(dm.widthPixels / tabGroup.getChildCount(), DisplayUtil.dip2px(6, dm.scaledDensity)));
		radioFlag.setBackgroundColor(getResources().getColor(R.color.common_font_color_49a81a));
		((LinearLayout)findViewById(R.id.main_foot_flag)).addView(radioFlag);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(2);// 缓存当前页面左右两边的页面个数
        
     // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

	public void onClickView(View view){
		if(BasesUtils.isFastDoubleClick())
			return;
		switch (view.getId()){
			case R.id.common_head_back:
				break;
			case R.id.common_head_tool:
				startActivity(new Intent().setClass(this, ActivitySearch.class));
				break;
			case R.id.fragment_shop_function_order_layout:
				if(BasesUtils.isLogin()) {
					startActivityForResult(new Intent().setClass(this, ActivityOrderListSlide.class), REQUESTCODE);
					ReportUtils.add(ReportUtils.DEFAULTEVENT_FMENUTMYLIST, null, null);
				}else
					startActivity(new Intent().setClass(this, ActivityLogin.class));
				break;
			case R.id.fragment_shop_function_prop_layout:
//				startActivity(new Intent().setClass(this, ActivityProductList.class));
				break;
			case R.id.fragment_shop_function_capture_layout:
				startActivity(new Intent().setClass(this, ActivityCapture.class));
				break;
			case R.id.fragment_shop_function_charge_layout:
				if(BasesUtils.isLogin())
					startActivity(new Intent().setClass(this, ActivityPayPackageList.class));
				else
					startActivity(new Intent().setClass(this, ActivityLogin.class));
				break;
			case R.id.fragment_shop_category_newest_more:
//				startActivity(new Intent().setClass(this, ActivityProductList.class).putExtra("product_type", "1"));
				if(BasesUtils.isLogin())
					startActivity(new Intent().setClass(this, ActivityPayPackageList.class));
				else
					startActivity(new Intent().setClass(this, ActivityLogin.class));
				break;
			case R.id.fragment_shop_category_hot_more:
//				startActivity(new Intent().setClass(this, ActivityProductList.class).putExtra("product_type", "2"));
				if(BasesUtils.isLogin())
					startActivity(new Intent().setClass(this, ActivityPayPackageList.class));
				else
					startActivity(new Intent().setClass(this, ActivityLogin.class));
				break;
//			case R.id.fragment_shop_category_review_more:
//				startActivity(new Intent().setClass(this, ActivityProductList.class).putExtra("product_type", "3"));
//				break;
//			case R.id.fragment_mine_head_touxiang:
//				if(BasesUtils.isLogin())
//					return;
//				startActivity(new Intent().setClass(this, ActivityLogin.class));
//				break;
//			case R.id.fragment_mine_exit:
//				((MyApplication)getApplication()).clearUserInfo();
//				((FragmentMine)mSectionsPagerAdapter.getItem(FRAGMENT_MINE)).updateUserInfo();
//				break;
		}
	}
    private void setOnListener(){
    	mViewPager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// 更新 tab 样式及选中位置
				int type = -1;
				switch (arg0) {
					case FRAGMENT_SHOP:
						type = R.id.main_tab_0;
						break;
					case FRAGMENT_NEWS:
						type = R.id.main_tab_1;
						break;
					case FRAGMENT_MINE:
						type = R.id.main_tab_2;
						break;

					default:
						break;
				}
				tabGroup.check(type);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
    	tabGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int type = -1;
				switch (checkedId) {
				case R.id.main_tab_0:
					type = FRAGMENT_SHOP;
					break;
				case R.id.main_tab_1:
					type = FRAGMENT_NEWS;
					break;
				case R.id.main_tab_2:
					type = FRAGMENT_MINE;
					break;

				default:
					break;
				}
				if(type == -1){
					Toast.makeText(ActivityMain.this, "该项未完成", Toast.LENGTH_LONG).show();
					return;
				}
				if(curFragment == type)
					return;

				changeRadioStyle(checkedId);

				if(mViewPager.getCurrentItem() != type)
		        	mViewPager.setCurrentItem(type);// 指定项

				curFragment = type;

				reportEvent();// 每切换一次，上报事件
			}
		});
    }

	/**
	 * Mdata 上报
	 */
	private void reportEvent(){
		String eventName = "";
		switch (curFragment){
			case FRAGMENT_SHOP:
				eventName = ReportUtils.DEFAULTEVENT_MALL;
				break;
			case FRAGMENT_NEWS:
				eventName = ReportUtils.DEFAULTEVENT_INFORMATION;
				break;
			case FRAGMENT_MINE:
				eventName = ReportUtils.DEFAULTEVENT_ME;
				break;
		}
		if(!TextUtils.isEmpty(eventName))
			ReportUtils.add(eventName, null, null);
	}
	/**
	 * 改变 RadioButton的字体颜色，及动画
	 * @param checkedId
	 */
	private void changeRadioStyle(int checkedId){
		int count = tabGroup.getChildCount();
		int index = 0;
		for (int i=0;i<count;i++){
			RadioButton v = (RadioButton)tabGroup.getChildAt(i);
			if(v.getId() == checkedId){
				v.setTextColor(getResources().getColor(R.color.common_font_color_49a81a));
				index = i;
			}else
				v.setTextColor(getResources().getColor(R.color.common_font_color_000000));
		}
		int singleRadioWidth = dm.widthPixels/count;
		float fromX = curFragment*singleRadioWidth;
		float toX = index*singleRadioWidth;
		TranslateAnimation animation = new TranslateAnimation(fromX, toX, 0, 0);
		animation.setDuration(Math.abs((int)(toX-fromX)/singleRadioWidth*200));
		animation.setFillAfter(true);
		radioFlag.startAnimation(animation);
	}
	int keyBackRepeatCount = 0;// 点击返回键的次数
	long lastTime = 0;// 上一次点击返回的时间
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		if (KeyEvent.KEYCODE_BACK == keyCode){
			long curTime = System.currentTimeMillis();
			if(keyBackRepeatCount >= 1 && (curTime - lastTime<3000)){// 在3秒内连续2次点击返回键
				if(GoogleBillingUtils.GoogleBillingTimer!=null)
					GoogleBillingUtils.GoogleBillingTimer.cancel();
				if(ReportUtils.reportTimer!=null)
					ReportUtils.reportTimer.cancel();

				if(toast != null)
					toast.cancel();
				finish();
				return true;
			}
			
			keyBackRepeatCount ++;
			lastTime = curTime;

			if(toast == null)
				toast = Toast.makeText(this, getString(R.string.main_notice_1), Toast.LENGTH_LONG);
			toast.setText(getString(R.string.main_notice_1));
			toast.show();
//			Toast.makeText(this, getString(R.string.main_notice_1), Toast.LENGTH_LONG).show();
		}
		return false;
	}
    

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
//            return PlaceholderFragment.newInstance(position + 1);
//        	return lists.get(position);
        	switch (position) {
            	case FRAGMENT_SHOP:
                   		return new FragmentShop();
               case FRAGMENT_NEWS:
                   		return new FragmentNews();
               case FRAGMENT_MINE:
                   		return new FragmentMine();
               default:
                   return null;
               }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
//            switch (position) {
//                case 0:
//                    return getString(R.string.title_section1).toUpperCase(l);
//                case 1:
//                    return getString(R.string.title_section2).toUpperCase(l);
//                case 2:
//                    return getString(R.string.title_section3).toUpperCase(l);
//            }
            return null;
        }
    }

}
