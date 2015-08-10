package com.gae.eat2013;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gae.control.UpdateHitsHot;
/**
 * 发布妙招分享
 * @author Administrator
 *
 */
public class IdeaDetailActivity extends Activity {
	private Button btnback = null;
	private TextView etitle = null, econtent = null,edituser = null;
	private String user = "";					//用户
	private UpdateHitsHot updateHitsHot;
	private String good = "",bad = "",id="";
	private TextView goodtv,badtv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.idea_detail_activity); //初始化界面 
		
		etitle = (TextView) findViewById(R.id.edittitle);
		econtent = (TextView) findViewById(R.id.editcontent);  //初始化编辑内容框

		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		String content = intent.getStringExtra("content");
		good = intent.getStringExtra("good");
		bad = intent.getStringExtra("bad");
		id = intent.getStringExtra("id");
		user = intent.getStringExtra("user");
		etitle.setText(title);
		econtent.setText(content);
		
		edituser = (TextView)findViewById(R.id.edituser);
		edituser.setText(user);
		
		// 点击返回
		btnback = (Button) findViewById(R.id.btnback);
		btnback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		goodtv = (TextView)findViewById(R.id.dingperson);
		goodtv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateHitsHot = new UpdateHitsHot(IdeaDetailActivity.this);
				updateHitsHot.updateNum(id,"G");
			    int num=Integer.valueOf(good)+1;
			    goodtv.setText("顶："+ num );
				
			}
		});
		goodtv.setText("顶："+ good );
		
		badtv = (TextView)findViewById(R.id.caiperson);
		badtv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateHitsHot = new UpdateHitsHot(IdeaDetailActivity.this);
				updateHitsHot.updateNum(id,"D");
				int badnum = Integer.valueOf(bad)+1;
				badtv.setText("踩："+ badnum);
			}
		});
		badtv.setText("踩："+ bad);
		
	}
}
