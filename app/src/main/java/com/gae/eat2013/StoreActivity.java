package com.gae.eat2013;

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
import com.gae.adapter.StoreAdapter;
import com.gae.basic.GetHttp;
import com.gae.basic.PopuItem;
import com.gae.basic.PopuJar;
import com.gae.control.AddCard;
import com.gae.control.AddHouse;
import com.gae.eat2013.FoodActivity.SaxParseHandler_resturant;
import com.gae.entity.EatParams;
import com.gae.entity.FoodItem;
import com.gae.entity.ShopItem;
import com.gae.listener.ListChangedListener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

/**
 * author:小巧 
 * version:2013-5-24 
 * description:餐馆主页
 * */
public class StoreActivity extends Activity {
	private String areaDbname = ""; 						// 城市数据库名称
	private String urlServer = ""; 							// 请求数据路径
	private ArrayList<FoodItem> foodlist = null; 			// 菜品数据源
	private Map<Integer, Boolean> selectedMap = null; 		// 选中项
	private AddCard add = null; 							// 初化化购物车数据
	private AddHouse house = null; 							// 初化化收藏数据
	private TextView cstorename, storename1, storetel1, storeaddr1,
			storecaixi1, totTextView;
	private ListView foodlistview = null;					//菜品列表对象
	private String vidStr = "";								//标识id
	private String result = "";								//数据请求返回标识
	private String errorStr = "";							//请求数据返回错误信息
	private StoreAdapter foodadapter = null;				//收藏列表对象	
	private ImageView carfor = null, collectfor = null;
	private Button foodback = null;							//返回
	private String storetel = "";							//电话号码
	
	private static final int ID_USER     = 1;
	private static final int ID_GROUNP   = 2;
	private PopuJar mPopu = null;
	private String storename = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.storeactivity);

		areaDbname = EatParams.getInstance().areaDbName;
		urlServer = EatParams.getInstance().getUrlServer();
		foodlist = new ArrayList<FoodItem>();
		selectedMap = new HashMap<Integer, Boolean>();
		add = new AddCard(StoreActivity.this);
		house = new AddHouse(StoreActivity.this);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		vidStr = bundle.getString("vid");
		storename = bundle.getString("storename");
		storetel = bundle.getString("storetel");
		String storeaddr = bundle.getString("storeaddr");
		String storecaixi = bundle.getString("storecaixi");
		
		
		foodlistview = (ListView) findViewById(R.id.storeList);
		EatParams.getInstance().setShopmb(storetel);
		cstorename = (TextView) findViewById(R.id.centertext);
		storename1 = (TextView) findViewById(R.id.tvfood);
		storetel1 = (TextView) findViewById(R.id.fortelname);
		storeaddr1 = (TextView) findViewById(R.id.foraddrname);
		storecaixi1 = (TextView) findViewById(R.id.forcaixiname);
		totTextView = (TextView) findViewById(R.id.totalnum);
		carfor = (ImageView) findViewById(R.id.carfor);
		collectfor = (ImageView) findViewById(R.id.collectfor);
		foodback = (Button) findViewById(R.id.foodback);
		cstorename.setText(storename);
		storename1.setText(storename);
		storetel1.setText(storetel);
		storeaddr1.setText(storeaddr);
		storecaixi1.setText(storecaixi);
		getAllfoodforStore();
		String number = add.totalNum();
		totTextView.setText(number);
		add = new AddCard(StoreActivity.this);
		add.getCardItemUrl();
		
		// 点击返回按扭
		foodback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 选中或不选中菜品事件，修改选中份数
		add.setListChangedlistener(new ListChangedListener() {

			@Override
			public void OnListItemClick(String str) {
				totTextView.setText(add.totalNum());
			}
		});
		// 收藏菜品事件
		storePopWin();
		collectfor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopu.show(v);
				mPopu.setAnimStyle(PopuJar.ANIM_REFLECT);
			}
		});
		// 进入购物车
		carfor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent in = new Intent(StoreActivity.this, CartActivity.class);
				startActivity(in);
			}
		});
		
		ImageView ivstorage = (ImageView)findViewById(R.id.imvpic2);
		ivstorage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				storageShop();
			}
		});
		
		ImageView ivtelorder = (ImageView)findViewById(R.id.btnrelalayout);
		ivtelorder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				telShop();
			}
		});
		
		// 选中或不选中菜品事件
		foodlistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (selectedMap.get(position)) {
					selectedMap.put(position, false);
					for (int i = 0; EatParams.getInstance().list.size() > i; i++) {
						if (foodlist.get(position).PID.equals(EatParams
								.getInstance().list.get(i).getPid())) {
							add.deleCarUrl(EatParams.getInstance().list.get(i)
									.getId());
						}
					}

				} else {
					selectedMap.put(position, true);
					for (int i = 0; EatParams.getInstance().list.size() > i; i++) {// 不同餐馆的删除
						if (!foodlist.get(position).VID.equals(EatParams
								.getInstance().list.get(i).getVid())) {
							add.delcart(EatParams.getInstance().list.get(i)
									.getId());
						}
					}
					add.addCarUrl(foodlist.get(position).VID,
							foodlist.get(position).VNAME,
							foodlist.get(position).PID,
							foodlist.get(position).PNAME,
							foodlist.get(position).DISC,
							foodlist.get(position).PRICE, "1");
					Toast.makeText(StoreActivity.this, "放到购物车里了罗", Toast.LENGTH_SHORT).show();
				}
				foodadapter.changeSelectMap(selectedMap);
				foodadapter.notifyDataSetInvalidated();
			}
		});
	}

	//收藏菜品
	private void storageFood(){
		boolean addable = false;
		for (int i = 0; foodlist.size() > i; i++) {
			if (foodlist.get(i).isSTATE()) {
				house.houseListItem(foodlist.get(i).getVID(),
						(String) foodlist.get(i).VNAME,
						foodlist.get(i).PID,
						(String) foodlist.get(i).PNAME,
						foodlist.get(i).PRICE, foodlist.get(i)
								.getICO1(), "SP");
				addable = true;
			}
		}
		if(!addable){
			Toast.makeText(StoreActivity.this, "还没有选择收藏的菜品哟", Toast.LENGTH_SHORT).show();
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
//						Toast.makeText(getApplicationContext(), "click user",
//								Toast.LENGTH_SHORT).show();
					storageFood();
				} else if (actionId == ID_GROUNP) {
//						Toast.makeText(getApplicationContext(), "click group",
//								Toast.LENGTH_SHORT).show();
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
//					Toast.makeText(getApplicationContext(), "Dismissed",
//							Toast.LENGTH_SHORT).show();
			}
		});
	}
		
	//收藏餐馆
	private void storageShop(){
		house.houseListItem(vidStr,storename,"","","","","SJ");
	}
	
	//电话订餐
	private void telShop(){		
		if(storetel.length() > 0){
			  String tno = storetel.split(",")[0];
			  if(tno.length() > 0){
				  Intent ient = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+ tno));  
	              startActivity(ient);  
			  }else{
				  Toast.makeText(getApplicationContext(), "这个餐馆还没提供联系电话呢", Toast.LENGTH_LONG).show();
			  }
		  }else{
			  Toast.makeText(getApplicationContext(), "这个餐馆还没提供联系电话呢", Toast.LENGTH_LONG).show();
		  }
	}
	
	/**
	 * 获取商家下面的菜品信息
	 */
	public void getAllfoodforStore() {
		final String url = urlServer + "?argv={webd,-DB," + areaDbname
				+ ",-P-READALL,'" + vidStr + "','','','100','0'" + "}";
		new Thread() {
			public void run() {
				saxParseXML(url);
				handler.sendEmptyMessage(0);
			};
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
			String html = getHttp.contentToString(url, "UTF-8");

			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());

			parser = factory.newSAXParser();
			foodlist = new ArrayList<FoodItem>();
			SaxParseHandler fooddata = new SaxParseHandler();
			parser.parse(is, fooddata);

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
				tempRestaurant.setMCLSN(attributes.getValue("MCLSN"));
				tempRestaurant.setDISC(attributes.getValue("MDISCV"));
				tempRestaurant.setUID(attributes.getValue("UID"));
				tempRestaurant.setMODE(attributes.getValue("TAB"));
				tempRestaurant.setDIST(attributes.getValue("DIST"));
				tempRestaurant.setSTATE(false);
				foodlist.add(tempRestaurant);// 将数据添加到list中去
			} else {
				str = localName;
			}
			super.startElement(uri, localName, qName, attributes);
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) { 				// 获取商家菜品成功事件
				if (result.equals("ok")) {
					for (int i = 0; i < foodlist.size(); i++) {
						selectedMap.put(i, false);
						if (EatParams.getInstance().list.size() > 0) {
							for (int j = 0; EatParams.getInstance().list.size() > j; j++) {
								if (EatParams.getInstance().list.get(j)
										.getPid()
										.equals(foodlist.get(i).getPID())) {
									selectedMap.put(i, true);
								}
							}
						}
					}
					foodadapter = new StoreAdapter(StoreActivity.this,
							foodlist, selectedMap);
					foodlistview.setAdapter(foodadapter);
				}else{
					if(errorStr.length() <= 0){
						errorStr = "没有数据显示呢";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
}