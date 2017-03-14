package com.dali.store.activity;

import java.util.Map;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dali.store.R;
import com.dali.store.R.id;
import com.dali.store.R.layout;
import com.dali.store.securiy.HashUtil;
import com.dali.store.securiy.SHA256Transcrypter;
import com.dali.store.securiy.Transcrypter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.image.SmartImageView;

public class MainActivity extends Activity {
	private static MainActivity ma;
	private static int timeout = 500000;
	private static SmartImageView siv;        //验证码图片
	private static EditText etAccountName;     //账户名称控件
	private static EditText etPassword;       //密码控件
	
	
	//private static final String basePath = "http://192.168.1.109:8080/quanminJieshang/";
	private static final String basePath = "http://192.168.1.18:8080/quanminJieshang/";
	private static final String loginPath = basePath +"/login";
	private static final String RESULT_MAP_KEY_CODE = "code";
	private static final String RESULT_MAP_KEY_MESSAGE = "message";
	private static final String RESULT_CODE_VALUE_0 = "0";
	public static final int DEFAULT_HASH_INTERATIONS = 64;
	static Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:  //刷新图片验证码
				siv.setImageBitmap((Bitmap)msg.obj);
				break;
				
				//++++++++++++++++++++++++++++处理验证++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			case 2: //图片验证码验证结果
				if(msg.obj.toString().equals(RESULT_CODE_VALUE_0)){
					//etImageValue.requestFocus();
					Toast.makeText(ma, "错误的图片验证码", Toast.LENGTH_LONG).show();
				}
				break;
			case 3:  //处理发生短信验证码请求
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				if(map.get(RESULT_MAP_KEY_CODE).toString().equals(RESULT_CODE_VALUE_0)){
					Toast.makeText(ma, map.get(RESULT_MAP_KEY_MESSAGE).toString(), Toast.LENGTH_LONG).show();
				}else{
					
				}
				break;
			case 18:
				String pass = etPassword.getText().toString().trim();
				if(pass.length() != 0 && pass.length() < 6){
					Toast.makeText(ma, "密码太短咯。。", Toast.LENGTH_LONG).show();
					etPassword.requestFocus();
				}
				break;
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
		etAccountName = (EditText) findViewById(R.id.et_account);
		etPassword = (EditText) findViewById(R.id.et_password);
	
		//控件事件绑定
		etAccountName.setOnFocusChangeListener(new OnFocusChangeListener() {  
		    @Override  
		    public void onFocusChange(View v, boolean hasFocus) {  
		        if(hasFocus) {
		        	// 此处为得到焦点时的处理内容
		        } else {
					Message msg = handler.obtainMessage();
					msg.what = 16;
					handler.sendMessage(msg);
		        }
		    }
		});
		
		
	
		etPassword.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
				}else{
					
					//校验密码是否符合规范
					Message msg = handler.obtainMessage();
					msg.what = 18;
					handler.sendMessage(msg);
				}
			}
			
		});
		
		
	}
	
	
	/**
	 * 单击注册按钮触发该方法，注册新的商户
	 * @param v
	 */
	public void login(View v){
		String name = etAccountName.getText().toString().trim();
		String pass = etPassword.getText().toString().trim();
		
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
			Toast.makeText(ma, new String(responseBody), Toast.LENGTH_LONG).show();
		}
	
		//请求失败此方法调用
		@Override
		public void onFailure(int statusCode, Header[] headers,
				byte[] responseBody, Throwable error) {
			Toast.makeText(ma, new String(responseBody), Toast.LENGTH_LONG).show();
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
