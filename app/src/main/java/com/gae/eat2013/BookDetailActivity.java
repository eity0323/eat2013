package com.gae.eat2013;
/*
 * author:小容
 * version:2013-5-24
 * description:预定下单详情
 * */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gae.UInterface.IBookDetailAction;
import com.gae.presenter.BookDetailPresenter;

//预定下单详情
public class BookDetailActivity extends Activity implements IBookDetailAction{

	private TextView textmb;
	private TextView textprice;
	private TextView textinfo;
	private TextView textaddr;
	private TextView texttimes;
	private TextView textawnm;
	private Button btndetail;

    private BookDetailPresenter helper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookdetail);
		
		textmb = (TextView)findViewById(R.id.usermb);
		textprice = (TextView)findViewById(R.id.userprice);
		textinfo = (TextView)findViewById(R.id.userfood);
		textaddr = (TextView)findViewById(R.id.useraddr);
		texttimes = (TextView)findViewById(R.id.usertime);
		textawnm = (TextView)findViewById(R.id.anwered);
		btndetail = (Button)findViewById(R.id.detailback);
		//返回按钮
		btndetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		//获得参数并显示
		Intent intent = getIntent();
		String time = intent.getStringExtra("time");
		String addr = intent.getStringExtra("addr");
		String infoname = intent.getStringExtra("infoname");
		String mb = intent.getStringExtra("mb");
		String price = intent.getStringExtra("price");
		String awnm = intent.getStringExtra("awnm");
		
		textmb.setText(mb);
		textaddr.setText(addr);
		textinfo.setText(infoname);
		textprice.setText(price);
		texttimes.setText(time);
		textawnm.setText(awnm);
	}

}
