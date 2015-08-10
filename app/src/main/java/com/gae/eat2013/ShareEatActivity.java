package com.gae.eat2013;

/**
 * author:小胡
 * version:2013-5-24
 * description:邀请好友
 * */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gae.basic.ProgressRing;
import com.gae.entity.EatParams;
import com.gae.entity.Info;
import com.gae.view.PingYinUtil;
import com.gae.view.PinyinComparator;
import com.gae.view.SideBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout.LayoutParams;

public class ShareEatActivity extends Activity implements OnScrollListener {
	private ListView lvContact = null;
	private Button contactNext = null;
	private Button contactBack = null;
	private SideBar indexBar = null;
	protected Map<String, Info> listcontact = null;
	public static Map<Integer, String> p2s = null;
	public List<Info> list = null;
	public static String[] mNicks = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shareeat_activity);

		listcontact = new HashMap<String, Info>();
		p2s = new HashMap<Integer, String>();
		list = new ArrayList<Info>();

		lvContact = (ListView) findViewById(R.id.lvContact);
		indexBar = (SideBar) findViewById(R.id.sideBar);

		//获取好友列表
		listcontact = getContacts();
		//初始化布局
		findView();
		
		//下一步
		contactNext = (Button) findViewById(R.id.contactnext);
		contactNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				list.clear();
				EatParams.getInstance().contactlist.clear();
				for (int i = 0; p2s.size() > i; i++) {
					if (listcontact.get(p2s.get(i)).isIs_Checked()) {
						list.add(listcontact.get(p2s.get(i)));
					}
				}
				EatParams.getInstance().contactlist = list;

				Intent intent = new Intent(ShareEatActivity.this,
						InviteNextActivity.class);
				startActivity(intent);
				finish();

			}
		});
		
		//返回
		contactBack = (Button) findViewById(R.id.contactback);
		contactBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareEatActivity.this.finish();
			}
		});
	}

	//初始化布局
	private void findView() {
		lvContact.setOnScrollListener(this);
		lvContact.setAdapter(new ContactAdapter(this, listcontact));
		indexBar.setListView(lvContact);
		ProgressRing.unProgeress();
	}

	//获取通讯录信息
	private Map<String, Info> getContacts() {
		// 存放多个电话号码
		String phoneNumbers = "";
		Map<String, Info> map = new HashMap<String, Info>();
		try {
			Cursor cursor = getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI, null, null, null,
					null);
			while (cursor.moveToNext()) {
				String phoneName = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String contactId = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				String hasPhone = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				if (hasPhone.compareTo("1") == 0) {
					Cursor phones = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
					while (phones.moveToNext()) {
						String phoneNumber = phones
								.getString(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						/* 判断是否有多个号码 */
						if (phoneNumber.startsWith("+86")) {
							phoneNumber.substring(3);
						} else if (phoneNumber.startsWith("12593")) {
							phoneNumber.substring(5);
						}
						if (phones.getCount() > 1) {
							/* 如果有多个号码以空格 隔开 方便后面取值 */
							phoneNumbers = phoneNumbers + ";" + phoneNumber;
						} else {
							phoneNumbers = phoneNumber;
						}

					}
					phones.close();
				}
				if (phoneNumbers.startsWith("1")) {
					map.put(phoneName, new Info(false, phoneNumbers, phoneName));
				}
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	// 美食分享适配器
	static class ContactAdapter extends BaseAdapter implements SectionIndexer {
		private Context mContext;

		private Map<String, Info> m = new HashMap<String, Info>();

		@SuppressWarnings("unchecked")
		public ContactAdapter(Context mContext, Map<String, Info> s) {
			this.mContext = mContext;
			this.m = s;
			if (m != null) {
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
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.contact_item, null);
				viewHolder = new ViewHolder();
				viewHolder.tvCatalog = (TextView) convertView
						.findViewById(R.id.contactitem_catalog);
				viewHolder.ivAvatar = (ImageView) convertView
						.findViewById(R.id.contactitem_avatar_iv);
				viewHolder.tvNick = (TextView) convertView
						.findViewById(R.id.contactitem_nick);
				viewHolder.numbers = (TextView) convertView
						.findViewById(R.id.numbers);
				viewHolder.checkbox = (CheckBox) convertView
						.findViewById(R.id.contactitem_select_cb);
				viewHolder.checkbox_fl = (FrameLayout) convertView
						.findViewById(R.id.contactitem_select_cb_fl);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			String catalog = null;
			String lastCatalog = null;
			catalog = PingYinUtil.converterToFirstSpell(nickName).substring(0,
					1);
			if (position == 0) {
				viewHolder.tvCatalog.setVisibility(View.VISIBLE);
				viewHolder.tvCatalog.setText(catalog);
			} else {
				lastCatalog = PingYinUtil.converterToFirstSpell(
						mNicks[index - 1]).substring(0, 1);
				if (catalog.equals(lastCatalog)) {
					viewHolder.tvCatalog.setVisibility(View.GONE);
				} else {
					viewHolder.tvCatalog.setVisibility(View.VISIBLE);
					viewHolder.tvCatalog.setText(catalog);
				}
			}
			viewHolder.checkbox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String nk = (String) v.getTag();
					if (m.get(nk).isIs_Checked()) {
						m.get(nk).setIs_Checked(false);
					} else {
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
			viewHolder.checkbox.setChecked(m.get(nickName).isIs_Checked());
			viewHolder.ivAvatar.setImageResource(R.drawable.default_avatar);
			viewHolder.tvNick.setText(nickName);
			viewHolder.numbers.setText(m.get(nickName).getNumber());
			viewHolder.checkbox.setTag(nickName);
			return convertView;
		}

		static class ViewHolder {
			TextView tvCatalog;
			ImageView ivAvatar;
			TextView tvNick;
			TextView numbers;
			CheckBox checkbox;
			FrameLayout checkbox_fl;
		}

		@Override
		public int getPositionForSection(int section) {
			for (int i = 0; i < mNicks.length; i++) {
				String l = PingYinUtil.converterToFirstSpell(mNicks[i])
						.substring(0, 1);
				char firstChar = l.toUpperCase().charAt(0);
				if (firstChar == section) {
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

	@Override
	public void onResume() {
		super.onResume();
	}
}
