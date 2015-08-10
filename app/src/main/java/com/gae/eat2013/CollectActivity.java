package com.gae.eat2013;
/*
 * author:小胡
 * version:2013-5-24
 * description:常点收藏
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
import com.gae.adapter.eateryAdapter;
import com.gae.basic.GetHttp;
import com.gae.control.AddCard;
import com.gae.entity.EatParams;
import com.gae.entity.FoodItem;
import com.gae.entity.ShopItem;
import com.gae.listener.ListChangedListener;
import com.gae.view.CollectViewLeft;
import com.gae.view.ExpandTabView;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class CollectActivity extends Activity{
	private String areaDbname="";										//城市数据库名称
	private String url_class = "";										//请求数据路径
	private String urlServer = "";										//服务器路径
	private ArrayList<ShopItem> mlist = null;							//餐馆数据源
    private View mainChild = null;                                      //主界面
    private View collectlistview= null;                                 //收藏餐馆
    private View collectseftview= null;                                 //自定义收藏
    private View collectfoodview= null;                                 //菜品收藏
    private LinearLayout llcontactview= null;                           //容器
    private String result = "";                                         //返回结果
    private String errorStr = "";										//请求数据返回错误信息
    private ListView collectlist= null;                                 //餐馆listview
    private TextView selfcollect= null;                                 //自定义名称
    private  ListView foodlistview= null;                                //菜品收藏
	private FoodAdapter foodadapter= null;                              //菜品适配器
	private TextView totalnum= null;                                    //点餐数量
	private ImageView carfor= null;                                     //转到购物车
    private ExpandTabView expandTabView= null;                          //分类容器
    private CollectViewLeft viewLeft= null;                             //菜品、餐馆
    private AddCard add = null;      									//购物车类
    private ArrayList<View> mViewArray = null;   						//分类容器
    private ArrayList<FoodItem> foodlist = null; 						//菜品
    private Map<Integer, Boolean> selectedMap = null;					//是否选中
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainChild=getLayoutInflater().inflate(R.layout.collect_activity,null);
		setContentView(mainChild);
		
		areaDbname=EatParams.getInstance().areaDbName;
		urlServer = EatParams.getInstance().getUrlServer();
		mlist = new ArrayList<ShopItem>();
		add = new AddCard(CollectActivity.this);
		mViewArray = new ArrayList<View>();
		foodlist = new ArrayList<FoodItem>();
		selectedMap = new HashMap<Integer, Boolean>();

		
        llcontactview=(LinearLayout)findViewById(R.id.collectcontact);
        collectlistview = getLayoutInflater().inflate(R.layout.collectlistview,null);
        collectseftview = getLayoutInflater().inflate(R.layout.collectseft,null);
        collectfoodview = getLayoutInflater().inflate(R.layout.collectfood,null);
        foodlistview = (ListView)collectfoodview.findViewById(R.id.foodList);
		totalnum = (TextView)collectfoodview.findViewById(R.id.totalnum);
		carfor = (ImageView)collectfoodview.findViewById(R.id.carfor);
        llcontactview.addView(collectlistview);
        
        collectlist = (ListView)collectlistview.findViewById(R.id.collectList);
        expandTabView = (ExpandTabView)findViewById(R.id.expandtab_view);
        viewLeft = new CollectViewLeft(CollectActivity.this);
        
        selfcollect = (TextView)findViewById(R.id.selfcollect);
        
        //返回
        Button collectback = (Button)findViewById(R.id.collectback);
        collectback.setOnClickListener(new OnClickListener() {       
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
        //购物车类
		add = new AddCard(CollectActivity.this);
		add.getCardItemUrl();                                 //查询购物车
		add.setListChangedlistener(new ListChangedListener() {

			@Override
			public void OnListItemClick(String str) {
				totalnum.setText(add.totalNum());
			}
		});
		
		//自定义收藏
        selfcollect.setOnClickListener(new OnClickListener() { 
			
			@Override
			public void onClick(View v) {
				llcontactview.removeAllViews();
				llcontactview.addView(collectseftview);
			}
		});
        
        //收藏菜品Item处理
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
					Toast.makeText(CollectActivity.this,"添加成功", Toast.LENGTH_SHORT).show();
					getShopInfo(foodlist.get(position).VID);
				}
				foodadapter.changeSelectMap(selectedMap);
				foodadapter.notifyDataSetInvalidated(); 
			}
		});
		
		//跳转到餐馆主页
		collectlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent = new Intent(CollectActivity.this,StoreActivity.class);
				intent.putExtra("storename", mlist.get(position).getTITLE());
				intent.putExtra("storecaixi", mlist.get(position).getMCLSN());
				intent.putExtra("storetel", mlist.get(position).getTEL());
				intent.putExtra("storeaddr", mlist.get(position).getADDR());
				intent.putExtra("vid", mlist.get(position).getVID());
				startActivity(intent);
			}
		});
		
		//购物车
		carfor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in=new Intent(CollectActivity.this,CartActivity.class);				
				startActivity(in);
				CollectActivity.this.finish();
			}
		});
        shopListItem();
        //初始化下拉选择组件
        initVaule();
        //初始化下拉选择组件监听事件
		initListener();
	}
	
	//初始化下拉选择组件
	private void initVaule() {
		mViewArray.add(viewLeft);
		ArrayList<String> mTextArray = new ArrayList<String>();
		
		mTextArray.add("类型");
		mTextArray.add("距离");
		expandTabView.setValue(mTextArray, mViewArray);
		expandTabView.setTitle(viewLeft.getShowText(), 0);
	}
	
	//初始化下拉选择组件监听事件
	private void initListener() {		
		viewLeft.setOnSelectListener(new CollectViewLeft.OnSelectListener() {
			@Override
			public void getValue(String distance, String showText) {
				onRefresh(viewLeft, showText);
			}

			@Override
			public void getdistance(String distance) {
				String urlServer = EatParams.getInstance().getUrlServer();
				if(distance.equals("常点菜品")){
					llcontactview.removeAllViews();
					foodlist.clear();
					llcontactview.addView(collectfoodview);
					url_class = urlServer + "?argv={webd,-DB,"+areaDbname+",-SID,'"
							+ EatParams.getInstance().getSession() + "',-HOUSEQUERY,'"
							+ EatParams.getInstance().mLongitude + "','"
							+ EatParams.getInstance().mLatitude + "','SP','0','30'}";
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							saxParseXMLfood(url_class);
							handler.sendEmptyMessage(1);
						}
					};
						Thread thread = new Thread(runnable);
						thread.start();
				}else{
					llcontactview.removeAllViews();
					llcontactview.addView(collectlistview);
					mlist.clear();
					shopListItem();
				}
				
			}
			
		});
	}
	
	//刷新下拉选择框显示
	private void onRefresh(View view, String showText) {
		expandTabView.onPressBack();
		int position = getPositon(view);
		if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
			expandTabView.setTitle(showText, position);
		}
		Toast.makeText(CollectActivity.this, showText, Toast.LENGTH_SHORT).show();

	}
	
	//获取下拉选择框选中索引
	private int getPositon(View tView) {
		for (int i = 0; i < mViewArray.size(); i++) {
			if (mViewArray.get(i) == tView) {
				return i;
			}
		}
		return -1;
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
	
	//获取餐馆菜品
	public void shopListItem(){
		String urlServer = EatParams.getInstance().getUrlServer();

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
				tempRestaurant.setSCAVG(attributes.getValue("SCAVG"));
				tempRestaurant.setVID(attributes.getValue("VID"));	
				tempRestaurant.setTEL(attributes.getValue("MB"));
				tempRestaurant.setTITLE(attributes.getValue("VNAME"));

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
			// Log.e("TEST", "endDocument");
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
				tempRestaurant.setPID(attributes.getValue("PID")); // PID
				tempRestaurant.setDISC(attributes.getValue("DISC"));
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
	private void saxParseXML_resturant(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "UTF-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			parser = factory.newSAXParser();
			SaxParseHandler_resturant personHandler = new SaxParseHandler_resturant();
			mlist = new ArrayList<ShopItem>();
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
	//异步处理数据，更新界面显示
		private Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what==0){									//获取餐馆菜品列表				
					if(result.equals("ok")){
						eateryAdapter adapter = new eateryAdapter(CollectActivity.this, mlist);
						collectlist.setAdapter(adapter);
					}else{
						if(errorStr.length() <= 0){
							errorStr = "未获取请求数据，请检查网络是否连接正常";
						}
						Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
					}			

				}else if(msg.what==1){								//常点菜品
					if (result.equals("ok")) {
						for (int i = 0; i < foodlist.size(); i++) {
							selectedMap.put(i, false);
						}
						foodadapter=new FoodAdapter(CollectActivity.this,foodlist,selectedMap);
						foodlistview.setAdapter(foodadapter);
						String number=add.totalNum();
						totalnum.setText(number);
					}else{
						if(errorStr.length() <= 0){
							errorStr = "未获取请求数据，请检查网络是否连接正常";
						}
						Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
					}
				}else if(msg.what == 3){							//获取餐馆
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
