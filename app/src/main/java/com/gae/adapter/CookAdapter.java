package com.gae.adapter;

import java.util.List;

import com.gae.adapter.CommentAdapter.ViewHolder;
import com.gae.eat2013.R;
import com.gae.entity.CookItem;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CookAdapter extends BaseAdapter {
	private List<CookItem> mList;			//菜谱数据源
	private LayoutInflater mInflater;
	private Activity a;
	
    public CookAdapter(Activity context,List<CookItem> mdata){
    	a = context;
    	mInflater = LayoutInflater.from(a);
    	mList = mdata;
    }
    public CookAdapter(Activity context){
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
             
            convertView = mInflater.inflate(R.layout.cookitem, null);
            holder.field1 = (TextView)convertView.findViewById(R.id.cname);
            holder.field2 = (TextView)convertView.findViewById(R.id.method);
            holder.field3 = (TextView)convertView.findViewById(R.id.degree);
            holder.field4 = (TextView)convertView.findViewById(R.id.renqi);
            holder.field5 = (TextView)convertView.findViewById(R.id.taste);
            holder.field6 = (TextView)convertView.findViewById(R.id.time);
            holder.pic = (ImageView)convertView.findViewById(R.id.pic);
            convertView.setTag(holder);
             
        }else {
             
            holder = (ViewHolder)convertView.getTag();
        }

        holder.field1.setText(mList.get(position).getCname().toString());
        holder.field2.setText("做法：" + mList.get(position).getMethod().toString());
        holder.field3.setText("难度：" + mList.get(position).getDifficulty().toString());
        holder.field4.setText("顶：" + mList.get(position).getUp() + "人   踩：" + mList.get(position).getDown().toString() + "人");
        holder.field5.setText("口味：" + mList.get(position).getTaste().toString());
        holder.field6.setText("时间：" + mList.get(position).getTime().toString() + "分钟");
        //holder.pic.setBackgroundResource((String)mList.get(position).getIco());
       
        return convertView;
    }
    
    public final class ViewHolder{
        public TextView field1;
        public TextView field2;
        public TextView field3;
        public TextView field4;
        public TextView field5;
        public TextView field6;
        public ImageView pic;
    }

}
