package com.gae.service;
/**
 * author:eity
 * version:2013-05-03
 * description:宠物功能菜单
 */


import com.gae.eat2013.PetActivity;
import com.gae.eat2013.R;
import com.gae.entity.EatParams;
import com.gae.basic.MyApplication;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class PetMenuService extends Service {

	private WindowManager wm = null;	//弹出窗口管理对象
	private WindowManager.LayoutParams wmParams = null;//弹出窗口参数
	private View view;					//子布局对象
	private float mTouchStartX;			//起始x坐标
	private float mTouchStartY;			//起始y坐标
	private float x;					//当前x坐标
	private float y;					//当前y坐标

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
		view = LayoutInflater.from(this).inflate(R.layout.float_menu, null);
		
		//打开起始页面
		Button start_btn = (Button) view.findViewById(R.id.f_menu_start);
		start_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {				
				Intent pintent = new Intent();
				pintent.setClass(getApplicationContext(), PetActivity.class);
				pintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
				pintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(pintent);
			}
		});

		//退出宠物
		Button cancel = (Button) view.findViewById(R.id.f_menu_exit);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				closePetMenu("exitPet");
			}
		});
				
		//喂食
		Button bfood = (Button) view.findViewById(R.id.btn_f_feed);
		bfood.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				petAction("feed");
			}
		});
		
		//洗澡
		Button bnear = (Button)view.findViewById(R.id.btn_f_bath);
		bnear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				petAction("bath");
			}
		});
		
		//饥饿
		Button bstore = (Button)view.findViewById(R.id.btn_f_hungry);
		bstore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				petAction("hungry");
			}
		});
		
		//孤独
		Button bmove = (Button)view.findViewById(R.id.btn_f_lonely);
		bmove.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				petAction("lonely");
			}
		});
		createView();

		//宠物状态
		EatParams.getInstance().setPetMenuStatus(1);
	}
	
	//退出宠物
	private void closePetMenu(String tag){		
		Intent intent = new Intent();//创建Intent对象  
		intent.putExtra("msg", tag);
		intent.setAction("com.gae.service.petInfo");   //发送给petActivity
		sendBroadcast(intent);//发送广播
	}
	
	//宠物动作
	private void petAction(String tag){
		Intent intent = new Intent();//创建Intent对象  
		intent.putExtra("msg", tag);
		intent.setAction("com.gae.service.petAction");   //发送给petService
		sendBroadcast(intent);//发送广播
	}

	//初始化宠物界面
	private void createView() {
		// 获取WindowManager
		wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		// 设置LayoutParams(全局变量）相关参数
		wmParams =  ((MyApplication) getApplication()).getMywmParams();
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;// 该类型提供与用户交互，置于所有应用程序上方，但是在状态栏后面
		wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 不接受任何按键事件
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始值
		wmParams.x = 20;
		int ty = EatParams.getInstance().getScreenHeight() - 200;
		int tpy = (int)EatParams.getInstance().getPetY();
		if(ty > tpy + 150){
			ty = tpy + 150;
		}
		wmParams.y = ty;
		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = PixelFormat.RGBA_8888;

		wm.addView(view, wmParams);

	}
	
	//更新宠物位置
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
        wm.removeView(view);
        super.onDestroy(); 
    } 
}
