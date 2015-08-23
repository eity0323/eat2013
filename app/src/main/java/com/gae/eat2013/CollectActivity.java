package com.gae.eat2013;
/*
 * author:小胡
 * version:2013-5-24
 * description:常点收藏
 * */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gae.UInterface.ICollectAction;
import com.gae.adapter.FoodAdapter;
import com.gae.control.AddCard;
import com.gae.entity.EatParams;
import com.gae.entity.FoodItem;
import com.gae.entity.ShopItem;
import com.gae.listener.ListChangedListener;
import com.gae.presenter.CollectPresenter;
import com.gae.view.CollectViewLeft;
import com.gae.view.ExpandTabView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CollectActivity extends Activity implements ICollectAction{
	private String areaDbname="";										//城市数据库名称
	private String url_class = "";										//请求数据路径
	private String urlServer = "";										//服务器路径
	private ArrayList<ShopItem> mlist = null;							//餐馆数据源
    private View mainChild = null;                                      //主界面
    private View collectlistview= null;                                 //收藏餐馆
    private View collectseftview= null;                                 //自定义收藏
    private View collectfoodview= null;                                 //菜品收藏
    private LinearLayout llcontactview= null;                           //容器
    private String result = "";                                         //返回结果
    private String errorStr = "";										//请求数据返回错误信息
    private ListView collectlist= null;                                 //餐馆listview
    private TextView selfcollect= null;                                 //自定义名称
    private  ListView foodlistview= null;                                //菜品收藏
	private FoodAdapter foodadapter= null;                              //菜品适配器
	private TextView totalnum= null;                                    //点餐数量
	private ImageView carfor= null;                                     //转到购物车
    private ExpandTabView expandTabView= null;                          //分类容器
    private CollectViewLeft viewLeft= null;                             //菜品、餐馆
    private AddCard add = null;      									//购物车类
    private ArrayList<View> mViewArray = null;   						//分类容器
    private ArrayList<FoodItem> foodlist = null; 						//菜品
    private Map<Integer, Boolean> selectedMap = null;					//是否选中

    private CollectPresenter helper;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collect_activity);

        helper = new CollectPresenter(this);
		
		areaDbname=EatParams.getInstance().areaDbName;
		urlServer = EatParams.getInstance().getUrlServer();

		mlist = new ArrayList<ShopItem>();
		add = new AddCard(CollectActivity.this);
		mViewArray = new ArrayList<View>();
		foodlist = new ArrayList<FoodItem>();
		selectedMap = new HashMap<Integer, Boolean>();

        initView();
	}

    private void initView(){
        llcontactview=(LinearLayout)findViewById(R.id.collectcontact);
        collectlistview = getLayoutInflater().inflate(R.layout.collectlistview,null);
        collectseftview = getLayoutInflater().inflate(R.layout.collectseft,null);
        collectfoodview = getLayoutInflater().inflate(R.layout.collectfood,null);
        foodlistview = (ListView)collectfoodview.findViewById(R.id.foodList);
        totalnum = (TextView)collectfoodview.findViewById(R.id.totalnum);
        carfor = (ImageView)collectfoodview.findViewById(R.id.carfor);
        llcontactview.addView(collectlistview);

        collectlist = (ListView)collectlistview.findViewById(R.id.collectList);
        expandTabView = (ExpandTabView)findViewById(R.id.expandtab_view);
        viewLeft = new CollectViewLeft(CollectActivity.this);

        selfcollect = (TextView)findViewById(R.id.selfcollect);

        //返回
        Button collectback = (Button)findViewById(R.id.collectback);
        collectback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //购物车类
        add = new AddCard(CollectActivity.this);
        add.getCardItemUrl();                                 //查询购物车
        add.setListChangedlistener(new ListChangedListener() {

            @Override
            public void OnListItemClick(String str) {
                totalnum.setText(add.totalNum());
            }
        });

        //自定义收藏
        selfcollect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                llcontactview.removeAllViews();
                llcontactview.addView(collectseftview);
            }
        });

        //收藏菜品Item处理
        foodlistview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                if (selectedMap.get(position)) {
                    selectedMap.put(position, false);
                    for(int i=0;EatParams.getInstance().list.size()>i;i++){
                        if(foodlist.get(position).PID.equals(EatParams.getInstance().list.get(i).getPid())){
                            add.deleCarUrl(EatParams.getInstance().list.get(i).getId());
                        }
                    }

                } else {
                    selectedMap.put(position, true);
                    for(int i=0;EatParams.getInstance().list.size()>i;i++){//不同餐馆的删除
                        if(!foodlist.get(position).VID.equals(EatParams.getInstance().list.get(i).getVid())){
                            add.delcart(EatParams.getInstance().list.get(i).getId());
                            for(int j=0;foodlist.size()>j;j++){
                                if(foodlist.get(j).getPID().equals(EatParams.getInstance().list.get(i).getPid())){
                                    selectedMap.put(j, false);
                                }
                            }
                        }
                    }
                    add.addCarUrl(foodlist.get(position).VID,
                            foodlist.get(position).VNAME,
                            foodlist.get(position).PID,
                            foodlist.get(position).PNAME,
                            foodlist.get(position).DISC,
                            foodlist.get(position).PRICE, "1");
                    Toast.makeText(CollectActivity.this,"添加成功", Toast.LENGTH_SHORT).show();
                    getShopInfo(foodlist.get(position).VID);
                }
                foodadapter.changeSelectMap(selectedMap);
                foodadapter.notifyDataSetInvalidated();
            }
        });

        //跳转到餐馆主页
        collectlist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Intent intent = new Intent(CollectActivity.this,StoreActivity.class);
                intent.putExtra("storename", mlist.get(position).getTITLE());
                intent.putExtra("storecaixi", mlist.get(position).getMCLSN());
                intent.putExtra("storetel", mlist.get(position).getTEL());
                intent.putExtra("storeaddr", mlist.get(position).getADDR());
                intent.putExtra("vid", mlist.get(position).getVID());
                startActivity(intent);
            }
        });

        //购物车
        carfor.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in=new Intent(CollectActivity.this,CartActivity.class);
                startActivity(in);
                CollectActivity.this.finish();
            }
        });
        shopListItem();
        //初始化下拉选择组件
        initVaule();
        //初始化下拉选择组件监听事件
        initListener();
    }
	
	//初始化下拉选择组件
	private void initVaule() {
		mViewArray.add(viewLeft);
		ArrayList<String> mTextArray = new ArrayList<String>();
		
		mTextArray.add("类型");
		mTextArray.add("距离");
		expandTabView.setValue(mTextArray, mViewArray);
		expandTabView.setTitle(viewLeft.getShowText(), 0);
	}
	
	//初始化下拉选择组件监听事件
	private void initListener() {		
		viewLeft.setOnSelectListener(new CollectViewLeft.OnSelectListener() {
			@Override
			public void getValue(String distance, String showText) {
				onRefresh(viewLeft, showText);
			}

			@Override
			public void getdistance(String distance) {
                getNearFood(distance);
            }
			
		});
	}

    private void getNearFood(String distance){
        String urlServer = EatParams.getInstance().getUrlServer();
        if(distance.equals("常点菜品")){
            llcontactview.removeAllViews();
            foodlist.clear();
            llcontactview.addView(collectfoodview);
           //TODO get food near
        }else{
            llcontactview.removeAllViews();
            llcontactview.addView(collectlistview);
            mlist.clear();
            shopListItem();
        }
    }
	
	//刷新下拉选择框显示
	private void onRefresh(View view, String showText) {
		expandTabView.onPressBack();
		int position = getPositon(view);
		if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
			expandTabView.setTitle(showText, position);
		}
		Toast.makeText(CollectActivity.this, showText, Toast.LENGTH_SHORT).show();

	}
	
	//获取下拉选择框选中索引
	private int getPositon(View tView) {
		for (int i = 0; i < mViewArray.size(); i++) {
			if (mViewArray.get(i) == tView) {
				return i;
			}
		}
		return -1;
	}
	
	//获取餐馆数据
	public void getShopInfo(String vid){

	}
	
	//获取餐馆菜品
	public void shopListItem(){

	}

}
