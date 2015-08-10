package com.gae.eat2013;
/**
 * author:小容
 * version:2013-5-24
 * description:订单
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

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gae.adapter.CarListAdapter;
import com.gae.adapter.OrderAdapter;
import com.gae.basic.GetHttp;
import com.gae.control.AddCard;
import com.gae.dbHelper.dbHelper;
import com.gae.eat2013.FoodActivity.SaxParseHandler_resturant;
import com.gae.entity.CarInfo;
import com.gae.entity.EatParams;
import com.gae.entity.OrderItem;

public class OrderActivity extends Activity{
	private String areaDbname="";    								 //城市数据库名称
	private String result = "";                                      //请求数据返回标识
	private String errorStr = "";									 //请求数据返回错误信息
	private String urlServer = "";									 //服务器路径
	private ArrayList<CarInfo> temprList = null;					 //购物车订单数据源
	private String url_class = "";                                   //请求数据路径
	private ListView orderlistview = null;                           //订单列表
	private Button backbtn = null;                                   //返回
	private TextView cartbtn = null;                                 //购物车
	private TextView historybtn = null;                              //历史订单
	//private View childview = null;                                   //界面
	private Dialog loginDialog = null;                               //登录对话框
	private dbHelper db = null;                                      // 数据库处理对象
	private Cursor myCursor = null;                                  // 游标
	private String uid = "";                                         // 登陆用户ID
	private String usermb = "";                                      //用户手机
	private String useraddr = "";                                    //用户地址
	private String username = "";			                       	 //用户
	private TextView mbtv = null;                                    //手机
	private TextView pwdtv = null;                                   //密码
	private CheckBox keeppwd = null;                                 //是否保存
	private Button logbtn = null;                                    //登录
	private ArrayList<OrderItem> orderlist = null;					 //获取订单数据源
	private String sid="";											 //事务id
	private LinearLayout shopcarBottom = null;						 //结算容器
	
	private String carVid = "";										//餐馆id
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		//childview=getLayoutInflater().inflate(R.layout.order_activity,null);
		this.setContentView(R.layout.order_activity);
		
		areaDbname=EatParams.getInstance().areaDbName;
		urlServer = EatParams.getInstance().getUrlServer();
		temprList = new ArrayList<CarInfo>();
		orderlist = new ArrayList<OrderItem>();
		sid=EatParams.getInstance().getSession();
		
		orderlistview=(ListView)findViewById(R.id.orderList);
        backbtn=(Button)findViewById(R.id.backbtn);
        cartbtn = (TextView)findViewById(R.id.cartbtn);
        historybtn = (TextView)findViewById(R.id.historybtn);   
        shopcarBottom = (LinearLayout)findViewById(R.id.shopcar_bottom);
      		
        //下一步
		TextView nextstep = (TextView)findViewById(R.id.nextstep);
		nextstep.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent(OrderActivity.this,PayOffActivity.class);
				in.putExtra("from", "OrderActivity");
				startActivity(in);
			}
		});
		
		//联系餐馆
		TextView phnumber = (TextView)findViewById(R.id.phnumber);
		phnumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String telno = EatParams.getInstance().getShopmb();
				  if(telno.length() > 0){
					  String tno = telno.split(" ")[0];
					  if(tno.length() > 0){
						  Intent ient = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+ tno));//parse("tel:"+phnumber.getText().toString()));  
			              startActivity(ient);  
					  }else{
						  Toast.makeText(getApplicationContext(), "这个餐馆还没提供联系电话呢", Toast.LENGTH_LONG).show();
					  }
				  }else{
					  Toast.makeText(getApplicationContext(), "这个餐馆还没提供联系电话呢", Toast.LENGTH_LONG).show();
				  }
			}
		});
        
        //购物车订单点击事件
        cartbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getCardList();
				historybtn.setTextColor(v.getResources().getColor(R.color.gbco));
				cartbtn.setTextColor(v.getResources().getColor(R.color.orange));
			}
		});
        
        //历史订单点击事件
        historybtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shopcarBottom.setVisibility(View.GONE);
				getOrderData();
				historybtn.setTextColor(v.getResources().getColor(R.color.orange));
				cartbtn.setTextColor(v.getResources().getColor(R.color.gbco));
			}
		});
        
        //返回按钮
        backbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(OrderActivity.this,MainActivity.class);
				startActivity(intent);
				OrderActivity.this.finish();
			}
		});
        
        if(EatParams.getInstance().getSession().equals("")){
			Toast.makeText(OrderActivity.this, "没登录", Toast.LENGTH_SHORT).show();
			loginPupWin();
		}else{
			getCardList();
		}	
	}
	//获取历史订单列表
	private void getOrderData(){
		String urlServer = EatParams.getInstance().getUrlServer();
		url_class = urlServer + "?argv={webdm,-DB,"+areaDbname+",-SID,'"+ sid +"',-MY-ORDNO,'15','0',''}";
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML(url_class);
				handler.sendEmptyMessage(3);
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
				handler.sendEmptyMessage(2);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}
	
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 3 && "ok".equals(result)) {					//获取历史订单数据成功事件
				OrderAdapter adapter = new OrderAdapter(OrderActivity.this,orderlist);
				orderlistview.setAdapter(adapter);
				
			}else if(msg.what == 0 && "ok".equals(result)){				//获取购物车数据成功事件
				CarListAdapter car = new CarListAdapter(OrderActivity.this, temprList);
				orderlistview.setAdapter(car);
				
		        TextView totalnumtview = (TextView)findViewById(R.id.totalnumtview);
		        totalnumtview.setText(temprList.size() +"份");
				shopcarBottom.setVisibility(View.VISIBLE);
				
				getShopInfo(carVid);
				
			}else if(msg.what == 2 && "ok".equals(result)){				//餐馆
				
			}else if(msg.what == 1 && "ok".equals(result)){				//登录成功事件
				getCardList();
				loginDialog.dismiss();
			}else{
				if(errorStr.length() <= 0){
					errorStr = "未获取请求数据，请检查网络是否连接正常";
				}
				Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	
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
			String html = getHttp.contentToString(url,"UTF-8");

			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			parser = factory.newSAXParser();
			SaxParseHandler personHandler = new SaxParseHandler();
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
	class SaxParseHandler extends DefaultHandler {
		String str = null;
		int i=0;
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
			str = localName;
			OrderItem item = new OrderItem();
			if ("r".equals(localName)) {
				
				item.setCtime(attributes.getValue("CTIME"));
				item.setState(attributes.getValue("STATE"));
				item.setUnm(attributes.getValue("UNM"));
				item.setVname(attributes.getValue("VNAME"));
				item.setId(attributes.getValue("ID"));
				item.setTotal(attributes.getValue("TOTAL"));
				orderlist.add(item);
			}else {
				str = localName;
			}
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
				 }else if("sid".equals(str)){
					 if(!data.equals("")){
						 sid = data;					 
					 }
				 }else if("id".equals(str)){
					 if(!data.equals("")){
						 uid = data;					 
					 }
				 }
			}
		}
	}

	//获得购物车订单列表
	public void getCardList(){
		uid = EatParams.getInstance().getUid();
		if (uid.equals("")) {
			uid = EatParams.getInstance().getMyUUID().toString();
		}
		final String CardUrl= urlServer + "?argv={webd,-DB,"+areaDbname+",-SEARCH-CART,"+ uid +"}";
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML_cart(CardUrl);
				handler.sendEmptyMessage(0);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

	/**
	 * 解析为获取登陆路径的xml
	 * @param url
	 */
	private void saxParseXML_cart(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "UTF-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			temprList = new ArrayList<CarInfo>();
			parser = factory.newSAXParser();
			SaxParseHandler_cart pandler = new SaxParseHandler_cart();
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
	 * SAX解析XML的处理机制 SaxParseHandler_xml
	 */
	class SaxParseHandler_cart extends DefaultHandler {
		String str = null;

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
			temprList= new ArrayList<CarInfo>();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if ("r".equals(localName)) {
				CarInfo carinfo = new CarInfo();
				String snum = attributes.getValue("SNUM");
				int num = Integer.parseInt(snum);
				carinfo.id=(attributes.getValue("ID")) ;
				carinfo.uid=(attributes.getValue("UID"));
				carinfo.vid=(attributes.getValue("VID"));
				carinfo.vnm=(attributes.getValue("VNM"));
				carinfo.pid=(attributes.getValue("PID"));
				carinfo.pname=(attributes.getValue("PNAME"));
				carinfo.price=(attributes.getValue("PRICE"));
				carinfo.snum=(num);
				carinfo.gdiscv=(attributes.getValue("GDISCV"));
				carinfo.discv=(attributes.getValue("DISCV"));
				carinfo.ctime=(attributes.getValue("CTIME"));
				temprList.add(carinfo);//将数据添加到list中去
				
				carVid = (attributes.getValue("VID"));
				
			}else {
				str = localName;
			}
			    super.startElement(uri, localName, qName, attributes);
		} 
	 }
	
	//弹出登录
	public void loginPupWin() {
		View popView = LayoutInflater.from(OrderActivity.this).inflate(
				R.layout.login, null);
//		win = new Alert(popView);
		loginDialog = new Dialog(this);  
		loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  
		loginDialog.setContentView(popView);  
		loginDialog.show();
		mbtv = (TextView) popView.findViewById(R.id.loginMb);
		pwdtv = (TextView) popView.findViewById(R.id.loginPwd);
		keeppwd = (CheckBox) popView.findViewById(R.id.loginKeepPwd);
		logbtn = (Button) popView.findViewById(R.id.loginBtn);

		String mb = "", pwd = "";
		if (mbtv != null) {
			mb = mbtv.getText().toString();
			mb = mb.trim();
		}
		if (pwdtv != null) {
			pwd = pwdtv.getText().toString();
			pwd = pwd.trim();
		}

		mbtv.setInputType(InputType.TYPE_CLASS_NUMBER);

		logbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				login(mbtv.getText().toString(),pwdtv.getText().toString());
				if (keeppwd.isChecked()) {
					keeppwd.setSelected(true);
					// 保存到数据库
					saveDBLogin(mbtv.getText().toString(), pwdtv.getText().toString());
				}
			}
		});
		Button calbtn = (Button) popView.findViewById(R.id.loginCancel);
		calbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loginDialog.dismiss();
				OrderActivity.this.finish();
			}
		});
	}


	// 保存登录数据
	private void saveDBLogin(String mb, String pwd) {
		db=new dbHelper(OrderActivity.this);
		ContentValues cv = new ContentValues();

		myCursor=db.select();
		if(myCursor.getCount() > 0){			//已存在则更新
			 myCursor.moveToPosition(0);			 
             String omb = myCursor.getString(myCursor.getColumnIndex(db.FIELD_MB));
             if(mb.equals("")){					//为空则清空记录
            	 db.delete(omb);
             }else{
	             cv.put(db.FIELD_MB, mb);
	     		 cv.put(db.FIELD_PWD, pwd);
	             db.update(cv,omb);
             }
		}else{									//未存在则添加
			if(!mb.equals("")){
				cv.put(db.FIELD_MB, mb);
				cv.put(db.FIELD_PWD, pwd);
				db.insert(cv);
			}
		}		
		db.close();
	}
	// 登录
	private void login(String number, String pwd) {
		if (number.length() != 11) {
			Toast.makeText(OrderActivity.this, "请输入正确的手机号码！", Toast.LENGTH_SHORT).show();
			return;
		}
		String urlServer = EatParams.getInstance().getUrlServer();
		url_class = urlServer + "?argv={webd,-db,"+areaDbname+",-login," + number + ","
				+ pwd + "}";

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML_login(url_class);
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
	private void saxParseXML_login(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "UTF-8");

			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			parser = factory.newSAXParser();

			SaxParseHandler_login personHandler = new SaxParseHandler_login();
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

	class SaxParseHandler_login extends DefaultHandler {
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
			str = localName;
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
				} else if ("res".equals(str)) {
					if (!data.equals("")) {
						errorStr = data;
					}
				 }else if ("sid".equals(str)) {
					if (!data.equals("")) {
						sid = data;
						EatParams.getInstance().setSession(sid);
					}
				} else if ("id".equals(str)) {
					if (!data.equals("")) {
						uid = data;
						EatParams.getInstance().setUid(uid);
					}
				} else if ("mb".equals(str)) {
					if (!data.equals("")) {
						usermb = data;
						EatParams.getInstance().setMb(usermb);
					}
				} else if ("addr".equals(str)) {
					if (!data.equals("")) {
						useraddr = data;
						EatParams.getInstance().setAddr(useraddr);
					}
				}else if("rname".equals(str)){
					 if(!data.equals("")){
						 username = data;		
						 EatParams.getInstance().setUsename(username);
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
