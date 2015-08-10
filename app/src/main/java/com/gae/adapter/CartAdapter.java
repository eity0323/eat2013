package com.gae.adapter;
/**
 * author:wing
 * version:2012-03-06 14:06:00
 * describe:购物车列表适配器
 */
import java.util.List;
import java.util.Map;

import com.gae.control.AddCard;
import com.gae.eat2013.R;
import com.gae.entity.CarInfo;
import com.gae.entity.EatParams;
import com.gae.listener.CartOpreateListener;
import com.gae.listener.FoodOpreateListener;
import com.gae.listener.ListItemClickListener;
import com.gae.listener.ListListener;
import com.gae.listener.StoreOpreateListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CartAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<CarInfo> mList;//购物车数据源
	private Map<Integer, Boolean> mMap;
	private CartOpreateListener foodListener = null;
	private Context mcontext;
	private ListItemClickListener listener = null;
    public CartAdapter(Context context,List<CarInfo> mdata,Map<Integer, Boolean> map,ListItemClickListener listen){
    	mInflater = LayoutInflater.from(context);
    	mList = mdata;
    	this.mMap = map;
    	mcontext=context;
    	listener = listen;
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

    @Override
    public View getView( final int position, View convertView, ViewGroup parent) {
    	 final int pos = position;
         ViewHolder holder = null;
        if (convertView == null) {
            holder=new ViewHolder();  
            convertView = mInflater.inflate(R.layout.cart_listitem, null);
            holder.foodname = (TextView)convertView.findViewById(R.id.cartfoodname);
            holder.foodnumber = (TextView)convertView.findViewById(R.id.cartfoodnumber);
            holder.foodprice = (TextView)convertView.findViewById(R.id.cartfoodprice);
          
            convertView.setTag(holder);
           
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.foodprice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(listener!=null)
					listener.OnListItemClick("price",position,v);   
//				Toast.makeText(mcontext,String.valueOf(position), 1000).show();
			}
		});
        holder.foodnumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(listener!=null)
					listener.OnListItemClick("number",position,v);   
			}
		});
        if (mMap.get(position)) {
        	convertView.setBackgroundResource(R.drawable.fooditemon);

		} else {
			convertView.setBackgroundResource(R.drawable.fooditmeoff);

		}
        //减少份数
    /**    holder.fooddele.setOnClickListener(new OnClickListener() {
        	
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
        });**/

        holder.foodname.setText((String)mList.get(position).getPname());
     //   int sum = EatParams.getInstance().list.get(pos).getSnum();
        holder.foodnumber.setText(mList.get(position).getSnum()+"份");
        int num = mList.get(position).getSnum();
        holder.foodprice.setText((Double.parseDouble(mList.get(position).getPrice())*num)+"元");
        
        return convertView;
    }
 
    public final class ViewHolder{
        public TextView foodname;
        public TextView foodprice;
        public TextView foodnumber;
    }

    private ListListener lisener = null;
    public void setCarInfoList(ListListener lisen){
    	lisener = lisen;
    }
    
    public void setCartOpreateListener(CartOpreateListener fl){
    	foodListener = fl;
    }
}


