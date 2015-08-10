package com.gae.adapter;

import java.util.List;

import com.gae.control.UpdateHitsHot;
import com.gae.eat2013.R;
import com.gae.entity.CoupShareItem;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CoupShareAdapter extends BaseAdapter  {
    private LayoutInflater inflater;
	private List<CoupShareItem> mlist ;
	private Activity activity;
	private UpdateHitsHot updateHitsHot;
	private int num;
	private int badnum;
	public CoupShareAdapter(Activity context,List<CoupShareItem> data){
       inflater = LayoutInflater.from(context);
       activity = context;
       mlist = data;
    }
	public CoupShareAdapter(Activity context) {
       activity = context;
	}

	@Override
	public int getCount() {
		return mlist.size();
	}

	@Override
	public Object getItem(int position) {
		return mlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
    ViewHolder holder;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView ==null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.coupshare_listitem, null);
			holder.title = (TextView)convertView.findViewById(R.id.coupname);//标题
			holder.content = (TextView)convertView.findViewById(R.id.othersongcan);//内容
			holder.dingnum = (TextView)convertView.findViewById(R.id.dingperson);
			holder.cainum = (TextView)convertView.findViewById(R.id.caiperson);	 

			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.dingnum.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateHitsHot = new UpdateHitsHot(activity);
				updateHitsHot.updateNum(mlist.get(position).getId(),"G");
			    num=Integer.valueOf(mlist.get(position).getGOOD())+1;
			    mlist.get(position).setGOOD(String.valueOf(num));
				notifyDataSetChanged();
			}
		});
		holder.cainum.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateHitsHot = new UpdateHitsHot(activity);
				updateHitsHot.updateNum(mlist.get(position).getId(),"D");
				badnum = Integer.valueOf(mlist.get(position).getBAD())+1;
				mlist.get(position).setBAD(String.valueOf(badnum));
				notifyDataSetChanged();
			}
		});
		holder.title.setText((String)mlist.get(position).getTITLE());
		String constr = (String)mlist.get(position).getCONTEXT();
		if(constr.length() > 80){
			constr = constr.substring(0, 80) + "...";
		}
		holder.content.setText(constr);
		holder.dingnum.setText("顶："+(String)mlist.get(position).getGOOD());
		holder.cainum.setText("踩："+(String)mlist.get(position).getBAD());			
		return convertView;
	}
	public final class ViewHolder{
		public TextView title;
		public TextView content;
		public TextView dingnum;
		public TextView cainum;
		public TextView dingtv;
		public TextView caitv;
	}

}
