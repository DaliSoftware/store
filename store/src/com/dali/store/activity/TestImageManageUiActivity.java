package com.dali.store.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dali.store.R;
import com.dali.store.ui.ImageManageView;

/**
 * 测试使用自定义的管理图片控件activity
 * @author xiewenhua
 *
 */
public class TestImageManageUiActivity extends Activity{
	private ImageManageView imv;
	private TestImageManageUiActivity context;
	protected String mImagePath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_image_manage_ui);
		context = this;
		
		imv = (ImageManageView) findViewById(R.id.imv_imageManage);
		List<String> imageUrls = new ArrayList<String>();
		//添加样图
		imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160202/5-160202104206.jpg");
		imageUrls.add("http://www.qulishi.com/uploads/collect/201602/m_52589-34arx9p.jpg");
		imageUrls.add("http://192.168.1.18:8080/quanminJieshang/doc/show/617");
		imageUrls.add("http://192.168.1.18:8080/quanminJieshang/doc/show/618");
/*		imageUrls.add("http://www.qulishi.com/uploads/collect/201602/m_52589-0epeuxp.jpg");
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
		imageUrls.add("http://4493bz.1985t.com/uploads/allimg/160123/3-160123104604.jpg");*/
		imv.addImageUrlsToViewFlipper(imageUrls);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//将图片选择结果交给图片管理器控件处理
		imv.onActivityResult(requestCode, resultCode, data);
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    
}
