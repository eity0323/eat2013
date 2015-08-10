package com.gae.eat2013;

/**
 * author:小胡
 * version:2013-5-24
 * description:提交订单
 * */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.gae.basic.GetHttp;
import com.gae.control.AddCard;
import com.gae.control.AddHouse;
import com.gae.dbHelper.dbHelper;
import com.gae.entity.CarInfo;
import com.gae.entity.EatParams;
import com.gae.listener.OnChangedListener;
import com.gae.view.Alert;
import com.gae.view.SlipButtonEat;
import com.gae.view.SlipButtonPay;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PayOffActivity extends Activity {
	private String areaDbname = ""; 					// 城市数据库名称
	private String urlServer = ""; 						// 获取访问路径前端信息
	private EditText mbtext = null; 					// 手机
	private EditText addrtext = null; 					// 地址
	private String changeatby = "送餐"; 					// 送餐方式
	private String changpayby = "A"; 					// 付款方式
	final String memo = ""; 							// 备注
	private TextView paymoney = null; 					// 金额
	private Button paybtn = null; 						// 提交
	private String sid = ""; 							// sessionID
	private String url_class = ""; 						// 登陆url
	private String uid = ""; 							// 登陆用户ID
	private String username = ""; 						// 用户
	private String vname = ""; 							// 餐馆名称
	private String addre = ""; 							// 地址
	private String phone = ""; 							// 电话
	private String pids = ""; 							// 菜品id
	private String numbers = ""; 						// 菜品份数
	private Button back = null; 						// 返回
	private Button orderbtn = null; 					// 历史订单
	private int num = 0; 								// 份数
	private String result = ""; 						// 请求数据返回结果
	private String errorStr = ""; 						// 请求数据返回错误信息
	private TextView mbtv = null; 						// 帐号
	private TextView pwdtv = null; 						// 密码
	private CheckBox keeppwd = null; 					// 保存
	private Button logbtn = null; 						// 登录
	private Alert win = null; 							// 登录框
	private AddCard cart = null; 						// 购物车
	private String from = ""; 							// 跳转来源
	private AddHouse house = null; 						// 收藏
	private ArrayList<String> foodnamelist = null; 		// 菜品名称
	private dbHelper db = null; 						// 数据库处理对象
	private Cursor myCursor = null; 					// 游标
	private String usermb = "";							//用户手机号
	private String useraddr = "";						//用户地址

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payoff_activity);

		// 获取城市数据库名称
		areaDbname = EatParams.getInstance().areaDbName;
		// 获取服务器路径
		urlServer = EatParams.getInstance().getUrlServer();
		// 收藏夹
		house = new AddHouse(PayOffActivity.this);
		// 购物车菜品数据源
		foodnamelist = new ArrayList<String>();

		Intent intent = getIntent();
		from = intent.getStringExtra("from");
		mbtext = (EditText) findViewById(R.id.mb);
		addrtext = (EditText) findViewById(R.id.addr);
		paymoney = (TextView) findViewById(R.id.pay);

		mbtext.setText(EatParams.getInstance().getMb());
		addrtext.setText(EatParams.getInstance().getAddr());
		sid = EatParams.getInstance().getSession();
		uid = EatParams.getInstance().getUid();

		// 送餐方式
		boolean changeat = true;
		SlipButtonEat btneat = (SlipButtonEat) findViewById(R.id.slipbtneat);
		btneat.setChecked(changeat);
		btneat.SetOnChangedListener("changeat", new OnChangedListener() {

			@Override
			public void OnChanged(String strName, boolean CheckState) {
				if (CheckState) {
					changeatby = "送餐";

				} else {
					changeatby = "到店就餐";
				}
			}
		});

		// 付款方式
		boolean changpay = true;
		SlipButtonPay btnpay = (SlipButtonPay) findViewById(R.id.slipbtnpay);
		btnpay.setChecked(changpay);
		btnpay.SetOnChangedListener("changpay", new OnChangedListener() {

			@Override
			public void OnChanged(String strName, boolean CheckState) {
				if (CheckState) {
					changpayby = "A";

				} else {
					changpayby = "B";
				}
			}
		});

		// 付款
		paybtn = (Button) findViewById(R.id.payoffbtn);
		paybtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				payoff();
			}
		});

		// 返回
		back = (Button) findViewById(R.id.orderback);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PayOffActivity.this.finish();
			}
		});

		// 继续订餐
		orderbtn = (Button) findViewById(R.id.orderbtn);
		orderbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent in = new Intent(PayOffActivity.this, OrderActivity.class);
				startActivity(in);
				PayOffActivity.this.finish();
			}
		});

		cart = new AddCard(PayOffActivity.this);

		paymoney.setText(cart.calculateTotal_money() + "元");
		// 获得总份数
		String total = cart.totalNum();
		double t = Double.parseDouble(total);
		Double in = new Double(t);
		num = in.intValue();
	}

	//付款
	private void payoff() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		addre = addrtext.getText().toString();
		phone = mbtext.getText().toString();
		ArrayList<CarInfo> lists = EatParams.getInstance().list;
		for (int i = 0; i < lists.size(); i++) {
			String pid = lists.get(i).pid + ";";
			pids += pid;
			vname = lists.get(i).vnm;
			foodnamelist.add(lists.get(i).pname);
			int nums;
			if (i == 0) {
				nums = lists.get(i).getSnum();
				numbers = nums + "";
			} else {
				numbers += ";" + lists.get(i).getSnum();
			}
		}
		if (sid.equals("")) {
			showPupWin(back);
		} else if (addre.equals("") || addre == null) {
			Toast.makeText(PayOffActivity.this, "地址不能为空", Toast.LENGTH_SHORT).show();
			return;
		} else if (pids.equals("") || pids == null) {
			Toast.makeText(PayOffActivity.this, "您还没点菜哦！", Toast.LENGTH_SHORT).show();
			return;
		} else {
			// 提交订单
			String submitStr = urlServer + "?argv={webd,-DB," + areaDbname
					+ ",-SID," + sid + ",-EAT-NEW," + URLEncoder.encode(uid)
					+ "," + URLEncoder.encode(phone) + ","
					+ URLEncoder.encode(changeatby) + "," + num + ","
					+ URLEncoder.encode(pids) + "," + numbers + ","
					+ URLEncoder.encode(memo) + "," + URLEncoder.encode(date)
					+ "," + changpayby + ",MB" + "}";
			submitMyCarOrder(submitStr);
		}
	}

	// 提交订单
	public void submitMyCarOrder(final String orderurl) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML_carorder(orderurl);
				handler.sendEmptyMessage(0);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();

		try {
			thread.join();
		} catch (Exception ex) {
		}
	}

	/**
	 * 解析为获取登陆路径的xml
	 * 
	 * @param url
	 */
	private void saxParseXML_carorder(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "UTF-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			parser = factory.newSAXParser();
			SaxParseHandler_carorder pandler = new SaxParseHandler_carorder();
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
	class SaxParseHandler_carorder extends DefaultHandler {
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
				}else if("res".equals(str)) {
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
			if ("T".equals(localName)) {

			} else {
				str = localName;
			}
			super.startElement(uri, localName, qName, attributes);
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {							//提交订单
				if (result.equals("ok")) {
					pids = "";
					numbers = "";
					if (from != null && from != "") {
						if (from.equals("InviteNextActivity")) {
							Intent in = new Intent(PayOffActivity.this,
									InviteNextActivity.class);
							in.putStringArrayListExtra("listfood", foodnamelist);
							in.putExtra("vname", vname);
							startActivity(in);
							PayOffActivity.this.finish();
						}
					} else {
						PayOffActivity.this.finish();
						for (int i = 0; EatParams.getInstance().list.size() > i; i++) { // 收藏
							house.houseListItem(EatParams.getInstance().list
									.get(i).getVid(), (String) EatParams
									.getInstance().list.get(i).getVnm(),
									EatParams.getInstance().list.get(i)
											.getPid(), (String) EatParams
											.getInstance().list.get(i)
											.getPname(), EatParams
											.getInstance().list.get(i)
											.getPrice(),
									EatParams.getInstance().list.get(i)
											.getIco(), "SP");

						}
					}
				}else{
					if(errorStr.length() <= 0){
						errorStr = "未获取请求数据，请检查网络是否连接正常";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			} else if (msg.what == 4) {								// 登陆成功事件
				if (result.equals("ok")) {
					cart.editCar();
					win.closePopUpWindow();
					mbtext.setText(usermb);
					addrtext.setText(useraddr);
				}else{
					if(errorStr.length() <= 0){
						errorStr = "未获取请求数据，请检查网络是否连接正常";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	//弹出登录框
	public void showPupWin(View v) {
		View popView = LayoutInflater.from(PayOffActivity.this).inflate(
				R.layout.login, null);
		win = new Alert(v, popView);
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
				login(mbtv.getText().toString(), pwdtv.getText().toString());
				if (keeppwd.isChecked()) {
					keeppwd.setSelected(true);
					// 保存到数据库
					saveDBLogin(mbtv.getText().toString(), pwdtv.getText()
							.toString());
				}
			}
		});
		Button calbtn = (Button) popView.findViewById(R.id.loginCancel);
		calbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				win.closePopUpWindow();
			}
		});
	}

	// 登录
	private void login(String number, String pwd) {
		if (number.length() != 11) {
			Toast.makeText(PayOffActivity.this, "请输入正确的手机号码！", Toast.LENGTH_SHORT).show();
			return;
		}
		String urlServer = EatParams.getInstance().getUrlServer();
		url_class = urlServer + "?argv={webd,-db," + areaDbname + ",-login,"
				+ number + "," + pwd + "}";

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML_login(url_class);
				handler.sendEmptyMessage(4);
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
				} else if ("rname".equals(str)) {
					if (!data.equals("")) {
						username = data;
						EatParams.getInstance().setUsename(username);
					}
				}
			}
		}
	}

	// 保存登录数据
	private void saveDBLogin(String mb, String pwd) {
		db = new dbHelper(PayOffActivity.this);
		ContentValues cv = new ContentValues();

		myCursor = db.select();
		if (myCursor.getCount() > 0) { 					//已存在则更新
			myCursor.moveToPosition(0);
			String omb = myCursor.getString(myCursor
					.getColumnIndex(db.FIELD_MB));
			if (mb.equals("")) { 						//为空则清空记录
				db.delete(omb);
			} else {
				cv.put(db.FIELD_MB, mb);
				cv.put(db.FIELD_PWD, pwd);
				db.update(cv, omb);
			}
		} else { 										// 未存在则添加
			if (!mb.equals("")) {
				cv.put(db.FIELD_MB, mb);
				cv.put(db.FIELD_PWD, pwd);
				db.insert(cv);
			}
		}
		db.close();
	}

}
