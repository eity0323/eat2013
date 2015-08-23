package com.gae.presenter;

import android.content.Context;

import com.gae.UInterface.ICollectAction;

/**
 * Created by sien on 2015/8/23.
 */
public class CollectPresenter extends BasePresenter{
    private Context mcontext;
    private ICollectAction impl;

    public CollectPresenter(Context context){
        mcontext = context;
        impl = (ICollectAction) context;
    }
}
