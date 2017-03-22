package com.dali.store.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.dali.store.activity.MainActivity;
import com.dali.store.util.ConfigUtil;


public class HttpUtil {
	public static final int LoginTimeOut = 15*1000;
	public static final int GetInfoTimeOut = 15*1000;
	private static final String LOGIN_URL = "http://192.168.1.18:8080/quanminJieshang/login";
	
	public static CookieStore cookieStore;
	/**
	 * 上传文件
	 * 
	 * @param file
	 * @param
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String upLoadFile(Context context, String fileNmae,
			File file, String url, HashMap<String, String> hm)
			throws ClientProtocolException, IOException {
		String result = null;   //上传结果
		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, LoginTimeOut);//请求超时
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, GetInfoTimeOut);//响应超时
		
		// 添加Cookie
		if (cookieStore != null) {
			httpclient.setCookieStore(cookieStore);
			Log.d("HTTPUtil", cookieStore.toString());
		} else {
			Log.d("HTTPUtil", "cookie is null");
		}
		HttpPost httppost = new HttpPost(url);
		
		//String imei = ((TelephonyManager) context
				//.getSystemService("phone")).getDeviceId();
		//httppost.addHeader("imei", imei);
		try {
			httppost.addHeader("version", ConfigUtil.getValue(context, "versionNumber"));
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MultipartEntity entity = new MultipartEntity();
		FileBody fb = new FileBody(file);
		entity.addPart(fileNmae, fb);
		if (hm != null) {
			Set<Entry<String, String>> entrySet = hm.entrySet();
			Iterator<Entry<String, String>> it = entrySet.iterator();
			while (it.hasNext()) {
			       Entry<String, String> entry = it.next();
			       String key = entry.getKey();
			       String value = entry.getValue();
				//String key = it.next().getKey();
				//String value = it.next().getValue();
				entity.addPart(key, new StringBody(value));
				System.out.println(key + "=" + value);
			}
		}
		httppost.setEntity(entity);
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity resEntity = response.getEntity();
		System.out.println("上传文件请求码： "+response.getStatusLine().getStatusCode());
		if (resEntity != null) {
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					resEntity.getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				builder.append(s);
			}
			result = builder.toString();
		}
		httpclient.getConnectionManager().shutdown();
		return result;
	}
	
	
	
	/**
	 * 网络请求post方式
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String sendPostRequest(Context context, String url,
			List<NameValuePair> params) throws Exception {
		
		if(params==null){
			params = new ArrayList<NameValuePair>();
		}
		
/*		String imei = ((TelephonyManager) context
				.getSystemService("phone")).getDeviceId();*/
		
		//params.add(new BasicNameValuePair("imei", "111"));
//		params.add(new BasicNameValuePair("version", ConfigUtil.getValue(context, "versionNumber")));
		
		String result = null;
		HttpPost httpPost = new HttpPost(url);
		if (params != null && params.size() > 0) {
			HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(entity);
		}
		//httpPost.addHeader("imei", imei);
		httpPost.addHeader("version", ConfigUtil.getValue(context, "versionNumber"));
		
		httpPost.addHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=\"UTF-8\"");
		DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, LoginTimeOut);//请求超时
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, GetInfoTimeOut);//响应超时
		// 添加Cookie
		if (cookieStore != null) {
			httpClient.setCookieStore(cookieStore);
			Log.d("HTTPUtil", cookieStore.toString());
		} else {
			Log.d("HTTPUtil", "cookie is null");
		}
		HttpResponse httpResponse = httpClient.execute(httpPost);
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				httpResponse.getEntity().getContent()));
		for (String s = reader.readLine(); s != null; s = reader.readLine()) {
			builder.append(s);
		}
		result = builder.toString();
		if (result.contains("SessionTimeout")) {// 重新登陆
			Intent intent = new Intent();
			intent.setClass(context, MainActivity.class);
			context.startActivity(intent);
		}
		if (url.equals(LOGIN_URL)) {
			cookieStore = httpClient.getCookieStore();
		}
		return result;
	}
	
	
}
