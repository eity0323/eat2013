package com.gae.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gae.eat2013.R;
import com.gae.entity.applyItem;

import java.util.ArrayList;
import java.util.List;

public class MainGridViewAdapter extends BaseAdapter{ 
    //上下文对象 
    private Context mcontext; 
	/**
	 * 数据源
	 */
	private List<applyItem> flist;

	public static final int APP_PAGE_SIZE = 9;
	private PackageManager pm;

	public MainGridViewAdapter(Context context, List<applyItem> list,int page) {
		this.mcontext = context;
        pm = context.getPackageManager();
//		flist=list;
        flist = new ArrayList<applyItem>();
		int i = page * APP_PAGE_SIZE;
		int iEnd = i+APP_PAGE_SIZE;
		while ((i<list.size()) && (i<iEnd)) {
			flist.add(list.get(i));
			i++;
		}
		
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
					R.layout.applygrid_item, null);
			aitme = new ApplyItem();
            aitme.ico=(ImageView)v.findViewById(R.id.gridImg);
			v.setTag(aitme);
			convertView = v;
		} else {
			aitme = (ApplyItem) convertView.getTag();
		}
		 Class<com.gae.eat2013.R.drawable> cls = R.drawable.class;  
	        try {  
	            int value = cls.getDeclaredField(flist.get(position).getGridico()).getInt(null);  
	            aitme.ico.setBackgroundResource(value);  
	                        //Log.v("value",value.toString());  
	        } catch (Exception e) {  
	            // TODO Auto-generated catch block  
	        	 aitme.ico.setBackgroundResource(R.drawable.activityfinance);  
	            e.printStackTrace();  
	        }   		
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
		
	}
} 
