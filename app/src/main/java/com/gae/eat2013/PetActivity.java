package com.gae.eat2013;
/**
 * author:eity
 * version:2013-05-03
 * description:宠物主页
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.gae.basic.MyContent;
import com.gae.dbHelper.petInfoSDCardHelper;
import com.gae.dbHelper.petSDCardHelper;
import com.gae.entity.EatParams;
import com.gae.entity.PetAlertInfo;
import com.gae.service.PetAlertService;
import com.gae.service.PetMenuService;
import com.gae.service.PetService;
import com.gae.view.Alert;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.oauthv2.OAuthV2Client;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;
import com.weibo.net.AccessToken;
import com.weibo.net.DialogError;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PetActivity extends Activity {

	private TimerTask myTask = null;		//宠物提示定时器
	private DataReceiver dataReceiver;		//宠物广播接收器
	private TimerTask closeTask = null;		//自动关闭宠物提示
	
	private TextView tv_nickname = null;	//昵称
	private TextView tv_gender = null;		//性别
	private TextView tv_birth = null;		//生日
	private TextView tv_mood = null;		//心情
	
	private final int SHOW_PETALERT = 1;	//显示宠物提示
	private final int CLOSE_PETALERT = 2;	//关闭宠物提示
	
	private String nickname = "点点",memo="温柔可人",gender="女",birth="2013-06-01",mood="100",lifetime="0";
	
	private Button infoBtn;		//宠物详细信息按钮
	private Button shareBtn;	//宠物分享信息按钮
	private Alert pwin;			//弹出窗口
	private View pinfo_layout; // 弹出窗布局内容
	
	/*----------------------------分享start--------------------------*/
	//sina
	private static final String CONSUMER_KEY = "657170323";//"913917729";
	private static final String CONSUMER_SECRET = "8383054cf54116dd1289be4dd17da035";//"32c47f37e4727ce9c0db1ceee12bf765";
	private String redirectUriSina="http://www.onpad.cn";//"http://www.sina.com"; //授权回调页    与 我的应用--应用信息--高级信息 中一致
	
	//tencent
	private static final String CLINETID = "100713215";//"801208558";
	private static final String CLIENTSECRET = "01136beb15e8ef129749dd7f04410a91";//"da6d09bb537559c37cb36561fd825346";
	//认证成功后浏览器会被重定向到这个url中  必须与注册时填写的一致
    private String redirectUriTen="http://www.anzhi.com/soft_736725.html";//"http://www.tencent.com/zh-cn/index.shtml"; 

	private String accessToken;//授权码
	private OAuthV2 authV2 = null;//腾讯微博Oauth
	private File file = null;//腾讯本地化授权路径
	/*----------------------------分享end--------------------------*/
	
	 @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet_activity);
        
        tv_nickname = (TextView)findViewById(R.id.p_nickname);
        tv_gender = (TextView)findViewById(R.id.p_gender);
        tv_birth = (TextView)findViewById(R.id.p_birth);
        tv_mood = (TextView)findViewById(R.id.p_mood);
        
        
        //启动服务
        Button start = (Button)findViewById(R.id.start_btn);
        start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent service = new Intent();
				service.setClass(PetActivity.this, PetService.class);		
				startService(service);//启动服务
			}
		});
        
        //返回
        Button reback  = (Button)findViewById(R.id.petback);
        reback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
        //设置
        Button setBtn = (Button)findViewById(R.id.pet_set_btn);
        setBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent itent = new Intent();
				itent.setClass(getApplicationContext(), PetSetActivity.class);
				startActivity(itent);
			}
		});
        
        //查看宠物信息
        infoBtn = (Button)findViewById(R.id.pet_info_btn);
        infoBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPetInfomation();
			}
		});
        
        //分享
        shareBtn = (Button)findViewById(R.id.pet_share_btn);
        shareBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sharePet();
			}
		});
        
        //帮助
        Button helpBtn = (Button)findViewById(R.id.pet_help_btn);
        helpBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), PetHelpActivity.class);
				startActivity(intent);
			}
		});
        
        showPetData();		//显示宠物基本信息
        getAlertInfoData();	//获取宠物信息数据源
        
        startInfoTimer();	//宠物提示信息计时器
        
		dataReceiver = new DataReceiver();  
	    IntentFilter filter = new IntentFilter();//创建IntentFilter对象  
	    filter.addAction("com.gae.service.petInfo");  
	    registerReceiver(dataReceiver, filter);//注册Broadcast Receiver  
	    
	    Display display = getWindowManager().getDefaultDisplay();
	    EatParams.getInstance().setScreenWidth(display.getWidth());
	    EatParams.getInstance().setScreenHeight(display.getHeight());
    }
	 
	//显示宠物详细信息 
	private void showPetInfomation(){
		pinfo_layout = LayoutInflater.from(PetActivity.this).inflate(
				R.layout.pet_info, null);
		pwin = new Alert(infoBtn, pinfo_layout);
        TextView textView = (TextView) pinfo_layout  
                .findViewById(R.id.dialog_pinfo1);  
        textView.setText("名称：" + nickname);  
        TextView textView2 = (TextView) pinfo_layout  
                .findViewById(R.id.dialog_pinfo2);  
        textView2.setText("生日：" + birth); 
        TextView textView3 = (TextView) pinfo_layout  
                .findViewById(R.id.dialog_pinfo3);  
        textView3.setText("性别：" + gender); 
        TextView textView4 = (TextView) pinfo_layout  
                .findViewById(R.id.dialog_pinfo4);  
        textView4.setText("心情：" + lifetime); 
        TextView textView5 = (TextView) pinfo_layout  
                .findViewById(R.id.dialog_pinfo5);  
        textView5.setText("描述：" + memo); 
        
        Button button = (Button) pinfo_layout.findViewById(R.id.custom_button_ok);  
        button.setOnClickListener(new View.OnClickListener() {  
  
            public void onClick(View v) {  
            	if (pwin != null) {
        			pwin.closePopUpWindow();
        			pwin = null;
        		}
            }  
        });  
        Button button1 = (Button) pinfo_layout  
                .findViewById(R.id.custom_button_cancl);  
        button1.setOnClickListener(new View.OnClickListener() {  
  
            public void onClick(View v) {  
            	if (pwin != null) {
        			pwin.closePopUpWindow();
        			pwin = null;
        		}
            }  
        });  
	}
	
	//宠物分享
	private void sharePet(){
		pinfo_layout = LayoutInflater.from(PetActivity.this).inflate(
				R.layout.pet_share, null);
		pwin = new Alert(shareBtn, pinfo_layout);
		//绑定Layout里面的ListView  
        ListView list = (ListView) pinfo_layout.findViewById(R.id.petShareList);  
          
        String[] strs = {"短信分享","分享到微信","分享到腾讯微博","分享到新浪微博","取消"};  
        int[] imgs = {R.drawable.msg,R.drawable.weixin,R.drawable.tengxun,R.drawable.sina,R.drawable.left};
        //生成动态数组，加入数据  
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
        for(int i=0;i<strs.length;i++)  
        {  
            HashMap<String, Object> map = new HashMap<String, Object>();             
            map.put("ItemTitle", strs[i]);  
            map.put("ItemText", imgs[i]);  
            listItem.add(map);  
        }  
        //生成适配器的Item和动态数组对应的元素  
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//数据源   
            R.layout.pet_share_item,//ListItem的XML实现  
            //动态数组与ImageItem对应的子项          
            new String[] {"ItemTitle","ItemText"},   
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
            new int[] {R.id.pet_share_txt,R.id.pet_share_img}  
        );  
         
        //添加并且显示  
        list.setAdapter(listItemAdapter);  
          
        //添加点击  
        list.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                    long arg3) {  
                if(arg2 == 0){//短信
                	share2msg();  
                }else if(arg2 ==1){//微信
                	share2weixin();
                }else if(arg2 == 2){//腾讯微博
                	share2tengxun();
                }else if(arg2 == 3){//新浪微博
                	share2xinlang();
                }else{//取消
                	if (pwin != null) {
            			pwin.closePopUpWindow();
            			pwin = null;
            		}
                }
            }  
        });  
	}
	 
	 //启动宠物提示计时器 
	private void startInfoTimer(){
		 if (myTask != null){
			myTask.cancel();  //将原任务从队列中移除
		 }
		
		 myTask = new MyTimerTask();
		 Timer timer = new Timer();
         timer.schedule(myTask, 2000,10000);
	 }
	 
	//插入宠物数据
	private void insertPetData(){
		petSDCardHelper db = new petSDCardHelper(this);
		db.open();	
		ContentValues cv = new ContentValues();
		cv.put(petSDCardHelper.FIELD_NICKNAME, "点点");
		cv.put(petSDCardHelper.FIELD_BIRTH, EatParams.getInstance().getFormatDate());
		cv.put(petSDCardHelper.FIELD_CLOSED, "N");
		cv.put(petSDCardHelper.FIELD_ENERGE, 100);
		cv.put(petSDCardHelper.FIELD_GENDER, "F");
		cv.put(petSDCardHelper.FIELD_HEALTH, 100);
		cv.put(petSDCardHelper.FIELD_MEMO, "温柔可人");
		cv.put(petSDCardHelper.FIELD_MOOD, 100);
		cv.put(petSDCardHelper.FIELD_SCORE, 0);
		cv.put(petSDCardHelper.FIELD_WEATHER, 100);
		cv.put(petSDCardHelper.FIELD_LIFETIME, 0);
		db.insert(cv);
		db.close();
	}
	
	//更新宠物数据
	private void updatePetData(String typ,String value){
		petSDCardHelper db = new petSDCardHelper(this);
		db.open();	
		ContentValues cv = new ContentValues();
		if(typ.equals("name")){
			cv.put(petSDCardHelper.FIELD_NICKNAME, value);
		}else if(typ.equals("lifetime")){
			long olt = EatParams.getInstance().getPetLifeTime();
			long nlt = olt + Long.valueOf(value);
			cv.put(petSDCardHelper.FIELD_LIFETIME, nlt);
		}
			
		db.update(cv, "1");
		db.close();
	}
	 
	//显示宠物数据
	private void showPetData(){
		petSDCardHelper db = new petSDCardHelper(this);
		db.open();	
		Cursor cur = db.select();
		if(cur == null)return;
		
		int leng = cur.getCount();
		if(leng <= 0){
			insertPetData();
		}
		
		for(int i =0;i<leng;i++){
			cur.moveToPosition(i);
            nickname = cur.getString(cur.getColumnIndex(petSDCardHelper.FIELD_NICKNAME));
            gender = cur.getString(cur.getColumnIndex(petSDCardHelper.FIELD_GENDER));
            birth = cur.getString(cur.getColumnIndex(petSDCardHelper.FIELD_BIRTH));
            mood = cur.getString(cur.getColumnIndex(petSDCardHelper.FIELD_MOOD));
            lifetime = cur.getString(cur.getColumnIndex(petSDCardHelper.FIELD_LIFETIME));
            memo =  cur.getString(cur.getColumnIndex(petSDCardHelper.FIELD_MEMO));
		}
		cur.close();
		db.close();
		
		tv_nickname.setText(nickname);
        if(gender.equals("F")){
        	tv_gender.setText("女");
        }else{
        	tv_gender.setText("男");
        }
        tv_birth.setText(birth);
        int ltime = (int)Math.floor( Double.valueOf(lifetime)/3600*1000);
        tv_mood.setText(""+ltime);//mood);
        EatParams.getInstance().setPetLifeTime( Long.valueOf(lifetime) );
	}
	 
	//延迟启动宠物提示
	class MyTimerTask extends TimerTask{
		
		@Override
		public void run() {
			Message message = new Message(); 
	        message.what = SHOW_PETALERT; 
	        handler.sendMessage(message); 
		}
	};
	
	
	//自动关闭宠物提示
	class CloseTimerTask extends TimerTask{

		@Override
		public void run() {
			Message message = new Message();
			message.what = CLOSE_PETALERT;
			handler.sendMessage(message);
		}
		
	}
	
	//显示宠物提示信息
	private void showPetInfoAlert(){
		EatParams inst = EatParams.getInstance();
		if(inst.getPetStatus() == 1 && inst.getPetInfoStatus() != 1){
			preInitAlertIndex();
			
			String ptype = inst.getPetAlertType();
			int [] pindexs = inst.getPerAlertIndex();
			int index = 0;
			ArrayList<PetAlertInfo> idata;
			if(ptype.equals( PetAlertInfo.TYPE_FUCTION ) ){//功能性信息
				index = pindexs[0];
				idata = inst.getfAlertList();
			}else if(ptype.equals( PetAlertInfo.TYPE_TIMED ) ){//时间性信息
				index = pindexs[1];
				idata = inst.gettAlertList();
			}else if(ptype.equals( PetAlertInfo.TYPE_SYSTEM ) ){//系统性信息
				index = pindexs[2];
				idata = inst.getsAlertList();
			}else if(ptype.equals( PetAlertInfo.TYPE_WEB ) ){//网络信息
				index = pindexs[3];
				idata = inst.getwAlertList();
			}else{
				idata = null;
				index = 0;
			}
			if(idata != null && idata.get(index).getShowable().equals("true")){
	        	Intent intent = new Intent();
	        	intent.setClass(getApplicationContext(), PetAlertService.class);
	        	startService(intent);
	        	
	        	closeTask = new CloseTimerTask();
	        	Timer timer = new Timer();
	        	timer.schedule(closeTask, 40000);
			}       	
    	}
	}
	
	//初始化提示信息索引
	private void preInitAlertIndex(){
		EatParams pins = EatParams.getInstance();
		
		petTimeInfoAlert();
		
		String curtype = pins.getPetAlertType();
		if(curtype.equals("T") || curtype.equals("F")){
			Log.i("---------------", "T--------------------------F");
		}else{
			if(pins.getPerAlertIndex()[2] < pins.getsAlertList().size()){
				pins.setPetAlertType("S");
				pins.setPetLastAlertType("S");
			}else{
				pins.getPerAlertIndex()[2] = 0;
				if(pins.getPerAlertIndex()[3] < pins.getwAlertList().size()){
					pins.setPetAlertType("W");
					pins.setPetLastAlertType("W");
				}else{
					pins.getPerAlertIndex()[3] = 0;
					pins.setPetAlertType("S");
					pins.setPetLastAlertType("S");
				}
			}
		}
	}
	
	//指定时间提示
	private void petTimeInfoAlert(){
		EatParams pins = EatParams.getInstance();
		ArrayList<PetAlertInfo> idata = pins.gettAlertList();
		int index = pins.getPerAlertIndex()[1];
		String ltime = "14:30";//idata.get(index).getTime();
		int hour = Integer.valueOf(ltime.split(":")[0]);
		int second = Integer.valueOf(ltime.split(":")[1]);
		Date da = new Date();
		
		boolean isTime = false;
		if(second > 0){
			if(hour == da.getHours()){
				if(da.getMinutes() > second - 10 || da.getMinutes() < second + 10){
					idata.get(index).setShowable("true");	
					isTime = true;
				}
			}
		}else{
			if(hour == da.getHours() + 1){
				if(da.getMinutes() >= 59){
					idata.get(index).setShowable("true");
					isTime = true;
				}
			}else if(hour == da.getHours()){
				if(da.getMinutes() <= 15){
					idata.get(index).setShowable("true");
					isTime = true;
				}
			}
		}
		
		if(isTime){
			pins.setPetAlertType("T");
		}else{
			pins.setPetAlertType(pins.getPetLastAlertType());
		}
	}
	
	//指定功能提示
	private void petFucInfoAlert(){
		EatParams pins = EatParams.getInstance();
		pins.setPetAlertType("F");
	}
	
	//自动关闭宠物提示窗口
	private void autoCloseInfoAlert(){
		EatParams inst = EatParams.getInstance();
		if(inst.getPetStatus() == 1 && inst.getPetInfoStatus() == 1){
			closePetAlert();			
		}
		if(closeTask != null){
			closeTask.cancel();
		}
		closeTask = null;
	}
	
	//获取宠物提示数据
	private void getAlertInfoData(){
		petInfoSDCardHelper db = new petInfoSDCardHelper(this);
		db.open();	
		Cursor cur = db.select();
		if(cur == null)return;
		
		EatParams pins = EatParams.getInstance();
		ArrayList<PetAlertInfo> fList = new ArrayList<PetAlertInfo>();
		ArrayList<PetAlertInfo> sList = new ArrayList<PetAlertInfo>();
		ArrayList<PetAlertInfo> wList = new ArrayList<PetAlertInfo>();
		ArrayList<PetAlertInfo> tList = new ArrayList<PetAlertInfo>();
		int leng = cur.getCount();		
		PetAlertInfo pobj = null;
		for(int i =0;i<leng;i++){
			cur.moveToPosition(i);
			pobj = new PetAlertInfo();
			pobj.setContent( cur.getString(cur.getColumnIndex(petInfoSDCardHelper.FIELD_CONTENT)) );
			pobj.setGrade( cur.getString(cur.getColumnIndex(petInfoSDCardHelper.FIELD_GRADE)) );
			pobj.setLink( cur.getString(cur.getColumnIndex(petInfoSDCardHelper.FIELD_LINK)) );
			pobj.setTime( cur.getString(cur.getColumnIndex(petInfoSDCardHelper.FIELD_TIME)) );
			String ty = cur.getString(cur.getColumnIndex(petInfoSDCardHelper.FIELD_TYPE));
			pobj.setType( ty );
			pobj.setShowable( cur.getString(cur.getColumnIndex(petInfoSDCardHelper.FIELD_SHOWABLE)) );
			if(ty.equals(PetAlertInfo.TYPE_FUCTION)){
				fList.add(pobj);
			}else if(ty.equals(PetAlertInfo.TYPE_SYSTEM)){
				sList.add(pobj);
			}else if(ty.equals(PetAlertInfo.TYPE_TIMED)){
				tList.add(pobj);
			}else if(ty.equals(PetAlertInfo.TYPE_WEB)){
				wList.add(pobj);
			}
		}
		cur.close();
		db.close();
		
		pins.setfAlertList(fList);
		pins.settAlertList(tList);
		pins.setwAlertList(wList);
		pins.setsAlertList(sList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//宠物菜单显示、隐藏
	private void petMenu(Intent intent){
		int mstatus = EatParams.getInstance().getPetMenuStatus();
		if(mstatus == 1){							//关闭
			EatParams.getInstance().setPetMenuStatus(2);
			Intent mintent = new Intent();
			mintent.setClass(getApplicationContext(), PetMenuService.class);
			stopService(mintent);
		}else{										//打开
			Float y = intent.getFloatExtra("py", 0);
			EatParams.getInstance().setPetY( y );
			Intent mintent = new Intent();					
			mintent.setClass(getApplicationContext(), PetMenuService.class);
			startService(mintent);
		}
	}
	
	//退出宠物
	private void exitPet(){
    	EatParams.getInstance().setPetMenuStatus(2);
		Intent mintent = new Intent();
		mintent.setClass(getApplicationContext(), PetMenuService.class);
		stopService(mintent);
		
		EatParams.getInstance().setPetStatus(2);
		Intent sintent = new Intent();
		sintent.setClass(getApplicationContext(), PetService.class);
    	stopService(sintent);
    	
    	Date dt = new Date(System.currentTimeMillis());
    	long cdt = dt.getTime();
    	long odt = EatParams.getInstance().getPetLiveHour();
    	long ldt = cdt - odt;
    	
    	updatePetData("lifetime","" +ldt);
	}
	
	//关闭宠物提示窗口
	private void closePetAlert(){
		EatParams.getInstance().setPetInfoStatus(2);
		
		Intent sintent = new Intent();
		sintent.setClass(getApplicationContext(), PetAlertService.class);
    	stopService(sintent);
	}
	
	@Override
	public void onDestroy(){
		unregisterReceiver(dataReceiver);
		super.onDestroy();
	}
	
	//宠物提示信息触发事件
	Handler handler = new Handler() { 
	    // 定义处理信息的方法 
	    public void handleMessage(Message msg) { 
	        switch (msg.what) { 
		        case SHOW_PETALERT: //显示宠物提示信息
		            //处理代码 		    	
		        	showPetInfoAlert();
		            break; 
		        case CLOSE_PETALERT://隐藏宠物提示信息
		        	autoCloseInfoAlert();
		        	break;
	        } 
	        super.handleMessage(msg); 
	    } 
	}; 
	
	//宠物广播接收器
	public class DataReceiver extends BroadcastReceiver {		
		@Override
		public void onReceive(Context context, Intent intent) {
			//显示提示内容
			String msg = intent.getStringExtra("msg");
			if(msg.equals("openPetMenu")){					//宠物功能菜单				
				petMenu(intent);
			}else if(msg.equals("exitPet")){				//退出宠物应用
				exitPet();
			}
		}
		
		public DataReceiver(){
			
		}
	}
	
	/*--------------------------------分享------------------------------*/
    //微信
    private void share2weixin(){
    	Intent tent = new Intent();
    	tent.putExtra("mtype", "pet");
    	tent.setClass(getApplicationContext(), WeiXinActivity.class);
    	startActivity(tent);
    }
    
    //短信
    private void share2msg(){
    	Uri uri = Uri.parse("smsto:");            
    	Intent it = new Intent(Intent.ACTION_SENDTO, uri);            
    	it.putExtra("sms_body", "测试发送内容");            
    	startActivity(it);
    }
	//新浪微博
	private void share2xinlang(){
		Weibo weibo = Weibo.getInstance();
		if(!isOauthSina(weibo)){	//未授权
			weibo.setupConsumerConfig(CONSUMER_KEY, CONSUMER_SECRET);//设置你的key和secret
			weibo.setRedirectUrl(redirectUriSina);
			weibo.authorize(this, new OauthDialogListener());
		}else{	//已授权
			Toast.makeText(getApplicationContext(), "该用户已经授权", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.putExtra("accessToken", accessToken);
			intent.putExtra("flag", MyContent.SINA);
			intent.putExtra("mtype", "pet");
			intent.setClass(PetActivity.this, ShareMsgActivity.class);
			startActivity(intent);
		}
	}
	
	//腾讯微博
	private void share2tengxun(){
		//判断用户是否已经授权
        if(!isOauchTen()){
        	//使用回调url来创建授权页面
			authV2 = new OAuthV2(redirectUriTen);
			authV2.setClientId(CLINETID);
			authV2.setClientSecret(CLIENTSECRET);
			//关闭OAuthV2Client中的默认开启的QHttpClient。
	        OAuthV2Client.getQHttpClient().shutdownConnection();
        	Intent intent = new Intent(PetActivity.this, OAuthV2AuthorizeWebView.class);//创建Intent，使用WebView让用户授权
            intent.putExtra("oauth", authV2);
            startActivityForResult(intent,2); 
        }else{
        	Toast.makeText(getApplicationContext(), "该用户已经授权", Toast.LENGTH_SHORT).show();
        	Intent intent = new Intent();
        	intent.putExtra("oauth", authV2);
			intent.putExtra("flag", MyContent.TENCENT);
			intent.putExtra("mtype", "pet");
			intent.setClass(PetActivity.this, ShareMsgActivity.class);
			startActivity(intent);
        }
	}
	
	/**
     * 将腾讯微博的oauth持久化到文件中
     */
    private void persistTenOauth(){
    	//加这一句的作用是防止  /data/data/package-name这个目录不存在
    	String s = getFileStreamPath("aaa").getAbsolutePath();
		for(String ss : fileList()){
			System.out.println("ss==" + ss);
		}
		String x = "";
		try{
			x = s.substring(0,s.lastIndexOf("/"));
		}catch(Exception e){
			e.printStackTrace();
			x = "/data/data/yanbin.insertweibo";
		}
		//将文件存放在 /data/data/package-name目录下，当然你也可以存储在别的地方
		try {
			file = new File(x + "/oauth_ten.data");
			if(!file.exists()){
				new File(x).mkdirs();
				file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    }
    
    /**
     * 腾讯微博接入是否已经验证
     * @return
     */
    private boolean isOauchTen() {
    	boolean b = false;
    	FileInputStream fis = null;
    	ObjectInputStream ois = null;
    	try {
    		openFileOutput("aaa", Context.MODE_PRIVATE);
    		persistTenOauth();
    		fis = new FileInputStream(file);
    		ois = new ObjectInputStream(fis);//此处抛出EOFException，原因是独到了流的末尾还是返回空，我们这里直接在异常中将标志位记为false即可。
    		authV2 = (OAuthV2) ois.readObject();
    		if(authV2 != null){
    			b = true;
    		}
		} catch (Exception e) {
			b = false;
		} finally{
			if(ois != null){
				try {
					ois.close();
					ois = null;
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if(fis != null){
				try {
					fis.close();
					fis = null;
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
				
		}
		return b;
	}
    
    /**
     * renren 判断用户是否已经授权
     * @return
     */
    private boolean isOauthRenren(){
    	boolean b = false;
    	String token = getSharedPreferences("oauth_renren", Context.MODE_PRIVATE).getString("access_token", "");
    	if(!"".equals(token)){
    		b = true;
    	}
    	return b;
    }
    
    /**
     * 新浪微博和人人网的授权
     * 通过读取OAuthV2AuthorizeWebView返回的Intent，获取用户授权信息
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode==2) {
    		//腾讯微博授权
            if (resultCode==OAuthV2AuthorizeWebView.RESULT_CODE)    {
                OAuthV2 oAuth=(OAuthV2) data.getExtras().getSerializable("oauth");
                if(oAuth != null && oAuth.getStatus()==0){
                	Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                	//跳转到发微博的界面
	                Intent intent = new Intent();
					intent.putExtra("accessToken", oAuth.getAccessToken());
					intent.putExtra("oauth", oAuth);
					intent.putExtra("flag", MyContent.TENCENT);
					//将认证保存起来，使用对象流
					FileOutputStream fos = null;
					ObjectOutputStream oos = null;
					try {
						fos = new FileOutputStream(file);
						oos = new ObjectOutputStream(fos);
						oos.writeObject(oAuth);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally{
						if(oos != null){
							try {
								oos.close();
								oos = null;
							} catch (IOException e) {
								e.printStackTrace();
							}
						if(fos != null){
							try {
								fos.close();
								fos = null;
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						}
					}
					intent.setClass(PetActivity.this, ShareMsgActivity.class);
					startActivity(intent);	
                }
                else
                	Toast.makeText(getApplicationContext(), "登陆失败", Toast.LENGTH_SHORT).show();
            }else{
            	Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    
    //=======================================================sina
    /**
     * 新浪微博 用户是否已经授权
     * @param weibo
     * @return
     */
    private boolean isOauthSina(Weibo weibo){
    	boolean b = false;
    	accessToken = getSharedPreferences("token", Context.MODE_PRIVATE).getString("access_token", "");
    	if(weibo != null && !accessToken.equals("")){
    		b = true;
    	}
    	return b;
    }
    
    /**
     * 弹出新浪微博的授权页面
     * @author yanbin
     *
     */
    private class OauthDialogListener implements WeiboDialogListener{
		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			AccessToken accessToken = new AccessToken(token, CONSUMER_SECRET);
			SharedPreferences sf = getSharedPreferences("token", Context.MODE_PRIVATE);
			sf.edit().putString("access_token", accessToken != null ? accessToken.getToken() : "")
			.commit();
//			accessToken.setExpiresIn(expires_in);
//			Weibo.getInstance().setAccessToken(accessToken);
			Intent intent = new Intent();
			intent.putExtra("accessToken",accessToken.getToken());
			intent.putExtra("flag", MyContent.SINA);
			intent.setClass(PetActivity.this, ShareMsgActivity.class);
			startActivity(intent);
		}

		@Override
		public void onWeiboException(WeiboException e) {
			//未作处理
		}

		@Override
		public void onError(DialogError e) {
			//未作处理
		}

		@Override
		public void onCancel() {
			Toast.makeText(PetActivity.this, "您已经取消授权", Toast.LENGTH_SHORT).show();
		}
    }
}
