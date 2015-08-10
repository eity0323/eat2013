package com.gae.eat2013;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.gae.adapter.GourmetMasteradapter;
import com.gae.basic.GetHttp;
import com.gae.entity.EatParams;
import com.gae.entity.GourmetMaster;
/*
 * author:小巧
 * version:2013-5-24
 * description:美食达人
 * */
public class GourmetMasterActivity extends Activity{  
	private List<GourmetMaster> mlist = null;						//美食达人数据源
	private String areaDbname= null;      							//区域
	private String urlServer = null; 								//服务器路径
	private String result = "";                                     //返回结果
	private String errorStr = "";									//请求数据返回错误信息
	private Button backbtn = null;                                  //返回
	private ListView listview = null;                               //数据列表
	private Button gourmetbtn = null;                               //推荐美食
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gourmetmaster);		
		
		mlist = new ArrayList<GourmetMaster>();
		areaDbname = EatParams.getInstance().areaDbName;
		urlServer = EatParams.getInstance().getUrlServer();
		
		//获取美食达人
		getGourmetMaster();
		listview = (ListView)findViewById(R.id.gourmetList);
		//返回
		backbtn = (Button)findViewById(R.id.backbtn);
		backbtn.setOnClickListener(listener);
		//推荐美食，成为达人
		gourmetbtn = (Button)findViewById(R.id.gourmetbtn);
		gourmetbtn.setOnClickListener(listener);
		
		//发现餐馆，成为达人
		Button gourmetshop = (Button)findViewById(R.id.gourmetbtn_shop);
		gourmetshop.setOnClickListener(listener);
	}
	
	//点击事件监听
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.backbtn:
					finish();
					break;
				case R.id.gourmetbtn:
					Intent in =  new Intent(GourmetMasterActivity.this,RecommendActivity.class);
					startActivity(in);
					break;
				case R.id.gourmetbtn_shop:
					Intent inent = new Intent(GourmetMasterActivity.this,FindNewShopActivity.class);
					startActivity(inent);
				default:
					break;
			}			
		}
	};
	
	//查找所有美食达人
	public void getGourmetMaster(){
		final String  url = urlServer + "?argv={webdm,-DB,"+areaDbname+",-CATE-DOYEN,10,1}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML_gourmetm(url);
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
	private void saxParseXML_gourmetm(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "UTF-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			parser = factory.newSAXParser();
			SaxParseHandler_gourmetm personHandler = new SaxParseHandler_gourmetm();
			mlist = new ArrayList<GourmetMaster>();
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
	class SaxParseHandler_gourmetm extends DefaultHandler {
		String str = null;
		int i = 0;

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
			if ("r".equals(localName)) {
				GourmetMaster gourmetmaster = new GourmetMaster();
				gourmetmaster.setNAME(attributes.getValue("NAME"));
				gourmetmaster.setICO(attributes.getValue("ICO"));
				gourmetmaster.setGRADE(attributes.getValue("GRADE"));
				gourmetmaster.setRCNUM(attributes.getValue("RCNUM"));
				gourmetmaster.setRINUM(attributes.getValue("RINUM"));
				mlist.add(gourmetmaster);// 将数据添加到list中去
			} else {
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
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0){						//获取美食达人数据成功
				if(result.equals("ok")){
				  GourmetMasteradapter adapter = new GourmetMasteradapter(GourmetMasterActivity.this, mlist);
				  listview.setAdapter(adapter);
				}else{
					if(errorStr.length() <= 0){
						errorStr = "这里没有达人出没，赶快插个红旗占块地";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
}
