package com.gae.presenter;

import android.content.Context;

import com.gae.UInterface.ICommentAction;

/**
 * Created by sien on 2015/8/23.
 */
public class CommentPresenter extends BasePresenter{
    private Context mcontext;
    private ICommentAction impl;

    public CommentPresenter(Context context){
        mcontext = context;

        impl = (ICommentAction) context;
    }
}
