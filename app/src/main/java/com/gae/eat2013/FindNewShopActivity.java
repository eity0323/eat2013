package com.gae.eat2013;

import com.gae.entity.EatParams;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FindNewShopActivity extends Activity{
	private Button back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.findnewshop_activity);		
		
		//返回
		back=(Button)findViewById(R.id.recommedback);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}


}
