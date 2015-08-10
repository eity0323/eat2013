package com.gae.basic;


import com.gae.eat2013.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class ShoppingCarService extends Service {

	WindowManager wm = null;
	WindowManager.LayoutParams wmParams = null;
	View view;
	private ImageView iview;
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	private boolean showed = false;
	private long time = 0;
	private Vibrator mVibrator01;  //声明一个振动器对象  
	private boolean dragable = false;
	
	//自定义的BINDER 让service有数据可以共享与传递
	public class MyBinder extends Binder{
		public WindowManager getWm() {
			return wm;
		}		
		public View getView(){
			return view;
		}
		public WindowManager.LayoutParams getWmParams() {
			return wmParams;
		}
	}	
	
	@Override
	public void onCreate() {
		super.onCreate();
		view = LayoutInflater.from(this).inflate(R.layout.float_shopping_car, null);
		iview=(ImageView)view.findViewById(R.id.float_shoppingcar);
		createView();
	}

	private void createView() {
		// 获取WindowManager
		wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		// 设置LayoutParams(全局变量）相关参数
		wmParams =  ((MyApplication) getApplication()).getMywmParams();
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;// 该类型提供与用户交互，置于所有应用程序上方，但是在状态栏后面
		wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 不接受任何按键事件
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始值
		wmParams.x = 0;
		wmParams.y = 320;
		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = PixelFormat.RGBA_8888;

		wm.addView(view, wmParams);

		iview.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(!dragable)	return false;
				
				// 获取相对屏幕的坐标，即以屏幕左上角为原点
				x = event.getRawX();
				// 25是系统状态栏的高度,也可以通过方法得到准确的值，自己微调就是了
				y = event.getRawY()-25 ; 

				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						// 获取相对View的坐标，即以此View左上角为原点
						mTouchStartX = event.getX();
						mTouchStartY = event.getY();//+view.getHeight()/2;
						break;
					case MotionEvent.ACTION_MOVE:
						updateViewPosition();
						break;
					case MotionEvent.ACTION_UP:
						updateViewPosition();
						mTouchStartX = mTouchStartY = 0;
						time = 0;
						dragable = false;
						break;
				}
				return true;
			}

		});
		
		iview.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				mVibrator01 = ( Vibrator ) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE); 
				mVibrator01.vibrate( new long[]{80, 200, 40, 300},-1);
				dragable = true;
				return false;
			}
		});
		
		iview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showFloatPanel();
			}
		});
	}

	private void showFloatPanel(){		
		if(dragable) return;
		
		//显示购物车
		Intent intent = new Intent();//创建Intent对象  
		intent.setAction("com.gae.service.scar");    
		sendBroadcast(intent);//发送广播
		
		//改变控件的状态
		wm.updateViewLayout(view, wmParams);	
	}
	
	private void updateViewPosition() {
		// 更新浮动窗口位置参数
		wmParams.x = (int) (x - mTouchStartX) - 50;
		wmParams.y = (int) (y - mTouchStartY);// + 25;
		wm.updateViewLayout(view, wmParams);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override 
    public void onDestroy() { 
//        Log.i("-------------", "topfloatservice onDestroy"); 
        wm.removeView(view);
        super.onDestroy(); 
    } 
}
