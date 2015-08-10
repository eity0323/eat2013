package com.gae.adapter;
/**
 * author:wing
 * version:2012-03-06 14:06:00
 * describe:购物车列表适配器
 */
import java.util.List;

import com.gae.control.AddCard;
import com.gae.eat2013.R;
import com.gae.entity.CarInfo;
import com.gae.entity.EatParams;
import com.gae.listener.ListListener;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class carfoodAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<CarInfo> mList;//购物车数据源
	private Context cont;
    public carfoodAdapter(Context context,List<CarInfo> mdata){
    	mInflater = LayoutInflater.from(context);
    	mList = mdata;
    	cont = context;
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	 final int pos = position;
        ViewHolder holder = null;
       
        if (convertView == null) {
             
            holder=new ViewHolder();  
             
            convertView = mInflater.inflate(R.layout.food_listitem, null);
            holder.foodname = (TextView)convertView.findViewById(R.id.foodname);
            holder.foodprice = (TextView)convertView.findViewById(R.id.foodprice);
            holder.fooddele = (ImageView)convertView.findViewById(R.id.fooddele);
            holder.foodnumber = (TextView)convertView.findViewById(R.id.foodnumber);
            holder.foodadd = (ImageView)convertView.findViewById(R.id.foodadd);
            
            convertView.setTag(holder);
             
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        
        //减少份数
        holder.fooddele.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		AddCard car = new AddCard((Activity) cont);
        		car.addcarTag = "second";
        		car.setLisener(lisener);
        		car.deleCarUrl(EatParams.getInstance().list.get(pos).getId());
        	}
        });
        //添加份数
        holder.foodadd.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		
        		AddCard car = new AddCard((Activity) cont);
        		car.addcarTag = "second";
        		car.setLisener(lisener);
        		String id = EatParams.getInstance().list.get(pos).getId();
        		String uid = EatParams.getInstance().list.get(pos).getUid();
        		int snum = EatParams.getInstance().list.get(pos).getSnum();
        		car.modifyCarUrl(id, uid, snum + 1);
        	}
        });
        holder.foodname.setText((String)mList.get(position).getPname());
        holder.foodprice.setText((String)mList.get(position).getPrice());
        int sum = EatParams.getInstance().list.get(pos).getSnum();
        holder.foodnumber.setText(sum + "份");
        
        return convertView;
    }
 
    public final class ViewHolder{
        public TextView foodname;
        public TextView foodprice;
        public ImageView fooddele;
        public TextView foodnumber;
        public ImageView foodadd;
    }

    private ListListener lisener = null;
    public void setCarInfoList(ListListener lisen){
    	lisener = lisen;
    }
    
    public void setDataSource(List<CarInfo> mdata){
    	mList = mdata;
    }
}


