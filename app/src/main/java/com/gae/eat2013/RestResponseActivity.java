package com.gae.eat2013;
/**
 * author:小容
 * version:2013-5-24
 * description:餐馆应答
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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gae.adapter.RestResponseAdapter;
import com.gae.basic.GetHttp;
import com.gae.entity.BookInfo;
import com.gae.entity.EatParams;

public class RestResponseActivity extends Activity {
	
	private String urlServer = "";				//服务器路径
	private String areaDbname="";				//城市数据库名称
	private String url_class = "";				//请求数据路径
	private String result = "";					//请求数据返回标识
	private String errorStr = "";				//请求数据返回错误信息
	private String sid="";						//事务id
	private ArrayList<BookInfo> booklist = null;//餐馆应答数据源
	private Button bookback= null;				//返回
	private ListView listView= null;			//列表对象
	private TextView txtname= null;				//标题

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rest_response_activity);
		
		urlServer = EatParams.getInstance().getUrlServer();
		areaDbname=EatParams.getInstance().areaDbName;
		sid=EatParams.getInstance().getSession();
		booklist = new ArrayList<BookInfo>();
		
		//获取预订数据
		getbookorder();
		
		txtname = (TextView)findViewById(R.id.txtname);
		txtname.setText("餐馆应答");
		
		//返回
		bookback = (Button)findViewById(R.id.bookback);
		bookback.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		//预订数据列表
		listView = (ListView)findViewById(R.id.bookedlist);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(RestResponseActivity.this);
				builder.setTitle("操作提示").setMessage("您确认接下订单【"+booklist.get(position).getReqinfo()+"】吗？")
				.setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String id = booklist.get(position).getId();
						restresponse(id);
					}
				}).setNegativeButton("取消", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
			
		});
	}
	
	//餐馆接单
	private void restresponse(String id){
		url_class = urlServer+ "?argv={webdm,-DB,"+areaDbname+",-sid,"+sid+",-EDIT-RQUIRE,'"+URLEncoder.encode(id)+"',''}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXMLResponse(url_class);
				handler.sendEmptyMessage(1);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}
	
	//订单列表
	private void getbookorder() {
		url_class = urlServer+ "?argv={webdm,-DB,"+areaDbname+",-sid,"+sid+",-RQUIRE-QUERY,'9999','0','','',''}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXMLResponse(url_class);
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
	private void saxParseXMLResponse(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "utf-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			booklist = new ArrayList<BookInfo>();
			parser = factory.newSAXParser();
			SaxParseHandlerResponse pandler = new SaxParseHandlerResponse();
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
	class SaxParseHandlerResponse extends DefaultHandler {
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
			if (msg.what == 0) { 						// 获取预订订单数据成功
				if (result.equals("ok")) {				
					RestResponseAdapter book = new RestResponseAdapter(RestResponseActivity.this, booklist);
					listView.setAdapter(book);
				}else{
					if(errorStr.length() <= 0){
						errorStr = "未获取请求数据，请检查网络是否连接正常";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}else if(msg.what == 1){					//餐馆接单成功
				if (result.equals("ok")){
					Toast.makeText(RestResponseActivity.this,"接单成功！", Toast.LENGTH_SHORT).show();
					getbookorder();
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
