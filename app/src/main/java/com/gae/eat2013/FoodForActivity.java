package com.gae.eat2013;
/*
 * author:小胡
 * version:2013-5-24
 * description:菜品信息
 * */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.gae.adapter.FoodAdapter;
import com.gae.basic.GetHttp;
import com.gae.control.AddCard;
import com.gae.control.AddHouse;
import com.gae.entity.EatParams;
import com.gae.entity.FoodItem;
import com.gae.listener.ListChangedListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FoodForActivity extends Activity{
	private String areaDbname = "";						//城市数据库名称
	private String url_class = "";						//请求数据路径
	public  ListView foodlistview = null;				//菜品列表
	private String urlServer = "";						//服务器路径
	private ArrayList<FoodItem> foodlist = null;		//菜品数据源
	private Map<Integer, Boolean> selectedMap = null;	//菜品选中项
	private AddCard add = null;							//购物车
	private AddHouse house = null;						//收藏夹
	private String result = "";							//请求数据返回标识
	private String errorStr = "";						//请求数据返回错误信息
	private FoodAdapter foodadapter = null;				//菜品列表适配器
	private TextView totalnum = null;					//总计
	private ImageView collectfor = null;				//收藏按钮
	private ImageView carfor = null;					//结算按钮
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.foodfor_activity);	
		
		areaDbname=EatParams.getInstance().areaDbName;
		urlServer = EatParams.getInstance().getUrlServer();
		foodlist = new ArrayList<FoodItem>();
		selectedMap = new HashMap<Integer, Boolean>();
		add = new AddCard(FoodForActivity.this);
		house = new AddHouse(FoodForActivity.this);
		
		//购物车
		add = new AddCard(FoodForActivity.this);
		add.getCardItemUrl();
		add.setListChangedlistener(new ListChangedListener() {

			@Override
			public void OnListItemClick(String str) {
				totalnum.setText(add.totalNum());
			}
		});
		totalnum=(TextView)findViewById(R.id.totalnum);
		
		//菜品列表
		foodlistview=(ListView)findViewById(R.id.foodList);
		foodlistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (selectedMap.get(position)) {
					selectedMap.put(position, false);
					for(int i=0;EatParams.getInstance().list.size()>i;i++){
						if(foodlist.get(position).PID.equals(EatParams.getInstance().list.get(i).getPid())){
							add.deleCarUrl(EatParams.getInstance().list.get(i).getId());
						}
					}
				} else {
					selectedMap.put(position, true);
					for(int i=0;EatParams.getInstance().list.size()>i;i++){//不同餐馆的删除
						if(!foodlist.get(position).VID.equals(EatParams.getInstance().list.get(i).getVid())){
							add.delcart(EatParams.getInstance().list.get(i).getId());
						}
					}
					add.addCarUrl(foodlist.get(position).VID,
							foodlist.get(position).VNAME,
							foodlist.get(position).PID,
							foodlist.get(position).PNAME,
							foodlist.get(position).DISC,
							foodlist.get(position).PRICE, "1");
					Toast.makeText(FoodForActivity.this,"添加成功", Toast.LENGTH_SHORT).show();
					getShopInfo(foodlist.get(position).VID);
				}
				foodadapter.changeSelectMap(selectedMap);
				foodadapter.notifyDataSetInvalidated(); 
			}
		});
		
		//收藏
		collectfor=(ImageView)findViewById(R.id.collectfor);
		collectfor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for(int i=0;foodlist.size()>i;i++){
					if(foodlist.get(i).isSTATE()){
						house.houseListItem(
								foodlist.get(i).getVID(),
								(String) foodlist.get(i).VNAME,
								foodlist.get(i).PID,
								(String) foodlist.get(i).PNAME,
								foodlist.get(i).PRICE,
								foodlist.get(i).getICO1(),
								"SP");
					}
				}
			}
		});
		
		//结算
		carfor=(ImageView)findViewById(R.id.carfor);
		carfor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in=new Intent(FoodForActivity.this,CartActivity.class);
				startActivity(in);
				FoodForActivity.this.finish();
			}
		});
		
		//获取菜品数据
		getfoodlsit();	
	}
	
	//获取菜品数据
	public void getfoodlsit(){
		url_class=urlServer+ "?argv={webd,-DB,"+areaDbname+",-PD-SEARCH,'"+EatParams.getInstance().mLongitude+"','"+EatParams.getInstance().mLongitude+"','','lknum','50','0'}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXMLfood(url_class);
				handler.sendEmptyMessage(0);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

	//获取餐馆数据
	public void getShopInfo(String vid){
		url_class=urlServer+ "?argv={webd,-DB,"+areaDbname+",-R-READ-ID,'"+vid+"','O'}";
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
	private void saxParseXMLfood(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "utf-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			foodlist = new ArrayList<FoodItem>();
			parser = factory.newSAXParser();
			SaxParseHandlerfood pandler = new SaxParseHandlerfood();
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
	class SaxParseHandlerfood extends DefaultHandler {
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
				FoodItem tempRestaurant = new FoodItem();
				tempRestaurant.setICO1(attributes.getValue("ICO1"));
				tempRestaurant.setCLSN(attributes.getValue("CLSN"));
				tempRestaurant.setVNAME(attributes.getValue("VNAME"));
				tempRestaurant.setVID(attributes.getValue("VID"));
				tempRestaurant.setSCALL(attributes.getValue("SCALL"));
				tempRestaurant.setRECMD(attributes.getValue("RECMD"));
				tempRestaurant.setPRICE(attributes.getValue("PRICE"));
				tempRestaurant.setPNAME(attributes.getValue("PNAME"));
				tempRestaurant.setPID(attributes.getValue("ID")); // PID
				tempRestaurant.setDISC(attributes.getValue("DISCV"));
				tempRestaurant.setDIST(attributes.getValue("DIST"));
				tempRestaurant.setMCLSN(attributes.getValue("MCLSN"));
				tempRestaurant.setUID(attributes.getValue("UID"));	
				tempRestaurant.setLKNUM(attributes.getValue("LKNUM"));
				tempRestaurant.setMODE("B");	
				tempRestaurant.setSTATE(false);
				foodlist.add(tempRestaurant);// 将数据添加到list中去
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
	class SaxParseHandler_resturant extends DefaultHandler {
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
			if (msg.what == 0) { //获取菜品数据成功事件
				if (result.equals("ok")) {
					for (int i = 0; i < foodlist.size(); i++) {
						selectedMap.put(i, false);
						if(EatParams.getInstance().list.size()>0){
						for(int j=0;EatParams.getInstance().list.size()>j;j++){
							if(EatParams.getInstance().list.get(j).getPid().equals(foodlist.get(i).getPID())){
								selectedMap.put(i, true);
							}
						}
						}
					}
					foodadapter=new FoodAdapter(FoodForActivity.this,foodlist,selectedMap);
					foodlistview.setAdapter(foodadapter);
					String number=add.totalNum();
					totalnum.setText(number);
				}else{
					if(errorStr.length() <= 0){
						errorStr = "未获取请求数据，请检查网络是否连接正常";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}else if(msg.what == 3) { // 获取餐馆数据成功事件
				if (result.equals("ok")) {
					
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
