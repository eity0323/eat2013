package com.gae.eat2013;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class PetHelpActivity extends Activity {
	
	private ListView list;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pet_help);
		
		Button reBtn = (Button)findViewById(R.id.petback);
		reBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		list = (ListView)findViewById(R.id.petHelpList);
		String[] strs = {"领养宠物","宠物成长","游戏基本操作","逗宠物玩","分享和反馈"};  
        //生成动态数组，加入数据  
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
        for(int i=0;i<strs.length;i++)  
        {  
            HashMap<String, Object> map = new HashMap<String, Object>();             
            map.put("ItemTitle", strs[i]);  
            map.put("ItemText", "Finished in 1 Min 54 Secs, 70 Moves! ");  
            listItem.add(map);  
        }  
        //生成适配器的Item和动态数组对应的元素  
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//数据源   
            R.layout.pet_share_item,//ListItem的XML实现  
            //动态数组与ImageItem对应的子项          
            new String[] {"ItemTitle"},   
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
            new int[] {R.id.pet_share_txt}  
        );  
         
        //添加并且显示  
        list.setAdapter(listItemAdapter);  
          
        //添加点击  
        list.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                    long arg3) {  
                Log.i("----------", "点击第"+arg2+"个项目");
            }  
        });  
	}
}
