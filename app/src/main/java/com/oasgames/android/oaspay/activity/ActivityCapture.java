package com.oasgames.android.oaspay.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

import com.base.tools.activity.BasesActivity;
import com.base.tools.http.CallbackResultForActivity;
import com.base.tools.utils.BasesUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.camera.CameraManager;
import com.google.zxing.decoding.CaptureActivityHandler;
import com.google.zxing.decoding.InactivityTimer;
import com.google.zxing.view.ViewfinderView;
import com.oasgames.android.oaspay.R;
import com.oasgames.android.oaspay.entity.OrderInfo;
import com.oasgames.android.oaspay.service.HttpService;
import com.oasgames.android.oaspay.tools.APPUtils;
import com.oasgames.android.oaspay.tools.ReportUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Vector;

/**
 * Initial the camera
 * @author xdb
 */
public class ActivityCapture extends BasesActivity implements Callback {
//	private static String CHARGEURL = "http://pay.oasgames.com/payment/oaspay.php?";

	private static final String TAG = ActivityCapture.class.getName();

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;

	String resultString;// 扫描内容经处理后
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.page_capture);
				CameraManager.init(getApplication());
		initHead(true, true, null, false, getString(R.string.fragment_shop_function_capture), false, null);
		viewfinderView = (ViewfinderView) findViewById(R.id.captrue_viewfinder);
		
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		ReportUtils.add(ReportUtils.DEFAULTEVENT_SCANCODE, null, null);

		setWaitScreen(false);
	}
	public void onClickViewToInput(View view){
		if(BasesUtils.isFastDoubleClick())
			return;
		startActivity(new Intent().setClass(this, ActivityCaptureInput.class));
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(BasesUtils.getResourceValue("id", "captrue_preview"));
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	/**
	 * 处理扫描结果
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
//		playBeepSoundAndVibrate();
		resultString = result.getText();
		resultString = checkResult(resultString);
		ReportUtils.add(ReportUtils.DEFAULTEVENT_SCANCODENUM, null, null);

		if(!TextUtils.isEmpty(resultString)){
			if(BasesUtils.isLogin())
				getOrderInfo(resultString);
			else
				startActivityForResult(new Intent().setClass(this, ActivityLogin.class), 100);
		}else{
			BasesUtils.showDialogBySystemUI(this, getString(R.string.capture_scan_text3), getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					ReportUtils.add(ReportUtils.DEFAULTEVENT_SCANCODEFAIL, null, null);
					handler.sendEmptyMessageDelayed(123123, 1500);// 支持继续扫描,延迟1500毫秒是为了让摄像头准备，避免摄像头还没有准备好就开始自动对焦
				}
			}, "", null, "", null);

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 100 && resultCode == Activity.RESULT_OK){
			getOrderInfo(resultString);
		}
	}

	/**
	 * 检查扫码结果是否正确（判断扫描内容是否来自OASIS）
	 * @param result
	 * @return
	 */
	private String checkResult(String result){
		if(TextUtils.isEmpty(result))
			return "";
		String data = "";
		try {
//			result = new String(Base64.decode(result, Base64.DEFAULT));
			JSONObject o = new JSONObject(result);
			if("OASPAY".equals(o.getString("Mark"))){
				data = o.getString("data");
			}
		}catch (Exception e){}
		return data;
	}
	private void getOrderInfo(String result){
		setWaitScreen(true);
		HttpService.instance().getOrderInfoByQR(result, new GetOrderInfoCallback(this));
	}
	class GetOrderInfoCallback implements CallbackResultForActivity{
		Activity activity;
		public GetOrderInfoCallback(Activity activity){
			this.activity = activity;
		}
		@Override
		public void success(Object data, int statusCode, String msg) {
			setWaitScreen(false);
			OrderInfo info = (OrderInfo)data;
			if(!"1".equals(info.order_status)){// 订单已被删除
				BasesUtils.showDialogBySystemUI(activity, getResources().getString(R.string.order_list_item_label9), getResources().getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.sendEmptyMessageDelayed(123123, 1500);// 支持继续扫描,延迟1500毫秒是为了让摄像头准备，避免摄像头还没有准备好就开始自动对焦
						dialog.cancel();
					}
				}, "", null, "", null);
				return;
			}
			startActivity(new Intent().setClass(activity, ActivityOrderDetails.class).putExtra("orderinfo", info));
			activity.finish();
		}

		@Override
		public void fail(int statusCode, String msg) {
			setWaitScreen(false);
			if(!TextUtils.isEmpty(msg) && "-16".equals(msg)){
				BasesUtils.showDialogBySystemUI(activity, getResources().getString(R.string.capture_scan_text4), getString(R.string.search_title_sub1), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.sendEmptyMessageDelayed(123123, 1500);// 支持继续扫描,延迟1500毫秒是为了让摄像头准备，避免摄像头还没有准备好就开始自动对焦
						dialog.cancel();
					}
				}, getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						startActivityForResult(new Intent().setClass(activity, ActivityLogin.class), 100);
					}
				}, "", null);
				return;
			}
			if(!TextUtils.isEmpty(msg) && "-22".equals(msg)){// 不是google订单
				BasesUtils.showDialogBySystemUI(activity, getResources().getString(R.string.capture_scan_text7), "", null, getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.sendEmptyMessageDelayed(123123, 1500);// 支持继续扫描,延迟1500毫秒是为了让摄像头准备，避免摄像头还没有准备好就开始自动对焦
						dialog.cancel();
					}
				}, "", null);
				return;
			}
			if(!TextUtils.isEmpty(msg) && "-23".equals(msg)){// 订单不存在
				BasesUtils.showDialogBySystemUI(activity, getResources().getString(R.string.order_list_item_label9),  "", null, getString(R.string.search_title_sub2), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.sendEmptyMessageDelayed(123123, 1500);// 支持继续扫描,延迟1500毫秒是为了让摄像头准备，避免摄像头还没有准备好就开始自动对焦
						dialog.cancel();
					}
				},"", null);
				return;
			}
			APPUtils.showErrorMessageByErrorCode(activity, "-2000");
			handler.sendEmptyMessageDelayed(123123, 1500);// 支持继续扫描,延迟1500毫秒是为了让摄像头准备，避免摄像头还没有准备好就开始自动对焦
		}

		@Override
		public void exception(Exception e) {
			setWaitScreen(false);
			APPUtils.showErrorMessageByErrorCode(activity, "-2000");
			handler.sendEmptyMessageDelayed(123123, 1500);// 支持继续扫描,延迟1500毫秒是为了让摄像头准备，避免摄像头还没有准备好就开始自动对焦
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(BasesUtils.getResourceValue("raw", "captrue_beep"));
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}