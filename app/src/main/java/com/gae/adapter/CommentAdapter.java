package com.gae.adapter;

import java.util.List;

import com.gae.eat2013.R;
import com.gae.entity.CommentItem;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {
	private List<CommentItem> mList;			//评论数据源
	private LayoutInflater mInflater;
	private Activity a;
	
    public CommentAdapter(Activity context,List<CommentItem> mdata){
    	a = context;
    	mInflater = LayoutInflater.from(a);
    	mList = mdata;
    }
    public CommentAdapter(Activity context){
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
             
            convertView = mInflater.inflate(R.layout.commentitem, null);
            holder.field1 = (TextView)convertView.findViewById(R.id.txtname);
            holder.field2 = (TextView)convertView.findViewById(R.id.txttime);
            holder.field3 = (TextView)convertView.findViewById(R.id.mark);
            holder.pic = (ImageView)convertView.findViewById(R.id.picname);
            convertView.setTag(holder);
             
        }else {
             
            holder = (ViewHolder)convertView.getTag();
        }

        holder.field1.setText((String)mList.get(position).getName());
        holder.field2.setText(((String)mList.get(position).getTime()).substring(0,10));
        holder.field3.setText((String)mList.get(position).getMark());
        //holder.pic.setBackgroundResource((Integer)mList.get(position).getPic());
       
        return convertView;
    }
    
    public final class ViewHolder{
        public TextView field1;
        public TextView field2;
        public TextView field3;
        public ImageView pic;
    }

}
