package com.gae.eat2013;

/**
 * author:小胡
 * version:2013-5-24
 * description:邀请好友数据
 * */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

import com.gae.basic.GetHttp;
import com.gae.control.SendMsg;
import com.gae.entity.EatParams;
import com.gae.view.MyViewGroup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class InviteNextActivity extends Activity {
	private String areaDbname = ""; 			// 区域
	private String url_class = ""; 				// 路径
	// private Button tpBtn; //邀请的好友数据
	private MyViewGroup myViewGroup = null; 	// 容器
	private String result = ""; 				// 返回结果
	private String errorStr = "";				//请求数据返回错误信息
	private String isval = ""; 					// 是否会员
	private SendMsg msend = null; 				// 发送信息
	private String phonenumber = ""; 			// 好友电话
	private String phonename = ""; 				// 好友名称
	private TextView shopfor = null; 			// 为好友选餐馆
	private TextView foodfor = null; 			// 为好友选菜
	private ArrayList<String> foodlists = null; // 为好友选择的菜品
	private String foodnames = ""; 				// 菜品名称
	private String vname = ""; 					// 餐馆名称
	/**
	 * 按钮列表
	 */
	private Button[] listBtns;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invitenext_activity);

		areaDbname = EatParams.getInstance().areaDbName;
		foodlists = new ArrayList<String>();

		Intent intent = getIntent();
		String shopname = intent.getStringExtra("shopname");
		foodlists = intent.getStringArrayListExtra("listfood");
		vname = intent.getStringExtra("vname");

		myViewGroup = (MyViewGroup) findViewById(R.id.myviewgroup);
		Button contactback = (Button) findViewById(R.id.contactback);
		Button contactnext = (Button) findViewById(R.id.contactnext);
		TextView queryfood = (TextView) findViewById(R.id.queryfood);
		TextView queryshop = (TextView) findViewById(R.id.queryshop);
		shopfor = (TextView) findViewById(R.id.shopfor);
		foodfor = (TextView) findViewById(R.id.foodfor);

		if (shopname != null && shopname != "") {
			shopfor.setText(shopname);
		} else {
			shopfor.setText("餐馆");
		}
		if (foodlists != null && foodlists != null) {
			for (int i = 0; foodlists.size() > i; i++) {
				foodnames += foodlists.get(i) + ";";
			}
			foodfor.setText(foodnames);
			shopfor.setText(vname);
		} else {
			foodfor.setText("美食");
		}

		// 返回
		contactback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InviteNextActivity.this.finish();
			}
		});
		contactview();
		msend = new SendMsg(InviteNextActivity.this);
		final List list = new ArrayList();

		// 删除选中用户
		ImageView imgdele = (ImageView) findViewById(R.id.contectdelect);
		imgdele.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				list.clear();
				boolean delable = false;
				int j = EatParams.getInstance().contactlist.size();
				for (int i = 0; i < j; i++) {
					if (!EatParams.getInstance().contactlist.get(i)
							.isIs_Checked()) {
						listBtns[i].setVisibility(View.GONE);
						list.add(i);
						delable = true;
					}
				}
				for (int i = 0; list.size() > i; i++) {
					int d = (Integer) list.get(i);
					EatParams.getInstance().contactlist.remove(d);
				}
				
				if(!delable){
					Toast.makeText(getApplicationContext(), "谁会是您不想请吃饭的呢？", Toast.LENGTH_LONG).show();
				}
				contactview();
			}
		});

		// 下一步
		contactnext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (int i = 0, j = EatParams.getInstance().contactlist.size(); i < j; i++) {
					String phnume = EatParams.getInstance().contactlist.get(i)
							.getNumber();
					String name = EatParams.getInstance().contactlist.get(i)
							.getName();
					reguser(phnume, name);
				}
				finish();
			}
		});

		// 选择菜品
		queryfood.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "尽请期待下一版本", Toast.LENGTH_LONG).show();
				return;
//				Intent intent = new Intent(InviteNextActivity.this,
//						FoodActivity.class);
//				intent.putExtra("from", "InviteNextActivity");
//				startActivity(intent);
			}
		});
		
		TextView selfood = (TextView)findViewById(R.id.foodfor);
		selfood.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "尽请期待下一版本", Toast.LENGTH_LONG).show();
				return;
			}
		});

		// 选择餐馆
		queryshop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "尽请期待下一版本", Toast.LENGTH_LONG).show();
//				if (!foodfor.getText().toString().equals("美食")) {
//					Toast.makeText(InviteNextActivity.this,
//							"您已为您的好友选了" + foodnames, 3000).show();
//				} else {
//					Intent intent = new Intent(InviteNextActivity.this,
//							ShopForActivity.class);
//					startActivity(intent);
//				}
			}
		});
		
		TextView selshop = (TextView)findViewById(R.id.shopfor);
		selshop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "尽请期待下一版本", Toast.LENGTH_LONG).show();
				return;
			}
		});
	}

	//更新选择用户
	public void contactview() {
		int i = 0;
		int j = 0;
		myViewGroup.removeAllViews();
		listBtns = null;
		listBtns = new Button[EatParams.getInstance().contactlist.size()];
		for (i = 0, j = EatParams.getInstance().contactlist.size(); i < j; i++) {
			final CheckBox tpBtn = new CheckBox(this, null);
			tpBtn.setText(EatParams.getInstance().contactlist.get(i).getName()
					.trim());
			tpBtn.setTextSize(15);
			tpBtn.setChecked(true);
			tpBtn.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));

			tpBtn.setGravity(Gravity.CENTER);
			tpBtn.setTextColor(0xff000000);
			tpBtn.setBackgroundResource(R.drawable.contactbtnoff);
			tpBtn.setId(i);
			tpBtn.setPadding(8, 3, 8, 3);
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			param.topMargin = 20;
			param.rightMargin = 10;
			param.setMargins(5, 5, 5, 5);
			myViewGroup.addView(tpBtn, param);
			tpBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						tpBtn.setBackgroundResource(R.drawable.contactbtnoff);
						EatParams.getInstance().contactlist.get(tpBtn.getId())
								.setIs_Checked(true);
					} else {
						tpBtn.setBackgroundResource(R.drawable.contactbtnon);
						String name = EatParams.getInstance().getContactlist()
								.get(tpBtn.getId()).getName();
						Toast.makeText(InviteNextActivity.this, name, Toast.LENGTH_SHORT)
								.show();
						EatParams.getInstance().contactlist.get(tpBtn.getId())
								.setIs_Checked(false);
					}
				}
			});

			listBtns[i] = tpBtn;
		}
	}
	
	@Override
	protected void onDestroy(){
		msend.release();
		super.onDestroy();
	}

	//校验用户是否已注册
	public void reguser(String phnume, String name) {
		phonenumber = "";
		phonename = "";
		phonenumber = phnume;
		phonename = name;
		String urlServer = EatParams.getInstance().getUrlServer();
		url_class = urlServer + "?argv={webdm,-DB," + areaDbname
				+ ",-is-reguser," + phnume + "}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML(url_class);
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
	private void saxParseXML(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "utf-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
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
			isval = "";
			if ("r".equals(localName)) {
				isval = attributes.getValue("isval");
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
			if (msg.what == 0) { // 校验用户是否注册
				if (result.equals("ok")) {
					String msgcontact = "";
					SimpleDateFormat sDateFormat = new SimpleDateFormat(
							"yyyy-MM-dd hh:mm");
					String date = sDateFormat.format(new java.util.Date());
					if (isval.equals("Y")) {
						if (foodfor.getText().toString().equals("美食")) {
							if (shopfor.getText().toString().equals("餐馆")) {
								msgcontact = "您的好友"
										+ EatParams.getInstance().getUsename()
										+ "在吾参网上邀请您" + date + "一起去吃饭，敬请赏光！";
							} else {
								msgcontact = "您的好友"
										+ EatParams.getInstance().getUsename()
										+ "在吾参网上邀请您" + date + "一起去"
										+ shopfor.getText().toString()
										+ "吃饭，敬请赏光！";
							}
							Toast.makeText(InviteNextActivity.this,
									"邀请函已发送给" + phonename, Toast.LENGTH_SHORT).show();
							// msend.send(phonenumber, msgcontact);
						} else {
							msgcontact = "您的好友"
									+ EatParams.getInstance().getUsename()
									+ "在吾参网上为您点了"
									+ foodfor.getText().toString()
									+ ",大约30分钟左右送到！";
							msend.send(phonenumber, msgcontact);
							Toast.makeText(InviteNextActivity.this,
									"邀请函已发送给" + phonename, Toast.LENGTH_SHORT).show();
						}
					} else {
						if (foodfor.getText().toString().equals("美食")) {
							if (shopfor.getText().toString().equals("餐馆")) {
								msgcontact = "您的好友"
										+ EatParams.getInstance().getUsename()
										+ "在吾参网(http://www.onpad.cn/app/eat2013.apk)上邀请您"
										+ date + "一起去吃饭，敬请赏光！";
							} else {
								msgcontact = "您的好友"
										+ EatParams.getInstance().getUsename()
										+ "在吾参网(http://www.onpad.cn/app/eat2013.apk)上邀请您"
										+ date + "一起去"
										+ shopfor.getText().toString()
										+ "吃饭，敬请赏光！";
							}
							msend.send(phonenumber, msgcontact);
							Toast.makeText(InviteNextActivity.this,
									"邀请函已发送给" + phonename, Toast.LENGTH_SHORT).show();
						} else {
							msgcontact = "您的好友"
									+ EatParams.getInstance().getUsename()
									+ "在吾参网(http://www.onpad.cn/app/eat2013.apk)上为您点了"
									+ foodfor.getText().toString()
									+ ",大约30分钟左右送到！";
							msend.send(phonenumber, msgcontact);
							Toast.makeText(InviteNextActivity.this,
									"邀请函已发送给" + phonename, Toast.LENGTH_SHORT).show();
						}
					}

				}else{
					if(errorStr.length() <= 0){
						errorStr = "邀请出问题啦";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
}
