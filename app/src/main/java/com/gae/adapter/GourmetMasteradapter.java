package com.gae.adapter;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gae.eat2013.R;
import com.gae.entity.GourmetMaster;

import java.util.List;

public class GourmetMasteradapter extends BaseAdapter {

	private List<GourmetMaster> mList;			//订单数据源
	private LayoutInflater mInflater;
	private Activity a;
	
    public GourmetMasteradapter(Activity context,List<GourmetMaster> mdata){
    	a = context;
    	mInflater = LayoutInflater.from(a);
    	mList = mdata;
    }
    public GourmetMasteradapter(Activity context){
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
             
            convertView = mInflater.inflate(R.layout.gourmetmaster_item, null);
            holder.field1 = (ImageView)convertView.findViewById(R.id.ivICO);
            holder.field2 = (TextView)convertView.findViewById(R.id.tvNAME);
            holder.field3 = (TextView)convertView.findViewById(R.id.tvRCNUM);
            holder.field4 = (TextView)convertView.findViewById(R.id.tvRINUM);
            convertView.setTag(holder);
             
        }else {
             
            holder = (ViewHolder)convertView.getTag();
        }
        BitmapDrawable drawable = new BitmapDrawable(mList.get(position).getICO());
		holder.field1.setImageDrawable(drawable);
        holder.field2.setText(mList.get(position).getNAME().toString());
        holder.field3.setText(mList.get(position).getRCNUM().toString()+"种");
        holder.field4.setText(mList.get(position).getRINUM().toString()+"张");
        if(mList.get(position).getICO()==null||mList.get(position).getICO().equals("")){
        	holder.field1.setImageResource(R.drawable.somebody);
        }
        /**    
        if(((String)mList.get(position).getAwstate()).equals("Y")){
        	holder.field2.setText("已被接单");
        }else{
        	holder.field2.setText("未被接单");
        	holder.field2.setTextColor(a.getResources().getColor(R.color.gray));
        }**/
        return convertView;
    }
    
    public final class ViewHolder{
        public ImageView field1;
        public TextView field2;
        public TextView field3;
        public TextView field4;
    }

}
