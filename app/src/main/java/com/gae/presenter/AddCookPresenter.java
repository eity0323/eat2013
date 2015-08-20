package com.gae.presenter;

import android.content.Context;

import com.gae.UInterface.IAddCook;

/**
 * 提交星级菜谱UI管理器
 * Created by sien on 2015/8/20.
 */
public class AddCookPresenter extends BasePresenter{

    private IAddCook impl;
    private Context mcontext;

    public AddCookPresenter(Context context){
        impl = (IAddCook) context;
        mcontext =  context;
    }

    public void requestCook(){

    }
}
