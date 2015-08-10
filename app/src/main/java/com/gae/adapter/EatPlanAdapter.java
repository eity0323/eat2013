package com.gae.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gae.eat2013.R;
import com.gae.entity.EatPlanItem;

import java.util.List;

public class EatPlanAdapter extends BaseAdapter {

	private List<EatPlanItem> mList;			//订单数据源
	private LayoutInflater mInflater;
	private Activity a;
	
    public EatPlanAdapter(Activity context,List<EatPlanItem> mdata){
    	a = context;
    	mInflater = LayoutInflater.from(a);
    	mList = mdata;
    }
    public EatPlanAdapter(Activity context){
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
             
            convertView = mInflater.inflate(R.layout.eatplan_item, null);
            holder.field1 = (TextView)convertView.findViewById(R.id.planweek);
            holder.field2 = (TextView)convertView.findViewById(R.id.plantitle);
            holder.field3 = (TextView)convertView.findViewById(R.id.planinfo);
            convertView.setTag(holder);
             
        }else {
             
            holder = (ViewHolder)convertView.getTag();
        }

        holder.field1.setText(mList.get(position).getWeek().toString());
        holder.field2.setText(mList.get(position).getTitle().toString());
        holder.field3.setText(mList.get(position).getDetails().toString());
        
      
        return convertView;
    }
    
    public final class ViewHolder{
        public TextView field1;
        public TextView field2;
        public TextView field3;
      
    }

}
