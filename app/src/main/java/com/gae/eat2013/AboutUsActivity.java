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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);

        initViews();
	}

    private void  initViews(){
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

        initEvents();
    }

    private void initEvents(){
        //点击返回事件
        findViewById(R.id.foodback).setOnClickListener(clickListener);

        //客服热线
        findViewById(R.id.servicetel).setOnClickListener(clickListener);

        //加盟热线
        findViewById(R.id.hottel).setOnClickListener(clickListener);
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.foodback:
                    finish();
                    break;
                case R.id.servicetel:
                    diagPhone("4000031368");
                    break;
                case R.id.hottel:
                    diagPhone("075525320450");
                    break;
            }
        }
    };

    /*拨打电话*/
    private void diagPhone(String tel){
        Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+tel));
        startActivity(intent);
    }
}
