package com.gae.eat2013;
/*
 * author:小容
 * version:2013-5-24
 * description:我的评论
 * */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.gae.UInterface.ICollectAction;
import com.gae.adapter.CommentAdapter;
import com.gae.basic.GetHttp;
import com.gae.entity.CommentItem;
import com.gae.entity.EatParams;
import com.gae.presenter.CommentPresenter;


public class CommentActivity extends Activity implements ICollectAction{
	private String areaDbname = "";  				//城市数据库名称
	private String url_class = "";                  //请求数据路径
	private String urlServer = "";					//服务器路径
	private String result = "";						//请求数据返回标识
	private String errorStr = "";					//请求数据返回错误信息
	private String sid="";          				//事物ID
	private ArrayList<CommentItem> commitem = null;	//评论数据源
	private ListView commlist = null;               //评论列表
	private Button commback = null;                 //返回

    private CommentPresenter helper;
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_activity);

        helper = new CommentPresenter(this);
		
		//获取城市数据库名称
		areaDbname=EatParams.getInstance().areaDbName;
		//获取服务器路径
		urlServer = EatParams.getInstance().getUrlServer();
		//获取事务id
		sid=EatParams.getInstance().getSession();
		//评论数据源
		commitem = new ArrayList<CommentItem>();
				
		initView();
	}

    private void initView(){
        //获取评论数据
        getmarklist();
        commlist = (ListView)findViewById(R.id.commentlist);

        //返回
        commback = (Button)findViewById(R.id.commentback);
        commback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

	//获得评论数据
	private void getmarklist(){

	}
}
