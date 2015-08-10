package com.gae.control;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.gae.basic.GetHttp;
import com.gae.dbHelper.dbHelper;
import com.gae.eat2013.R;
import com.gae.entity.EatParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class AddHouse {
	public Context mcontext;
	private String url_class="" ;
	private String result="";
	private String errorStr = "";
	private Dialog customDialog;
	private EditText userName;
	private EditText userPwd;
	private Button btn_login;
	private Button btn_reset;
	private CheckBox auto;   
	private String uName = "";
	private String uPwd = "";
	private String uname = "";
	private String upass = "";
	private dbHelper db; 
	private Cursor myCursor; 
	private SharedPreferences sp = null;
//	private ArrayList<AreaInfo> mgrlist = new ArrayList<AreaInfo>();
	private List books = new ArrayList(); 
	private String areaDbname=EatParams.getInstance().areaDbName;
	public AddHouse(Context tempcontext){
		mcontext = tempcontext;
	}
	//收藏
	public void houseListItem(String vid,String vname,String pid,String pname,String price, String ico, String typ){
		String urlServer = EatParams.getInstance().getUrlServer();
    	String sid=EatParams.getInstance().getSession();
    	LayoutInflater inflater = ((Activity) mcontext).getLayoutInflater();
		View layout = inflater.inflate(R.layout.login, null);
		userName = (EditText)layout.findViewById(R.id.loginMb);
		userPwd =  (EditText)layout.findViewById(R.id.loginPwd);
		auto = (CheckBox)layout.findViewById(R.id.loginKeepPwd);
		btn_login =  (Button)layout.findViewById(R.id.loginBtn);
		btn_reset =  (Button)layout.findViewById(R.id.loginCancel);
		sp = mcontext.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
    	if(sid==""||sid==null){
    		customDialog = new Dialog(mcontext);  
			customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  
			customDialog.setContentView(layout);  
			customDialog.show();
			if (sp.getBoolean("auto", false)){
				SharedPreferences settings = mcontext.getSharedPreferences("userinfo", 0);
				String uName = settings.getString("uName", "");
				String uPwd = settings.getString("uPwd", "");
				userName.setText(uName);
				userPwd.setText(uPwd);
	  	    	auto.setChecked(true);
	  		}
			btn_login.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					uName = userName.getText().toString();
					uPwd = userPwd.getText().toString();
					
					if(uName.trim().equals("")){
		    			Toast.makeText(mcontext, "手机号码不能为空", Toast.LENGTH_SHORT).show();
						return;
		    		}
		    		if(uPwd.trim().equals("")){
		    			Toast.makeText(mcontext, "密码不能为空", Toast.LENGTH_SHORT).show();
						return;
		    		}
					boolean autoLogin = auto.isChecked();
					if (autoLogin) {
						Editor editor = sp.edit();
						editor.putString("uName", uName);
						editor.putString("uPwd", uPwd);
						editor.putBoolean("auto", true);
						editor.commit();
					}else{
						Editor editor = sp.edit();
						editor.putString("uName", null);
						editor.putString("uPwd", null);
						editor.putBoolean("auto", false);
						editor.commit();
					}
					
					LoginControl  logincontrol= new LoginControl((Activity)mcontext);
					logincontrol.loginUrl(uName,uPwd);
					if(customDialog!=null)customDialog.dismiss();
				}
			});
			btn_reset.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(customDialog!=null)customDialog.dismiss();
				}
			});
			
    	}else{
		//argv={webd,-db,"+areaDbname+",-SID,sid,-HOUSEADD,vid,vname,pid,pname,price, ico, typ
		url_class =urlServer +"?argv={webd,-DB,"+areaDbname+",-SID,'"+sid+"',-HOUSEADD,'"+vid+"','"+URLEncoder.encode(vname)+
				"','"+URLEncoder.encode(pid)+"','"+URLEncoder.encode(pname)+"','"+price+"','"+ico+"','"+typ+"'}";
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
	}
	/**
	 * HTTP连接，SAX解析XML    
	 * @param url
	 * 
	 */
	private void saxParseXML(String url){
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "utf-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			parser = factory.newSAXParser();
			SaxParseHandler pandler = new SaxParseHandler();
			parser.parse(is, pandler);

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
			super.endDocument();
		}
		/**
		 * XML结束时，执行此函数
		 */
		@Override
		public void endDocument() throws SAXException {
			//			Log.e("TEST", "endDocument");
		}

		/**
		 * XML标签开始时，执行此函数，读取标签名称，和标签内的属性
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			str = localName;
			if ("r".equals(localName)) {
				
			}else {
				str = localName;
			}
			 super.startElement(uri, localName, qName, attributes);
		}
		/**
		 * XML标签结束时，执行此函数
		 */
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			str = null;
			//			Log.e("TEST", "endElement");
			super.endElement(uri, localName, qName);
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
				 }else if ("res".equals(str)) {
					if (!data.equals("")) {
						errorStr = data;
					}
				 }
			}
		}
	}
	
	//异步处理数据，更新界面显示
		private Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what==0){	//初始化显示					
					if(result.equals("ok")){						
						Toast.makeText(mcontext,"已经收藏好了！",Toast.LENGTH_SHORT).show();
					}else{
						if(errorStr.length() <= 0){
							errorStr = "收藏出问题啦";
						}
						Toast.makeText(mcontext,errorStr,Toast.LENGTH_SHORT).show();
					}			

				}
			}
		};
}


