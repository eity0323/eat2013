package com.gae.view;
/*
 * author:eity
 * version:2013-3-1
 * description:弹出窗口类
 * */
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

public class Alert {
	private View mview;							//显示控制对象						
	private View showView;						//显示内容对象
	private PopupWindow popupWindow = null;
	private Context mcontext;
	public Alert(View parentView,View showView) {
		this.mview = parentView;
		this.showView = showView;
	    init(); 
	}
	//初始化弹出窗口
	private void init(){
		popupWindow = new PopupWindow(showView, LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT, true);
		popupWindow.setContentView(showView); 
		
		ColorDrawable dw = new ColorDrawable(-00000);
		popupWindow.setBackgroundDrawable(dw);
		popupWindow.setFocusable(true);

		popupWindow.showAtLocation(mview,Gravity.CENTER
                | Gravity.CENTER, 0, 0);
	}
	
	//关闭弹出窗口
	public void closePopUpWindow(){
		if(popupWindow != null)
			popupWindow.dismiss();
	}
}
