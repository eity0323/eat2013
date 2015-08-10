package com.gae.basic;
/*
 * author:eity
 * version:2013-3-1
 * description:当前应用，用于更新版本
 * */


import com.gae.entity.EatParams;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.WindowManager;

public class MyApplication extends Application {
	

	public static int localVersion = 0;// 本地安装版本

	public static String downloadDir = "eat2013/";// 安装目录

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			PackageInfo packageInfo = getApplicationContext()
					.getPackageManager().getPackageInfo(getPackageName(), 0);
			localVersion = packageInfo.versionCode;
			downloadDir = EatParams.getInstance().getDownloadDir();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	 private Activity myActivity;
     
     public void setInstance(Activity instance){
             myActivity = instance;
     }
     
     public Activity getInstance(){
             return myActivity;
     }
     
     /**
 	 * 创建全局变量 全局变量一般都比较倾向于创建一个单独的数据类文件，并使用static静态变量
 	 * 这里使用了在Application中添加数据的方法实现全局变量
 	 * 注意在AndroidManifest.xml中的Application节点添加android:name=".MyApplication"属性
 	 */
 	private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

 	public WindowManager.LayoutParams getMywmParams() {
 		return wmParams;
 	}

}
