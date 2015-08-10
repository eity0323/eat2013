package com.gae.adapter;

import java.util.List;

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
import android.widget.TextView;
import android.widget.Toast;

import com.gae.eat2013.R;
import com.gae.entity.EatParams;
import com.gae.entity.ManagerItem;
import com.gae.entity.ShopItem;
import com.gae.listener.eatery_menu_Listener;
import com.gae.listener.manager_menu_Listener;
public class ManagerAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<ManagerItem> mList;//数据源
	private Context mcontext;
	private manager_menu_Listener menuListener = null;
    public ManagerAdapter(Context context,List<ManagerItem> mdata){
    	mInflater = LayoutInflater.from(context);
    	mList = mdata;
    	mcontext=context;
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
             
            convertView = mInflater.inflate(R.layout.manager_list_item, null);
            holder.name = (TextView)convertView.findViewById(R.id.shopName);
            holder.phone = (ImageView)convertView.findViewById(R.id.shopPhone);
            holder.order = (Button)convertView.findViewById(R.id.shopOrder);

            convertView.setTag(holder);
             
        }else {
             
            holder = (ViewHolder)convertView.getTag();
        }
        holder.order.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				String id=mList.get(position).did;
//				Intent intent = new Intent(mcontext,ShopListItemActivity.class);
//				intent.putExtra("id", id);//设置传递内容。
//				intent.putExtra("name", mList.get(position).rname);
//				intent.putExtra("mb", mList.get(position).mb);
//			    mcontext.startActivity(intent);	
				//菜单点击事件
				if (menuListener != null) {
					menuListener.OnListItemClick(position,v);
				}
				
			}
		});
        holder.phone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String mb=mList.get(position).mb;
				if(!EatParams.getInstance().checkDevices(mcontext).equals("phone")){
					Toast.makeText(mcontext, "此设备不能打电话，请确认！", Toast.LENGTH_LONG).show();
					return;
				}	
				Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+mb));  
				mcontext.startActivity(intent);  
			}
		});
        holder.name.setText((String)mList.get(position).getRname());
        //holder.phone.setText((String)mList.get(position).getMb());       
         

        return convertView;
    }
 
    
    public final class ViewHolder{
        public TextView name;
        public ImageView phone;
        public Button order;
    }
    public void setShopMenuListener(manager_menu_Listener listener){
    	menuListener = listener;
    }
    public void getFoodListData(List<ManagerItem> foodlist){
    	mList = foodlist;
    }

}