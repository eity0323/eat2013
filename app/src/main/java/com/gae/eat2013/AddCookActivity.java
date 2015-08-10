package com.gae.eat2013;
/*
 * author:小容
 * version:2013-5-24
 * description:发布星级菜谱
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gae.basic.GetHttp;
import com.gae.entity.CookItem;
import com.gae.entity.EatParams;

public class AddCookActivity extends Activity {
	private String urlServer = "";				//服务器路径
	private String areaDbname = "";				//城市数据库名称
	private String url_class = "";				//请求数据路径
	private String result = "";					//请求数据返回标识
	private String errorStr = "";				//请求数据返回错误信息
	private String sid = "";					//事务id
	private ArrayList<CookItem> cooklist = null;//星级菜谱数据源
	
	private Button back;
	private Button add;
	private TextView txt1; 
	private TextView txt2;
	private TextView txt3; 
	private TextView txt4;
	private TextView txt5; 
	private TextView txt6;
	private TextView txt7; 
	private TextView txt8;
	private ImageView img1;
	private ImageView img2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcook_activity);
		
		urlServer = EatParams.getInstance().getUrlServer();
		areaDbname = EatParams.getInstance().areaDbName;
		sid = EatParams.getInstance().getSession();
		cooklist = new ArrayList<CookItem>();
		
		txt1 = (TextView)findViewById(R.id.cname);
		txt2 = (TextView)findViewById(R.id.method);
		txt3 = (TextView)findViewById(R.id.taste);
		txt4 = (TextView)findViewById(R.id.diffic);
		txt5 = (TextView)findViewById(R.id.time);
		txt6 = (TextView)findViewById(R.id.main);
		txt7 = (TextView)findViewById(R.id.sub);
		txt8 = (TextView)findViewById(R.id.step);
		//img1 = (ImageView)findViewById(R.id.upic);
		//img2 = (ImageView)findViewById(R.id.dico);
		
		//返回按钮事件
		back = (Button)findViewById(R.id.backbtn);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		//发布菜谱按钮事件
		add = (Button)findViewById(R.id.sure);
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addcooklist();
			}
		});
		
	}
	//新增菜谱
	private void addcooklist(){
		//发布菜谱的接口
		//argv={webdm,-db,eat,-sid,sid,-ADD-BCMENU,vname,material,amaterial,mark,taste,diffic,utime,detail,ico,dico}
		String vname = txt1.getText().toString();
		String main = txt6.getText().toString();
		String sub = txt7.getText().toString();
		String method = txt2.getText().toString();
		String taste = txt3.getText().toString();
		String diffic = txt4.getText().toString();
		String time = txt5.getText().toString();
		String detail = txt8.getText().toString();
		String ico = "";
		String dico = "";
		
//		if(ico.equals("") || ico == null){
//			Toast.makeText(AddCookActivity.this, "图片不能为空！", 2000).show();
//			return;	
//		}else 
		if(vname.equals("") || vname == null){
			Toast.makeText(AddCookActivity.this, "菜名不能为空！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(method.equals("") || method == null){
			Toast.makeText(AddCookActivity.this, "做法不能为空！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(taste.equals("") || taste == null){
			Toast.makeText(AddCookActivity.this, "口味不能为空！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(diffic.equals("") || diffic == null){
			Toast.makeText(AddCookActivity.this, "难度不能为空！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(time.equals("") || time == null){
			Toast.makeText(AddCookActivity.this, "时间不能为空！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(main.equals("") || main == null){
			Toast.makeText(AddCookActivity.this, "主料不能为空！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(sub.equals("") || sub == null){
			Toast.makeText(AddCookActivity.this, "辅料不能为空！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(detail.equals("") || detail == null){
			Toast.makeText(AddCookActivity.this, "步骤不能为空！", Toast.LENGTH_SHORT).show();
			return;	
//		}else if(dico.equals("") || dico == null){
//			Toast.makeText(AddCookActivity.this, "图片不能为空！", 2000).show();
//			return;	
		}
		
		url_class = urlServer+ "?argv={webdm,-DB,"+areaDbname+",-sid"+sid+",-ADD-BCMENU,"
					+URLEncoder.encode(vname)+"','"
					+URLEncoder.encode(main)+"','"
					+URLEncoder.encode(sub)+"','"
					+URLEncoder.encode(method)+"','"
					+URLEncoder.encode(taste)+"','"
					+URLEncoder.encode(diffic)+"','"
					+URLEncoder.encode(time)+"','"
					+URLEncoder.encode(detail)+"','"
					+URLEncoder.encode(ico)+"','"
					+URLEncoder.encode(dico)
					+"'}";
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
			// Log.e("TEST", "endDocument");
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
			// Log.e("TEST", "endElement");
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
			if (msg.what == 0) { // 添加菜谱成功事件
				if (result.equals("ok")) {
					Toast.makeText(AddCookActivity.this,"添加成功！", Toast.LENGTH_SHORT).show();
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
