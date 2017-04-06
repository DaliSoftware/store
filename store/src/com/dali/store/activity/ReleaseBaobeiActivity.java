package com.dali.store.activity;

import java.util.ArrayList;
import java.util.List;

import com.dali.store.R;
import com.dali.ui.ImageManageView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
/**
 * 发布宝贝
 * @author xiewenhua
 *
 */
public class ReleaseBaobeiActivity extends Activity{
	private ImageManageView imvBaobeiImage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				//WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.release_baobei);
		imvBaobeiImage = (ImageManageView) findViewById(R.id.imv_imageManage);
		List<String> imageUrls = new ArrayList<String>();
		//添加样图
		imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160202/5-160202104206.jpg");
		imageUrls.add("http://www.qulishi.com/uploads/collect/201602/m_52589-34arx9p.jpg");
		imageUrls.add("http://192.168.1.18:8080/quanminJieshang/doc/show/617");
		imageUrls.add("http://192.168.1.18:8080/quanminJieshang/doc/show/618");
		imvBaobeiImage.addImageUrlsToViewFlipper(imageUrls);
		
	}
	
	
}
