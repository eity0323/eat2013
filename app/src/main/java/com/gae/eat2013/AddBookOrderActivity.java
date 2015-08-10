package com.gae.eat2013;
/**
 * author:小容
 * version:2013-5-24
 * description:新增、修改个人预订订单
 * */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gae.basic.GetHttp;
import com.gae.entity.BookInfo;
import com.gae.entity.EatParams;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

//新增、修改个人订单
public class AddBookOrderActivity extends Activity {
	private String areaDbname = "";					//城市数据库名称
	private String urlServer = "";					//服务器路径
	private String url_class = "";					//请求数据路径
	private static Dialog dialog = null;			//弹出对话框
	private String result = "";						//请求数据返回标识
	private String errorStr = "";					//请求数据返回错误信息
	private String sid = "";						//事务id
	private EditText editmb = null;					//手机号
	private EditText editprice = null;				//价格
	private EditText editinfo = null;				//描述
	private EditText editaddr = null;				//地址
	private Button btnAdd = null;					//添加
	private Button addbookback = null;				//返回
	private EditText times = null;					//时间
	private TextView bookOrder = null;				//文本说明
	private ArrayList<BookInfo> booklist = null;	//个人预定下单数据源
	
	private SimpleDateFormat formatter = null;        
	private Date curDate  = null;					//获取当前时间     

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addbook);
		
		//获取服务器路径
		urlServer = EatParams.getInstance().getUrlServer();
		//获取城市数据库名称
		areaDbname=EatParams.getInstance().areaDbName;
		//格式化时间对象
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//获取事务id
		sid = EatParams.getInstance().getSession();
		//获取当前时间
		curDate  = new Date(System.currentTimeMillis());
		//预订下单数据源
		booklist = new ArrayList<BookInfo>();
		
		editmb = (EditText)findViewById(R.id.usermb);
		editmb.setText(EatParams.getInstance().getMb());
		editprice = (EditText)findViewById(R.id.userprice);
		editinfo = (EditText)findViewById(R.id.userfood);
		editaddr = (EditText)findViewById(R.id.useraddr);
		editaddr.setText(EatParams.getInstance().getAddr());
		times = (EditText)findViewById(R.id.usertime);
		times.setInputType(InputType.TYPE_NULL);
		times.setFocusable(false);
		btnAdd = (Button)findViewById(R.id.addbooksure);
		bookOrder = (TextView)findViewById(R.id.bookOrder);

		//获得BookOrderActivity传来的参数
		Intent intent = getIntent();
		String type = intent.getStringExtra("type");
		String time = intent.getStringExtra("time");
		String addr = intent.getStringExtra("addr");
		String infoname = intent.getStringExtra("infoname");
		String mb = intent.getStringExtra("mb");
		String price = intent.getStringExtra("price");
		final String id = intent.getStringExtra("id");
		//判断点击的按钮是新增还是修改，设置显示信息
		if(type.equals("add")){
			bookOrder.setText("新增订单");
		}else if(type.equals("update")){
			bookOrder.setText("修改订单");
			editmb.setText(mb);
			editaddr.setText(addr);
			editinfo.setText(infoname);
			editprice.setText(price);
			times.setText(time);
		}
		//时间控件
		times.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				creatDateDialog(times);
			}
		});
		//判断是新增还是修改，并调用相应方法
		btnAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(bookOrder.getText().toString() == "新增订单"){
					adddinnerinfo();
				}else if(bookOrder.getText().toString() == "修改订单"){
					updatebookinfo(id);
				}
			}
		});
		//返回按钮事件
		addbookback = (Button)findViewById(R.id.addbookback);
		addbookback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	//新增
	private void adddinnerinfo() {
		String usermb = editmb.getText().toString();
		String userprice = editprice.getText().toString();
		String userfoodinfo = editinfo.getText().toString();
		String useraddr = editaddr.getText().toString();
		String usertime = times.getText().toString();
		Date nowtime = null;
		try {
			nowtime = formatter.parse(usertime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(usermb.equals("") || usermb == null){
			Toast.makeText(AddBookOrderActivity.this, "没有电话就联系不到你哟！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(usertime.equals("") || usertime == null){
			Toast.makeText(AddBookOrderActivity.this, "没有时间就会送得比较晚哦！", Toast.LENGTH_SHORT).show();
			return;
		}else if(curDate.after(nowtime)){
			Toast.makeText(AddBookOrderActivity.this, "送餐时间早于当前时间，您是要穿越吗", Toast.LENGTH_SHORT).show();
			return;
		}else if(useraddr.equals("") || useraddr == null){
			Toast.makeText(AddBookOrderActivity.this, "没有地址餐会送不到的哟", Toast.LENGTH_SHORT).show();
			return;
		}else if(userfoodinfo.equals("") || userfoodinfo == null){
			Toast.makeText(AddBookOrderActivity.this, "说说您想吃啥呗！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		url_class=urlServer+ "?argv={webdm,-DB,"+areaDbname+",-sid,"+sid+",-ISSUE-RQUIRE,'"
								+URLEncoder.encode(usermb)+"','"
								+URLEncoder.encode(userprice)+"','"
								+URLEncoder.encode(userfoodinfo)+"','"
								+URLEncoder.encode(useraddr)+"','"
								+URLEncoder.encode(usertime)+"'}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXMLBookOrder(url_class);
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
	private void saxParseXMLBookOrder(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "utf-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			booklist = new ArrayList<BookInfo>();
			parser = factory.newSAXParser();
			SaxParseHandlerBookOrder pandler = new SaxParseHandlerBookOrder();
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
	class SaxParseHandlerBookOrder extends DefaultHandler {
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
				
				booklist.add(tempBook);// 将数据添加到list中去
				
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
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) { 					// 新增订单
				if (result.equals("ok")) {
					Toast.makeText(AddBookOrderActivity.this,"您的订单已经发布罗", Toast.LENGTH_SHORT).show();
				}else{
					if(errorStr.length() <= 0){
						errorStr = "没有数据显示呢";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}else if(msg.what == 1){				//修改订单
				if(result.equals("ok")){
					Toast.makeText(AddBookOrderActivity.this,"已经修改好了", Toast.LENGTH_SHORT).show();
				}else{
					if(errorStr.length() <= 0){
						errorStr = "没有数据显示呢";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}
			finish();
		}
	};
	
	//修改
	private void updatebookinfo(String id){
		String usermb = editmb.getText().toString();
		String userprice = editprice.getText().toString();
		String userfoodinfo = editinfo.getText().toString();
		String useraddr = editaddr.getText().toString();
		String usertime = times.getText().toString();
		Date nowtime = null;
		try {
			nowtime = formatter.parse(usertime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(usermb.equals("") || usermb == null){
			Toast.makeText(AddBookOrderActivity.this, "没有电话就联系不到你哟！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(usertime.equals("") || usertime == null){
			Toast.makeText(AddBookOrderActivity.this, "没有时间就会送得比较晚哦！", Toast.LENGTH_SHORT).show();
			return;
		}else if(curDate.after(nowtime)){
			Toast.makeText(AddBookOrderActivity.this, "时间不对呢！", Toast.LENGTH_SHORT).show();
			return;
		}else if(useraddr.equals("") || useraddr == null){
			Toast.makeText(AddBookOrderActivity.this, "没有地址餐会送不到的哟！", Toast.LENGTH_SHORT).show();
			return;
		}else if(userfoodinfo.equals("") || userfoodinfo == null){
			Toast.makeText(AddBookOrderActivity.this, "说说您想吃啥呗！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		url_class=urlServer+ "?argv={webdm,-DB,"+areaDbname+",-sid,"+sid+",-EDIT-RQUIRE,'"
								+URLEncoder.encode(id)+"','"
								+URLEncoder.encode(usermb)+"','"
								+URLEncoder.encode(userprice)+"','"
								+URLEncoder.encode(userfoodinfo)+"','"
								+URLEncoder.encode(useraddr)+"','"
								+URLEncoder.encode(usertime)+"'}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXMLBookOrder(url_class);
				handler.sendEmptyMessage(1);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
		
	}
	
	// 创建日期弹出窗口
	private String date_time;
	private String date;
	private String time;
	private DatePicker xdpdate;
	private TimePicker xtptime;

	public void creatDateDialog(View v) {
		LayoutInflater inflater = AddBookOrderActivity.this.getLayoutInflater();
		View layout = inflater.inflate(R.layout.time_dialog, null);
		// 获得日期值跟时间值
		xdpdate = (DatePicker) layout.findViewById(R.id.orderdate);
		xtptime = (TimePicker) layout.findViewById(R.id.ordertime);
		Date te = new Date(System.currentTimeMillis());
		
		xtptime.setCurrentHour(te.getHours());
		xtptime.setCurrentMinute(te.getMinutes());
		xtptime.setIs24HourView(true);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				AddBookOrderActivity.this);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				date = String.valueOf(xdpdate.getYear()) + "-"
						+ String.valueOf(xdpdate.getMonth() + 1) + "-"
						+ String.valueOf(xdpdate.getDayOfMonth());

				time = String.valueOf(xtptime.getCurrentHour()) + ":"
						+ String.valueOf(xtptime.getCurrentMinute()) + ":00";
				date_time = date + " " + time;
				times.setText(date_time);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setView(layout);
		dialog = builder.create();
		dialog.show();
	}

}
