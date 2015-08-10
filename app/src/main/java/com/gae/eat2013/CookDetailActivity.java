package com.gae.eat2013;
/*
 * author:小容
 * version:2013-5-24
 * description:星级菜谱详细
 * */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CookDetailActivity extends Activity {
	private TextView txt1;
	private TextView txt2;
	private TextView txt3;
	private TextView txt4;
	private TextView txt5;
	private TextView txt6;
	private TextView txt7;
	private TextView txt8;
	private TextView txt9;
	private TextView txt10;
	private TextView txt11;
	private ImageView img1;
	private ImageView img2;
	private Button back;
	private Button comment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cookdetail_activity);
		
		//返回
		back = (Button)findViewById(R.id.backbtn);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		txt1 = (TextView)findViewById(R.id.mark);
		txt2 = (TextView)findViewById(R.id.taste);
		txt3 = (TextView)findViewById(R.id.diffic);
		txt4 = (TextView)findViewById(R.id.time);
		txt5 = (TextView)findViewById(R.id.uname);
		txt6 = (TextView)findViewById(R.id.up);
		txt7 = (TextView)findViewById(R.id.down);
		txt8 = (TextView)findViewById(R.id.main);
		txt9 = (TextView)findViewById(R.id.sub);
		txt10 = (TextView)findViewById(R.id.step);
		txt11 = (TextView)findViewById(R.id.cname);
		img1 = (ImageView)findViewById(R.id.ico);
		//img2 = (ImageView)findViewById(R.id.dico);
		//显示信息
		Intent in = getIntent();
		txt1.setText(in.getStringExtra("1"));
		txt2.setText(in.getStringExtra("2"));
		txt3.setText(in.getStringExtra("3"));
		txt4.setText(in.getStringExtra("4"));
		txt5.setText(in.getStringExtra("5"));
		txt6.setText(in.getStringExtra("6") + "人");
		txt7.setText(in.getStringExtra("7") + "人");
		txt8.setText(in.getStringExtra("8"));
		txt9.setText(in.getStringExtra("9"));
		txt10.setText(in.getStringExtra("10"));
		txt11.setText(in.getStringExtra("11"));
	}

}
