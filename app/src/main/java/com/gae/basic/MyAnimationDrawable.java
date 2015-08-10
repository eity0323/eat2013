package com.gae.basic;
/**
 * author:eity
 * version:2013-05-03
 * description:监听帧动画结束事件
 */


import com.gae.listener.AnimationEventListener;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;

public class MyAnimationDrawable extends AnimationDrawable {
	private Handler finishHandler; 				//判断结束的Handler
	private AnimationEventListener listener;	//监听结束事件

	public MyAnimationDrawable(AnimationDrawable ad) {
		// 这里得自己把每一帧加进去
		for (int i = 0; i < ad.getNumberOfFrames(); i++) {
			this.addFrame(ad.getFrame(i), ad.getDuration(i));
		}
	}
	
	public void setAnimationEventListener(AnimationEventListener listen){
		listener = listen;
	}

	@Override
	public void start() {
		super.start();
		/**
		 * 首先用父类的start() 然后启动线程，来调用onAnimationEnd()
		 */
		finishHandler = new Handler();
		finishHandler.postDelayed(new Runnable() {
			public void run() {
				if(listener != null){
					listener.onAnimationEnd();
				}
			}
		}, getTotalDuration());
	}

	/**
	 * 这个方法获得动画的持续时间（之后调用onAnimationEnd()）
	 */
	public int getTotalDuration() {
		int durationTime = 0;
		for (int i = 0; i < this.getNumberOfFrames(); i++) {
			durationTime += this.getDuration(i);
		}
		return durationTime;
	}
}