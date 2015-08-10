package com.gae.eat2013;
/**
 * @author eity
 * @version 2013-06-03
 * @description 宠物设置信息
 * */
import com.gae.dbHelper.petSDCardHelper;
import com.gae.listener.OnChangedListener;
import com.gae.view.SlipButton;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class PetSetActivity extends Activity {
	private SlipButton sbtn1;	//是否禁用宠物
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pet_set);
		
		//返回
		Button reback = (Button)findViewById(R.id.petback);
		reback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		//获取宠物状态
		boolean petopen = true;
		String petstate = "Y";
		petSDCardHelper pdb = new petSDCardHelper(getApplicationContext());
		pdb.open();
		Cursor cursor = pdb.select();
		while(cursor.moveToNext()){
			petstate = cursor.getString( cursor.getColumnIndex(petSDCardHelper.FIELD_CLOSED) );
		}
		if(petstate.equals("N")){
			petopen = false;
		}else{
			petopen = true;
		}
		cursor.close();
		pdb.close();
		
		//禁用宠物
		sbtn1 = (SlipButton)findViewById(R.id.pet_usable);
		sbtn1.setChecked(petopen);
		sbtn1.SetOnChangedListener("pusable", new OnChangedListener() {
			
			@Override
			public void OnChanged(String strName, boolean CheckState) {
				forbidPet(CheckState);
			}
		});
		
		LinearLayout cityLayout = (LinearLayout)findViewById(R.id.pet_city_layout);
		cityLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent itent = new Intent();
				itent.setClass(getApplicationContext(), WeatherActivity.class);
				startActivity(itent);
			}
		});
	}
	
	//禁用宠物
	private void forbidPet(boolean state) {
		if (!state) {
			final boolean cstate = state;
			AlertDialog.Builder builder = new Builder(PetSetActivity.this);
			builder.setMessage("亲，你真的不想再看到我了吗？");
			builder.setTitle("提示");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					updatePet(cstate);
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					sbtn1.setChecked(!cstate);
					dialog.dismiss();
					sbtn1.invalidate();
				}
			});
			builder.create().show();
		}else{
			updatePet(state);
		}
	}
	
	//更新数据库
	private void updatePet(boolean state){
		String petshow;
		if(state){
			petshow = "Y";
		}else{
			petshow = "N";
		}
		petSDCardHelper pdb = new petSDCardHelper(getApplicationContext());
		pdb.open();
		ContentValues cv = new ContentValues();
		cv.put(petSDCardHelper.FIELD_CLOSED, petshow);
		pdb.update(cv, "1");
		pdb.close();
	}
}
