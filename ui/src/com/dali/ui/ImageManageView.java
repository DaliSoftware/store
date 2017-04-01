package com.dali.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.alibaba.fastjson.JSONObject;
import com.dali.app.base.common.Resource;
import com.dali.app.base.http.HttpUtil;
import com.loopj.android.image.SmartImageTask.OnCompleteListener;
import com.loopj.android.image.SmartImageView;
import com.zhy.imageloader.SelectedActivity;

/**
 * 自定义图片管理控件
 * @author xiewenhua
 *
 */
public class ImageManageView extends FrameLayout{
	/**
	 * 使用该图片管理控件的activity的实例
	 */
	private Activity context;
	private ViewFlipper vfImages;
	private ViewFlipper vfMenu;
	private LinearLayout llQiehuanStatus;
	private LinearLayout llMenu;
	private ImageView ivUpImage;
	private ImageView ivDelImage;	
	

	private float x = 0;//触摸屏幕的时的位置x坐标值
	private int imageSize = 0;
	private ProgressDialog progressDialog;
	
	//新拍的需要上传的照片的存放路径
	private  String tempImagePath;
	
	//上一次触摸动作  0为按下事件  1为滑动切换图片事件
	private int lastTouchAction = 0;
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	
	
	//上传成功后的图片的资源id集合
	private List<Integer> docIds = new ArrayList<Integer>();
	public ImageManageView(final Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = (Activity) context;
		LayoutInflater.from(context).inflate(R.layout.image_manage, this);
		
		vfImages = (ViewFlipper) findViewById(R.id.vf_ImageList);
		vfMenu = (ViewFlipper) findViewById(R.id.vf_menu);
		
		vfImages.setAutoStart(true);
		vfImages.setFlipInterval(2000);
        Animation lInAnim = AnimationUtils.loadAnimation(context, R.anim.ui_push_left_in);       // 向左滑动左侧进入的渐变效果（alpha 0.1  -> 1.0）  
        Animation lOutAnim = AnimationUtils.loadAnimation(context, R.anim.ui_push_left_out);     // 向左滑动右侧滑出的渐变效果（alpha 1.0  -> 0.1）  
        vfImages.setInAnimation(lInAnim);  
        vfImages.setOutAnimation(lOutAnim);
	    if(vfImages.isAutoStart() && !vfImages.isFlipping()){  
	    	vfImages.startFlipping();  
        } 
	    //向ViewGroup中添加或删除子试图时分别触发onChildViewAdded和onChildViewRemoved方法
/*	    vfImages.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
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
		});*/
	    
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
					siv.setImageResource(R.drawable.ui_round_16px);
				}
				SmartImageView siv = (SmartImageView) llQiehuanStatus.findViewById(vfImages.getDisplayedChild());
				if(siv != null){
					siv.setImageResource(R.drawable.ui_round_24px);
				}
				
			}
		});

	    
	    ivUpImage = (ImageView) findViewById(R.id.iv_upImage);
	    ivUpImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(context, SelectedActivity.class);
				ImageManageView.this.context.startActivityForResult(intent, 88);
				//doUpImage();
			}
		});
	    llQiehuanStatus = (LinearLayout) findViewById(R.id.ll_qiehuan_status);
	    llMenu = (LinearLayout) findViewById(R.id.ll_menu);
	    ivDelImage = (ImageView) findViewById(R.id.iv_delImage);
	    ivDelImage.setOnClickListener(new DelImageClick(this));
		//gestureDetector = new GestureDetector(this.context, new ImageManageGestureDetector());
	    
	    getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				
				/*
				 *容器中少于2张图片关闭自动切换 
				 */
				if(imageSize < 2){
					vfImages.stopFlipping();
				}
			}
		});
	}

	/**
	 * 删除管理器中的一张图片
	 * @author xiewenhua
	 *
	 */
	private class DelImageClick implements OnClickListener{
		private ImageManageView imv;
		public DelImageClick(ImageManageView imv){
			this.imv  = imv;
		}
		@Override
		public void onClick(View v) {
			if(imageSize == 0)
				return;
			imageSize --;
			v = vfImages.getCurrentView();
			if(imageSize == 0){
				((SmartImageView)v).setImageResource(R.drawable.ui_uploadimage);
			}else{
				vfImages.removeView(v);
				v = llQiehuanStatus.getChildAt(llQiehuanStatus.getChildCount() - 1);
				llQiehuanStatus.removeView(v);
			}
		}
		
	}
	
	/**
	 * 图片加载成功后动作
	 * @author xiewenhua
	 *
	 */
	private class ImageLoadComplete extends OnCompleteListener{
		private SmartImageView siv;
		public ImageLoadComplete(SmartImageView siv){
			this.siv = siv;
		}
		@Override
		public void onComplete() {
			if(siv.getDrawable() == null){
				return;
			}
			vfImages.addView(siv);
			
			siv = new SmartImageView(context);
			siv.setImageResource(R.drawable.ui_round_16px);
			siv.setId(imageSize ++);
			llQiehuanStatus.addView(siv);
		}
	}
	
	
	public void addImageUrlsToViewFlipper(List<String> imageUrlList){
		if(imageUrlList != null && ! imageUrlList.isEmpty()){
			SmartImageView siv = null;
			for(int i = 0; i < imageUrlList.size(); i ++){
				siv = new SmartImageView(context);
				siv.setImageUrl(imageUrlList.get(i), new ImageLoadComplete(siv));
			}	
			
			if(imageSize != 0){
				if(vfMenu.getCurrentView().getId() != R.id.ll_qiehuan_status){
					Animation tInAnim = AnimationUtils.loadAnimation(context, R.anim.ui_push_bottom_in);
					Animation tOutAnim = AnimationUtils.loadAnimation(context, R.anim.ui_push_top_out);
					vfMenu.setInAnimation(tInAnim);
					vfMenu.setOutAnimation(tOutAnim);
					vfMenu.showNext();
					lastTouchAction = 1;
				}
			}
		}
	}

	public void addBitmapToViewFipper(Bitmap bm){
		if(bm != null){
			SmartImageView siv = null;
			if(imageSize == 0){
				siv = (SmartImageView) vfImages.getCurrentView();
				siv.setImageBitmap(bm);
				llQiehuanStatus.removeAllViews();
			}else{
				siv = new SmartImageView(context);
				siv.setImageBitmap(bm);
				vfImages.addView(siv);
			}
			
			siv = new SmartImageView(context);
			siv.setImageResource(R.drawable.ui_round_16px);
			siv.setId(imageSize ++);
			llQiehuanStatus.addView(siv);	
			
			if(vfImages.isAutoStart() && !vfImages.isFlipping() && imageSize > 1){  
		    	vfImages.startFlipping();  
	        }
		}
	}
	
	public void doUpImage(){
		AlertDialog.Builder builder = new Builder(context);
		builder.setItems(new String[] { "拍照", "从手机相册里选择" }, 
				new android.content.DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (which == 0) // 拍照上传
				{
					 // 执行拍照前，应该先判断SD卡是否存在
			        String SDState = Environment.getExternalStorageState();
			        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
			            
			        	/**
			             * 通过指定图片存储路径，解决部分机型onActivityResult回调 data返回为null的情况
			             */
			            //创建存放新拍出的照片的文件夹
			            String imageFilePath = context.getExternalCacheDir().getPath() + "/upimage/";
			            File image = new File(imageFilePath);
			            if(! image.exists()){
			            	image.mkdirs();
			            }
			            	
			            //生成新拍照片的文件名称
			            tempImagePath = imageFilePath + dateFormat.format(new Date())+".jpg"; 
			            File imageFile = new File(tempImagePath);// 通过路径创建保存文件
			           
			            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			            
			            //告诉相机拍摄完毕后输出图片到指定的Uri
			            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
			            context.startActivityForResult(intent, 1);
			        } else {
			            Toast.makeText(context, "内存卡不存在！", Toast.LENGTH_LONG).show();
			        }
				}
				else if (which == 1) // 从手机相册选择
				{
					//选择照片的时候也一样，我们用Action为Intent.ACTION_GET_CONTENT，  
	                //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个  
	                Intent intent = new Intent();  
	                intent.setType("image/*");  
	                intent.setAction(Intent.ACTION_GET_CONTENT);  
	                context.startActivityForResult(intent, 2); 
				}
			}
		});
		Dialog dialog = builder.create();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.show();
//		Toast.makeText(context, "要上传图片咯。。", Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 滑动屏幕切换图片
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		vfImages.stopFlipping();
		vfImages.setAutoStart(false);
		
		if(MotionEvent.ACTION_DOWN == event.getAction()){
			x = event.getX();
			

		}else if(MotionEvent.ACTION_UP == event.getAction()){
			float juli = x - event.getX();//计算移動距離
			if(juli < -50f){
	            Animation rInAnim = AnimationUtils.loadAnimation(context, R.anim.ui_push_right_in);  // 向右滑动左侧进入的渐变效果（alpha  0.1 -> 1.0）  
	            Animation rOutAnim = AnimationUtils.loadAnimation(context, R.anim.ui_push_right_out); // 向右滑动右侧滑出的渐变效果（alpha 1.0  -> 0.1）  
	  
	            vfImages.setInAnimation(rInAnim);  
	            vfImages.setOutAnimation(rOutAnim);  
	            vfImages.showPrevious();  
	            vfImages.setAutoStart(false);
	            
	            if(lastTouchAction != 1){
					Animation tInAnim = AnimationUtils.loadAnimation(context, R.anim.ui_push_bottom_in);
					Animation tOutAnim = AnimationUtils.loadAnimation(context, R.anim.ui_push_top_out);
					vfMenu.setInAnimation(tInAnim);
					vfMenu.setOutAnimation(tOutAnim);
					vfMenu.showNext();
					lastTouchAction = 1;
				}
	            
	            
			}else if(juli > 50f){
	            Animation lInAnim = AnimationUtils.loadAnimation(context, R.anim.ui_push_left_in);       // 向左滑动左侧进入的渐变效果（alpha 0.1  -> 1.0）  
	            Animation lOutAnim = AnimationUtils.loadAnimation(context, R.anim.ui_push_left_out);     // 向左滑动右侧滑出的渐变效果（alpha 1.0  -> 0.1）  
	  
	            vfImages.setInAnimation(lInAnim);  
	            vfImages.setOutAnimation(lOutAnim);
	            vfImages.setAutoStart(false);
	            vfImages.showNext();  
				if(lastTouchAction != 1){//上一次动作不是滑动
					Animation tInAnim = AnimationUtils.loadAnimation(context, R.anim.ui_push_bottom_in);
					Animation tOutAnim = AnimationUtils.loadAnimation(context, R.anim.ui_push_top_out);
					vfMenu.setInAnimation(tInAnim);
					vfMenu.setOutAnimation(tOutAnim);
					vfMenu.showNext();
					lastTouchAction = 1;
				}
			}else{
				if(lastTouchAction != 0){
					Animation tInAnim = AnimationUtils.loadAnimation(context, R.anim.ui_push_bottom_in);
					Animation tOutAnim = AnimationUtils.loadAnimation(context, R.anim.ui_push_top_out);
					vfMenu.setInAnimation(tInAnim);
					vfMenu.setOutAnimation(tOutAnim);
					vfMenu.showNext();
					lastTouchAction = 0;
				}
			}
			x = 0;
		}
		return true;
	}
	
	/**
	 * 使用这个自定义图片管理控件的时候，务必在activity的onActivityResult方法中调用此方法，
	 * 不然将不能上传图片
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == 88 && SelectedActivity.SELECTED_RESULT_CODE == resultCode){
			if(data != null){
				ArrayList<CharSequence> list = data.getCharSequenceArrayListExtra(SelectedActivity.SELECTED_IMAGE_LIST_KEY);
				System.out.println(list.size());
			}
			
		}
		if (data != null) {  
            if (requestCode == 2 && resultCode == Activity.RESULT_OK)  
                showYourPic(data); 
            
            if(requestCode == 1){
            	showYourPic(data);
            }
        }else {
        	if(requestCode == 1){
            	showYourPic(data);
            }
        }
	}
	
	
	
	
	// 调用android自带图库，显示选中的图片  
    private void showYourPic(Intent data) {  
    	if (data != null) {  
            //取得返回的Uri,基本上选择照片的时候返回的是以Uri形式，但是在拍照中有得机子呢Uri是空的，所以要特别注意  
            Uri imageUri = data.getData();  
            //返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取  
            if (imageUri != null) {  
            	tempImagePath = imageUri.getPath();  
            } else {  
            	/*
            	 *如果启动相机应用时没有自定义图片输出路径将会来到这里
            	 *但通过 data.getExtras().getParcelable("data")所获得的bitmap是所拍照片的缩略图
            	 *因此基本上不能用之
            	 */
                Bundle extras = data.getExtras();  
                if (extras != null) {  
                    //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片  
                    Bitmap image = extras.getParcelable("data");  
                    if(image != null){
                        //将bitmap保存到缓存
                    	tempImagePath = context.getExternalCacheDir().getPath();
                    	tempImagePath += "/"+ dateFormat.format(new Date()) +".png";
                        try {
    						image.compress(CompressFormat.PNG, 100, new FileOutputStream(new File(tempImagePath)));
    					} catch (FileNotFoundException e) {
    						e.printStackTrace();
    					}
                    }
                }  
            }  
    	}
    	
    	//上传照片
    	if(new File(tempImagePath).exists()){
    		UploadFile upFileTask = new UploadFile(tempImagePath);
         	new Thread(upFileTask).start();
         	progressDialog = ProgressDialog.show(context, null, "正在上传...");    
         	progressDialog.setOnCancelListener(new OnUploadOver(upFileTask));
         	
    	}
    }  
    
    private class OnUploadOver implements OnCancelListener{
    	private UploadFile upFileTask;
    	public OnUploadOver(UploadFile upFileTask){
    		this.upFileTask = upFileTask;
    	}
    	
		@Override
		public void onCancel(DialogInterface dialog) {
			if(upFileTask.uploadStatus){
         		addBitmapToViewFipper(suofanImage(tempImagePath));
         	}
		}
    	
    }
    
    /**
     * 按图片管理器大小缩放
     * @param imagePath
     * @return
     */
    private Bitmap suofanImage(String imagePath){
    	if(! new File(imagePath).exists())
    		return null;
    	Options option = new Options();
    	option.inJustDecodeBounds = true;
    	Bitmap bm = BitmapFactory.decodeFile(imagePath, option);
    	int width = option.outWidth;
    	int height = option.outHeight;
    	
    	int imvWidth = this.getWidth();
    	int imvHeight = this.getHeight();
    	
    	int bilv = 1;
    	int widthBilv = width / imvWidth;
    	int heightBilv = height / imvHeight;
    	
    	if(widthBilv >= 1 && widthBilv >= heightBilv){
    		bilv = widthBilv;
    	}else if(heightBilv >= 1 && heightBilv >= widthBilv){
    		bilv = heightBilv;
    	}
    	
    	option.inSampleSize = bilv;
    	option.inJustDecodeBounds = false;
    	bm = BitmapFactory.decodeFile(imagePath, option);
    	return bm;
    }
    
    /**
     * 上传一张图片
     * @author xiewenhua
     *
     */
    private class UploadFile implements Runnable{
    	/**
    	 * 上传状态  上传成功时为true
    	 */
    	public boolean uploadStatus = false;
    	private String filePath;
    	public UploadFile(String filePath){
    		this.filePath = filePath;
    	}
    	
		@Override
		public void run() {
			String json;
			try {
				File file = new File(this.filePath);
				json = HttpUtil.upLoadFile(context, "upfile", file, Resource.urlUploadFile, null);
				Map<String, Object> resultMap = JSONObject.parseObject(json, Map.class);
				
				//如果图片上传成功
				if("0".equals(resultMap.get("resultCode").toString())){
					docIds.add(Integer.parseInt(resultMap.get("docId").toString()));
					uploadStatus = true;
				}
				progressDialog.cancel();
				System.out.println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }

	public List<Integer> getDocIds() {
		return docIds;
	}

	public void setDocIds(List<Integer> docIds) {
		this.docIds = docIds;
	}

	public void setAutoStart(boolean autoStart){
		vfImages.setAutoStart(autoStart);
	}
}
