package com.gae.control;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.gae.basic.GetHttp;
import com.gae.entity.EatParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class LoginControl {
	private Activity mactivity;
	 private String url_class;				//url连接 
	 private String result;					//获取数据返回状态
	 private long second =0;				//毫秒数
	 private long stime;					//起始时间
	 private String session = "";			//事务id
	 private String uid = "";               //用户id
	 private String mb = "";                //用户电话
	 private String addr = "";				//用户地址
	 private String username = "";				//用户
	 private int initime= 1000;

	 private String serverVersion = "1";
	private String sid = "";
	private String areaDbname=EatParams.getInstance().areaDbName;

	
	public ProgressDialog mypDialog = null;
	public LoginControl(Activity tempcontext){
		mactivity = tempcontext;
	}
	   //登陆
	public void loginUrl(String name,String pwd){
		String urlServer = EatParams.getInstance().getUrlServer();
		String templogin =urlServer + "?argv={webd,-db,"+areaDbname+",-login,"+ name +","+ pwd +"}";
		login(templogin);
	}
	
	public void login(final String url){
		if (!isNetworkVailable()) {
			Toast.makeText(mactivity, "网络有错，请检查网络！",Toast.LENGTH_SHORT).show();
			return;
		}else{
			mypDialog=new ProgressDialog(mactivity);
			//实例化
			mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			//设置进度条风格，风格为圆形，旋转的
			mypDialog.setMessage("登录中，请稍等");
			//设置ProgressDialog 提示信息
			mypDialog.setIndeterminate(false);
			//设置ProgressDialog 的进度条是否不明确
			mypDialog.setCancelable(true);
			//设置ProgressDialog 是否可以按退回按键取消
			mypDialog.show();
			//让ProgressDialog显示

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				saxParseXML_xml(url);
				handler.sendEmptyMessage(0);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}
}
	
	/**
	 * 解析为获取登陆路径的xml
	 * 
	 * @param url
	 */
	private void saxParseXML_xml(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "UTF-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			parser = factory.newSAXParser();
			SaxParseHandler_xml pandler = new SaxParseHandler_xml();
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
	 * SAX解析XML的处理机制 SaxParseHandler_xml
	 */
	class SaxParseHandler_xml extends DefaultHandler {
		String str = null;

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (str != null) {
				String data = new String(ch, start, length);
				// XML文件格式化的时候容易产生以下换行，制表符等特殊字符，
				// 这些需要特别注意处理
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher m = p.matcher(data);
				data = m.replaceAll("");

				 if ("ret".equals(str)) {
						if (!data.equals("")) {
							result = data;
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
							 EatParams.getInstance().setDownloadUrl(data);
							 Log.i("initActivity", "downloadUrl:" + data);
						 }
					 }else if("version".equals(str)){
						 if(!data.equals("")){
							 serverVersion = data;
							 EatParams.getInstance().setServerVersion(data);
						 }
					 }else if("mb".equals(str)){
						 if(!data.equals("")){
							 mb = data;		
							 EatParams.getInstance().setMb(mb);
						 }
					 }else if("addr".equals(str)){
						 if(!data.equals("")){
							 addr = data;		
							 EatParams.getInstance().setAddr(addr);
						 }
					 }else if("rname".equals(str)){
						 if(!data.equals("")){
							 username = data;		
							 EatParams.getInstance().setUsename(username);
						 }
					 }
						 
			}
		}

		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			str = null;
			super.endElement(uri, localName, qName);
		}

		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
				str = localName;
			    super.startElement(uri, localName, qName, attributes);
		} 
	}
	
	 private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0 && "ok".equals(result)) {
				EatParams.getInstance().setSession(session);
				mypDialog.dismiss();
				
			} else {
				if (result.contains("2")) { // 用户名有误
					Toast.makeText(mactivity, "用户名错误！",Toast.LENGTH_SHORT).show();
					mypDialog.dismiss();
				} else if (result.contains("-6")) { // 密码有误
					Toast.makeText(mactivity, "个人密码错误！",Toast.LENGTH_SHORT).show();
					mypDialog.dismiss();
				}
				Toast.makeText(mactivity, "登陆失败",Toast.LENGTH_SHORT).show();
				mypDialog.dismiss();
				return;
			}
		}			
	};


		/**
		 * 判断是否有可用网络
		 * 
		 * @return
		 */
		public boolean isNetworkVailable() {
			ConnectivityManager cManager = (ConnectivityManager) mactivity
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cManager.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				return true;
			} else {
				return false;
			}
		}

}
