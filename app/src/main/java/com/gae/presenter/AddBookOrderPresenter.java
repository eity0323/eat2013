package com.gae.presenter;

import android.content.Context;

import com.gae.UInterface.IAddBookOrderAction;

/**
 * 添加预定订单ui管理器
 * Created by sien on 2015/8/20.
 */
public class AddBookOrderPresenter extends BasePresenter{

    private IAddBookOrderAction impl;
    private Context mcontext;

    public AddBookOrderPresenter(Context context){
        impl = (IAddBookOrderAction) context;
        mcontext = context;
    }

    public void requestAddBook(){

    }


}
