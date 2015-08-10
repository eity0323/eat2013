package com.gae.adapter;
/**
 * author:wing
 * version:2012-03-06 14:06:00
 * describe:餐馆菜品列表适配器
 */
import java.util.List;
import java.util.Map;

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

public class StoreAdapter extends BaseAdapter {

	private List<FoodItem> mList;			//菜品数据源
	private LayoutInflater mInflater;
	private Activity a;
	private FoodOpreateListener foodListener = null;
	private StoreOpreateListener storeListener = null;
	private Map<Integer, Boolean> mMap;
	
    public StoreAdapter(Activity context,List<FoodItem> mdata,Map<Integer, Boolean> map){
    	a = context;
    	mInflater = LayoutInflater.from(a);
    	mList = mdata;
    	this.mMap = map;
    }
    public StoreAdapter(Activity context){
    	a = context;
    }
    public void changeSelectMap(Map<Integer, Boolean> map) {
		this.mMap = map;
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
             
            convertView = mInflater.inflate(R.layout.storelist_item, null);
            holder.foodname = (TextView)convertView.findViewById(R.id.foodname);
         //   holder.xiaolang = (TextView)convertView.findViewById(R.id.xiaolang);
            holder.shopname = (TextView)convertView.findViewById(R.id.shopname);
         //   holder.distance = (TextView)convertView.findViewById(R.id.distance);
            holder.foodprice = (TextView)convertView.findViewById(R.id.foodprice);
            convertView.setTag(holder);
             
        }else {
             
            holder = (ViewHolder)convertView.getTag();
        }
        if (mMap.get(position)) {
        	convertView.setBackgroundResource(R.drawable.fooditemon);
        	mList.get(position).setSTATE(true);
		} else {
			convertView.setBackgroundResource(R.drawable.fooditmeoff);
			mList.get(position).setSTATE(false);
		}
        holder.foodname.setText((String)mList.get(position).getPNAME());
        holder.shopname.setText("出品："+(String)mList.get(position).getVNAME());
     //   holder.distance.setText((String)mList.get(position).getDIST());
        holder.foodprice.setText("￥"+(String)mList.get(position).getPRICE()+"元");

        FoodItem tempr = mList.get(position);

        return convertView;
    }
    
    public void setFoodOpreateListener(FoodOpreateListener fl){
    	foodListener = fl;
    }
    public void setStoreOpreateListener(StoreOpreateListener sl){
    	storeListener = sl;
    }
    public final class ViewHolder{
        public TextView foodname;
        public TextView xiaolang;//销量
        public TextView shopname;
        public TextView distance;
        public TextView foodprice;
    }

}


