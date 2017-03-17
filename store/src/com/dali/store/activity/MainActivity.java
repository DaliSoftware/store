package com.dali.store.activity;

import java.util.Map;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dali.store.R;
import com.dali.store.securiy.HashUtil;
import com.dali.store.securiy.SHA256Transcrypter;
import com.dali.store.securiy.Transcrypter;
import com.dali.store.ui.LabelEditTextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MainActivity extends Activity {
	private static final String MA_TOAST_TEXT_404 = "访问网络出错咯！";
	
	
	private static MainActivity ma;
	private static int timeout = 50000;
	private static LabelEditTextView etAccountName;     //账户名称控件
	private static LabelEditTextView etPassword;       //密码控件
	
	
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

}
