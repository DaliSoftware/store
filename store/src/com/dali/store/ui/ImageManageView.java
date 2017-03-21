package com.dali.store.ui;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.dali.store.R;
import com.loopj.android.image.SmartImageView;

public class ImageManageView extends FrameLayout{
	private Context context;
	private ViewFlipper vfImages;
	private ImageView ivUpImage;
	private TextView tvQiehuanStatus;
	private LinearLayout llQiehuanStatus;
	
	
	private OnClickListener upImageClickListener;
	
	
	//private GestureDetector gestureDetector;
	private boolean autoStart = true;//通过布局文件设置图片是否自动切换
	private float x = 0;//触摸屏幕的时的位置x坐标值
	private int imageSize = 0;
//	private int index = 1;
	public ImageManageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.image_manage, this);
		
		vfImages = (ViewFlipper) findViewById(R.id.vf_ImageList);
		
		
		vfImages.setAutoStart(autoStart);
		vfImages.setFlipInterval(2000);
        Animation lInAnim = AnimationUtils.loadAnimation(context, R.anim.push_left_in);       // 向左滑动左侧进入的渐变效果（alpha 0.1  -> 1.0）  
        Animation lOutAnim = AnimationUtils.loadAnimation(context, R.anim.push_left_out);     // 向左滑动右侧滑出的渐变效果（alpha 1.0  -> 0.1）  
        vfImages.setInAnimation(lInAnim);  
        vfImages.setOutAnimation(lOutAnim);
	    if(vfImages.isAutoStart() && !vfImages.isFlipping()){  
	    	vfImages.startFlipping();  
        } 
	    
	    //向ViewGroup中添加或删除子试图时分别触发onChildViewAdded和onChildViewRemoved方法
	    vfImages.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
			@Override
			public void onChildViewRemoved(View arg0, View arg1) {
				// TODO Auto-generated method stub
				//System.out.println("onChildViewRemoved");
			}
			
			@Override
			public void onChildViewAdded(View arg0, View arg1) {
				// TODO Auto-generated method stub
				//System.out.println("新增子视图时调用 onChildViewAdded");
				//imageSize = imageSize + 1;
				
			}
		});
	    
	    
	    //tvQiehuanStatus = (TextView) findViewById(R.id.tv_qiehuan_status);
	    
	    /*
	     *布局改变的时候触发，  每次切换图片的时候其布局都会发生改变，以此机制判断切换图片，并在onLayoutChange方法中完成切换图片时需要处理的动作 
	     *即使是完全相同的图片也会触发这个事件！开始我还以为图片相同的情况下切换图片会不会触发这个事件
	     */
	    vfImages.addOnLayoutChangeListener(new OnLayoutChangeListener() {
			
			@Override
			public void onLayoutChange(View arg0, int arg1, int arg2, int arg3,
					int arg4, int arg5, int arg6, int arg7, int arg8) {
				// TODO 布局变化时调用  
				for(int i = 0; i < imageSize; i ++){
					SmartImageView siv = (SmartImageView) llQiehuanStatus.findViewById(i);
					siv.setImageResource(R.drawable.round_16px);
				}
				SmartImageView siv = (SmartImageView) llQiehuanStatus.findViewById(vfImages.getDisplayedChild());
				siv.setImageResource(R.drawable.round_24px);
			}
		});

	    
	    ivUpImage = (ImageView) findViewById(R.id.iv_upImage);
	    ivUpImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				doUpImage();
			}
		});
	    llQiehuanStatus = (LinearLayout) findViewById(R.id.ll_qiehuan_status);
		//gestureDetector = new GestureDetector(this.context, new ImageManageGestureDetector());
	}

	public void addImageUrlsToViewFlipper(List<String> imageUrlList){
		if(imageUrlList != null && ! imageUrlList.isEmpty()){
			SmartImageView siv = null;
			for(int i = 0; i < imageUrlList.size(); i ++){
				siv = new SmartImageView(context);
				siv.setImageUrl(imageUrlList.get(i));
				vfImages.addView(siv);
				
				siv = new SmartImageView(context);
				siv.setImageResource(R.drawable.round_16px);
				siv.setId(imageSize ++);
				llQiehuanStatus.addView(siv);
			}	
		}
	}

	public void addBitmapToViewFipper(Bitmap bm){
		SmartImageView siv = new SmartImageView(context);
		siv.setImageBitmap(bm);
		vfImages.addView(siv);
		
		siv = new SmartImageView(context);
		siv.setImageResource(R.drawable.round_16px);
		siv.setId(imageSize ++);
		llQiehuanStatus.addView(siv);
	}
	
	public void doUpImage(){
		upImageClickListener.onClick(ivUpImage);
		Toast.makeText(context, "要上传图片咯。。", Toast.LENGTH_LONG).show();
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		vfImages.stopFlipping();
		vfImages.setAutoStart(false);
		
		System.out.println("ImageManageView  onTouchEvent 调用"+ event.getAction());
		if(MotionEvent.ACTION_DOWN == event.getAction()){
			x = event.getX();
		}else if(MotionEvent.ACTION_UP == event.getAction()){
			
			float juli = x - event.getX();//移動距離
			if(juli < -50f){
	            Animation rInAnim = AnimationUtils.loadAnimation(context, R.anim.push_right_in);  // 向右滑动左侧进入的渐变效果（alpha  0.1 -> 1.0）  
	            Animation rOutAnim = AnimationUtils.loadAnimation(context, R.anim.push_right_out); // 向右滑动右侧滑出的渐变效果（alpha 1.0  -> 0.1）  
	  
	            vfImages.setInAnimation(rInAnim);  
	            vfImages.setOutAnimation(rOutAnim);  
	            vfImages.showPrevious();  
			}else if(juli > 50f){
	            Animation lInAnim = AnimationUtils.loadAnimation(context, R.anim.push_left_in);       // 向左滑动左侧进入的渐变效果（alpha 0.1  -> 1.0）  
	            Animation lOutAnim = AnimationUtils.loadAnimation(context, R.anim.push_left_out);     // 向左滑动右侧滑出的渐变效果（alpha 1.0  -> 0.1）  
	  
	            vfImages.setInAnimation(lInAnim);  
	            vfImages.setOutAnimation(lOutAnim);
	            vfImages.showNext();  
			}
			x = 0;
		}
		return true;
	}

	public void setUpImageClickListener(OnClickListener upImageClickListener) {
		this.upImageClickListener = upImageClickListener;
	}
	
/*	class ImageManageGestureDetector extends GestureDetector.SimpleOnGestureListener{
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e2.getX() - e1.getX() > 120) {            // 从左向右滑动（左进右出）  
	            Animation rInAnim = AnimationUtils.loadAnimation(context, R.anim.push_right_in);  // 向右滑动左侧进入的渐变效果（alpha  0.1 -> 1.0）  
	            Animation rOutAnim = AnimationUtils.loadAnimation(context, R.anim.push_right_out); // 向右滑动右侧滑出的渐变效果（alpha 1.0  -> 0.1）  
	  
	            vfImages.setInAnimation(rInAnim);  
	            vfImages.setOutAnimation(rOutAnim);  
	            vfImages.showPrevious();  
	        } else if (e2.getX() - e1.getX() < -120) {        // 从右向左滑动（右进左出）  
	            Animation lInAnim = AnimationUtils.loadAnimation(context, R.anim.push_left_in);       // 向左滑动左侧进入的渐变效果（alpha 0.1  -> 1.0）  
	            Animation lOutAnim = AnimationUtils.loadAnimation(context, R.anim.push_left_out);     // 向左滑动右侧滑出的渐变效果（alpha 1.0  -> 0.1）  
	  
	            vfImages.setInAnimation(lInAnim);  
	            vfImages.setOutAnimation(lOutAnim);
	            vfImages.showNext();  
	        }  
	        return true; 
		}
	}*/
	

	
}
