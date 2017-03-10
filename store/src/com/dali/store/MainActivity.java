package com.dali.store;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.loopj.android.image.SmartImageView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static MainActivity ma;
	private static int timeout = 10000;
	private static ImageView iv;
	private static SmartImageView siv;        //验证码图片
	private static EditText etImageValue;     //输入图片验证码图片控件
	private static EditText etPhone;          //输入手机号码控件
	
	private static final String getPhoneVerifyCodePath = "http://192.168.1.18:8080/quanminJieshang/verifyCode/sendRegisterSecurityCode/";
	private static final String updateImageVerifyPath = "http://192.168.1.18:8080/quanminJieshang/verifyCode/updateImageVerify?width=280&height=110";
	private static final String checkoutImageVerifyPath = "http://192.168.1.18:8080/quanminJieshang/verifyCode/checkoutImageVerify/";
	//String path = "http://www.mnxz8.com/uploads/allimg/c120814/134495063430210-916450.jpg";
	
	
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
			case 3:
				if(msg.obj.toString().equals("0")){
					Toast.makeText(ma, "发送短信验证码时系统出错！ 请检查输入的手机号是否正确", 1).show();
				}
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
		siv.setImageUrl(updateImageVerifyPath);
		
		
		etImageValue = (EditText) findViewById(R.id.et_image_value);
		/*etImageValue.setOnFocusChangeListener(new OnFocusChangeListener() {  
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
		});*/
		
		etPhone = (EditText) findViewById(R.id.et_phone);
		/*etPhone.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					
				}else{
					//TODO 校验手机号的正确性
				}
			}
		});*/
	}

	
	
	/**
	 * 发生短信验证码
	 * @param v
	 */
	public void sendPhoneVerifyCode(View v){
		String phone = etPhone.getText().toString();
		if(phone.trim().length() > 0){
			UpdatePhoneVerifyCode thread = new UpdatePhoneVerifyCode(getPhoneVerifyCodePath + phone);
			thread.start();
		}
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
	
	
	class UpdatePhoneVerifyCode extends Thread{
		private String path;
		public UpdatePhoneVerifyCode(String path){
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
					msg.what = 3;
					handler.sendMessage(msg);
					
				}
				else{
					//创建消息对象
					Message msg = handler.obtainMessage();
					msg.what = 3;
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
}
