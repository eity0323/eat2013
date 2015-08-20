package com.gae.eat2013;
/*
 * author:小容
 * version:2013-5-24
 * description:发布星级菜谱
 * */

import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gae.UInterface.IAddCookAction;
import com.gae.entity.CookItem;
import com.gae.entity.EatParams;
import com.gae.presenter.AddCookPresenter;

public class AddCookActivity extends Activity implements IAddCookAction {
	private String urlServer = "";				//服务器路径
	private String areaDbname = "";				//城市数据库名称
	private String url_class = "";				//请求数据路径
	private String result = "";					//请求数据返回标识
	private String errorStr = "";				//请求数据返回错误信息
	private String sid = "";					//事务id
	private ArrayList<CookItem> cooklist = null;//星级菜谱数据源
	
	private Button back;
	private Button add;
	private TextView txt1; 
	private TextView txt2;
	private TextView txt3; 
	private TextView txt4;
	private TextView txt5; 
	private TextView txt6;
	private TextView txt7; 
	private TextView txt8;

    private AddCookPresenter helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcook_activity);

        helper = new AddCookPresenter(this);
		
		urlServer = EatParams.getInstance().getUrlServer();
		areaDbname = EatParams.getInstance().areaDbName;
		sid = EatParams.getInstance().getSession();
		cooklist = new ArrayList<CookItem>();
		
		txt1 = (TextView)findViewById(R.id.cname);
		txt2 = (TextView)findViewById(R.id.method);
		txt3 = (TextView)findViewById(R.id.taste);
		txt4 = (TextView)findViewById(R.id.diffic);
		txt5 = (TextView)findViewById(R.id.time);
		txt6 = (TextView)findViewById(R.id.main);
		txt7 = (TextView)findViewById(R.id.sub);
		txt8 = (TextView)findViewById(R.id.step);

		//返回按钮事件
		back = (Button)findViewById(R.id.backbtn);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		//发布菜谱按钮事件
		add = (Button)findViewById(R.id.sure);
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addcooklist();
			}
		});
		
	}
	//新增菜谱
	private void addcooklist(){
		//发布菜谱的接口
		//argv={webdm,-db,eat,-sid,sid,-ADD-BCMENU,vname,material,amaterial,mark,taste,diffic,utime,detail,ico,dico}
		String vname = txt1.getText().toString();
		String main = txt6.getText().toString();
		String sub = txt7.getText().toString();
		String method = txt2.getText().toString();
		String taste = txt3.getText().toString();
		String diffic = txt4.getText().toString();
		String time = txt5.getText().toString();
		String detail = txt8.getText().toString();
		String ico = "";
		String dico = "";
		
		if(TextUtils.isEmpty(vname)){
			Toast.makeText(AddCookActivity.this, "菜名不能为空！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(TextUtils.isEmpty(method)){
			Toast.makeText(AddCookActivity.this, "做法不能为空！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(TextUtils.isEmpty(taste)){
			Toast.makeText(AddCookActivity.this, "口味不能为空！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(TextUtils.isEmpty(diffic)){
			Toast.makeText(AddCookActivity.this, "难度不能为空！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(TextUtils.isEmpty(time)){
			Toast.makeText(AddCookActivity.this, "时间不能为空！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(TextUtils.isEmpty(main)){
			Toast.makeText(AddCookActivity.this, "主料不能为空！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(TextUtils.isEmpty(sub)){
			Toast.makeText(AddCookActivity.this, "辅料不能为空！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(TextUtils.isEmpty(detail)){
			Toast.makeText(AddCookActivity.this, "步骤不能为空！", Toast.LENGTH_SHORT).show();
			return;	
		}
		
		url_class = urlServer+ "?argv={webdm,-DB,"+areaDbname+",-sid"+sid+",-ADD-BCMENU,"
					+URLEncoder.encode(vname)+"','"
					+URLEncoder.encode(main)+"','"
					+URLEncoder.encode(sub)+"','"
					+URLEncoder.encode(method)+"','"
					+URLEncoder.encode(taste)+"','"
					+URLEncoder.encode(diffic)+"','"
					+URLEncoder.encode(time)+"','"
					+URLEncoder.encode(detail)+"','"
					+URLEncoder.encode(ico)+"','"
					+URLEncoder.encode(dico)
					+"'}";

        if (helper != null)
            helper.requestCook();
	}
}
