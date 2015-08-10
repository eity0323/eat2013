package com.gae.basic;
import java.io.ByteArrayInputStream;  
import java.io.ByteArrayOutputStream;  
import java.io.InputStream;  

import android.graphics.Bitmap;  
import android.graphics.BitmapFactory;  
import android.graphics.Canvas;  
import android.graphics.Matrix;
import android.graphics.PixelFormat;  
import android.graphics.drawable.BitmapDrawable;  
import android.graphics.drawable.Drawable;  
  
/** 
 * Bitmap与DrawAble与byte[]与InputStream之间的转换工具类 
 * @author Administrator 
 * 
 */  
public class BitmapFormatTools {
	private static BitmapFormatTools tools = new BitmapFormatTools();  
	  
    public static BitmapFormatTools getInstance() {  
        if (tools == null) {  
            tools = new BitmapFormatTools();  
            return tools;  
        }  
        return tools;  
    }  
  
    // 将byte[]转换成InputStream  
    public InputStream Byte2InputStream(byte[] b) {  
        ByteArrayInputStream bais = new ByteArrayInputStream(b);  
        return bais;  
    }  
  
    // 将InputStream转换成byte[]  
    public byte[] InputStream2Bytes(InputStream is) {  
        String str = "";  
        byte[] readByte = new byte[1024];  
        int readCount = -1;  
        try {  
            while ((readCount = is.read(readByte, 0, 1024)) != -1) {  
                str += new String(readByte).trim();  
            }  
            return str.getBytes();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    // 将Bitmap转换成InputStream  
    public InputStream Bitmap2InputStream(Bitmap bm) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
        InputStream is = new ByteArrayInputStream(baos.toByteArray());  
        return is;  
    }  
  
    // 将Bitmap转换成InputStream  
    public InputStream Bitmap2InputStream(Bitmap bm, int quality) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);  
        InputStream is = new ByteArrayInputStream(baos.toByteArray());  
        return is;  
    }  
  
    // 将InputStream转换成Bitmap  
    public Bitmap InputStream2Bitmap(InputStream is) {  
        return BitmapFactory.decodeStream(is);  
    }  
  
    // Drawable转换成InputStream  
    public InputStream Drawable2InputStream(Drawable d) {  
        Bitmap bitmap = this.drawable2Bitmap(d);  
        return this.Bitmap2InputStream(bitmap);  
    }  
  
    // InputStream转换成Drawable  
    public Drawable InputStream2Drawable(InputStream is) {  
        Bitmap bitmap = this.InputStream2Bitmap(is);  
        return this.bitmap2Drawable(bitmap);  
    }  
  
    // Drawable转换成byte[]  
    public byte[] Drawable2Bytes(Drawable d) {  
        Bitmap bitmap = this.drawable2Bitmap(d);  
        return this.Bitmap2Bytes(bitmap);  
    }  
  
    // byte[]转换成Drawable  
    public Drawable Bytes2Drawable(byte[] b) {  
        Bitmap bitmap = this.Bytes2Bitmap(b);  
        return this.bitmap2Drawable(bitmap);  
    }  
  
    // Bitmap转换成byte[]  
    public byte[] Bitmap2Bytes(Bitmap bm) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);  
        return baos.toByteArray();  
    }  
  
    // byte[]转换成Bitmap  
    public Bitmap Bytes2Bitmap(byte[] b) {  
        if (b.length != 0) {  
            return BitmapFactory.decodeByteArray(b, 0, b.length);  
        }  
        return null;  
    }  
  
    // Drawable转换成Bitmap  
    public Bitmap drawable2Bitmap(Drawable drawable) {  
        Bitmap bitmap = Bitmap  
                .createBitmap(  
                        drawable.getIntrinsicWidth(),  
                        drawable.getIntrinsicHeight(),  
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                                : Bitmap.Config.RGB_565);  
        Canvas canvas = new Canvas(bitmap);  
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),  
                drawable.getIntrinsicHeight());  
        drawable.draw(canvas);  
        return bitmap;  
    }  
  
    // Bitmap转换成Drawable  
    public Drawable bitmap2Drawable(Bitmap bitmap) {  
        BitmapDrawable bd = new BitmapDrawable(bitmap);  
        Drawable d = (Drawable) bd;  
        return d;  
    }  
    
    private void imageZoom(Bitmap bitMap) {
        //图片允许最大空间   单位：KB
        double maxSize =400.00;
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        //将字节换成KB
        double mid = b.length/1024;
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
                //获取bitmap大小 是允许最大大小的多少倍
                double i = mid / maxSize;
                //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
                bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i),
                                bitMap.getHeight() / Math.sqrt(i));
        }
	}
	
	
	
	/***
	 * 图片的缩放方法
	 * 
	 * @param bgimage
	 *            ：源图片资源
	 * @param newWidth
	 *            ：缩放后宽度
	 * @param newHeight
	 *            ：缩放后高度
	 * @return
	 */
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
	                double newHeight) {
	        // 获取这个图片的宽和高
	        float width = bgimage.getWidth();
	        float height = bgimage.getHeight();
	        // 创建操作图片用的matrix对象
	        Matrix matrix = new Matrix();
	        // 计算宽高缩放率
	        float scaleWidth = ((float) newWidth) / width;
	        float scaleHeight = ((float) newHeight) / height;
	        // 缩放图片动作
	        matrix.postScale(scaleWidth, scaleHeight);
	        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
	                        (int) height, matrix, true);
	        return bitmap;
	}
}
