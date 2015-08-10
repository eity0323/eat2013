package com.gae.adapter;

import java.util.List;
import java.util.Map;

import com.gae.eat2013.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ShareAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Map<String, Object>> mData;
	
	public ShareAdapter(Context context,List<Map<String,Object>> list){
		mInflater = LayoutInflater.from(context);
		mData = list;
	}
	
	public void setDataSource(List<Map<String,Object>> data){
		mData = data;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
        if (convertView == null) {             
            holder=new ViewHolder();  
            
            convertView = mInflater.inflate(R.layout.shareitem, null);
            holder.text = (TextView)convertView.findViewById(R.id.sharename);
            holder.btn = (TextView)convertView.findViewById(R.id.sharepic);
            convertView.setTag(holder);
             
        }else {             
            holder = (ViewHolder)convertView.getTag();
        }
        
        holder.text.setText((String)mData.get(position).get("title"));
        holder.btn.setBackgroundResource((Integer)mData.get(position).get("info"));
        return convertView;
	}
	
	 public final class ViewHolder{
		 public TextView text;
		 public TextView btn;
	 }
}
