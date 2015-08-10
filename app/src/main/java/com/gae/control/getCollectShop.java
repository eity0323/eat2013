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

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gae.adapter.eateryAdapter;
import com.gae.basic.GetHttp;
import com.gae.eat2013.CollectActivity;
import com.gae.eat2013.FoodActivity;
import com.gae.eat2013.R;
import com.gae.entity.EatParams;
import com.gae.entity.FoodItem;
import com.gae.entity.ShopItem;

import com.gae.listener.eatery_menu_Listener;

public class getCollectShop {
	public Context mcontext;
	private String url_class="" ;
	private String result="";
	private ArrayList<ShopItem> mlist = new ArrayList<ShopItem>();
	private String areaDbname=EatParams.getInstance().areaDbName;
	public getCollectShop(Context tempcontext){
		mcontext = tempcontext;
	}
	public void shopListItem(){
			String urlServer = EatParams.getInstance().getUrlServer();
			//argv={webd,-DB,"+areaDbname+",-R-READ,'18','0','KC','00000100004','active desc','','0000010000400031','','N'}
			//argv={webd,-DB,"+areaDbname+",-SID,'P13022710333312382168',-HOUSEQUERY,'22.557692','114.096495','SJ','0','10'}

				url_class=urlServer+"?argv={webd,-DB,"+areaDbname+",-SID,'"+EatParams.getInstance().getSession()+"',-HOUSEQUERY,'"+
						EatParams.getInstance().mLongitude+"','"+EatParams.getInstance().mLatitude+"','SJ','0','30'}";	
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
				if ("r".equals(localName)) {
					ShopItem tempRestaurant = new ShopItem();
					tempRestaurant.setICO1(attributes.getValue("ICO1")) ;
					tempRestaurant.setDISTAN(attributes.getValue("DISTAN"));
					tempRestaurant.setADDR(attributes.getValue("ADDR"));
					tempRestaurant.setCURPX(attributes.getValue("CURPX"));
					tempRestaurant.setCURPY(attributes.getValue("CURPY"));
					tempRestaurant.setPRAVG(attributes.getValue("PRAVG"));
					tempRestaurant.setSPTYP(attributes.getValue("SPTYP"));
					if(EatParams.getInstance().getSession()==""||EatParams.getInstance().getSession()==null){
						tempRestaurant.setTEL(attributes.getValue("MB"));
						tempRestaurant.setTITLE(attributes.getValue("TITLE"));
						tempRestaurant.setVID(attributes.getValue("WCODE"));
					}else{
					tempRestaurant.setVID(attributes.getValue("VID"));	
					tempRestaurant.setTEL(attributes.getValue("MB"));
					tempRestaurant.setTITLE(attributes.getValue("VNAME"));
					}
					tempRestaurant.setMCLSN(attributes.getValue("MCLSN"));
					tempRestaurant.setID(attributes.getValue("ID"));
				
					mlist.add(tempRestaurant);//将数据添加到list中去
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
							eateryAdapter adapter = new eateryAdapter(mcontext, mlist);
							
						}else{
							Toast.makeText(mcontext,result, Toast.LENGTH_LONG).show();
						}			

					}
				}
			};
			
}
