package com.dali.store.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.dali.store.R;
import com.dali.store.http.HttpUtil;
import com.dali.store.ui.ImageManageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;


import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class TestImageManageUiActivity extends Activity{
	private ImageManageView imv;
	private TestImageManageUiActivity context;
	private String pic_path;
	protected String mImagePath;
	private ProgressDialog progressDialog;
	private static final String mstrIP = "http://192.168.1.18:8080/quanminJieshang/";
	private static final String strUploadFile = mstrIP + "doc/upload";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.test_image_manage_ui);
		context = this;
		
		
		imv = (ImageManageView) findViewById(R.id.imv_imageManage);
		
		List<String> imageUrls = new ArrayList<String>();
		imageUrls.add("http://img.qpwes.com/uploads/allimg/151104/2-151104113KY06.jpg");
		imageUrls.add("http://www.qulishi.com/uploads/collect/201602/m_52589-0epeuxp.jpg");
		imageUrls.add("http://www.qulishi.com/uploads/collect/201602/m_52589-34arx9p.jpg");
		imageUrls.add("http://t-1.tuzhan.com/a322b3a71fd2/c-1/l/2012/12/09/01/7e0f712ffe73420c83d235e011934de1.jpg");
		imageUrls.add("http://t-1.tuzhan.com/d1567d45cd1e/c-1/l/2012/12/09/01/91f7223ae9644402bc921e0baaaa6c2f.jpg");
		imageUrls.add("http://t-1.tuzhan.com/6f06cb40f4b9/c-1/l/2012/12/09/01/85c5d88deda14711b1f9f88e61683d19.jpg");
		imageUrls.add("http://t-1.tuzhan.com/bd3a7bc71511/c-1/l/2012/12/09/01/610d763a1276482e9b7b0e18541c6a20.jpg");
		imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160202/5-160202104206.jpg");
		imv.addImageUrlsToViewFlipper(imageUrls);
		
		
		imageUrls.clear();
		
		//设置上传图片按钮点击事件
		imv.setUpImageClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				List<String> imageUrls = new ArrayList<String>();
/*				imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160201/3-160201155618.jpg");
				imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160201/5-160201110534.jpg");
				imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160130/5-160130103217.jpg");
				imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160128/5-16012Q15114.jpg");
				imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160128/5-16012Q12A4.jpg");
				imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160128/5-16012Q05635.jpg");
				imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160128/5-16012Q04535.jpg");
				imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160128/5-16012Q02Z1.jpg");
				imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160127/5-16012G14442.jpg");
				imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160123/3-160123104604.jpg");*/
				imv.addImageUrlsToViewFlipper(imageUrls);
				selectImage();
			}
		});

	}
	
	public void selectImage(){
		AlertDialog.Builder builder = new Builder(context);
		builder.setItems(new String[] { "拍照", "从手机相册里选择" }, 
				new OnClickListener(){

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
			            //获取与应用相关联的路径
			            String imageFilePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
			            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA); 
			           
			            //根据当前时间生成图片的名称
			            String timestamp = "/"+formatter.format(new Date())+".jpg"; 
			            File imageFile = new File(imageFilePath,timestamp);// 通过路径创建保存文件
			            mImagePath = imageFile.getAbsolutePath();
			            Uri imageFileUri = Uri.fromFile(imageFile);// 获取文件的Uri
			           
			            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageFileUri);// 告诉相机拍摄完毕输出图片到指定的Uri
			            startActivityForResult(intent, 1);
			        } else {
			            Toast.makeText(context, "内存卡不存在！", Toast.LENGTH_LONG).show();
			        }
				}
				else if (which == 1) // 从手机相册选择
				{
/*					Intent picture = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(picture, 2);*/
	                
					//选择照片的时候也一样，我们用Action为Intent.ACTION_GET_CONTENT，  
	                //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个  
	                Intent intent = new Intent();  
	                intent.setType("image/*");  
	                intent.setAction(Intent.ACTION_GET_CONTENT);  
	                startActivityForResult(intent, 2); 
					
	                //自己设计的图片选择器
/*					Intent intent = new Intent();
					intent.setClass(context, SelectImageActivity.class);
					startActivity(intent);*/
				}
			}
		});
		Dialog dialog = builder.create();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.show();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {  
            if (requestCode == 2 && resultCode == Activity.RESULT_OK)  
                showYourPic(data); 
            
            if(requestCode == 1){
            	showYourPic(data);
            }
        }  
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	// 调用android自带图库，显示选中的图片  
    private void showYourPic(Intent data) {  
    	if (data != null) {  
            //取得返回的Uri,基本上选择照片的时候返回的是以Uri形式，但是在拍照中有得机子呢Uri是空的，所以要特别注意  
            Uri imageUri = data.getData();  
            
            //返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取  
            if (imageUri != null) {  
                Bitmap image;  
                try {  
                    //这个方法是根据Uri获取Bitmap图片的静态方法  
                    //image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);  
                	image = suofanImage(imageUri.getPath());
                    imv.addBitmapToViewFipper(image); 
                    
                    UploadFile upFileTask = new UploadFile(imageUri.getPath());
                	new Thread(upFileTask).start();
                	progressDialog = ProgressDialog.show(context, null, "正在上传..."); 
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            } else {  
                Bundle extras = data.getExtras();  
                if (extras != null) {  
                    //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片  
                    Bitmap image = extras.getParcelable("data");  
                    imv.addBitmapToViewFipper(image); 
                    
                }  
            }  
    	}

    }  
    
    /**
     * 上传一张图片
     * @author xiewenhua
     *
     */
    private class UploadFile implements Runnable{
    	private String filePath;
    	public UploadFile(String filePath){
    		this.filePath = filePath;
    	}
    	
		@Override
		public void run() {
			 
			String json;
			try {
				File file = new File(this.filePath);
				json = HttpUtil.upLoadFile(context, "upfile", file, 
						strUploadFile, null);
				progressDialog.dismiss();
				System.out.println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    	
    }
    
    /**
     * 按图片管理器大小缩放
     * @param imagePath
     * @return
     */
    private Bitmap suofanImage(String imagePath){
    	Options option = new Options();
    	option.inJustDecodeBounds = true;
    	Bitmap bm = BitmapFactory.decodeFile(imagePath, option);
    	int width = option.outWidth;
    	int height = option.outHeight;
    	
    	int imvWidth = imv.getWidth();
    	int imvHeight = imv.getHeight();
    	
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
    
}
