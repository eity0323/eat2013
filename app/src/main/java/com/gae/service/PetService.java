package com.gae.service;
/**
 * author:eity
 * version:2013-05-03
 * description:宠物
 */
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.gae.basic.MyAnimationDrawable;
import com.gae.eat2013.R;
import com.gae.entity.EatParams;
import com.gae.listener.AnimationEventListener;
import com.gae.basic.MyApplication;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
import android.widget.Button;
import android.widget.ImageView;

public class PetService extends Service {

	private WindowManager wm = null;					//弹出窗口管理对象
	private WindowManager.LayoutParams wmParams = null; //弹出窗口参数
	private View view;									//子布局容器
	private ImageView iview;							//宠物对象
	private float mTouchStartX;							//起始x坐标
	private float mTouchStartY;							//起始y坐标
	private float x;									//当前x坐标
	private float y;									//当前y坐标

	private Vibrator mVibrator01;  						//声明一个振动器对象  
	private boolean dragable = false;					//是否可拖动
	
	private MyAnimationDrawable anim;					//动画播放对象
	private ActionReceiver dataReceiver;				//宠物动作广播接收器
	private TimerTask timerTask = null;					//时间计算器
	
	private float pointx = 0,pointy = 320;				//宠物位置
	
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
		view = LayoutInflater.from(this).inflate(R.layout.float_pet, null);
		iview=(ImageView)view.findViewById(R.id.float_image);
		createView();
		
		if (timerTask != null){
			timerTask.cancel();  //将原任务从队列中移除
		}
		
		timerTask = new MyTimerTask();
		
		// new一个计时器实例 
		Timer timer = new Timer(); 
		// new一个计时器任务的实例，并重写其run方法，定义触发的动作 
		// 设定计时器的任务以及时间 
		timer.schedule(timerTask, 1000);
		
				
		EatParams.getInstance().setPetStatus(1);
		Date dt = new Date(System.currentTimeMillis());
		EatParams.getInstance().setPetLiveHour(  dt.getTime() );
		dataReceiver = new ActionReceiver();  
	    IntentFilter filter = new IntentFilter();//创建IntentFilter对象  
	    filter.addAction("com.gae.service.petAction");  
	    registerReceiver(dataReceiver, filter);//注册Broadcast Receiver  
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
						dragable = false;
						break;
				}
				return true;
			}

		});
		
		iview.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				mVibrator01 = ( Vibrator ) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE); 
				mVibrator01.vibrate( new long[]{80, 200, 40, 300},-1);
				dragable = true;

				return false;
			}
		});
		
		iview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showFloatPanel();				
			}
		});

	}
	
	//喂食
	private void actionFeed(){
		anim = new MyAnimationDrawable((AnimationDrawable) getResources().getDrawable(R.drawable.dog_feed));
		anim.setAnimationEventListener(new AnimationEventListener() {			
			@Override
			public void onAnimationEnd() {
				normalAction();
			}
		});
	    
		iview.setBackgroundDrawable(anim);
		anim.stop();
		anim.start();
	}
	
	//洗澡
	private void actionBath(){
		anim = new MyAnimationDrawable((AnimationDrawable) getResources().getDrawable(R.drawable.dog_bath));
		anim.setAnimationEventListener(new AnimationEventListener() {			
			@Override
			public void onAnimationEnd() {
				normalAction();
			}
		});
	    
		iview.setBackgroundDrawable(anim);
		anim.stop();
		anim.start();
	}
	
	//饥饿
	private void actionHungry(){		
		anim = new MyAnimationDrawable((AnimationDrawable) getResources().getDrawable(R.drawable.dog_hungry));
		anim.setAnimationEventListener(new AnimationEventListener() {			
			@Override
			public void onAnimationEnd() {
				normalAction();
			}
		});
	    
		iview.setBackgroundDrawable(anim);
		anim.stop();
		anim.start();
	}
	
	//孤独
	private void actionLonely(){
//		iview.setBackgroundResource(R.drawable.dog_lonely);
//		anim = null;
//		Object ob = iview.getBackground();
//		anim = (AnimationDrawable) ob;
//		anim.stop();
//		anim.start();
		anim = new MyAnimationDrawable((AnimationDrawable) getResources().getDrawable(R.drawable.dog_lonely));
		anim.setAnimationEventListener(new AnimationEventListener() {			
			@Override
			public void onAnimationEnd() {
				normalAction();
			}
		});
	    
		iview.setBackgroundDrawable(anim);
		anim.stop();
		anim.start();
	}

	//点击功能菜单窗口
	private void showFloatPanel(){		
		Intent intent = new Intent();//创建Intent对象  
		intent.putExtra("msg", "openPetMenu");
		intent.putExtra("px", pointx);
		intent.putExtra("py", pointy);
		intent.setAction("com.gae.service.petInfo");    
		sendBroadcast(intent);//发送广播
	}
	
	//更新宠物位置
	private void updateViewPosition() {
		// 更新浮动窗口位置参数
		wmParams.x = (int) (x - mTouchStartX) - 50;
		wmParams.y = (int) (y - mTouchStartY);// + 25;
		
		pointx = (int) (x - mTouchStartX) - 50;
		pointy = (int) (y - mTouchStartY);// + 25;
		wm.updateViewLayout(view, wmParams);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	//初始动画
	private void normalAction(){
		iview.setBackgroundResource(R.drawable.dog_normal );
		Object ob = iview.getBackground();
		AnimationDrawable normalAnimation = (AnimationDrawable) ob;
		normalAnimation.stop();
		normalAnimation.start();
	}
	
	@Override 
    public void onDestroy() { 
        wm.removeView(view);
        unregisterReceiver(dataReceiver);
        super.onDestroy(); 
    } 
	
	//延迟触发事件
	private Handler handler = new Handler() { 
	    // 定义处理信息的方法 
	    public void handleMessage(Message msg) { 
	        switch (msg.what) { 
		        case 1: 
		        	normalAction();
		            break; 
	        } 
	        super.handleMessage(msg); 
	    } 
	}; 
	 
	//延迟启动初始动画
	class MyTimerTask extends TimerTask{ 
	    @Override 
	    public void run() { 
	        Message message = new Message(); 
	        message.what = 1; 
	        handler.sendMessage(message); 
	    } 
	}; 

	//宠物动作广播接收器
	public class ActionReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			//显示提示内容
			String msg = intent.getStringExtra("msg");
			if(msg.equals("bath")){
				actionBath();
			}else if(msg.equals("lonely")){
				actionLonely();
			}else if(msg.equals("hungry")){
				actionHungry();
			}else if(msg.equals("feed")){
				actionFeed();
			}
		}
		
		public ActionReceiver(){
			
		}
	}
}
