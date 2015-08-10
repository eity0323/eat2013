package com.gae.control;

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

import com.gae.basic.GetHttp;
import com.gae.entity.EatParams;
import com.gae.entity.FoodTypeItem;
import com.gae.entity.ManagerItem;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetFoodTypItem {
	public Context mcontext;
	private String url_class="" ;
	private String result="";
	
	private ArrayList<FoodTypeItem> fstyplist = new ArrayList<FoodTypeItem>();// 菜品种类数据源
	private String areaDbname=EatParams.getInstance().areaDbName;
	public GetFoodTypItem(Context tempcontext){
		mcontext = tempcontext;
	}
	// 获得菜品种类
	public void getfoodtypeData() {
		String urlServer = EatParams.getInstance().getUrlServer();
		areaDbname=EatParams.getInstance().areaDbName;
		url_class = urlServer + "?argv={webd,-DB,"+areaDbname+",-SOC-SERACH,30,0}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML_foodtype(url_class);
				handler.sendEmptyMessage(1);
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
	private void saxParseXML_foodtype(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "UTF-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			parser = factory.newSAXParser();
			SaxParseHandler_foodtype pandler = new SaxParseHandler_foodtype();
			fstyplist = new ArrayList<FoodTypeItem>();
			parser.parse(is, pandler);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			Log.e("TEST1", "1" + e.toString());
		} catch (SAXException e) {
			e.printStackTrace();
			Log.e("TEST2", "2" + e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("TEST3", "3" + e.toString());
		}
	}

	/**
	 * SAX解析XML的处理机制
	 * 
	 */
	class SaxParseHandler_foodtype extends DefaultHandler {
		String str = null;
		int i = 0;

		/**
		 * XML开始时，执行此函数
		 */
		@Override
		public void startDocument() throws SAXException {
			// Log.e("TEST", "startDocument");
		}

		/**
		 * XML结束时，执行此函数
		 */
		@Override
		public void endDocument() throws SAXException {
			// Log.e("TEST", "endDocument");
		}

		/**
		 * XML标签开始时，执行此函数，读取标签名称，和标签内的属性
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			// Log.e("TEST", "startElement-------" + str);
			if ("r".equals(localName)) {
				FoodTypeItem tempfoodtype = new FoodTypeItem();
				tempfoodtype.setCNAME(attributes.getValue("CNAME"));
				tempfoodtype.setID(attributes.getValue("CODE"));
				fstyplist.add(tempfoodtype);// 将数据添加到list中去
			} else {
				str = localName;
			}
			// Log.e("TEST", "startElement");
		}

		/**
		 * XML标签结束时，执行此函数
		 */
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			str = null;
			// Log.e("TEST", "endElement");
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
				}
			}
		}
	}		
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) { // 获取大厦数据成功事件
			
			} else if (msg.what == 1) {
				if (result.equals("ok")) {
					FoodTypeItem tempfoodtype = new FoodTypeItem();
					tempfoodtype.setCNAME("全部");
					tempfoodtype.setID("");
					fstyplist.add(tempfoodtype);// 将数据添加到list中去
					EatParams.getInstance().fstyplist=fstyplist;
					
				}
			} 
		}
	};
}
