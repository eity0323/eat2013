package com.gae.eat2013;
/**
 * author:小胡
 * version:2013-5-24
 * description:餐馆界面
 * */
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.gae.adapter.eateryAdapter;
import com.gae.basic.GetHttp;
import com.gae.entity.EatParams;
import com.gae.entity.FoodTypeItem;
import com.gae.entity.ShopItem;
import com.gae.listener.eatery_menu_Listener;
import com.gae.view.ExpandTabView;
import com.gae.view.ViewLeft;
import com.gae.view.ViewRight;

public class ShopActivity extends Activity{
	private String areaDbname = "";								//城市数据库名称
	private String url_class = "";								//获取数据路径
	private String result = "";									//返回结果标志
	private String errorStr = "";								//返回错误信息
	private ListView shoplist = null;							//餐馆列表
	private Button btnsearch = null;							//搜索按钮
	private Button back = null;									//返回按钮
	private ExpandTabView expandTabView = null;					//下拉选择框
	private ViewLeft viewLeft = null;							//距离下拉选择
	private ViewRight viewRight = null;							//类型下拉选择
	private ArrayList<View> mViewArray = null;					//下拉选择框数据源
	private ArrayList<ShopItem> mlist = null;					//餐馆数据源
	private ArrayList<FoodTypeItem> fstyplist = null;			//菜品种类数据源
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_activity);
		
		//获取城市数据库名称
		areaDbname=EatParams.getInstance().areaDbName;
		//下拉选择框数据源
		mViewArray = new ArrayList<View>();
		//餐馆数据源
		mlist = new ArrayList<ShopItem>();
		//菜品种类数据源
		fstyplist = new ArrayList<FoodTypeItem>();
         
        expandTabView = (ExpandTabView)findViewById(R.id.expandtab_view);		
        
        //返回
        back=(Button)findViewById(R.id.shopback);
        back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
        //餐馆列表
        shoplist=(ListView)findViewById(R.id.shopList);
        shoplist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(ShopActivity.this,StoreActivity.class);
				intent.putExtra("storename", mlist.get(position).getTITLE());
				intent.putExtra("storecaixi", mlist.get(position).getMCLSN());
				intent.putExtra("storetel", mlist.get(position).getTEL());
				intent.putExtra("storeaddr", mlist.get(position).getADDR());
				intent.putExtra("vid", mlist.get(position).getVID());
				startActivity(intent);
			}
		});
        
        //搜索
        btnsearch = (Button)findViewById(R.id.research);
        btnsearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
        
        //获取数据
        getNearShopData();
        //初始化显示
        getview();
		
	}
	
	//初始化页面显示
	private void getview(){
		viewLeft = new ViewLeft(ShopActivity.this);
		viewRight = new ViewRight(ShopActivity.this);
		initVaule();
		initListener();
	}
	
	//初始化下拉选择框数据
	private void initVaule() {		
		mViewArray.add(viewLeft);
		mViewArray.add(viewRight);
		
		ArrayList<String> mTextArray = new ArrayList<String>();
		
		mTextArray.add("类型");
		mTextArray.add("距离");
		expandTabView.setValue(mTextArray, mViewArray);
		expandTabView.setTitle(viewLeft.getShowText(), 0);
		expandTabView.setTitle(viewRight.getShowText(), 1);
	}
	
	//初始化监听事件
	private void initListener() {
		//距离下拉框监听事件
		viewLeft.setOnSelectListener(new ViewLeft.OnSelectListener() {

			@Override
			public void getValue(String distance, String showText) {
				onRefresh(viewLeft, showText);				
			}

			@Override
			public void getdistance(String distance) {
				String ord = viewRight.getShowText().toString();
				mlist.clear();
				String type="";
				String dist;
				if(viewRight.getShowText().toString().trim().equals("类型")){
					type="";
				}else if(viewRight.getShowText().toString().trim().equals("全部")){
					type="";
				}else{
					type=viewRight.getShowText().toString().trim();
				}
				if(distance.replaceAll("[^a-zA-Z0-9]+","").equals("")){
					dist="1000000000000000";
				}else{
					dist=distance.replaceAll("[^a-zA-Z0-9]+","");
				}
				String urlServer = EatParams.getInstance().getUrlServer();
				url_class = urlServer  + "?argv={webd,-DB,"+ areaDbname +",-R-READ-PAD,"
    			        + EatParams.getInstance().mLatitude + ","
    			        + EatParams.getInstance().mLongitude + ","
    			        + dist + ","  //距离
    			        + "'0','30','','','"+ URLEncoder.encode(type) +"'}";
					
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
			
		});
		
	
		//类型下拉框监听事件
		viewRight.setOnSelectListener(new ViewRight.OnSelectListener() {

			@Override
			public void getValue(String distance, String showText) {
				onRefresh(viewRight, showText);
			}

			@Override
			public void getOrd(String ordname) {
			    mlist.clear();
			    String urlServer = EatParams.getInstance().getUrlServer();
			    String dist="";
			    String type="";
			    if(ordname.equals("全部")){
			    	type="";
			    }else{
			    	type=ordname;
			    }
			    
			    if(viewLeft.getShowText().toString().trim().replaceAll("[^a-zA-Z0-9]+","").equals("")){
			    	dist="1000000000000000";
			    }else{
			    	dist=viewLeft.getShowText().toString().trim().replaceAll("[^a-zA-Z0-9]+","");
			    }
			    url_class = urlServer  + "?argv={webd,-DB,"+areaDbname+",-R-READ-PAD,"
    			        + EatParams.getInstance().mLatitude + ","
    			        + EatParams.getInstance().mLongitude + ","
    			        + dist + ","  //距离
    			        + "'0','30','','','"+URLEncoder.encode(type)+"'}";
					
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
		});		
	}
	
	//刷新下拉框显示
	private void onRefresh(View view, String showText) {
		expandTabView.onPressBack();
		int position = getPositon(view);
		if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
			expandTabView.setTitle(showText, position);
		}
		Toast.makeText(ShopActivity.this, showText, Toast.LENGTH_SHORT).show();
	}
	
	//获取下拉框选中索引
	private int getPositon(View tView) {
		for (int i = 0; i < mViewArray.size(); i++) {
			if (mViewArray.get(i) == tView) {
				return i;
			}
		}
		return -1;
	}
	
	// 获取附近的餐馆
	public void getNearShopData() {
		areaDbname=EatParams.getInstance().areaDbName;
		String urlServer = EatParams.getInstance().getUrlServer();
		url_class = urlServer + "?argv={webd,-DB,"+areaDbname+",-R-READ-PAD,'"+EatParams.getInstance().mLatitude+"','"+EatParams.getInstance().mLongitude+"','10000000','0','50','','',''}" ;

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
					ShopItem tempRestaurant = new ShopItem();
					tempRestaurant.setICO1(attributes.getValue("ICO1"));
					tempRestaurant.setDISTAN(attributes.getValue("DISTAN"));
					tempRestaurant.setADDR(attributes.getValue("ADDR"));
					tempRestaurant.setCURPX(attributes.getValue("CURPX"));
					tempRestaurant.setCURPY(attributes.getValue("CURPY"));
					tempRestaurant.setPRAVG(attributes.getValue("PRAVG"));
					tempRestaurant.setSPTYP(attributes.getValue("SPTYP"));
					tempRestaurant.setTEL(attributes.getValue("TEL"));
					tempRestaurant.setTITLE(attributes.getValue("TITLE"));
					tempRestaurant.setMCLSN(attributes.getValue("MCLSN"));
					tempRestaurant.setID(attributes.getValue("ID"));//pid
					tempRestaurant.setVID(attributes.getValue("VID"));
					tempRestaurant.setSCAVG(attributes.getValue("SCAVG"));
					mlist.add(tempRestaurant);// 将数据添加到list中去
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
					}else if("res".equals(str)){
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
				if (msg.what == 3) { 					//获取餐馆数据成功事件
					if (result.equals("ok")) {
						eateryAdapter adapter = new eateryAdapter(ShopActivity.this, mlist);
						adapter.setEateryMenuListener(new eatery_menu_Listener(){

							@Override
							public void OnListItemClick(int position, View str) {
								String vids = mlist.get(position).getVID();
								String title = mlist.get(position).getTITLE();
								String tels = mlist.get(position).getTEL();
								Intent intent=new Intent(ShopActivity.this,FoodActivity.class);
								intent.putExtra("id", vids);
								intent.putExtra("from", "shop");
								intent.putExtra("title", title);
								startActivity(intent);
							}
						});
						shoplist.setAdapter(adapter);
					}else {
						if(errorStr.length() <= 0){
							errorStr = "未获取请求数据，请检查网络是否连接正常";
						}
						Toast.makeText(ShopActivity.this, errorStr, Toast.LENGTH_SHORT).show();
					}
				}
			}
		};
}
