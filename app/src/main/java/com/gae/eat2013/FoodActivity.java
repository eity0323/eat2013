package com.gae.eat2013;
/*
 * author:小胡
 * version:2013-5-24
 * description:附近菜品
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
import com.gae.basic.PopuItem;
import com.gae.basic.PopuJar;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FoodActivity extends Activity{
	private String areaDbname = "";                    	  //城市数据库名称
	private String url_class = "";                     	  //路径
	public  ListView foodlistview = null;                 //菜品数据
	private String urlServer = "";              		  //服务Url
	private ArrayList<FoodItem> foodlist = null;          //数据源
	private Map<Integer, Boolean> selectedMap = null;     //选中标记
	private AddCard add = null;                           //购物车
	private AddHouse house = null;                        //收藏
	private String result = "";                           //获取数据返回结果
	private String errorStr = "";						  //获取数据返回错误信息
	private FoodAdapter foodadapter = null;               //菜品适配
	private TextView totalnum = null;                     //已点份数
	private ImageView collectfor = null;                  //收藏
	private ImageView carfor = null;                      //购物车
	private String from = "";                             //跳转来源
	
	
	private static final int ID_USER     = 1;
	private static final int ID_GROUNP   = 2;
	private PopuJar mPopu = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.foodfor_activity);	
		
		//获取城市数据库名称
		areaDbname = EatParams.getInstance().areaDbName;
		urlServer = EatParams.getInstance().getUrlServer();
		foodlist = new ArrayList<FoodItem>();
		selectedMap = new HashMap<Integer, Boolean>();
//		add = new AddCard(FoodActivity.this);
		house = new AddHouse(FoodActivity.this);
		
		Intent intent=getIntent();
		from=intent.getStringExtra("from");
		
		//购物车
		add = new AddCard(FoodActivity.this);
		add.getCardItemUrl();
		add.setListChangedlistener(new ListChangedListener() {
			@Override
			public void OnListItemClick(String str) {
				totalnum.setText(add.totalNum());
			}
		});
		
		//总份数
		totalnum=(TextView)findViewById(R.id.totalnum);
		
		//返回
		Button back=(Button)findViewById(R.id.foodback);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
				
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
					for(int i=0;EatParams.getInstance().list.size()>i;i++){		//不同餐馆的删除
						if(!foodlist.get(position).VID.equals(EatParams.getInstance().list.get(i).getVid())){
							add.delcart(EatParams.getInstance().list.get(i).getId());
							for(int j=0;foodlist.size()>j;j++){
								if(foodlist.get(j).getPID().equals(EatParams.getInstance().list.get(i).getPid())){
									selectedMap.put(j, false);
								}
							}
							
						}
					}
					add.addCarUrl(foodlist.get(position).VID,
							foodlist.get(position).VNAME,
							foodlist.get(position).PID,
							foodlist.get(position).PNAME,
							foodlist.get(position).DISC,
							foodlist.get(position).PRICE, "1");
					
					Toast.makeText(FoodActivity.this,"放到购物车里罗", Toast.LENGTH_SHORT).show();
					getShopInfo(foodlist.get(position).VID);
				}
				foodadapter.changeSelectMap(selectedMap);
				foodadapter.notifyDataSetInvalidated(); 
			}
		});
		
		//收藏
		
		storePopWin();		
		collectfor=(ImageView)findViewById(R.id.collectfor);
		collectfor.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				mPopu.show(v);
				mPopu.setAnimStyle(PopuJar.ANIM_REFLECT);
			}
		});
		
		//结算
		carfor=(ImageView)findViewById(R.id.carfor);
		carfor.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent in=new Intent(FoodActivity.this,CartActivity.class);
				in.putExtra("from", from);
				startActivity(in);
			}
		});
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
	
	//获取餐馆信息(用于校验是否点的同一餐馆的菜)
	public void getShopInfo(String vid){
		url_class=urlServer+ "?argv={webd,-DB,"+areaDbname+",-R-READ-ID,'"+vid+"','O'}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML_resturant(url_class);
				handler.sendEmptyMessage(1);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}
	
	//收藏菜品
	private void storageFood(){
		boolean addable = false;
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
				addable = true;
			}
		}
		if(!addable){
			Toast.makeText(FoodActivity.this, "还没有选择收藏的菜品哟", Toast.LENGTH_SHORT).show();
		}
	}
	
	//跳转到收藏页
	private void go2StorageApp(){
		Intent tent = new Intent();
		tent.setClass(getApplicationContext(), CollectActivity.class);
		startActivity(tent);
	}
	
	//收藏弹出框
	private void storePopWin(){
		//收藏提示弹出框
		PopuItem userItem = new PopuItem(ID_USER, "加到收藏夹吧", null);
		PopuItem grounpItem = new PopuItem(ID_GROUNP, "去收藏夹看看", null);
		userItem.setSticky(true);
		mPopu = new PopuJar(this, PopuJar.VERTICAL);

		// add action items into PopuJar
		mPopu.addPopuItem(userItem);
		mPopu.addPopuItem(grounpItem);

		// Set listener for action item clicked
		mPopu.setOnPopuItemClickListener(new PopuJar.OnPopuItemClickListener() {
			@Override
			public void onItemClick(PopuJar source, int pos, int actionId) {
				PopuItem PopuItem = mPopu.getPopuItem(pos);

				if (actionId == ID_USER) {
//					Toast.makeText(getApplicationContext(), "click user",
//							Toast.LENGTH_SHORT).show();
					storageFood();
				} else if (actionId == ID_GROUNP) {
//					Toast.makeText(getApplicationContext(), "click group",
//							Toast.LENGTH_SHORT).show();
					go2StorageApp();
				} else {
					Toast.makeText(getApplicationContext(),
							PopuItem.getTitle() + " selected",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mPopu.setOnDismissListener(new PopuJar.OnDismissListener() {
			@Override
			public void onDismiss() {
//				Toast.makeText(getApplicationContext(), "Dismissed",
//						Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) { 			//获取菜品数据成功事件
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
					foodadapter=new FoodAdapter(FoodActivity.this,foodlist,selectedMap);
					if(foodlist.size() > 0){
						foodlistview.setVisibility(View.VISIBLE);
						foodlistview.setAdapter(foodadapter);
					}else{
						TextView tv = (TextView)findViewById(R.id.publish_food);
						tv.setVisibility(View.VISIBLE);
						tv.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								Intent tent = new Intent();
								tent.setClass(getApplicationContext(), RecommendActivity.class);
								startActivity(tent);
							}
						});
						foodlistview.setVisibility(View.GONE);
					}
					String number=add.totalNum();
					totalnum.setText(number);
				}else{
					if(errorStr.length() <= 0){
						errorStr = "没有数据显示呢";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}else if(msg.what == 1) { 	//获取餐馆数据成功事件
				if (result.equals("ok")) {
					
				}else{
					if(errorStr.length() <= 0){
						errorStr = "没有数据显示呢";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	
	/*菜品数据解析*/
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
				}else if("res".equals(str)) {
					if (!data.equals("")) {
						errorStr = data;
					}
				}
			}
		}
	}
	/*餐馆数据解析*/
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
				}else if("res".equals(str)) {
					if (!data.equals("")) {
						errorStr = data;
					}
				}
			}
		}
	}
}
