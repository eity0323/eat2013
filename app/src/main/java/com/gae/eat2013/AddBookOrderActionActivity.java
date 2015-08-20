package com.gae.eat2013;
/**
 * author:小容
 * version:2013-5-24
 * description:新增、修改个人预订订单
 * */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gae.UInterface.IAddBookOrderAction;
import com.gae.entity.BookInfo;
import com.gae.entity.EatParams;
import com.gae.presenter.AddBookOrderPresenter;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//新增、修改个人订单
public class AddBookOrderActionActivity extends Activity implements IAddBookOrderAction {
	private String areaDbname = "";					//城市数据库名称
	private String urlServer = "";					//服务器路径
	private String url_class = "";					//请求数据路径
	private static Dialog dialog = null;			//弹出对话框
	private String result = "";						//请求数据返回标识
	private String errorStr = "";					//请求数据返回错误信息
	private String sid = "";						//事务id
	private EditText editmb = null;					//手机号
	private EditText editprice = null;				//价格
	private EditText editinfo = null;				//描述
	private EditText editaddr = null;				//地址
	private EditText times = null;					//时间
	private TextView bookOrder = null;				//文本说明
	private ArrayList<BookInfo> booklist = null;	//个人预定下单数据源

    private String id;
	
	private SimpleDateFormat formatter = null;        
	private Date curDate  = null;					//获取当前时间

    // 创建日期弹出窗口
    private String date_time;
    private String date;
    private String time;
    private DatePicker xdpdate;
    private TimePicker xtptime;

    private AddBookOrderPresenter helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addbook);

        helper = new AddBookOrderPresenter(this);

		//获取服务器路径
		urlServer = EatParams.getInstance().getUrlServer();
		//获取城市数据库名称
		areaDbname=EatParams.getInstance().areaDbName;
		//格式化时间对象
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//获取事务id
		sid = EatParams.getInstance().getSession();
		//获取当前时间
		curDate  = new Date(System.currentTimeMillis());
		//预订下单数据源
		booklist = new ArrayList<BookInfo>();

        initVies();
	}

    private void initVies(){
        editmb = (EditText)findViewById(R.id.usermb);
        editmb.setText(EatParams.getInstance().getMb());
        editprice = (EditText)findViewById(R.id.userprice);
        editinfo = (EditText)findViewById(R.id.userfood);
        editaddr = (EditText)findViewById(R.id.useraddr);
        editaddr.setText(EatParams.getInstance().getAddr());
        bookOrder = (TextView)findViewById(R.id.bookOrder);

        times = (EditText)findViewById(R.id.usertime);
        times.setInputType(InputType.TYPE_NULL);
        times.setFocusable(false);
        //时间控件
        times.setOnClickListener(clickListener);

        String type = getIntent().getStringExtra("type");
        id = getIntent().getStringExtra("id");
        //判断点击的按钮是新增还是修改，设置显示信息
        if(type.equals("add")){
            bookOrder.setText("新增订单");
        }else if(type.equals("update")){
            bookOrder.setText("修改订单");
            initData();
        }
        //判断是新增还是修改，并调用相应方法
        findViewById(R.id.addbooksure).setOnClickListener(clickListener);
        //返回按钮事件
        findViewById(R.id.addbookback).setOnClickListener(clickListener);
    }

    //获得BookOrderActivity传来的参数
    private void initData(){
        Intent intent = getIntent();
        String time = intent.getStringExtra("time");
        String addr = intent.getStringExtra("addr");
        String infoname = intent.getStringExtra("infoname");
        String mb = intent.getStringExtra("mb");
        String price = intent.getStringExtra("price");

        editmb.setText(mb);
        editaddr.setText(addr);
        editinfo.setText(infoname);
        editprice.setText(price);
        times.setText(time);
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.usertime:
                    creatDateDialog(times);
                    break;
                case R.id.addbooksure:
                    if(bookOrder.getText().toString() == "新增订单"){
                        addOrEditOrderInfo(null);
                    }else if(bookOrder.getText().toString() == "修改订单"){
                        addOrEditOrderInfo(id);
                    }
                    break;
                case R.id.addbookback:
                    finish();
                    break;
            }
        }
    };
	
	//新增
	private void addOrEditOrderInfo(String id) {
		String usermb = editmb.getText().toString();
		String userprice = editprice.getText().toString();
		String userfoodinfo = editinfo.getText().toString();
		String useraddr = editaddr.getText().toString();
		String usertime = times.getText().toString();
		Date nowtime = null;
		try {
			nowtime = formatter.parse(usertime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(TextUtils.isEmpty(usermb)){
			Toast.makeText(AddBookOrderActionActivity.this, "没有电话就联系不到你哟！", Toast.LENGTH_SHORT).show();
			return;	
		}else if(TextUtils.isEmpty(usertime)){
			Toast.makeText(AddBookOrderActionActivity.this, "没有时间就会送得比较晚哦！", Toast.LENGTH_SHORT).show();
			return;
		}else if(curDate.after(nowtime)){
			Toast.makeText(AddBookOrderActionActivity.this, "送餐时间早于当前时间，您是要穿越吗", Toast.LENGTH_SHORT).show();
			return;
		}else if(TextUtils.isEmpty(useraddr)){
			Toast.makeText(AddBookOrderActionActivity.this, "没有地址餐会送不到的哟", Toast.LENGTH_SHORT).show();
			return;
		}else if(TextUtils.isEmpty(userfoodinfo)){
			Toast.makeText(AddBookOrderActionActivity.this, "说说您想吃啥呗！", Toast.LENGTH_SHORT).show();
			return;
		}

        if(id == null) {
            url_class = urlServer + "?argv={webdm,-DB," + areaDbname + ",-sid," + sid + ",-ISSUE-RQUIRE,'"
                    + URLEncoder.encode(usermb) + "','"
                    + URLEncoder.encode(userprice) + "','"
                    + URLEncoder.encode(userfoodinfo) + "','"
                    + URLEncoder.encode(useraddr) + "','"
                    + URLEncoder.encode(usertime) + "'}";
        }else{
            url_class=urlServer+ "?argv={webdm,-DB,"+areaDbname+",-sid,"+sid+",-EDIT-RQUIRE,'"
                    +URLEncoder.encode(id)+"','"
                    +URLEncoder.encode(usermb)+"','"
                    +URLEncoder.encode(userprice)+"','"
                    +URLEncoder.encode(userfoodinfo)+"','"
                    +URLEncoder.encode(useraddr)+"','"
                    +URLEncoder.encode(usertime)+"'}";
        }
        if(helper != null)
            helper.requestAddBook();
	}

    /*创建日期弹出框*/
	public void creatDateDialog(View v) {
		View layout = getLayoutInflater().inflate(R.layout.time_dialog, null);
		// 获得日期值跟时间值
		xdpdate = (DatePicker) layout.findViewById(R.id.orderdate);
		xtptime = (TimePicker) layout.findViewById(R.id.ordertime);
		Date te = new Date(System.currentTimeMillis());
		
		xtptime.setCurrentHour(te.getHours());
		xtptime.setCurrentMinute(te.getMinutes());
		xtptime.setIs24HourView(true);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				AddBookOrderActionActivity.this);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				date = String.valueOf(xdpdate.getYear()) + "-"
						+ String.valueOf(xdpdate.getMonth() + 1) + "-"
						+ String.valueOf(xdpdate.getDayOfMonth());

				time = String.valueOf(xtptime.getCurrentHour()) + ":"
						+ String.valueOf(xtptime.getCurrentMinute()) + ":00";
				date_time = date + " " + time;
				times.setText(date_time);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setView(layout);
		dialog = builder.create();
		dialog.show();
	}

}
