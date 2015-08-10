package com.gae.eat2013;
/*
 * author:小胡
 * version:2013-5-24
 * description:美食计划
 * */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.gae.adapter.EatPlanAdapter;
import com.gae.basic.GetHttp;
import com.gae.entity.EatParams;
import com.gae.entity.EatPlanItem;
import com.gae.view.EatPlanViewLeft;
import com.gae.view.EatPlanViewTyp;
import com.gae.view.EatPlanViewWeek;
import com.gae.view.ExpandTabView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EatPlanActivity extends Activity {                              
	private String areaDbname = "";											//城市数据库名称
	private String url_class = "";											//请求数据路径
	private String result = "";                                             //请求数据返回标识
	private String errorStr = "";											//请求数据返回错误信息
	private ExpandTabView expandTabView = null;                             //计划分类   
	private ExpandTabView seftTapView = null;                               //计划分类
	private ExpandTabView seftTapweek = null;                               //日期
	private View mainChild = null;                                          //主界面
	private LinearLayout llcontactview = null;                              //容器
	private TextView selfplan = null;                                       //制定计划
	private ListView planlist = null;                                       //计划列表
	private EditText title = null;                                          //标题                                                  
	private EditText infoplan = null;                                       //计划详情
	private EditText titleedit = null;                                      //标题修改
	private EditText infoplanedit = null;                                   //详情修改
	private TextView edittype = null;                                       //分类 
	private TextView editweek = null;                                       //日期
	private EatPlanViewLeft viewLeft = null;                                //分类列表
	private EatPlanViewTyp planviewtype = null;                             //分类列表
	private EatPlanViewWeek planviewweek = null;                            //日期
	private ArrayList<View> mViewArray = null;             					//分类数据源
	private ArrayList<View> mViewArrayplan = null;         					//分类数据源
	private ArrayList<View> mViewArrayweek = null;         					//周期数据源
	private ArrayList<EatPlanItem> mlist= null;      						//计划列表
	private View planview= null;                                            //初始界面
	private View seftplan= null;                                            //自制计划界面
	private View editplan= null;                                            //修改计划界面
	private int numpoit = 0;                                                //标记                                            
	private String id = "";                                                 //计划ID
	private EatPlanAdapter planAdapter = null;                              //适配器

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainChild = getLayoutInflater()
				.inflate(R.layout.eatplan_activity, null);
		this.setContentView(mainChild);
		
		areaDbname = EatParams.getInstance().areaDbName;
		mViewArray = new ArrayList<View>();
		mViewArrayplan = new ArrayList<View>();
		mViewArrayweek = new ArrayList<View>();
		mlist = new ArrayList<EatPlanItem>();

		expandTabView = (ExpandTabView) findViewById(R.id.expandtab_view);		
		llcontactview = (LinearLayout) findViewById(R.id.collectcontact);
		
		planview = getLayoutInflater().inflate(R.layout.collectlistview, null);
		seftplan = getLayoutInflater().inflate(R.layout.selfplanview, null);
		editplan = getLayoutInflater().inflate(R.layout.editplanview, null);
		planlist=(ListView)planview.findViewById(R.id.collectList);
		seftTapView = (ExpandTabView) seftplan.findViewById(R.id.expandtab_viewtyp);
		seftTapweek = (ExpandTabView)seftplan.findViewById(R.id.expandtab_viewweek);
		edittype=(TextView)editplan.findViewById(R.id.editplantyp);
		editweek=(TextView)editplan.findViewById(R.id.editplanweek);
		title=(EditText)seftplan.findViewById(R.id.plantitle);
		infoplan=(EditText)seftplan.findViewById(R.id.infoplan);
		titleedit=(EditText)editplan.findViewById(R.id.editplantitle);
		infoplanedit=(EditText)editplan.findViewById(R.id.editinfoplan);
		Button editbtn=(Button)editplan.findViewById(R.id.editbtn);
		final Button commidplan=(Button)seftplan.findViewById(R.id.commid);
		llcontactview.addView(planview);
		
		//返回
		Button eatplanback = (Button) findViewById(R.id.planctback);
		eatplanback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				EatPlanActivity.this.finish();
			}
		});
		
		//自定义美食计划
		selfplan = (TextView) findViewById(R.id.selfcollect);
		selfplan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				llcontactview.removeAllViews();
				llcontactview.addView(seftplan);
				
				planviewtype.setShowText( viewLeft.getShowText() );
			}
		});
		
		//提交我的美食计划
		commidplan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(title.getText().toString().equals("")||title.getText().toString()==null){
					Toast.makeText(EatPlanActivity.this, "您还没填写标题！", Toast.LENGTH_SHORT).show();
				}else if(infoplan.getText().toString().equals("")||infoplan.getText().toString()==null){
					Toast.makeText(EatPlanActivity.this, "您还没写详情！", Toast.LENGTH_SHORT).show();
				}else {
					commidplan();
				}
			}
		});
		
		//计划列表长按事件
		planlist.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				ShowDialog(mlist.get(arg2).getTitle(),mlist.get(arg2).getId(),arg2);
				return false;
			}
		});
		
		//计划列表点击事件
		planlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				llcontactview.removeAllViews();
				llcontactview.addView(editplan);
				edittype.setText(viewLeft.getShowText()+"                              ");
				editweek.setText(mlist.get(arg2).getWeek());
				titleedit.setText(mlist.get(arg2).getTitle());
				infoplanedit.setText(mlist.get(arg2).getDetails());
				id=mlist.get(arg2).getId();
						
			}
		});
		
		//修改计划内容
		editbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editplan();
			}
		});
		
		viewLeft = new EatPlanViewLeft(EatPlanActivity.this);
		viewLeft.setShowText("养生餐");
		
		planviewtype = new EatPlanViewTyp(EatPlanActivity.this);
//		planviewtype.setShowText( viewLeft.getShowText() );
		
		planviewweek = new EatPlanViewWeek(EatPlanActivity.this);
		
		//初始化顶部下拉选择框
		initVaule();
		//初始化美食计划中类型下拉选择框
		initVauleplan();
		//初始化美食计划中周别下拉选择框
		initVauleweek();
		//初始化顶部下拉选择框监听事件
		initListener();
		//初始化美食计划中类型下拉选择框监听事件
		initplanListener();
		//初始化美食计划中周别下拉选择框监听事件
		initweekListener();
		//获取美食计划列表
		getplanlist();
	}

	//初始化类型下拉选择组件
	private void initVaule() {
		mViewArray.add(viewLeft);

		ArrayList<String> mTextArray = new ArrayList<String>();
		mTextArray.add("类型");
		expandTabView.setValue(mTextArray, mViewArray);
		expandTabView.setTitle(viewLeft.getShowText(), 0);
	}

	//制定美食计划中类型
	private void initVauleplan() {
		mViewArrayplan.add(planviewtype);

		ArrayList<String> mTextArray = new ArrayList<String>();
		mTextArray.add("类型");
		seftTapView.setValue(mTextArray, mViewArrayplan);
		seftTapView.setTitle(planviewtype.getShowText(), 0);
	}
	
	//制定美食计划中的周别
	private void initVauleweek() {
		mViewArrayweek.add(planviewweek);

		ArrayList<String> mTextArray = new ArrayList<String>();
		mTextArray.add("类型");
		seftTapweek.setValue(mTextArray, mViewArrayweek);
		seftTapweek.setTitle(planviewweek.getShowText(), 0);
	}

	//初始化下拉框监听事件
	private void initListener() {
		viewLeft.setOnSelectListener(new EatPlanViewLeft.OnSelectListener() {
			@Override
			public void getValue(String distance, String showText) {
				onRefresh(viewLeft, showText);
			}

			@Override
			public void getdistance(String distance) {
				llcontactview.removeAllViews();
				llcontactview.addView(planview);
				mlist.clear();
				String urlServer = EatParams.getInstance().getUrlServer();
				url_class = urlServer + "?argv={webdm,-DB," + areaDbname + ",-SID,'"
						+ EatParams.getInstance().getSession()
						+ "',-SEARCH-FOODPLAN,'"+URLEncoder.encode(distance)+"',''}";
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						 saxParseXML(url_class);
						 handler.sendEmptyMessage(1);
					}
				};
				Thread thread = new Thread(runnable);
				thread.start();
			}

		});
	}

	//初始化制定美食中的类型下拉框监听事件
	private void initplanListener() {
		planviewtype.setOnSelectListener(new EatPlanViewTyp.OnSelectListener() {
			@Override
			public void getValue(String distance, String showText) {
				onRefreshplan(planviewtype, showText);
			}

			@Override
			public void getdistance(String distance) {

			}
		});
	}
	
	//初始化制定美食中的周别下拉框监听事件
	private void initweekListener() {
		planviewweek.setOnSelectListener(new EatPlanViewWeek.OnSelectListener() {
			@Override
			public void getValue(String distance, String showText) {
				onRefreshweek(planviewweek, showText);
			}

			@Override
			public void getdistance(String distance) {

			}
		});
	}

	//刷新下拉框选择
	private void onRefresh(View view, String showText) {
		expandTabView.onPressBack();
		int position = getPositon(view);
		if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
			expandTabView.setTitle(showText, position);
		}
		Toast.makeText(EatPlanActivity.this, showText, Toast.LENGTH_SHORT).show();

	}

	//获取下拉框选中索引
	private int getPositon(View tView) {
		for (int i = 0; i < mViewArray.size(); i++) {
			if (mViewArray.get(i) == tView) {
				return i;
			}
		}
		return -1;
	}
	
	//刷新制定美食中的类型下拉选择
	private void onRefreshplan(View view, String showText) {
		seftTapView.onPressBack();
		int position = getPositonplan(view);
		if (position >= 0 && !seftTapView.getTitle(position).equals(showText)) {
			seftTapView.setTitle(showText, position);
		}
		Toast.makeText(EatPlanActivity.this, showText, Toast.LENGTH_SHORT).show();
	}

	//获取制定美食中的类型下拉框选中索引
	private int getPositonplan(View tView) {
		for (int i = 0; i < mViewArrayplan.size(); i++) {
			if (mViewArrayplan.get(i) == tView) {
				return i;
			}
		}
		return -1;
	}
	
	//获取制定美食中周别下拉框选择
	private void onRefreshweek(View view, String showText) {
		seftTapweek.onPressBack();
		int position = getPositonweek(view);
		if (position >= 0 && !seftTapweek.getTitle(position).equals(showText)) {
			seftTapweek.setTitle(showText, position);
		}
		Toast.makeText(EatPlanActivity.this, showText, Toast.LENGTH_SHORT).show();

	}

	//获取制定美食中周别下拉框选中索引
	private int getPositonweek(View tView) {
		for (int i = 0; i < mViewArrayweek.size(); i++) {
			if (mViewArrayweek.get(i) == tView) {
				return i;
			}
		}
		return -1;
	}
	
	//显示删除计划弹出框
	public void ShowDialog(String title,final String id,final int position){
		AlertDialog.Builder builder=new AlertDialog.Builder(EatPlanActivity.this);
		builder.setTitle("提示....");
		builder.setMessage("确定删除<"+title+">计划");		
		builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				numpoit=position;
				String urlServer = EatParams.getInstance().getUrlServer();
				url_class = urlServer + "?argv={webdm,-DB," + areaDbname + ",-SID,'"
						+ EatParams.getInstance().getSession()
						+ "',-DEL-FOODPLAN,'"+id+"'}";
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						 saxParseXML(url_class);
						 handler.sendEmptyMessage(2);
					}
				};
				Thread thread = new Thread(runnable);
				thread.start();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		Dialog dialog=builder.create();
		dialog.show();
	}
	
	//获取计划列表
	public void getplanlist() {
		String urlServer = EatParams.getInstance().getUrlServer();
		mlist.clear();
		url_class = urlServer + "?argv={webdm,-DB," + areaDbname + ",-SID,'"
				+ EatParams.getInstance().getSession()
				+ "',-SEARCH-FOODPLAN,'"+URLEncoder.encode(viewLeft.getShowText().toString())+"',''}";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				 saxParseXML(url_class);
				 handler.sendEmptyMessage(1);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}
	
	//添加美食计划
	public void commidplan(){
		String urlServer = EatParams.getInstance().getUrlServer();
		url_class = urlServer + "?argv={webdm,-DB," + areaDbname + ",-SID,'"
				+ EatParams.getInstance().getSession()
				+ "',-ADD-FOODPLAN,'"+URLEncoder.encode(planviewweek.getShowText().toString())+"','"+
				URLEncoder.encode(planviewtype.getShowText().toString())+"','"+URLEncoder.encode(title.getText().toString())+"','"+URLEncoder.encode(infoplan.getText().toString())+"'}";
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
	
	//编辑美食计划
	public void editplan(){
		String urlServer = EatParams.getInstance().getUrlServer();
		url_class = urlServer + "?argv={webdm,-DB," + areaDbname + ",-SID,'"
				+ EatParams.getInstance().getSession()
				+ "',-EDIT-FOODPLAN,'"+id+"','"+URLEncoder.encode(titleedit.getText().toString())+"','"+URLEncoder.encode(infoplanedit.getText().toString())+"'}";
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
		int i=0;
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
				EatPlanItem tempRestaurant = new EatPlanItem();
				tempRestaurant.setId(attributes.getValue("FNO")) ;
				tempRestaurant.setDetails(attributes.getValue("DTEAIL")) ;
				tempRestaurant.setTitle(attributes.getValue("TITLE")) ;
				tempRestaurant.setWeek(attributes.getValue("WEEK")) ;
				
				mlist.add(tempRestaurant);//将数据添加到list中去
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
				//XML文件格式化的时候容易产生以下换行，制表符等特殊字符，
				//这些需要特别注意处理
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
	//异步处理数据，更新界面显示
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==0){									//添加美食计划					
				if(result.equals("ok")){
					Toast.makeText(EatPlanActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();
					title.setText("");
					infoplan.setText("");
				}else{
					if(errorStr.length() <= 0){
						errorStr = "未获取请求数据，请检查网络是否连接正常";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}else if(msg.what==1){								//获取美食计划
				if(result.equals("ok")){
					planAdapter=new EatPlanAdapter(EatPlanActivity.this, mlist);
					planlist.setAdapter(planAdapter);
				}else{
					if(errorStr.length() <= 0){
						errorStr = "未获取请求数据，请检查网络是否连接正常";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}else if(msg.what==2){								//删除计划
				if(result.equals("ok")){
					mlist.remove(numpoit);
					planAdapter.notifyDataSetChanged();
					Toast.makeText(EatPlanActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
				}else{
					if(errorStr.length() <= 0){
						errorStr = "未获取请求数据，请检查网络是否连接正常";
					}
					Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
				}
			}else if(msg.what==3){								//编辑美食计划
				if(result.equals("ok")){
					llcontactview.removeAllViews();
					llcontactview.addView(planview);
					getplanlist();
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
