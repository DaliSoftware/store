package com.dali.store.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dali.store.R;
import com.dali.store.securiy.HashUtil;
import com.dali.store.securiy.SHA256Transcrypter;
import com.dali.store.securiy.Transcrypter;
import com.dali.store.ui.ButtonItemView;
import com.dali.store.ui.ImageGroup;
import com.dali.store.ui.LabelEditTextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.image.SmartImageView;

public class MainActivity extends Activity {
	private static final String MA_TOAST_TEXT_404 = "访问网络出错咯！";
	
	
	private static MainActivity ma;
	private static int timeout = 50000;
	private static LabelEditTextView etAccountName;     //账户名称控件
	private static LabelEditTextView etPassword;       //密码控件
	private ButtonItemView biv;
	private ImageView ivBigImage;
	private SmartImageView sivBigImage;
	private ImageGroup igImages;
	
	

	
	//private static final String basePath = "http://192.168.1.109:8080/quanminJieshang/";
	private static final String basePath = "http://192.168.1.18:8080/quanminJieshang/";
	private static final String loginPath = basePath +"/login";
	public static final int DEFAULT_HASH_INTERATIONS = 64;
	
	
	
	
	static Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(ma, msg.obj.toString(), Toast.LENGTH_LONG).show();
				break;
			}
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ma = this;
		
		
		// 初始化控件
		etAccountName = (LabelEditTextView) findViewById(R.id.et_account);
		etPassword = (LabelEditTextView) findViewById(R.id.et_password);
		biv = (ButtonItemView) findViewById(R.id.biv_test);
//		ivBigImage = (ImageView) findViewById(R.id.iv_bigImage);
		sivBigImage = (SmartImageView) findViewById(R.id.siv_bigImage);
		igImages = (ImageGroup) findViewById(R.id.ig_images);
		
		
		
		
		
		biv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ma, RegisterActivity.class);
				startActivityForResult(intent, 0);
				
			}
		});
	}
	
	
	/**
	 * 单击注册按钮触发该方法，注册新的商户
	 * @param v
	 */
	public void login(View v){
		String name = etAccountName.getEditTextValue();
		String pass = etPassword.getEditTextValue();
		
		//创建异步httpclient
		AsyncHttpClient ahc = new AsyncHttpClient();
		
		ahc.setTimeout(timeout);
		RequestParams params = new RequestParams();
		Transcrypter crypter = new SHA256Transcrypter();
		params.add("username", name);
		params.add("password", crypter.encrypt(pass));
		ahc.post(loginPath, params, new LoginHandler());
	}
	
	public void register(View v){
		Intent intent = new Intent();
		intent.setClass(this, RegisterActivity.class);
		startActivity(intent);
	}
	
	public void showImages(View v){
		Intent intent = new Intent();
		
		List<String> imageUrls = new ArrayList<String>();
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
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(ShowImagesActivity.IMAGE_TYPE_URL, (ArrayList<String>) imageUrls);
		intent.putExtras(bundle);
		intent.setClass(ma, ShowImagesActivity.class);
		startActivity(intent);
	}
	
	
	
	public void toTestImageUiActivity(View v){
		Intent intent = new Intent();
		intent.setClass(ma, TestImageManageUiActivity.class);
		startActivity(intent);
	}
	
	
	
	
	
	class LoginHandler extends AsyncHttpResponseHandler{
	
		//请求服务器成功时，此方法调用
		@Override
		public void onSuccess(int statusCode, Header[] headers,
				byte[] responseBody) {
			Map<String, Object> map = jsonToMap(new String(responseBody));
			if(-1 == Integer.parseInt(map.get("resultCode").toString())){
				etAccountName.requestFocus();
				Toast.makeText(ma, map.get("resultMsg").toString(), Toast.LENGTH_LONG).show();	
			}else{
				//TODO 登入成功
				Intent intent = new Intent();
				intent.setClass(ma, WelcomActivity.class);
				ma.startActivity(intent);
			}
			
			
		}
	
		//请求失败此方法调用
		@Override
		public void onFailure(int statusCode, Header[] headers,
				byte[] responseBody, Throwable error) {
			Toast.makeText(ma, MA_TOAST_TEXT_404, Toast.LENGTH_LONG).show();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private static Map<String, Object> jsonToMap(String jsonStr){
		return JSON.parseObject(jsonStr, Map.class);
	}

	public String createPassword(String accountName, String password, String salt) {
		String defaultPassword = HashUtil.sha256Hex(password);
		return HashUtil.sha256Hex(defaultPassword, accountName + salt, DEFAULT_HASH_INTERATIONS);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0){
			String name = data.getStringExtra("name");
			Toast.makeText(ma, name, 1).show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
