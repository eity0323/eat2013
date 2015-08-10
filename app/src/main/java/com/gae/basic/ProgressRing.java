package com.gae.basic;
/*
 * author:eity
 * version:2013-3-1
 * description:进度条
 * */
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

public class ProgressRing{
	private static ProgressDialog pd;
	static Dialog d;
	static Thread thread;
	
	//显示进度条
	public static void onProgeress(Context context, String title, String msg, long time){
		if(time<5000){
			time=10000;
		}
		final long t=time;
		pd = ProgressDialog.show(context, title, msg, true, false);
		Runnable runnable = new Runnable() {
			
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					thread.sleep(t);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				unProgeress();
			}
		};
		thread = new Thread(runnable);
        thread.start();
	}
	
	public static void onProgeress(Context context,String title,String msg){
		pd = ProgressDialog.show(context, title, msg, true, false);
	}
	//隐藏进度条
	public static void unProgeress(){
//		thread.stop();
		if(pd != null)pd.dismiss();
	}
}