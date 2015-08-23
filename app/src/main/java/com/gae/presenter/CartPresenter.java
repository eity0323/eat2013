package com.gae.presenter;

import android.content.Context;

import com.gae.UInterface.ICartAction;

/**
 * Created by sien on 2015/8/23.
 */
public class CartPresenter extends BasePresenter{

    private Context mcontext;
    private ICartAction impl;

    public CartPresenter(Context context){
        mcontext = context;
        impl = (ICartAction) context;
    }
}
