package com.gae.presenter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.gae.UInterface.IMainAction;
import com.gae.basic.ProgressRing;
import com.gae.control.GpsCityControl;
import com.gae.dbHelper.ApplydbHelper;
import com.gae.eat2013.MainActivity;
import com.gae.entity.EatParams;
import com.gae.entity.applyItem;
import com.gae.listener.GpsCompleteListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页面ui管理类
 * Created by sien on 2015/8/12.
 */
public class MainPresenter extends BasePresenter{

    private IMainAction impl;
    private Context context;

    private List<applyItem> applyList=null;                           		//抽屉应用列表数据
    private List<applyItem> applygList=null;                          		//主界面应用列表数据

    private String dbname = "";												//本地数据库名称

    public GpsCityControl GpsCity = null;

    public MainPresenter(Context context){
        this.context = context;
        this.impl = (IMainAction) context;

        init();
    }

    public List<applyItem> getApplyList() {
        return applyList;
    }

    public List<applyItem> getApplygList() {
        return applygList;
    }

    private void init(){
        //获取本地应用数据库名称
        dbname = EatParams.getInstance().getSDdbname();
    }

    public void setLocation(){
        //定位地址
        ProgressRing.onProgeress(context, "智点", "正在定位...");
        GpsCity = new GpsCityControl(context);
        GpsCity.InitGPS();
        GpsCity.setGpsCompleteListener(new GpsCompleteListener() {

            @Override
            public void onComplete(String str) {
                if(impl != null)impl.updateCityView(str);
                ProgressRing.unProgeress();
            }
        });
    }

    public void InitGPS(){
        GpsCity.InitGPS();
    }

    //获取功能应用
    public void getAllApply(){
        applyList=new ArrayList<applyItem>();
        applygList=new ArrayList<applyItem>();

        ApplydbHelper applydb=new ApplydbHelper(context);
        applydb.open(dbname);                               //打开数据库
        Cursor cursor=applydb.getAllApplys();               //获取数据

        while (cursor.moveToNext()) {
            applyItem item=new applyItem();
            item.setId(cursor.getString(cursor.getColumnIndex(applydb.APPLY_ID)));
            item.setName(cursor.getString(cursor.getColumnIndex(applydb.APPLY_NAME)));
            item.setMemo(cursor.getString(cursor.getColumnIndex(applydb.APPLY_MEMO)));
            item.setGridico(cursor.getString(cursor.getColumnIndex(applydb.APPLY_GRIDICO)));
            item.setSliico(cursor.getString(cursor.getColumnIndex(applydb.APPLY_SLIICO)));
            item.setLink(cursor.getString(cursor.getColumnIndex(applydb.APPLY_LINK)));
            item.setLknum(cursor.getString(cursor.getColumnIndex(applydb.APPLY_LKNUM)));
            item.setShow(cursor.getString(cursor.getColumnIndex(applydb.APPLY_SHOW)));
            item.setGroup(cursor.getString(cursor.getColumnIndex(applydb.APPLY_GROUP)));
            item.setTime(cursor.getString(cursor.getColumnIndex(applydb.APPLY_TIME)));
            applyList.add(item);
            if(cursor.getString(cursor.getColumnIndex(applydb.APPLY_SHOW)).equals("Y")){
                applygList.add(item);
            }
        }
        applyItem itemg=new applyItem();
        itemg.setGridico("applyadd");                             //最后一个应用添加的标示
        applygList.add(itemg);
        applydb.close();
    }

    //统计应用访问次数
    public void appVisitNumChange(String appname){
        for(int i = 0,j = applyList.size();i<j;i++){
            if(applyList.get(i).getLink().equals(appname)){
                int lknum = Integer.valueOf(applyList.get(i).getLknum());
                applyList.get(i).setLknum( ""+(lknum + 1) );
                break;
            }
        }
    }

    public void deletePlugin(int pos){
        ApplydbHelper applydb=new ApplydbHelper(context);
        applydb.open(dbname);
        applydb.update(applygList.get(pos).getId().trim(),"N");
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);  //刷新界面
        context.startActivity(intent);
        applydb.close();
    }

    public void updatePlugin(int pos){
        ApplydbHelper applydb=new ApplydbHelper(context);
        applydb.open(dbname);
        applydb.update(applyList.get(pos).getId().trim(),"Y");
        applydb.close();
    }

    public void removeGPSListener(){
        if(GpsCity != null)
            GpsCity.stopListener();//停止监听
    }
}
