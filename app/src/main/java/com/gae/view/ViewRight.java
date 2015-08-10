package com.gae.view;

import java.util.ArrayList;

import com.gae.adapter.TextAdapter;
import com.gae.eat2013.R;
import com.gae.entity.EatParams;
import com.gae.entity.FoodTypeItem;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class ViewRight extends RelativeLayout implements ViewBaseAction{

    private ListView mListView;
    private final String[] items = new String[] {"距离", "评论", "价格" };
    private ArrayList<FoodTypeItem> listdata = EatParams.getInstance().fstyplist;
    private ArrayList<String> groups = new ArrayList<String>();
    private ArrayList<String> groupsId = new ArrayList<String>();
    private final String[] itemsVaule = new String[] { "1", "2", "3"};//id
    private OnSelectListener mOnSelectListener;
    private TextAdapter adapter;
    private String mDistance;
    private String showText = "类型";
    private Context mContext;

    public String getShowText() {
        return showText;
    }

    public ViewRight(Context context) {
        super(context);
        init(context);
    }

    public ViewRight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ViewRight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_distance, this, true);
        setBackgroundDrawable(getResources().getDrawable(R.drawable.choosearea_bg_right));
        mListView = (ListView) findViewById(R.id.listView);

        for(int i=0;i<listdata.size();i++){
            groups.add(listdata.get(i).getCNAME().toString());//获取一级分类的名称
        }
        adapter = new TextAdapter(context, groups, R.drawable.choose_item_right, R.drawable.choose_eara_item_selector);
        adapter.setTextSize(17);
        if (mDistance != null) {
            for (int i = 0; i < listdata.size(); i++) {
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
                    showText = listdata.get(position).getCNAME();
                    mOnSelectListener.getValue(listdata.get(position).getID(), listdata.get(position).getCNAME());
                    mOnSelectListener.getOrd(listdata.get(position).getCNAME());

                }
            }
        });
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
    }

    public interface OnSelectListener {
        public void getValue(String distance, String showText);
        public void getOrd(String ordname);
    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

}
