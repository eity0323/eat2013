package com.gae.eat2013;

/**
 * author:小胡
 * version:2013-5-24
 * description:摇一摇
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

import com.gae.adapter.carfoodAdapter;
import com.gae.adapter.eateryDetailAdapter;
import com.gae.basic.GetHttp;
import com.gae.basic.ShoppingCarService;
import com.gae.control.AddCard;
import com.gae.control.AddHouse;
import com.gae.dbHelper.dbHelper;
import com.gae.entity.CarInfo;
import com.gae.entity.EatParams;
import com.gae.entity.FoodItem;
import com.gae.listener.FoodOpreateListener;
import com.gae.listener.ListChangedListener;
import com.gae.listener.ListListener;
import com.gae.listener.ShakeListener;
import com.gae.listener.StoreOpreateListener;
import com.gae.listener.ShakeListener.OnShakeListener;
import com.gae.view.Alert;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ShakeFoodActivity extends Activity {
	
	private ShakeListener mShakeListener = null;	//摇一摇监听事件
	private dbHelper db = null; 					// 数据库处理对象
	private Cursor myCursor= null; 					// 游标
	private String url_class; 						//请求数据路径
	private String result = "";						//请求数据返回标识
	private String errorStr = "";					//请求数据错误信息
	private Vibrator vibrator= null;				//震动对象
	private ArrayList<FoodItem> foodlist = null;	//菜品列表数据源
	private AddCard add = null;						//购物车
	private AddHouse house = null;					//收藏夹
	private TextView title = null;					//标题
	private ImageView back = null;					//图片
	private View childview = null;					//子对象
	private static Dialog dialog = null;			//弹出框
	private String urlServer = "";					//服务器路径
	private String areaDbname = "";					//城市数据库名称
	
	private LinearLayout moveLayout = null;
	
	// 结算弹出框
	private ListView foodlistview;
	private RadioButton btn1;
	private RadioButton btn2;
	private RadioGroup group;
	private Button deleDialog;
	private Button backs;
	private Button payonline;
	private Button paymoney;
	private TextView times;
	private EditText tele;
	private EditText address;
	private AddCard addcard;
	private carfoodAdapter carfoodadapter;// 购物车adapter

	private String addre = ""; 						// 地址
	private String phone = ""; 						// 电话
	private String daytime = "";	 				// 日期时间
	private String pids = ""; 						// 菜品id
	private String numbers = ""; 					// 菜品份数
	private String order_result = "";
	private Alert pwin; 							// 购物车弹出窗
	private View layout; 							// 弹出窗内容
	private String radioStr = "A";
	private String sid = ""; 						// sessionID
	private String uid; 							// 登陆用户ID

	// 根据用户输入数据登录
	private String session = "";
	private String useid = "";
	
	private View popView;
	private TextView mbtv;
	private TextView pwdtv;
	private CheckBox keeppwd;
	private Button logbtn;
	private Alert win;
	private String date_time;
	private String date;
	private String time;
	private DatePicker xdpdate;
	private TimePicker xtptime;
	private String usermb = "";
	private String useraddr = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		childview = getLayoutInflater().inflate(R.layout.shakefood_activity,
				null);
		this.setContentView(childview);
		
		foodlist = new ArrayList<FoodItem>();
		add = new AddCard(ShakeFoodActivity.this);
		house = new AddHouse(ShakeFoodActivity.this);
		urlServer = EatParams.getInstance().getUrlServer();
		areaDbname = EatParams.getInstance().areaDbName;
		title = (TextView) findViewById(R.id.food_top_center);
		
		moveLayout = (LinearLayout)findViewById(R.id.move_listItem);
		
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		mShakeListener = new ShakeListener(this);
		mShakeListener.setOnShakeListener(new shakeLitener());
		
		// 查询购物车数据
		add = new AddCard(ShakeFoodActivity.this);
		add.setListChangedlistener(new ListChangedListener() {

			@Override
			public void OnListItemClick(String str) {
				showMenuPopWin(childview);
			}
		});
		add.getCardItemUrl();
		
		//返回
		back = (ImageView) findViewById(R.id.food_top_left);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	//摇一摇监听事件
	private class shakeLitener implements OnShakeListener {
		@Override
		public void onShake() {
			vibrator.vibrate(500);
			String desc = "knum desc";
			String urlServer = EatParams.getInstance().getUrlServer();
			url_class = urlServer + "?argv={webd,-db," + areaDbname
					+ ",-PD-SEARCH,'" + EatParams.getInstance().mLatitude
					+ "','" + EatParams.getInstance().mLongitude
					+ "','','lknum','40','0'}";

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					saxParseXMLfood(url_class);
					handler.sendEmptyMessage(0);
				}
			};
			Thread thread = new Thread(runnable);
			thread.start();
			mShakeListener.stop();
		}
	}
	
	private void shakeFoodItem(){
		View convertView = getLayoutInflater().inflate(R.layout.eatery_detailitem, null);//LayoutInflater.from(getApplicationContext()).inflate(R.layout.eatery_detailitem, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		ImageView store = (ImageView)convertView.findViewById(R.id.storeIco);
		store.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				house.houseListItem(
						foodlist.get(0).getVID(),
						(String) foodlist.get(0).VNAME,
						foodlist.get(0).PID,
						(String) foodlist.get(0).PNAME,
						foodlist.get(0).PRICE,
						foodlist.get(0).getICO1(),
						"SP");
			}
		});
		
		Button gnext = (Button)convertView.findViewById(R.id.pay_btn);
		gnext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				add.addCarUrl(foodlist.get(0).VID,
						foodlist.get(0).VNAME,
						foodlist.get(0).PID,
						foodlist.get(0).PNAME,
						foodlist.get(0).DISC,
						foodlist.get(0).PRICE, "1");
			}
		});
//		convertView.startAnimation(Animation);
		moveLayout.addView(convertView,params);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * HTTP连接，SAX解析XML
	 * 
	 * @param url
	 * 
	 */
	private void saxParseXMLfood(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "utf-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			parser = factory.newSAXParser();
			SaxParseHandlerfood pandler = new SaxParseHandlerfood();
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
	class SaxParseHandlerfood extends DefaultHandler {
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
				FoodItem tempRestaurant = new FoodItem();
				tempRestaurant.setICO1(attributes.getValue("ICO1"));
				tempRestaurant.setCLSN(attributes.getValue("CLSN"));
				tempRestaurant.setVNAME(attributes.getValue("VNAME"));
				tempRestaurant.setVID(attributes.getValue("VID"));
				tempRestaurant.setSCALL(attributes.getValue("SCALL"));
				tempRestaurant.setRECMD(attributes.getValue("RECMD"));
				tempRestaurant.setPRICE(attributes.getValue("PRICE"));
				tempRestaurant.setPNAME(attributes.getValue("PNAME"));
				tempRestaurant.setPID(attributes.getValue("ID")); // PID
				tempRestaurant.setMCLSN(attributes.getValue("MCLSN"));
				tempRestaurant.setDISC(attributes.getValue("DISCV"));
				tempRestaurant.setUID(attributes.getValue("UID"));
				tempRestaurant.setMODE("B");

				foodlist.add(tempRestaurant);// 将数据添加到list中去
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
				}else if("res".equals(str)) {
					if (!data.equals("")) {
						errorStr = data;
					}
				}
			}
		}
	}

	// 异步处理数据，更新界面显示
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {					//获取推荐菜品
				
				shakeFoodItem();
			} else if (msg.what == 1) {				//提交订单
				if (order_result.equals("ok")) {
					pids = "";
					numbers = "";
					pwin.closePopUpWindow();
				} else if (order_result.contains("[-1]")) {
					Toast.makeText(ShakeFoodActivity.this, errorStr, Toast.LENGTH_LONG).show();
				} else if (order_result.contains("[-3]")) {
					// 登录
					showPupWin(back);
					pwin.closePopUpWindow();
				}
			} else if (msg.what == 4) { 			// 登录成功事件
				if (result.equals("ok")) {
					addcard.editCar();
					// win.closePopUpWindow();
					addcard.getCardItemUrl();
					showMenuPopWin(back);
				}
			} else {
				Toast.makeText(ShakeFoodActivity.this, result, Toast.LENGTH_SHORT).show();

			}
		}
	};

	// 登陆弹出窗内容
	public void showPupWin(View v) {
		popView = LayoutInflater.from(ShakeFoodActivity.this).inflate(
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
		if (keeppwd.isChecked()) {
			keeppwd.setSelected(true);
			// 保存到数据库
			saveDBLogin(mbtv.getText().toString(), pwdtv.getText().toString());
		}
		logbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				login(mbtv.getText().toString(), pwdtv.getText().toString());
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

	private void showMenuPopWin(View v) {

		addcard = new AddCard(ShakeFoodActivity.this);
		addcard.getCardItemUrl();// 查询购物车数据库
		layout = LayoutInflater.from(ShakeFoodActivity.this).inflate(
				R.layout.pay_dialog, null);
		if (pwin != null) {
			pwin.closePopUpWindow();
			pwin = null;
		}
		pwin = new Alert(v, layout);

		backs = (Button) layout.findViewById(R.id.back);
		deleDialog = (Button) layout.findViewById(R.id.deleDialog);
		foodlistview = (ListView) layout.findViewById(R.id.foodList);
		group = (RadioGroup) layout.findViewById(R.id.groupBtn);
		btn1 = (RadioButton) layout.findViewById(R.id.radio1);
		btn2 = (RadioButton) layout.findViewById(R.id.radio2);
		tele = (EditText) layout.findViewById(R.id.mb);
		address = (EditText) layout.findViewById(R.id.addr);
		times = (TextView) layout.findViewById(R.id.times);
		payonline = (Button) layout.findViewById(R.id.payonline);
		paymoney = (Button) layout.findViewById(R.id.paymoney);
		// 获得手机号.地址
		String mbs = EatParams.getInstance().getMb();
		String addrs = EatParams.getInstance().getAddr();
		// 设置手机，地址
		tele.setText(mbs);
		address.setText(addrs);

		String num = addcard.calculateTotal();// 计算在线支付的总金额
		String mun = addcard.calculateTotal_money();// 计算现金支付的总金额
		payonline.setText("在线支付 " + num);
		paymoney.setText("现金支付 " + mun);

		btn1.setChecked(true);// 默认选中第一个按钮

		sid = EatParams.getInstance().getSession();
		uid = EatParams.getInstance().getUid();
		addre = address.getText().toString();
		phone = tele.getText().toString();
		daytime = times.getText().toString();

		// 获得总分数
		String total = addcard.totalNum();
		double t = Double.parseDouble(total);
		Double in = new Double(t);
		final int tt = in.intValue();

		final String memo = "";
		// 获得购物车数据
		ArrayList<CarInfo> tpList = EatParams.getInstance().list;
		carfoodadapter = new carfoodAdapter(ShakeFoodActivity.this, tpList);
		carfoodadapter.setCarInfoList(new ListListener() {

			@Override
			public void OnListItemClick(String str) {
				carfoodadapter.setDataSource(EatParams.getInstance().list);
				carfoodadapter.notifyDataSetChanged();
				String num = addcard.calculateTotal();
				String mun = addcard.calculateTotal_money();// 计算现金支付的总金额
				payonline.setText("在线支付 " + num);
				paymoney.setText("现金支付 " + mun);
			}
		});
		foodlistview.setAdapter(carfoodadapter);

		if (daytime.equals("") || daytime == null) {
			SimpleDateFormat sDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			times.setText(date);
			daytime = times.getText().toString();
		}

		backs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pwin.closePopUpWindow();
				pwin = null;
			}
		});

		deleDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pwin.closePopUpWindow();

				showShoppingCar();
			}
		});
		payonline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addcard.getCardItemUrl();
				if (radioStr.equals("A")) {
					addre = address.getText().toString();
				} else {
					addre = "来餐馆就餐";
				}
				ArrayList<CarInfo> lists = EatParams.getInstance().list;
				for (int i = 0; i < lists.size(); i++) {
					String pid = lists.get(i).pid + ";";
					pids += pid;
					int nums;
					if (i == 0) {
						nums = lists.get(i).getSnum();
						numbers = nums + "";
					} else {
						numbers += ";" + lists.get(i).getSnum();
					}
				}
				if (sid.equals("")) {
					showPupWin(childview);
					pwin.closePopUpWindow();
					pwin = null;
				} else if (addre.equals("") || addre == null) {
					Toast.makeText(ShakeFoodActivity.this, "地址不能为空", Toast.LENGTH_SHORT)
							.show();
					return;
				} else if (pids.equals("") || pids == null) {
					Toast.makeText(ShakeFoodActivity.this, "菜品ID不能为空", Toast.LENGTH_SHORT)
							.show();
					return;
				} else {
					// 提交订单
					String submitStr = urlServer + "?argv={webd,-DB,"
							+ areaDbname + ",-SID," + sid + ",-EAT-NEW,"
							+ URLEncoder.encode(uid) + ","
							+ URLEncoder.encode(phone) + ","
							+ URLEncoder.encode(addre) + "," + tt + ","
							+ URLEncoder.encode(pids) + "," + numbers + ","
							+ URLEncoder.encode(memo) + ","
							+ URLEncoder.encode(daytime) + "," + "A" + ",MB"
							+ "}";
					submitMyCarOrder(submitStr);
				}
			}
		});
		paymoney.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addcard.getCardItemUrl();
				if (radioStr.equals("A")) {
					addre = address.getText().toString();
				} else {
					addre = "来餐馆就餐";
				}
				ArrayList<CarInfo> lists = EatParams.getInstance().list;
				for (int i = 0; i < lists.size(); i++) {
					String pid = lists.get(i).pid + ";";
					pids += pid;
					int nums;// = lists.get(i).getSnum()+"";
					if (i == 0) {
						nums = lists.get(i).getSnum();
						numbers = nums + "";
					} else {
						numbers += ";" + lists.get(i).getSnum();
					}
				}
				if (sid.equals("")) {
					showPupWin(childview);
					pwin.closePopUpWindow();
					pwin = null;
				} else if (addre.equals("") || addre == null) {
					Toast.makeText(ShakeFoodActivity.this, "地址不能为空", Toast.LENGTH_SHORT)
							.show();
					return;
				} else if (pids.equals("") || pids == null) {
					Toast.makeText(ShakeFoodActivity.this, "菜品ID不能为空", Toast.LENGTH_SHORT)
							.show();
					return;
				} else {
					// 提交订单
					String submitStr = urlServer + "?argv={webd,-DB,"
							+ areaDbname + ",-SID," + sid + ",-EAT-NEW,"
							+ URLEncoder.encode(uid) + ","
							+ URLEncoder.encode(phone) + ","
							+ URLEncoder.encode(addre) + "," + tt + ","
							+ URLEncoder.encode(pids) + "," + numbers + ","
							+ URLEncoder.encode(memo) + ","
							+ URLEncoder.encode(daytime) + "," + "B" + ",MB"
							+ "}";
					submitMyCarOrder(submitStr);
				}
			}
		});
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == btn1.getId()) {
					addre = address.getText().toString();
					radioStr = "A";
				} else if (checkedId == btn2.getId()) {
					addre = "来餐馆就餐";
					radioStr = "B";
				}
			}
		});

		times.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				creatDateDialog(times);
			}
		});

	}

	public void submitMyCarOrder(final String orderurl) {
		Thread tr;
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML_carorder(orderurl);
				handler.sendEmptyMessage(1);
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
						order_result = data;
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
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if ("T".equals(localName)) {
				// MessageInfo msginfo = new MessageInfo();
				// msginfo.setMbs(attributes.getValue("MB"));
				// msginfo.setMsgs(attributes.getValue("MSG"));
				// msglist.add(msginfo);// 将数据添加到list中去
			} else {
				str = localName;
			}
			super.startElement(uri, localName, qName, attributes);
		}
	}

	// 创建日期弹出窗口
	public void creatDateDialog(View v) {
		LayoutInflater inflater = ShakeFoodActivity.this.getLayoutInflater();
		View layout = inflater.inflate(R.layout.time_dialog, null);
		// 获得日期值跟时间值
		xdpdate = (DatePicker) layout.findViewById(R.id.orderdate);
		xtptime = (TimePicker) layout.findViewById(R.id.ordertime);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ShakeFoodActivity.this);
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

	// 保存登录数据
	private void saveDBLogin(String mb, String pwd) {
		db = new dbHelper(ShakeFoodActivity.this);
		ContentValues cv = new ContentValues();
		cv.put(db.FIELD_MB, mb);
		cv.put(db.FIELD_PWD, pwd);

		myCursor = db.select();
		if (myCursor.getCount() > 0) {
			myCursor.moveToPosition(0);
			String omb = myCursor.getString(myCursor
					.getColumnIndex(db.FIELD_MB));
			db.update(cv, omb);
		}
		db.close();
		Toast.makeText(ShakeFoodActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
	}

	// 登录
	private void login(String number, String pwd) {
		if (number.length() != 11) {
			Toast.makeText(ShakeFoodActivity.this, "请输入正确的手机号码！", Toast.LENGTH_SHORT).show();
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
						session = data;
						EatParams.getInstance().setSession(session);
					}
				} else if ("id".equals(str)) {
					if (!data.equals("")) {
						useid = data;
						EatParams.getInstance().setUid(useid);
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
				}
			}
		}
	}

	//显示购物车
	private void showShoppingCar() {
		Intent scar = new Intent();
		scar.setClass(getApplication(), ShoppingCarService.class);
		startService(scar);
		EatParams.getInstance().setCarShowed(true);
	}
}
