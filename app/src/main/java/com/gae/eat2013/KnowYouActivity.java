package com.gae.eat2013;
/**
 * author:小胡
 * version:2013-5-24
 * description:智能配餐
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

import com.gae.basic.GetHttp;
import com.gae.entity.EatParams;
import com.gae.entity.spinneritem;
import com.gae.listener.OnChangedListener;
import com.gae.view.SlipButtonSex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class KnowYouActivity extends Activity {
	private String areaDbname = "";       				//区域
	private String url_class = "";                      //路径
	private Spinner goalSpinner = null;                 //目标
	private Spinner workSpinner = null;                 //工作强度
	private Button cateringbtn = null;                  //配餐
	private Button back = null;                         //返回 
	private EditText edtage = null;                     //年龄
	private EditText edthiht = null;                    //身高
	private EditText edtweight = null;                  //体重
	private String sex="男";                            //性别
	private SlipButtonSex sexslip = null;               //性别选择
	private String result = "";                         //返回结果
	private String errorStr = "";						//请求数据返回错误信息
	private String urlServer = "";     					//服务器
	private List<spinneritem> goalitme = null;      	//目标数据源
	private List<spinneritem> workitme = null;          //工作强度数据源

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.knowyou_activity);
		
		areaDbname = EatParams.getInstance().areaDbName; 
		urlServer = EatParams.getInstance().getUrlServer();
		goalitme = new ArrayList<spinneritem>();
		workitme = new ArrayList<spinneritem>();
		
		goalSpinner = (Spinner) findViewById(R.id.goalSpinner);
		workSpinner = (Spinner) findViewById(R.id.workSpinner);
		cateringbtn=(Button)findViewById(R.id.catering);
		edtage=(EditText)findViewById(R.id.age);
		edthiht=(EditText)findViewById(R.id.hight);
		edtweight=(EditText)findViewById(R.id.weight);
		sexslip=(SlipButtonSex)findViewById(R.id.slipbtnsex);
		back=(Button)findViewById(R.id.back);
		boolean changeat = true;
		
		//性别选择
		sexslip.setChecked(changeat);
		sexslip.SetOnChangedListener("changeat", new OnChangedListener() {

			@Override
			public void OnChanged(String strName, boolean CheckState) {
				if (CheckState) {
					sex="男";
					
				} else {
					sex="女";
				}
			}
		});	
		
		//返回
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				KnowYouActivity.this.finish();
			}
		});
		
		//获取目标数据源
		getGoalItem();           
	
		//工作情况
		workSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// 获取键的方法：mySpinner.getSelectedItem().toString()或((spinneritem)mySpinner.getSelectedItem()).getId()
				// // 获取值的方法：((spinneritem)mySpinner.getSelectedItem()).getText();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		
		//开始配餐
		cateringbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				String kdkdk=((spinneritem)goalSpinner.getSelectedItem()).getText();
//				String kdkdkke=((spinneritem)workSpinner.getSelectedItem()).getText();
				Intent intent=new Intent(KnowYouActivity.this,EateringResultActivity.class);
				intent.putExtra("age", edtage.getText().toString());
				intent.putExtra("hight", edthiht.getText().toString());
				intent.putExtra("sex", sex);
				intent.putExtra("weight", edtweight.getText().toString());
				intent.putExtra("goal", ((spinneritem)goalSpinner.getSelectedItem()).getText().toString());
				intent.putExtra("work", ((spinneritem)workSpinner.getSelectedItem()).getText().toString());
				startActivity(intent);
				
			}
		});
	}
	
	//获取体重目标数据
   public void getGoalItem(){
	   url_class = urlServer+ "?argv={webds,-DB,"+areaDbname+",-SEARCH-PARAM,'A'}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXMLGoal(url_class);
				handler.sendEmptyMessage(0);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
   }
   
   //获取工作情况数据
   public void getWorkItem(){
	   url_class = urlServer+ "?argv={webds,-DB,"+areaDbname+",-SEARCH-PARAM,'B'}";
	   Runnable runnable = new Runnable() {
		   @Override
		   public void run() {
			   saxParseXMLWork(url_class);
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
	private void saxParseXMLGoal(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "utf-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			goalitme = new ArrayList<spinneritem>();
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
			int i=0;
			if ("r".equals(localName)) {
				goalitme.add(new spinneritem(i=i+1, attributes.getValue("PNM")));
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
	private void saxParseXMLWork(String url) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			GetHttp getHttp = new GetHttp();
			String html = getHttp.contentToString(url, "utf-8");
			ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			workitme = new ArrayList<spinneritem>();
			parser = factory.newSAXParser();
			SaxParseHandlerwork pandler = new SaxParseHandlerwork();
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
	class SaxParseHandlerwork extends DefaultHandler {
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
			// Log.e("TEST", "endDocument");
		}

		/**
		 * XML标签开始时，执行此函数，读取标签名称，和标签内的属性
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			int i=0;
			if ("r".equals(localName)) {
				workitme.add(new spinneritem(i=i+1, attributes.getValue("PNM")));
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
			// Log.e("TEST", "endElement");
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
			if (msg.what == 0) { 			//获取体重目标数据成功事件
				if (result.equals("ok")) {
					ArrayAdapter<spinneritem> goaladapter = new ArrayAdapter<spinneritem>(KnowYouActivity.this,android.R.layout.simple_spinner_item, goalitme);
					goaladapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					goalSpinner.setAdapter(goaladapter);
					getWorkItem();           //获取工作强度数据源
				}else{
					if(errorStr.length() <= 0){
						errorStr = "未获取请求数据，请检查网络是否连接正常";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}else if(msg.what == 1) { 		//获取工作强度数据成功事件
				if (result.equals("ok")) {
					ArrayAdapter<spinneritem> workadapter = new ArrayAdapter<spinneritem>(KnowYouActivity.this,
							android.R.layout.simple_spinner_item, workitme);
					workadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					workSpinner.setAdapter(workadapter);
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
