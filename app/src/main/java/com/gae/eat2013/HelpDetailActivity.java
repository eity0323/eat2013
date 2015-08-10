package com.gae.eat2013;
/**
 * @author:eity
 * @version:2013.06.03
 * @description:具体帮助页内容
 * */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HelpDetailActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_detail_activity);
		
		//获取具体帮助名称
		Intent intent = getIntent();
		String str = intent.getStringExtra("aname");
		
		//标题
		TextView tv = (TextView)findViewById(R.id.help_detail_title);
		tv.setText(str);
		
		//返回按钮
		Button btn = (Button)findViewById(R.id.help_detail_back);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
