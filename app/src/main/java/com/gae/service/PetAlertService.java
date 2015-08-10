package com.gae.service;
/**
 * author:eity
 * version:2013-05-03
 * description:宠物提示服务
 */

import java.util.ArrayList;

import com.gae.basic.MyApplication;
import com.gae.eat2013.R;
import com.gae.entity.EatParams;
import com.gae.entity.PetAlertInfo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

public class PetAlertService extends Service {

	private WindowManager wm = null;	//弹出窗口管理对象
	private WindowManager.LayoutParams wmParams = null;//弹出窗口参数
	private View view;					//子布局对象
	
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
		
		EatParams pins = EatParams.getInstance();
		
		view = LayoutInflater.from(this).inflate(R.layout.pet_alert_info, null);
		
		String ptype = pins.getPetAlertType();
		int [] pindexs = pins.getPerAlertIndex();
		ArrayList<PetAlertInfo> idata;
		int index = 0;
		String stxt;
		if(ptype.equals( PetAlertInfo.TYPE_FUCTION ) ){
			index = pindexs[0];
			idata = pins.getfAlertList();			
			stxt = idata.get( index ).getContent();
			int rind = index + 1;
			pins.setPetAlertType(pins.getPetLastAlertType());
			pindexs[0] = rind;
		}else if(ptype.equals( PetAlertInfo.TYPE_TIMED )){
			index = pindexs[1];
			idata = pins.gettAlertList();			
			stxt = idata.get( index ).getContent();			
			int rind = index + 1;
			pins.setPetAlertType(pins.getPetLastAlertType());
			pindexs[1] = rind;
		}else if(ptype.equals( PetAlertInfo.TYPE_SYSTEM)){
			index = pindexs[2];
			idata = pins.getsAlertList();	
			stxt = idata.get( index ).getContent();			
			int rind = index + 1;
			pindexs[2] = rind;
		}else if(ptype.equals( PetAlertInfo.TYPE_WEB)){
			index = pindexs[3];
			idata = pins.getwAlertList();			
			stxt = idata.get( index ).getContent();			
			int rind = index + 1;
			pindexs[3] = rind;
		}else{
			idata = null;
			index = 0;
			stxt = "主人，陪我玩玩心情就会好哦！";
		}		
		
		TextView tv = (TextView) view.findViewById(R.id.alert_txt);
		tv.setText(stxt);
		if(ptype.equals( PetAlertInfo.TYPE_TIMED )){
			idata.get( index ).setShowable("false");				
		}
		
		ImageButton ok = (ImageButton) view.findViewById(R.id.btn_ok);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {				
				closePetAlert();
			}
		});

		// 关闭宠物提示窗口
		ImageButton cancel = (ImageButton) view.findViewById(R.id.btn_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				closePetAlert();
			}
		});
		createView();
		
		EatParams.getInstance().setPetInfoStatus(1);
	}
	
	// 关闭宠物提示窗口
	private void closePetAlert(){		
		stopSelf();
	}

	//初始化宠物提示窗口
	private void createView() {
		// 获取WindowManager
		wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		// 设置LayoutParams(全局变量）相关参数
		wmParams =  ((MyApplication) getApplication()).getMywmParams();
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;// 该类型提供与用户交互，置于所有应用程序上方，但是在状态栏后面
		wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 不接受任何按键事件
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始值
		wmParams.x = 120;
		wmParams.y = 5;
		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = PixelFormat.RGBA_8888;

		wm.addView(view, wmParams);

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
