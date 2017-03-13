package com.dali.store;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.image.SmartImageView;

import android.R.string;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static MainActivity ma;
	private static int timeout = 50000;
	private static ImageView iv;
	private static SmartImageView siv;        //验证码图片
	private static EditText etImageValue;     //输入图片验证码图片控件
	private static EditText etAccountName;     //账户名称控件
	private static EditText etPhone;          //输入手机号码控件
	private static EditText etPhoneValue;     //输入手机验证码的控件
	private static EditText etPassword;       //密码控件
	private static EditText etQuerenPassword;//确认密码控件
	private static Button btGetPhoneVerifyCode;//获取短信验证码按钮控件
	private static Button btRegister;
	
	
	private static UpdatePhoneVerifyCode sendSmsThread;
	private static final String basePath = "http://192.168.1.18:8080/quanminJieshang/";
	private static final String getPhoneVerifyCodePath = basePath + "verifyCode/sendRegisterSecurityCode/";
	private static final String checkoutPhoneVerifyCodePath = basePath + "verifyCode/checkoutPhoneVerifyCode/";
	private static final String updateImageVerifyPath = basePath + "verifyCode/updateImageVerify?width=280&height=110";
	private static final String checkoutImageVerifyPath = basePath + "verifyCode/checkoutImageVerify/";
	private static final String registerPath = basePath +"user/saveNewAccount";
	//String path = "http://www.mnxz8.com/uploads/allimg/c120814/134495063430210-916450.jpg";
	private static final String RESULT_MAP_KEY_CODE = "code";
	private static final String RESULT_MAP_KEY_MESSAGE = "message";
	
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
			//如果what的值为1表示请求成功，为0则表示请求失败
			switch (msg.what) {
			case 1:
				//将消息对象中携带的位图对象刷新到UI
				siv.setImageBitmap((Bitmap)msg.obj);
				break;
			case 2: //图片验证码验证结果
				if(msg.obj.toString().equals("0")){
					//etImageValue.requestFocus();
					Toast.makeText(ma, "错误的图片验证码", 0).show();
				}
				break;
			case 3:  //处理发生短信验证码请求
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				
				if(map.get(RESULT_MAP_KEY_CODE).toString().equals("0")){
					Toast.makeText(ma, map.get(RESULT_MAP_KEY_MESSAGE).toString(), 1).show();
				}else{
					
				}
				break;
			case 4:  //处理短信验证码校验结果
				Map<String, Object> resultMap = jsonToMap(msg.obj.toString());
				if(resultMap.get(RESULT_MAP_KEY_CODE).toString().equals("0")){
					Toast.makeText(ma, resultMap.get(RESULT_MAP_KEY_MESSAGE).toString(),1).show();
				}else{
					//短信验证码验证通过  结束读秒进程
					sendSmsThread.setName("stop"+ sendSmsThread.getName());
				}
				break;
			case 6:
				System.out.println(msg.obj.toString());
				break;
				
			case 99:
				btGetPhoneVerifyCode.setText(msg.obj.toString());
				break;
			case 0:
				Toast.makeText(ma, msg.obj.toString(), 0).show();
				break;
			}
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ma = this;
		
		siv = (SmartImageView) findViewById(R.id.iv);
		//siv.setImageUrl(updateImageVerifyPath);
		updateVerifyImage(null);
		
		// 初始化控件
		etImageValue = (EditText) findViewById(R.id.et_image_value);
		etAccountName = (EditText) findViewById(R.id.et_accountName);
		etPhone = (EditText) findViewById(R.id.et_phone);
		etPhoneValue = (EditText) findViewById(R.id.et_phoneVerifyCode);
		btGetPhoneVerifyCode = (Button) findViewById(R.id.bt_getPhoneVerifyCode);
		etPassword = (EditText) findViewById(R.id.et_password);
		etQuerenPassword = (EditText) findViewById(R.id.et_queren_password);
		btRegister = (Button) findViewById(R.id.bt_register);
		
		
		//控件事件绑定
		etImageValue.setOnFocusChangeListener(new OnFocusChangeListener() {  
		    @Override  
		    public void onFocusChange(View v, boolean hasFocus) {  
		        if(hasFocus) {
		        	// 此处为得到焦点时的处理内容
		        } else {
		        	String code = ((EditText)v).getText().toString();
		        	if(code.trim().length() > 0){
		        		String checkoutImageVerifyCodePath = checkoutImageVerifyPath + code;
			        	System.out.println("用户输入的验证码是："+ checkoutImageVerifyCodePath);
			        	// 此处为失去焦点时的处理内容
			        	
			        	CheckImageVerify t = new CheckImageVerify(checkoutImageVerifyCodePath);
			    		t.start();
		        	}
		        	
		        }
		    }
		});
		
		etPhone.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					
				}else{
					//TODO 校验手机号的正确性
				}
			}
		});
		
		//短信验证码控件  之焦点事件处理
		etPhoneValue.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					
				}else{
					String path = etPhoneValue.getText().toString().trim();
					if(path.length() > 0){
						path = checkoutPhoneVerifyCodePath + path;
						Message msg = handler.obtainMessage();
						msg.what = 4;//验证短信验证码结果
						msg.obj = 0;
						CheckoutVerifyCode ther = new CheckoutVerifyCode(path, msg);
						ther.start();
					}
				}
			}
		});
		
	}

	
	
	/**
	 * 发生短信验证码
	 * @param v
	 */
	public void sendPhoneVerifyCode(View v){
		String phone = etPhone.getText().toString();
		if(phone.trim().length() > 0){
			sendSmsThread = new UpdatePhoneVerifyCode(getPhoneVerifyCodePath + phone);
			sendSmsThread.start();
		}
	}
	
	/**
	 * 单击注册按钮触发该方法，注册新的商户
	 * @param v
	 */
	public void register(View v){
		//创建异步httpclient
		AsyncHttpClient ahc = new AsyncHttpClient();
		
		ahc.setTimeout(timeout);
		//ahc.post(registerPath, new MyResponseHandler());
		//发送post请求提交数据
		//把要提交的数据封装至RequestParams对象
		RequestParams params = new RequestParams();
		String name = etAccountName.getText().toString();
		String phone = etPhone.getText().toString(); 
		String pass = etPassword.getText().toString();
		params.add("userName", name);
		params.add("password", phone);
		params.add("mobileno", pass);
		ahc.post(registerPath, params, new MyResponseHandler());
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
				//锟斤拷锟斤拷图片
				//List<View> allImage = getAllChildViews(MainActivity.this.getWindow().getDecorView(), ImageView.class);
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
	
/*	 private List<View> getAllChildViews(View parent, Class<?> T) {
	
	    List<View> allchildren = new ArrayList<View>();
	    if (parent instanceof ViewGroup) {
	       ViewGroup vp = (ViewGroup) parent;
	       for (int i = 0; i < vp.getChildCount(); i++) {
	           View viewchild = vp.getChildAt(i);
	
	           if (viewchild.getClass().equals(T)) {
	              allchildren.add(viewchild);
	           }
	           allchildren.addAll(getAllChildViews(viewchild, T));
	       }
	   } 
	   return allchildren;
	 }	*/
	
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
					//创建消息对象
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
	                if(!map.get(RESULT_MAP_KEY_CODE).toString().equals("0")){
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
	

	class MyResponseHandler extends AsyncHttpResponseHandler{
	
		//请求服务器成功时，此方法调用
		@Override
		public void onSuccess(int statusCode, Header[] headers,
				byte[] responseBody) {
			Map<String, Object> map = jsonToMap(new String(responseBody));
			Toast.makeText(MainActivity.this, map.get(RESULT_MAP_KEY_MESSAGE).toString(), 1).show();
			
		}
	
		//请求失败此方法调用
		@Override
		public void onFailure(int statusCode, Header[] headers,
				byte[] responseBody, Throwable error) {
			Toast.makeText(MainActivity.this, "请求失败", 0).show();
			
		}
		
	}
	
	class Register extends Thread{
		private String path;
		private String name;
		private String phone;
		private String pass;
		public Register(String path, String name, String phone, String pass){
			this.path = path;
			this.name = name;
			this.phone = phone;
			this.pass = pass;
			
		}
		
		@Override
		public void run() {
	    	//1.创建客户端对象
	    	HttpClient hc = new DefaultHttpClient();
	    	//2.创建post请求对象
	    	HttpPost hp = new HttpPost(path);
	    	
	    	//封装form表单提交的数据
	    	BasicNameValuePair bnvp = new BasicNameValuePair("name", name);
	    	BasicNameValuePair bnvp2 = new BasicNameValuePair("pass", pass);
	    	BasicNameValuePair bnvp3 = new BasicNameValuePair("phone", phone);
	    	List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
	    	//把BasicNameValuePair放入集合中
	    	parameters.add(bnvp);
	    	parameters.add(bnvp2);
	    	parameters.add(bnvp3);
	    	try {
	    		//要提交的数据都已经在集合中了，把集合传给实体对象
		    	UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, "utf-8");
		    	//设置post请求对象的实体，其实就是把要提交的数据封装至post请求的输出流中
		    	hp.setEntity(entity);
		    	//3.使用客户端发送post请求
				HttpResponse hr = hc.execute(hp);
				if(hr.getStatusLine().getStatusCode() == 200){
					InputStream is = hr.getEntity().getContent();
					String text = getTextFromStream(is);
					
					//发送消息，让主线程刷新ui显示text
					Message msg = handler.obtainMessage();
					msg.what = 6;
					msg.obj = text;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static Map<String, Object> jsonToMap(String jsonStr){
		return JSON.parseObject(jsonStr, Map.class);
	}
	
	
	
	private static String getTextFromStream(InputStream is){
		
		byte[] b = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			while((len = is.read(b)) != -1){
				bos.write(b, 0, len);
			}
			String text = new String(bos.toByteArray());
			bos.close();
			return text;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
