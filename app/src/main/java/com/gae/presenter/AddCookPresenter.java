package com.gae.presenter;

import android.content.Context;

import com.gae.UInterface.IAddCookAction;

/**
 * 提交星级菜谱UI管理器
 * Created by sien on 2015/8/20.
 */
public class AddCookPresenter extends BasePresenter{

    private IAddCookAction impl;
    private Context mcontext;

    public AddCookPresenter(Context context){
        impl = (IAddCookAction) context;
        mcontext =  context;
    }

    public void requestCook(){

    }
}
