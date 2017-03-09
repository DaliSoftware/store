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
	private static SmartImageView siv;        //��֤��ͼƬ
	private static EditText etImageValue;     //����ͼƬ��֤��ͼƬ�ؼ�
	private static EditText etPhone;          //�����ֻ�����ؼ�
	
	private static final String getPhoneVerifyCodePath = "http://192.168.1.18:8080/quanminJieshang/verifyCode/sendRegisterSecurityCode/";
	private static final String updateImageVerifyPath = "http://192.168.1.18:8080/quanminJieshang/verifyCode/updateImageVerify?width=280&height=110";
	private static final String checkoutImageVerifyPath = "http://192.168.1.18:8080/quanminJieshang/verifyCode/checkoutImageVerify/";
	//String path = "http://www.mnxz8.com/uploads/allimg/c120814/134495063430210-916450.jpg";
	
	
	static Handler handler = new Handler(){
		/**
         *###��Ϣ���л���
         * ���̴߳���ʱ��ϵͳ��ͬʱ������Ϣ���ж���MessageQueue������Ϣ��ѯ������Looper��
         * ��ѯ�������ã����ǲ�ͣ�ļ����Ϣ�������Ƿ�����Ϣ��Message��
         * ��Ϣ����һ������Ϣ����ѯ�������Ϣ���󴫸���Ϣ��������Handler���������������handleMessage����������������Ϣ��
         * handleMessage�������������߳��У����Կ���ˢ��ui
         * �ܽ᣺ֻҪ��Ϣ��������Ϣ��handleMessage�����ͻ����
         *���߳������Ҫˢ��ui��ֻ��Ҫ����Ϣ�����з�һ����Ϣ������handleMessage��������
         * ���߳�ʹ�ô����������sendMessage����������Ϣ
		 */
		public void handleMessage(android.os.Message msg) {
			//���what��ֵΪ1��ʾ����ɹ���Ϊ0���ʾ����ʧ��
			switch (msg.what) {
			case 1:
				//����Ϣ������Я����λͼ����ˢ�µ�UI
				siv.setImageBitmap((Bitmap)msg.obj);
				break;
			case 2: //ͼƬ��֤����֤���
				if(msg.obj.toString().equals("0")){
					//etImageValue.requestFocus();
					Toast.makeText(ma, "�����ͼƬ��֤��", 0).show();
				}
				break;
			case 3:
				if(msg.obj.toString().equals("0")){
					Toast.makeText(ma, "���Ͷ�����֤��ʱϵͳ���� ����������ֻ����Ƿ���ȷ", 1).show();
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
		        	// �˴�Ϊ�õ�����ʱ�Ĵ�������
		        } else {
		        	String code = ((EditText)v).getText().toString();
		        	if(code.trim().length() > 0){
		        		String checkoutImageVerifyCodePath = checkoutImageVerifyPath + code;
			        	System.out.println("�û��������֤���ǣ�"+ checkoutImageVerifyCodePath);
			        	// �˴�Ϊʧȥ����ʱ�Ĵ�������
			        	
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
					//TODO У���ֻ��ŵ���ȷ��
				}
			}
		});*/
	}

	
	
	/**
	 * ����������֤��
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
	 * ������֤ͼƬ
	 * @param v
	 */
	public void updateVerifyImage(View v){
		/** 
		 * �۴����̵߳���Ҫ�ԣ�
		 * ����һЩ��Ҫ�ķѽϳ�ʱ��������Ķ������ڰ�׿4.0��ǰ�İ汾�ǿ��Խ�����ֱ��д�����̣߳��ֳ�UI�̣߳���
		 * ֮��İ汾�ǲ�����ģ�  ���ǰ�׿Ϊ�˱�֤�û����������Ĺ淶����Ϊ�������ʱ�����������̣߳�
		 * ���ܻᵼ��Ӧ������Ӧ������⽫�����û������顣
		 * ��������������ں�ʱ�Ĳ����������ٲ��ѡ����������Դ�ϴ�ʱ��
		 */
		Thread t = new Thread(){
			@Override
			public void run() {
				//����ͼƬ
				//List<View> allImage = getAllChildViews(MainActivity.this.getWindow().getDecorView(), ImageView.class);
				//1.ȷ��Ҫ���ʵ���Դ��·��
				//String path = "http://192.168.1.18:8080/quanminJieshang/verifyCode/updateImageVerify?width=220&height=100";
				try {
					//2.����Դ·������URL����
					URL url = new URL(updateImageVerifyPath);
					//3.�����Ӷ��󣬵���ʱ��δ��������
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					//4.��ʼ�����Ӷ���
					//�������󷽷�
					conn.setRequestMethod("GET");
					//�������ӳ�ʱʱ��
					conn.setConnectTimeout(timeout);
					//���ö�ȡʱ�䳬ʱʱ��
					conn.setReadTimeout(timeout);
					//5.���������������
					conn.connect();
					//��ȡ��������Ӧ�룬���Ϊ200��ʾ���ӳɹ�
					if(conn.getResponseCode() == 200){
						//��ȡ���Ӷ������������������ȡ������������
						InputStream is = conn.getInputStream();
						//��������������λͼ����
						Bitmap bm = BitmapFactory.decodeStream(is);
						
//						ImageView iv = (ImageView) findViewById(R.id.iv);
//						//��λͼ������ʾ��UI
//						iv.setImageBitmap(bm);
						
						Message msg = new Message();
						//������Ϣ����Я��λͼ����
						msg.obj = bm;
						msg.what = 1;
						//������Ϣ
						handler.sendMessage(msg);
						
					}
					else{
//						Toast.makeText(MainActivity.this, "�����������ʧ��", 0).show();
						
						//������Ϣ����
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
	 * ��֤ͼƬ��֤���Ƿ���ȷ
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
					//������Ϣ����
					Message msg = handler.obtainMessage();
					msg.what = 2;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				System.out.println(e.toString());
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = "����"+this.path+"����";
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
					//������Ϣ����
					Message msg = handler.obtainMessage();
					msg.what = 3;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				System.out.println(e.toString());
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = "����"+this.path+"����";
				handler.sendMessage(msg);
				e.printStackTrace();
			}
		}
	}
}
