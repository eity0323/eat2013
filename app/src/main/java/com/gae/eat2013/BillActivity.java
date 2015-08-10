package com.gae.eat2013;
/*
 * author:小容
 * version:2013-5-24
 * description:账单
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
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.gae.adapter.BillAdapter;
import com.gae.basic.GetHttp;
import com.gae.dbHelper.dbHelper;
import com.gae.eat2013.PayOffActivity.SaxParseHandler_login;
import com.gae.entity.BillItem;
import com.gae.entity.EatParams;
import com.gae.view.Alert;

public class BillActivity extends Activity{
	private String areaDbname = "";		  //城市数据库名称
	private String url_class = "";	      //请求数据路径
	private TextView summonney = null;    //总消费
	private TextView commonney = null;    //总充值
	private TextView savmonney = null;    //结余
	private ListView billListview = null; //帐单列表
	private String result = "";        	  //返回结果
	private String errorStr = "";		  //请求数据返回错误信息
	private String cz_count = "0"; 		  //总充值
	private String xh_count = "0"; 		  //总消费
	private String jr_count = "0"; 		  //结余
	private Button backbtn = null;        //返回
	private TextView mbtv = null;         //帐号
	private TextView pwdtv = null;        //密码
	private CheckBox keeppwd = null;      //保存密码
	private Button logbtn = null;         //登录
	private Dialog loginDialog = null;    //对话框	
	private dbHelper db= null; 			  //数据库处理对象
	private Cursor myCursor= null; 		  //游标
	private String uid=""; 				  //登陆用户ID
	private String usermb = "";           //用户手机
	private String useraddr = "";         //用户地址
	private String username = "";		  //用户
	private String sid = "";			  //事务id
	private List<BillItem> billlist=null; //个人账单数据源
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bill_activity);	
		
		//获取城市数据库名称
		areaDbname=EatParams.getInstance().areaDbName;
		//获取事务id
		sid = EatParams.getInstance().getSession();
		//个人账单数据源
		billlist=new ArrayList<BillItem>();

		summonney=(TextView)findViewById(R.id.sunmonney);
		commonney=(TextView)findViewById(R.id.commonney);
		savmonney=(TextView)findViewById(R.id.savmonney);
		
		//点击进入该月账单
		billListview=(ListView)findViewById(R.id.billList);
		billListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				int ind = billlist.size()-1-position;
				intent.putExtra("month", billlist.get(ind).getTime());
				intent.setClass(BillActivity.this, MonthBillActivity.class);
				startActivity(intent);
			}
			
		});
		//返回按钮
		backbtn = (Button)findViewById(R.id.billback);
		backbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		if(EatParams.getInstance().getSession().equals("")){
			Toast.makeText(BillActivity.this, "未登录", Toast.LENGTH_SHORT).show();
			loginPupWin();
		}else{
			getMonneyData();
		}	
	}
	
	//获取个人余额和待返金额及账单数据
	public void getMonneyData(){
		String urlServer = EatParams.getInstance().getUrlServer();
		url_class = urlServer + "?argv={webdm,-DB,"+areaDbname+",-SID,"+sid +",-ORDER-QUERY,'2013'}";
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML_list(url_class);
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
	private void saxParseXML_list(String url){
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url,"UTF-8");

			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			parser = factory.newSAXParser();

			SaxParseHandler_list personHandler = new SaxParseHandler_list();
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
	class SaxParseHandler_list extends DefaultHandler {
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
			if ("r".equals(localName)) {
				BillItem order = new BillItem();
				order.setCz_money(attributes.getValue("CZ_MONEY"));
				order.setXh_money(attributes.getValue("XH_MONEY")) ;
				order.setJy_money(attributes.getValue("JR_MONEY"));
				order.setTime(attributes.getValue("TIME"));
				order.setMode("Y");
				billlist.add(order);//将数据添加到list中去
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
				}else if ("cz_count".equals(str)) {
					if (!data.equals("")) {
						cz_count = data;
					}
				}else if ("xh_count".equals(str)) {
					if (!data.equals("")) {
						xh_count = data;
					}
				}else if ("jr_count".equals(str)) {
					if (!data.equals("")) {
						jr_count = data;
					}
				}
	 		}
	   }
	}
	//异步处理数据，更新界面显示
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==0){	
				if(result.equals("ok")){//成功获取订单数据
					String xh = xh_count.substring(1);
					summonney.setText(xh);
					commonney.setText(cz_count);
					savmonney.setText(jr_count);

					BillAdapter adapter=new BillAdapter(BillActivity.this,billlist);
					billListview.setAdapter(adapter);
				}else{
					if(errorStr.length() <= 0){
						errorStr = "未获取请求数据，请检查网络是否连接正常";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}else if (msg.what == 1) {// 登陆成功事件
				if (result.equals("ok")) {
					getMonneyData();					
					loginDialog.dismiss();
				}else{
					if(errorStr.length() <= 0){
						errorStr = "未获取请求数据，请检查网络是否连接正常";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	//登录界面
	public void loginPupWin() {
		View popView = LayoutInflater.from(BillActivity.this).inflate(
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
				BillActivity.this.finish();
			}
		});
	}


	// 保存登录数据
	private void saveDBLogin(String mb, String pwd) {
		db=new dbHelper(BillActivity.this);
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
			Toast.makeText(BillActivity.this, "请输入正确的手机号码！", Toast.LENGTH_SHORT).show();
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
				}else if ("res".equals(str)) {
					if (!data.equals("")) {
						errorStr = data;
					}
				} else if ("sid".equals(str)) {
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
}
