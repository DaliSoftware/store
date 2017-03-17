package com.dali.store.activity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.http.Header;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dali.store.R;
import com.dali.store.securiy.HashUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.image.SmartImageView;

/**
 * 注册商户
 * @author Administrator
 *
 */
public class RegisterActivity extends Activity {
	private static RegisterActivity ma;
	private static int timeout = 500000;
	private static SmartImageView siv;        //验证码图片
	private static EditText etImageVerifyCode;     //输入图片验证码图片控件
	private static EditText etAccountName;     //账户名称控件
	private static EditText etPhoneNumber;          //输入手机号码控件
	private static EditText etSmsVerifyCode;     //输入手机验证码的控件
	private static EditText etPassword;       //密码控件
	private static EditText etQuerenPassword;//确认密码控件
	private static Button btGetPhoneVerifyCode;//获取短信验证码按钮控件
//	private static Button btRegister;
	
	
	private static UpdatePhoneVerifyCode sendSmsThread;
	private static final String basePath = "http://192.168.1.109:8080/quanminJieshang/";
	//private static final String basePath = "http://192.168.1.18:8080/quanminJieshang/";
	private static final String getPhoneVerifyCodePath = basePath + "verifyCode/sendRegisterSecurityCode/";
	private static final String checkoutPhoneVerifyCodePath = basePath + "verifyCode/checkoutPhoneVerifyCode/";
	private static final String updateImageVerifyPath = basePath + "verifyCode/updateImageVerify?width=280&height=110";
	private static final String checkoutImageVerifyPath = basePath + "verifyCode/checkoutImageVerify/";
	private static final String registerPath = basePath +"user/saveNewAccount";
	//String path = "http://www.mnxz8.com/uploads/allimg/c120814/134495063430210-916450.jpg";
	private static final String RESULT_MAP_KEY_CODE = "code";
	private static final String RESULT_MAP_KEY_MESSAGE = "message";
	private static final String RESULT_CODE_VALUE_0 = "0";
	public static final int DEFAULT_HASH_INTERATIONS = 64;
	static Handler handler = new Handler(){
		/**
         *###消息队列机制
         * 主线程创建时，系统会同时创建消息队列对象（MessageQueue）和消息轮询器对象（Looper）
         * 轮询器的作用，就是不停的检测消息队列中是否有消息（Message）
         * 消息队列一旦有消息，轮询器会把消息对象传给消息处理器（Handler），处理器会调用handleMessage方法来处理这条消息，
         * handleMessage方法运行在主线程中，所以可以刷新ui
         * 总结：只要消息队列有消息，handleMessage方法就会调用
         *子线程如果需要刷新ui，只需要往消息队列中发一条消息，触发handleMessage方法即可
         * 子线程使用处理器对象的sendMessage方法发送消息
		 */
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
			case 4:  //处理短信验证码校验结果
				Map<String, Object> resultMap = jsonToMap(msg.obj.toString());
				if(resultMap.get(RESULT_MAP_KEY_CODE).toString().equals(RESULT_CODE_VALUE_0)){
					Toast.makeText(ma, resultMap.get(RESULT_MAP_KEY_MESSAGE).toString(),Toast.LENGTH_LONG).show();
				}else{
					//短信验证码验证通过  结束读秒进程
					sendSmsThread.setName("stop"+ sendSmsThread.getName());
				}
				break;
			//本地验证失败处理
			case 17://验证手机号码的正确性
				String phoneStr = etPhoneNumber.getText().toString().trim();
				if(phoneStr.length() == 0 ){
					Toast.makeText(ma, "请输入您的手机号！", Toast.LENGTH_LONG).show();
					etPhoneNumber.requestFocus();
					break;
				}
				if(phoneStr.length() < 9){
					Toast.makeText(ma, "您这手机号不对头撒！", Toast.LENGTH_LONG).show();
					etPhoneNumber.requestFocus();
				}
				break;
			case 18:
				String pass = etPassword.getText().toString().trim();
				if(pass.length() != 0 && pass.length() < 6){
					Toast.makeText(ma, "密码太短咯。。", Toast.LENGTH_LONG).show();
					etPassword.requestFocus();
				}
				break;
			case 19:
				String password = etPassword.getText().toString().trim(); 
				if(password.length() > 0){
					String querenPass = etQuerenPassword.getText().toString().trim();
					if(querenPass.length() > 0 && ! password.equals(querenPass)){
						Toast.makeText(ma, "两次输入的密码不一致", Toast.LENGTH_LONG).show();
					}
				}
				break;
			
				
			case 99:  //更新获取短信验证码按钮的秒数
				btGetPhoneVerifyCode.setText(msg.obj.toString());
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
		setContentView(R.layout.register);
		ma = this;
		
		siv = (SmartImageView) findViewById(R.id.iv);
		//siv.setImageUrl(updateImageVerifyPath);
		updateVerifyImage(null);
		
		// 初始化控件
		etImageVerifyCode = (EditText) findViewById(R.id.et_image_value);
		etAccountName = (EditText) findViewById(R.id.et_accountName);
		etPhoneNumber = (EditText) findViewById(R.id.et_phone);
		etSmsVerifyCode = (EditText) findViewById(R.id.et_phoneVerifyCode);
		btGetPhoneVerifyCode = (Button) findViewById(R.id.bt_getPhoneVerifyCode);
		etPassword = (EditText) findViewById(R.id.et_password);
		etQuerenPassword = (EditText) findViewById(R.id.et_queren_password);
//		btRegister = (Button) findViewById(R.id.bt_register);
		
		
		//控件事件绑定
		etImageVerifyCode.setOnFocusChangeListener(new OnFocusChangeListener() {  
		    @Override  
		    public void onFocusChange(View v, boolean hasFocus) {  
		        if(hasFocus) {
		        	// 此处为得到焦点时的处理内容
		        } else {
		        	
		        	//验证图片验证码的正确性
		        	String code = ((EditText)v).getText().toString();
		        	if(code.trim().length() > 0){
		        		String path = checkoutImageVerifyPath + code;
			        	CheckImageVerify t = new CheckImageVerify(path);
			    		t.start();
		        	}
		        	
		        }
		    }
		});
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
		
		etPhoneNumber.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					
				}else{
/*					Message msg = handler.obtainMessage();
					msg.what = 17;
					handler.sendMessage(msg);*/
				}
			}
		});
		
		//短信验证码控件  之焦点事件处理
		etSmsVerifyCode.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					
				}else{
					//验证短信验证码是否正确
					String path = etSmsVerifyCode.getText().toString().trim();
					if(path.length() > 0){
						path = checkoutPhoneVerifyCodePath + path;
						Message msg = handler.obtainMessage();
						msg.what = 4;
						msg.obj = 0;
						
						CheckoutVerifyCode ther = new CheckoutVerifyCode(path, msg);
						ther.start();
					}
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
		
		etQuerenPassword.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					
				}else{
					
					//校验确认密码是否与第一次输入的密码是否一致
					Message msg = handler.obtainMessage();
					msg.what = 19;
					handler.sendMessage(msg);
				}
			}
			
		});
		
	}

	
	
	/**
	 * 发生短信验证码
	 * @param v
	 */
	public void sendPhoneVerifyCode(View v){
		String phone = etPhoneNumber.getText().toString().trim();
		if(phone.length() > 9 && phone.length() < 12){
			sendSmsThread = new UpdatePhoneVerifyCode(getPhoneVerifyCodePath + phone);
			sendSmsThread.start();	
		}else{
			Message msg = handler.obtainMessage();
			msg.what = 17;
			handler.sendMessage(msg);
		}
			
	}
	
	/**
	 * 单击注册按钮触发该方法，注册新的商户
	 * @param v
	 */
	public void register(View v){
		
		
		//注册前最后校验
		String name = etAccountName.getText().toString().trim();
		String phone = etPhoneNumber.getText().toString().trim(); 
		String pass = etPassword.getText().toString().trim();
		String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
		Message msg = handler.obtainMessage();
		msg.what = 21;
		handler.sendMessage(msg);
		
		
		//创建异步httpclient
		AsyncHttpClient ahc = new AsyncHttpClient();
		
		ahc.setTimeout(timeout);
		//ahc.post(registerPath, new MyResponseHandler());
		//发送post请求提交数据
		//把要提交的数据封装至RequestParams对象
		RequestParams params = new RequestParams();

		params.add("userName", name);
		params.add("salt", salt);
		params.add("password", createPassword(name, pass, salt));
		params.add("mobileno", phone);
		ahc.post(registerPath, params, new RegisterHandler());
		/*
		Register register = new Register(registerPath, name, phone, pass);
		register.start();*/
	}
	
	
	
	
	/**
	 * 更换验证图片
	 * @param v
	 */
	public void updateVerifyImage(View v){
		/** 
		 * 论创建线程的重要性：
		 * 对于一些需要耗费较长时间来处理的动作，在安卓4.0以前的版本是可以将动作直接写在主线程（又称UI线程），
		 * 之后的版本是不允许的，  这是安卓为了保证用户体验而定义的规范，因为如果将耗时操作放在主线程，
		 * 可能会导致应用无响应结果，这将降低用户的体验。
		 * 请求服务器就属于耗时的操作（在网速不佳、或请求的资源较大时）
		 */
		Thread t = new Thread(){
			@Override
			public void run() {
				//1.确定要访问的资源的路径
				//String path = "http://192.168.1.18:8080/quanminJieshang/verifyCode/updateImageVerify?width=220&height=100";
				try {
					//2.用资源路径构建URL对象
					URL url = new URL(updateImageVerifyPath);
					//3.打开连接对象，但此时并未建立连接
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					//4.初始化连接对象
					//设置请求方法
					conn.setRequestMethod("GET");
					//设置连接超时时间
					conn.setConnectTimeout(timeout);
					//设置读取时间超时时间
					conn.setReadTimeout(timeout);
					//5.与服务器建立连接
					conn.connect();
					//获取服务器响应码，如果为200表示连接成功
					if(conn.getResponseCode() == 200){
						//获取连接对象的输入流，用来读取服务器的数据
						InputStream is = conn.getInputStream();
						//将输入流构建成位图对象
						Bitmap bm = BitmapFactory.decodeStream(is);
						
//						ImageView iv = (ImageView) findViewById(R.id.iv);
//						//将位图对象显示在UI
//						iv.setImageBitmap(bm);
						
						Message msg = new Message();
						//利用消息对象携带位图对象
						msg.obj = bm;
						msg.what = 1;
						//发送消息
						handler.sendMessage(msg);
						
					}
					else{
//						Toast.makeText(MainActivity.this, "与服务器连接失败", 0).show();
						
						//创建消息对象
						Message msg = handler.obtainMessage();
						msg.what = 0;
						msg.obj = "获取图像验证码失败！";
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					System.out.println(e.toString());
					e.printStackTrace();
				}
			}
		};
		t.start();
		
	}
	
	/**
	 * 验证图片验证码是否正确
	 * @author xiewenhua
	 *
	 */
	class CheckImageVerify extends Thread{
		private String path;
		public CheckImageVerify(String path){
			this.path = path;
		}
		
		@Override
		public void run() {
			try {
				URL url = new URL(this.path);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(timeout);
				conn.setReadTimeout(timeout);
				conn.connect();
				if(conn.getResponseCode() == 200){
					InputStream is = conn.getInputStream();
	                ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                byte[] buffer = new byte[1024];
	                int len = 0;
	                while(-1 != (len = is.read(buffer))){
	                    baos.write(buffer,0,len);
	                    baos.flush();
	                }
					Message msg = new Message();
					msg.obj = baos.toString("utf-8");
					msg.what = 2;
					handler.sendMessage(msg);
					
				}
				else{
					Message msg = handler.obtainMessage();
					msg.obj = 0;
					msg.what = 2;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				System.out.println(e.toString());
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = "链接"+this.path+"出错！";
				handler.sendMessage(msg);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 请求服务器发送短信验证码
	 * @author Administrator
	 *
	 */
	class UpdatePhoneVerifyCode extends Thread{
		private String path;
		public UpdatePhoneVerifyCode(String path){
			this.path = path;
		}
		
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			try {
				msg.what = 3;
				URL url = new URL(this.path);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(timeout);
				conn.setReadTimeout(timeout);
				conn.connect(); 

				if(conn.getResponseCode() == 200){
					InputStream is = conn.getInputStream();
	                ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                byte[] buffer = new byte[1024];
	                int len = 0;
	                while(-1 != (len = is.read(buffer))){
	                    baos.write(buffer,0,len);
	                    baos.flush();
	                }
	                String jsonStr = baos.toString("utf-8"); 
	                Map<String, Object> map = jsonToMap(jsonStr);
	                msg.obj = map;
	                handler.sendMessage(msg);
	                
	                //短信验证码发送成功 开启读秒  以告诉用户赶快输入短信验证码  以免过期
	                if(!map.get(RESULT_MAP_KEY_CODE).toString().equals(RESULT_CODE_VALUE_0)){
	                	//读秒器   告诉用户验证码还剩多长时间过期
						int i = 80;
						while (i > -1) {
							try {
								Thread.sleep(1000);
								Message msg2 = handler.obtainMessage();
								msg2.what = 99;
								msg2.obj = i +" s";
								
								//如果短信验证码已经验证通过或者验证码已经过期
								if(this.getName().startsWith("stop") || i == 0){
									msg2.obj = "获取短信验证码";
									handler.sendMessage(msg2);
									break;
								}
								handler.sendMessage(msg2);
								i --;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
	                }
				}else {
					msg.what = 0;
					msg.obj = "链接"+this.path+"失败！"+ conn.getResponseCode();
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				System.out.println(e.toString());
				msg.what = 0;
				msg.obj = "链接"+this.path+"出错！";
				handler.sendMessage(msg);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 验证短信验证码的真确性
	 * @author Administrator
	 *
	 */
	class CheckoutVerifyCode extends Thread{
		private String path;
		private Message msg;
		public CheckoutVerifyCode(String path, Message msg){
			this.path = path;
			this.msg = msg;
		}
		
		@Override
		public void run() {
			try {
				URL url = new URL(this.path);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(timeout);
				conn.setReadTimeout(timeout);
				conn.connect(); 
				if(conn.getResponseCode() == 200){
					InputStream is = conn.getInputStream();
	                ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                byte[] buffer = new byte[1024];
	                int len = 0;
	                while(-1 != (len = is.read(buffer))){
	                    baos.write(buffer,0,len);
	                    baos.flush();
	                }
					msg.obj = baos.toString("utf-8");
				}
			} catch (Exception e) {
				System.out.println(e.toString());
				msg.what = 0;
				msg.obj = "链接"+this.path+"出错！";
				e.printStackTrace();
			}
			handler.sendMessage(msg);
		}
	}
	

	class RegisterHandler extends AsyncHttpResponseHandler{
	
		//请求服务器成功时，此方法调用
		@Override
		public void onSuccess(int statusCode, Header[] headers,
				byte[] responseBody) {
			Map<String, Object> map = jsonToMap(new String(responseBody));
			Toast.makeText(RegisterActivity.this, map.get(RESULT_MAP_KEY_MESSAGE).toString(), Toast.LENGTH_LONG).show();
			
		}
	
		//请求失败此方法调用
		@Override
		public void onFailure(int statusCode, Header[] headers,
				byte[] responseBody, Throwable error) {
			Toast.makeText(RegisterActivity.this, "请求失败", Toast.LENGTH_LONG).show();
			
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
