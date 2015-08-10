package com.gae.eat2013;
/**
 * author:小容 
 * version:2013-5-24
 * description:星级厨师
 * */
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
import android.widget.TextView;
import android.widget.Toast;

import com.gae.adapter.CookAdapter;
import com.gae.adapter.CookerAdapter;
import com.gae.basic.GetHttp;
import com.gae.entity.CookItem;
import com.gae.entity.EatParams;

//星级厨师、菜谱
public class PracticeActivity extends Activity{
	private String urlServer = "";					//服务器路径
	private String areaDbname= "";					//城市数据库名称
	private String url_class = "";					//请求数据路径
	private String result = "";						//请求数据返回标识
	private String errorStr = "";					//请求数据返回错误信息
	private ArrayList<CookItem> cooklist = null;	//星级菜谱数据源
	private ArrayList<CookItem> cookerlist = null;	//星级厨师数据源
	private Button back = null;						//返回按钮
	private Button add = null;						//发布菜谱
	private ListView listview = null; 				//列表对象
	private TextView cooker = null;					//星级厨师
	private TextView cookbook = null;				//星级菜谱
	private TextView tname = null;					//标题
	private CookerAdapter cookerAdapter = null;		//星级厨师适配器
	private CookAdapter cookAdapter = null;			//星级菜谱适配器

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.starcook_activity);
		
		urlServer = EatParams.getInstance().getUrlServer();
		areaDbname=EatParams.getInstance().areaDbName;
		cooklist = new ArrayList<CookItem>();
		cookerlist = new ArrayList<CookItem>();
		
		tname = (TextView)findViewById(R.id.name);
		
		//返回
		back = (Button)findViewById(R.id.backbtn);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		//发布菜谱
		add = (Button)findViewById(R.id.addcook);
		add.setVisibility(View.GONE);
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent();
				in.setClass(PracticeActivity.this, AddCookActivity.class);
				startActivity(in);
			}
		});
		
		//星级厨师点击事件
		cooker = (TextView)findViewById(R.id.cooker);
		cooker.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tname.setText("星级厨师");
				add.setVisibility(View.GONE);
				listview.setAdapter(null);
				cookerAdapter = new CookerAdapter(PracticeActivity.this, cookerlist);
				listview.setAdapter(cookerAdapter);
				cooker.setTextColor(v.getResources().getColor(R.color.orange));
				cookbook.setTextColor(v.getResources().getColor(R.color.gbco));
			}
		});
		
		//星级菜谱点击事件
		cookbook = (TextView)findViewById(R.id.cookbook);
		cookbook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tname.setText("星级菜谱");
				add.setVisibility(View.VISIBLE);
				listview.setAdapter(null);
				cookAdapter = new CookAdapter(PracticeActivity.this, cooklist);
				listview.setAdapter(cookAdapter);
				cookbook.setTextColor(v.getResources().getColor(R.color.orange));
				cooker.setTextColor(v.getResources().getColor(R.color.gbco));
				
				
			}
		});
		
		listview = (ListView)findViewById(R.id.cookitem);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//判断若是星级厨师，则屏蔽listview点击事件
				if(tname.getText().toString().equals("星级厨师")){
					return;
				}else{
					Intent in = new Intent();
					in.putExtra("1", cooklist.get(position).getMethod());
					in.putExtra("2", cooklist.get(position).getTaste());
					in.putExtra("3", cooklist.get(position).getDifficulty());
					in.putExtra("4", cooklist.get(position).getTime());
					in.putExtra("5", cooklist.get(position).getUnm());
					in.putExtra("6", cooklist.get(position).getUp());
					in.putExtra("7", cooklist.get(position).getDown());
					in.putExtra("8", cooklist.get(position).getMain());
					in.putExtra("9", cooklist.get(position).getSub());
					in.putExtra("10", cooklist.get(position).getDetail());
					in.putExtra("11", cooklist.get(position).getCname());					
					in.setClass(PracticeActivity.this, CookDetailActivity.class);
					startActivity(in);
				}
			}
		});
		//获取星级厨师数据
		getcookerlist();
	}
	
	//星级菜谱
	private void getcooklist(){
		url_class = urlServer+ "?argv={webdm,-DB,"+areaDbname+",-SEARCH-BCMENU,'10','0',''}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXMLCook(url_class);
				handler.sendEmptyMessage(0);
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
	private void saxParseXMLCook(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "utf-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			cooklist = new ArrayList<CookItem>();
			parser = factory.newSAXParser();
			SaxParseHandlerCook pandler = new SaxParseHandlerCook();
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
	class SaxParseHandlerCook extends DefaultHandler {
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
				CookItem cook = new CookItem();
				cook.setCname(attributes.getValue("VNAME"));
				cook.setDetail(attributes.getValue("DETAIL"));
				cook.setDico(attributes.getValue("DICO"));
				cook.setDifficulty(attributes.getValue("DIFFIC"));
				cook.setDown(attributes.getValue("BDA"));
				cook.setIco(attributes.getValue("ICO"));
				cook.setId(attributes.getValue("DNO"));
				cook.setMain(attributes.getValue("MATERIAL"));
				cook.setMethod(attributes.getValue("MARK"));
				cook.setSub(attributes.getValue("AMATERIAL"));
				cook.setTaste(attributes.getValue("TASTE"));
				cook.setTime(attributes.getValue("UTIME"));
				cook.setUnm(attributes.getValue("UNM"));
				cook.setUp(attributes.getValue("GOOD"));
				cooklist.add(cook);
			}else{
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
	
	//星级厨师
	private void getcookerlist(){
		url_class = urlServer+ "?argv={webdm,-DB,"+areaDbname+",-SEARCH-CHEF,'10','0',''}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXMLCooker(url_class);
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
	private void saxParseXMLCooker(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "utf-8");
			cookerlist = new ArrayList<CookItem>();
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			parser = factory.newSAXParser();
			SaxParseHandlerCooker pandler = new SaxParseHandlerCooker();
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
	class SaxParseHandlerCooker extends DefaultHandler {
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
				CookItem cook = new CookItem();
				cook.setDown(attributes.getValue("BAD"));
				cook.setUnm(attributes.getValue("UNM"));
				cook.setUp(attributes.getValue("GOOD"));
				cook.setUico(attributes.getValue("ICO"));
				cook.setNum(attributes.getValue("VNAME"));
				cook.setStar(attributes.getValue("STARC"));
				cookerlist.add(cook);
			}else{
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
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) { 					// 星级菜谱
				if (result.equals("ok")) {
//					listview.setAdapter(null);
//					cookAdapter = new CookAdapter(PracticeActivity.this, cooklist);
//					listview.setAdapter(cookAdapter);
				}else{
					if(errorStr.length() <= 0){
						errorStr = "未获取请求数据，请检查网络是否连接正常";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}else if (msg.what == 1) { 				// 星级厨师
				if (result.equals("ok")) {
					cookerAdapter = new CookerAdapter(PracticeActivity.this, cookerlist);
					listview.setAdapter(cookerAdapter);
					getcooklist();
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
