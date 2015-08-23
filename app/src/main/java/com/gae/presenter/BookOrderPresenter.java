package com.gae.presenter;

import android.content.Context;

import com.gae.UInterface.IBookOrderAction;

/**
 * Created by sien on 2015/8/23.
 */
public class BookOrderPresenter extends BasePresenter{

    private Context mContext;
    private IBookOrderAction impl;

    public BookOrderPresenter(Context context){
        mContext = context;
        impl = (IBookOrderAction) context;
    }
}
