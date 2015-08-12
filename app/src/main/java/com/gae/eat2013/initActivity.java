package com.gae.eat2013;
/**
 * author:小胡
 * version:2013-5-24
 * description:欢迎界面
 * */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import com.gae.UInterface.IInitAction;
import com.gae.basic.MyApplication;
import com.gae.control.GetFoodTypItem;
import com.gae.dbHelper.dbHelper;
import com.gae.entity.EatParams;
import com.gae.presenter.InitPresenter;

import java.util.Timer;
import java.util.TimerTask;

public class initActivity extends Activity implements IInitAction {

	 private dbHelper db = null;
	 private Cursor myCursor = null;
	 private Intent mainIntent = null;
	 private String url_class = "";			//url连接 
	 private String result = "";			//获取数据返回状态
	 private String errorStr = "";			//获取数据返回错误信息
	 private long second = 0;				//毫秒数
	 private long stime = 0;				//起始时间
	 private String session = "";			//事务id
	 private int initime= 1000;				//启动页停留时间

	 private MyApplication myApplication = null;

    private InitPresenter helper;
	 
	 public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init);

         helper = new InitPresenter(this);

         init();
	}

    private void init(){
        ((MyApplication)getApplication()).setInstance(initActivity.this);

        helper.setVersionInfo();

        helper.checkVersion();
    }

    @Override
    public void setVersion(String version){
        TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
        versionNumber.setText("Version " + version);
    }

    @Override
    public void check2Page(){
        if (EatParams.isNetworkVailable(initActivity.this)) {//有网络

            Timer timer = new Timer();
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    autoLogin();
                }

            }, 3000);//表示3秒后执行  (3秒时间定位）
        }else{														//无网络
            Toast.makeText(initActivity.this, "网络有问题，请检查网络！", Toast.LENGTH_SHORT).show();

            second = System.currentTimeMillis() - stime;
            long wtime = initime - second;
            if(wtime > 0){
                waitShow(wtime);
            }else{
                waitShow(0);
            }
        }
    }

	//判断自动登录
	 public void autoLogin(){
	        db=new dbHelper(initActivity.this);
	        myCursor = db.select();
	        if(myCursor.getCount() > 0){
	        	
	        	myCursor.moveToPosition(0);
	        	
	        	String mb = myCursor.getString(myCursor.getColumnIndex(dbHelper.FIELD_MB));
	        	String pwd = myCursor.getString(myCursor.getColumnIndex(dbHelper.FIELD_PWD));

                //TODO login
                waitShow(0);
	        }else{
	        	second = System.currentTimeMillis() - stime;
				long wtime = initime - second;
				if(wtime > 0){
					waitShow(wtime);
				}else{
					waitShow(0);
				}
	        }
	        db.close();
	         
	 }
	 //延迟显示页跳转
	 public void waitShow(long time){
		 new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
              /* Create an Intent that will start the Main WordPress Activity. */
            	GetFoodTypItem foodTyp=new GetFoodTypItem(initActivity.this);
            	foodTyp.getfoodtypeData();
                Bundle bd = new Bundle();
                bd.putString("session", session);
                SharedPreferences sharedPreferences = getSharedPreferences("guidnum", Context.MODE_PRIVATE);
	            //getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
	            String guidnum = sharedPreferences.getString("guidnum","");
                if(!guidnum.equals("")&&guidnum!=null){
                	mainIntent = new Intent(initActivity.this, MainActivity.class);
                }else{               	
                	mainIntent = new Intent(initActivity.this, GuidActivity.class);
                }
                mainIntent.putExtra("attachment", bd);
                mainIntent.putExtra("source", "initActivity");
                initActivity.this.startActivity(mainIntent);
                initActivity.this.finish();
             }
         }, 100);
	 }	 

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
