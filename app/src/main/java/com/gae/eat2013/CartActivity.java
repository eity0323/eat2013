package com.gae.eat2013;
/*
 * author:小胡
 * version:2013-5-24
 * description:购物车处理
 * */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gae.UInterface.ICartAction;
import com.gae.adapter.CartAdapter;
import com.gae.control.AddCard;
import com.gae.entity.CarInfo;
import com.gae.entity.EatParams;
import com.gae.listener.ListChangedListener;
import com.gae.listener.ListItemClickListener;
import com.gae.presenter.CartPresenter;

import java.util.HashMap;
import java.util.Map;

public class CartActivity extends Activity implements ICartAction{
	private Button continueorder = null;                                         //返回点餐
	private Map<Integer, Boolean> selectedMap = null; 							 //是否选中
	private ListView orderfoodList = null;                                       //菜品列表
	private TextView totalnumtview = null;                                       //数据
	private TextView phnumber = null;                                            //餐馆电话
	private ImageView rubbishbtn = null;                                         //删除
	private TextView nextstep = null;                                            //下一步
	private AddCard add = null;													 //购物车
	private CartAdapter cartAdapter = null;                                      //适配器
	private String from = "";                                                    //判断来源

    private CartPresenter helper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cart_activity);

        helper = new CartPresenter(this);
		
		//获取选中订单
		selectedMap = new HashMap<Integer, Boolean>();

        initView();
	}

    private void  initView(){
        Intent intent=getIntent();
        from=intent.getStringExtra("from");
        continueorder = (Button)findViewById(R.id.continueorder);
        orderfoodList = (ListView)findViewById(R.id.orderfoodList);
        totalnumtview = (TextView)findViewById(R.id.totalnumtview);
        rubbishbtn = (ImageView)findViewById(R.id.rubbishbtn);
        nextstep = (TextView)findViewById(R.id.nextstep);
        phnumber = (TextView)findViewById(R.id.phnumber);
        phnumber.setText("联系餐馆");

        add = new AddCard(CartActivity.this);
        add.getCardItemUrl();
        for (int i = 0; i < EatParams.getInstance().list.size(); i++) {
            selectedMap.put(i, false);
        }

        add.setListChangedlistener(new ListChangedListener() {  //购物车监听

            @Override
            public void OnListItemClick(String str) {
                String totalNum =add.totalNum();
                totalnumtview.setText(totalNum+"份");
                for (int i = 0; i < EatParams.getInstance().list.size(); i++) {
                    selectedMap.put(i, false);
                }
                cartAdapter = new CartAdapter(CartActivity.this, EatParams.getInstance().list,selectedMap,listlisten);
                orderfoodList.setAdapter(cartAdapter);
                cartAdapter.notifyDataSetChanged();

            }
        });
        String totalNum =add.totalNum();
        totalnumtview.setText(totalNum+"份");
        continueorder.setOnClickListener(listener);
        rubbishbtn.setOnClickListener(listener);
        nextstep.setOnClickListener(listener);
        phnumber.setOnClickListener(listener);

        cartAdapter = new CartAdapter(CartActivity.this, EatParams.getInstance().list,selectedMap,listlisten);
        orderfoodList.setAdapter(cartAdapter);
    }

    private ListItemClickListener listlisten = new ListItemClickListener() {

        @Override
        public void OnListItemClick(String type,final int position, View v) {
            if(type.equals("price")){                       //选择
                if (selectedMap.get(position)) {
                    selectedMap.put(position, false);
                    EatParams.getInstance().list.get(position).setState("N");

                } else {
                    selectedMap.put(position, true);
                    EatParams.getInstance().list.get(position).setState("Y");

                }
                cartAdapter.changeSelectMap(selectedMap);
                cartAdapter.notifyDataSetInvalidated();
                cartAdapter.notifyDataSetChanged();
            }else if(type.equals("number")){                       //修改份数

                changeCartNumber(EatParams.getInstance().list.get(position));
            }
        }
    };

    //修改份数
    private void changeCartNumber(final CarInfo data){
        AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
        builder.setTitle("您想来几份呢：");
        final EditText edit=new EditText(CartActivity.this);
        DigitsKeyListener numericOnlyListener = new DigitsKeyListener(false, true);
        edit.setKeyListener(numericOnlyListener);
        builder.setView(edit);
        builder.setNeutralButton("确认",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(edit.getText().toString().equals("")){
                    Toast.makeText(CartActivity.this, "亲，您还没输入份数哦！", Toast.LENGTH_SHORT).show();
                    return;
                }else if(Integer.valueOf(edit.getText().toString())==0){
                    add.delcart(data.getId());
                }
                else{
                    add.modifyCarUrl(data.getId(),
                            EatParams.getInstance().getUid(),Integer.valueOf(edit.getText().toString()));
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog dialog=builder.create();
        dialog.show();
    }

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.continueorder:                                //返回
					finish();				
					break;
				case R.id.rubbishbtn:                                    //删除
					boolean delable = false;
					for (int i = 0; i < EatParams.getInstance().list.size(); i++){
						if(EatParams.getInstance().list.get(i).getState().equals("Y")){
							add.delcart(EatParams.getInstance().list.get(i).getId());		
							delable = true;
						}
						cartAdapter.notifyDataSetChanged();					
					}	
					String delmsg = "";
					if(delable){
						delmsg = "真的删除了哟";
					}else{
						delmsg = "还没有选择要删除的菜品哟";
					}
					Toast.makeText(CartActivity.this, delmsg, Toast.LENGTH_SHORT).show();
					break;
				case R.id.nextstep:                                       //提交
					Intent in = new Intent(CartActivity.this,PayOffActivity.class);
					in.putExtra("from", from);
					startActivity(in);
					break;
				case R.id.phnumber:                                        //打电话
					  String telno = EatParams.getInstance().getShopmb();
					  if(telno.length() > 0){
						  String tno = telno.split(",")[0];
						  if(tno.length() > 0){
							  Intent ient = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+ tno));//parse("tel:"+phnumber.getText().toString()));  
				              startActivity(ient);  
						  }else{
							  Toast.makeText(getApplicationContext(), "这个餐馆还没提供联系电话呢", Toast.LENGTH_LONG).show();
						  }
					  }else{
						  Toast.makeText(getApplicationContext(), "这个餐馆还没提供联系电话呢", Toast.LENGTH_LONG).show();
					  }
					  
					break;
				default:
					break;
				}

		}
	};
}