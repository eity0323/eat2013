package com.gae.eat2013;
/*
 * author:小容
 * version:2013-5-24
 * description:我的评论
 * */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.gae.adapter.CommentAdapter;
import com.gae.basic.GetHttp;
import com.gae.entity.CommentItem;
import com.gae.entity.EatParams;


public class CommentActivity extends Activity{
	private String areaDbname = "";  				//城市数据库名称
	private String url_class = "";                  //请求数据路径
	private String urlServer = "";					//服务器路径
	private String result = "";						//请求数据返回标识
	private String errorStr = "";					//请求数据返回错误信息
	private String sid="";          				//事物ID
	private ArrayList<CommentItem> commitem = null;	//评论数据源
	private ListView commlist = null;               //评论列表
	private Button commback = null;                 //返回
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_activity);	
		
		//获取城市数据库名称
		areaDbname=EatParams.getInstance().areaDbName;
		//获取服务器路径
		urlServer = EatParams.getInstance().getUrlServer();
		//获取事务id
		sid=EatParams.getInstance().getSession();
		//评论数据源
		commitem = new ArrayList<CommentItem>();
				
		//获取评论数据
		getmarklist();
		commlist = (ListView)findViewById(R.id.commentlist);
		
		//返回
		commback = (Button)findViewById(R.id.commentback);
		commback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}

	//获得评论数据
	private void getmarklist(){
		url_class = urlServer+ "?argv={webdm,-DB,"+areaDbname+",-sid,"+sid+",-COMM-QUERY,'','','','','0','10','','ALL'}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXMLMark(url_class);
				handler.sendEmptyMessage(0);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

	/**
	 * HTTP连接，SAX解析XML
	 * 
	 * @param url
	 * 
	 */
	private void saxParseXMLMark(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "utf-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			commitem = new ArrayList<CommentItem>();
			parser = factory.newSAXParser();
			SaxParseHandlermark pandler = new SaxParseHandlermark();
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
	class SaxParseHandlermark extends DefaultHandler {
		String str = null;
		int i = 0;

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
		}

		/**
		 * XML标签开始时，执行此函数，读取标签名称，和标签内的属性
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if ("r".equals(localName)) {
				CommentItem item = new CommentItem();
				item.setGuid(attributes.getValue("GUID"));
				item.setMark(attributes.getValue("CBODY"));
				item.setName(attributes.getValue("NAME"));
				item.setTime(attributes.getValue("CTIME"));
				item.setUname(attributes.getValue("UNAME"));
				commitem.add(item);
			}else{
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
				// XML文件格式化的时候容易产生以下换行，制表符等特殊字符，
				// 这些需要特别注意处理
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
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) { //获取评论成功事件
				if (result.equals("ok")) {
					CommentAdapter commadapter = new CommentAdapter(CommentActivity.this, commitem);
					commlist.setAdapter(commadapter);
				}else{
					if(errorStr.length() <= 0){
						errorStr = "未获取请求数据，请检查网络是否连接正常";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
}
