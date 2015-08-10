package com.gae.eat2013;
/**
 * 达人发布
 * @author Administrator
 *
 */
import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class RecommendActivity extends Activity {
	private Button camerabtn = null;
	private Button findshop = null;
	private Button back = null;
	private String imageFilePath = "";
	private ImageView imv = null;
	final static int CAMERA_RESULT = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommend_activity);

		imv = (ImageView) findViewById(R.id.img); 

		//拍照
		camerabtn = (Button) findViewById(R.id.camerbtn);
		camerabtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
						+ "/mypicture.jpg";
				File imageFile = new File(imageFilePath);
				Uri imageFileUri = Uri.fromFile(imageFile);
				Intent i = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
						imageFileUri);
				startActivityForResult(i, CAMERA_RESULT);
			}
		});
		
		//发现新餐馆
		findshop=(Button)findViewById(R.id.findshop);
		findshop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(RecommendActivity.this,FindNewShopActivity.class);
				startActivity(intent);
			}
		});
		
		//返回
		back=(Button)findViewById(R.id.recommedback);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override  
    protected void onActivityResult(int requestCode, int resultCode,  
            Intent intent) {  
        super.onActivityResult(requestCode, resultCode, intent);  
  
        //如果拍照成功  
        if (resultCode == RESULT_OK) {  
            //取得屏幕的显示大小  
            Display currentDisplay = getWindowManager().getDefaultDisplay();  
            int dw = currentDisplay.getWidth();  
            int dh = currentDisplay.getHeight();  
  
            //对拍出的照片进行缩放  
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();  
            bmpFactoryOptions.inJustDecodeBounds = true;  
            Bitmap bmp = BitmapFactory.decodeFile(imageFilePath,  
                    bmpFactoryOptions);  
  
            int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight  
                    / (float) dh);  
            int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth  
                    / (float) dw);  
  
            if (heightRatio > 1 && widthRatio > 1) {  
  
                if (heightRatio > widthRatio) {  
  
                    bmpFactoryOptions.inSampleSize = heightRatio;  
                } else {  
                    bmpFactoryOptions.inSampleSize = widthRatio;  
                }  
            }  
  
            bmpFactoryOptions.inJustDecodeBounds = false;  
            bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);  
  
            imv.setImageBitmap(bmp);  
  
        }  
    }

}
