package com.dali.store.common;

public class Resource {

	/**
	 * 服务端地址
	 */
	public static final String urlService = "http://192.168.1.18:8080/quanminJieshang/";
	
	/**
	 * 宿舍环境
	 */
//	public static final String urlService = "http://192.168.1.109:8080/quanminJieshang/";
	public static final String urlUploadFile = urlService + "doc/upload";
	
	/**
	 * 登入url
	 */
	public static final String urlLogin = urlService + "login";
	
	/**
	 * 获取手机验证码url
	 */
	public static final String urlGetPhoneVerifyCode = urlService + "verifyCode/sendRegisterSecurityCode/";
	
	/**
	 * 验证短信验证码url
	 */
	public static final String urlCheckoutPhoneVerifyCode = urlService + "verifyCode/checkoutPhoneVerifyCode/";
	
	/**
	 * 获取图片验证码url
	 */
	public static final String urlUpdateImageVerify = urlService + "verifyCode/updateImageVerify?width=280&height=110";
	
	/**
	 * 验证图片验证码正确性url
	 */
	public static final String urlCheckoutImageVerify = urlService + "verifyCode/checkoutImageVerify/";
	
	/**
	 * 保存新注册商户的信息url
	 */
	public static final String urlRegister = urlService +"user/saveNewAccount";
}
