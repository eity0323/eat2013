package com.gae.eat2013;
/**
 * author:小胡
 * version:2013-5-24
 * description:分享有礼
 * */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.gae.adapter.ShareAdapter;
import com.gae.basic.MyContent;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.oauthv2.OAuthV2Client;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;
import com.weibo.net.AccessToken;
import com.weibo.net.DialogError;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;

public class ShareGiftActivity extends Activity{
	private ListView sharelist;
	private Button shareback;
	
	
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
	 
	Button btn_sina,btn_tencent;
	String accessToken;
	OAuthV2 authV2 = null;//腾讯微博Oauth
	File file = null;
	String basepath = "";
	/*----------------------------分享end--------------------------*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_activity);
		sharelist = (ListView)findViewById(R.id.sharelist);
		List<Map<String,Object>> list = getShareData();
		ShareAdapter shareadapter = new ShareAdapter(this,list);
		sharelist.setAdapter(shareadapter);
	    	
		shareback = (Button)findViewById(R.id.shareback);
		shareback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		sharelist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				if(position == 1){
					share2weixin();
				}else if(position==0){
					share2msg();
				}else if(position == 2){
                	share2tengxun();
                }else if(position == 3){
                	share2xinlang();
                }
				
			}
		});
	}
	
	//分享途径列表
	public List<Map<String,Object>> getShareData(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String,Object>map = new HashMap<String, Object>();
		map.put("title", "短信分享");
        map.put("info", R.drawable.msg);
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("title", "分享到微信");
        map.put("info", R.drawable.weixin);
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("title", "分享到腾讯微博");
        map.put("info", R.drawable.tengxun);
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("title", "分享到新浪微博");
        map.put("info", R.drawable.sina);
        list.add(map);
        
//        map = new HashMap<String, Object>();
//        map.put("title", "分享到人人网");
//        map.put("info", R.drawable.renren);
//        list.add(map);
        
//        map = new HashMap<String, Object>();
//        map.put("title", "分享到其他");
//        map.put("info", R.drawable.sina);
//        list.add(map);
        
        return list;
	}
	
	/*--------------------------------分享------------------------------*/
	private void share2xinlang(){
		Weibo weibo = Weibo.getInstance();
		if(!isOauthSina(weibo)){
			weibo.setupConsumerConfig(CONSUMER_KEY, CONSUMER_SECRET);//设置你的key和secret
			weibo.setRedirectUrl(redirectUriSina);
			weibo.authorize(this, new OauthDialogListener());
		}else{
//			tv.setText("access_token : " + accessToken);
			Toast.makeText(getApplicationContext(), "该用户已经授权", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.putExtra("mtype", "gift");
			intent.putExtra("accessToken", accessToken);
			intent.putExtra("flag", MyContent.SINA);
			intent.setClass(ShareGiftActivity.this, ShareMsgActivity.class);
			startActivity(intent);
		}
	}
	
	private void share2tengxun(){
		//判断用户是否已经授权
        if(!isOauchTen()){
        	//使用回调url来创建授权页面
			authV2 = new OAuthV2(redirectUriTen);
			authV2.setClientId(CLINETID);
			authV2.setClientSecret(CLIENTSECRET);
			//关闭OAuthV2Client中的默认开启的QHttpClient。
	        OAuthV2Client.getQHttpClient().shutdownConnection();
        	Intent intent = new Intent(ShareGiftActivity.this, OAuthV2AuthorizeWebView.class);//创建Intent，使用WebView让用户授权
            intent.putExtra("oauth", authV2);
            startActivityForResult(intent,2); 
        }else{
        	Toast.makeText(getApplicationContext(), "该用户已经授权", Toast.LENGTH_SHORT).show();
        	Intent intent = new Intent();
        	intent.putExtra("oauth", authV2);
			intent.putExtra("flag", MyContent.TENCENT);
			intent.putExtra("mtype", "gift");
			intent.setClass(ShareGiftActivity.this, ShareMsgActivity.class);
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
					intent.setClass(ShareGiftActivity.this, ShareMsgActivity.class);
					startActivity(intent);	
                }
                else
                	Toast.makeText(getApplicationContext(), "登陆失败", Toast.LENGTH_SHORT).show();
            }else{
            	Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
            }
        }
//    	else if(requestCode == RENREN_REQUEST_CODE){
//        	//人人网授权
//        	if (renren != null) {
//    			renren.authorizeCallback(requestCode, resultCode, data);
//    		}else{
//    			Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
//    		}
//        }
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
//			tv.setText("access_token : " + token + "  expires_in: "
//					+ expires_in);
			AccessToken accessToken = new AccessToken(token, CONSUMER_SECRET);
			SharedPreferences sf = getSharedPreferences("token", Context.MODE_PRIVATE);
			sf.edit().putString("access_token", accessToken != null ? accessToken.getToken() : "")
			.commit();
//			accessToken.setExpiresIn(expires_in);
//			Weibo.getInstance().setAccessToken(accessToken);
			Intent intent = new Intent();
			intent.putExtra("accessToken",accessToken.getToken());
			intent.putExtra("flag", MyContent.SINA);
			intent.setClass(ShareGiftActivity.this, ShareMsgActivity.class);
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
			Toast.makeText(ShareGiftActivity.this, "您已经取消授权", Toast.LENGTH_SHORT).show();
		}
    }
    
    //---------------------------------------------------------weixin
    private void share2weixin(){
    	Intent tent = new Intent();
    	tent.putExtra("mtype", "gift");
    	tent.setClass(getApplicationContext(), WeiXinActivity.class);
    	startActivity(tent);
    }
    
    //----------------------------------------------------------msg
    private void share2msg(){
    	Uri uri = Uri.parse("smsto:");            
    	Intent it = new Intent(Intent.ACTION_SENDTO, uri);    
    	it.putExtra("sms_body", "智点，一款不一样的手机点餐应用，你也来试试吧！【http://www.onpad.cn/app/eat2013.apk】");            
    	startActivity(it);
    }
}
