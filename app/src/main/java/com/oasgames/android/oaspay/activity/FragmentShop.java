package com.oasgames.android.oaspay.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.base.tools.http.CallbackResultForActivity;
import com.base.tools.utils.BasesUtils;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.adapter.AdapterShopNewestHotProductList;
import com.oasgames.android.oaspay.entity.ProductInfo;
import com.oasgames.android.oaspay.entity.ShopFocus;
import com.oasgames.android.oaspay.entity.ShopList;
import com.oasgames.android.oaspay.service.HttpService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FragmentShop extends Fragment {
	final String TAG = FragmentShop.class.getSimpleName();

	ShopList shop;

	ViewPager viewPager;
	AdapterShopHeadImage headAdapter;
	ArrayList<ImageView> viewPagerImages = new ArrayList<ImageView>();
	ArrayList<ImageView> viewPagerImageFlags = new ArrayList<ImageView>();

	View main_shop_wait;
	View networkError;
	public ShopHeadImageHandler handler;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_main_shop, null);
	}
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		main_shop_wait = getActivity().findViewById(R.id.main_shop_wait);
		main_shop_wait.setVisibility(View.VISIBLE);
		networkError =getActivity().findViewById(R.id.main_shop_netwrok_error);

//		TextView tv = (TextView)this.getActivity().findViewById(R.id.main_shop_test);
//		tv.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
////				//调用扫码界面
////				startActivity(new Intent().setClass(FragmentShop.super.getActivity(), CaptureActivity.class));
//
////				// 调用Google支付页面
////				String productID = "oas_mtester_300_7174";// testpay_m_product_1 testpay_nm_product_1 oas_ahbr_300  oas_ahbr_1500
////				Intent intent = new Intent().setClass(FragmentShop.super.getActivity(), ActivityGooglePlayBilling.class);
////				startActivity(intent.putExtra("inAppProductID", productID).putExtra("revenue", "0.99").putExtra("ext", ""));
//
////				//订单列表
////				startActivity(new Intent().setClass(FragmentShop.super.getActivity(), ActivityOrderList.class));
//
////				FragmentShop.super.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
////				BasesUtils.showDialogBySystemUI(getActivity(), "显示内容", "取消取消取消", new DialogInterface.OnClickListener() {
////					@Override
////					public void onClick(DialogInterface dialog, int which) {
////						System.out.println("点击取消按钮了，，，，，，" + which);
////					}
////				}, null, new DialogInterface.OnClickListener() {
////					@Override
////					public void onClick(DialogInterface dialog, int which) {
////						System.out.println("点击中性按钮了，，，，，，" + which);
////					}
////				}, "确定确定确定", new DialogInterface.OnClickListener() {
////					@Override
////					public void onClick(DialogInterface dialog, int which) {
////						System.out.println("点击确定按钮了，，，，，，" + which);
////					}
////				});
//				BasesUtils.showSingleChoiceDialogListBySystemUI(getActivity(), new String[]{"123", "456", "123sd", "456sdf", "1234646", "456wrwer"}, 10, new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				});
//			}
//		});

		handler = new ShopHeadImageHandler(new WeakReference<FragmentShop>(this));

		handler.sendEmptyMessageDelayed(ShopHeadImageHandler.MSG_LOADALL, 1500);
	}// end of onActivityCreated

	private void loadAllData(){
		main_shop_wait.setVisibility(View.VISIBLE);
		HttpService.instance().getShopInfo(new MyCallBack(this.getActivity()));
	}

	private class MyCallBack implements CallbackResultForActivity{
		ActivityMain a;
		public MyCallBack(Activity a){
			this.a = (ActivityMain)a;
		}
		@Override
		public void exception(Exception e) {
			main_shop_wait.setVisibility(View.INVISIBLE);
			networkError.setVisibility(View.VISIBLE);
			networkError.findViewById(R.id.common_network_retry).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					networkError.setVisibility(View.INVISIBLE);
					loadAllData();
				}
			});
		}

		@Override
		public void success(Object data, int statusCode, String msg) {
			main_shop_wait.setVisibility(View.INVISIBLE);
			shop = (ShopList)data;
//			for (ShopFocus focus:shop.focusList) {
//				System.out.println("focus:"+focus.focus_img_url);
//			}
//			for (ProductInfo p:shop.newestList) {
//				System.out.println("newest:"+p.product_id);
//			}
//			for (ProductInfo p:shop.hotList) {
//				System.out.println("hot:"+p.product_id);
//			}
//			for (ProductInfo p:shop.browseList) {
//				System.out.println("browse:"+p.id);
//			}
			initViewPager();
			initNewest();
			initHotest();
			initBrowser();
		}

		@Override
		public void fail(int statusCode, String msg) {// 失败与异常相同处理，否则，失败时无法触发重新加载数据
			main_shop_wait.setVisibility(View.INVISIBLE);
			networkError.setVisibility(View.VISIBLE);
			networkError.findViewById(R.id.common_network_retry).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					networkError.setVisibility(View.INVISIBLE);
					loadAllData();
				}
			});
		}
	}

	private void initNewest(){
		if(shop == null || shop.newestList == null || shop.newestList.size() <= 0){
			getActivity().findViewById(R.id.main_shop_newest).setVisibility(View.GONE);
			return;
		}
		GridView newestGridView = (GridView)getActivity().findViewById(R.id.fragment_shop_category_newest_grid);
		final AdapterShopNewestHotProductList newestAdapter = new AdapterShopNewestHotProductList(getActivity(), shop.newestList, 1, null);
		newestGridView.setAdapter(newestAdapter);
		newestGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(BasesUtils.isFastDoubleClick())
					return;

				ProductInfo p = newestAdapter.getItem(position);
				if("payapp".equals(p.product_property)){// 套餐，跳转至套餐列表

					if(BasesUtils.isLogin())
						startActivity(new Intent().setClass(getActivity(), ActivityPayPackageList.class).putExtra("id", p.price_product_id));
					else
						startActivity(new Intent().setClass(getActivity(), ActivityLogin.class));
				}else if("giftapp".equals(p.product_property)) {// 跳转至礼包列表
					startActivity(new Intent().setClass(getActivity(), ActivityProductDetails.class).putExtra("id", p.product_id));
				}
			}
		});
	}
	private void initHotest(){
		if(shop == null || shop.hotList == null || shop.hotList.size() <= 0){
			getActivity().findViewById(R.id.main_shop_hot).setVisibility(View.GONE);
			return;
		}
		GridView hotestGridView = (GridView)getActivity().findViewById(R.id.fragment_shop_category_hot_grid);
		final AdapterShopNewestHotProductList hotestAdapter = new AdapterShopNewestHotProductList(getActivity(), shop.hotList, 1, null);
		hotestGridView.setAdapter(hotestAdapter);
		hotestGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(BasesUtils.isFastDoubleClick())
					return;
				ProductInfo p = hotestAdapter.getItem(position);

				if("payapp".equals(p.product_property)) {// 套餐，跳转至套餐列表
					if(BasesUtils.isLogin())
						startActivity(new Intent().setClass(getActivity(), ActivityPayPackageList.class).putExtra("id", p.price_product_id));
					else
						startActivity(new Intent().setClass(getActivity(), ActivityLogin.class));
				}else if("giftapp".equals(p.product_property)) {// 跳转至礼包列表\
					startActivity(new Intent().setClass(getActivity(), ActivityProductDetails.class).putExtra("id", p.product_id));
				}
			}
		});
	}
	ListView browseListView;
	private void initBrowser(){
		getActivity().findViewById(R.id.main_shop_review).setVisibility(View.GONE);
//		if(shop == null || shop.browseList == null || shop.browseList.size() <= 0){
//			getActivity().findViewById(R.id.main_shop_review).setVisibility(View.GONE);
//
//			return;
//		}
//		browseListView = (ListView)getActivity().findViewById(R.id.fragment_shop_category_review_grid);
//		final AdapterProdcutList reviewAdapter = new AdapterProdcutList(getActivity(), shop.browseList, 1, null);
//		browseListView.setAdapter(reviewAdapter);
//		browseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				if(BasesUtils.isFastDoubleClick())
//					return;
//				startActivity(new Intent().setClass(getActivity(), ActivityProductDetails.class).putExtra("id", reviewAdapter.getItem(position).product_id));
//			}
//		});
//
//		ViewGroup.LayoutParams params = browseListView.getLayoutParams();
//		params.height = shop.browseList.size() * DisplayUtil.dip2px(78, BasesUtils.getDisplayMetrics(getActivity()).density) + (browseListView.getDividerHeight() * (reviewAdapter.getCount() - 1));
//		// listView.getDividerHeight()获取子项间分隔符占用的高度
//		// params.height最后得到整个ListView完整显示需要的高度
//		browseListView.setLayoutParams(params);

	}
	private void initViewPager(){
		viewPager = (ViewPager) getActivity().findViewById(R.id.main_shop_viewpager);

		viewPagerImages.clear();
		headAdapter = new AdapterShopHeadImage(viewPagerImages);
		viewPager.setAdapter(headAdapter);
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			//配合Adapter的currentItem字段进行设置。
			@Override
			public void onPageSelected(int position) {
				handler.sendMessage(Message.obtain(handler, ShopHeadImageHandler.MSG_PAGE_CHANGED, position, 0));

				int size = viewPagerImageFlags.size();
				if (size <= 0)
					return;
				viewPagerImageFlags.get(position % size)
						.setImageResource(R.mipmap.common_viewpager_select);
				for (int i = 0; i < size; i++) {
					if (position % size != i) {
						viewPagerImageFlags.get(i)
								.setImageResource(R.mipmap.common_viewpager_unselect);
					}
				}
			}

			//覆写该方法实现轮播效果的暂停和恢复
			@Override
			public void onPageScrollStateChanged(int state) {

				switch (state) {
					case ViewPager.SCROLL_STATE_DRAGGING:
//						handler.sendEmptyMessage(ImageHandler.MSG_KEEP_SILENT);
						handler.removeMessages(ShopHeadImageHandler.MSG_UPDATE_IMAGE);
						//开始轮播效果
						handler.sendEmptyMessageDelayed(ShopHeadImageHandler.MSG_UPDATE_IMAGE, ShopHeadImageHandler.MSG_DELAY);
						break;
					case ViewPager.SCROLL_STATE_IDLE:
						handler.removeMessages(ShopHeadImageHandler.MSG_UPDATE_IMAGE);
						handler.sendEmptyMessageDelayed(ShopHeadImageHandler.MSG_UPDATE_IMAGE, ShopHeadImageHandler.MSG_DELAY);
						break;
					default:
						break;
				}// end of switch
			}// end of onPageScrollStateChanged
		});

//		viewPager.setCurrentItem(Integer.MAX_VALUE/2);//默认在中间，使用户看不到边界
		viewPager.setCurrentItem(0);//测试时默认0
		initViewPagerData();
	}// end of initViewPager

	private void initViewPagerData(){

		int count = shop.focusList.size();
		for (int k = 0; k < 3; k++) {// 循环3次，解决当轮播图只有一个时，无法轮播的问题
			for (int i = 0; i < count; i++) {
				final ShopFocus sf = shop.focusList.get(i);
				LayoutInflater inflater = LayoutInflater.from(this.getActivity());
				final ImageView view = (ImageView) inflater.inflate(R.layout.fragment_main_shop_viewpager, null);
				view.setBackgroundResource(R.mipmap.shop_viewpager_bg);
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						sf.startActivity(getActivity());
					}
				});
				BasesUtils.loadImg(getActivity(), view, sf.focus_img_url);

				viewPagerImages.add(view);
			}//end for
		}

		headAdapter.viewlist = viewPagerImages;
		headAdapter.notifyDataSetChanged();
		if(!handler.hasMessages(ShopHeadImageHandler.MSG_UPDATE_IMAGE) && viewPagerImages.size()>0)
			//开始轮播效果
			handler.sendEmptyMessageDelayed(ShopHeadImageHandler.MSG_UPDATE_IMAGE, ShopHeadImageHandler.MSG_DELAY);

		//ViewPager指示标志
		LinearLayout viewPagerFlag = (LinearLayout)getActivity().findViewById(R.id.main_shop_viewpager_flag);
		for (int i = 0; i < count; i++) {
			ImageView iv = new ImageView(getActivity());
			iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			iv.setPadding(0,0,10,0);
			if (i == viewPager.getCurrentItem()){
				iv.setImageResource(R.mipmap.common_viewpager_select);
			}else
				iv.setImageResource(R.mipmap.common_viewpager_unselect);
			viewPagerFlag.addView(iv);
			viewPagerImageFlags.add(iv);
		}// end of for


	}
	@Override
	public void onResume() {
		super.onResume();
		if(!handler.hasMessages(ShopHeadImageHandler.MSG_UPDATE_IMAGE) && viewPagerImages.size()>0)
			//开始轮播效果
			handler.sendEmptyMessageDelayed(ShopHeadImageHandler.MSG_UPDATE_IMAGE, ShopHeadImageHandler.MSG_DELAY);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	private static class ShopHeadImageHandler extends Handler{

		/**
		 * 请求更新显示的View。
		 */
		protected static final int MSG_UPDATE_IMAGE  = 1;
		/**
		 * 请求暂停轮播。
		 */
		protected static final int MSG_KEEP_SILENT   = 2;
		/**
		 * 请求恢复轮播。
		 */
		protected static final int MSG_BREAK_SILENT  = 3;
		/**
		 * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。
		 * 例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
		 * 则应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
		 */
		protected static final int MSG_PAGE_CHANGED  = 4;

		protected static final int MSG_LOADALL = 100;

		//轮播间隔时间
		protected static final long MSG_DELAY = 3000;

		//使用弱引用避免Handler泄露.这里的泛型参数可以不是Activity，也可以是Fragment等
		private WeakReference<FragmentShop> weakReference;
		private int currentItem = 0;

		protected ShopHeadImageHandler(WeakReference<FragmentShop> wk){
			weakReference = wk;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			FragmentShop fragmentShop = weakReference.get();
			if (fragmentShop.getActivity()==null){
				//Activity已经回收，无需再处理UI了
				return ;
			}

			//检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题。
//			if (fragmentShop.handler.hasMessages(MSG_UPDATE_IMAGE)){
//				fragmentShop.handler.removeMessages(MSG_UPDATE_IMAGE);
//			}
			switch (msg.what) {
				case MSG_UPDATE_IMAGE:
					currentItem++;
					fragmentShop.viewPager.setCurrentItem(currentItem);
					//准备下次播放
					fragmentShop.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
					break;
				case MSG_KEEP_SILENT:
					//只要不发送消息就暂停了
					break;
				case MSG_BREAK_SILENT:
					fragmentShop.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
					break;
				case MSG_PAGE_CHANGED:
					//记录当前的页号，避免播放的时候页面显示不正确。
					currentItem = msg.arg1;
					break;
				case MSG_LOADALL:
					fragmentShop.loadAllData();
					break;
				default:
					break;
			}// end of switch
		}// end of handleMessage
	}// end of ImageHandler

	private class AdapterShopHeadImage extends PagerAdapter {

		private ArrayList<ImageView> viewlist;

		public AdapterShopHeadImage(ArrayList<ImageView> viewlist) {
			this.viewlist = viewlist;
		}

		@Override
		public int getCount() {
			//设置成最大，使用户看不到边界
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}
		@Override
		public void destroyItem(ViewGroup container, int position,
								Object object) {
			//Warning：不要在这里调用removeView
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			if(viewlist == null || viewlist.size() <= 0)
				return  null;

			//对ViewPager页号求模取出View列表中要显示的项
			position %= viewlist.size();
			if (position<0){
				position = viewlist.size()+position;
			}

			ImageView view = viewlist.get(position);
			//如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
			ViewParent vp =view.getParent();
			if (vp!=null){
				ViewGroup parent = (ViewGroup)vp;
				parent.removeView(view);
			}
			container.addView(view);
			//add listeners here if necessary
			return view;
		}
	}// end of AdapterShopHeadImage
}// end of FragmentShop
