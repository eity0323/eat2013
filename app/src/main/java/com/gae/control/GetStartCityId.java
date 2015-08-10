package com.gae.control;
/**
 * author:hxb
 * descriable:查询城市信息
 * version：2012-03-22 9:30:00
 */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
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

import android.app.AlertDialog;
import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class GetStartCityId {
	public Context mcontext;
	private String url_class="" ;
	private String result="";
	private String url;
	private String cityId="";
	private String areaDbname=EatParams.getInstance().areaDbName;
	public GetStartCityId(Context tempcontext){
		mcontext = tempcontext;
	}
	public void getCityId(String cityname){
		
		//argv={webds,-DB,name, -SID,sid,-PARAM-QUERY,atid,conm,stype,ord,limit,offset ,next,isval}
	    String urlServer = EatParams.getInstance().getUrlServer();
	    url = urlServer + "?argv={webds,-DB,"+areaDbname+",-PARAM-QUERY,'','"+URLEncoder.encode(cityname)+"','CT','','1','0','Y','Y'}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXMLEatery(url);
				handlerEatery.sendEmptyMessage(0);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
		}
	
		/**
		 * HTTP连接，SAX解析XML    
		 * @param url_class
		 * 
		 */
	/**
	 * HTTP连接，SAX解析XML    
	 * @param url
	 * 
	 */
	private void saxParseXMLEatery(String url){
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url,"UTF-8");


			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
////			cp.add	 打印输出返回数据
//			BufferedReader reader = new BufferedReader(new InputStreamReader(is));			
//        	String result = "";
//        	String line ="";
//        	//读取字符流内容
//        	while((line = reader.readLine()) != null){
//        		result = result + line;
//        	}
//        	Log.i("result data",url+ ">>>>>>>>>>>>> return data: " + result);
////        	cp.add
			
			parser = factory.newSAXParser();

			SaxParseHandlerEatery personHandler = new SaxParseHandlerEatery();
			parser.parse(is, personHandler);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			//			Log.e("TEST1", "1" + e.toString());
		} catch (SAXException e) {
			e.printStackTrace();
			//			Log.e("TEST2", "2" + e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			//			Log.e("TEST3", "3" + e.toString());
		}
	}

	/**
	 * SAX解析XML的处理机制
	 * 
	 */
	class SaxParseHandlerEatery extends DefaultHandler {
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
			//			Log.e("TEST", "endDocument");
		}

		/**
		 * XML标签开始时，执行此函数，读取标签名称，和标签内的属性
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if ("r".equals(localName)) {
				cityId=attributes.getValue("COID");
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
		//异步处理数据，更新界面显示
		private Handler handlerEatery = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what==0){	//初始化显示
					if(result.equals("ok")){
						if(cityId!=""||cityId!=null){
						EatParams.getInstance().cityId=cityId;
						}else{
							Toast.makeText(mcontext,"对不起！业务还没拓展到此城市！",Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(mcontext, result, Toast.LENGTH_SHORT).show();
					}			

				}
			}
		};
}
