package com.dali.store.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dali.store.R;
import com.dali.store.bean.ImageFloder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

/**
 * 自定义选择图片的activity
 * 暂时没有完善且不能使用
 * @author xiewenhua
 *
 */
public class SelectImageActivity extends Activity{
	private static SelectImageActivity context;
	private ProgressDialog mProgressDialog;
	private Set<String> mDirPaths = new HashSet<String>();
	protected int totalCount;
	
	//具有图片的文件夹列表
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();
	protected int mPicsSize;//最多图片的文件夹下的图片数量
	protected File mImgDir;//最多图片的文件夹
	private int mScreenHeight;
	
	
	private static Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0X110:
				Toast.makeText(context, "图片扫描完毕！", Toast.LENGTH_LONG).show();
				break;

			default:
				break;
			}
			
		};
	};
	
	@Override  
    protected void onCreate(Bundle savedInstanceState)  
    {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
  
        DisplayMetrics outMetrics = new DisplayMetrics();  
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);  
        mScreenHeight = outMetrics.heightPixels;  
  
        //initView();  
        getImages();  
        //initEvent();  
  
    }  
	
	
	/** 
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹 
     */  
    private void getImages()  
    {  
        if (!Environment.getExternalStorageState().equals(  
                Environment.MEDIA_MOUNTED))  
        {  
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();  
            return;  
        }  
        // 显示进度条  
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");  
  
        new Thread(new Runnable()  
        {  
            @Override  
            public void run()  
            {  
  
                String firstImage = null;  
  
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
                ContentResolver mContentResolver = SelectImageActivity.this  
                        .getContentResolver();  
  
                // 只查询jpeg和png的图片  
                Cursor mCursor = mContentResolver.query(mImageUri, null,  
                        MediaStore.Images.Media.MIME_TYPE + "=? or "  
                                + MediaStore.Images.Media.MIME_TYPE + "=?",  
                        new String[] { "image/jpeg", "image/png" },  
                        MediaStore.Images.Media.DATE_MODIFIED);  
  
                Log.e("TAG", mCursor.getCount() + "");  
                while (mCursor.moveToNext())  
                {  
                    // 获取图片的路径  
                    String path = mCursor.getString(mCursor  
                            .getColumnIndex(MediaStore.Images.Media.DATA));  
  
                    Log.e("TAG", path);  
                    // 拿到第一张图片的路径  
                    if (firstImage == null)  
                        firstImage = path;  
                    // 获取该图片的父路径名  
                    File parentFile = new File(path).getParentFile();  
                    if (parentFile == null)  
                        continue;  
                    String dirPath = parentFile.getAbsolutePath();  
                    ImageFloder imageFloder = null;  
                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）  
                    if (mDirPaths.contains(dirPath))  
                    {  
                        continue;  
                    } else  
                    {  
                        mDirPaths.add(dirPath);  
                        // 初始化imageFloder  
                        imageFloder = new ImageFloder();  
                        imageFloder.setDir(dirPath);  
                        imageFloder.setFirstImagePath(path);  
                    }  
  
                    int picSize = parentFile.list(new FilenameFilter()  
                    {  
                        @Override  
                        public boolean accept(File dir, String filename)  
                        {  
                            if (filename.endsWith(".jpg")  
                                    || filename.endsWith(".png")  
                                    || filename.endsWith(".jpeg"))  
                                return true;  
                            return false;  
                        }  
                    }).length;  
                    totalCount += picSize;  
  
                    imageFloder.setCount(picSize);  
                    mImageFloders.add(imageFloder);  
  
                    if (picSize > mPicsSize)  
                    {  
                        mPicsSize = picSize;  
                        mImgDir = parentFile;  
                    }  
                }  
                mCursor.close();  
  
                // 扫描完成，辅助的HashSet也就可以释放内存了  
                mDirPaths = null;  
  
                // 通知Handler扫描图片完成  
                mHandler.sendEmptyMessage(0x110);  
  
            }  
        }).start();  
  
    }  
}
