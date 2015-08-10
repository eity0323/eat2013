package com.gae.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gae.eat2013.R;
import com.gae.entity.CarInfo;

import java.util.List;

public class CarListAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private List<CarInfo> mList;						//数据源
    
    public CarListAdapter(Context context,List<CarInfo> mdata){
    	mInflater = LayoutInflater.from(context);
    	mList = mdata;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
             
            holder=new ViewHolder();  
             
            convertView = mInflater.inflate(R.layout.order_listitem, null);
            holder.field1 = (TextView)convertView.findViewById(R.id.otherdate);
            holder.field2 = (TextView)convertView.findViewById(R.id.otherfood);
            holder.field3 = (TextView)convertView.findViewById(R.id.otherprice);
            holder.field4 = (TextView)convertView.findViewById(R.id.foodnum);
           
            convertView.setTag(holder);
             
        }else {
             
            holder = (ViewHolder)convertView.getTag();
        }

        holder.field1.setText((String)mList.get(position).getCtime());
        holder.field2.setText((String)mList.get(position).getPname());
        holder.field3.setText(Float.parseFloat((String)mList.get(position).getPrice())*((int)mList.get(position).getSnum())+"元");
        holder.field4.setText(" " + (int)mList.get(position).getSnum() + " 份");

        return convertView;
    }
 
    
    public final class ViewHolder{
        public TextView field1;
        public TextView field2;
        public TextView field3;
        public TextView field4;
    }
}
