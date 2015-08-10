package com.gae.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gae.eat2013.R;
import com.gae.entity.BookInfo;

public class RestResponseAdapter extends BaseAdapter {
	private List<BookInfo> mList;			//餐馆应答数据源
	private LayoutInflater mInflater;
	private Activity a;
	
    public RestResponseAdapter(Activity context,List<BookInfo> mdata){
    	a = context;
    	mInflater = LayoutInflater.from(a);
    	mList = mdata;
    }
    public RestResponseAdapter(Activity context){
    	a = context;
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	ViewHolder holder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
            
            holder=new ViewHolder();  
             
            convertView = mInflater.inflate(R.layout.responseorder, null);
            holder.field1 = (TextView)convertView.findViewById(R.id.utime);
            holder.field2 = (TextView)convertView.findViewById(R.id.uaddr);
            holder.field3 = (TextView)convertView.findViewById(R.id.ubook);
            holder.field4 = (TextView)convertView.findViewById(R.id.uname);
            holder.field5 = (TextView)convertView.findViewById(R.id.uprice);
            holder.bookId = (TextView)convertView.findViewById(R.id.bookId);
            convertView.setTag(holder);
             
        }else {
             
            holder = (ViewHolder)convertView.getTag();
        }

        holder.field1.setText((String)mList.get(position).getReqtime());
        holder.field2.setText((String)mList.get(position).getAddr());
        holder.field3.setText((String)mList.get(position).getReqinfo());
        holder.field4.setText((String)mList.get(position).getUnm());
        holder.field5.setText((String)mList.get(position).getPrice());
        holder.bookId.setText((String)mList.get(position).getId());
        
        return convertView;
    }
    
    public final class ViewHolder{
        public TextView field1;
        public TextView field2;
        public TextView field3;
        public TextView field4;
        public TextView field5;
        public TextView bookId;
    }

}
