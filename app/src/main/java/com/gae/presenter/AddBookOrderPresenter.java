package com.gae.presenter;

import android.content.Context;

import com.gae.UInterface.IAddBookOrder;

/**
 * 添加预定订单ui管理器
 * Created by sien on 2015/8/20.
 */
public class AddBookOrderPresenter extends BasePresenter{

    private IAddBookOrder impl;
    private Context mcontext;

    public AddBookOrderPresenter(Context context){
        impl = (IAddBookOrder) context;
        mcontext = context;
    }

    public void requestAddBook(){

    }


}
