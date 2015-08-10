package com.gae.view;

import com.gae.adapter.TextAdapter;
import com.gae.eat2013.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class EatPlanViewWeek extends RelativeLayout implements ViewBaseAction{

	private ListView mListView;
	private final String[] items = new String[] {"周日","周一", "周二","周三","周四","周五","周六"};
	private final String[] itemsVaule = new String[] { "1", "2","3","4","5","6","7"};
	private OnSelectListener mOnSelectListener;
	private TextAdapter adapter;
	private String mDistance;
	private String showText = "周日";
	private Context mContext;

	public String getShowText() {
		return showText;
	}

	public EatPlanViewWeek(Context context) {
		super(context);
		init(context);
	}

	public EatPlanViewWeek(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public EatPlanViewWeek(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_distance, this, true);
		setBackgroundDrawable(getResources().getDrawable(R.drawable.choosearea_bg_mid));
		mListView = (ListView) findViewById(R.id.listView);
		adapter = new TextAdapter(context, items, R.drawable.choose_item_right, R.drawable.choose_eara_item_selector);
		adapter.setTextSize(17);
		if (mDistance != null) {
			for (int i = 0; i < itemsVaule.length; i++) {
				if (itemsVaule[i].equals(mDistance)) {
					adapter.setSelectedPositionNoNotify(i);
					showText = items[i];
					break;
				}
			}
		}
		mListView.setAdapter(adapter);
		adapter.setOnItemClickListener(new TextAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {

				if (mOnSelectListener != null) {
					showText = items[position];
					mOnSelectListener.getValue(itemsVaule[position], items[position]);
					mOnSelectListener.getdistance(items[position]);
				}
			}
		});
	}

	public void setOnSelectListener(OnSelectListener onSelectListener) {
		mOnSelectListener = onSelectListener;
	}

	public interface OnSelectListener {
		public void getValue(String distance, String showText);
		public void getdistance(String distance);
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void show() {
		
	}

}
