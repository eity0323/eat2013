package com.gae.eat2013;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.gae.adapter.ChatMsgViewAdapter;
import com.gae.entity.ChatMsgEntity;
import com.gae.listener.audioPlayListener;
import com.gae.view.AudioRecorder;
import com.gae.view.MyDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * 
 * @author eity
 * @version 2013-5-18
 * @description 语音订餐
 * 
 */
public class SpellOrderActivity extends Activity {

	private Button record = null;					//录音按钮
	private MyDialog dialog = null;					//录音弹出框
	private AudioRecorder mr = null;				//录音对象
	private MediaPlayer mediaPlayer = null;			//播放对象
	private File directory = null;					//文件路径
	private int current = 0;						//当前索引
	private int model = 1;							//模式 0 文本，1录音
	
	private Button mBtnSend = null;					//发送按钮
	private Button mBtnBack = null;					//返回按钮
	private EditText mEditTextContent = null;		//输入框
	private Button mBtnChg = null;					//切换模式
	private Button mBtnChg2 = null;
	
	private LinearLayout botLayout;
	private RelativeLayout botLayout2;
	
	private DateFormat formatFN = null;				//音频文件名称格式
	private DateFormat formatTime = null;			//时间格式
	private String tpfileName = "";					//录制文件名称
	private String file_pre = "";					//音频文件前缀
	private String resultStr = "";					//
	private String actionUrl = "http://10.168.2.168/mbpic.php";
	
	private String newName = "jankey.3gp";			//服务器上音频文件名称
	private String uploadFile = "/sdcard/jankey.3gp";//音频文件本地路径
	
	private ListView mListView = null;				//对话列表
	private ChatMsgViewAdapter mAdapter = null;		//对话适配器
	private List<ChatMsgEntity> mDataArrays = null;	//对话数据源

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_audio);
		
		mDataArrays = new ArrayList<ChatMsgEntity>();

		directory = Environment.getExternalStorageDirectory();	
		file_pre = directory.getAbsolutePath() + "/audiorder/";
		formatFN = new SimpleDateFormat("yyyyMMddHHmmss");
		formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		initBody();
	}
	
	//初始化界面显示
	private void initBody(){
		
		botLayout = (LinearLayout)findViewById(R.id.rl_bottom);
		botLayout2 = (RelativeLayout)findViewById(R.id.rl_bottom2);
		
		//录音按钮
		record = (Button) this.findViewById(R.id.record);
		mListView = (ListView) this.findViewById(R.id.audioList);
		record.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				recordStart();
				return false;
			}
		});
		record.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						recordStop();
						refreshView();
						break;
				}
				return false;
			}
		});
		
		//切换模式按钮
		mBtnChg = (Button)this.findViewById(R.id.chg_model);
		mBtnChg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				model = model == 0?1:0;
				changeModel();
			}
		});
		
		mBtnChg2 = (Button)this.findViewById(R.id.chg_model2);
		mBtnChg2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				model = model == 0?1:0;
				changeModel();
			}
		});
		
		initView();		        
	    initData();
		showView();		 
	    
	}
	
	//切换模式
	private void changeModel(){
		//启动activity时不自动弹出软键盘
		if(model == 1){
			botLayout.setVisibility(View.VISIBLE);
			botLayout2.setVisibility(View.GONE);
		}else{
			botLayout.setVisibility(View.GONE);
			botLayout2.setVisibility(View.VISIBLE);	
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		}
	}
	
	
	//开始录音
	String lastFileName = "";
	private void recordStart(){
		Date date = new Date(System.currentTimeMillis());
		tpfileName = "C_" + formatFN.format(date);		
		lastFileName = formatTime.format(date);;
		
		mr = new AudioRecorder("audiorder/"+tpfileName);
		dialog = new MyDialog(SpellOrderActivity.this, "正在录音");
		try {
			record.setText("正在录音...");
			mr.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dialog.show();
		current++;
	}
	
	//停止录音
	private void recordStop(){
		try {
			if(mr != null)
				mr.stop();
			record.setText("录音停止!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(dialog != null)
			dialog.dismiss();
	}

	//界面对话布局
	private View curView;
	private void showView() {
		playFile();
		for (int i = apklist.size() -1; i >= 0; i--) {
			ChatMsgEntity entity = new ChatMsgEntity();

			String fstr = apklist.get(i);
			File f = new File( fstr );
			Date date=new Date(f.lastModified());
			String ftime = formatTime.format(date);

			entity.setDate(ftime);
			entity.setFtag(fstr);
			
			if (fstr.indexOf("C_") != -1)
    		{   			
    			entity.setName("人马");
    			entity.setMsgType(false);
    		}else{
    			entity.setName("小黑");
    			entity.setMsgType(true);
    		}
			mDataArrays.add(entity);
		}
		
		mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mAdapter.setAudioPlayListener(new audioPlayListener() {
			
			@Override
			public void onClick(View v) {
				String fname = file_pre + v.getTag().toString() + ".3gp";
				File f = new File( fname );
				if(f.isFile()){
					playMusic(f);
				}
			}
		});
		mListView.setAdapter(mAdapter);
	}
	
	//刷新对话显示
	private void refreshView(){
		if (lastFileName.length() > 0)
		{
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(lastFileName);
			entity.setName("人马");
			entity.setMsgType(false);
			entity.setFtag(tpfileName);
			
			mDataArrays.add(entity);
			mAdapter.notifyDataSetChanged();
			
			mListView.setSelection(mListView.getCount() - 1);
		}
		doUploadHandler();
	}
	
	//下载文件
	private void doUploadHandler(){
		newName = tpfileName + ".3gp";
		uploadFile = file_pre + tpfileName + ".3gp";
		
		FileUploadTask fileuploadtask = new FileUploadTask();
		fileuploadtask.execute();
	}
	
	//播放录音文件
	private void playMusic(File f){
		mediaPlayer = new MediaPlayer();
		mediaPlayer.reset();// 重置  
		
		try {
			mediaPlayer.setDataSource(SpellOrderActivity.this, Uri.fromFile(f));
			//mediaPlayer.setDataSource(path);// 设置数据源  
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}// 准备  
		mediaPlayer.start();// 开始播放  
		mediaPlayer  
		        .setOnCompletionListener(new OnCompletionListener() {  
		
		            @Override  
		            public void onCompletion(MediaPlayer mp) {  
		                //NextMusic();  
		            	if(curView != null){
		            		((Button)curView).setBackgroundResource(R.drawable.audio_btn);
		            	}
		            }  
		
		        });  
		mediaPlayer.setOnErrorListener(new OnErrorListener() {  
		
		    @Override  
		    public boolean onError(MediaPlayer mp, int what, int extra) {  
		    	mediaPlayer.reset();  
		        return true;  
		    }  
		
		});  
	}

	//获取播放录音
	private void playFile() {
		String tpath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/audiorder";
		List<String> getFiles = GetFiles(
				tpath + "/", ".3gp", true);
		for (String string : getFiles) {
			System.out.println(string);
		}
	}

	//获取文件列表
	private List<String> apklist = new ArrayList<String>(); 
	public List<String> GetFiles(String Path, String Extension,  
	        boolean IsIterative) 
	{  
	    File[] files = new File(Path).listFiles();  
	    if(files == null) return apklist;
	    
	    for (int i = 0; i < files.length; i++) {  
	        File f = files[i];  
	        if (f.isFile()) {  
	            if (f.getPath()  
	                    .substring(f.getPath().length() - Extension.length())  
	                    .equals(Extension))
	                apklist.add(f.getPath());  
	            if (!IsIterative)  
	                break;  
	        } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1)   
	            GetFiles(f.getPath(), Extension, IsIterative); 
	    }  
	    return apklist;  
	}  
	
	@Override
	protected void onStop(){
		//是否启动悬浮菜单
		super.onStop();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	
	
	//====================================================初始化界面显示
	 
    public void initView()
    {
    	mListView = (ListView) findViewById(R.id.audioList);
    	mBtnSend = (Button) findViewById(R.id.btn_send);
    	mBtnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				send();
			}
		});
    	mBtnBack = (Button) findViewById(R.id.rec_btn_back);
    	mBtnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    	
    	mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
    }
    
    //初始化对话数据
    private String[]msgArray = new String[]{"你好，有什么可以为您服务的？"};
    private final static int COUNT = 1;
    public void initData()
    {
    	for(int i = 0; i < COUNT; i++)
    	{
    		ChatMsgEntity entity = new ChatMsgEntity();
    		Date date=new Date(System.currentTimeMillis());
			String ftime= formatTime.format(date);
    		entity.setDate(ftime);
    		entity.setFtag("TXT");
    		if (i % 2 == 0)
    		{
    			entity.setName("客服");
    			entity.setMsgType(true);
    		}else{
    			entity.setName("您");
    			entity.setMsgType(false);
    		}
    		
    		entity.setText(msgArray[i]);
    		mDataArrays.add(entity);
    	}

    	mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
		
    }
    
    //发送事件
    private void send()
	{
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0)
		{
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(getDate());
			entity.setName("人马");
			entity.setMsgType(false);
			entity.setText(contString);
			entity.setFtag("TXT");
			
			mDataArrays.add(entity);
			mAdapter.notifyDataSetChanged();
			
			mEditTextContent.setText("");
			
			mListView.setSelection(mListView.getCount() - 1);
		}
	}
	
    //获取时间字符串
    private String getDate() {
        Calendar c = Calendar.getInstance();

        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH));
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));

        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins); 
					
        return sbBuffer.toString();
    }
    
    //============================================================上传录音文件
    class FileUploadTask extends AsyncTask<Object, Integer, Void> {
		private ProgressDialog dialog = null;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(SpellOrderActivity.this);
			dialog.setMessage("正在上传...");
			dialog.setIndeterminate(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setProgress(0);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Object... arg0) {
			uploadFile();
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			dialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				dialog.dismiss();
				showDialog(resultStr);
			} catch (Exception e) {
			}
		}
	}

	/* 上传文件吹Server的method */
	private void uploadFile() {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设定传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			/* 设定DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; "
					+ "name=\"uploadedfile\";filename=\"" + newName + "\""
					+ end);
			ds.writeBytes(end);

			/* 取得文件的FileInputStream */
			FileInputStream fStream = new FileInputStream(uploadFile);
			/* 设定每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			int length = -1;
			/* 从文件读取数据到缓冲区 */
			while ((length = fStream.read(buffer)) != -1) {
				/* 将数据写入DataOutputStream中 */
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

			/* close streams */
			fStream.close();
			ds.flush();

			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			/* 将Response显示于Dialog */
			// showDialog(b.toString().trim());
			resultStr = b.toString().trim();
			/* 关闭DataOutputStream */
			ds.close();
		} catch (Exception e) {
			// showDialog(""+e);
		}
	}

	/* 显示Dialog的method */
	private void showDialog(String mess) {
		new AlertDialog.Builder(SpellOrderActivity.this).setTitle("Message")
				.setMessage(mess)
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}
}
