
package com.gae.adapter;

import android.R.integer;
import android.content.Context;
import android.database.DataSetObserver;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.gae.eat2013.R;
import com.gae.entity.ChatMsgEntity;
import com.gae.listener.audioPlayListener;

public class ChatMsgViewAdapter extends BaseAdapter {
	
	public static interface IMsgViewType
	{
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
	
    private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();

    private List<ChatMsgEntity> coll;

    private Context ctx;
    
    private LayoutInflater mInflater;
    
    private audioPlayListener listener;

    public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll) {
        ctx = context;
        this.coll = coll;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return coll.size();
    }

    public Object getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    public void setAudioPlayListener(audioPlayListener listen){
    	listener = listen;
    }


	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
	 	ChatMsgEntity entity = coll.get(position);
	 	
	 	if (entity.getMsgType())
	 	{
	 		return IMsgViewType.IMVT_COM_MSG;
	 	}else{
	 		return IMsgViewType.IMVT_TO_MSG;
	 	}
	 	
	}


	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	ChatMsgEntity entity = coll.get(position);
    	boolean isComMsg = entity.getMsgType();
    		
    	ViewHolder viewHolder = null;	
	    if (convertView == null)
	    {
	    	  if (isComMsg)
			  {
				  convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
			  }else{
				  convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right, null);
			  }

	    	  viewHolder = new ViewHolder();
			  viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			  viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
			  viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			  viewHolder.isComMsg = isComMsg;
			  
			  convertView.setTag(viewHolder);
	    }else{
	        viewHolder = (ViewHolder) convertView.getTag();
	    }
	
	    
	    
	    viewHolder.tvSendTime.setText(entity.getDate());
	    viewHolder.tvUserName.setText(entity.getName());
	    viewHolder.tvContent.setText(entity.getText());
	    viewHolder.tvContent.setTag(entity.getFtag());
	    viewHolder.tvContent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(listener != null){
					listener.onClick(v);
				}
			}
		});
	    
	    return convertView;
    }
    

    static class ViewHolder { 
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public boolean isComMsg = true;
    }


}
