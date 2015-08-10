package com.gae.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gae.eat2013.R;
import com.gae.entity.BuildingItem;
import com.gae.listener.eatery_menu_Listener;

import java.util.List;

public class BuildingListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<BuildingItem> mList;	//大厦据源
	private Context context;
	private eatery_menu_Listener menuListener = null;
	
	public BuildingListAdapter(Context contex,List<BuildingItem> mData) {
    	context= contex;
    	mInflater = LayoutInflater.from(context);
    	mList = mData;
		
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
            convertView = mInflater.inflate(R.layout.selectlistitem, null);
            holder.field1 = (TextView)convertView.findViewById(R.id.addrlistitem);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.field1.setText((String)mList.get(position).getCDNM());		
        return convertView;
	}
	
	
	public final class ViewHolder{
	    public TextView field1;
	}
	public void setEateryMenuListener(eatery_menu_Listener listener){
		menuListener = listener;
	}
	public void getFoodListData(List<BuildingItem> buildinglist){
		mList = buildinglist;
	}
}
