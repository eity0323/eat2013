package com.gae.eat2013;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


import com.gae.basic.MyContent;
import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 腾讯微博，新浪微博   简单的发送一条微博，可以进你自己的微博查看是否发送成功
 * @author yanbin
 */
public class ShareMsgActivity extends Activity {
	private Button btnSend = null;				//确认分享
	private EditText et = null;					//文本编辑
	private String accessToken = null;			//授权码		
	private OAuthV2 oAuthV2 = null,oAuthV2_2 = null;//授权对象
	private int flag = 0 ;						//flag用来标记是来自新浪，腾讯微博还是人人
	private String msgStr = "智点，一款不一样的手机点餐应用，你也来试试吧！【http://www.onpad.cn/app/eat2013.apk】";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_weibo);
		
		//获取授权码
		accessToken = getIntent().getStringExtra("accessToken");
		//判断授权类型
		flag = getIntent().getIntExtra("flag", -1);
		//获取授权对象
		oAuthV2 =  (OAuthV2) getIntent().getSerializableExtra("oauth");
		
		btnSend = (Button) findViewById(R.id.btnSend);
		et = (EditText) findViewById(R.id.et);
		
		//返回
		Button reback = (Button)findViewById(R.id.share_back);
		reback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		TextView tv = (TextView)findViewById(R.id.share_title);
		tv.setText("分享到微博");
		
		String mstr = getIntent().getStringExtra("mtype");
		if(mstr == "pet"){
			msgStr = "想拥有你自己的餐桌宠物吗 ？下载智点，领养属于你的餐宠吧！【http://www.onpad.cn/app/eat2013.apk】";
		}else{
			msgStr = "智点，一款不一样的手机点餐应用，你也来试试吧！【http://www.onpad.cn/app/eat2013.apk】";
		}
		et.setText(msgStr);
	}
	
	//点击事件
	public void click(View view){
		int id = view.getId();
		switch (id) {
			case R.id.btnSend:
				switch (flag) {
					case MyContent.SINA:				//新浪
						Weibo weibo = Weibo.getInstance();
						WeiboParameters parameters = new WeiboParameters();
						parameters.add("access_token", accessToken);
						parameters.add("status", et.getText().toString());
						//发送一条微博 url https://api.weibo.com/2/statuses/update.json
						try {
							String flag = weibo.request(this, Weibo.SERVER + "statuses/update.json", parameters, Utility.HTTPMETHOD_POST, weibo.getAccessToken());
							Toast.makeText(this, "发送微博成功", Toast.LENGTH_SHORT).show();
						} catch (WeiboException e) {
							e.printStackTrace();
							Toast.makeText(this, "发送微博失败", Toast.LENGTH_SHORT).show();
						}
						finish();
						break;
					case MyContent.TENCENT:				//腾讯
						TAPI tapi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
						 try {
		                     String response=tapi.add(oAuthV2, "json", et.getText().toString(), getHostIp());
		                     tapi.shutdownConnection();
		    				 Toast.makeText(this, "发送微博成功", Toast.LENGTH_SHORT).show();
		                 } catch (Exception e) {
		                     e.printStackTrace();
		                     Toast.makeText(this, "发送微博失败", Toast.LENGTH_SHORT).show();
		                 }
						 finish();
		                 break;
				}
				break;
		}
	}
	 /**
     * 获取用户ip
     * @return
     */
	public static String getHostIp() {	
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
						.hasMoreElements();) {
					InetAddress inetAddress = ipAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {						
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
		} catch (Exception e) {
		}
		return null;
	}
}
