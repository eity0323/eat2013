package com.gae.eat2013;
/**
 * author:小胡
 * version:2013-5-24
 * description:主界面
 * */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.gae.adapter.MainGridViewAdapter;
import com.gae.adapter.SliListViewAdapter;
import com.gae.basic.ProgressRing;
import com.gae.basic.UploadUtil;
import com.gae.basic.UploadUtil.OnUploadProcessListener;
import com.gae.control.GpsCityControl;
import com.gae.dbHelper.ApplydbHelper;
import com.gae.entity.EatParams;
import com.gae.entity.applyItem;
import com.gae.listener.GpsCompleteListener;
import com.gae.view.PageControlView;
import com.gae.view.ScrollLayout;
import com.gae.view.ScrollLayout.OnScreenChangeListenerDataLoad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements OnUploadProcessListener{
	private SlidingDrawer mdrawer = null;                                   // 定义一个抽屉控件
	private TextView center = null;                                         //中间地址
    private String dbname = "";												//本地数据库名称
    private List<applyItem> applyList=null;                           		//抽屉应用列表数据
    private List<applyItem> applygList=null;                          		//主界面应用列表数据
    private ScrollLayout mScrollLayout = null;                              //滑动控件
	private static final float APP_PAGE_SIZE = 9.0f;                  		//每个页面显示应用的个数
	private PageControlView pageControl = null;                             //分页
	private MyHandler myHandler = null;                                     //线程
	private int atpage=0;                                              		//页码
	private DataLoading dataLoad = null;                                    //数据加载
	private Dialog applyDialog = null;                                      //添加应用对话框 
	private SliListViewAdapter sliAdapter = null;                           //抽屉适配器
	private MainGridViewAdapter gridAdapter = null;                         //桌面应用适配器
	private LayoutInflater mLayoutInflater = null;							//
	private LinearLayout lltopView = null;									//广告/个人信息栏
	
	/**
	 * 去上传文件
	 */
	protected static final int TO_UPLOAD_FILE = 1;  
	/**
	 * 上传文件响应
	 */
	protected static final int UPLOAD_FILE_DONE = 2;  //
	/**
	 * 选择文件
	 */
	public static final int TO_SELECT_PHOTO = 3;
	/**
	 * 上传初始化
	 */
	private static final int UPLOAD_INIT_PROCESS = 4;
	/**
	 * 上传中
	 */
	private static final int UPLOAD_IN_PROCESS = 5;
	
	private static String requestURL = "http://10.168.2.168/mbpic.php";
	
	private String picPath = null;
	private ProgressDialog progressDialog;
	
	private Bitmap personImage = null;
	
	public GpsCityControl GpsCity = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//获取本地应用数据库名称
		dbname = EatParams.getInstance().getSDdbname();
				
		mLayoutInflater = getLayoutInflater();
		
		Intent tent = getIntent();
		String source = tent.getStringExtra("source");
		if(source != null && source.length() > 0){
			setLocation();
		}
	    
	    //初始化界面显示
		initBody();
	}
	
	private void setLocation(){
		//定位地址
		ProgressRing.onProgeress(MainActivity.this, "智点", "正在定位...");
	    GpsCity = new GpsCityControl(MainActivity.this);
	    GpsCity.InitGPS();
	    GpsCity.setGpsCompleteListener(new GpsCompleteListener() {
			
			@Override
			public void onComplete(String str) {
				if(center != null)center.setText(str);
				ProgressRing.unProgeress();
			}
		});
	}
	
	//定位之后显示页面内容
	private void initBody(){
		getAllApply();										//获取应用数据
		topview();											//初始化广告栏显示
		findview();                       					//界面显示
		drawerlistener();                                   //抽屉响应事件
		
		dataLoad = new DataLoading();
		mScrollLayout = (ScrollLayout)findViewById(R.id.ScrollLayoutTest);
		myHandler = new MyHandler(this,1);
	    MyThread m = new MyThread();                       //加载数据
	    new Thread(m).start();
	    
	    progressDialog = new ProgressDialog(this);
	}

	//获取功能应用
	private void getAllApply(){                             
		applyList=new ArrayList<applyItem>();
		applygList=new ArrayList<applyItem>();
		
		ApplydbHelper applydb=new ApplydbHelper(MainActivity.this); 
		applydb.open(dbname);                               //打开数据库
		Cursor cursor=applydb.getAllApplys();               //获取数据
		
		while (cursor.moveToNext()) {                       
			applyItem item=new applyItem();
			item.setId(cursor.getString(cursor.getColumnIndex(applydb.APPLY_ID)));
			item.setName(cursor.getString(cursor.getColumnIndex(applydb.APPLY_NAME)));
			item.setMemo(cursor.getString(cursor.getColumnIndex(applydb.APPLY_MEMO)));
			item.setGridico(cursor.getString(cursor.getColumnIndex(applydb.APPLY_GRIDICO)));
			item.setSliico(cursor.getString(cursor.getColumnIndex(applydb.APPLY_SLIICO)));
			item.setLink(cursor.getString(cursor.getColumnIndex(applydb.APPLY_LINK)));
			item.setLknum(cursor.getString(cursor.getColumnIndex(applydb.APPLY_LKNUM)));
			item.setShow(cursor.getString(cursor.getColumnIndex(applydb.APPLY_SHOW)));
			item.setGroup(cursor.getString(cursor.getColumnIndex(applydb.APPLY_GROUP)));
			item.setTime(cursor.getString(cursor.getColumnIndex(applydb.APPLY_TIME)));
			applyList.add(item);
			if(cursor.getString(cursor.getColumnIndex(applydb.APPLY_SHOW)).equals("Y")){
				applygList.add(item);
			}			
		}
		applyItem itemg=new applyItem();
		itemg.setGridico("applyadd");                             //最后一个应用添加的标示
		applygList.add(itemg);
		applydb.close();		
	}
	
	//顶部图片显示
	private void topview(){
		lltopView=(LinearLayout)findViewById(R.id.lltopView);
		lltopView.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		if(EatParams.getInstance().getSession().length()>0){				//登录状态
			View pernsonView = mLayoutInflater.inflate(R.layout.pernsoninfo, null);
			ImageView hicon = (ImageView)pernsonView.findViewById(R.id.headIcom);
			if(personImage != null){
				hicon.setImageBitmap(personImage);
			}
			hicon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					takePhoto();
				}
			});
			ImageView ivedit = (ImageView)pernsonView.findViewById(R.id.user_edit);
			ivedit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//修改个人信息
					Intent lintent = new Intent();
					lintent.setClass(getApplicationContext(), PersonInfoActivity.class);
					startActivity(lintent);
				}
			});
			TextView name=(TextView)pernsonView.findViewById(R.id.usename);
			TextView addr=(TextView)pernsonView.findViewById(R.id.addr);
			name.setText(EatParams.getInstance().getUsename());
			addr.setText("地址："+EatParams.getInstance().getAddr());			
			lltopView.addView(pernsonView,params);
		}else{																//未登录状态
			View adView = mLayoutInflater.inflate(R.layout.adinfo_main, null);
			ImageView hicon = (ImageView)adView.findViewById(R.id.headIcom);
			hicon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//登录
					Intent lintent = new Intent();
					lintent.setClass(getApplicationContext(), SetLoginActivity.class);
					startActivity(lintent);
				}
			});
			
			TextView ivregister = (TextView)adView.findViewById(R.id.nouser_register);
			ivregister.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent lintent = new Intent();
					lintent.setClass(getApplicationContext(), RegisterActivity.class);
					startActivity(lintent);
				}
			});
			
			TextView ivlogin = (TextView)adView.findViewById(R.id.nouser_login);
			ivlogin.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent lintent = new Intent();
					lintent.setClass(getApplicationContext(), SetLoginActivity.class);
					startActivity(lintent);
				}
			});
			
			lltopView.addView(adView,params);
		}
	}

	//上传图片
	private void takePhoto(){
		Intent intent = new Intent(this,SelectPicActivity.class);
		startActivityForResult(intent, TO_SELECT_PHOTO);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO)
		{
			picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
//			Log.i(TAG, "最终选择的图片="+picPath);
			Bitmap bm = BitmapFactory.decodeFile(picPath);
//			imageView.setImageBitmap(bm);
			personImage = bm;
			
			//上传图片
			if(picPath!=null)
			{
				handler.sendEmptyMessage(TO_UPLOAD_FILE);
			}else{
				Toast.makeText(this, "上传的文件路径出错", Toast.LENGTH_LONG).show();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	//初始化列表组件及抽屉组件
	private void findview() {		
		mdrawer = (SlidingDrawer) findViewById(R.id.slidingdrawer);
		center = (TextView) findViewById(R.id.high_rise);
		
		//当前地址
		String localpos = EatParams.getInstance().getGpsAddr();
		if(localpos == null || localpos.length() <= 0){
			localpos = "未获取到您的位置";
		}
		center.setText(localpos);            
		
		//抽屉组件
		ListView sligv = (ListView)findViewById(R.id.SlidListView);         
		sliAdapter=new SliListViewAdapter(MainActivity.this,applyList);
		sligv.setAdapter(sliAdapter);
		sligv.setOnItemClickListener(new OnItemClickListener() 
        { 
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
            { 

            	Intent intent=new Intent();
            	if(applyList.get(position).getLink().equals("ShareEatActivity")){
            		ProgressRing.onProgeress(MainActivity.this, "提示", "正在加载手机好友，请稍候....", 4000);
            	}
            	
            	int lknum = Integer.valueOf(applyList.get(position).getLknum());
            	applyList.get(position).setLknum( ""+(lknum + 1) );
            	
            	intent.setClassName(MainActivity.this,"com.gae.eat2013."+applyList.get(position).getLink());
           	    MainActivity.this.startActivity(intent);
           	    MainActivity.this.finish();
                Toast.makeText(MainActivity.this, "跳转到" +applyList.get(position).getLink(), Toast.LENGTH_SHORT).show(); 
            } 
        });
		
		//切换位置
		Button changCity=(Button)findViewById(R.id.shop_top_right);
		changCity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ProgressRing.onProgeress(MainActivity.this, "智点", "正在定位...");
				GpsCity.InitGPS();
			}
		});
	}

	private void drawerlistener() {
		// 这个事件是当抽屉打开时触发的事件，这里所指的“打开”是当抽屉完全到达顶部触发的事件，我们在这里改变了ImageButton按钮的图片
		mdrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
				
			}

		});
		// 这个事件当然就是和上面相对应的事件了。当抽屉完全关闭时触发的事件，我们将ImageButton的图片又变回了最初状态
		mdrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
			@Override
			public void onDrawerClosed() {
				
			}

		});
		// 这个事件是抽屉的拖动事件，当抽屉在开始拖动和结束拖动时分别触发onScrollStarted() 和onScrollEnded() 事件
		mdrawer.setOnDrawerScrollListener(new SlidingDrawer.OnDrawerScrollListener() {
			// 当手指离开抽屉头时触发此事件（松开ImageButton触发）
			@Override
			public void onScrollEnded() {
			}

			// 当按下抽屉头时触发此事件（按下ImageButton触发）
			@Override
			public void onScrollStarted() {
			}

		});

	}
	
	// 更新后台数据
	class MyThread implements Runnable {
		public void run() {
			String msglist = "1";
			Message msg = new Message();
			Bundle b = new Bundle();// 存放数据
			b.putString("rmsg", msglist);
			msg.setData(b);
			MainActivity.this.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
		}
	}

	class MyHandler extends Handler {
		private MainActivity mContext;
		public MyHandler(Context conn,int a) {
			mContext = (MainActivity)conn;
		}

		public MyHandler(Looper L) {
			super(L);
		}

		// 子类必须重写此方法,接受数据
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			final int pageNo = (int)Math.ceil( applygList.size()/APP_PAGE_SIZE);
			for (int i = 0; i < pageNo; i++) {
				GridView appPage = new GridView(mContext);
				// get the "i" page data
				gridAdapter=new MainGridViewAdapter(mContext, applygList, i);
				appPage.setAdapter(gridAdapter);
				appPage.setNumColumns(3);                      //每列的应用个数
				appPage.setGravity(Gravity.CENTER);
				appPage.setPadding(0, 20, 0, 0);
				appPage.setHorizontalSpacing(10);
				appPage.setVerticalSpacing(30);
				
				appPage.setOnItemClickListener(new OnItemClickListener() {
	
					@Override
					public void onItemClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						int num = mScrollLayout.getCurScreen();      //第几页
						int longdk = applygList.size();
						if(num*APP_PAGE_SIZE + arg2==applygList.size()-1){    //最后一个应用
							Toast.makeText(MainActivity.this, "添加应用", Toast.LENGTH_SHORT).show();
							creatAddApply();								
						}else{
							Intent intent=new Intent();
							if(applygList.get((int) (num*APP_PAGE_SIZE+arg2)).getLink().equals("ShareEatActivity")){
			            		ProgressRing.onProgeress(MainActivity.this, "提示", "正在加载手机好友，请稍候....", 4000);
			            	}
							String applink = applygList.get((int) (num*APP_PAGE_SIZE+arg2)).getLink();
							appVisitNumChange(applink);
			            	intent.setClassName(MainActivity.this,"com.gae.eat2013." + applink);
			           	    MainActivity.this.startActivity(intent);						
						}
					}
				});
				appPage.setOnItemLongClickListener(new OnItemLongClickListener() {      //长按删除应用
	
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, final int arg2, long arg3) {
						final int num=mScrollLayout.getCurScreen();      //第几页
						if(num*APP_PAGE_SIZE+arg2!=applygList.size()-1){
							AlertDialog.Builder built=new AlertDialog.Builder(MainActivity.this);
							built.setTitle("提示");
							built.setMessage("您确定要删除《"+applygList.get((int) (num*APP_PAGE_SIZE+arg2)).getName()+"》桌面应用");
							
							built.setNeutralButton("确认",new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									ApplydbHelper applydb=new ApplydbHelper(MainActivity.this); 
									applydb.open(dbname);
									applydb.update(applygList.get((int) (num*APP_PAGE_SIZE+arg2)).getId().trim(),"N");
									Intent intent = new Intent();
									intent.setClass(MainActivity.this, MainActivity.class);  //刷新界面
									startActivity(intent);
									MainActivity.this.finish();
									dialog.dismiss();
									applydb.close();
								}
							});
							
							built.setNegativeButton("取消",new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							Dialog dialog=built.create();
							dialog.show();
						}
						return false;
					}
				});
				mScrollLayout.addView(appPage);
			}
			//加载分页
			pageControl = (PageControlView) findViewById(R.id.pageControl);
			pageControl.bindScrollViewGroup(mScrollLayout);
			//加载分页数据
			dataLoad.bindScrollViewGroup(mScrollLayout);
			
		}
	}

	//统计应用访问次数
	private void appVisitNumChange(String appname){
		for(int i = 0,j = applyList.size();i<j;i++){
			if(applyList.get(i).getLink().equals(appname)){
				int lknum = Integer.valueOf(applyList.get(i).getLknum());
            	applyList.get(i).setLknum( ""+(lknum + 1) );
				break;
			}
		}
	}
	
	//添加桌面应用
	public void creatAddApply() {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.apply_adddialog, null);
		Button dele=(Button)layout.findViewById(R.id.dele);
		ListView dialoglist=(ListView)layout.findViewById(R.id.dialogListView);
		final SliListViewAdapter Adapter=new SliListViewAdapter(MainActivity.this,applyList);
		dialoglist.setAdapter(Adapter);
		final ApplydbHelper applydb=new ApplydbHelper(MainActivity.this); 
		applydb.open(dbname);                               //打开数据库
		dialoglist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				applydb.update(applyList.get(arg2).getId().trim(),"Y");
				Toast.makeText(MainActivity.this,
						"添加" + applyList.get(arg2).getName(), Toast.LENGTH_SHORT).show();

				applyDialog.dismiss();
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MainActivity.class);  //刷新界面
				startActivity(intent);
				finish();

			}
		});
		dele.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				applyDialog.dismiss();
			}
		});
		applydb.close();
		applyDialog = new Dialog(this);
		applyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		applyDialog.setContentView(layout);
		applyDialog.show();
	}
	
		
	//分页数据
	class DataLoading {
		private int count;
		public void bindScrollViewGroup(ScrollLayout scrollViewGroup) {
			this.count=scrollViewGroup.getChildCount();
			scrollViewGroup.setOnScreenChangeListenerDataLoad(new OnScreenChangeListenerDataLoad() {
				public void onScreenChange(int currentIndex) {
					atpage=currentIndex;
					generatePageControl(currentIndex);
				}
			});
			
		}
		
		private void generatePageControl(int currentIndex){
			//如果到最后一页，就加载16条记录
			atpage=currentIndex;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		topview();
	}
	
	@Override
	protected void onDestroy() {
		if(GpsCity != null)
			GpsCity.stopListener();//停止监听
		super.onDestroy();
	}
	
	private long exitTime = 0;
	//捕获返回事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
	        if((System.currentTimeMillis()-exitTime) > 2000){  
	            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                
	            exitTime = System.currentTimeMillis();   
	        } else {
	        	//moveTaskToBack(true);
	        	finish();
	        }
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
    }
	
	/*-----------------------------上传---------------------------------*/
	/**
	 * 上传服务器响应回调
	 */
	@Override
	public void onUploadDone(int responseCode, String message) {
		progressDialog.dismiss();
		Message msg = Message.obtain();
		msg.what = UPLOAD_FILE_DONE;
		msg.arg1 = responseCode;
		msg.obj = message;
		handler.sendMessage(msg);
	}
	
	//上传文件
	private void toUploadFile()
	{
		progressDialog.setMessage("正在上传文件...");
		progressDialog.setIndeterminate(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setProgress(0);
		progressDialog.show();
		String fileKey = "uploadedfile";
		UploadUtil uploadUtil = UploadUtil.getInstance();;
		uploadUtil.setOnUploadProcessListener(this);  //设置监听器监听上传状态
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", "11111");
		uploadUtil.uploadFile( picPath,fileKey, requestURL,params);
	}
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TO_UPLOAD_FILE:
				toUploadFile();
				break;
			
			case UPLOAD_INIT_PROCESS:
				progressDialog.setProgress(msg.arg1);
				break;
			case UPLOAD_IN_PROCESS:
				progressDialog.setProgress(msg.arg1);
				break;
			case UPLOAD_FILE_DONE:
//				String result = "响应码："+msg.arg1+"\n响应信息："+msg.obj+"\n耗时："+UploadUtil.getRequestTime()+"秒";
//				Log.i("MainActivity=======",result);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};

	@Override
	public void onUploadProcess(int uploadSize) {
		Message msg = Message.obtain();
		msg.what = UPLOAD_IN_PROCESS;
		msg.arg1 = uploadSize;
		handler.sendMessage(msg );
	}

	@Override
	public void initUpload(int fileSize) {
		Message msg = Message.obtain();
		msg.what = UPLOAD_INIT_PROCESS;
		msg.arg1 = fileSize;
		handler.sendMessage(msg );
	}
		
}