package com.gae.eat2013;
/*
 * author:小容
 * version:2013-5-24
 * description:预定下单
 * */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gae.UInterface.IBookOrderAction;
import com.gae.entity.BookInfo;
import com.gae.entity.EatParams;
import com.gae.presenter.BookOrderPresenter;

import java.util.ArrayList;

public class BookOrderActivity extends Activity implements IBookOrderAction{
    public static Dialog dialog = null;			//弹出框
	private String urlServer = null;			//服务器路径
	private String areaDbname = null;			//城市数据库名称
	private String url_class = "";				//请求数据路径
	private String errorStr = "";				//请求数据返回错误信息
	private String result = "";					//请求数据返回标识
	private String sid= "";						//事务id
	private ArrayList<BookInfo> booklist = null;//预定下单数据源
	private ListView listView = null;			//数据列表

    private BookOrderPresenter helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_activity);

        helper = new BookOrderPresenter(this);
		
		//获取服务器路径
		urlServer = EatParams.getInstance().getUrlServer();
		//获取城市数据库名称
		areaDbname=EatParams.getInstance().areaDbName;
		//获取事务id
		sid=EatParams.getInstance().getSession();
		//列表数据源
		booklist = new ArrayList<BookInfo>();

        initView();
	}

    private void initView(){
        //获取预订下单数据
        getbookinfo();

        findViewById(R.id.booksure).setOnClickListener(clickListener);
        findViewById(R.id.bookback).setOnClickListener(clickListener);

        //预订下单列表
        listView = (ListView)findViewById(R.id.bookedlist);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                showPopWindow(booklist.get(position));
            }
        });
    }

    private void showPopWindow(final BookInfo data){
        AlertDialog.Builder builder = new AlertDialog.Builder(BookOrderActivity.this);
        if (data.getAwstate().equals("N")) {

            builder.setTitle("操作提示").setItems(new String[] {"编辑"}, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.putExtra("type", "update");
                    intent.putExtra("time", data.getReqtime());
                    intent.putExtra("addr", data.getAddr());
                    intent.putExtra("infoname", data.getReqinfo());
                    intent.putExtra("mb", data.getMb());
                    intent.putExtra("price", data.getPrice());
                    intent.putExtra("id", data.getId());

                    intent.setClass(BookOrderActivity.this, AddBookOrderActionActivity.class);
                    startActivity(intent);
                }
            });
        }else if(data.getAwstate().equals("Y")){
            builder.setTitle("操作提示").setItems(new String[] {"详细"}, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.putExtra("time",data.getReqtime());
                    intent.putExtra("addr", data.getAddr());
                    intent.putExtra("infoname", data.getReqinfo());
                    intent.putExtra("mb", data.getMb());
                    intent.putExtra("price", data.getPrice());
                    intent.putExtra("awnm", data.getAwnm());

                    intent.setClass(BookOrderActivity.this, BookDetailActivity.class);
                    startActivity(intent);
                }
            });
        }
        builder.create().show();
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.booksure:
                    Intent intent = new Intent();
                    intent.putExtra("type", "add");
                    intent.setClass(BookOrderActivity.this, AddBookOrderActionActivity.class);
                    startActivity(intent);
                    break;
                case R.id.bookback:
                    finish();
                    break;
            }
        }
    };

	//个人预定下单列表
	private void getbookinfo() {

	}

}
