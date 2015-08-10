package com.gae.adapter;

import java.util.List;

import com.gae.eat2013.R;
import com.gae.entity.BookInfo;
import com.gae.listener.FoodOpreateListener;
import com.gae.listener.StoreOpreateListener;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BookAdapter extends BaseAdapter {

	private List<BookInfo> mList;			//订单数据源
	private LayoutInflater mInflater;
	private Activity a;
	
    public BookAdapter(Activity context,List<BookInfo> mdata){
    	a = context;
    	mInflater = LayoutInflater.from(a);
    	mList = mdata;
    }
    public BookAdapter(Activity context){
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
             
            convertView = mInflater.inflate(R.layout.book_item, null);
            holder.field1 = (TextView)convertView.findViewById(R.id.utime);
            holder.field2 = (TextView)convertView.findViewById(R.id.ustate);
            holder.field3 = (TextView)convertView.findViewById(R.id.ubook);
            holder.bookId = (TextView)convertView.findViewById(R.id.bookId);
            convertView.setTag(holder);
             
        }else {
             
            holder = (ViewHolder)convertView.getTag();
        }

        holder.field1.setText((String)mList.get(position).getReqtime());
        holder.field3.setText((String)mList.get(position).getReqinfo());
        holder.bookId.setText((String)mList.get(position).getId());
        
        if(((String)mList.get(position).getAwstate()).equals("Y")){
        	holder.field2.setText("已被接单");
        }else{
        	holder.field2.setText("未被接单");
        	holder.field2.setTextColor(a.getResources().getColor(R.color.gray));
        }
        return convertView;
    }
    
    public final class ViewHolder{
        public TextView field1;
        public TextView field2;
        public TextView field3;
        public TextView bookId;
    }

}
