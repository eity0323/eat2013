package com.gae.eat2013;
/**
 * @author eity
 * @version 2013-06-04
 * @description 分享到微信
 * */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gae.basic.CameraUtil;
import com.gae.basic.Constants;
import com.gae.basic.Util;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class WeiXinActivity extends Activity implements IWXAPIEventHandler{


	private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	private IWXAPI api;			//微信api接口
	private EditText et;		//文本内容编辑框
	private CheckBox isTimelineCb;	//是否允许发到好友圈
	private String msgStr = "智点，一款不一样的手机点餐应用，你也来试试吧！【http://www.onpad.cn/app/eat2013.apk】";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
		
		setContentView(R.layout.send_weixin);
		
		initView();
		
		api.handleIntent(getIntent(), this);
		
		//是否分享到好友圈
		isTimelineCb = (CheckBox) findViewById(R.id.is_timeline_cb);
		isTimelineCb.setChecked(false);
		
		//返回
		Button reback = (Button)findViewById(R.id.share_back);
		reback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		//分享文本
		String mstr = getIntent().getStringExtra("mtype");
		if(mstr == "pet"){
			msgStr = "想拥有你自己的餐桌宠物吗 ？下载智点，领养属于你的餐宠吧！【http://www.onpad.cn/app/eat2013.apk】";
		}else{
			msgStr = "智点，一款不一样的手机点餐应用，你也来试试吧！【http://www.onpad.cn/app/eat2013.apk】";
		}
		
		TextView tv = (TextView)findViewById(R.id.share_title);
		tv.setText("分享到微信");
		
		et = (EditText) findViewById(R.id.et);
		et.setText(msgStr);
	}

	//界面对象初始化
	private void initView() {
		// send to weixin
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
						
				// 初始化一个WXTextObject对象
				WXTextObject textObj = new WXTextObject();
				textObj.text = et.getText().toString();

				// 用WXTextObject对象初始化一个WXMediaMessage对象
				WXMediaMessage msg = new WXMediaMessage();
				msg.mediaObject = textObj;
				// 发送文本类型的消息时，title字段不起作用
				// msg.title = "Will be ignored";
				msg.description = et.getText().toString();

				// 构造一个Req
				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
				req.message = msg;
				req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
				
				// 调用api接口发送数据到微信
				api.sendReq(req);
				finish();
			}
		});		
	}

	//接受微信返回
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {

		case 0x101: {
			final WXAppExtendObject appdata = new WXAppExtendObject();
			final String path = CameraUtil.getResultPhotoPath(this, data, SDCARD_ROOT + "/tencent/");
			appdata.filePath = path;
			appdata.extInfo = "this is ext info";

			final WXMediaMessage msg = new WXMediaMessage();
			msg.setThumbImage(Util.extractThumbNail(path, 150, 150, true));
			msg.title = "this is title";
			msg.description = "this is description";
			msg.mediaObject = appdata;
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("appdata");
			req.message = msg;
			req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
			api.sendReq(req);
			
			finish();
			break;
		}
		default:
			break;
		}
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			Toast.makeText(this, "get msg", Toast.LENGTH_LONG).show();		
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			Toast.makeText(this, "show msg", Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		String result = "";
		
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = "成功";
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = "取消";
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = "拒绝";
			break;
		default:
			result = "未知";
			break;
		}
		
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	}
}
