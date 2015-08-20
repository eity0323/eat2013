package com.gae.presenter;

import android.content.Context;

import com.gae.UInterface.IBillAction;

/**
 * 账单ui管理器
 * Created by sien on 2015/8/20.
 */
public class BillPresenter extends BasePresenter{
    private IBillAction impl;
    private Context mcontext;

    public BillPresenter(Context context){
        impl = (IBillAction) context;
        mcontext = context;
    }
}
