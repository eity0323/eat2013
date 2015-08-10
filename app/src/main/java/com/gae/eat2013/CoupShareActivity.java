package com.gae.eat2013;
/*
 * author:小容
 * version:2013-5-24
 * description:妙招分享
 * */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

import com.gae.adapter.CoupShareAdapter;
import com.gae.basic.GetHttp;
import com.gae.entity.CoupShareItem;
import com.gae.entity.EatParams;

public class CoupShareActivity extends Activity{
	private String areaDbname="";  						//城市数据库名称
	private String urlServer = "";						//服务器路径
	private List<CoupShareItem> clist= null; 			//妙招数据源
	private String url_class = "";						//请求数据路径
	private String result = "";                         //返回结果
	private String errorStr = "";						//请求数据返回错误信息
	private ListView couplistview = null;               //列表
	private Button backbtn = null;
	private Button coupsharebtn = null;
	private TextView allIdeaTv = null;
	private TextView myIdeaTv = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coupshare_activity);
		
		//获取城市数据库名称
		areaDbname=EatParams.getInstance().areaDbName;
		//获取服务器路径
		urlServer = EatParams.getInstance().getUrlServer();
		//妙招数据源
		clist= new ArrayList<CoupShareItem>();
		
		//获取妙招数据
		getCoupShareData();
		
		//妙招分享
		allIdeaTv = (TextView)findViewById(R.id.all_idea);
		allIdeaTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getCoupShareData();
				coupsharebtn.setVisibility(View.VISIBLE);
				allIdeaTv.setTextColor(v.getResources().getColor(R.color.orange));
				myIdeaTv.setTextColor(v.getResources().getColor(R.color.gbco));
			}
		});
		
		//我的妙招
		myIdeaTv = (TextView)findViewById(R.id.my_idea);
		myIdeaTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getMyCoupShare();
				coupsharebtn.setVisibility(View.GONE);
				allIdeaTv.setTextColor(v.getResources().getColor(R.color.gbco));
				myIdeaTv.setTextColor(v.getResources().getColor(R.color.orange));
			}
		});
		
		//编辑妙招
		couplistview = (ListView)findViewById(R.id.couplist);
		couplistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(CoupShareActivity.this,IdeaDetailActivity.class);
				intent.putExtra("title", clist.get(position).getTITLE());
				intent.putExtra("content", clist.get(position).getCONTEXT());
				intent.putExtra("id", clist.get(position).getId());
				intent.putExtra("user", clist.get(position).getUNM());
				intent.putExtra("good",clist.get(position).getGOOD());
				intent.putExtra("bad", clist.get(position).getBAD());
				intent.putExtra("isupdate", "true");
				startActivity(intent);
			}
		});
		
		//返回
		backbtn = (Button)findViewById(R.id.foodback);
		backbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		//妙招分享
		coupsharebtn = (Button)findViewById(R.id.btnsharecoup);
		coupsharebtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CoupShareActivity.this,PublishFoodActivity.class);
				startActivity(intent);
			}
		});
	}
	
	//获取我的妙招
	public void getMyCoupShare(){
		clist = new ArrayList<CoupShareItem>();
		CoupShareAdapter adapter = new CoupShareAdapter(CoupShareActivity.this, clist);
		couplistview.setAdapter(adapter);
	}
	
	//获取妙招数据
	public void getCoupShareData(){
		url_class = urlServer+"?argv={webdm,-db,"+ areaDbname +",-G-MSG,10,0,'',''}";

		new Thread(){
			public void run() {
				saxParseXML(url_class);
				handler.sendEmptyMessage(0);				
			}
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
			String html = getHttp.contentToString(url, "utf-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			clist = new ArrayList<CoupShareItem>();
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
				CoupShareItem coup = new CoupShareItem();
				coup.setCONTEXT(attributes.getValue("CONTEXT"));//内容
				coup.setTITLE(attributes.getValue("TITLE"));  //标题
				coup.setTYPE(attributes.getValue("TYPE"));   //类型
				coup.setUNM(attributes.getValue("UNM"));   //发布人
				coup.setGOOD(attributes.getValue("GOOD")); //取接口内的字段GOOD值
				coup.setBAD(attributes.getValue("BAD"));//取接口内的字段BAD值
				coup.setId(attributes.getValue("id"));
				clist.add(coup);// 将数据添加到list中去
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
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what==0){				//获取妙招数据
				if(result.equals("ok")){
					CoupShareAdapter adapter = new CoupShareAdapter(CoupShareActivity.this, clist);
					couplistview.setAdapter(adapter);
				}else{
					if(errorStr.length() <= 0){
						errorStr = "这里还没有闪亮亮的想法喔，快来秀出你的妙招吧";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		
	};
}

