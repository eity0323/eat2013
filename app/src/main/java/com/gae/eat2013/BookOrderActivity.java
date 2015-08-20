package com.gae.eat2013;
/*
 * author:小容
 * version:2013-5-24
 * description:预定下单
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

import com.gae.adapter.BookAdapter;
import com.gae.basic.GetHttp;
import com.gae.entity.BookInfo;
import com.gae.entity.EatParams;

public class BookOrderActivity extends Activity{
    public static Dialog dialog = null;			//弹出框
	private String urlServer = null;			//服务器路径
	private String areaDbname = null;			//城市数据库名称
	private String url_class = "";				//请求数据路径
	private String errorStr = "";				//请求数据返回错误信息
	private String result = "";					//请求数据返回标识
	private String sid= "";						//事务id
	private ArrayList<BookInfo> booklist = null;//预定下单数据源
	private Button bookback = null;				//返回按钮
	private Button bookbtn = null;				//确认按钮
	private ListView listView = null;			//数据列表

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_activity);	
		
		//获取服务器路径
		urlServer = EatParams.getInstance().getUrlServer();
		//获取城市数据库名称
		areaDbname=EatParams.getInstance().areaDbName;
		//获取事务id
		sid=EatParams.getInstance().getSession();
		//列表数据源
		booklist = new ArrayList<BookInfo>();
		
		//获取预订下单数据
		getbookinfo();

		bookbtn = (Button)findViewById(R.id.booksure);
		bookback = (Button)findViewById(R.id.bookback);
		bookbtn.setOnClickListener(new MyBtnListener());
		bookback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		//预订下单列表
		listView = (ListView)findViewById(R.id.bookedlist);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(BookOrderActivity.this);
				if (booklist.get(position).getAwstate().equals("N")) {

					builder.setTitle("操作提示").setItems(new String[] {"编辑"}, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent();
							intent.putExtra("type", "update");
			            	intent.putExtra("time",booklist.get(position).getReqtime());
			            	intent.putExtra("addr", booklist.get(position).getAddr());
			            	intent.putExtra("infoname", booklist.get(position).getReqinfo());
			            	intent.putExtra("mb", booklist.get(position).getMb());
			            	intent.putExtra("price", booklist.get(position).getPrice());
			            	intent.putExtra("id", booklist.get(position).getId());
			            	
			            	intent.setClass(BookOrderActivity.this, AddBookOrderActionActivity.class);
			    			startActivity(intent);
						}
					});
				}else if(booklist.get(position).getAwstate().equals("Y")){
					builder.setTitle("操作提示").setItems(new String[] {"详细"}, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent();
			            	intent.putExtra("time",booklist.get(position).getReqtime());
			            	intent.putExtra("addr", booklist.get(position).getAddr());
			            	intent.putExtra("infoname", booklist.get(position).getReqinfo());
			            	intent.putExtra("mb", booklist.get(position).getMb());
			            	intent.putExtra("price", booklist.get(position).getPrice());
			            	intent.putExtra("awnm", booklist.get(position).getAwnm());
			            	
			            	intent.setClass(BookOrderActivity.this, BookDetailActivity.class);
			    			startActivity(intent);
						}
					});
				}
				builder.create().show();
			}
		});
	}

	class MyBtnListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra("type", "add");
			intent.setClass(BookOrderActivity.this, AddBookOrderActionActivity.class);
			startActivity(intent);
		}		
	}
	
	//个人预定下单列表
	private void getbookinfo() {
		url_class = urlServer+ "?argv={webdm,-DB,"+areaDbname+",-sid,"+ sid +",-RQUIRE-QUERY,'10','0',''}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXMLbook(url_class);
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
	private void saxParseXMLbook(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "utf-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			booklist = new ArrayList<BookInfo>();
			parser = factory.newSAXParser();
			SaxParseHandlerbook pandler = new SaxParseHandlerbook();
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
	class SaxParseHandlerbook extends DefaultHandler {
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
				BookInfo tempBook = new BookInfo();
				tempBook.setId(attributes.getValue("id"));
				tempBook.setAddr(attributes.getValue("ADDR"));
				tempBook.setMb(attributes.getValue("MB"));
				tempBook.setReqinfo(attributes.getValue("REQINFO"));
				tempBook.setReqtime(attributes.getValue("REQTIME"));
				tempBook.setUnm(attributes.getValue("UNM"));
				tempBook.setPrice(attributes.getValue("PRICE"));
				tempBook.setAwstate(attributes.getValue("AWSTATE"));
				tempBook.setAwnm(attributes.getValue("AWNM"));
				tempBook.setAwtime(attributes.getValue("AWTIME"));
				booklist.add(tempBook);
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
				}else if("res".equals(str)) {
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
			if (msg.what == 0) { //获取订单列表成功
				if (result.equals("ok")) {
					BookAdapter bookAdapter = new BookAdapter(BookOrderActivity.this, booklist);
					listView.setAdapter(bookAdapter);
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
