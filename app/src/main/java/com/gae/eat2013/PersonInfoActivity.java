package com.gae.eat2013;

import com.gae.listener.OnChangedListener;
import com.gae.view.SlipButtonSex;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class PersonInfoActivity extends Activity {

	private SlipButtonSex sexslip = null;
	private String sex="男";                            //性别
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personinfo_activity);
		
		TextView back = (TextView)findViewById(R.id.pinfoback);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		sexslip=(SlipButtonSex)findViewById(R.id.slipbtnsex);
		//性别选择
		sexslip.setChecked(true);
		sexslip.SetOnChangedListener("changeat", new OnChangedListener() {

			@Override
			public void OnChanged(String strName, boolean CheckState) {
				if (CheckState) {
					sex="男";
					
				} else {
					sex="女";
				}
			}
		});	
	}
}
