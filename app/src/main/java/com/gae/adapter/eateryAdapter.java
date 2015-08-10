package com.gae.adapter;
/**
 * author:wing
 * version:2012-03-06 14:06:00
 * describe:餐馆列表适配器
 */
import java.util.List;

import com.gae.eat2013.R;
import com.gae.entity.EatParams;
import com.gae.entity.ShopItem;
import com.gae.listener.eatery_menu_Listener;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class eateryAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<ShopItem> mList;	//餐馆数据源
	private Context context;
	 private eatery_menu_Listener menuListener = null;
    public eateryAdapter(Context contex,List<ShopItem> mdata){
    	context= contex;
    	mInflater = LayoutInflater.from(context);
    	mList = mdata;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder=new ViewHolder();  
            convertView = mInflater.inflate(R.layout.eatery_item, null);
            holder.field1 = (TextView)convertView.findViewById(R.id.vname);
            holder.field2 = (TextView)convertView.findViewById(R.id.tvprice);
            holder.field3 = (TextView)convertView.findViewById(R.id.distance);
            holder.field4 = (RatingBar)convertView.findViewById(R.id.ratingbar);
            holder.field5 = (TextView)convertView.findViewById(R.id.tvstype);
            //holder.field2 = (ImageView)convertView.findViewById(R.id.mbIco);
            // holder.field3 = (Button)convertView.findViewById(R.id.menu);
           
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.field1.setText((String)mList.get(position).getTITLE());
        holder.field2.setText((String)mList.get(position).getPRAVG());
        holder.field3.setText((String)mList.get(position).getDISTAN()+"米");
        if(mList.get(position).getSCAVG()!=null&&mList.get(position).getSCAVG()!=""){
        Float i=Float.parseFloat(mList.get(position).getSCAVG());
        holder.field4.setRating(i);
        }else{
        	holder.field4.setRating(3);
        }
        
//        holder.field4.set mList.get(position).getSCAVG();
        holder.field5.setText((String)mList.get(position).getMCLSN());

        return convertView;
    }
 
    public final class ViewHolder{
        public TextView field1;
        public TextView field2;
        public TextView field3;
        public RatingBar field4;
        public TextView field5;
    }

   
    public void setEateryMenuListener(eatery_menu_Listener listener){
    	menuListener = listener;
    }
    public void getFoodListData(List<ShopItem> foodlist){
    	mList = foodlist;
    }
}


