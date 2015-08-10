package com.gae.control;
/**
 * author:wing
 * version:2012-03-1 14:06:00
 * describe:操作购物车数据的类
 */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
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
import com.gae.entity.CarInfo;
import com.gae.entity.EatParams;
import com.gae.entity.ShopItem;
import com.gae.listener.ListChangedListener;
import com.gae.listener.ListListener;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AddCard {
	private Context mcontext;
	public String addcarTag = "";
	private Activity a;
	private String result = "";
	private String vid="";
	private String urlServer = EatParams.getInstance().getUrlServer();
	public String uid="";// = EatParams.getInstance().getMyUUID().toString();
	private boolean isadded = false;
	private String sid = EatParams.getInstance().getSession();
	private ArrayList<CarInfo> temprList = new ArrayList<CarInfo>();
	private ListListener lisener = null;
	private ListChangedListener listchangelistener = null;
	private String areaDbname=EatParams.getInstance().areaDbName;
	public AddCard(Activity tempcontext){
		a = tempcontext;
	}
	
	public void setLisener(ListListener lisen){
		lisener = lisen;
	}
	public void setListChangedlistener(ListChangedListener addlisten){
		listchangelistener = addlisten;
	}
	public void addCarUrl(String vid,String vname,String pid,String pname,String discv,String price,String gdiscv){
		isadded = true;
		ArrayList<CarInfo> list = EatParams.getInstance().list;
		boolean had = false;
		int id = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).pid.equals(pid)) {
				had = true;
				id=i;
			}
		}
		
		if(had){
			int tnum = list.get(id).getSnum();
			String unitid = list.get(id).getId();
			modifyCarUrl(unitid,uid,tnum + 1);
		}else{
			//添加到购物车
			uid = EatParams.getInstance().getUid();
			if (uid.equals("")) {
				uid = EatParams.getInstance().getMyUUID().toString();
			}
			String carUrl=urlServer + "?argv={webd,-DB,"+areaDbname+",-ADD-CART,"+ uid + ","+ vid 
					+ ","+ URLEncoder.encode(vname) + ","+ pid + ","+ URLEncoder.encode(pname)+ "," + price+ ","+ discv + ",1,"+gdiscv+"}";
			CardAdd(carUrl);
		}
	}
	//更新购物车
	public void modifyCarUrl(String id,String uid,int snum){
		uid = uid;
		isadded = true;
		String modifyUrl=urlServer + "?argv={webd,-DB,"+areaDbname+",-EDIT-CART,"+id+","+uid+","+ snum + "}";
		CardAdd(modifyUrl);
	}
	public void delAll(){
		//{webd,-db,"+areaDbname+",-DEL-CART,'6251D8A3-DC5C-47ED-9A35-7709EDB31809',''}
		uid = EatParams.getInstance().getUid();
		String delAllUrl=urlServer+"?argv={webd,-DB,"+areaDbname+",-DEL-CART,'"+uid+"',''}";
		EatParams.getInstance().list=null;
		ArrayList<CarInfo> list=null;
		temprList=null;
		CardAdd(delAllUrl);
	}
	public void deleCarUrl(String id){  //删除购物车菜品
		isadded = true;
		ArrayList<CarInfo> list = EatParams.getInstance().list;
		int num = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).id.equals(id)) {
				num=list.get(i).getSnum();
			}
		}
		
		uid = EatParams.getInstance().getUid();
		if (uid.equals("")) {
			uid = EatParams.getInstance().getMyUUID().toString();
		}
		if(num > 1){
			modifyCarUrl(id,uid,num - 1);
		}else{
			String delUrl=urlServer + "?argv={webd,-DB,"+areaDbname+",-DEL-CART,"+uid+","+id+"}";
			CardAdd(delUrl);
		}
	}
	public void delcart(String id){
		isadded = true;		
		uid = EatParams.getInstance().getUid();
		if (uid.equals("")) {
			uid = EatParams.getInstance().getMyUUID().toString();
		}	
			String delUrl=urlServer + "?argv={webd,-DB,"+areaDbname+",-DEL-CART,"+uid+","+id+"}";
			CardAdd(delUrl);		
	}
	private void CardAdd(final String carUrl) {
		Thread tr;
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML_xml(carUrl);
				handler.sendEmptyMessage(0);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
		
		try{
			thread.join();
		}catch(Exception ex){
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
	
	    //查询购物车
		public void getCardItemUrl(){
			uid = EatParams.getInstance().getUid();
			if (uid.equals("")) {
				uid = EatParams.getInstance().getMyUUID().toString();
			}
			String CardUrl= urlServer + "?argv={webd,-DB,"+areaDbname+",-SEARCH-CART,"+ uid +"}";
			getCardItem(CardUrl);
		}
		
		private void getCardItem(final String cardUrl) {
			Thread tr;
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					saxParseXML_car(cardUrl);
					handler.sendEmptyMessage(1);
				}
			};
			Thread thread = new Thread(runnable);
			thread.start();
			}
		
		/**
		 * 解析为获取登陆路径的xml
		 * @param url
		 */
		private void saxParseXML_car(String url) {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser;
			try {
				GetHttp getHttp = new GetHttp();
				String html = getHttp.contentToString(url, "UTF-8");
				ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
				parser = factory.newSAXParser();
				SaxParseHandler_car pandler = new SaxParseHandler_car();
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
		class SaxParseHandler_car extends DefaultHandler {
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
				temprList= new ArrayList<CarInfo>();
			}

			@Override
			public void startElement(String uri, String localName, String qName,
					Attributes attributes) throws SAXException {
				if ("r".equals(localName)) {
					CarInfo carinfo = new CarInfo();
					String snum = attributes.getValue("SNUM");
					int num = Integer.parseInt(snum);
					carinfo.id=(attributes.getValue("ID")) ;
					carinfo.uid=(attributes.getValue("UID"));
					carinfo.vid=(attributes.getValue("VID"));
					carinfo.vnm=(attributes.getValue("VNM"));
					carinfo.pid=(attributes.getValue("PID"));
					carinfo.pname=(attributes.getValue("PNAME"));
					carinfo.price=(attributes.getValue("PRICE"));
					carinfo.snum=(num);
					carinfo.state="N";
					carinfo.gdiscv=(attributes.getValue("GDISCV"));
					carinfo.discv=(attributes.getValue("DISCV"));
					carinfo.ctime=(attributes.getValue("CTIME"));
					temprList.add(carinfo);//将数据添加到list中去
					
				}else {
					str = localName;
				}
				    super.startElement(uri, localName, qName, attributes);
			} 
		}
		
		//合并购物车
		public void editCar(){
			String uid_ = EatParams.getInstance().getUid();
			uid = EatParams.getInstance().getMyUUID();
			String editUrl= urlServer + "?argv={webd,-DB,"+areaDbname+",-EDIT-CART,"+ uid+ "," + uid_ +"}";
			getEditCar(editUrl);
		}
		
		public void getEditCar(final String editUrl){
			Thread tr;
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					saxParseXML_edit(editUrl);
					handler.sendEmptyMessage(4);
				}
			};
			Thread thread = new Thread(runnable);
			thread.start();
		}
		
		private void saxParseXML_edit(String editurl){
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser;
			try {
				GetHttp getHttp = new GetHttp();
				String html = getHttp.contentToString(editurl, "UTF-8");
				
				temprList= new ArrayList<CarInfo>();
				
				ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
				parser = factory.newSAXParser();
				SaxParseHandler_edit pandler = new SaxParseHandler_edit();
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
		class SaxParseHandler_edit extends DefaultHandler {
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
				if ("r".equals(localName)) {
					CarInfo carinfo = new CarInfo();
					String snum = attributes.getValue("SNUM");
					int num = Integer.parseInt(snum);
					carinfo.id=(attributes.getValue("ID")) ;
					carinfo.uid=(attributes.getValue("UID"));
					carinfo.vid=(attributes.getValue("VID"));
					vid=attributes.getValue("VID");
					carinfo.vnm=(attributes.getValue("VNM"));
					carinfo.pid=(attributes.getValue("PID"));
					carinfo.pname=(attributes.getValue("PNAME"));
					carinfo.price=(attributes.getValue("PRICE"));
					carinfo.snum=(num);
					carinfo.gdiscv=(attributes.getValue("GDISCV"));
					carinfo.discv=(attributes.getValue("DISCV"));
					carinfo.ctime=(attributes.getValue("CTIME"));
					temprList.add(carinfo);//将数据添加到list中去
					
				}else {
					str = localName;
				}
				    super.startElement(uri, localName, qName, attributes);
			} 
		}
	 private Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
					if (msg.what == 0 && "ok".equals(result)) {
						//查询购物车
						getCardItemUrl();
					}else if (msg.what ==1) {
						if (result.equals("ok")) {//查询购物车成功
							getShopInfo(vid);//电话
							boolean initdata = false;
							if (!isadded) {
								initdata = true;								
							}
							EatParams.getInstance().list = temprList;
							if (!initdata && lisener != null) {
								lisener.OnListItemClick("second");
							}
							if (!initdata && listchangelistener != null) {
								listchangelistener.OnListItemClick("first");
							}
						}
					}else if (msg.what ==4) {
						if (result.equals("ok")) {//查询购物车成功
						}
					}
				}
			
		};

		/**
		 * 计算计算在线支付的总金额
		 */
		public String calculateTotal() {
			String totals = "0";
			double sum = 0;
			for (int i = 0; i < EatParams.getInstance().list.size(); i++) {
				String price = EatParams.getInstance().list.get(i).price;
				String discv = EatParams.getInstance().list.get(i).getDiscv();
				int sumb = EatParams.getInstance().list.get(i).getSnum();
				DecimalFormat df = new DecimalFormat("###.00");  
				double prices = Double.parseDouble(price);
				double discvs = Double.parseDouble(discv);
				sum += sumb * prices * discvs;
				totals = String.valueOf(df.format(sum));
			}
			return totals;
		}
		
		/**
		 * 计算现金支付的总金额
		 */
		public String calculateTotal_money() {
			String totals = "0";
			double sum = 0;
			for (int i = 0; i < EatParams.getInstance().list.size(); i++) {
				String price = EatParams.getInstance().list.get(i).price;
				int sumb = EatParams.getInstance().list.get(i).getSnum();
				double prices = Double.parseDouble(price);
				sum += sumb * prices;
				totals = String.valueOf(sum);
			}
			return totals;
		}
		
		/**
		 * 计算点餐的总份数
		 */
		public String totalNum() {
			String totalNum = "0";
			double sum = 0;
			for (int i = 0; i < EatParams.getInstance().list.size(); i++) {
				int number = EatParams.getInstance().list.get(i).getSnum();
				sum += number;
				totalNum = String.valueOf(sum);
			}
			return totalNum;
		}
		//{webd,-db,eat,-R-READ-ID,130225-153619-115212,O}
		public void getShopInfo(String vid){
			final String url_class=urlServer+ "?argv={webd,-DB,"+areaDbname+",-R-READ-ID,'"+vid+"','O'}";
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					saxParseXML_resturant(url_class);
					handler.sendEmptyMessage(3);
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
		private void saxParseXML_resturant(String url) {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser;
			try {
				GetHttp getHttp = new GetHttp();
				String html = getHttp.contentToString(url, "UTF-8");
				ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
				parser = factory.newSAXParser();
				SaxParseHandler_resturant personHandler = new SaxParseHandler_resturant();
				parser.parse(is, personHandler);

			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				// Log.e("TEST1", "1" + e.toString());
			} catch (SAXException e) {
				e.printStackTrace();
				// Log.e("TEST2", "2" + e.toString());
			} catch (IOException e) {
				e.printStackTrace();
				// Log.e("TEST3", "3" + e.toString());
			}
		}
		/**
		 * SAX解析XML的处理机制
		 * 
		 */
		class SaxParseHandler_resturant extends DefaultHandler {
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
				if ("r".equals(localName)) {

					EatParams.getInstance().setShopmb(attributes.getValue("TEL"));
					
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
}

