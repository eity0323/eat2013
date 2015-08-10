package com.gae.view;

import com.gae.entity.EatParams;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

public class DashedLine extends View {
	public DashedLine(Context context, AttributeSet attrs) {  
        super(context, attrs);            
        width = EatParams.getInstance().getScreenWidth();
    }  
  
	public int width = 100;
	public int height = 100;
	public boolean type = false;//true竖线，false横线
	
    @Override  
    protected void onDraw(Canvas canvas) {  
        // TODO Auto-generated method stub  
        super.onDraw(canvas);          
        Paint paint = new Paint();  
        paint.setStyle(Paint.Style.STROKE);  
        paint.setColor(0xffbbbbbb);  
        Path path = new Path();  
        if(type){
	        path.moveTo(0, 0);  
	        path.lineTo(0,width);
        }else{
        	path.moveTo(0, 0);  
 	        path.lineTo(width,0);
        }
        PathEffect effects = new DashPathEffect(new float[]{2,2,2,2},1);  
        paint.setPathEffect(effects);  
        canvas.drawPath(path, paint);  
    } 
}
