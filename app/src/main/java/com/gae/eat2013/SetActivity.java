package com.gae.eat2013;
/**
 * author:小胡
 * version:2013-5-24
 * description:设置
 * */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.gae.adapter.SetAdapter;
import com.gae.basic.GetHttp;
import com.gae.basic.MyApplication;
import com.gae.basic.UpdateService;
import com.gae.entity.EatParams;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SetActivity extends Activity{
	
	private EatParams eatparamInstance; //公共参数实例对象
	private String url_class = "";			//url连接 
	private String result = "";				//数据请求返回标识
	private MyApplication myApplication = null;
	private String serverVersion = "1";
	private String errorStr = "";			//获取数据返回错误信息
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_activity);		
		
		eatparamInstance = EatParams.getInstance();
		
		//设置列表
		ListView setlist=(ListView)findViewById(R.id.setlist);		
		List<Map<String,Object>> setinfo = getsetlist();
		SetAdapter setadapter=new SetAdapter(SetActivity.this,setinfo);
		setlist.setAdapter(setadapter);
		setlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				setlistaction(arg2);
			}
		});
		
		//返回
		Button back=(Button)findViewById(R.id.setbackbtn);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		//退出登录
		Button exit = (Button)findViewById(R.id.set_login_exit);
		exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				logout();
			}
		});
	}
	
   //设置的数据源
	public List<Map<String,Object>>getsetlist(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String,Object>map = new HashMap<String, Object>();
		map.put("title", "自动登录");
        map.put("info", R.drawable.right);
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("title", "注册");
        map.put("info", R.drawable.right);
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("title", "版本检测");
        map.put("info", R.drawable.right);
        list.add(map);
//        map = new HashMap<String, Object>();
//        map.put("title", "切换城市");
//        map.put("info", R.drawable.right);
//        list.add(map);
        
        return list;
	}
	
	//跳转到对应设置页
	public void setlistaction(int position){
		if(position==0){
			Intent intent=new Intent(SetActivity.this,SetLoginActivity.class);
			startActivity(intent);
		}else if(position==1){
			Intent intent=new Intent(SetActivity.this,RegisterActivity.class);
			startActivity(intent);
		}else if(position == 2){
			getConfigInfo();
		}
	}
	
	private void logout(){
		//	{webd,-DB,eat,-LOGOUT,P13062411561119304628}
		String urlServer = eatparamInstance.getUrlServer();
		String sid = eatparamInstance.getSession();
		url_class = urlServer + "?argv={webd,-db,"+ eatparamInstance.areaDbName +",-LOGOUT,"+ sid + "}";

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML(url_class);
				handler.sendEmptyMessage(0);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}
	
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
				 }else if("version".equals(str)){
					 if(!data.equals("")){
						 serverVersion = data;
						 eatparamInstance.setServerVersion(data);
					 }
				 }
			}
		}
	}

	
	//异步处理数据，更新界面显示
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==0){										//退出登录成功
				eatparamInstance.setSession("");
			}else if(msg.what == 2){								//成功获取配置信息
				if(result.equals("ok")){
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
	
	//-------------------------------------------------------------------------------------------
	
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
											SetActivity.this,
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
									dialog.dismiss();
								}
							});
			alert.create().show();

		}else{
			Toast.makeText(SetActivity.this, "期待下一版本能快点发布", Toast.LENGTH_SHORT).show();
		}
	}

}
