package com.gae.eat2013;
/**
 * @author:eity
 * @version:2013.06.03
 * @description:午餐指南页
 * */
import java.util.ArrayList;
import java.util.HashMap;

import com.gae.dbHelper.ApplydbHelper;
import com.gae.entity.EatParams;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class GuideLunchActivity extends Activity{
	
	private ListView guideList = null;							//午餐指南列表
	private String dbname = "";									//数据库名称
	private ArrayList<HashMap<String, Object>> listItem=null;	//午餐指南列表数据源
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guidelunch_activity);		
		
		dbname = EatParams.getInstance().getSDdbname();
	  	
		//返回
		Button back = (Button)findViewById(R.id.guidelunchback);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		guideList = (ListView)findViewById(R.id.guide_lunch);
		
		//获取应用数据
        listItem = new ArrayList<HashMap<String, Object>>();  
		
		ApplydbHelper adb = new ApplydbHelper(getApplicationContext());
		adb.open(dbname);
		Cursor cor = adb.getAllApplys();
		while(cor.moveToNext()){
			HashMap<String, Object> map = new HashMap<String, Object>();             
            map.put("ItemTitle", cor.getString(cor.getColumnIndex(adb.APPLY_NAME)));  
            map.put("ItemText", cor.getString(cor.getColumnIndex(adb.APPLY_HELP)));  
            listItem.add(map); 
		}
		cor.close();
		adb.close();
		
        //生成适配器的Item和动态数组对应的元素  
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//数据源   
            R.layout.pet_share_item,//ListItem的XML实现  
            //动态数组与ImageItem对应的子项          
            new String[] {"ItemTitle"},   
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
            new int[] {R.id.pet_share_txt}  
        ); 
        
        guideList.setAdapter(listItemAdapter);
        guideList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), HelpDetailActivity.class);
				intent.putExtra("aname", listItem.get(arg2).get("ItemTitle").toString() );
				startActivity(intent);
			}
		});
	}


}
