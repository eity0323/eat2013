package com.gae.control;
/*
 * author:eity
 * version:2013-3-1
 * description:信息广播监听
 * */

import com.gae.listener.MsgListener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {  
	private static final String ACTION_SMS_SEND = "com.gae.activity.send";  
    private static final String ACTION_SMS_DELIVERY = "com.gae.activity.delivery"; 
    private static final String ACTION_SMS_RECEIVER = "android.provider.Telephony.SMS_RECEIVED";
    
    private MsgListener mlistener;
    public void setMsgListener(MsgListener listen){
    	mlistener = listen;
    }
    public void onReceive(Context context, Intent intent) {  
        String actionName = intent.getAction();  
        int resultCode = getResultCode();  
        String results = "";
        if (actionName.equals(ACTION_SMS_SEND)) {  
        	results = "发送失败";
            switch (resultCode) {  
	            case Activity.RESULT_OK:  
	            	results = "发送成功";
	                System.out.println("/n[Send]SMS Send:Successed!");  
	                break;  
	            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:  
	            	System.out.println("/n[Send]SMS Send:RESULT_ERROR_GENERIC_FAILURE!");  
	                break;  
	            case SmsManager.RESULT_ERROR_NO_SERVICE:  
	            	System.out.println("/n[Send]SMS Send:RESULT_ERROR_NO_SERVICE!");  
	                break;  
	            case SmsManager.RESULT_ERROR_NULL_PDU:  
	            	System.out.println("/n[Send]SMS Send:RESULT_ERROR_NULL_PDU!");  
	                break;  
	            case SmsManager.RESULT_ERROR_RADIO_OFF:  
	                break;  
            }  
            if(mlistener != null)
            	mlistener.message(actionName, results); 
        } else if (actionName.equals(ACTION_SMS_DELIVERY)) {  
        	results = "发送失败";
            switch (resultCode) {  
	            case Activity.RESULT_OK:  
	            	results = "送达";
	            	System.out.println("/n[Delivery]SMS Delivery:Successed!");  
	                break;  
	            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:  
	            	System.out.println("/n[Delivery]SMS Delivery:RESULT_ERROR_GENERIC_FAILURE!");  
	                break;  
	            case SmsManager.RESULT_ERROR_NO_SERVICE:  
	            	System.out.println("/n[Delivery]SMS Delivery:RESULT_ERROR_NO_SERVICE!");  
	                break;  
	            case SmsManager.RESULT_ERROR_NULL_PDU:  
	            	System.out.println("/n[Delivery]SMS Delivery:RESULT_ERROR_NULL_PDU!");  
	                break;  
	            case SmsManager.RESULT_ERROR_RADIO_OFF:  
	            	System.out.println("/n[Delivery]SMS Delivery:RESULT_ERROR_RADIO_OFF!");  
	                break;  
            }  
            System.out.println("/n正在等待下行短信..."); 
            if(mlistener != null)
            	mlistener.message(actionName, results); 
        } else if (actionName.equals(ACTION_SMS_RECEIVER)) {  
            System.out.println("[Sodino]result = " + resultCode);  
            Bundle bundle = intent.getExtras();  
            if (bundle != null) {  
                Object[] myOBJpdus = (Object[]) bundle.get("pdus");  
                SmsMessage[] messages = new SmsMessage[myOBJpdus.length];  
                for (int i = 0; i < myOBJpdus.length; i++) {  
                    messages[i] = SmsMessage  
                            .createFromPdu((byte[]) myOBJpdus[i]);  
                }  
                SmsMessage message = messages[0];  
                System.out.println("/n短信服务中心号码为："  
                        + message.getServiceCenterAddress());  
                
                results = message.getMessageBody();
                if(mlistener != null)
                	mlistener.message(actionName, results);
            }  
        }  
        
    }  
}