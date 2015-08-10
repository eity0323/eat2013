package com.gae.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gae.eat2013.R;
import com.gae.entity.CookItem;

import java.util.List;

public class CookerAdapter extends BaseAdapter {
	private List<CookItem> mList;			//厨师数据源
	private LayoutInflater mInflater;
	private Activity a;
	
    public CookerAdapter(Activity context,List<CookItem> mdata){
    	a = context;
    	mInflater = LayoutInflater.from(a);
    	mList = mdata;
    }
    public CookerAdapter(Activity context){
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
             
            convertView = mInflater.inflate(R.layout.master_activity, null);
            holder.field1 = (TextView)convertView.findViewById(R.id.uname);
            holder.field2 = (TextView)convertView.findViewById(R.id.star);
            holder.field3 = (TextView)convertView.findViewById(R.id.good);
            holder.field4 = (TextView)convertView.findViewById(R.id.num);
            holder.field5 = (TextView)convertView.findViewById(R.id.bad);
            holder.pic = (ImageView)convertView.findViewById(R.id.img1);
            convertView.setTag(holder);
             
        }else {
             
            holder = (ViewHolder)convertView.getTag();
        }

        holder.field1.setText(mList.get(position).getUnm().toString());
        holder.field2.setText(mList.get(position).getStar().toString());
        holder.field3.setText(mList.get(position).getUp().toString());
        holder.field4.setText(mList.get(position).getNum().toString());
        holder.field5.setText(mList.get(position).getDown().toString());
        //holder.pic.setBackgroundResource((String)mList.get(position).getUico());
       
        return convertView;
    }
    
    public final class ViewHolder{
        public TextView field1;
        public TextView field2;
        public TextView field3;
        public TextView field4;
        public TextView field5;
        public ImageView pic;
    }

}
