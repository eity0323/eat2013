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

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.gae.basic.GetHttp;
import com.gae.entity.EatParams;

public class UpdateHitsHot {
	

	private String urlServer = EatParams.getInstance().getUrlServer();	
	private String areaDbname=EatParams.getInstance().areaDbName;
	private String sid = EatParams.getInstance().getSession();
	private String url_class;
	private String result;
	public Activity mcontext;
	private String uptype = "";
	
	public  UpdateHitsHot(Activity context){
		mcontext=context;
	}

    //顶或踩的方法
	public void updateNum(String id, String type) {
		if(type.equals("G")){
			uptype = "感谢您的支持";
		}else{
			uptype = "很遗憾未能帮到您";
		}
		
		url_class = urlServer + "?argv={webdm,-db," + areaDbname + ",-sid,"
				+ sid + ",-GOOD-OR-BAD," + id + "," + type + "}";
		new Thread() {
			public void run() {
				saxParseXML(url_class);
				handler.sendEmptyMessage(0);
			};
		}.start();

	}
	private void saxParseXML(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "UTF-8");
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
	 * SAX解析XML的处理机制 SaxParseHandler_xml
	 */
	class SaxParseHandler extends DefaultHandler {
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
                   Toast.makeText(mcontext,uptype, Toast.LENGTH_SHORT).show();
				}
			}
		
	};
}
