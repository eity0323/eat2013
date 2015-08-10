package com.gae.eat2013;
/**
 * author:小容 
 * version:2013-5-24
 * description:月账单情况
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

import com.gae.adapter.BillAdapter;
import com.gae.basic.GetHttp;
import com.gae.entity.BillItem;
import com.gae.entity.EatParams;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MonthBillActivity extends Activity {
	private String areaDbname = "";				//城市数据库名称
	private String url_class = "";				//请求数据路径
	private TextView summonney = null;			//总消费
	private TextView commonney = null;			//总充值
	private TextView savmonney = null;			//结余
	private ListView billListview = null;		//账单列表
	private String result = "";					//请求数据返回标识
	private String errorStr = "";				//请求数据返回错误信息
	private String cz_count = "0"; 				//总充值
	private String xh_count = "0"; 				//总消费
	private String jr_count = "0"; 				//结余
	private Button backbtn = null;				//返回
	private TextView txtname = null;			//标题
	private String sid = "";					//事务id
	private List<BillItem> billlist = null;		//账单数据源

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bill_activity);
		
		areaDbname=EatParams.getInstance().areaDbName;
		sid = EatParams.getInstance().getSession();
		billlist=new ArrayList<BillItem>();
		
		summonney=(TextView)findViewById(R.id.sunmonney);
		commonney=(TextView)findViewById(R.id.commonney);
		savmonney=(TextView)findViewById(R.id.savmonney);
		billListview=(ListView)findViewById(R.id.billList);
		txtname = (TextView)findViewById(R.id.centertext);

		Intent intent = getIntent();
		String month = intent.getStringExtra("month");
		if(month.substring(0,1).equals("0")){
			txtname.setText(month.substring(1) + "月账单");
		}else{
			txtname.setText(month + "月账单");
		}
		
		//返回
		backbtn = (Button)findViewById(R.id.billback);
		backbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		//获取月账单数据
		getMontyMonney(month);
	}
	
	//获取个人余额和待返金额数据
	public void getMontyMonney(String month){
		String urlServer = EatParams.getInstance().getUrlServer();
		url_class = urlServer + "?argv={webdm,-DB,"+areaDbname+",-SID,"+sid +",-DORDER-QUERY,'2013',"+month+"}";
		
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
				order.setMode("M");
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
				}else if ("res".equals(str)) {
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
				if(result.equals("ok")){			//成功获取订单数据
					String xh = xh_count.substring(1);
					summonney.setText(xh);
					commonney.setText(cz_count);
					savmonney.setText(jr_count);
					
					BillAdapter adapter=new BillAdapter(MonthBillActivity.this,billlist);
					billListview.setAdapter(adapter);
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
