package com.gae.eat2013;

/**
 * author:小胡
 * version:2013-5-24
 * description:登录设置
 * */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.gae.basic.GetHttp;
import com.gae.dbHelper.dbHelper;
import com.gae.entity.EatParams;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SetLoginActivity extends Activity {
	private String areaDbname = ""; // 城市数据库名称
	private String url_class = ""; // 请求数据路径
	private LayoutInflater mLayoutInflater = null;
	private EditText oldpwd = null; // 旧密码
	private EditText newpwd = null; // 新密码
	private EditText surepwd = null; // 确认密码
	private dbHelper db = null; // 数据库处理对象
	private Cursor myCursor = null; // 游标
	private String mb = ""; // 用户电话
	private String pwd = ""; // 用户密码
	private Dialog changpwdDialog = null; // 修改密码弹出框
	private String session = ""; // 事务id
	private String uid = ""; // 用户id
	private String addr = ""; // 用户地址
	private String result = ""; // 获取数据返回状态
	private String errorStr = ""; // 请求数据

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_login);

		areaDbname = EatParams.getInstance().areaDbName;

		mLayoutInflater = getLayoutInflater();
		db = new dbHelper(SetLoginActivity.this);
		myCursor = db.select();

		//获取用户登录信息
		if (myCursor.getCount() > 0) { // 初始化设置值
			myCursor.moveToPosition(0);
			mb = myCursor.getString(myCursor.getColumnIndex(db.FIELD_MB));
			pwd = myCursor.getString(myCursor.getColumnIndex(db.FIELD_PWD));
		}
		db.close();		
		
		final View edtview = mLayoutInflater.inflate(R.layout.set_pwd_chang,
				null);
		final EditText mbtv = (EditText) findViewById(R.id.loginSetMb);
		TextView edtpwd = (TextView) findViewById(R.id.edtPwd);
		TextView forgetpwd = (TextView) findViewById(R.id.forgetPwd);
		oldpwd = (EditText) edtview.findViewById(R.id.oldpwd);
		newpwd = (EditText) edtview.findViewById(R.id.newpwd);
		surepwd = (EditText) edtview.findViewById(R.id.pwdsure);
		Button dimisbtn = (Button) edtview.findViewById(R.id.dimisbtn);
		Button pwdbtn = (Button) edtview.findViewById(R.id.pwdbtn);
		forgetpwd.setText(Html.fromHtml("<u>" + "忘记密码" + "</u>"));
		mbtv.setInputType(InputType.TYPE_CLASS_NUMBER);
		mbtv.setText(mb);

		final EditText pwdtv = (EditText) findViewById(R.id.loginSetPwd);
		pwdtv.setText(pwd);

		//登录保存
		Button saveBtn = (Button) findViewById(R.id.loginSetBtn);
		saveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String urlServer = EatParams.getInstance().getUrlServer();
				url_class = urlServer + "?argv={webd,-db," + areaDbname
						+ ",-login," + mbtv.getText() + "," + pwdtv.getText()
						+ "}";

				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						saxParseXML(url_class);
						handler.sendEmptyMessage(9);
					}
				};
				Thread thread = new Thread(runnable);
				thread.start();

			}
		});
		
		//忘记密码
		forgetpwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String mb = mbtv.getText().toString();
				if (mb != null && !mb.equals("")) {
					if (!(mb.length() == 11)) {
						Toast.makeText(SetLoginActivity.this, "手机号码格式不对！", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					final String msgurl = EatParams.getInstance()
							.getUrlServer()
							+ "?argv={webd,-DB,"
							+ areaDbname
							+ ",-FIND-PWD," + mbtv.getText().toString() + ",}";
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							saxParseXML_pwd(msgurl);
							handler.sendEmptyMessage(7);
						}
					};
					Thread thread = new Thread(runnable);
					thread.start();
				} else {
					Toast.makeText(SetLoginActivity.this, "请输入手机号码！", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		
		//修改密码
		edtpwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				changpwdDialog = new Dialog(SetLoginActivity.this);
				changpwdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				changpwdDialog.setContentView(edtview);
				changpwdDialog.show();
				WindowManager.LayoutParams params = changpwdDialog.getWindow()
						.getAttributes();
				params.width = 300;
				changpwdDialog.getWindow().setAttributes(params);
			}
		});
		
		//取消修改密码
		dimisbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changpwdDialog.dismiss();
			}
		});
		
		//确认修改密码
		pwdbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (EatParams.getInstance().getSession() == ""
						|| EatParams.getInstance().getSession() == null) {
					Toast.makeText(SetLoginActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
				} else {
					if (oldpwd.getText().toString().equals("")
							|| newpwd.getText().toString().equals("")
							|| surepwd.getText().toString().equals("")) {
						Toast.makeText(SetLoginActivity.this, "请输入完整信息！", Toast.LENGTH_SHORT)
								.show();
					} else {
						if (!newpwd.getText().toString()
								.equals(surepwd.getText().toString())) {
							Toast.makeText(SetLoginActivity.this,
									"两次输入的密码不一致！", Toast.LENGTH_SHORT).show();
						} else {
							edtpassword(oldpwd.getText().toString(), newpwd
									.getText().toString());
						}
					}
				}
			}
		});

		//返回
		Button reback = (Button) findViewById(R.id.setloginbackbtn);
		reback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	//修改密码
	protected void edtpassword(String oldpwd, String newpwd) {
		final String msgurl = EatParams.getInstance().getUrlServer()
				+ "?argv={webd,-DB," + areaDbname + ",-SID,'"
				+ EatParams.getInstance().getSession() + "',-R-PWD,'" + oldpwd
				+ "','" + newpwd + "'}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML_pwdedt(msgurl);
				handler.sendEmptyMessage(8);
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
			String html = getHttp.contentToString(url, "UTF-8");

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
			if ("r".equals(localName)) {

			} else {
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
					}
				} else if ("id".equals(str)) {
					if (!data.equals("")) {
						uid = data;
					}
				} else if ("mb".equals(str)) {
					if (!data.equals("")) {
						mb = data;

					}
				} else if ("addr".equals(str)) {
					if (!data.equals("")) {
						addr = data;

					}
				}
			}
		}
	}

	/**
	 * HTTP连接，SAX解析XML
	 * 
	 * @param url
	 * 
	 */
	private void saxParseXML_pwd(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "UTF-8");

			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			parser = factory.newSAXParser();

			SaxParseHandler_pwd personHandler = new SaxParseHandler_pwd();
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
	class SaxParseHandler_pwd extends DefaultHandler {
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
				}
			}
		}
	}

	/**
	 * HTTP连接，SAX解析XML
	 * 
	 * @param url
	 * 
	 */
	private void saxParseXML_pwdedt(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "UTF-8");

			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			parser = factory.newSAXParser();

			SaxParseHandler_pwdedt personHandler = new SaxParseHandler_pwdedt();
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
	class SaxParseHandler_pwdedt extends DefaultHandler {
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
				}
			}
		}
	}

	// 异步处理数据，更新界面显示
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 7) {				//忘记密码
				if (result.equals("ok")) {
					Toast.makeText(SetLoginActivity.this, "密码已发送到你的手机，请注意查收！",
                            Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(SetLoginActivity.this, "该手机号还未注册，请先注册！",
                            Toast.LENGTH_SHORT).show();
				}
			} else if (msg.what == 8) {				//修改密码
				if (result.equals("ok")) {
					Toast.makeText(SetLoginActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
					changpwdDialog.dismiss();
				} else {
					Toast.makeText(SetLoginActivity.this, "旧密码错误！", Toast.LENGTH_SHORT)
							.show();
				}
			} else if (msg.what == 9) {				//登录保存
				if (result.equals("ok")) {
					EatParams.getInstance().setSession(session);
					EatParams.getInstance().setAddr(addr);
					EatParams.getInstance().setMb(mb);
					EatParams.getInstance().setUid(uid);
					Toast.makeText(SetLoginActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
					saveLoginSet();
					finish();
				} else {
					if (result.contains("2")) { // 用户名有误
						Toast.makeText(SetLoginActivity.this, "用户名错误！", Toast.LENGTH_SHORT)
								.show();
					}else if (result.contains("-6")) { // 密码有误
						Toast.makeText(SetLoginActivity.this, "个人密码错误！", Toast.LENGTH_SHORT)
								.show();
					}else{
						if(errorStr.length() <= 0){
							errorStr = "登录失败";
						}
						Toast.makeText(SetLoginActivity.this, errorStr,
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	};

	// 保存登录设置
	private void saveLoginSet() {
		String mb = "", pwd = "";
		EditText mbtxt = (EditText) findViewById(R.id.loginSetMb);
		if (mbtxt != null) {
			mb = mbtxt.getText().toString();
			mb = mb.trim();
		}
		EditText pwdtxt = (EditText) findViewById(R.id.loginSetPwd);
		if (pwdtxt != null) {
			pwd = pwdtxt.getText().toString();
			pwd = pwd.trim();
		}
		String tstr = mbtxt.getText().toString().trim();
		if (tstr.length() != 11) {
			Toast.makeText(SetLoginActivity.this, "请输入正确的手机号码！", Toast.LENGTH_SHORT).show();
			return;
		}

		saveDBLogin(mb, pwd);
	}

	// 保存登录数据，mb手机，pwd密码
	private void saveDBLogin(String mb, String pwd) {
		db = new dbHelper(SetLoginActivity.this);
		ContentValues cv = new ContentValues();

		myCursor = db.select();
		if (myCursor.getCount() > 0) { // 已存在则更新
			myCursor.moveToPosition(0);
			String omb = myCursor.getString(myCursor
					.getColumnIndex(db.FIELD_MB));
			if (mb.equals("")) { // 为空则清空记录
				db.delete(omb);
			} else {
				cv.put(db.FIELD_MB, mb);
				cv.put(db.FIELD_PWD, pwd);
				db.update(cv, omb);
			}
		} else { // 未存在则添加
			if (!mb.equals("")) {
				cv.put(db.FIELD_MB, mb);
				cv.put(db.FIELD_PWD, pwd);
				db.insert(cv);
			}
		}
		db.close();
	}
}
