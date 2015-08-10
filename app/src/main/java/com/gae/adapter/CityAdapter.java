package com.gae.adapter;
/**
 * author:wing
 * version:2012-03-06 14:06:00
 * describe:餐馆列表适配器
 */
import java.util.ArrayList;
import java.util.List;

import com.gae.eat2013.R;
import com.gae.entity.cityItem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CityAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<cityItem> c1List;	//附近分类数据源
	private Context context;
	
    public CityAdapter(Context contex,ArrayList<cityItem> list){
    	context= contex;
    	mInflater = LayoutInflater.from(context);
    	c1List = list;    	
    }
    @Override
    public int getCount() {
    	if (c1List != null) {
    		return c1List.size();
		}else{
			return 0;
		}
    }

    @Override
    public Object getItem(int position) {
        return c1List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder=new ViewHolder();              
            convertView = mInflater.inflate(R.layout.city_listitem, null);            
            holder.text = (TextView)convertView.findViewById(R.id.cityItem);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        
//        holder.image.setImageResource(c1List.get(position).getPicId());
        holder.text.setText(c1List.get(position).getConm());

        return convertView;
    }
 
    public final class ViewHolder{
        public TextView text;
    }
}


