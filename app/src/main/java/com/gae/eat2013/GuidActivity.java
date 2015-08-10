package com.gae.eat2013;
/**
 * author:小胡
 * version:2013-5-24
 * description:向导
 * */
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GuidActivity extends Activity {
	
	 private ViewPager viewPager;          //切换控件
     private ArrayList<View> pageViews;    //切换界面数据
     private ImageView back;                //返回
     private int currPage =0;               //页面
     
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.other_guide);
		
		 LayoutInflater inflater = getLayoutInflater();   
		 //分页图片
         pageViews = new ArrayList<View>();           
         pageViews.add(inflater.inflate(R.layout.item01, null));   
         pageViews.add(inflater.inflate(R.layout.item02, null));   
         pageViews.add(inflater.inflate(R.layout.item03, null)); 
         pageViews.add(inflater.inflate(R.layout.item04, null)); 

         viewPager = (ViewPager)findViewById(R.id.guidePages);   
         
         //返回
         back=(ImageView)findViewById(R.id.back);
         back.setVisibility(View.GONE);
         back.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SharedPreferences sharedPreferences =getSharedPreferences("guidnum", Context.MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();//获取编辑器
				editor.putString("guidnum", "1");
				editor.commit();//提交修改
				Intent intent = new Intent(GuidActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
         
         //分页控件
         viewPager.setAdapter(new GuidePageAdapter());   
         viewPager.setOnPageChangeListener(new GuidePageChangeListener());   
	}

	 /** 指引页面Adapter */ 
    class GuidePageAdapter extends PagerAdapter {   
           
        @Override   
        public int getCount() {   
            return pageViews.size();   
        }   
   
        @Override   
        public boolean isViewFromObject(View arg0, Object arg1) {   
            return arg0 == arg1;   
        }   
   
        @Override   
        public int getItemPosition(Object object) {   
            return super.getItemPosition(object);   
        }   
   
        @Override   
        public void destroyItem(View arg0, int arg1, Object arg2) {   
            ((ViewPager) arg0).removeView(pageViews.get(arg1));   
        }   
   
        @Override   
        public Object instantiateItem(View arg0, int arg1) {   
        	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
            ((ViewPager) arg0).addView(pageViews.get(arg1),params);   
            return pageViews.get(arg1);   
        }   
   
        @Override   
        public void restoreState(Parcelable arg0, ClassLoader arg1) {   
   
        }   
   
        @Override   
        public Parcelable saveState() {   
            return null;   
        }   
   
        @Override   
        public void startUpdate(View arg0) {   
   
        }   
   
        @Override   
        public void finishUpdate(View arg0) {   
   
        }   
    }   
     
    /** 指引页面改监听器 */ 
    class GuidePageChangeListener implements OnPageChangeListener {   
   
        @Override   
        public void onPageScrollStateChanged(int arg0) {   
        }   
   
        @Override   
        public void onPageScrolled(int arg0, float arg1, int arg2) {   
        }   
   
        @Override   
        public void onPageSelected(int arg0) {   
           currPage = arg0; 
           if(arg0==pageViews.size()-1){
        	   back.setVisibility(View.VISIBLE);
           }else{
        	   back.setVisibility(View.GONE);
           } 
        }   
    }    
    
    private class MyOnGestureListener extends SimpleOnGestureListener{ 
    	private Context mContext; 
    	MyOnGestureListener(Context context) { 
            mContext = context; 
        } 
    	
        @Override 
        public boolean onFling(MotionEvent e1, MotionEvent e2, 
                float velocityX, float velocityY) { 
            if(currPage==pageViews.size()-1 && (e1.getX() - e2.getX()>0)){ 
                finish(); 
                return true; 
            } 
            return false; 
        } 
    }  
}
