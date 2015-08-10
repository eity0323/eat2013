package com.gae.adapter;
/*
 * author:eity
 * version:2013-3-1
 * description:其他页面主view适配器
 * */
import java.util.List;
import java.util.Map;

import com.gae.eat2013.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SetAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Map<String, Object>> mData;					//数据源
//	private OtherListItemListener listener;						//事件监听器
//	public ImageLoader imageLoader; 
	
	public SetAdapter(Context context,List<Map<String,Object>> list){
		mInflater = LayoutInflater.from(context);
		mData = list;
	}
	
	//设置事件监听
//	public void setOtherListItemListener(OtherListItemListener listen){
//		listener = listen;
//	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int currentIndex = position;
		ViewHolder holder = null;
        if (convertView == null) {             
            holder=new ViewHolder();  
            
            convertView = mInflater.inflate(R.layout.set_listitem, null);
            holder.text = (TextView)convertView.findViewById(R.id.set_field1);
            holder.btn = (ImageView)convertView.findViewById(R.id.set_field2);
//            holder.ico = (ImageView)convertView.findViewById(R.id.other_ico);
            
//            holder.btn.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(listener != null){
//						listener.onItemClick(v, currentIndex);
//					}
//				}
//			});
            convertView.setTag(holder);
             
        }else {             
            holder = (ViewHolder)convertView.getTag();
        }
        
        holder.text.setText((String)mData.get(position).get("title"));
//        holder.ico.setBackgroundResource((Integer)mData.get(position).get("info"));
        return convertView;
	}
	
	 public final class ViewHolder{
		 public TextView text;
		 public ImageView btn;
		 public ImageView ico;
	 }

}
