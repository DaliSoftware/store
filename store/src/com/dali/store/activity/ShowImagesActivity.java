package com.dali.store.activity;

import java.util.ArrayList;
import java.util.List;

import com.dali.store.R;
import com.loopj.android.image.SmartImageView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

/**
 * 展示单张图片
 * 可以缩放
 * @author Administrator
 *
 */
public class ShowImagesActivity extends Activity{
	public static final String IMAGE_TYPE_URL = "url";
	public static final String IMAGE_TYPE_BITMAP = "bitmap";
	
	private ShowImagesActivity siActivity;
	private ViewFlipper vfImages;
	private GestureDetector gestureDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.show_images);
		siActivity = this;
		vfImages = (ViewFlipper) findViewById(R.id.vf_images);
		Intent intent = getIntent();
		if(intent.getStringArrayListExtra(IMAGE_TYPE_URL) != null){
			List<String> imageUrls = intent.getStringArrayListExtra(IMAGE_TYPE_URL);
			for(int i = 0; i < imageUrls.size(); i ++){
				SmartImageView siv = new SmartImageView(siActivity);
				siv.setImageUrl(imageUrls.get(i));
				vfImages.addView(siv);
			}
		}
		
		vfImages.setAutoStart(true);
		vfImages.setFlipInterval(2000);
	    if(vfImages.isAutoStart() && !vfImages.isFlipping()){  
	    	vfImages.startFlipping();  
        } 
	    
		gestureDetector = new GestureDetector(this, new CustomGestureDetector());
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		vfImages.stopFlipping();
		vfImages.setAutoStart(false);
		return gestureDetector.onTouchEvent(event);
	}
	
	class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener{
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e2.getX() - e1.getX() > 120) {            // 从左向右滑动（左进右出）  
	            Animation rInAnim = AnimationUtils.loadAnimation(siActivity, R.anim.push_right_in);  // 向右滑动左侧进入的渐变效果（alpha  0.1 -> 1.0）  
	            Animation rOutAnim = AnimationUtils.loadAnimation(siActivity, R.anim.push_right_out); // 向右滑动右侧滑出的渐变效果（alpha 1.0  -> 0.1）  
	  
	            vfImages.setInAnimation(rInAnim);  
	            vfImages.setOutAnimation(rOutAnim);  
	            vfImages.showPrevious();  
	        } else if (e2.getX() - e1.getX() < -120) {        // 从右向左滑动（右进左出）  
	            Animation lInAnim = AnimationUtils.loadAnimation(siActivity, R.anim.push_left_in);       // 向左滑动左侧进入的渐变效果（alpha 0.1  -> 1.0）  
	            Animation lOutAnim = AnimationUtils.loadAnimation(siActivity, R.anim.push_left_out);     // 向左滑动右侧滑出的渐变效果（alpha 1.0  -> 0.1）  
	  
	            vfImages.setInAnimation(lInAnim);  
	            vfImages.setOutAnimation(lOutAnim);
	            vfImages.showNext();  
	        }  
	        return true; 
		}
		
		
	}
	
}

class BitMapListParcel implements Parcelable{
	private List<Bitmap> bitmapList;
	public BitMapListParcel(List<Bitmap> bitmapList){
		this.bitmapList = bitmapList;
	}
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	public List<Bitmap> getBitmapList(){
		return this.bitmapList;
	}
}
