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

import com.gae.UInterface.IMainAction;
import com.gae.adapter.MainGridViewAdapter;
import com.gae.adapter.SliListViewAdapter;
import com.gae.basic.ProgressRing;
import com.gae.basic.UploadUtil;
import com.gae.basic.UploadUtil.OnUploadProcessListener;
import com.gae.entity.EatParams;
import com.gae.presenter.MainPresenter;
import com.gae.view.PageControlView;
import com.gae.view.ScrollLayout;
import com.gae.view.ScrollLayout.OnScreenChangeListenerDataLoad;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements OnUploadProcessListener,IMainAction{
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

    /*初始化app列表页*/
    private static final int INIT_APP_FRAGMENT = 6;

    private static String requestURL = "http://10.168.2.168/mbpic.php";

    private static final float APP_PAGE_SIZE = 9.0f;                  		//每个页面显示应用的个数

    private SlidingDrawer mdrawer = null;                                   // 定义一个抽屉控件
    private TextView center = null;                                         //中间地址
    private ScrollLayout mScrollLayout = null;                              //滑动控件
    private DataLoading dataLoad = null;                                    //数据加载
    private Dialog applyDialog = null;                                      //添加应用对话框
    private SliListViewAdapter sliAdapter = null;                           //抽屉适配器
    private MainGridViewAdapter gridAdapter = null;                         //桌面应用适配器
    private LayoutInflater mLayoutInflater = null;							//

    private LinearLayout lltopView = null;									//广告/个人信息栏

	private PageControlView pageControl = null;                             //分页

    private ProgressDialog progressDialog;
    private Bitmap personImage = null;

	private MyHandler myHandler = null;                                     //线程

	private int atpage=0;                                              		//页码
    private String picPath = null;

    private MainPresenter helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        helper = new MainPresenter(this);
		mLayoutInflater = getLayoutInflater();
        myHandler = new MyHandler(this);
        progressDialog = new ProgressDialog(this);

        initViews();
	}

    private void initViews(){
        Intent tent = getIntent();
        String source = tent.getStringExtra("source");
        if(source != null && source.length() > 0){
            setLocation();
        }

        //初始化界面显示
        initLayout();
    }
	
	private void setLocation(){
		if(helper != null){
            helper.setLocation();
        }
	}
	
	//定位之后显示页面内容
	private void initLayout(){
		//初始化广告栏显示
        lltopView=(LinearLayout)findViewById(R.id.lltopView);
        lltopView.removeAllViews();

        //界面显示
        mdrawer = (SlidingDrawer) findViewById(R.id.slidingdrawer);
        center = (TextView) findViewById(R.id.high_rise);

        AdvertiseLayout();

        //当前地址
        String localpos = EatParams.getInstance().getGpsAddr();
        if(localpos == null || localpos.length() <= 0){
            localpos = "未获取到您的位置";
        }
        center.setText(localpos);

        //抽屉组件
        ListView sligv = (ListView)findViewById(R.id.SlidListView);
        sliAdapter=new SliListViewAdapter(MainActivity.this,helper.getApplyList());
        sligv.setAdapter(sliAdapter);
        sligv.setOnItemClickListener(itemClickListener);

        //切换位置
        Button changCity=(Button)findViewById(R.id.shop_top_right);
        changCity.setOnClickListener(clickListener);

		drawerlistener();                                   //抽屉响应事件
		
		dataLoad = new DataLoading();
		mScrollLayout = (ScrollLayout)findViewById(R.id.ScrollLayoutTest);

        initial();
	}

    private void initial(){
        getAllApply();										//获取应用数据
        initAppFragment();
    }

    private void initAppFragment(){
        //加载数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                myHandler.sendEmptyMessage(INIT_APP_FRAGMENT); // 向Handler发送消息,更新UI
            }
        }).start();
    }

    /* *
     * 广告栏显示
     * */
    private void AdvertiseLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View infoView;
        if(EatParams.getInstance().getSession().length()>0){				//登录状态
            infoView = mLayoutInflater.inflate(R.layout.pernsoninfo, null);
            ImageView hicon = (ImageView)infoView.findViewById(R.id.headIcom);
            if(personImage != null){
                hicon.setImageBitmap(personImage);
            }
            hicon.setOnClickListener(clickListener);

            ImageView ivedit = (ImageView)infoView.findViewById(R.id.user_edit);
            ivedit.setOnClickListener(clickListener);

            TextView name=(TextView)infoView.findViewById(R.id.usename);
            name.setText(EatParams.getInstance().getUsename());

            TextView addr=(TextView)infoView.findViewById(R.id.addr);
            addr.setText("地址："+EatParams.getInstance().getAddr());
        }else{																//未登录状态
            infoView = mLayoutInflater.inflate(R.layout.adinfo_main, null);
            ImageView hicon = (ImageView)infoView.findViewById(R.id.headIcom);
            hicon.setOnClickListener(clickListener);

            TextView ivregister = (TextView)infoView.findViewById(R.id.nouser_register);
            ivregister.setOnClickListener(clickListener);

            TextView ivlogin = (TextView)infoView.findViewById(R.id.nouser_login);
            ivlogin.setOnClickListener(clickListener);

        }
        lltopView.addView(infoView,params);
    }

    private void getAllApply(){
        if(helper != null){
            helper.getAllApply();
        }
    }

    private OnItemClickListener itemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Intent intent=new Intent();
            if(helper.getApplyList().get(position).getLink().equals("ShareEatActivity")){
                ProgressRing.onProgeress(MainActivity.this, "提示", "正在加载手机好友，请稍候....", 4000);
            }

            int lknum = Integer.valueOf(helper.getApplyList().get(position).getLknum());
            helper.getApplyList().get(position).setLknum( ""+(lknum + 1) );

            intent.setClassName(MainActivity.this,"com.gae.eat2013."+helper.getApplyList().get(position).getLink());
            MainActivity.this.startActivity(intent);
            MainActivity.this.finish();
            Toast.makeText(MainActivity.this, "跳转到" +helper.getApplyList().get(position).getLink(), Toast.LENGTH_SHORT).show();
        }
    };

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.shop_top_right:
                    ProgressRing.onProgeress(MainActivity.this, "智点", "正在定位...");

                    if(helper != null){
                        helper.InitGPS();
                    }
                    break;
                case R.id.nouser_login:
                    go2SignalActivity(SetLoginActivity.class);
                    break;
                case R.id.nouser_register:
                    go2SignalActivity( RegisterActivity.class);
                    break;
                case R.id.headIcom:
                    //TODO 判断是否登录
                    //已登录
                    takePhoto();
                    //未登录
                    go2SignalActivity( SetLoginActivity.class);
                    break;
                case R.id.user_edit:
                    //修改个人信息
                    go2SignalActivity(PersonInfoActivity.class);
                    break;
            }
        }
    };

    private void go2SignalActivity(Class<?> activity){
        Intent lintent = new Intent();
        lintent.setClass(getApplicationContext(), activity);
        startActivity(lintent);
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

	class MyHandler extends Handler {
		private MainActivity mContext;
		public MyHandler(Context conn) {
			mContext = (MainActivity)conn;
		}

		public MyHandler(Looper L) {
			super(L);
		}

		// 子类必须重写此方法,接受数据
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			final int pageNo = (int)Math.ceil( helper.getApplygList().size()/APP_PAGE_SIZE);
			for (int i = 0; i < pageNo; i++) {
                mScrollLayout.addView( createAppFragment(mContext,i) );
			}
			//加载分页
			pageControl = (PageControlView) findViewById(R.id.pageControl);
			pageControl.bindScrollViewGroup(mScrollLayout);
			//加载分页数据
			dataLoad.bindScrollViewGroup(mScrollLayout);
			
		}
	}

    //创建app滑动页
    private GridView createAppFragment(Context mContext, int i){
        GridView appPage = new GridView(mContext);
        // get the "i" page data
        gridAdapter=new MainGridViewAdapter(mContext, helper.getApplygList(), i);
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
                int longdk = helper.getApplygList().size();
                if(num*APP_PAGE_SIZE + arg2==helper.getApplygList().size()-1){    //最后一个应用
                    Toast.makeText(MainActivity.this, "添加应用", Toast.LENGTH_SHORT).show();
                    creatAddApply();
                }else{
                    Intent intent=new Intent();
                    if(helper.getApplygList().get((int) (num * APP_PAGE_SIZE + arg2)).getLink().equals("ShareEatActivity")){
                        ProgressRing.onProgeress(MainActivity.this, "提示", "正在加载手机好友，请稍候....", 4000);
                    }
                    String applink = helper.getApplygList().get((int) (num * APP_PAGE_SIZE + arg2)).getLink();
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
                if(num*APP_PAGE_SIZE+arg2!=helper.getApplygList().size()-1){
                    AlertDialog.Builder built=new AlertDialog.Builder(MainActivity.this);
                    built.setTitle("提示");
                    built.setMessage("您确定要删除《"+helper.getApplygList().get((int) (num * APP_PAGE_SIZE + arg2)).getName()+"》桌面应用");

                    built.setNeutralButton("确认",new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            helper.deletePlugin((int) (num*APP_PAGE_SIZE+arg2));
                            finish();
                            dialog.dismiss();
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

        return  appPage;
    }

    @Override
    public void updateCityView(String city) {
        if(center != null)center.setText(city);
    }

    //统计应用访问次数
	private void appVisitNumChange(String appname){
		if (helper != null){
            helper.appVisitNumChange(appname);
        }
	}
	
	//添加桌面应用
	public void creatAddApply() {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.apply_adddialog, null);
		Button dele=(Button)layout.findViewById(R.id.dele);

		ListView dialoglist=(ListView)layout.findViewById(R.id.dialogListView);
		final SliListViewAdapter Adapter=new SliListViewAdapter(MainActivity.this,helper.getApplyList());
		dialoglist.setAdapter(Adapter);
		dialoglist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                helper.updatePlugin(arg2);

                Toast.makeText(MainActivity.this,
                        "添加" + helper.getApplyList().get(arg2).getName(), Toast.LENGTH_SHORT).show();

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
	}
	
	@Override
	protected void onDestroy() {
		helper.removeGPSListener();
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