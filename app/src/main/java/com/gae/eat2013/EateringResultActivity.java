package com.gae.eat2013;
/*
 * author:小胡
 * version:2013-5-24
 * description:智能配餐
 * */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EateringResultActivity extends Activity{                          //智能配餐结果
	private String areaDbname=EatParams.getInstance().areaDbName;              //区域
	private String result="";                                                  //返回结果
	private String mgsresult="";                                               //返回信息
	private String saveUrl = "";											   //请求数据路径
	private String errorStr = "";											   //请求数据返回错误信息
	private String age = "";                                                   //年龄
	private String hight = "";                                                 //身高
	private String sex = "";                                                   //性别
	private String weight = "";                                                //体重
	private String goal = "";                                                  //目标
	private String work = "";                                                  //工作强度
	private TextView eatmorning = null;                                        //早餐
	private TextView eatnoontime = null;                                       //中餐
	private TextView eatafternoon = null;                                      //晚餐
	private Button savebtn = null;                                             //保存
	private Button afreshbtn = null;                                           //重新配餐
	private Button back = null;                                                //返回
	private LinearLayout layoutday;                                            //周期容器
	private String fno;                                                        //ID
	private String mondaymorn;                                                 //周一早餐配餐结果
	private String mondaynoon;                                                 //周一中餐配餐结果 
	private String mondaynine;                                                 //周一晚餐配餐结果
	private String tuesdaymorn;
	private String tuesdaynoon;
	private String tuesdaynine;
	private String wednesdaymorn;
	private String wednesdaynoon;
	private String wednesdaynine;
	private String thursdaymorn;
	private String thursdaynoon;
	private String thursdaynine;
	private String fridaymorn;
	private String fridaynoon;
	private String fridaynine;
	private String saturdaymorn;
	private String saturdaynoon;
	private String saturdaynine;
	private String sundaymorn;
	private String sundaynoon;
	private String sundaynine;
	/**
	 * 按钮列表
	 */
	private TextView[] listBtns;
	private String urlServer = EatParams.getInstance().getUrlServer();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eateringresult_activity);		

		Intent in=getIntent();
		age=in.getStringExtra("age");
		hight=in.getStringExtra("hight");
		sex=in.getStringExtra("sex");
		weight=in.getStringExtra("weight");
		goal=in.getStringExtra("goal");
		work=in.getStringExtra("work");
		
		eatmorning=(TextView)findViewById(R.id.morning);
		eatnoontime=(TextView)findViewById(R.id.noontime);
		eatafternoon=(TextView)findViewById(R.id.afternoon);
		savebtn=(Button)findViewById(R.id.saveresutl);
		afreshbtn=(Button)findViewById(R.id.starteat);
		back=(Button)findViewById(R.id.back);
		
		layoutday = (LinearLayout) findViewById(R.id.daybtn);
		String[] start_btn_arr = getResources().getStringArray(
				R.array.eat_day_list);
		listBtns = new TextView[start_btn_arr.length];
		
		for (int i = 0, j = start_btn_arr.length; i < j; i++) {   //一周列表
			TextView tpBtn = new TextView(this, null);
			tpBtn.setText(start_btn_arr[i]);
			tpBtn.setTextSize(25);
			tpBtn.setWidth(45);
			tpBtn.setHeight(65);
			tpBtn.setGravity(Gravity.CENTER_HORIZONTAL);
			tpBtn.setTextColor(0xff000000);
			tpBtn.setBackgroundColor(Color.parseColor("#F5F5F5"));
			tpBtn.setId(i);
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			param.topMargin = 0;
			layoutday.addView(tpBtn, param);
			tpBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SwitchActivity(v.getId());
					// 先把所有button 恢复到原来的颜色
					TextView tbtn;
					for (int i = 0; i < listBtns.length; i++) {
						tbtn = listBtns[i];
						tbtn.setBackgroundColor(Color.parseColor("#F5F5F5"));
					}
					// 再设置当前选中的颜色#FF9912
					v.setBackgroundColor(Color.parseColor("#FF9912"));
					v.setSelected(true);
				}
			});
			listBtns[i] = tpBtn;
		}
		listBtns[0].setBackgroundColor(Color.parseColor("#FF9912"));
		
		//重新配餐
		afreshbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		
		//返回
       back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
       
       //保存结果
       savebtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveeatering();
			}
		});
       
       //切换每天配餐
       SwitchActivity(0);
       
       //获取配餐记录
	   getDayEatInfo();

	}
	
	//切换每天配餐安排
	public void SwitchActivity(int id){
		if(id==0){
			eatmorning.setText(mondaymorn);
			eatnoontime.setText(mondaynoon);
			eatafternoon.setText(mondaynine);
		}else if(id==1){
			eatmorning.setText(tuesdaymorn);
			eatnoontime.setText(tuesdaynoon);
			eatafternoon.setText(tuesdaynine);
		}else if(id==2){
			eatmorning.setText(wednesdaymorn);
			eatnoontime.setText(wednesdaynoon);
			eatafternoon.setText(wednesdaynine);
		}else if(id==3){
			eatmorning.setText(thursdaymorn);
			eatnoontime.setText(thursdaynoon);
			eatafternoon.setText(thursdaynine);
		}else if(id==4){
			eatmorning.setText(fridaymorn);
			eatnoontime.setText(fridaynoon);
			eatafternoon.setText(fridaynine);
		}else if(id==5){
			eatmorning.setText(saturdaymorn);
			eatnoontime.setText(saturdaynoon);
			eatafternoon.setText(saturdaynine);
		}else if(id==6){
			eatmorning.setText(sundaymorn);
			eatnoontime.setText(sundaynoon);
			eatafternoon.setText(sundaynine);
		}
	}
	
	//保存配餐安排
	public void saveeatering(){
		String sid=EatParams.getInstance().getSession();
		if(sid.equals("")){
			Toast.makeText(EateringResultActivity.this, "您没登录！不能保存。", Toast.LENGTH_SHORT).show();
			return;
		}
		if(fno==null||fno==""){
			Toast.makeText(EateringResultActivity.this, "没有数据保存。", Toast.LENGTH_SHORT).show();
			return;
		}else{
			saveUrl = urlServer + "?argv={webds,-DB,"+areaDbname+",-sid,'"+sid+"',-SAVE-BPANTRY,'"+fno+"'}";
					
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					saxParseXML(saveUrl);
					handler.sendEmptyMessage(1);
				}
			};
			Thread thread = new Thread(runnable);
			thread.start();
		}
	}
	
	//获取配餐数据
	public void getDayEatInfo(){
		
		saveUrl = urlServer + "?argv={webds,-DB,"+areaDbname+",-FIND-BPANTRY,'"+
				URLEncoder.encode(age)+"','"+URLEncoder.encode(hight)+"','"+URLEncoder.encode(sex)+"','"+
				URLEncoder.encode(weight)+"','"+URLEncoder.encode(goal)+"','"+URLEncoder.encode(work)+"'}";
				
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				saxParseXML(saveUrl);
				handler.sendEmptyMessage(0);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}
	
	//解析数据
	public void saxParseXML(String url){
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
                fno=attributes.getValue("FNO");
                mondaymorn=attributes.getValue("MO_MORNING");
                mondaynoon=attributes.getValue("MO_NOONTIME");
                mondaynine=attributes.getValue("MO_AFTERNOON");

                tuesdaymorn=attributes.getValue("TU_MORNING");
                tuesdaynoon=attributes.getValue("TU_NOONTIME");
                tuesdaynine=attributes.getValue("TU_AFTERNOON");

			    wednesdaymorn=attributes.getValue("WE_MORNING");
			    wednesdaynoon=attributes.getValue("WE_NOONTIME");
			    wednesdaynine=attributes.getValue("WE_AFTERNOON");

			    thursdaymorn=attributes.getValue("TH_MORNING");
			    thursdaynoon=attributes.getValue("TH_NOONTIME");
			    thursdaynine=attributes.getValue("TH_AFTERNOON");

			    fridaymorn=attributes.getValue("FR_MORNING");
			    fridaynoon=attributes.getValue("FR_NOONTIME");
			    fridaynine=attributes.getValue("FR_AFTERNOON");
			   
			    saturdaymorn=attributes.getValue("SA_MORNING");
			    fridaynoon=attributes.getValue("SA_NOONTIME");
			    fridaynine=attributes.getValue("SA_AFTERNOON");
			    
			    sundaymorn=attributes.getValue("SU_MORNING");
			    sundaynoon=attributes.getValue("SU_NOONTIME");
			    sundaynine=attributes.getValue("SU_AFTERNOON");
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
				}else if("msg".equals(str)){
					if (!data.equals("")) {
						mgsresult = data;
					}
				}
			}
	    }
	}
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==0){						//获取配餐数据
				if(result.equals("ok")){
					if(mgsresult==""||mgsresult==null){
						eatmorning.setText(mondaymorn);
						eatnoontime.setText(mondaynoon);
						eatafternoon.setText(mondaynine);
					}else{
						Toast.makeText(EateringResultActivity.this, mgsresult, Toast.LENGTH_SHORT).show();
					}
				}
			}else if(msg.what==1){					//保存配餐结果
				if(result.equals("ok")){
				
					Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
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
