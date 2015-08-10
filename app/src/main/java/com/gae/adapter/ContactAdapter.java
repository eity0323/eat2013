package com.gae.adapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.gae.eat2013.R;
import com.gae.entity.Info;
import com.gae.view.PingYinUtil;
import com.gae.view.PinyinComparator;

public class ContactAdapter extends BaseAdapter implements SectionIndexer{
	private Context mContext;
	public static String[] mNicks;
	public static Map<Integer, String> p2s = new HashMap<Integer, String>();
	private Map<String, Info> m = new HashMap<String, Info>();
	

	@SuppressWarnings("unchecked")
	public ContactAdapter(Context mContext, Map<String, Info> s)
	{
		this.mContext = mContext;
		this.m = s;
		if (m != null)
		{
			Set<String> set = m.keySet();
			mNicks = new String[set.size()];
			set.toArray(mNicks);
		}
		// 排序(实现了中英文混排)
		Arrays.sort(mNicks, new PinyinComparator());
	}

	@Override
	public int getCount() {
		return mNicks.length;
	}

	@Override
	public Object getItem(int position) {
		return mNicks[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int index = position;
		final String nickName = mNicks[index];
		p2s.put(position, nickName);
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvCatalog = (TextView) convertView.findViewById(R.id.contactitem_catalog);
			viewHolder.ivAvatar = (ImageView) convertView.findViewById(R.id.contactitem_avatar_iv);
			viewHolder.tvNick = (TextView) convertView.findViewById(R.id.contactitem_nick);
			viewHolder.numbers = (TextView) convertView.findViewById(R.id.numbers);
			viewHolder.checkbox = (Button) convertView.findViewById(R.id.contactitem_select_cb);
			viewHolder.checkbox_fl = (FrameLayout) convertView.findViewById(R.id.contactitem_select_cb_fl);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String catalog = null;
		String lastCatalog = null;
		catalog = PingYinUtil.converterToFirstSpell(nickName).substring(0, 1);
		if (position == 0)
		{
			viewHolder.tvCatalog.setVisibility(View.VISIBLE);
			viewHolder.tvCatalog.setText(catalog);
		} else
		{
			lastCatalog = PingYinUtil.converterToFirstSpell(mNicks[index - 1]).substring(0, 1);
			if (catalog.equals(lastCatalog))
			{
				viewHolder.tvCatalog.setVisibility(View.GONE);
			} else
			{
				viewHolder.tvCatalog.setVisibility(View.VISIBLE);
				viewHolder.tvCatalog.setText(catalog);
			}
		}
		viewHolder.checkbox.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				String nk = (String) v.getTag();
				if (m.get(nk).isIs_Checked())
				{
					m.get(nk).setIs_Checked(false);
				} else
				{
					m.get(nk).setIs_Checked(true);
				}

			}
		});
		viewHolder.checkbox_fl.setOnClickListener(new OnClickListener()

		{

			@Override
			public void onClick(View v) {
				v.findViewById(R.id.contactitem_select_cb).performClick();
			}
		});
		viewHolder.ivAvatar.setImageResource(R.drawable.default_avatar);
		viewHolder.tvNick.setText(nickName);
		viewHolder.numbers.setText(m.get(nickName).getNumber());
		return convertView;
	}

	static class ViewHolder {
		TextView tvCatalog;
		ImageView ivAvatar;
		TextView tvNick;
		TextView numbers;			
		Button checkbox;			
		FrameLayout checkbox_fl;	
		}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < mNicks.length; i++)
		{
			String l = PingYinUtil.converterToFirstSpell(mNicks[i]).substring(0, 1);
			char firstChar = l.toUpperCase().charAt(0);
			if (firstChar == section)
			{
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}