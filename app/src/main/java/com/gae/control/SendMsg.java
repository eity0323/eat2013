package com.gae.control;
/*
 * author:eity
 * version:2013-3-1
 * description:发送信息
 * */
import java.util.List;

import com.gae.listener.MsgListener;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.gsm.SmsManager;

public class SendMsg {
	private static final String ACTION_SMS_SEND = "com.gae.activity.send";  
    private static final String ACTION_SMS_DELIVERY = "com.gae.activity.delivery";  
    private static final String ACTION_SMS_RECEIVER = "android.provider.Telephony.SMS_RECEIVED";
    
	private SMSReceiver sendReceiver;  
	private SMSReceiver deliveryReceiver;  
	private SMSReceiver smsReceiver;  
	
	private Context mcontext;
	private MsgListener mlisten;
	public SendMsg(Context context){
		mcontext = context;
		// 注册send  
        sendReceiver = new SMSReceiver();  
        sendReceiver.setMsgListener(mlisten);
        IntentFilter sendFilter = new IntentFilter(ACTION_SMS_SEND);  
        context.registerReceiver(sendReceiver, sendFilter);  
        // 注册delivery  
        deliveryReceiver = new SMSReceiver();  
        deliveryReceiver.setMsgListener(mlisten);
        IntentFilter deliveryFilter = new IntentFilter(ACTION_SMS_DELIVERY);  
        context.registerReceiver(deliveryReceiver, deliveryFilter);  
        // 注册接收下行receiver  
        smsReceiver = new SMSReceiver();  
        smsReceiver.setMsgListener(mlisten);
        IntentFilter receiverFilter = new IntentFilter(ACTION_SMS_RECEIVER);  
        context.registerReceiver(smsReceiver, receiverFilter); 
	}
	
	public void setMsgListener(MsgListener listen){
		mlisten = listen;
	}
	//发送信息
	public void send(String mb,String content){
		SmsManager smsMag = SmsManager.getDefault();
		Intent sendIntent = new Intent(ACTION_SMS_SEND);  
        PendingIntent sendPI = PendingIntent.getBroadcast(mcontext, 0, sendIntent,  
                0);  
        Intent deliveryIntent = new Intent(ACTION_SMS_DELIVERY);  
        PendingIntent deliveryPI = PendingIntent.getBroadcast(mcontext, 0,  
                deliveryIntent, 0);  
        if(content.length() >= 70){
            //短信字数大于70，自动分条
            List<String> ms = smsMag.divideMessage(content);           
            for(String str : ms ){
                //短信发送
            	smsMag.sendTextMessage(mb, null, str, sendPI, null);
            }
        }else
        {
        	smsMag.sendTextMessage(mb, null, content, sendPI, null);
        }        
//        smsMag.sendTextMessage(mb, null, content, sendPI, deliveryPI);  
	}
	//注销监听
	public void release(){
		mcontext.unregisterReceiver(sendReceiver);  
		mcontext.unregisterReceiver(deliveryReceiver);  
		mcontext.unregisterReceiver(smsReceiver);  
	}
}
