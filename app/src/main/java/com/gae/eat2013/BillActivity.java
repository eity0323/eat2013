package com.gae.eat2013;
/*
 * author:小容
 * version:2013-5-24
 * description:账单
 * */

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gae.UInterface.IBillAction;
import com.gae.dbHelper.dbHelper;
import com.gae.entity.BillItem;
import com.gae.entity.EatParams;
import com.gae.presenter.BillPresenter;

import java.util.ArrayList;
import java.util.List;

public class BillActivity extends Activity implements IBillAction{
	private String areaDbname = "";		  //城市数据库名称
	private String url_class = "";	      //请求数据路径
	private TextView summonney = null;    //总消费
	private TextView commonney = null;    //总充值
	private TextView savmonney = null;    //结余
	private ListView billListview = null; //帐单列表
	private String result = "";        	  //返回结果
	private String errorStr = "";		  //请求数据返回错误信息
	private String cz_count = "0"; 		  //总充值
	private String xh_count = "0"; 		  //总消费
	private String jr_count = "0"; 		  //结余
	private Button backbtn = null;        //返回
	private TextView mbtv = null;         //帐号
	private TextView pwdtv = null;        //密码
	private CheckBox keeppwd = null;      //保存密码
	private Button logbtn = null;         //登录
	private Dialog loginDialog = null;    //对话框	
	private dbHelper db= null; 			  //数据库处理对象
	private Cursor myCursor= null; 		  //游标
	private String uid=""; 				  //登陆用户ID
	private String usermb = "";           //用户手机
	private String useraddr = "";         //用户地址
	private String username = "";		  //用户
	private String sid = "";			  //事务id
	private List<BillItem> billlist=null; //个人账单数据源

    private BillPresenter helper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bill_activity);

        helper = new BillPresenter(this);
		
		//获取城市数据库名称
		areaDbname=EatParams.getInstance().areaDbName;
		//获取事务id
		sid = EatParams.getInstance().getSession();
		//个人账单数据源
		billlist=new ArrayList<BillItem>();

        initView();
	}

    private void initView(){
        summonney=(TextView)findViewById(R.id.sunmonney);
        commonney=(TextView)findViewById(R.id.commonney);
        savmonney=(TextView)findViewById(R.id.savmonney);

        //点击进入该月账单
        billListview=(ListView)findViewById(R.id.billList);
        billListview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent();
                int ind = billlist.size()-1-position;
                intent.putExtra("month", billlist.get(ind).getTime());
                intent.setClass(BillActivity.this, MonthBillActivity.class);
                startActivity(intent);
            }

        });
        //返回按钮
        backbtn = (Button)findViewById(R.id.billback);
        backbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(EatParams.getInstance().getSession().equals("")){
            Toast.makeText(BillActivity.this, "未登录", Toast.LENGTH_SHORT).show();
            go2LoginActivity();
        }else{
            getMonneyData();
        }
    }
	
	//获取个人余额和待返金额及账单数据
	public void getMonneyData(){

	}

    private void go2LoginActivity(){

    }
}
