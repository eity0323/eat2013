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
import com.gae.entity.EatParams;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity{
	private String areaDbname = "";				//城市数据库名称
	private String url_class = "";				//请求数据路径		
	private String result = "";					//请求数据返回标识
	private String errorStr = "";				//请求数据返回错误信息
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);		
				
		areaDbname=EatParams.getInstance().areaDbName;
		
		final EditText regmb = (EditText)findViewById(R.id.regMb);
		regmb.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		
		//注册
		final Button no = (Button)findViewById(R.id.no);
		no.setEnabled(false);
		no.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {   //清空数据
				String regmbs = regmb.getText().toString();
				String regEx= "^1[3|4|5|8][0-9]{9}$";
				Pattern pattern = Pattern.compile(regEx);
				Matcher m= pattern.matcher(regmbs);
				if (regmbs.equals("") || regmbs == null) {
					Toast.makeText(RegisterActivity.this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
				}else{
					if(regmbs.length()!=11){
						Toast.makeText(RegisterActivity.this, "手机号输入错误！", Toast.LENGTH_SHORT).show();
					}
					else if(!(m.replaceAll("").equals(""))){
						Toast.makeText(RegisterActivity.this, "手机号格式不正确！", Toast.LENGTH_SHORT).show();
					}else{
						url_class = EatParams.getInstance().getUrlServer() + "?argv={webd,-DB,"+areaDbname+",-R-MBNEW,"+ regmbs + ",}";
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
				}
			}
		});
		
		//阅读协议
		final CheckBox checkbox = (CheckBox)findViewById(R.id.checkbox);
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					no.setEnabled(true);
					no.setBackgroundResource(R.drawable.register_btn);
				}else{
					no.setEnabled(false);
					no.setBackgroundResource(R.drawable.register_btn2);
				}
			}
		});
		
		//查看协议
		final TextView msgText = (TextView)findViewById(R.id.msgText);
		msgText.setText(Html.fromHtml("<u>" + " 我已认真阅读并接受《用户协议》" +"</u>"));
		msgText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("http://www.onpad.cn");
				Intent it  = new Intent(Intent.ACTION_VIEW,uri);
				startActivity(it);
			}
		});
		
		//返回
		Button back=(Button)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RegisterActivity.this.finish();
			}
		});
	}
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
			if ("r".equals(localName)) {
				
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
				 }
			}
		}
	}
	//异步处理数据，更新界面显示
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==0){							//注册返回成功
				if(result.equals("ok")){
					Toast.makeText(RegisterActivity.this, "初始密码已经发送，请注意查收！", Toast.LENGTH_SHORT).show();
				}else if (result.contains("-1")) {
					Toast.makeText(RegisterActivity.this, "注册失败，请重试！", Toast.LENGTH_SHORT).show();
				}else if (result.contains("-2")) {
					Toast.makeText(RegisterActivity.this, "此手机已注册！", Toast.LENGTH_SHORT).show();
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
