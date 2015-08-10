package com.gae.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gae.eat2013.R;
import com.gae.entity.applyItem;

import java.util.List;

public class SliListViewAdapter extends BaseAdapter{ 
    //上下文对象 
    private Context mcontext; 
	/**
	 * 数据源
	 */
	private List<applyItem> flist;

	/**
	 * 监听器
	 */
//	private ListItemClickListener listener = null;
	/**
	 * list项点击类型
	 */
//	private String CLICKTYPE = "del";

	public SliListViewAdapter(Context context, List<applyItem> list) {
		this.mcontext = context;
		flist = list;		
	}

	public List<applyItem> getDataProvide() {
		return flist;
	}

	@Override
	public int getCount() {
		return flist.size();
	}

	@Override
	public Object getItem(int position) {
		return flist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ApplyItem aitme;	
		if (convertView == null) {
			View v = LayoutInflater.from(mcontext).inflate(
					R.layout.applylist_item, null);
			aitme = new ApplyItem();
            aitme.ico=(ImageView)v.findViewById(R.id.applyico);
            aitme.show=(ImageView)v.findViewById(R.id.slishow);
            aitme.name=(TextView)v.findViewById(R.id.applyname);
			
			v.setTag(aitme);
			convertView = v;
		} else {
			aitme = (ApplyItem) convertView.getTag();
		}
		 Class<com.gae.eat2013.R.drawable> cls = R.drawable.class;  
	        try {  
	            int value = cls.getDeclaredField(flist.get(position).getSliico()).getInt(null);  
	            aitme.ico.setBackgroundResource(value);  
	                        //Log.v("value",value.toString());  
	        } catch (Exception e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }   
	        String shoeee=flist.get(position).getShow();
	        if(flist.get(position).getShow().equals("Y")){
	        	aitme.show.setVisibility(View.VISIBLE);
	        }else{
	        	aitme.show.setVisibility(View.GONE);
	        }
		aitme.name.setText(flist.get(position).getName());
       
		return convertView;
	}

	/**
	 * 每个列表显示的数据
	 * 
	 * @author zhaoxinbo
	 * 
	 */
	class ApplyItem {
		ImageView ico; // 图片
		ImageView show; // 图片
		TextView name; // 名称
		
		
	}
} 
