package com.dali.store.ui;

import java.util.ArrayList;
import java.util.List;

import com.dali.store.R;
import com.loopj.android.image.SmartImageView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ImageGroup extends FrameLayout{
	private SmartImageView sivBigImage;
	private ImageView ivNextLeft;
	private ImageView ivNextRight;
	private ImageView ivUpImage;
	private List<String> imageUrls = new ArrayList<String>();
	private int index = 0;
	
	private float x = 0;
	private float juli;//觸摸移動距離
	private boolean biaozhi = true;
	public ImageGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.image_group, this);
		sivBigImage = (SmartImageView) findViewById(R.id.siv_bigImage);
		ivNextLeft = (ImageView) findViewById(R.id.iv_nextLeft);
		ivNextRight = (ImageView) findViewById(R.id.iv_nextRight);
		ivUpImage = (ImageView) findViewById(R.id.iv_upImage);
		ivNextLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				nextImageLeft();
			}
		});
		ivNextRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				nextImageRight();
			}
		});
		ivUpImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				upImage();
			}
		});
		
		//前方高能
		imageUrls.add("http://img.qpwes.com/uploads/allimg/151104/2-151104113KY06.jpg");
		imageUrls.add("http://www.qulishi.com/uploads/collect/201602/m_52589-0epeuxp.jpg");
		imageUrls.add("http://www.qulishi.com/uploads/collect/201602/m_52589-34arx9p.jpg");
		imageUrls.add("http://t-1.tuzhan.com/a322b3a71fd2/c-1/l/2012/12/09/01/7e0f712ffe73420c83d235e011934de1.jpg");
		imageUrls.add("http://t-1.tuzhan.com/d1567d45cd1e/c-1/l/2012/12/09/01/91f7223ae9644402bc921e0baaaa6c2f.jpg");
		imageUrls.add("http://t-1.tuzhan.com/6f06cb40f4b9/c-1/l/2012/12/09/01/85c5d88deda14711b1f9f88e61683d19.jpg");
		imageUrls.add("http://t-1.tuzhan.com/bd3a7bc71511/c-1/l/2012/12/09/01/610d763a1276482e9b7b0e18541c6a20.jpg");
		imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160202/5-160202104206.jpg");
		imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160201/3-160201155618.jpg");
		imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160201/5-160201110534.jpg");
		imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160130/5-160130103217.jpg");
		imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160128/5-16012Q15114.jpg");
		imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160128/5-16012Q12A4.jpg");
		imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160128/5-16012Q05635.jpg");
		imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160128/5-16012Q04535.jpg");
		imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160128/5-16012Q02Z1.jpg");
		imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160127/5-16012G14442.jpg");
		imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160123/3-160123104604.jpg");
		sivBigImage.setImageUrl(imageUrls.get(index));
	}
	
	public void nextImageLeft(){
		if(index != 0){
			String url = imageUrls.get(-- index);
			sivBigImage.setImageUrl(url);
		}
	}

	public void nextImageRight(){
		if(imageUrls.size() - 1 > index){
			String url = imageUrls.get(++ index);
			sivBigImage.setImageUrl(url);
		}
	}
	
	/**
	 * 上傳圖片
	 */
	public void upImage(){
		
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.ACTION_MOVE == event.getAction()){
			if(x == 0){
				x = event.getX();
				biaozhi = true;
			}
			if(biaozhi){
				juli = x - event.getX();//移動距離
				if(juli < -50f){
					nextImageLeft();
					biaozhi = false;
				}else if(juli > 50f){
					nextImageRight();
					biaozhi = false;
				}
			}
		}else if(event.ACTION_MOVE == event.getAction()){
			System.out.println("y:"+event.getY()+"    x"+event.getX());
		}else if(event.ACTION_UP == event.getAction()){
			x = 0;
			biaozhi = false;
		}
		return true;
	}
}
