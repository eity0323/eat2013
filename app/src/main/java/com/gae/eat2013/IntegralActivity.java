package com.gae.eat2013;
/**
 * author:小容
 * version:2013-5-24
 * description:我的积分
 * */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gae.adapter.IntegralAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class IntegralActivity extends Activity{
	private ListView integrallist = null;    //查看列表
	private Button integralback = null;      //返回
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.integral_activity);		
		
		integrallist = (ListView)findViewById(R.id.integrallist);
		//获取积分栏目数据
		List<Map<String,Object>> list = getIntegalData();
		IntegralAdapter shareadapter = new IntegralAdapter(this,list);
		integrallist.setAdapter(shareadapter);
	    
		//返回
		integralback = (Button)findViewById(R.id.integralback);
		integralback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	//选项列表
	public List<Map<String,Object>> getIntegalData(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String,Object>map = new HashMap<String, Object>();
		map.put("title", "积分明细");
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("title", "积分规则");
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("title", "积分使用");
        list.add(map);
        
        return list;
	}

}
