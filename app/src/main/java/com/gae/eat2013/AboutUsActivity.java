package com.gae.eat2013;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/*
 * author:小巧
 * version:2013-5-20
 * description:关于我们
 * */

public class AboutUsActivity extends Activity{
	private TextView foodback = null,servicetelbtn = null,hottelbtn = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		//点击返回事件
		foodback = (TextView)findViewById(R.id.foodback);
		foodback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			   	AboutUsActivity.this.finish();
			}
		});
		
		//客服热线
		servicetelbtn = (TextView)findViewById(R.id.servicetel);
		servicetelbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String tel = "4000031368";
			    Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+tel));	
			    startActivity(intent);
			}
		});
		
		//加盟热线
		hottelbtn = (TextView)findViewById(R.id.hottel);
		hottelbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String tel = "075525320450";
			    Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+tel));			 	
			    startActivity(intent);
			}
		});
		
		//获取版本信息
		PackageManager packageManager = getPackageManager();
        PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(),0);
			String version = packInfo.versionName;
			TextView tvTextView = (TextView)findViewById(R.id.version);
			tvTextView.setText("版本:"+version);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}		
	}
}
