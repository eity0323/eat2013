package com.gae.presenter;

import android.content.Context;

import com.gae.UInterface.IBookDetailAction;

/**
 * 订单详情
 * Created by sien on 2015/8/23.
 */
public class BookDetailPresenter extends BasePresenter{

    private Context mcontext;
    private IBookDetailAction impl;

    public BookDetailPresenter(Context context){
        mcontext = context;
        impl = (IBookDetailAction) context;

    }
}
