package com.gae.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gae.eat2013.R;
import com.gae.entity.CarInfo;
import com.gae.entity.EatParams;
import com.gae.entity.FoodItem;
import com.gae.listener.FoodOpreateListener;
import com.gae.listener.ListListener;
import com.gae.view.Alert;

import java.util.ArrayList;
import java.util.List;

public class CollectFoodAdapter extends BaseAdapter {

	private List<FoodItem> mList;			//数据源
	private LayoutInflater mInflater;
	private Alert pwin;						//弹出窗
	private View layout;					//弹出窗内容
	private String radioStr = "A";
	private Activity a;
	
	private String sid = "";
	private String url_class;               //登陆url
	private String mail;
	private String addr;
	private String uid;
	private String mb;
	private String result;
	private String urlServer = EatParams.getInstance().getUrlServer();
	
	private ListListener lisener = null;
	private FoodOpreateListener foodListener = null;
	private ArrayList<CarInfo> temprList = new ArrayList<CarInfo>();
	
    public CollectFoodAdapter(Activity context,List<FoodItem> mdata){
    	a = context;
    	mInflater = LayoutInflater.from(a);
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

    ViewHolder holder = null;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
    
        if (convertView == null) {
             
            holder=new ViewHolder();  
             
            convertView = mInflater.inflate(R.layout.collectfoodlitem, null);
            holder.field1 = (TextView)convertView.findViewById(R.id.pname);
            holder.field2 = (TextView)convertView.findViewById(R.id.vname);
            holder.field3 = (TextView)convertView.findViewById(R.id.price);
            holder.field5 = (Button)convertView.findViewById(R.id.btn);
            convertView.setTag(holder);
             
        }else {
             
            holder = (ViewHolder)convertView.getTag();
        }

        holder.field1.setText((String)mList.get(position).getPNAME());
        holder.field2.setText((String)mList.get(position).getVNAME());
        holder.field3.setText((String)mList.get(position).getPRICE());
        final FoodItem tempr = mList.get(position);
        holder.field5.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//派发事件
				if(foodListener!=null)
					foodListener.OnListItemClick(position,v);
			}
		});
        
        return convertView;
    }
    
    public void setFoodListener(FoodOpreateListener fl){
    	foodListener = fl;
    }
    public final class ViewHolder{
        public TextView field1;
        public TextView field2;
        public TextView field3;
        public Button field5;
    }

}


