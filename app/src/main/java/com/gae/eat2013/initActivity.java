package com.gae.eat2013;
/**
 * author:小胡
 * version:2013-5-24
 * description:欢迎界面
 * */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.gae.basic.GetHttp;
import com.gae.basic.MyApplication;
import com.gae.basic.UpdateService;
import com.gae.control.GetFoodTypItem;
import com.gae.dbHelper.dbHelper;
import com.gae.entity.EatParams;
import com.gae.entity.areaDbInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

public class initActivity extends Activity {
	 private PackageInfo pi = null;
	 private dbHelper db = null;
	 private Cursor myCursor = null;
	 private Intent mainIntent = null;
	 private String url_class = "";			//url连接 
	 private String result = "";			//获取数据返回状态
	 private String errorStr = "";			//获取数据返回错误信息
	 private long second = 0;				//毫秒数
	 private long stime = 0;				//起始时间
	 private String session = "";			//事务id
	 private String uid = "";               //用户id
	 private String mb = "";                //用户电话
	 private String addr = "";				//用户地址
	 private String usernm = "";            //用户名称
	 private int initime= 1000;				//启动页停留时间
	 
	 private String serverVersion = "1";
	 private MyApplication myApplication = null;
	 private String areaDbname = "";		//城市数据库名称
	 private String cityName = "";			//城市名称
	 private ArrayList<areaDbInfo> arealist = null; //获取城市数据库配置
	    
	 private EatParams eatparamInstance; //公共参数实例对象
	 
	 public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init);
        
        eatparamInstance = EatParams.getInstance();
        arealist = new ArrayList<areaDbInfo>();

        ((MyApplication)getApplication()).setInstance(initActivity.this);
        
        //设置版本信息
		PackageManager pm = getPackageManager();
        try {
            pi = pm.getPackageInfo(getPackageName(), 0);
            TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
            versionNumber.setText("Version " + pi.versionName);
        } catch (NameNotFoundException e) {
           e.printStackTrace();
        }
        
        if (eatparamInstance.isNetworkVailable(initActivity.this)) {//有网络
            Timer timer = new Timer();  
            timer.schedule(new TimerTask()  
            {    
                @Override  
                public void run()   
                {  
                	getConfigInfo(); //执行的内容  
                }  
                  
            }, 3000);//表示3秒后执行  (3秒时间定位）
		}else{														//无网络
			eatparamInstance.setNetPosWorking(2);
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

	//获取配置信息 
	private void getConfigInfo(){
		url_class = eatparamInstance.getConfigUrl();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML(url_class);
				handler.sendEmptyMessage(2);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}
	 
	//初始化显示内容
	private void initUI(){
		getCityByIP();
	}
	
	//根据ip获取城市名称
	private void getCityByIP(){
		url_class = eatparamInstance.getUrlServer() + "?argv={weba,-ip}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML_city(url_class);
				handler.sendEmptyMessage(1);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}
	
	//判断自动登录
	 public void autoLogin(){
		 if(eatparamInstance.getNetPosWorking() <2){
	        db=new dbHelper(initActivity.this);
	        myCursor = db.select();
	        if(myCursor.getCount() > 0){
	        	
	        	myCursor.moveToPosition(0);
	        	
	        	String mb = myCursor.getString(myCursor.getColumnIndex(dbHelper.FIELD_MB));
	        	String pwd = myCursor.getString(myCursor.getColumnIndex(dbHelper.FIELD_PWD));
	        	String urlServer = eatparamInstance.getUrlServer();
//	        	if(areaDbname.length() <= 0){
//	        		areaDbname = "eat";
//	        	}
	    		url_class = urlServer + "?argv={webd,-db,"+areaDbname+",-login,"+ mb +","+ pwd +"}";

	    		Runnable runnable = new Runnable() {
	    			@Override
	    			public void run() {
	    				saxParseXML(url_class);
	    				handler.sendEmptyMessage(0);
	    			}
	    		};
	    		Thread thread = new Thread(runnable);
	    		thread.start();
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
		 }else{
			 second = System.currentTimeMillis() - stime;
			long wtime = initime - second;
			if(wtime > 0){
				waitShow(wtime);
			}else{
				waitShow(0);
			}
		 }
	         
	 }
	 //延迟显示页跳转
	 public void waitShow(long time){
		 new Handler().postDelayed(new Runnable() {
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
         }, time);
	 }	 
	 
	//异步处理数据，更新界面显示
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==0){										//成功获取登录数据
				if(result.equals("ok")){
					eatparamInstance.setSession(session);
					eatparamInstance.setUid(uid);
					
				}else{
					if(errorStr.length() <= 0){
						errorStr = "登录出问题啦";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}			
				second = System.currentTimeMillis() - stime;
				long wtime = initime - second;
				if(wtime > 0){
					waitShow(wtime);
				}else{
					waitShow(0);
				}
			}else if(msg.what==1){	//根据ip获取城市
				if(result.equals("ok")){
					for(int i=0;i<arealist.size();i++){
						if(arealist.get(i).getCity().indexOf(cityName) != -1){
							areaDbname = arealist.get(i).getDbname();
							eatparamInstance.areaDbName = areaDbname;
						}
					}
					autoLogin();
				}else{
					if(errorStr.length() <= 0){
						errorStr = "获取城市信息出错啦";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}else if(msg.what == 2){								//成功获取配置信息
				if(result.equals("ok")){
					if(arealist.size()>0){
						for(int i=0;i<arealist.size();i++){
							if(arealist.get(i).getCity().indexOf(cityName) != -1){
								areaDbname = arealist.get(i).getDbname();
								eatparamInstance.areaDbName = areaDbname;
							}
						}
						eatparamInstance.areaDblist =arealist;
					}
					checkVersion();
				}else{
					if(errorStr.length() <= 0){
						errorStr = "服务器在火星，连接不上啊";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	 
	 /*----------------------------------------登录 or 获取配置文件----------------------------------------*/
	private void saxParseXML(String url){
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url,"UTF-8");

			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());

//				//cp.add	 打印输出返回数据
//				BufferedReader reader = new BufferedReader(new InputStreamReader(is));			
//	        	String result = "";
//	        	String line ="";
//	        	//读取字符流内容
//	        	while((line = reader.readLine()) != null){
//	        		result = result + line;
//	        	}
//	        	Log.i("result data",url+ ">>>>>>>>>>>>> return data: " + result);
//	        	//cp.add
				
			parser = factory.newSAXParser();

			SaxParseHandler personHandler = new SaxParseHandler();
			parser.parse(is, personHandler);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * SAX解析XML的处理机制
	 * 
	 */
	class SaxParseHandler extends DefaultHandler {
		String str = null;
		int i=0;
		/**
		 * XML开始时，执行此函数
		 */
		@Override
		public void startDocument() throws SAXException {
			//			Log.e("TEST", "startDocument");
		}
		/**
		 * XML结束时，执行此函数
		 */
		@Override
		public void endDocument() throws SAXException {
		}

		/**
		 * XML标签开始时，执行此函数，读取标签名称，和标签内的属性
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			str = localName;
			if ("r".equals(localName)) {
				areaDbInfo areaItem=new areaDbInfo();
				areaItem.setCity(attributes.getValue("city"));
				areaItem.setDbname(attributes.getValue("dbname"));
				arealist.add(areaItem);
				
			}else {
				str = localName;
			}
		}
		/**
		 * XML标签结束时，执行此函数
		 */
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			str = null;
		}
		/**
		 * 解析标签内的值，如<chinese>ssss</chinese>，为了读取"ssss"
		 */
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (str != null) {
				String data = new String(ch, start, length);
				//XML文件格式化的时候容易产生以下换行，制表符等特殊字符，
				//这些需要特别注意处理
				Pattern p = Pattern.compile("\\s*|\t|\r|\n"); 
				Matcher m = p.matcher(data); 
				data = m.replaceAll(""); 

				 if ("ret".equals(str)) {
					if (!data.equals("")) {
						result = data;
					}
				 }else if("res".equals(str)){
					 if(!data.equals("")){
						 errorStr = data;
					 }
				 }else if("sid".equals(str)){
					 if (!data.equals("")) {
						 session = data;
					 }
				 }else if("id".equals(str)){
					 if(!data.equals("")){
						 uid = data;					 
					 }
				 }else if("durl".equals(str)){
					 if(!data.equals("")){
						 eatparamInstance.setDownloadUrl(data);
					 }
				 }else if("version".equals(str)){
					 if(!data.equals("")){
						 serverVersion = data;
						 eatparamInstance.setServerVersion(data);
					 }
				 }else if("mb".equals(str)){
					 if(!data.equals("")){
						 mb = data;		
						 eatparamInstance.setMb(mb);
					 }
				 }else if("addr".equals(str)){
					 if(!data.equals("")){
						 addr = data;		
						 eatparamInstance.setAddr(addr);
					 }
				 }else if("rname".equals(str)){
					 if(!data.equals("")){
						 usernm = data;		
						 eatparamInstance.setUsename(usernm);
					 }
				 }
			}
		}
	}
		
		
	/*-------------------------------------获取城市--------------------------------------*/
	/**
	 * HTTP连接，SAX解析XML    
	 * @param url
	 * 
	 */
	private void saxParseXML_city(String url){
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url,"UTF-8");

			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());

//				//cp.add	 打印输出返回数据
//				BufferedReader reader = new BufferedReader(new InputStreamReader(is));			
//	        	String result = "";
//	        	String line ="";
//	        	//读取字符流内容
//	        	while((line = reader.readLine()) != null){
//	        		result = result + line;
//	        	}
//	        	Log.i("result data",url+ ">>>>>>>>>>>>> return data: " + result);
//	        	//cp.add
				
			parser = factory.newSAXParser();

			SaxParseHandler_city personHandler = new SaxParseHandler_city();
			parser.parse(is, personHandler);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * SAX解析XML的处理机制
	 * 
	 */
	class SaxParseHandler_city extends DefaultHandler {
		String str = null;
		int i=0;
		/**
		 * XML开始时，执行此函数
		 */
		@Override
		public void startDocument() throws SAXException {
		}
		/**
		 * XML结束时，执行此函数
		 */
		@Override
		public void endDocument() throws SAXException {
			
		}

		/**
		 * XML标签开始时，执行此函数，读取标签名称，和标签内的属性
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			str = localName;
			if ("r".equals(localName)) {
				cityName = attributes.getValue("city");				
			}else {
				str = localName;
			}
		}
		/**
		 * XML标签结束时，执行此函数
		 */
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			str = null;
		}
		/**
		 * 解析标签内的值，如<chinese>ssss</chinese>，为了读取"ssss"
		 */
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (str != null) {
				String data = new String(ch, start, length);
				//XML文件格式化的时候容易产生以下换行，制表符等特殊字符，
				//这些需要特别注意处理
				Pattern p = Pattern.compile("\\s*|\t|\r|\n"); 
				Matcher m = p.matcher(data); 
				data = m.replaceAll(""); 

				 if ("ret".equals(str)) {
					if (!data.equals("")) {
						result = data;
					}
				 }else if("res".equals(str)){
					 if(!data.equals("")){
						 errorStr = data;
					 }
				 }
			}
		}
	}
		
	/***
	 * 检查是否更新版本
	 */
	public void checkVersion() {
		myApplication = (MyApplication) getApplication();
		if (myApplication.localVersion < Integer.valueOf(serverVersion)) {

			// 发现新版本，提示用户更新
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("软件升级")
					.setMessage("发现新版本,建议立即更新使用.")
					.setPositiveButton("更新",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// 开启更新服务UpdateService
									// 这里为了把update更好模块化，可以传一些updateService依赖的值
									// 如布局ID，资源ID，动态获取的标题,这里以app_name为例
									Intent updateIntent = new Intent(
											initActivity.this,
											UpdateService.class);
									updateIntent.putExtra(
											"app_name",
											eatparamInstance.getAppName());
									startService(updateIntent);
									SharedPreferences sharedPreferences =getSharedPreferences("guidnum", Context.MODE_PRIVATE);
									Editor editor = sharedPreferences.edit();//获取编辑器
									editor.putString("guidnum", "");
									editor.commit();//提交修改
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									initUI();
									dialog.dismiss();
								}
							});
			alert.create().show();

		}else{
			initUI();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
