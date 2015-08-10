package com.gae.eat2013;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gae.basic.GetHttp;
import com.gae.basic.StringUtils;
import com.gae.entity.EatParams;
/**
 * 发布妙招分享
 * @author Administrator
 *
 */
public class PublishFoodActivity extends Activity {
	private String areaDbname = "";				//城市数据库名称
	private String urlServer = "";				//服务器路径
	private String sid = "";					//事务id
	private String username = "";				//用户名
	private Button btnback = null, publishinfo = null;
	private EditText etitle = null, econtent = null;
//	private TextView promptcontent = null; 		//定义提示输入字数tv
	private String url_class = "";				//请求数据路径
	private String result ="";					//请求数据返回标识
	private String errorStr = "";				//请求数据返回错误信息
	private String id = "";						//用户id
	private String user = "";					//用户

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.publisfood_activity); //初始化界面 
		
		areaDbname = EatParams.getInstance().areaDbName;
		urlServer = EatParams.getInstance().getUrlServer();
		sid = EatParams.getInstance().getSession();
		username = EatParams.getInstance().getUsename();
		
		etitle = (EditText) findViewById(R.id.edittitle);
		econtent = (EditText) findViewById(R.id.editcontent);  //初始化编辑内容框
//		promptcontent = (TextView) findViewById(R.id.tvprompt);  //初始化提示输入字数tv
		Intent intent = getIntent();
		final String isupdate = intent.getStringExtra("isupdate");
		String title = intent.getStringExtra("title");
		String content = intent.getStringExtra("content");
		id = intent.getStringExtra("id");
		user = intent.getStringExtra("user");
		etitle.setText(title);
		econtent.setText(content);
		econtent.addTextChangedListener(watcher);//对输入的内容进行监听
		
		// 发布信息
		publishinfo = (Button) findViewById(R.id.publishinfo);
		publishinfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isupdate!=null&&!isupdate.equals("")) {//判断是不是进来修改信息
					// 修改发布信息
					if (isChecked()) {
						if(sid==null||sid.equals("")){
							Toast.makeText(PublishFoodActivity.this, "您还没有登录噢！", Toast.LENGTH_SHORT).show();
						    return;								
						}
						else if(!username.equals(user)){
							Toast.makeText(PublishFoodActivity.this, "不够权限，只能修改自己的妙招分享！", Toast.LENGTH_SHORT).show();
						    return;
						}
						    updatePublishInformation();
					}
				} else {//否则是发布信息
					if (isChecked()) {
						if(sid==null||sid.equals("")){
							Toast.makeText(PublishFoodActivity.this, "您还没有登录噢！", Toast.LENGTH_SHORT).show();
						    return;
						}
						publishInformation();
					}
				}
			}
		});
		
		// 点击返回
		btnback = (Button) findViewById(R.id.btnback);
		btnback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	
	// 监听EditText字数改变
	TextWatcher watcher = new TextWatcher() {
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			textCountSet();
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			textCountSet();
		}

		@Override
		public void afterTextChanged(Editable s) {
			textCountSet();
		}
	};
			
	/**
	 * 数据合法性判断
	 */
	private boolean isChecked() {
		boolean ret = true;
//		if (StringUtils.isBlank(econtent.getText().toString())) {
//			Toast.makeText(PublishFoodActivity.this, "写点什么吧",
//					Toast.LENGTH_SHORT).show();
//			ret = false;
//		} else if (econtent.getText().toString().length() > 140) {
//			int currentLength = econtent.getText().toString().length();
//
//			Toast.makeText(PublishFoodActivity.this,
//					"已超出" + (currentLength - 140) + "字", Toast.LENGTH_SHORT)
//					.show();
//			ret = false;
//		}
		return ret;
	}

	/**
	 * 设置稿件字数
	 */
	private void textCountSet() {
//		String textContent = econtent.getText().toString();
//		int currentLength = textContent.length();
//		if (currentLength <= 140) {
//			promptcontent.setTextColor(Color.BLACK);
//			promptcontent.setText(String.valueOf(textContent.length()));
//		} else {
//			promptcontent.setTextColor(Color.RED);
//			promptcontent.setText(String.valueOf(140 - currentLength));
//		}
	}

	/**
	 * 修改发布信息
	 */
	public void updatePublishInformation() {
		String title = etitle.getText().toString();
		String content = econtent.getText().toString();
		url_class = urlServer + "?argv={webdm,-db," + areaDbname + ",-sid,"
				+ sid + ",-EDIT-MSG," + id + "," + URLEncoder.encode(title)
				+ "," + URLEncoder.encode(content) + ",''}";

		new Thread(){
			public void run() {
				saxParseXML_update(url_class);
				handler.sendEmptyMessage(1);				
			}
		}.start();
	}

	/**
	 * HTTP连接，SAX解析XML
	 * 
	 * @param url
	 * 
	 */
	private void saxParseXML_update(String url) {
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
	 * 发布信息
	 */
	public void publishInformation() {
		String title = etitle.getText().toString();
		String content = econtent.getText().toString();
		url_class = urlServer + "?argv={webdm,-db," + areaDbname + ",-sid,"
				+ sid + ",-ISSUE-MSG," + URLEncoder.encode(title) + ","
				+ URLEncoder.encode(content) + ",''}";

		new Thread(){
			public void run() {
				saxParseXML(url_class);
				handler.sendEmptyMessage(0);				
			}
		}.start();
	}

	/**
	 * HTTP连接，SAX解析XML
	 * 
	 * @param url
	 * 
	 */
	private void saxParseXML(String url) {
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
			str = localName;
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
			if (msg.what == 0) {						//发布信息
				if (result.equals("ok")) {
					Toast.makeText(PublishFoodActivity.this, "发布成功",
							Toast.LENGTH_SHORT).show();
				}else{
					if(errorStr.length() <= 0){
						errorStr = "未获取请求数据，请检查网络是否连接正常";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}else if (msg.what == 1) {					//修改发布信息
				if (result.equals("ok")) {
					Toast.makeText(PublishFoodActivity.this, "修改成功",
							Toast.LENGTH_SHORT).show();
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
