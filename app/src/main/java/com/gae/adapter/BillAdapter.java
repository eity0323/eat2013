package com.gae.adapter;

import java.util.List;

import com.gae.eat2013.R;
import com.gae.entity.BillItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BillAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<BillItem> mList;
    
    public BillAdapter(Context context,List<BillItem> mdata){
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
             
            convertView = mInflater.inflate(R.layout.bill_listitem, null);
            holder.field1 = (TextView)convertView.findViewById(R.id.monney);
            holder.field2 = (TextView)convertView.findViewById(R.id.xh_money);
            holder.field3 = (TextView)convertView.findViewById(R.id.cz_money);
            holder.field4 = (TextView)convertView.findViewById(R.id.jy_money);
           
            convertView.setTag(holder);
             
        }else {
             
            holder = (ViewHolder)convertView.getTag();
        }
        
        int ind = mList.size()-1-position;
		if(mList.get(ind).getMode().equals("Y")){
			holder.field1.setText((String)mList.get(ind).getTime() + " 年");
		}else{
			holder.field1.setText((String)mList.get(ind).getTime() + " 月");
		}
      
        holder.field2.setText(((String)mList.get(ind).getXh_money()).substring(1));
        holder.field3.setText((String)mList.get(ind).getCz_money());
        holder.field4.setText((String)mList.get(ind).getJy_money());

        return convertView;
    }
 
    
    public final class ViewHolder{
        public TextView field1;
        public TextView field2;
        public TextView field3;
        public TextView field4;
    }

}
