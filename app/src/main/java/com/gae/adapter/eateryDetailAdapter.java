package com.gae.adapter;
/**
 * author:wing
 * version:2012-03-06 14:06:00
 * describe:餐馆菜品列表适配器
 */
import java.util.List;

import com.gae.eat2013.R;
import com.gae.entity.FoodItem;
import com.gae.listener.FoodOpreateListener;
import com.gae.listener.StoreOpreateListener;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class eateryDetailAdapter extends BaseAdapter {

	private List<FoodItem> mList;			//菜品数据源
	private LayoutInflater mInflater;
	private Activity a;
	private FoodOpreateListener foodListener = null;
	private StoreOpreateListener storeListener = null;
	
    public eateryDetailAdapter(Activity context,List<FoodItem> mdata){
    	a = context;
    	mInflater = LayoutInflater.from(a);
    	mList = mdata;
    }
    public eateryDetailAdapter(Activity context){
    	a = context;
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

    ViewHolder holder = null;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
    
        if (convertView == null) {
             
            holder=new ViewHolder();  
             
            convertView = mInflater.inflate(R.layout.eatery_detailitem, null);
            holder.field1 = (TextView)convertView.findViewById(R.id.pname);
            holder.field2 = (TextView)convertView.findViewById(R.id.vname);
            holder.field3 = (ImageView)convertView.findViewById(R.id.storeIco);
            holder.field4 = (TextView)convertView.findViewById(R.id.price);
            holder.field5 = (Button)convertView.findViewById(R.id.btn);
            convertView.setTag(holder);
             
        }else {
             
            holder = (ViewHolder)convertView.getTag();
        }

        holder.field1.setText((String)mList.get(position).getPNAME());
        holder.field2.setText((String)mList.get(position).getVNAME());
        holder.field4.setText((String)mList.get(position).getPRICE());
        if(mList.get(position).getMODE().equals("A")){
        	holder.field2.setVisibility(View.GONE);
        }else{
        	holder.field2.setVisibility(View.VISIBLE);
        }
        FoodItem tempr = mList.get(position);
        holder.field5.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//派发事件
				if(foodListener!=null)
					foodListener.OnListItemClick(position,v);
			}
		});
        holder.field3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(storeListener!=null)
					storeListener.OnListItemClick(position,v);
			}			
		});
        return convertView;
    }
    
    public void setFoodOpreateListener(FoodOpreateListener fl){
    	foodListener = fl;
    }
    public void setStoreOpreateListener(StoreOpreateListener sl){
    	storeListener = sl;
    }
    public final class ViewHolder{
        public TextView field1;
        public TextView field2;
        public ImageView field3;
        public TextView field4;
        public Button field5;
    }

}


